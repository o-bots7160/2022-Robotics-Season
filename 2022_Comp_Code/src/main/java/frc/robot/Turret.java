package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Turret {
    private final WPI_TalonFX _turret    = new WPI_TalonFX(40); 
    private final WPI_TalonFX _shooter   = new WPI_TalonFX(41);     
    private final Timer       shotTimer  = new Timer(); 
    
    private boolean isShooting                = false;
    private boolean m_LimelightHasValidTarget = false;

    private double m_LimelightSteerCommand = 0.0;
    private boolean isHigh = false;
    private double offset;


protected void execute(){
    //SmartDashboard.putNumber("RPM", _shooter.getSelectedSensorVelocity());
    //SmartDashboard.putNumber("Turrent Position", offset - _turret.getSelectedSensorPosition());
    SmartDashboard.putBoolean("Target?", m_LimelightHasValidTarget);
    //Update_Limelight_Tracking();
}

public Turret() {
    offset = _turret.getSelectedSensorPosition();
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

//sets shooter speed based off of switch
public void ShooterOn(){
    
}

//sets shooter to shoot into the upper hub
public void SetHigh(){
    isHigh = true;
    _shooter.set(0.70);
}

//sets shooter to shoot into the lower hub
public void SetLow(){
    isHigh = false;
    _shooter.set(0.35);
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
    setTurret(-0.125);
}

//turns turret right
public void TurnRight(){
    setTurret(0.125);
}

//sets a 5 second delay so the _shooter can get up to speed
public boolean isReady(){
    double target = 5000;
    if ( isHigh )
    {
        target = 14000;
    } 
    if(_shooter.getSelectedSensorVelocity() > target) {
        return true;
    }else {
        return false;
    }
}

public void Update_Limelight_Tracking(){
        // These numbers must be tuned for your Robot!  Be careful!
        final double TURN_K = 0.0225;                     // how hard to turn toward the target
        final double RIGHT_MAX = 0.3;                   // Max speed the turret motor can go
        final double LEFT_MAX = -0.3;
        final double RIGHT_MIN = 0.04;
        final double LEFT_MIN = -0.04;

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);

        //System.out.println("tv" + tv);
        //System.out.println( "tx" + tx);
        if (tv < 1.0)
        {
          m_LimelightHasValidTarget = false;
          m_LimelightSteerCommand = 0.0;
          return;
        }

        m_LimelightHasValidTarget = true;

        // Start with proportional steering
        double turn_cmd = tx * TURN_K;
        if (turn_cmd > RIGHT_MAX)
        {
          turn_cmd = RIGHT_MAX;
        }else if(turn_cmd > 0 && turn_cmd < RIGHT_MIN){
          turn_cmd = RIGHT_MIN;
        }else if(turn_cmd < 0 && turn_cmd > LEFT_MIN){
          turn_cmd = LEFT_MIN;
        } else if (turn_cmd < LEFT_MAX){
          turn_cmd = LEFT_MAX;
        }

        m_LimelightSteerCommand = turn_cmd;
        setTurret( m_LimelightSteerCommand );

  }

private void setTurret(double turnRate) {
    double position = offset - _turret.getSelectedSensorPosition();

    if (turnRate > 0.05 && position > -50000) {
        _turret.setNeutralMode(NeutralMode.Coast); 
        _turret.set(turnRate);
    } else if (turnRate < 0.05 && position < 50000) {
        _turret.setNeutralMode(NeutralMode.Coast); 
        _turret.set(turnRate);
    } else {
        _turret.stopMotor();
    }
  }
}