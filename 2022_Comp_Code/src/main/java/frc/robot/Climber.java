package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

public class Climber {

    private final WPI_TalonFX _Climber    = new WPI_TalonFX(61);
    private final WPI_TalonFX _Tiltinator = new WPI_TalonFX(62);
    
    //sets _Climber and _Tiltinator to brake mode and sets the sensor to 0
    public Climber() {
        _Climber.setNeutralMode(NeutralMode.Brake);
        _Climber.setSelectedSensorPosition(0.0d);
        _Tiltinator.setNeutralMode(NeutralMode.Brake);
        _Tiltinator.setSelectedSensorPosition(0.0);
    }

    //resets the _Tiltinator to 0
    public void reset(){
        _Climber.setSelectedSensorPosition(0.0d);
        _Tiltinator.setSelectedSensorPosition(0.0);
    }

    //prints the _Tiltinator position to the console
    public void execute(){
        System.out.println(_Tiltinator.getSelectedSensorPosition());
    }

    //pushes the _Climber out
    public void Extend() {
        if (_Climber.getSelectedSensorPosition() >= 611586) {  //TODO test for number
            _Climber.stopMotor();
        } else {
            _Climber.set(.75);
        }
    }

    //pulls the _Climber in
    public void Retract( boolean ignore) {
        if ( _Climber.getSelectedSensorPosition() <= 0 &&
             ! ignore ) {   //TODO test for number
            _Climber.stopMotor();
        } else {
             _Climber.set(-.75);
        }
    }

    //pushes the _Tiltinator away from the robot
    public void Push() {
        if(_Tiltinator.getSelectedSensorPosition() < 45903.0) {
            _Tiltinator.set(.125);
        } else {
            _Tiltinator.stopMotor();
        }
    }

    //pulls the _Tiltinator towards the robot
    public void Pull() {
        _Tiltinator.set(-.125);
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
