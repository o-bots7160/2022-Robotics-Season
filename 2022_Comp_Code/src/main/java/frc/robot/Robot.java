package frc.robot;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoMode.PixelFormat;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class Robot extends TimedRobot {

  private final WestCoastDrive _westCoastDrive    = new WestCoastDrive(); 
  private final Intake _intakeClass               = new Intake();
  private final Turret _turretClass               = new Turret();
  private final SendableChooser<AUTO> _chooser    = new SendableChooser<>();
  private final Climber _climberClass             = new Climber();
  private final Timer   timer                     = new Timer();
  private final Spark _LED                        = new Spark(1);
  private final Timer   endGameTimer              = new Timer(); 

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
    SECONDBALLPICKUP,
    THIRDTURN,
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
    BALLPICKUP,
    TURN,
    SHOOT,
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

  @Override
  public void robotInit() {
    //sets up auton options on the Smart Dashboard
    _chooser.setDefaultOption("LAUNCHAUTO", AUTO.LAUNCHAUTO);
    //_chooser.addOption(name, object);
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
    _westCoastDrive.zeroEncoders();
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
      break;
      case CUSTOM_3:
      break;
      case CUSTOM_4:
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
    }
    switch(autonTracker){
      case CUSTOM_1:
      custom_1();
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
        if(_westCoastDrive.moveTo(65, 9)){
          System.out.println("Is driving");
          
        }else{
          lA = LAUNCHAUTO.TURN;
        }
        break;

        case TURN:
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
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0); 
    switch(C1){
        
      case FIRSTBALLPICKUP:
      _turretClass.SetHigh();
      _intakeClass.Collect();
      if(_westCoastDrive.moveTo(65, 9)){
        System.out.println("Is driving");
        
      }else{
        C1 = CUSTOM_1.FIRSTTURN;
      }
      break;

      case FIRSTTURN:
      if(_westCoastDrive.turnTo(95, 20)){
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
      }else if (timer.hasElapsed( 10 )) {
        _turretClass.StopShooter();
        _intakeClass.Stop();
        C1 = CUSTOM_1.SECONDTURN;
      }
      break;

      case SECONDTURN:
      if(_westCoastDrive.turnTo(-65, 20)){
        _intakeClass.Collect();
        C1 = CUSTOM_1.SECONDBALLPICKUP;
      }
      break;

      case SECONDBALLPICKUP:
      _turretClass.SetHigh();
      _intakeClass.Collect();
      if(_westCoastDrive.moveTo(130, 9)){
        System.out.println("Is driving");
      }else if(_intakeClass.AutonSecondDrive()) {
        C1 = CUSTOM_1.THIRDTURN;
      }
      break;

      case THIRDTURN:
      if(_westCoastDrive.turnTo(180, 20)) {
        _intakeClass.Collect();
        _turretClass.AutonCenterTurret();
      }else{
        timer.reset();
        timer.start();
        C1 = CUSTOM_1.SECONDSHOOT;
      }
      break;

      case SECONDSHOOT:
      if(_westCoastDrive.moveTo(130, 9)) {
        System.out.println("Is driving");
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
      break;
      }
  }
  
  private void custom_2 () {
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
    _intakeClass.breakMode();
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
        //_turretClass.IdleSpeed();
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
      if (_turretClass.isReady())
      {
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
    else if(UI.getFlushLow())
    {
      _intakeClass.intakeFlush();
    }
    else
    {
        _intakeClass.Stop();
        if (_intakeClass.haveBallHigh()) {
          //_turretClass.IdleSpeed();
        } else {
          _turretClass.StopShooter();
        }
    }

    if(UI.getAutoAim()){
      _turretClass.breakMode();
      if(!UI.getSafeZone()){ // Yes I know backwards
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(1); 
      }else {
       NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);
      }
      if(UI.getShoot()){
        _turretClass.Update_Limelight_Tracking();
      }else{
        _turretClass.manualControl();
      }
    }else{
      _turretClass.breakMode();
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(3); 
      _turretClass.manualControl();
    
	}

  if(UI.getClimbExtend()){
    _climberClass.Extend();
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
  else if(UI.getClimbPush()){
    _climberClass.Push();
  }
  else{
    _climberClass.StopTilt();
  }
}

  @Override
  public void disabledInit() {
    _westCoastDrive.setCoastMode();
    _climberClass.setCoastMode();
    _turretClass.setCoast();
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
