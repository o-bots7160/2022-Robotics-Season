package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class Robot extends TimedRobot {

  private final WestCoastDrive _westCoastDrive    = new WestCoastDrive(); 
  private final IntakeAssembly _intakeClass       = new IntakeAssembly();
  private final TurretAssembly _turretClass       = new TurretAssembly();
  private final SendableChooser<AUTO> _chooser    = new SendableChooser<>();
  private final Climber _climberClass             = new Climber();
  private final Timer   timer                     = new Timer();
  private final Spark _LED                        = new Spark(1);
  private final Timer   endGameTimer              = new Timer();
  private       Boolean shooting                  = false; 

  private enum AUTO {
    LAUNCHAUTO,
    TERMINALAUTO, // Jaiden
    TOWARDSWALL, // Quinn
  }

  private AUTO autonTracker;

  private  enum LAUNCHAUTO {
    BALLPICKUP,
    DRIVEBACK,
    TURN,
    SHOOT,
    STOP
  }
  private  enum TERMINALAUTO {
    FIRSTBALLPICKUP,
    FIRSTTURN,
    FIRSTSHOOT,
    SECONDTURN,
    DELAYONE,
    SECONDBALLPICKUP,
    WAITFORHUMAN,
    THIRDMOVE,
    SECONDSHOOT,
    STOP
  }
  private  enum TOWARDSWALL {
    WALLBALLPICKUP,
    TURNTOTERMINAL,
    SHOOT,
    TOTERMINAL,
    WAITFORHUMAN,
    TURNTOHUB,
    TOHUB,
    STOP
  }

  
  private LAUNCHAUTO lA = LAUNCHAUTO.BALLPICKUP;
  private TERMINALAUTO tA = TERMINALAUTO.FIRSTBALLPICKUP;
  private TOWARDSWALL tw = TOWARDSWALL.WALLBALLPICKUP;

  @Override
  public void robotInit() {
    
    //sets up auton options on the Smart Dashboard
    _chooser.setDefaultOption("LAUNCHAUTO", AUTO.LAUNCHAUTO);
    _chooser.addOption("TERMINALAUTO", AUTO.TERMINALAUTO);
    //_chooser.addOption("TOWARDSWALL", AUTO.TOWARDSWALL);

  }

  @Override
  public void robotPeriodic() {
    _intakeClass.execute();
    _turretClass.execute();
    //_climberClass.execute();
    //puts options and result on Smart Dashboard
    _westCoastDrive.robotPeriodic();

    SmartDashboard.putData(_chooser);
    _westCoastDrive.robotPeriodic();  
  }

  @Override
  public void autonomousInit() {
    _turretClass.enableSoftLimits();
    endGameTimer.reset();
    endGameTimer.start();
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
    _turretClass.zeroEncoders();
    _westCoastDrive.autonomousInit();
    _westCoastDrive.zeroSensors();
    _intakeClass.ZeroEncoders();
    //UI.setBlue();
    _LED.set(-.95);
    switch ( _chooser.getSelected() ){
      case LAUNCHAUTO:
        autonTracker = AUTO.LAUNCHAUTO;
        lA = LAUNCHAUTO.BALLPICKUP;
        break;
      case TERMINALAUTO:
        autonTracker = AUTO.TERMINALAUTO;
        tA = TERMINALAUTO.FIRSTBALLPICKUP;
        break;
      case TOWARDSWALL:
        autonTracker = AUTO.TOWARDSWALL;
        tw = TOWARDSWALL.WALLBALLPICKUP;
        break;
    }
  }

  @Override
  public void autonomousPeriodic() {

    switch(autonTracker){
      case LAUNCHAUTO:
        launchAuto();
        break;
      case TERMINALAUTO:
        terminalAuto();
        break;
      case TOWARDSWALL:
        towardsWall();
        break;
    }
    _westCoastDrive.setBrakeMode();
    
  }

  private void launchAuto () {
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(lA){
        
        case BALLPICKUP:
        _turretClass.SetHigh();
        _intakeClass.Collect();
        if(_westCoastDrive.moveTo(74, 9)){
         // System.out.println("Ball Pick up");
          
        }else{
          //System.out.println("Reset!");
          _westCoastDrive.resetGyro();
          timer.reset();
          timer.start();
          lA = LAUNCHAUTO.DRIVEBACK;
        }
        break;

        case DRIVEBACK:
        if(_westCoastDrive.moveTo(-12, 5)){
          }else{
            timer.reset();
            timer.start();
            lA = LAUNCHAUTO.TURN;
          }
        break;

        case TURN:
          _turretClass.Update_Limelight_Tracking();
          if(_westCoastDrive.turnTo(90, timer)){
            //System.out.println("Turning");
            _intakeClass.Collect();
          }else{
            timer.reset();
            timer.start();
            _westCoastDrive.resetGyro();
            lA = LAUNCHAUTO.SHOOT;
          }
        break;

        case SHOOT:
        _turretClass.Update_Limelight_Tracking();
        //if(timer.hasElapsed(3)){
          _turretClass.Shoot();
          if(_turretClass.isReady()) {
            _intakeClass.Shoot();
          }else if (timer.hasElapsed( 10 )) {
            _turretClass.StopShooter();
            _intakeClass.Stop();
          }
      //}
        break;

        case STOP:
        break;
      }
  }

  private void terminalAuto () {
    System.out.println("Case: " + tA.toString());

    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(tA){
        
      case FIRSTBALLPICKUP:
        _turretClass.shootAtX();
        _intakeClass.Collect();
        if(_westCoastDrive.moveTo(75, 9)){
          System.out.println("Is driving");
          
        }else{
          _westCoastDrive.resetGyro();
          timer.reset();
          timer.start();
          tA = TERMINALAUTO.FIRSTTURN;
        }
        break;

        case FIRSTTURN:
        _turretClass.Update_Limelight_Tracking();
        if(_westCoastDrive.turnTo(90, timer)){
          //System.out.println("Turning");
          _intakeClass.Collect();
        }else{
          timer.reset();
          timer.start();
          _westCoastDrive.resetGyro();
          tA = TERMINALAUTO.FIRSTSHOOT;
        }
        break;

        case FIRSTSHOOT:
        _turretClass.Update_Limelight_Tracking();
        _turretClass.Shoot();
        if(_turretClass.isReady()) {
          _intakeClass.Shoot();
        }
        if (timer.hasElapsed( 2.5 )) {
          _turretClass.StopShooter();
          _intakeClass.Stop();
          _westCoastDrive.resetGyro();
          timer.reset();
          timer.start();
          tA = TERMINALAUTO.SECONDTURN;
        }
        break; 

      case SECONDTURN:
      _turretClass.TurnLeft();
      _turretClass.AutonIdleSpeed();
      if(_westCoastDrive.turnTo(-75, timer)){ //TODO adjust this angle
        //System.out.println("Turning");
        _intakeClass.Collect();
      }else{
        timer.reset();

        timer.start();
        _westCoastDrive.resetGyro();
        _westCoastDrive.zeroSensors();
        tA = TERMINALAUTO.SECONDBALLPICKUP;
        
      }
      break;

      case SECONDBALLPICKUP:
      _turretClass.TurnLeft();
      _turretClass.AutonIdleSpeed();
      _intakeClass.Collect();
      if(_westCoastDrive.moveTo(126, 20)){
        System.out.println("Is driving");
      }else {
        timer.reset();
        timer.start();
        tA = TERMINALAUTO.WAITFORHUMAN;
      }
      break;

      case WAITFORHUMAN:
      _turretClass.TurnLeft();
      if(timer.hasElapsed(1)){
        timer.reset();
        timer.start();
        tA = TERMINALAUTO.THIRDMOVE;
      }else{
        _intakeClass.waitForHuman();
      }
      break;

      case THIRDMOVE:
      _turretClass.TurnLeft();
      if(_westCoastDrive.moveTo(-126, 20)) {
        System.out.println("Is driving");
        _intakeClass.waitForHuman();
      }else{
        timer.reset();
        timer.start();
        tA = TERMINALAUTO.SECONDSHOOT;
      }
      break;

      case SECONDSHOOT:
      _turretClass.Update_Limelight_Tracking();
      if(_westCoastDrive.turnTo(-110, timer)) { //TODO adjust this angle
        System.out.println("Is driving");
        _turretClass.SetHigh();
      }else {
        _turretClass.Shoot();
        if(_turretClass.isReady()) {
          _intakeClass.Shoot();
        }else if (timer.hasElapsed(10)) {
          _turretClass.StopShooter();
          _intakeClass.Stop();
          tA = TERMINALAUTO.STOP;
        }
      }
      break;

      case STOP:
      _westCoastDrive.stopDrive();
      break;
      }
  }
  
  private void towardsWall () {
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(tw){

      case WALLBALLPICKUP:
        if(_westCoastDrive.moveTo(40.5,10)) {
          _turretClass.SetHigh();
          _intakeClass.Collect();
        } else {
          _westCoastDrive.setBrakeMode();
          tw = TOWARDSWALL.TOHUB;
        }
        break;
        
      case TURNTOTERMINAL:
        if(_westCoastDrive.turnTo(115, timer)) {
          _intakeClass.Collect();
        } else {
          tw = TOWARDSWALL.SHOOT;
          timer.reset();
          timer.start();
        }
        break;

      case SHOOT:
      _turretClass.Update_Limelight_Tracking();
      _turretClass.Shoot();
      _intakeClass.Shoot();
      if (timer.hasElapsed( 5 )) {
        _turretClass.StopShooter();
        _intakeClass.Stop();
        _westCoastDrive.zeroSensors();
        tw = TOWARDSWALL.TOTERMINAL;
      }
      break;

      case TOTERMINAL:
      if(_westCoastDrive.moveTo(50,20)) {
        _intakeClass.Collect();
        _turretClass.SetHigh();
      } else {
        timer.reset();
        timer.start();
        tw = TOWARDSWALL.WAITFORHUMAN;
      }
      break;

      case WAITFORHUMAN:
      if(timer.hasElapsed(10)) {
        _westCoastDrive.setBrakeMode();
        tw = TOWARDSWALL.TURNTOHUB;
      }

      case TURNTOHUB:
      if(_westCoastDrive.turnTo(155, timer)) {
       // set the turret position
      } else {
        timer.reset();
        timer.start();
        _westCoastDrive.zeroSensors();
        tw = TOWARDSWALL.TOHUB;
      }
      break;

      case TOHUB:
      _turretClass.Update_Limelight_Tracking();
      if(_westCoastDrive.moveTo(50, 25)) {
        _turretClass.SetHigh();
        _intakeClass.Collect();
      } else if (!timer.hasElapsed(10)) {
        _turretClass.Shoot();
      } else {
        tw = TOWARDSWALL.STOP;
      }
      break;

      case STOP:
      break;
    }


}
  
  @Override
  public void teleopInit() {
    _climberClass.reset();
    endGameTimer.reset();
    endGameTimer.start();
    //  _turretClass.zeroEncoders(); // TODO Comment at match
    //_turretClass.softLimits(); // TODO Comment at match
    _turretClass.breakMode(); // Move this to Auton init at comp
    _intakeClass.setCoastMode();
    _intakeClass.ZeroEncoders();
  }

  @Override
  public void teleopPeriodic() {

    UI.getSpeedChange(); // Checks to see if the speed change buttons were pressed


    if(endGameTimer.get() > 85.0d){
      _LED.set(-.25);
      } else if(_intakeClass.haveBallHigh() && !_intakeClass.haveBallLow()){
      _LED.set(.57);
      } else if (_intakeClass.haveBallHigh() && _intakeClass.haveBallLow()){
      _LED.set(.75);
    }else{
      _LED.set(-.43);
    }
 
    _westCoastDrive.arcadeDrive(UI.yInput(), UI.zInput()); 
    
    if(UI.getIntake())
    {
      _intakeClass.Collect();
      if (_intakeClass.haveBallHigh()) {
        _turretClass.IdleSpeed();
      } else {
        _turretClass.StopShooter();
      }
    }
    else if(UI.getShoot())
    { 
      if (UI.getShooterLow()) {
        _turretClass.SetLow();
      } else {
        if (!UI.getSafeZone()){ // Yes I know backwards
          //NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(1); 
          _turretClass.SetHigh();
        }else {
         //NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
          _turretClass.shootAtX();
        }
      }
      _turretClass.Shoot();
      if (_turretClass.isReady()|| shooting)
      {
        shooting = true;
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
    else
    {
      shooting = false;
      _intakeClass.Stop();
      if (_intakeClass.haveBallHigh()) {
        //_turretClass.IdleSpeed();
      } else {
        _turretClass.StopShooter();
      }
    }

    if(UI.getAutoAim() && !(UI.getTurretLeft() || UI.getTurretRight())){
      _turretClass.breakMode();
      if(!UI.getSafeZone()){ // Yes I know backwards
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(1); 
      }else {
       NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
      }
        _turretClass.Update_Limelight_Tracking();
      
    }else{
      _turretClass.breakMode();
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(3); 
      _turretClass.manualControl();
  	}

  if(UI.getClimbExtend()){
    _climberClass.Extend();
    _intakeClass.StowIntake();
  }
  else if(UI.getClimbRetract()){
    _climberClass.Retract( UI.getIgnoreLimits());
  }
  else{
    _climberClass.StopClimber();
  }

  if(UI.getClimbPull()){
    _climberClass.Pull();
  }
  /*else if(UI.getClimbPush()){
    _climberClass.Push();
  }*/
  else{
    _climberClass.StopTilt();
  }


  if(UI.resetLimits()){
    _turretClass.disableSoftLimits();
  }else{
    _turretClass.enableSoftLimits();
  }

}

  @Override
  public void disabledInit() {
    _westCoastDrive.setCoastMode();
    _climberClass.setCoastMode();
    _turretClass.setCoastMode();
    _intakeClass.setCoastMode();
    _intakeClass.StowIntake();
  }

  @Override
  public void disabledPeriodic() {
    _turretClass.disabledInit();
  }

  int step = 0;
  @Override
  public void testInit() {
  } 

  @Override
  public void testPeriodic() {
  }
}
