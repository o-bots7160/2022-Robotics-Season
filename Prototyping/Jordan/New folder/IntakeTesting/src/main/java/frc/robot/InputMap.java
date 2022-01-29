package frc.robot;

public class InputMap{

    // Input systemI
    public static final int DRIVEJOY             =   0;
    public static final int MINIPJOY_1           =   1;
    public static final int MINIPJOY_2           =   2;
    //public static final int 

    // Speeds
    public static final double SPEED_Y          = 0.5d;
    public static final double SPEED_Z          = 0.5d;
    //

    // Drive joy axises
    public static final int DRIVEJOY_X          =    0;
    public static final int DRIVEJOY_Y          =    1;
    public static final int DRIVEJOY_Z          =    2;
    public static final int DRIVEJOY_SLIDER     =    3;
    //

    // DRIVEJOY buttons
    public static final int ENGAGE_INTAKE       =    1;
    public static final int DISENGAGE_INTAKE    =    2;
    public static final int ENGAGE_RATCHET      =    3;
    public static final int DISENGAGE_RATCHET   =    4;
    public static final int LIMELIGHT_TARGET    =    5;
    public static final int REALCLOSE_PIPE      =    7;
    public static final int FRONTPANEL_PIPE     =    9;
    public static final int BEHINDPANEL_PIPE    =   11;
    public static final int FEED_VIA_SHOOTER    =   12;
    //

    // MINPJOY_1 joy buttons
    public static final int SHOOTBUTTON         =    1;
    public static final int UPPER_HOPPER_UP     =    2;
    public static final int LOWER_HOPPER_UP     =    3;
    public static final int COLOR_POSITION      =    4;
    public static final int COLOR_4_TIMES       =    5;
    public static final int BACKFEED            =    6;
    public static final int LIMELIGHT_ON        =    7;

    // MINPJOY_2 Joy buttons
    public static final int INTAKE_IN           =    1;
    public static final int TURRET_LEFT         =    2;
    public static final int TURRET_RIGHT        =    3;
    public static final int LEVELER_GREEN       =    4;
    public static final int LEVELER_RED         =    5;
    public static final int LIFT_DOWN           =    6;
    public static final int LIFT_UP             =    9;
    public static final int COLOR_AUTO          =    8;

    // Dead band
    public static boolean DeadBand(double lowerLimit, double input){

        if(Math.abs(input) >= lowerLimit)
            return true;
        else
            return false;
    }

}
