package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;

//import edu.wpi.first.wpilibj.motorcontrol.Spark;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {
    
    private final WPI_TalonFX _intake    = new WPI_TalonFX(30);
    private final WPI_TalonFX _index     = new WPI_TalonFX(31); 
    private final TimeOfFlight _catch    = new TimeOfFlight(101);
    private final TimeOfFlight _barrel   = new TimeOfFlight(102);
    private final OnOffDelay _lowDelay   = new OnOffDelay( 0.05, 1, () -> _catch.getRange() < 100  );
    private final OnOffDelay _highDelay  = new OnOffDelay( 0.0, 0.15, () -> _barrel.getRange() < 100 );
    //private final Spark _LEDi             = new Spark(1);

//puts stuff on the Smart Dashboard
protected void execute() {
    //SmartDashboard.putNumber("_catch", _catch.getRange());
    //SmartDashboard.putNumber("_barrel", _barrel.getRange());
}

//sets Ranging Mode on TOF sensors
public Intake() {
    _catch.setRangingMode    ( RangingMode.Short, 24.0d );
    _barrel.setRangingMode   ( RangingMode.Short, 24.0d );
}    

//uses TOF sensors to intake or not intake  
public void Collect() {    
    if( haveBallLow() && haveBallHigh() ) {
        //_LEDi.set(.75);
        _index.stopMotor();
        _intake.stopMotor();
    }else if( haveBallHigh() && !haveBallLow()) {
        //_LEDi.set(.57);
        _index.stopMotor();
        _intake.set(0.90);
    }else if( !haveBallHigh() && haveBallLow()) {
        //_LEDi.set(.57);
        _index.set(0.70);
        _intake.stopMotor();
    }else if( !haveBallHigh() && !haveBallLow()){
        _index.stopMotor();
        _intake.set(0.90);
    }
}

public void feedUp(){
    _index.set(0.70);
}

//moves cargo up when the shoot button is pressed
public void Shoot(){
    _intake.set(0.40);
    _index.set(0.40);
}

//spits all cargo out
public void FlushHigh(){
    _intake.set(-0.90);
    _index.set(-0.40);
}

//spits the lower cargo out
public void intakeFlush(){
    _intake.set(-0.60);
    _index.stopMotor();
}

//stops _intake & _index motors
public void Stop() {
    _intake.stopMotor();
    _index.stopMotor();
}

public boolean haveBallLow() {
    return (_lowDelay.isOn( ) );
}

public boolean haveBallHigh() {
    return (_highDelay.isOn( ) );
}  
}