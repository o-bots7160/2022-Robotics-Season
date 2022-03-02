package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

  private final WestCoastDrive _westCoastDrive    = new WestCoastDrive(); 
  private final Intake _intakeClass               = new Intake();
  private final Turret _turretClass               = new Turret();
  private final SendableChooser<AUTO> _chooser    = new SendableChooser<>();
  private final Climber _climberClass             = new Climber();

  private enum AUTO {
    LAUNCHAUTO
  }

  private AUTO autonTracker;

  private  enum LAUNCHAUTO {
    BALLPICKUP,
    TURN,
    SHOOT
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
<<<<<<< Updated upstream
    _westCoastDrive.robotPeriodic();




=======
    SmartDashboard.putData(_chooser);
    _westCoastDrive.robotPeriodic();  
>>>>>>> Stashed changes
  }

  @Override
  public void autonomousInit() {
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
    _intakeClass.Collect();
      switch(lA){
        
        case BALLPICKUP:
        _turretClass.SetHigh();
        if(_westCoastDrive.moveTo(56, 12)){
          System.out.println("Is driving");
          
        }else{
          lA = LAUNCHAUTO.TURN;
        }
        _turretClass.isReady();
        break;

        case TURN:
        if(_westCoastDrive.turnTo(95, 35)){
          _intakeClass.Collect();
        }else{
          lA = LAUNCHAUTO.SHOOT;
        }
        _turretClass.isReady();
        break;

        case SHOOT:
        _turretClass.Update_Limelight_Tracking();
        _turretClass.Shoot();
        _intakeClass.feedUp();
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

