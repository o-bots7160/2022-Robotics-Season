package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurretShooter {
    private final WPI_TalonFX _shooter      = new WPI_TalonFX(41);     
    private double            speed;

    public TurretShooter() {
        _shooter.configFactoryDefault();
		_shooter.config_kF( 0, 0.045,          30); //determined by CTRE tuner
		_shooter.config_kP( 0, 0.25,           30);
		_shooter.config_kI( 0, 0.001,          30);
		_shooter.config_kD( 0, 9.0,            30);
		_shooter.config_IntegralZone( 0, 50.0, 30);
    }

    public void Execute() {
        SmartDashboard.putNumber("RPM", _shooter.getSelectedSensorVelocity());
    }

    public void IdleSpeed() {
        speed = 1500;
        _shooter.setNeutralMode(NeutralMode.Coast); 
        _shooter.set( ControlMode.Velocity, speed);
    }
    
    public void AutonIdleSpeed() {
        speed = 7500;
        _shooter.setNeutralMode(NeutralMode.Coast); 
        _shooter.set( ControlMode.Velocity, speed);
    }
    public void SetHigh(){
        speed = 15500;
        _shooter.set( ControlMode.Velocity, speed);
    }
    
    //sets shooter to shoot into the lower hub
    public void SetLow(){
        speed = 6000;
        _shooter.set( ControlMode.Velocity, speed);
    }
    
    //sets shooter to shoot into the upper hub from around the tarmac
    public void shootAtX(){
        speed = 13500;
        _shooter.set( ControlMode.Velocity, speed);
    }
    
    //stops shooter motor
    public void StopShooter(){ 
        _shooter.set(0.0);
    }

    //sets a 5 second delay so the _shooter can get up to speed
public boolean isReady(){
    double error = Math.abs( _shooter.getSelectedSensorVelocity() - speed );

    if( error < 200 ) {
        return true;
    }else {
        return false;
    }
}

//turns on shooter motor
public void Shoot(){
    _shooter.set( ControlMode.Velocity, speed);
}
}
