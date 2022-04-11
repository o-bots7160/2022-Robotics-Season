package frc.robot;

/*import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoMode.PixelFormat;*/
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
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
    CUSTOM_1, // Jaiden
    CUSTOM_2, // Quinn
    CUSTOM_3, // Sam
    CUSTOM_4, // Ashley
    CUSTOM_5, // Olivia
    CUSTOM_6, // Ethan
  }

  private AUTO autonTracker;

  private  enum LAUNCHAUTO {
    BALLPICKUP,
    TURN,
    SHOOT,
    STOP
  }
  private  enum CUSTOM_1 {
    FIRSTBALLPICKUP,
    FIRSTTURN,
    FIRSTSHOOT,
    SECONDTURN,
    DELAYONE,
    SECONDBALLPICKUP,
    THIRDMOVE,
    SECONDSHOOT,
    STOP
  }
  private  enum CUSTOM_2 {
    BALLPICKUP,
    TURN,
    SHOOT,
    STOP
  }
  private  enum CUSTOM_3 {
    BALLPICKUP,
    TURN,
    SHOOT,
    STOP
  }
  private  enum CUSTOM_4 {
    MOVE,
    STOP
  }
  private  enum CUSTOM_5 {
    BALLPICKUP,
    TURN,
    SHOOT,
    STOP
  }
  private  enum CUSTOM_6 {
    BALLPICKUP,
    TURN,
    SHOOT,
    STOP
  }
  private LAUNCHAUTO lA = LAUNCHAUTO.BALLPICKUP;
  private CUSTOM_1 C1 = CUSTOM_1.FIRSTBALLPICKUP;
  private CUSTOM_2 C2 = CUSTOM_2.BALLPICKUP;
  private CUSTOM_4 C4 = CUSTOM_4.MOVE;

  @Override
  public void robotInit() {
    //sets up auton options on the Smart Dashboard
    _chooser.setDefaultOption("LAUNCHAUTO", AUTO.LAUNCHAUTO);
    _chooser.addOption("Custom1", AUTO.CUSTOM_1);
    _chooser.addOption("Custom2", AUTO.CUSTOM_2);
    _chooser.addOption("Custom3", AUTO.CUSTOM_3);
    _chooser.addOption("Custom4", AUTO.CUSTOM_4);
    _chooser.addOption("Custom5", AUTO.CUSTOM_5);
    _chooser.addOption("Custom6", AUTO.CUSTOM_6);
    //UI.setBlue();
    /*Thread camera =
    new Thread(() ->{
      UsbCamera cameraServer = CameraServer.startAutomaticCapture(1);
      cameraServer.setVideoMode(PixelFormat.kMJPEG, 320, 240, 15);
    });
    camera.setDaemon(true);
    camera.start();*/

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
    endGameTimer.reset();
    endGameTimer.start();
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
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
      case CUSTOM_1:
        autonTracker = AUTO.CUSTOM_1;
        C1 = CUSTOM_1.FIRSTBALLPICKUP;
        break;
      case CUSTOM_2:
        autonTracker = AUTO.CUSTOM_2;
        C2 = CUSTOM_2.BALLPICKUP;
        break;
      case CUSTOM_3:
        break;
      case CUSTOM_4:
        autonTracker = AUTO.CUSTOM_4;
        C4 = CUSTOM_4.MOVE;
        break;
      case CUSTOM_5:
        break;
      case CUSTOM_6:
        break;
    }
  }

  @Override
  public void autonomousPeriodic() {

    switch(autonTracker){
      case LAUNCHAUTO:
        launchAuto();
        break;
      case CUSTOM_1:
        custom_1();
        break;
      case CUSTOM_4:
        custom_4();
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
          System.out.println("Is driving");
          
        }else{
          lA = LAUNCHAUTO.TURN;
        }
        break;

        case TURN:
        _turretClass.Update_Limelight_Tracking();
        if(_westCoastDrive.turnTo(94, 20)){
          _intakeClass.Collect();
        }else{
          timer.reset();
          timer.start();
          lA = LAUNCHAUTO.SHOOT;
        }
        break;

        case SHOOT:
        _turretClass.Update_Limelight_Tracking();
        _turretClass.Shoot();
        if(_turretClass.isReady()) {
          _intakeClass.Shoot();
        }else if (timer.hasElapsed( 10 )) {
          _turretClass.StopShooter();
          _intakeClass.Stop();
        }
        break;

        case STOP:
        break;
      }
  }

  private void custom_1 () {
    System.out.println("Case: " + C1.toString());

    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(C1){
        
      case FIRSTBALLPICKUP:
        _turretClass.SetHigh();
        _intakeClass.Collect();
        if(_westCoastDrive.moveTo(75, 9)){
          System.out.println("Is driving");
          
        }else{
          C1 = CUSTOM_1.FIRSTTURN;
        }
        break;

        case FIRSTTURN:
        _turretClass.Update_Limelight_Tracking();
        if(_westCoastDrive.turnTo(94, 20)){
          _intakeClass.Collect();
        }else{
          timer.reset();
          timer.start();
          C1 = CUSTOM_1.FIRSTSHOOT;
        }
        break;

        case FIRSTSHOOT:
        _turretClass.Update_Limelight_Tracking();
        _turretClass.Shoot();
        if(_turretClass.isReady()) {
          _intakeClass.Shoot();
        }else if (timer.hasElapsed( 1.7 )) {
          _turretClass.StopShooter();
          _intakeClass.Stop();
          C1 = CUSTOM_1.SECONDTURN;
        }
        break;

      case SECONDTURN:
      _turretClass.StopTurret();
      if(_westCoastDrive.turnTo(-85, 20)){
        _intakeClass.Collect();
      }else{
        timer.reset();
        timer.start();
        C1 = CUSTOM_1.DELAYONE;
        
      }
      break;

      case DELAYONE:
      if(timer.hasElapsed(2)){
        C1 = CUSTOM_1.SECONDBALLPICKUP;
      }
      break;

      case SECONDBALLPICKUP:
      _turretClass.SetHigh();
      _intakeClass.Collect();
      if(_westCoastDrive.moveTo(120, 40)){
        System.out.println("Is driving");
      }else {
        C1 = CUSTOM_1.STOP;
      }
      break;

      case THIRDMOVE:
      if(_westCoastDrive.moveTo(-130, 30)) {
        System.out.println("Is driving");
        _intakeClass.Collect();
      }else{
        timer.reset();
        timer.start();
        C1 = CUSTOM_1.SECONDSHOOT;
      }
      break;

      case SECONDSHOOT:
      if(_westCoastDrive.turnTo(115, 20)) {
        System.out.println("Is driving");
        _turretClass.AutonIdleSpeed();
      }else {
        _turretClass.Update_Limelight_Tracking();
        _turretClass.Shoot();
        if(_turretClass.isReady()) {
          _intakeClass.Shoot();
        }else if (timer.hasElapsed(10)) {
          _turretClass.StopShooter();
          _intakeClass.Stop();
          C1 = CUSTOM_1.STOP;
        }
      }
      break;

      case STOP:
      _westCoastDrive.stopDrive();
      break;
      }
  }
  
  private void custom_2 () {
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(lA){
        
        case BALLPICKUP:
        _intakeClass.Collect();
        if ( _westCoastDrive.moveTo(65, 8) ) {
          System.out.println("We gaming");
        } else {
          C2 = CUSTOM_2.TURN;
        }
        break;

        case TURN:
        break;

        case SHOOT:
        break;

        case STOP:
        break;
      }
  }
  private void custom_3 () {
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(lA){
        
        case BALLPICKUP:
        break;

        case TURN:
        break;

        case SHOOT:
        break;

        case STOP:
        break;
      }
  }
  private void custom_4 () {
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(C4){

        case MOVE:
        if ( _westCoastDrive.moveTo(70, 30) ) {
        }
        else{
          C4 = CUSTOM_4.STOP;
        }
        break;

        case STOP:
        break;
      }
  }
  private void custom_5 () {
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(lA){
        
        case BALLPICKUP:
        break;

        case TURN:
        break;

        case SHOOT:
        break;

        case STOP:
        break;
      }
  }
  private void custom_6 () {
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(lA){
        
        case BALLPICKUP:
        break;

        case TURN:
        break;

        case SHOOT:
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
    _turretClass.zeroEncoders(); // Comment at match
    _turretClass.softLimits(); // Comment at match
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
