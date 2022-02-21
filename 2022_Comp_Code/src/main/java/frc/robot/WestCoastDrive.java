package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.WPI_Pigeon2;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WestCoastDrive {
  private final WPI_TalonFX _leftFrnt       = new WPI_TalonFX(10);
  private final WPI_TalonFX _leftBack       = new WPI_TalonFX(11);
  private final WPI_TalonFX _rghtFrnt       = new WPI_TalonFX(20);
  private final WPI_TalonFX _rghtBack       = new WPI_TalonFX(21);
  private final DifferentialDrive _difDrive = new DifferentialDrive(_leftFrnt, _rghtFrnt);
  private final WPI_Pigeon2 _pidgeyGyro     = new WPI_Pigeon2(8);
  //private final AHRS gyro                   = new AHRS(SPI.Port.kMXP);
  private final double _rotP                = 0.035;
  private final double _rotI                = 0.013;//0.096;
  private final double _rotD                = 0.0;//0.001;
  private final TrapezoidProfile.Constraints limits = new TrapezoidProfile.Constraints( 100.0, 30.0);
  private final ProfiledPIDController _rotPID       = new ProfiledPIDController(_rotP, _rotI, _rotD, limits);
  // private final double _distP               = 0.0;
  // private final double _distI               = 0.0;
  // private final double _distD               = 0.0;
  // private final ProfiledPIDController _distPID      = new ProfiledPIDController(_distP, _distI, _distD, limits);
  private final double GEAR_BOX_RATIO       = 2.7;
  private boolean autonActive               = false;
  private double[] xyz                      = new double[4];
  private double _rotError                  = 0.0;
  private double _rotSpeed                  = 0.0;
  private double _rotAngle                  = 0.0;
  private TrapezoidProfile.State _rotGoal   = new TrapezoidProfile.State(0.0, 0.0);
 
  public void zeroEncoders(){
    _leftFrnt.setSelectedSensorPosition( 0.0 );
    _leftFrnt.setSelectedSensorPosition( 0.0 );
  }
  public WestCoastDrive() {
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
    zeroEncoders();
    _pidgeyGyro.calibrate();
    _pidgeyGyro.reset();
    //gyro.reset();
  }

  public void autonomousInit() {
    zeroEncoders();
    _pidgeyGyro.reset();
    //gyro.reset();
  }

  public void arcadeDrive(double y, double z){
    _difDrive.arcadeDrive(y, z);
  }
  
  //
  //  Turn robot to angle from -180 to 180
  //
  //
  public boolean turnTo( double angle ) {
    double tempAngle;
    if ( angle > 180 )
    {
      angle = 180.0;  
    }
    else if ( angle < -180.0 )
    {
      angle = -180.0;
    }
    if (!autonActive) {
      setBrakeMode();
      _pidgeyGyro.reset();
      _rotPID.reset( 0.0 );
      _rotGoal = new TrapezoidProfile.State( angle, 0.0 );
      _rotPID.setGoal( _rotGoal );
      _rotPID.setTolerance( 0.5, 0.5 );
      autonActive = true;
      _rotAngle = 0.0;
      _rotSpeed = 0.0;
    }
    else
    {
      tempAngle = _pidgeyGyro.getAngle();
      _rotSpeed = _rotAngle - tempAngle;
      _rotAngle = tempAngle;
    }

    _rotError = _rotPID.calculate( _rotAngle, _rotGoal );
    if ( ! _rotPID.atGoal( ) ) {
      if ( _rotError > 1 )
      {
        _rotError = 1;
      }
      else if ( _rotError < -1 )
      {
        _rotError = -1;
      }
      _difDrive.arcadeDrive( 0, _rotError );
      return false;
    } else {
      _difDrive.stopMotor();
      autonActive = false;
      return true;
    }
  }

  private void driveStraight(double forwardSpeed) {
    _difDrive.arcadeDrive(forwardSpeed, _rotPID.calculate(_pidgeyGyro.getAngle()));
  }

  public void robotPeriodic() {
    SmartDashboard.putNumber("LeftDrive", _leftFrnt.getSelectedSensorPosition() );
    SmartDashboard.putNumber("Angle", _pidgeyGyro.getAngle());

  }
  public boolean moveTo( double distance ) {
    if (!autonActive) {
      zeroEncoders();
      _pidgeyGyro.reset();
      //_rotPID.setSetpoint(0.0);
      autonActive = true;
    }

    double pulsesPerInch = 2048 / 6 / Math.PI * GEAR_BOX_RATIO;
    double pulses = distance * pulsesPerInch;  

    if( _leftFrnt.getSelectedSensorPosition() > -pulses ) {
      driveStraight(-.4);
      return false;
    } else {
      _difDrive.stopMotor();
      autonActive = false;
      return true;
    }

  }

  public double[] getXYZ(){
    return xyz;
  }
  public double getrotError(){
    return _rotError;
  }

  public void setCoastMode(){
    _leftFrnt.setNeutralMode(NeutralMode.Coast);
    _rghtFrnt.setNeutralMode(NeutralMode.Coast);
  }
  public void setBrakeMode(){
    _leftFrnt.setNeutralMode(NeutralMode.Coast);
    _rghtFrnt.setNeutralMode(NeutralMode.Coast);
  }

}
