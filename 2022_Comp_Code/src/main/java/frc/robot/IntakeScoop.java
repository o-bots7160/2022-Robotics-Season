package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import java.util.function.BooleanSupplier;

public class IntakeScoop {

    private final WPI_TalonFX _scoop     = new WPI_TalonFX(30); 
    private BooleanSupplier   ballLow;
    
public IntakeScoop(BooleanSupplier new_ballLow) {

    ballLow   = new_ballLow;
    _scoop.setNeutralMode(NeutralMode.Brake);

}

public void Flush() {
    _scoop.set(-0.30);
}

public void Stop() {
    _scoop.stopMotor();
}

public void disable() {
    _scoop.setNeutralMode( NeutralMode.Coast);
    _scoop.stopMotor();
}

public void Shoot() {
    _scoop.set(0.40);
}

public void Collect() {
    if(!ballLow.getAsBoolean()) {
        _scoop.set(.45);
    }else {
        _scoop.stopMotor();
    }
}
}
