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

    public static boolean getIntake(){
       return  _joystick.getRawButton(1);
    }

    public static boolean getAutoAim(){
        return _buttons1.getRawButton( 3 );
    }

    public static boolean getTurretLeft(){
        return _buttons2.getRawButton( 4 );
    }

    public static boolean getTurretRight(){
        return _buttons2.getRawButton( 2 ); 
    }

    public static boolean getShoot(){
        return _buttons2.getRawButton( 3 ) || _joystick.getRawButton(8);
    }

    public static boolean getShooterLow(){
        return _buttons1.getRawButton( 1 );
    }

    public static boolean getClimbExtend(){
        return _buttons2.getRawButton( 5 );
    }

    public static boolean getClimbRetract(){
        return _buttons2.getRawButton( 6 );
    }

    /*public static boolean getClimbPush(){
        return _buttons1.getRawButton( 12 );
    }*/

    public static boolean getClimbPull(){
        return _buttons1.getRawButton( 4 );
    }
    
    public static boolean getSafeZone(){
        return !_buttons2.getRawButton( 1 ) || _joystick.getRawButton(7);
    }
    public static boolean getIgnoreLimits() {
        return _joystick.getRawButton(7);
    }

    public static boolean resetLimits(){
        return _joystick.getRawButton(12); 
    }
    /*public static boolean getCenter() {
        return _buttons1.getRawButton(1);
    }

    public static boolean getLimit() {
        return _buttons2.getRawButton(1);
    }*/

    public static void getSpeedChange() {
        if(getIntake()){
            speedReducerZ = 1.75;    
        }else{
            if ( _joystick.getRawButton( 4 ) || _joystick.getRawButton( 3 )) {
                speedReducerY = 2;
                speedReducerZ = 2.25;
            } else if ( _joystick.getRawButton( 2 ) ) {
                speedReducerY = 1;
                speedReducerZ = 1.25;
            } else {
                speedReducerY = 1.25;
                speedReducerZ = 1.75;
            }
        }

        
    }
}
