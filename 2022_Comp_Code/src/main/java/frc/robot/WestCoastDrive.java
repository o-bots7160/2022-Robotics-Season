package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WestCoastDrive {
  private final WPI_TalonFX _leftFrnt       = new WPI_TalonFX(10);
  private final WPI_TalonFX _leftBack       = new WPI_TalonFX(11);
  private final WPI_TalonFX _rghtFrnt       = new WPI_TalonFX(20);
  private final WPI_TalonFX _rghtBack       = new WPI_TalonFX(21);
  private final DifferentialDrive _difDrive = new DifferentialDrive(_leftFrnt, _rghtFrnt);
  private boolean autonActive = false;
  
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
  }

  public void autonomousInit() {
    zeroEncoders();
  }

  public void arcadeDrive(double y, double z){
    _difDrive.arcadeDrive(y, z);
  }
  
  public boolean turnTo( double rotation ) {
    if (!autonActive) {
      zeroEncoders();
      autonActive = true;
    }
   
    if ( _leftFrnt.getSelectedSensorPosition() < rotation ) {
      _difDrive.arcadeDrive(0, 0.5);
      return false;
    } else {
      _difDrive.stopMotor();
      autonActive = false;
      return true;
    }
  }
  public void robotPeriodic() {
    SmartDashboard.putNumber("LeftDrive", _leftFrnt.getSelectedSensorPosition() );
  }
  public boolean moveTo( double distance ) {
    if (!autonActive) {
      zeroEncoders();
      autonActive = true;
    }

    double pulsesPerInch = 2048 / 6 / Math.PI;
    double pulses = distance * pulsesPerInch;  

    if( _leftFrnt.getSelectedSensorPosition() < -pulses ) {
      _difDrive.arcadeDrive( -0.3, 0);
      return false;
    } else {
      _difDrive.stopMotor();
      autonActive = false;
      return true;
    }

  }
}
