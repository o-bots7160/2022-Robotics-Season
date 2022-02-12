package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.DriverStation;

public class Robot extends TimedRobot {

  private final WestCoastDrive _westCoastDrive    = new WestCoastDrive(); 
  private final Intake _intakeClass               = new Intake();
  private final Turret _turretClass               = new Turret();
  private final Joystick _joystick                = new Joystick(0);
  private final SendableChooser<Integer> _chooser = new SendableChooser<>();

  private double speedReducerY = 2.5;
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
    //puts options and result on Smart Dashboard
    SmartDashboard.putData(_chooser);
    SmartDashboard.putNumber("Position", _chooser.getSelected());
  }

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

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
  }

  @Override
  public void teleopPeriodic() {

    if(_joystick.getRawButton(5)){
      _turretClass.TurnLeft();
    }else if(_joystick.getRawButton(6)){
      _turretClass.TurnRight();
    }else{
      _turretClass.StopTurret();
    }

    _westCoastDrive.arcadeDrive(yInput(), zInput()); 
    if(_joystick.getRawButton(1))
    {
      _intakeClass.Collect();
    }
    else if(_joystick.getRawButton(2))
    {
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
    else if(_joystick.getRawButton(4))
    {
      _intakeClass.Flush();
    }
    else if(_joystick.getRawButton(3) )
    {
        _intakeClass.intakeFlush();
      }
      else if(_joystick.getRawButton(7)){
        _turretClass.ShooterOn();
      }
    else 
    {
      _intakeClass.Stop();
      _turretClass.StopShooter();

    }
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
