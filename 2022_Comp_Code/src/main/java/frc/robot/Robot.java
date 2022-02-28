package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;

public class Robot extends TimedRobot {

  private final WestCoastDrive _westCoastDrive    = new WestCoastDrive(); 
  private final Intake _intakeClass               = new Intake();
  private final Turret _turretClass               = new Turret();
  private final SendableChooser<AUTO> _chooser = new SendableChooser<>();
  private final Climber _climberClass             = new Climber();

  //creates options for Smart Dashboard
  private final Integer PositionOne   = 1;
  private final Integer PositionTwo   = 2;
  private final Integer PositionThree = 3;

  // Camera Thread
  Thread m_visionThread;

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
  }

  @Override
  public void robotPeriodic() {
    _intakeClass.execute();
    _turretClass.execute();
    //_climberClass.execute();
    //puts options and result on Smart Dashboard
    SmartDashboard.putData(_chooser);
    _westCoastDrive.robotPeriodic();



    
  }

  @Override
  public void autonomousInit() {
    _westCoastDrive.autonomousInit();
    _westCoastDrive.zeroEncoders();
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
      switch(lA){
        case BALLPICKUP:
        if(_westCoastDrive.moveTo(120, 18)){
          System.out.println("Is driving");
        }else{
          lA = LAUNCHAUTO.TURN;
        }
        break;

        case TURN:

        break;
      }
  }
  
  @Override
  public void teleopInit() {
    _climberClass.reset();
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
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {
    _westCoastDrive.zeroEncoders();
  }

  @Override
  public void testPeriodic() {
    System.out.println(_westCoastDrive.getTicks());

  }
}
