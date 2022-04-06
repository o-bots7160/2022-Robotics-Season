package frc.robot;

import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;

//import edu.wpi.first.wpilibj.motorcontrol.Spark;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeAssembly {
    private final IntakeIndexer _indexer;
    private final IntakeScoop _scoop;
    private final IntakeOTBI _OTBI;
    private final TimeOfFlight _catch     = new TimeOfFlight(101);
    private final TimeOfFlight _barrel    = new TimeOfFlight(102);
    private final OnOffDelay _lowDelay    = new OnOffDelay( 0.05, 0.5, () -> _catch.getRange() < 100  );
    private final OnOffDelay _highDelay   = new OnOffDelay( 0.6, 0.15, () -> _barrel.getRange() < 100 );
    private boolean OTBI_high = false;

//puts stuff on the Smart Dashboard
protected void execute() {
    //SmartDashboard.putNumber("_catch", _catch.getRange());
    //SmartDashboard.putNumber("_barrel", _barrel.getRange());
    //System.out.println( _index.getSelectedSensorPosition());
    _OTBI.Execute();
    //56408.0 Limit
}

public void ZeroEncoders() {
    _OTBI.zeroEncoders();
}

//sets Ranging Mode on TOF sensors
public IntakeAssembly() {
    _indexer = new IntakeIndexer( () -> haveBallLow(),
                                  () -> haveBallHigh() );
    _OTBI    = new IntakeOTBI();
    _scoop   = new IntakeScoop(   () -> haveBallLow());
    _catch.setRangingMode    ( RangingMode.Short, 24.0d );
    _barrel.setRangingMode   ( RangingMode.Short, 24.0d );
}    
//uses TOF sensors to intake or not intake  
public void Collect() {
    OTBI_high = false;    
    _indexer.Collect();
    _scoop.Collect();
    if(haveBallHigh() && haveBallLow()) {
        _OTBI.RaiseIntake();
    }else {
        _OTBI.LowerIntake();
    }
}

//moves cargo up when the shoot button is pressed
public void Shoot(){
    _scoop.Shoot();
    _indexer.Shoot();
    _OTBI.RaiseIntake();
}

//spits all cargo out
public void FlushHigh(){
    _scoop.Flush();
    _OTBI.RaiseIntake();
    _indexer.Flush();
}

//spits the lower cargo out
public void intakeFlush() {
    _scoop.Flush();
    _OTBI.RaiseIntake();
}

//stops _intake & _index motors
public void Stop() {
    _scoop.Stop();
    _indexer.StopAfterBall();
    if ( OTBI_high )
    {
        _OTBI.StowIntake();
    } else {
        _OTBI.RaiseIntake();
    }
}

public void setCoastMode(){
    _indexer.disable();
    _scoop.disable();
    _OTBI.disable();
}

public void StowIntake() {
    OTBI_high = true;
    _OTBI.StowIntake();
}

public boolean haveBallLow() {
    return (_lowDelay.isOn( ) );
}

public boolean haveBallHigh() {
    return ( _highDelay.isOn( ) );
}  

public boolean HaveTwoBalls() {
    if( haveBallLow() && haveBallHigh() ) {
        return true;
    }else
    return false;
}
}