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
  private final SendableChooser<Integer> _chooser = new SendableChooser<>();
  private final Climber _climberClass             = new Climber();


  //creates options for Smart Dashboard
  private final Integer PositionOne   = 1;
  private final Integer PositionTwo   = 2;
  private final Integer PositionThree = 3;

  // Camera Thread
  Thread m_visionThread;


  @Override
  public void robotInit() {
    //sets up auton options on the Smart Dashboard
    _chooser.setDefaultOption("Position 1", PositionOne);
    _chooser.addOption("Position 2", PositionTwo);
    _chooser.addOption("Position 3", PositionThree);
    m_visionThread =
        new Thread(
            () -> {
              // Get the UsbCamera from CameraServer
              UsbCamera camera = CameraServer.startAutomaticCapture();
              // Set the resolution
              camera.setResolution(320, 240);
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
    SmartDashboard.putNumber("Position", _chooser.getSelected());
    SmartDashboard.putNumber("Step", step);
    _westCoastDrive.robotPeriodic();   
  }

  @Override
  public void autonomousInit() {
    _westCoastDrive.autonomousInit();
  }

  @Override
  public void autonomousPeriodic() {

    switch ( _chooser.getSelected() ){
      case 1:
        auton1();
        break;
      case 2: 
        break;
      case 3:
        break;
    }
  }

  private int step = 0; 
  private void auton1 () {

      // move to cargo 2 with intake on
      if (step == 0) {
        _westCoastDrive.zeroEncoders();
        step++;
      } else if (step == 1) {
        //_intakeClass.Collect();
        if (_westCoastDrive.turnTo( 144.0 )) {
          step++;
        }
      } else if (step == 2) {
        _intakeClass.Collect();
        if (_westCoastDrive.moveTo( 30 )) {
            step++;
        }
      }
      
      // shoot two balls
      //_westCoastDrive.turnTo( 1000 );
     /* _turretClass.Shoot();
      if ( !_turretClass.isReady()){}
      if ( true ){
        _intakeClass.Shoot();
      } 
      */
      //delay???
      // turn to face third ball
      // move to cargo 3 with intake on
      // shoot one ball 
  }
  
  @Override
  public void teleopInit() {
    _climberClass.reset();
  }

  @Override
  public void teleopPeriodic() {

    if(UI.getTurretLeft()){
      _turretClass.TurnLeft();
    }else if(UI.getTurretRight()){
      _turretClass.TurnRight();
    }else{
      _turretClass.StopTurret();
    }

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

    /*if (_buttons2.getRawButton(8)){
      _turretClass.AutoAim();
    }*/
    

  if(UI.getClimbExtend()) {
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
  }
}

  @Override
  public void disabledInit() {
    step = 0;
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

