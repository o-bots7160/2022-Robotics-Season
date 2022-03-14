package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class Robot extends TimedRobot {

  private final WestCoastDrive _westCoastDrive    = new WestCoastDrive(); 
  private final Intake _intakeClass               = new Intake();
  private final Turret _turretClass               = new Turret();
  private final SendableChooser<AUTO> _chooser    = new SendableChooser<>();
  private final Climber _climberClass             = new Climber();
  private final Timer   timer                     = new Timer();
  private final Spark _LED                        = new Spark(1);
  private final Timer   endGameTimer              = new Timer(); 

  private enum AUTO {
    LAUNCHAUTO
  }

  private AUTO autonTracker;

  private  enum LAUNCHAUTO {
    BALLPICKUP,
    TURN,
    SHOOT,
    STOP
  }
  private LAUNCHAUTO lA = LAUNCHAUTO.BALLPICKUP;

  @Override
  public void robotInit() {
    //sets up auton options on the Smart Dashboard
    _chooser.setDefaultOption("LAUNCHAUTO", AUTO.LAUNCHAUTO);
    //_chooser.addOption(name, object);
    //UI.setBlue();
  }

  @Override
  public void robotPeriodic() {
    _intakeClass.execute();
    _turretClass.execute();
    //_climberClass.execute();
    //puts options and result on Smart Dashboard
    _westCoastDrive.robotPeriodic();

    SmartDashboard.putData(_chooser);
    _westCoastDrive.robotPeriodic();  
  }

  @Override
  public void autonomousInit() {
    endGameTimer.reset();
    endGameTimer.start();
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
    _westCoastDrive.autonomousInit();
    _westCoastDrive.zeroEncoders();
    //UI.setBlue();
    _LED.set(-.95);
    switch ( _chooser.getSelected() ){
      case LAUNCHAUTO:
        autonTracker = AUTO.LAUNCHAUTO;
        lA = LAUNCHAUTO.BALLPICKUP;
        break;
    }
  }

  @Override
  public void autonomousPeriodic() {

    switch(autonTracker){
      case LAUNCHAUTO:
      launchAuto();
      break;
    }
    _westCoastDrive.setBrakeMode();
    
  }

  private void launchAuto () {
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(lA){
        
        case BALLPICKUP:
        _turretClass.SetHigh();
        _intakeClass.Collect();
        if(_westCoastDrive.moveTo(65, 9)){
          System.out.println("Is driving");
          
        }else{
          lA = LAUNCHAUTO.TURN;
        }
        break;

        case TURN:
        if(_westCoastDrive.turnTo(95, 20)){
          _intakeClass.Collect();
        }else{
          timer.reset();
          timer.start();
          lA = LAUNCHAUTO.SHOOT;
        }
        break;

        case SHOOT:
        _turretClass.Update_Limelight_Tracking();
        _turretClass.Shoot();
        if(_turretClass.isReady()) {
          _intakeClass.Shoot();
        }else if (timer.hasElapsed( 10 )) {
          _turretClass.StopShooter();
          _intakeClass.Stop();
        }
        break;

        case STOP:
        break;
      }
  }
  
  @Override
  public void teleopInit() {
    _climberClass.reset();
    endGameTimer.reset();
    endGameTimer.start();
    _turretClass.zeroEncoders(); // Comment at match
    _turretClass.softLimits(); // Comment at match
    _turretClass.breakMode(); // Move this to Auton init at comp
    _intakeClass.breakMode();
  }

  @Override
  public void teleopPeriodic() {
    if(endGameTimer.get() > 85.0d){
      _LED.set(-.25);
      } else if(_intakeClass.haveBallHigh() && !_intakeClass.haveBallLow()){
      _LED.set(.57);
      } else if (_intakeClass.haveBallHigh() && _intakeClass.haveBallLow()){
      _LED.set(.75);
    }else{
   _LED.set(-.45);
    }
 
    _westCoastDrive.arcadeDrive(UI.yInput(), UI.zInput()); 
    
    if(UI.getIntake())
    {
      _intakeClass.Collect();
      if (_intakeClass.haveBallHigh()) {
        //_turretClass.IdleSpeed();
      } else {
        _turretClass.StopShooter();
      }
    }
    else if(UI.getShoot())
    { 
      if (UI.getShooterLow()) {
        _turretClass.SetLow();
      } else {
        if(!UI.getSafeZone()){ // Yes I know backwards
        _turretClass.SetHigh();
        }else {
          _turretClass.shootAtX();
        }
      }
      _turretClass.Shoot();
      if (_turretClass.isReady())
      {
        _intakeClass.Shoot();
      }
      else
      {
        _intakeClass.Stop();
      }
    }
    else if(UI.getFlushHigh())
    {
        _intakeClass.FlushHigh();
    }
    else if(UI.getFlushLow())
    {
      _intakeClass.intakeFlush();
    }
    else
    {
        _intakeClass.Stop();
        if (_intakeClass.haveBallHigh()) {
          //_turretClass.IdleSpeed();
        } else {
          _turretClass.StopShooter();
        }
    }

    if(UI.getAutoAim()){
      _turretClass.breakMode();
      if(UI.getShoot()){
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
        _turretClass.Update_Limelight_Tracking();
      }else{
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
        _turretClass.manualControl();
      }
    }else{
      _turretClass.breakMode();
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(3); 
      _turretClass.manualControl();
    
	}

  if(UI.getClimbExtend()){
    _climberClass.Extend();
  }
  else if(UI.getClimbRetract()){
    _climberClass.Retract( UI.getIgnoreLimits());
  }
  else{
    _climberClass.StopClimber();
  }

  if(UI.getClimbPull()){
    _climberClass.Pull();
  }
  else if(UI.getClimbPush()){
    _climberClass.Push();
  }
  else{
    _climberClass.StopTilt();
  }
}

  @Override
  public void disabledInit() {
    _westCoastDrive.setCoastMode();
    _climberClass.setCoastMode();
    _turretClass.setCoast();
  }

  @Override
  public void disabledPeriodic() {
    _turretClass.disabledInit();
  }

  int step = 0;
  @Override
  public void testInit() {
  } 

  @Override
  public void testPeriodic() {
  }
}
