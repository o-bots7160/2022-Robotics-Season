package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;


public class Turret {
    private final WPI_TalonFX _turret    = new WPI_TalonFX(40); 
    private final WPI_TalonFX _shooter   = new WPI_TalonFX(41);     
    private final Timer       shotTimer  = new Timer();
    private final DigitalInput leftLimitSW = new DigitalInput( 1 ); 
    private final DigitalInput rightLimitSW = new DigitalInput( 0 ); 
    
    private boolean isShooting                = false;
    private boolean m_LimelightHasValidTarget = false;

    private double m_LimelightSteerCommand = 0.0;
    private boolean isHigh = false;
    private double _turret_power;

    private double leftLimit = -76260.0;
    private double rightLimit = 64774.0;

    public void zeroEncoders(){
        _turret.setSelectedSensorPosition(0.0);
    }


protected void execute(){
    //SmartDashboard.putNumber("RPM", _shooter.getSelectedSensorVelocity());
    //SmartDashboard.putNumber("Turrent Position", offset - _turret.getSelectedSensorPosition());
    //SmartDashboard.putBoolean("Target?", m_LimelightHasValidTarget);
    //Update_Limelight_Tracking();
}


//turns on shooter motor
public void Shoot(){
    _turret.setNeutralMode(NeutralMode.Coast); 
    _shooter.set(_turret_power);
    if (isShooting == false){
        shotTimer.reset();
        shotTimer.start();
    } 
    isShooting = true;
}

//sets shooter to shoot into the upper hub
public void SetHigh(){
    isHigh = true;
    _turret_power = 0.67;
}

//sets shooter to shoot into the lower hub
public void SetLow(){
    isHigh = false;
    _turret_power = 0.40;
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
    double target = 7500;
    if ( isHigh )
    {
        target = 14650;
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
        if((turnRate > 0 && !leftLimitSW.get())||(turnRate < 0 && !rightLimitSW.get())){
            _turret.stopMotor();
        }else{
            _turret.set(turnRate);
        }
     }

    public void setCoast(){
        _turret.setNeutralMode(NeutralMode.Coast);
    }

  public double getTicks(){
      return _turret.getSelectedSensorPosition();
  }
}