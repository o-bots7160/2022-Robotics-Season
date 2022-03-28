package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;

//import edu.wpi.first.wpilibj.motorcontrol.Spark;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {
    
    private final WPI_TalonFX _intake    = new WPI_TalonFX(30);
    private final WPI_TalonFX _index     = new WPI_TalonFX(31); 
    private final WPI_TalonFX _OTBI      = new WPI_TalonFX(32); //The motor to lower the intake
    private final TimeOfFlight _catch    = new TimeOfFlight(101);
    private final TimeOfFlight _barrel   = new TimeOfFlight(102);
    private final OnOffDelay _lowDelay   = new OnOffDelay( 0.05, 1, () -> _catch.getRange() < 100  );
    private final OnOffDelay _highDelay  = new OnOffDelay( 0.0, 0.15, () -> _barrel.getRange() < 100 );
    private       boolean     indexing   = false;

//puts stuff on the Smart Dashboard
protected void execute() {
    //SmartDashboard.putNumber("_catch", _catch.getRange());
    //SmartDashboard.putNumber("_barrel", _barrel.getRange());
    //System.out.println( _index.getSelectedSensorPosition());
    System.out.println( _OTBI.getSelectedSensorPosition());
    //56408.0 Limit
}

//sets Ranging Mode on TOF sensors
public Intake() {
    _catch.setRangingMode    ( RangingMode.Short, 24.0d );
    _barrel.setRangingMode   ( RangingMode.Short, 24.0d );

    _index.configFactoryDefault();
    //_index.config_kF( 0, 0.125,  30); //determined by CTRE tuner
    _index.config_kP( 0, 0.125,           30);
    _index.config_kI( 0, 0.00125,         30);
    _index.config_kD( 0, 12.0,            30);
    _index.config_IntegralZone( 0, 400.0, 30);
    _index.setNeutralMode(NeutralMode.Brake);
    zeroEncoders();
    OTBILimit();
}    

public void zeroEncoders() {
    _OTBI.setSelectedSensorPosition(0);
}

public void LowerIntake() {
    _OTBI.set(0.5);
}

public void RaiseIntake() {
    _OTBI.set(-0.5);
}

public void OTBILimit() {

    _OTBI.configFactoryDefault();
    _OTBI.setNeutralMode( NeutralMode.Brake);
    _OTBI.configReverseSoftLimitThreshold( -500.0, 10);
    _OTBI.configForwardSoftLimitThreshold( 56000.0, 10);
    _OTBI.configForwardSoftLimitEnable(true, 10);
    _OTBI.configReverseSoftLimitEnable(true, 10);
}

//uses TOF sensors to intake or not intake  
public void Collect() {    
    if( haveBallLow() && haveBallHigh() ) {
        //_index.stopMotor();
        _intake.stopMotor();
        //_turretClass.IdleSpeed();
    }else if( haveBallHigh() && !haveBallLow()) {
        //_index.stopMotor();
        _intake.set(0.95);
        //_turretClass.IdleSpeed();
    }else if( !haveBallHigh() && haveBallLow()) {
        if ( ! indexing)
        {
            indexing = true;
            _index.setSelectedSensorPosition( 0 );
            _index.set( ControlMode.Position, 82000 );
        }
        _intake.stopMotor();
    }else if( !haveBallHigh() && !haveBallLow()){
        _index.stopMotor();
        _intake.set(0.95);
    }
}

public void feedUp(){
    _index.set(0.70);
}

//moves cargo up when the shoot button is pressed
public void Shoot(){
    indexing = false;
    _intake.set(0.40);
    _index.set(0.40);
}

//spits all cargo out
public void FlushHigh(){
    indexing = false;
    _intake.set(-0.90);
    _index.set(-0.40);
}

//spits the lower cargo out
public void intakeFlush(){
    _intake.set(-0.60);
}

//stops _intake & _index motors
public void Stop() {
    _intake.stopMotor();
    if ( ! indexing )
    {
        _index.stopMotor();
    }
}

public void setCoastMode(){
    _index.setNeutralMode(NeutralMode.Coast);
    _intake.setNeutralMode(NeutralMode.Coast);
    _OTBI.setNeutralMode( NeutralMode.Coast);
}

public boolean haveBallLow() {
    return (_lowDelay.isOn( ) );
}

public boolean haveBallHigh() {
    return ( _highDelay.isOn( ) );
}  

public boolean AutonSecondDrive() {
    if( haveBallLow() && haveBallHigh() ) {
        return true;
    }else
    return false;
}
}