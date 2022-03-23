package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Turret {
    enum ShootPosition {
        LOW,
        HIGH,
        SAFE
    }
    private final WPI_TalonFX _turret       = new WPI_TalonFX(40); 
    private final WPI_TalonFX _shooter      = new WPI_TalonFX(41);     
    private final Timer       shotTimer     = new Timer();
    private final DigitalInput leftLimitSW  = new DigitalInput( 1 ); 
    private final DigitalInput rightLimitSW = new DigitalInput( 0 ); 
    
    private boolean isShooting                = false;
    private boolean m_LimelightHasValidTarget = false;

    private double m_LimelightSteerCommand = 0.0;
    private ShootPosition position = ShootPosition.LOW;
    private double _turret_speed;

    private double lToR                       = 126774.0;  // Left To Right
    private double lToRS                      = 114962.0;  // Left To Right Slow Down Distance
    private double lToCS                      = 49290.0;   // Left to Center Slow Down Distance
    private double lToC                       = 64367.0;   // Left To Center
    private double rToL                       = 125654.0;  // Right To Left
    private double rToLS                      = 114962.0;  // Right To Left Slow Down Distance
    private double rToCS                      = 45521.0;   // Right To Center Slow Down Distance
    private double rToC                       = 62019.0;   // Right To Center
    private boolean centering                 = false;
    private boolean toLeft                    = false;
    private boolean toRight                   = false;


    private double target = 0.0;
    private double greaterSlow = 0.0;
    private double lesserSlow = 0.0;

    // Zeroed Direction
    enum zeroedDirection{
        LEFT,
        RIGHT
    }

    public Turret( ){
        _shooter.configFactoryDefault();
		_shooter.config_kF( 0, 0.045,  30); //determined by CTRE tuner
		_shooter.config_kP( 0, 0.25,           30);
		_shooter.config_kI( 0, 0.001,          30);
		_shooter.config_kD( 0, 9.0,            30);
		_shooter.config_IntegralZone( 0, 50.0, 30);
    }

    private zeroedDirection zD;

    public void zeroEncoders(){
        _turret.setSelectedSensorPosition(0.0);
    }

    public void teleopInit(){
        target = 0;
    }


protected void execute(){
    SmartDashboard.putNumber("RPM", _shooter.getSelectedSensorVelocity());
    //SmartDashboard.putNumber("Turrent Position", offset - _turret.getSelectedSensorPosition());
    //SmartDashboard.putBoolean("Target?", m_LimelightHasValidTarget);
    //Update_Limelight_Tracking();
}


//turns on shooter motor
public void Shoot(){
    _turret.setNeutralMode(NeutralMode.Coast); 
    _shooter.set( ControlMode.Velocity, _turret_speed);
    if (isShooting == false){
        shotTimer.reset();
        shotTimer.start();
    } 
    isShooting = true;
}

public void breakMode(){
    _turret.setNeutralMode(NeutralMode.Brake);
}

public void disabledInit(){
    _turret.setNeutralMode(NeutralMode.Coast);
}

public void IdleSpeed() {
    _turret_speed = 300;
    _shooter.setNeutralMode(NeutralMode.Coast); 
    _shooter.set( ControlMode.Velocity, _turret_speed);
}

//sets shooter to shoot into the upper hub
public void SetHigh(){
    position = ShootPosition.SAFE;
    _turret_speed = 14200;
    _shooter.set( ControlMode.Velocity, _turret_speed);
}

//sets shooter to shoot into the lower hub
public void SetLow(){
    position = ShootPosition.LOW;
    _turret_speed = 3000;
    _shooter.set( ControlMode.Velocity, _turret_speed);
}

//sets shooter to shoot into the upper hub from around the tarmac
public void shootAtX(){
    position = ShootPosition.HIGH;
    _turret_speed = 12750;
    _shooter.set( ControlMode.Velocity, _turret_speed);
}

//stops shooter motor
public void StopShooter(){ 
    _shooter.set(0.0);
    isShooting = false;
}


//stops turret motor
public void StopTurret(){ 
    _turret.stopMotor();
}

//turns turret left, goes faster if AutoAim is on
public void TurnLeft(){
    if (UI.getAutoAim()) {
        setTurret(-0.2);   //TODO test for number
    } else {
        setTurret(-0.25);
    }
}

//turns turret right, goes faster if AutoAim is on
public void TurnRight(){
    if (UI.getAutoAim()) {
        setTurret(0.2);    //TODO test for number
    } else {
        setTurret(0.25);
    }
}

//sets a 5 second delay so the _shooter can get up to speed
public boolean isReady(){
    double error = Math.abs( _shooter.getSelectedSensorVelocity() - _turret_speed );

    if( error < 100 ) {
        return true;
    }else {
        return false;
    }
}

public void Update_Limelight_Tracking(){
        // These numbers must be tuned for your Robot!  Be careful!
        final double TURN_K = 0.0225;                     // how hard to turn toward the target
        final double RIGHT_MAX = 1;                   // Max speed the turret motor can go
        final double LEFT_MAX = -1;
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

  public void softLimits(){
      _turret.configForwardSoftLimitThreshold(-3000, 0);
      _turret.configReverseSoftLimitThreshold(-121000.0, 0);
      _turret.configForwardSoftLimitEnable(true, 0);
      _turret.configReverseSoftLimitEnable(true, 0);
  }

  public void manualControl(){
    if(UI.getTurretLeft()) {
        TurnLeft();
        System.out.println("LEFT TURN");
      }
      else if(UI.getTurretRight()) {
        TurnRight();
        System.out.println("RIGHT TURN");
      }else{
        StopTurret();
      }
  }

  public void turretControl() {
      System.out.println("Target: " + target);;

    if(!leftLimitSW.get()){
        zD = zeroedDirection.LEFT;
        zeroEncoders();
    }else if(!rightLimitSW.get()){
        zD = zeroedDirection.RIGHT;
        zeroEncoders();
    }
        
    switch(zD){
        case LEFT:
        if(target > lToR){
            target = lToR;
        }else if(target < 0){
            target = 0;
        }

        if(UI.getTurretLeft() && !UI.getLimit() && !UI.getCenter() && !UI.getTurretRight()){
            target-=1000;
        }else if(UI.getTurretRight() && !UI.getLimit() && !UI.getCenter() && !UI.getTurretLeft()){
            target+=1000;
        }else if(UI.getCenter() && !UI.getTurretLeft() && !UI.getLimit() && !UI.getTurretRight()){
            target = lToC;
            lesserSlow = lToCS;
            greaterSlow = rToCS;
        }else if(UI.getLimit() && UI.getTurretLeft() && !UI.getCenter() && !UI.getTurretRight()){
            target = rToL;
        }else if(UI.getLimit() && UI.getTurretRight() && !UI.getCenter() && !UI.getTurretLeft()){
            target = lToR;
        }

        if (getTicks() > target-500 && getTicks() < target+500){
            StopTurret();
        }else{
            if(getTicks() < target){
                if(getTicks() < lesserSlow){
                    setTurret(.2);
                }else{
                    setTurret(.125);
                }
            }else if(getTicks() > target){
                if(getTicks() > greaterSlow){
                    setTurret(-.2);
                }else{
                    setTurret(-.125);
                }
            }
        }
        break;
        case RIGHT:
        if(target > rToL){
            target = rToL;
        }else if(target < 0){
            target = 0;
        }

        if(UI.getTurretLeft() && !UI.getLimit() && !UI.getCenter() && !UI.getTurretRight()){
            target+=1000;
        }else if(UI.getTurretRight() && !UI.getLimit() && !UI.getCenter() && !UI.getTurretLeft()){
            target-=1000;
        }else if(UI.getCenter() && !UI.getTurretLeft() && !UI.getLimit() && !UI.getTurretRight()){
            target = rToC;
            lesserSlow = rToCS;
            greaterSlow = lToCS;
        }else if(UI.getLimit() && UI.getTurretLeft() && !UI.getCenter() && !UI.getTurretRight()){
            target = lToR;
        }else if(UI.getLimit() && UI.getTurretRight() && !UI.getCenter() && !UI.getTurretLeft()){
            target = rToL;
        }

        if (getTicks() > target-500 && getTicks() < target+500){
            StopTurret();
        }else{
            if(getTicks() < target){
                if(getTicks() < lesserSlow){
                    setTurret(-.4);
                }else{
                    setTurret(-.2);
                }
            }else if(getTicks() > target){
                if(getTicks() > greaterSlow){
                    setTurret(.4);
                }else{
                    setTurret(.2);
                }
            }
        }
        break;

    }

  }

  public void AutonCenterTurret() {
      target = rToC;
      lesserSlow = rToCS;
      greaterSlow = lToCS;
  }
  
}