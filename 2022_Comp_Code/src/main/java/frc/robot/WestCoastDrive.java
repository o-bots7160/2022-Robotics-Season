package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.WPI_Pigeon2; // for using Pigeon Gyro
//import com.kauailabs.navx.frc.AHRS;          // for using NavX Gyro

//import edu.wpi.first.math.controller.PIDController;
//import edu.wpi.first.math.controller.ProfiledPIDController;
//import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.SPI;
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
  //private final WPI_Pigeo gyro              = new WPI_Pigeon2(8);      // for using Pigeon Gyro
  //private final AHRS gyro                   = new AHRS(SPI.Port.kMXP); // for using NavX Gyro
  private final double GEAR_BOX_RATIO       = 2.7;
  private boolean autonActive               = false;
 
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
    //gyro.calibrate();
    //gyro.reset();
  }

  public void autonomousInit() {
    zeroEncoders();
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
    double error = 0.0;
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
      //gyro.reset(); 
      autonActive = true;
    } else { // Turn to angle
      //error = angle - gyro.getAngle();
       if (error > 15.0) {
         _difDrive.arcadeDrive( 0, 0.35);
       } else if ( error > 5 ) {
        _difDrive.arcadeDrive( 0, 0.15);
       } else if ( error > -5) {
         _difDrive.stopMotor();
         autonActive = false;
       } else if (error > -15) {
         _difDrive.arcadeDrive( 0, -0.15);
       } else {
         _difDrive.arcadeDrive( 0, -0.35);
       }
    }

    return autonActive;
  }

  public void robotPeriodic() {
    SmartDashboard.putNumber("LeftDrive", _leftFrnt.getSelectedSensorPosition() );
    //PIDController test = new PIDController(0, 0, 0);
    ///SmartDashboard.putNumber("Angle", gyro.ge);

  }
  public boolean moveTo( double distance ) {

    double pulsesPerInch = 2048 / 6 / Math.PI * GEAR_BOX_RATIO;
    double pulses = distance * pulsesPerInch;
    double position = _rghtFrnt.getSelectedSensorPosition();

    if (!autonActive) {
      zeroEncoders();
      //gyro.reset();
      //_rotPID.setSetpoint(0.0);
      autonActive = true;
    } 

    double error = position - pulses;

    // Move to the set location
    if (error > 15.0) {
      _difDrive.arcadeDrive( 0.35, 0);
    } else if ( error > 5 ) {
     _difDrive.arcadeDrive( 0.15, 0);
    } else if ( error > -5) {
      _difDrive.stopMotor();
      autonActive = false;
    } else if (error > -15) {
      _difDrive.arcadeDrive( -0.15, 0);
    } else {
      _difDrive.arcadeDrive( -0.35, 0);
    }

    return autonActive;
  }

  public void setCoastMode(){
    _leftFrnt.setNeutralMode(NeutralMode.Coast);
    _rghtFrnt.setNeutralMode(NeutralMode.Coast);
  }

  public void setBrakeMode(){
    _leftFrnt.setNeutralMode(NeutralMode.Coast);
    _rghtFrnt.setNeutralMode(NeutralMode.Coast);
  }

  public void stopDrive(){
    _difDrive.arcadeDrive(0, 0);
  }

}
