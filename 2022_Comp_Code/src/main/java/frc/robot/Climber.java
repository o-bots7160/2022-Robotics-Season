package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

public class Climber {

    private final WPI_TalonFX _Climber    = new WPI_TalonFX(61);
    private final WPI_TalonFX _Tiltinator = new WPI_TalonFX(62);
    

    public Climber() {
        _Climber.setNeutralMode(NeutralMode.Brake);
        _Tiltinator.setNeutralMode(NeutralMode.Brake);
        _Tiltinator.setSelectedSensorPosition(0.0);
    }

    public void reset(){
        _Tiltinator.setSelectedSensorPosition(0.0);
    }


    public void execute(){
        System.out.println(_Tiltinator.getSelectedSensorPosition());
    }

    public void Extend() {

            _Climber.set(.75);
    }

    public void Retract() {
            _Climber.set(-.75);
    }

    public void Push() {
        if(_Tiltinator.getSelectedSensorPosition() < 45903.0) {
            _Tiltinator.set(.25);
        } else {
            _Tiltinator.stopMotor();
        }
    }

    public void Pull() {
            _Tiltinator.set(-.25);
    }

    //stops the _Climber motor
    public void StopClimber() {
        _Climber.stopMotor();
    }

    //stops the _Tiltinator motor
    public void StopTilt() {
        _Tiltinator.stopMotor();
    }

    //sets the _Tiltinator motor to coast mode
    public void setCoastMode(){
        _Tiltinator.setNeutralMode(NeutralMode.Coast);
    }
}
