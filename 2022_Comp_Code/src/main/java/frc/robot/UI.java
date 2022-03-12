package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class UI {

    private static final Joystick _joystick        = new Joystick(0);
    private static final Joystick _buttons1        = new Joystick(1);
    private static final Joystick _buttons2        = new Joystick(2);
    //

    //SPEED REDUCER
    private static double speedReducerY = 1.25;
    private static double speedReducerZ = 1.75; 

    public static double yInput(){
        if(_joystick.getRawAxis(1) >=.2 || _joystick.getRawAxis(1) <= -.2){
          return -(_joystick.getY() / speedReducerY);
        }else{
          return 0;
        }
    }

    public static double zInput(){
        if(_joystick.getRawAxis(2) >=.1 || _joystick.getRawAxis(2) <= -.1){
          return _joystick.getZ() / speedReducerZ;
        }else{
          return 0;
        }
    }

    public static boolean getFlushHigh(){
        return _buttons1.getRawButton( 2 );
    }

    public static boolean getFlushLow(){
        return _buttons1.getRawButton( 3 );
    }

    public static boolean getIntake(){
        //slows turning speed down when intaking
        if (_joystick.getRawButton(1)){  
            speedReducerZ = 2.0;               //TODO test for number
        }else{
            speedReducerZ = 1.75;              //TODO test for number
        }
       return  _joystick.getRawButton(1);
    }

    public static boolean getAutoAim(){
        return _buttons1.getRawButton( 4 );
    }

    public static boolean getTurretLeft(){
        return _buttons2.getRawButton( 10 );
    }

    public static boolean getTurretRight(){
        return _buttons1.getRawButton( 10 );
    }

    public static boolean getShoot(){
        return _buttons2.getRawButton( 11 );
    }

    public static boolean getShooterLow(){
        return !_buttons2.getRawButton( 7 );
    }

    public static boolean getClimbExtend(){
        return _buttons2.getRawButton( 9 );
    }

    public static boolean getClimbRetract(){
        return _buttons2.getRawButton( 8 );
    }

    public static boolean getClimbPush(){
        return _buttons1.getRawButton( 12 );
    }

    public static boolean getClimbPull(){
        return _buttons1.getRawButton( 11 );
    }
    
    public static boolean getSafeZone(){
        return _buttons2.getRawButton( 6 );
    }
    public static boolean getIgnoreLimits() {
        return _joystick.getRawButton( 7);
    }
}
