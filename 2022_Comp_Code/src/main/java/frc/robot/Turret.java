package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret {
    private final WPI_TalonFX _turret    = new WPI_TalonFX(40); 
    private final WPI_TalonFX _shooter   = new WPI_TalonFX(41);     
    private final Timer       shotTimer  = new Timer();

    private boolean isShooting = false;

protected void execute(){
    SmartDashboard.putNumber("RPM", _shooter.getSelectedSensorVelocity());
}

//turns on shooter motor
public void Shoot(){
    _turret.setNeutralMode(NeutralMode.Coast); 
    _shooter.set(0.55);
    if (isShooting == false){
        shotTimer.reset();
        shotTimer.start();
    } 
    isShooting = true;
}

public void ShooterOn(){
    _shooter.set(0.55);
}

//stops shooter motor
public void StopShooter(){ 
    _shooter.stopMotor();
    isShooting = false;
}

//stops turret motor
public void StopTurret(){ 
    _turret.stopMotor();
}

//turns turret left
public void TurnLeft(){
    _turret.setNeutralMode(NeutralMode.Coast); 
    _turret.set(-.1);
    //_turret.getSpeed();
}

//turns turret right
public void TurnRight(){
    _turret.setNeutralMode(NeutralMode.Coast); 
    _turret.set(.1);
}

//sets a 5 second delay so the _shooter can get up to speed
public boolean isReady(){
    if(_shooter.getSelectedSensorVelocity() > 10000) {
        return true;
    }else {
        return false;
    }
}}