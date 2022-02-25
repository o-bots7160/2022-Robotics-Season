package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.hal.simulation.DriverStationDataJNI;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.DriverStation;

public class Robot extends TimedRobot {

  private final WestCoastDrive _westCoastDrive    = new WestCoastDrive(); 
  private final Intake _intakeClass               = new Intake();
  private final Turret _turretClass               = new Turret();
  private final Joystick _joystick                = new Joystick(0);
  private final Joystick _buttons1                = new Joystick(1);
  private final Joystick _buttons2                = new Joystick(2);
  private final SendableChooser<Integer> _chooser = new SendableChooser<>();
  private final Climber _climberClass             = new Climber();


  private double speedReducerY = 2.0;
  private double speedReducerZ = 2.5; 

  //creates options for Smart Dashboard
  private final Integer PositionOne   = 1;
  private final Integer PositionTwo   = 2;
  private final Integer PositionThree = 3;


 

  @Override
  public void robotInit() {
    //sets up auton options on the Smart Dashboard
    _chooser.setDefaultOption("Position 1", PositionOne);
    _chooser.addOption("Position 2", PositionTwo);
    _chooser.addOption("Position 3", PositionThree);
  }

  @Override
  public void robotPeriodic() {
    _intakeClass.execute();
    _turretClass.execute();
    _climberClass.execute();
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

  private double yInput(){
    if(_joystick.getRawAxis(1) >=.2 || _joystick.getRawAxis(1) <= -.2){
      return _joystick.getY() / speedReducerY;
    }else{
      return 0;
    }
  }

  private double zInput(){
    if(_joystick.getRawAxis(2) >=.1 || _joystick.getRawAxis(2) <= -.1){
      return _joystick.getZ() / speedReducerZ;
    }else{
      return 0;
    }
  }
  
  @Override
  public void teleopInit() {
    _climberClass.reset();
  }

  @Override
  public void teleopPeriodic() {

    /*if(_buttons1.getRawButton(12)){
      _turretClass.TurnLeft();
    }else if(_buttons1.getRawButton(10)){
      _turretClass.TurnRight();
    }else{
      _turretClass.StopTurret();
    }*/

    _westCoastDrive.arcadeDrive(yInput(), zInput()); 
    if(_buttons1.getRawButton(7) || _joystick.getRawButton(1))
    {
      _intakeClass.Collect();
    }
    else if(_buttons1.getRawButton(11))
    { 
      if (_buttons2.getRawButton(7)) {
        //_turretClass.SetLow();
      } else {
        //_turretClass.SetHigh();
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
    else if(_buttons1.getRawButton(9))
    {
      _intakeClass.Flush();
    }
    else if(_buttons1.getRawButton(8) )
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
    
  //   if(_ButtonBoard.getRawButton(5)) {
  //     _climberClass.Extend();
  //   }else if(_ButtonBoard.getRawButton(2)) {
  //     _climberClass.Retract();
  //   }else {
  //     _climberClass.StopClimber();
  //   }

  //   if(_ButtonBoard.getRawButton(1)) {
  //     _climberClass.Push();
  //   }else if(_ButtonBoard.getRawButton(3)) {
  //     _climberClass.Pull();
  //   }else {
  //     _climberClass.StopTilt();
  //   }
  // }

  if(_buttons2.getRawButton(12)) {
    _climberClass.Extend();
  }else if(_buttons2.getRawButton(11)) {
    _climberClass.Retract();
  }else {
    _climberClass.StopClimber();
  }

  if(_buttons2.getRawButton(10)) {
    _climberClass.Push();
  }else if(_buttons2.getRawButton(9)) {
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
