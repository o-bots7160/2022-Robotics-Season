package frc.robot;


import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.math.controller.PIDController;

class _turret{

    //Shooter Motor Controllers
    private WPI_TalonFX _shotMain = new WPI_TalonFX(41);
    private WPI_TalonFX _turret = new WPI_TalonFX(40);

    private final double kP = 0.0035;
    private final double kI = 0.000000;
    private final double kD = 0;
    private PIDController RPMPID = new PIDController(kP, kI, kD);

    boolean controlling = true;

       private boolean limeControl = false;

    public void BallShooter(Timer autonTimer, Joystick MINIPJOY_1, Joystick MINIPJOY_2, Joystick DRIVEJOY){
        config.enable = true;
        config.triggerThresholdCurrent = 35.0d;
        config.triggerThresholdTime = 0.10d;


        this.autonTimer = autonTimer;
        this.DRIVEJOY = DRIVEJOY;
        this.MINIPJOY_1 = MINIPJOY_1;
        this.MINIPJOY_2 = MINIPJOY_2; 
        final int kTimeoutMs = 30;
        _shotMain.configFactoryDefault();
        _shotMain.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor ,0,kTimeoutMs);
        RPMPID.setTolerance(5.0d);
        _shotMain.setInverted( true  );
        _shotMain.configGetSupplyCurrentLimit(config);
   }

}