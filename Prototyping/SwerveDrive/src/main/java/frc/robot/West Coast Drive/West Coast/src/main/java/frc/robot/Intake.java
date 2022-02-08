package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {
    private final WPI_TalonFX _intake    = new WPI_TalonFX(30);
    private final WPI_TalonFX _index     = new WPI_TalonFX(31); 
    private final TimeOfFlight _catch    = new TimeOfFlight(101);
    private final TimeOfFlight _barrel   = new TimeOfFlight(102);
    
protected void execute() {
    SmartDashboard.putNumber("_catch", _catch.getRange());
    SmartDashboard.putNumber("_barrel", _barrel.getRange());
}

public void Intake() {
    _catch.setRangingMode    ( RangingMode.Short, 24.0d );
    _barrel.setRangingMode   ( RangingMode.Short, 24.0d );
}    

public void Collect() {
    if( haveBallLow() ) {
        _intake.stopMotor();
    }else{
        _intake.set(0.60);
    }
    if( haveBallHigh() ) {
        _index.stopMotor();
    }else{
        _index.set(0.70);
    }

    if( haveBallLow() && haveBallHigh() ) {
        _index.stopMotor();
        _intake.stopMotor();
    }else if( haveBallHigh() && !haveBallLow()) {
        _index.stopMotor();
        _intake.set(0.60);
    }else if( !haveBallHigh() && haveBallLow()) {
        _index.set(0.70);
        _intake.stopMotor();
    }else {
        _index.stopMotor();
        _intake.set(0.60);
    }
}

public void Stop() {
    _intake.stopMotor();
    _index.stopMotor();
}

public boolean haveBallLow() {
    return (_catch.getRange() < 100);
}

public boolean haveBallHigh() {
    return (_barrel.getRange() < 100);
}
    
}
