package frc.robot;


public class Robot extends TimedRobot {

  @Override
  public void robotInit() {
    led = new Spark(9);
  }

  @Override
  public void robotPeriodic() {
    led.set(0.61);
  }

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {}

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}
