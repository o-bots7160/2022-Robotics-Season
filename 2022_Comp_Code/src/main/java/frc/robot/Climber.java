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

    public boolean Extend() {
        boolean Extend_Value = false;
        if(_Climber.getSelectedSensorPosition() < 500) {
            _Climber.set(.25);
        } else {
            _Climber.stopMotor();
            Extend_Value = true;
        }
        return Extend_Value;
    }
    public boolean Retract() {
        boolean Return_Value = false;
        if(_Climber.getSelectedSensorPosition() > 0) {
            _Climber.set(-.25);
        } else {
            _Climber.stopMotor();
            Return_Value = true;
        }
        return Return_Value;
    }

    public boolean Push() {
        boolean Push_Value = false;
        if(_Tiltinator.getSelectedSensorPosition() < 45903.0) {
            _Tiltinator.set(.25);
        } else {
            _Tiltinator.stopMotor();
            Push_Value = true;
        }
        return Push_Value;
    }
    public boolean Pull() {
        boolean Pull_Value = true;
        if(_Tiltinator.getSelectedSensorPosition() > 0) {
            _Tiltinator.set(-.25);
        } else {
            _Tiltinator.stopMotor();
            Pull_Value = true;
        }
        return Pull_Value;
    }

    public void StopClimber() {
        _Climber.stopMotor();

    }

    public void StopTilt() {
        _Tiltinator.stopMotor();
    }

    public void setCoastMode(){
        _Tiltinator.setNeutralMode(NeutralMode.Coast);
    }
}
