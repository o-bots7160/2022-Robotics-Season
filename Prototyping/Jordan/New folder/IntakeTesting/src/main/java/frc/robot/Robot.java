// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;


public class Robot extends TimedRobot {
  Joystick manip = new Joystick(0);
  VictorSPX intake = new VictorSPX(50);
  WestCoastDrive drive = new WestCoastDrive( manip );
  @Override
  public void robotInit() {}

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {

    if(manip.getRawButton(InputMap.ENGAGE_INTAKE)){
      intake.set(ControlMode.PercentOutput, .5);
    }else if(manip.getRawButton(InputMap.DISENGAGE_INTAKE)){
      intake.set(ControlMode.PercentOutput, -.2);
    }else{
      intake.set(ControlMode.PercentOutput, 0);
    }
    drive.arcadeDrive( -manip.getRawAxis(InputMap.DRIVEJOY_Y),
                      manip.getRawAxis(InputMap.DRIVEJOY_Z));
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
