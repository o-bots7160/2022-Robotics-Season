package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.hal.simulation.SimDeviceDataJNI;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.TalonFXSimCollection;

public class WestCoastDrive {
  //private final WPI_TalonFX _leftFrnt       = new WPI_TalonFX(10);
  //private final WPI_TalonFX _leftBack       = new WPI_TalonFX(11);
  //private final WPI_TalonFX _rghtFrnt       = new WPI_TalonFX(20);
  //private final WPI_TalonFX _rghtBack       = new WPI_TalonFX(21);
  //private final DifferentialDrive _difDrive = new DifferentialDrive(_leftFrnt, _rghtFrnt);
  
  /*(public WestCoastDrive() {
    _leftFrnt.configFactoryDefault();
    _leftBack.configFactoryDefault();
    _rghtFrnt.configFactoryDefault();
    _rghtBack.configFactoryDefault();
   
    _leftBack.follow( _leftFrnt );
    _rghtBack.follow( _rghtFrnt );
    _leftFrnt.setInverted(TalonFXInvertType.CounterClockwise);
    _leftBack.setInverted(TalonFXInvertType.FollowMaster);
    _rghtFrnt.setInverted(TalonFXInvertType.Clockwise);
    _rghtBack.setInverted(TalonFXInvertType.FollowMaster);

  }*/

  // create our motor controller objects, ensure they are the WPI variant for DriveSim compatibility
  private WPI_TalonFX motorLeftMaster = new WPI_TalonFX(Constants.kMotorLeftMaster);
  private WPI_TalonFX motorLeftFollower = new WPI_TalonFX(Constants.kMotorLeftFollower);
  private WPI_TalonFX motorRightMaster = new WPI_TalonFX(Constants.kMotorRightMaster);
  private WPI_TalonFX motorRightFollower = new WPI_TalonFX(Constants.kMotorRightFollower);
  private DifferentialDrive drive = new DifferentialDrive(motorLeftMaster, motorRightMaster);

  private AHRS gyro = new AHRS(Port.kMXP);


  // Simulation model of the drivetrain
  private DifferentialDrivetrainSim driveSim =
      new DifferentialDrivetrainSim(
          DCMotor.getFalcon500(2), // 2 Falcons on each side of the drivetrain.
          Constants.kGearRatio, // Standard AndyMark Gearing reduction.
          2.1, // MOI of 2.1 kg m^2 (from CAD model).
          26.5, // Mass of the robot is 26.5 kg.
          Units.inchesToMeters(Constants.kWheelRadiusInches),
          Constants.kTrackWidthMeters, // Distance between wheels in meters
          null);

  // Object for simulated inputs into Talon.
  private TalonFXSimCollection leftDriveSim = motorLeftMaster.getSimCollection();
  private TalonFXSimCollection rightDriveSim = motorRightMaster.getSimCollection();

  // create field object for visualizations
  private Field2d field = new Field2d();

  // create robot odometry, "where" our robot is
  private DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(gyro.getRotation2d());

  // left and right pid controller for wheel speeds
  private final PIDController leftPIDController = new PIDController(Constants.kP, 0, 0);
  private final PIDController rightPIDController = new PIDController(Constants.kP, 0, 0);

  private final DifferentialDriveKinematics kinematics =
      new DifferentialDriveKinematics(Constants.kTrackWidthMeters);

  private final SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(Constants.kS, Constants.kV);

  SimDouble gyroSim =
      new SimDouble(
          SimDeviceDataJNI.getSimValueHandle(
              SimDeviceDataJNI.getSimDeviceHandle("navX-Sensor[0]"), "Yaw"));

  public WestCoastDrive() {
    // reset to default to ensure that no sensitive settings has been messed with
    motorLeftMaster.configFactoryDefault();
    motorRightMaster.configFactoryDefault();

    motorLeftFollower.follow(motorLeftMaster);
    motorRightFollower.follow(motorRightMaster);

    // configure open loop ramp rate for smoother acceleration
    motorLeftMaster.configOpenloopRamp(Constants.kDriveOpenLoopRampRate);
    motorRightMaster.configOpenloopRamp(Constants.kDriveOpenLoopRampRate);

    motorLeftFollower.setInverted(InvertType.FollowMaster);
    motorRightFollower.setInverted(InvertType.FollowMaster);

    // ensure left side is not inverted
    motorLeftMaster.setInverted(InvertType.None);
    motorLeftMaster.setSensorPhase(false);

    // reset our encoders
    motorLeftMaster.setSelectedSensorPosition(0);
    motorRightMaster.setSelectedSensorPosition(0);

    gyro.calibrate();
    gyro.reset();

    Shuffleboard.getTab("Simulation").add(field);

    // invert right side only on real robot, as simulation expects both side positive
    if (RobotBase.isReal()) {
      motorRightMaster.setInverted(InvertType.InvertMotorOutput);
      motorRightMaster.setSensorPhase(false);

    } else {
      motorRightMaster.setInverted(InvertType.None);
      motorRightMaster.setSensorPhase(false);
    }
  }

  // update our drivetrain location
  public void periodic() {

    var leftMeters = Units.inchesToMeters(motorLeftMaster.getSelectedSensorPosition() / Constants.kMagMultiplier);
    var rightMeters = Units.inchesToMeters(motorRightMaster.getSelectedSensorPosition() / Constants.kMagMultiplier);
    
    SmartDashboard.putNumber("Left Meters", leftMeters);
    SmartDashboard.putNumber("Right Meters", rightMeters);

    double angle = -Units.degreesToRadians(gyro.getAngle());

    if (RobotBase.isSimulation()) {
      angle = Units.degreesToRadians(gyro.getAngle());
    }
    
    odometry.update(
        new Rotation2d(angle),
        leftMeters,
        rightMeters); 

    field.setRobotPose(odometry.getPoseMeters());

    SmartDashboard.putNumber("Pose Forward (X)", odometry.getPoseMeters().getX());
    SmartDashboard.putNumber("Pose Sideways (Y)", odometry.getPoseMeters().getY());

    // debug information
    SmartDashboard.putNumber("Left Master Voltage", motorLeftMaster.getMotorOutputVoltage());
    SmartDashboard.putNumber("Right Master Voltage", motorRightMaster.getMotorOutputVoltage());
    SmartDashboard.putNumber("Left Follower Voltage", motorLeftFollower.getMotorOutputVoltage());
    SmartDashboard.putNumber("Right Follower Voltage", motorRightFollower.getMotorOutputVoltage());

    SmartDashboard.putNumber(
        "Left Master Velocity",
        getLeftVelocityMeters());

    SmartDashboard.putNumber(
        "Right Master Velocity",
        getRightVelocityMeters());
  }

  // periodically called during simulation
  public void simulationPeriodic() {
    driveSim.setInputs(
        motorLeftMaster.getMotorOutputVoltage(), motorRightMaster.getMotorOutputVoltage());

    // update our simulation every 20ms
    driveSim.update(0.02);

    // update all of our sensors
    leftDriveSim.setIntegratedSensorRawPosition(
        CTREUtil.distanceToNativeUnits(driveSim.getLeftPositionMeters()));
    leftDriveSim.setIntegratedSensorRawPosition(
        CTREUtil.velocityToNativeUnits(driveSim.getLeftVelocityMetersPerSecond()));
    rightDriveSim.setIntegratedSensorRawPosition(
        CTREUtil.distanceToNativeUnits(driveSim.getRightPositionMeters()));
    rightDriveSim.setIntegratedSensorVelocity(
        CTREUtil.velocityToNativeUnits(driveSim.getRightVelocityMetersPerSecond()));

    leftDriveSim.setBusVoltage(RobotController.getBatteryVoltage());
    rightDriveSim.setBusVoltage(RobotController.getBatteryVoltage());

    gyroSim.set(-driveSim.getHeading().getDegrees());
  }

  public void resetOdometry(Pose2d pose) {
    motorLeftMaster.setSelectedSensorPosition(0);
    motorRightMaster.setSelectedSensorPosition(0);
    odometry.resetPosition(pose, gyro.getRotation2d());
  }

  public Pose2d getPose() {
    return odometry.getPoseMeters();
  }

  public void plotTrajectory(Trajectory trajectory) {
    ArrayList<Pose2d> poses = new ArrayList<>();

    for (Trajectory.State pose : trajectory.getStates()) {
      poses.add(pose.poseMeters);
    }

    field.getObject("foo").setPoses(poses);
  }

  // TODO investigate if the math here is right
  public double getLeftVelocityMeters() {
    return Units.inchesToMeters(
        motorLeftMaster.getSelectedSensorVelocity() / Constants.kMagMultiplier);
  }

  public double getRightVelocityMeters() {
    return Units.inchesToMeters(
        motorRightMaster.getSelectedSensorVelocity() / Constants.kMagMultiplier);
  }

  public void setSpeeds(DifferentialDriveWheelSpeeds speeds) {
    final double leftFeedforward = feedforward.calculate(speeds.leftMetersPerSecond);
    final double rightFeedforward = feedforward.calculate(speeds.rightMetersPerSecond);

    final double leftOutput =
        leftPIDController.calculate(getLeftVelocityMeters(), speeds.leftMetersPerSecond);
    final double rightOutput =
        rightPIDController.calculate(getRightVelocityMeters(), speeds.rightMetersPerSecond);
    motorLeftMaster.set(ControlMode.Current ,(leftOutput + leftFeedforward));
    motorRightMaster.set(ControlMode.Current ,(rightOutput + rightFeedforward));
  }

  // normal teleop drive function
  public void arcadeDrive(double yInput, double zInput) {
    drive.arcadeDrive(yInput/2, zInput/2);
  }

  // trajectory following drive function
  public void drive(double xSpeed, double rot) {
    var wheelSpeeds = kinematics.toWheelSpeeds(new ChassisSpeeds(xSpeed, 0.0, rot));
    setSpeeds(wheelSpeeds);
  }

}
