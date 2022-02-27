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
    m_visionThread =
        new Thread(
            () -> {
              // Get the UsbCamera from CameraServer
              UsbCamera camera = CameraServer.startAutomaticCapture();
              // Set the resolution
              camera.setResolution(640, 480);
            });
    m_visionThread.setDaemon(true);
    m_visionThread.start();
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
    switch ( _chooser.getSelected() ){
      case LAUNCHAUTO:
        autonTracker = AUTO.LAUNCHAUTO;
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
    
  }

  private void launchAuto () {
      
      
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
    if(UI.getTurretLeft()) {
      _turretClass.TurnLeft();
    }
    else if(UI.getTurretRight()) {
      _turretClass.TurnRight();
    }else{
      _turretClass.StopTurret();
    }


    /*if (_buttons2.getRawButton(8)){
      _turretClass.AutoAim();
    }*/
    

  /*if(UI.getClimbExtend()) {
    _climberClass.Extend();
  }else if(UI.getClimbRetract()) {
    _climberClass.Retract();
  }else {
    _climberClass.StopClimber();
  }

  if(UI.getClimbPush()) {
    _climberClass.Push();
  }else if(UI.getClimbPull()) {
    _climberClass.Pull();
  }else {
    _climberClass.StopTilt();
  }*/

}

  @Override
  public void disabledInit() {
    _westCoastDrive.setCoastMode();
    _climberClass.setCoastMode();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
