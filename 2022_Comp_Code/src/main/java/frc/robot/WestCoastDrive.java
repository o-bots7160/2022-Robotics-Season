package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.WPI_Pigeon2;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WestCoastDrive {
  private final WPI_TalonFX _leftFrnt       = new WPI_TalonFX(10);
  private final WPI_TalonFX _leftBack       = new WPI_TalonFX(11);
  private final WPI_TalonFX _rghtFrnt       = new WPI_TalonFX(20);
  private final WPI_TalonFX _rghtBack       = new WPI_TalonFX(21);
  private final DifferentialDrive _difDrive = new DifferentialDrive(_leftFrnt, _rghtFrnt);
  private final WPI_Pigeon2 _pidgeyGyro     = new WPI_Pigeon2(8);
  private final double _rotP                = 0.0005;
  private final double _rotI                = 0.0;
  private final double _rotD                = 0.0;
  private final PIDController _rotPID       = new PIDController(_rotP, _rotI, _rotD);
  private final double _distP               = 0.0;
  private final double _distI               = 0.0;
  private final double _distD               = 0.0;
  private final PIDController _distPID      = new PIDController(_distP, _distI, _distD);
  private final double GEAR_BOX_RATIO       = 2.7;
  private boolean autonActive               = false;
  private double[] xyz                      = new double[4];
  private double _rotError                  = 0.0;
 
  
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
  }

  public void autonomousInit() {
    zeroEncoders();
    _pidgeyGyro.reset();
  }

  public void arcadeDrive(double y, double z){
    _difDrive.arcadeDrive(y, z);
  }
  
  public boolean turnTo( double angle ) {
    if (!autonActive) {
      _pidgeyGyro.reset();
      _rotPID.reset();
      _rotPID.setSetpoint(angle);
      autonActive = true;
    }

    _rotError = _rotPID.calculate(xyz[2]);
   
    if ( _pidgeyGyro.getAngle() <  angle) {
      _difDrive.arcadeDrive(0, _rotError);
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
    SmartDashboard.putNumber("Angle?", _pidgeyGyro.getAngle());

  }
  public boolean moveTo( double distance ) {
    if (!autonActive) {
      zeroEncoders();
      _pidgeyGyro.reset();
      _rotPID.setSetpoint(0.0);
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

}
