// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Joystick;
/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  TalonFX motorLF = new TalonFX(10);
  TalonFX motorLR = new TalonFX(11);
  TalonFX motorRF = new TalonFX(12);
  TalonFX motorRR = new TalonFX(13);
  Joystick Joystick = new Joystick(0); 
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}
  private void Drive (double x, double y) {
    double drive_left  = y - x * 0.5;
    double drive_right = y + x * 0.5;
    motorLF.set(ControlMode.PercentOutput, drive_left );
    motorLR.set(ControlMode.PercentOutput, drive_left );
   
    motorRF.set(ControlMode.PercentOutput, drive_right);
    motorRR.set(ControlMode.PercentOutput, drive_right);
  }
  @Override
  public void teleopPeriodic() {
    Drive( Joystick.getX(), Joystick.getY());
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
