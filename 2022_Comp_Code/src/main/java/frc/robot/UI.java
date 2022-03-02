package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class UI {

    private static final Joystick _joystick        = new Joystick(0);
    private static final Joystick _buttons1        = new Joystick(1);
    private static final Joystick _buttons2        = new Joystick(2);
    private static final Spark _LED                = new Spark(0);

<<<<<<< Updated upstream
    private static final Joystick _buttons2                = new Joystick(2);
=======
>>>>>>> Stashed changes
    private static double speedReducerY = 1.5;
    private static double speedReducerZ = 2; 

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
<<<<<<< Updated upstream
        return  _joystick.getRawButton(1);
=======
        return _joystick.getRawButton(1);
>>>>>>> Stashed changes
    }

    public static boolean getAutoAim(){
        return _buttons1.getRawButton( 6 ); //fix
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
        return _buttons2.getRawButton( 7 );
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
<<<<<<< Updated upstream
=======
    }

    //sets LEDS to the blue pattern
    public static void setLedsBlue() {
        System.out.println("is blue");
        _LED.set(-0.95);
>>>>>>> Stashed changes
    }

    //sets LEDS to solid green
    public static void setLedsGreen() {
        System.out.println("is green");
        _LED.set(0.75);
    }

    //sets LEDS to solid pink
    public static void setLedsPink() {
        System.out.println("is pink");
        _LED.set(0.57);
    }
    
}
