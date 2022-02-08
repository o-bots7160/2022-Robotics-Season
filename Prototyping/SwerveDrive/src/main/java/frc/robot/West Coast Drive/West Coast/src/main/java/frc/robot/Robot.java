package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.DriverStation;

public class Robot extends TimedRobot {

  private final WestCoastDrive _westCoastDrive = new WestCoastDrive(); 
  private final Intake _intakeClass            = new Intake();
  private final Turret _turretClass            = new Turret();
  private final Joystick _joystick             = new Joystick(0);

  private double speedReducerY = 2.5;
  private double speedReducerZ = 2; 

  @Override
  public void robotInit() {}

  @Override
  public void robotPeriodic() {
    _intakeClass.execute();
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
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {

    if(_joystick.getRawButton(5)){
      _turretClass.TurnLeft();
    }else if(_joystick.getRawButton(6)){
      _turretClass.TurnRight();
    }else{
      _turretClass.StopTurret();
    }

    _westCoastDrive.arcadeDrive(zInput(), yInput()); 
    if(_joystick.getRawButton(1)){
      _intakeClass.Collect();
    //_turretClass.Shoot();
    }else{
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
