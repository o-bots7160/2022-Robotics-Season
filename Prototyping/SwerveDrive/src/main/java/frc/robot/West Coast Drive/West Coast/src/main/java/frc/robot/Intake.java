package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

public class Intake {
    private final WPI_TalonFX _intake    = new WPI_TalonFX(30);
    private final WPI_TalonFX _index     = new WPI_TalonFX(31); 

public void Collect() {
    _intake.set(0.60);
    _index.set(0.70);
}

public void Stop() {
    _intake.stopMotor();
    _index.stopMotor();
}

}
