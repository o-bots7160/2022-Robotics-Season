package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

public class Turret {
    private final WPI_TalonFX _turret    = new WPI_TalonFX(40); 
    private final WPI_TalonFX _shooter   = new WPI_TalonFX(41);     

//turns on shooter motor
public void Shoot(){
    _turret.setNeutralMode(NeutralMode.Coast); 
    _shooter.set(0.55);
}

//stops shooter motor
public void StopShooter(){ 
    _shooter.stopMotor();
}

//stops turret motor
public void StopTurret(){ 
    _turret.stopMotor();
}

//turns turret left
public void TurnLeft(){
    _turret.setNeutralMode(NeutralMode.Coast); 
    _turret.set(-.1);
}

//turns turret right
public void TurnRight(){
    _turret.setNeutralMode(NeutralMode.Coast); 
    _turret.set(.1);
}
}
 