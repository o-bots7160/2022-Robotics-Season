package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends TimedRobot {

  private final WestCoastDrive _westCoastDrive    = new WestCoastDrive(); 
  private final Intake _intakeClass               = new Intake();
  private final Turret _turretClass               = new Turret();
  private final SendableChooser<AUTO> _chooser    = new SendableChooser<>();
  private final Climber _climberClass             = new Climber();
  private final Timer   timer                     = new Timer();

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
    UI.setLedsBlue();
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
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
    _westCoastDrive.autonomousInit();
    _westCoastDrive.zeroEncoders();
    UI.setLedsBlue();
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
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(2); 
    switch(lA){
        
        case BALLPICKUP:
        _turretClass.SetHigh();
        _intakeClass.Collect();
        if(_westCoastDrive.moveTo(56, 9)){
          System.out.println("Is driving");
          
        }else{
          lA = LAUNCHAUTO.TURN;
        }
        break;

        case TURN:
        if(_westCoastDrive.turnTo(100, 35)){
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
    UI.setLedsBlue();
  }

  @Override
  public void teleopPeriodic() {


    _westCoastDrive.arcadeDrive(UI.yInput(), UI.zInput()); 
    
    if(UI.getIntake())
    {
      _intakeClass.Collect();
    }
    else if(UI.getShoot())
    { 
      if (UI.getShooterLow()) {
        _turretClass.SetLow();
      } else {
        _turretClass.SetHigh();
      }
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
      _turretClass.StopShooter();
    }


    if(UI.getAutoAim()){
      _turretClass.Update_Limelight_Tracking();
    }else{
      if(UI.getTurretLeft()) {
        _turretClass.TurnLeft();
      }
      else if(UI.getTurretRight()) {
        _turretClass.TurnRight();
      }else{
        _turretClass.StopTurret();
      }

    if(UI.getClimbExtend()){
        _climberClass.Extend();
    }
    else if(UI.getClimbRetract()){
      _climberClass.Retract();
    }
    else{
      _climberClass.StopClimber();
    }
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
  public void disabledPeriodic() {}

  int step = 0;
  @Override
  public void testInit() {
    _westCoastDrive.zeroEncoders();
    _turretClass.zeroEncoders();
    step = 0;
  }

  

  @Override
  public void testPeriodic() {
    
    System.out.println(_turretClass.getTicks());
  }
}
