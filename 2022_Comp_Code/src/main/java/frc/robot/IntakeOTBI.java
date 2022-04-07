package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public class IntakeOTBI {

    private final WPI_TalonFX _OTBI = new WPI_TalonFX(32); //The motor to lower the intake

public IntakeOTBI( ) {
    _OTBI.configFactoryDefault();
    _OTBI.configReverseSoftLimitThreshold(     0.0, 0);
    _OTBI.configForwardSoftLimitThreshold( 54500.0, 0);
    _OTBI.configForwardSoftLimitEnable(true, 0);
    _OTBI.configReverseSoftLimitEnable(true, 0);
    _OTBI.config_kP( 0, 0.06,            30);
    //_OTBI.config_kI( 0, 0.001,          30);
    //_OTBI.config_kD( 0, 0.4,            30);
    //_OTBI.config_IntegralZone( 0, 4000.0, 30);
_OTBI.setNeutralMode( NeutralMode.Brake);
    zeroEncoders();
}
public void Execute() {
    //System.out.println( _OTBI.getSelectedSensorPosition());
}

public void zeroEncoders() {
    _OTBI.setSelectedSensorPosition(0);
}

public void LowerIntake() {
    _OTBI.set( ControlMode.Position, 54500.0);
}

public void StowIntake() {
    _OTBI.set( ControlMode.Position, 0.00);
}

public void RaiseIntake() {
    _OTBI.set( ControlMode.Position, 18000.00);
}

public void disable() {
    _OTBI.setNeutralMode( NeutralMode.Coast);
    _OTBI.stopMotor();
}
    
}
