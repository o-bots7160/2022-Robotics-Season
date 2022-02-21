package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;



public class Climber {

    private final WPI_TalonFX _Climber    = new WPI_TalonFX(1);
    private final WPI_TalonFX _Tiltinator = new WPI_TalonFX(2);


    public void Extend() {
        _Climber.set(.25);
    }
    public void Retract() {
        _Climber.set(-.25);
    }

    public void Push() {
        _Tiltinator.set(.25);
    }
    public void Pull() {
        _Tiltinator.set(-.25);
    }

    public void StopClimber() {
        _Climber.stopMotor();

    }

    public void StopTilt() {
        _Tiltinator.stopMotor();
    }
}
