package frc.robot;

import java.io.IOException;
import java.nio.file.Path;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;




public class Robot extends TimedRobot {
  
  CANSparkMax _rMotor;
  CANSparkMax _lMotor;
  DifferentialDrive _drive;
  Joystick _joy;
  WPI_PigeonIMU pidgey;
  AHRS ahrs;


  String trajectoryJSON = "paths/circle.wpilib.json";
  Trajectory trajectory = new Trajectory();

  @Override
  public void robotInit() {

    _rMotor = new CANSparkMax(20, MotorType.kBrushless);
    _lMotor = new CANSparkMax(10, MotorType.kBrushless);
    _drive = new DifferentialDrive(_lMotor, _rMotor);
    _joy = new Joystick(0);
    pidgey = new WPI_PigeonIMU(30);
    pidgey.calibrate();
    ahrs = new AHRS(SPI.Port.kMXP);
    ahrs.reset();
  }

  @Override
  public void robotPeriodic() {
    System.out.println(ahrs.getAngle());
  }
  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    ahrs.reset();
  }

  @Override
  public void teleopPeriodic() {
    if(_joy.getRawAxis(1) >= .2 || _joy.getRawAxis(1) <= -.2 ||
    _joy.getRawAxis(3) >= .2 || _joy.getRawAxis(3) <= -.2){
      _drive.tankDrive(-_joy.getRawAxis(1)/2, _joy.getRawAxis(3)/2);
    }else{
      _drive.tankDrive(0, 0);
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
