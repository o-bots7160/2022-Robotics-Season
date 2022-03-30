package frc.robot;

import java.util.function.BooleanSupplier;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class IntakeIndexer {
    private final WPI_TalonFX _index   = new WPI_TalonFX(31);
    private BooleanSupplier   ballLow;

    public IntakeIndexer( BooleanSupplier new_ballLow) {
        _index.configFactoryDefault();
        _index.setNeutralMode(NeutralMode.Brake);
        ballLow = new_ballLow;
    }

    public void disable() {
        _index.stopMotor();
        _index.setNeutralMode(NeutralMode.Coast);
    }
    public void Collect() {
        if ( ballLow.getAsBoolean() ) {
            _index.set( ControlMode.PercentOutput, 0.20 );
        }
        else {
            _index.stopMotor();
        }
    }
    public void Flush() {
        _index.set( ControlMode.PercentOutput, -0.40);
    }
    public void Stop() {
        if ( ! ballLow.getAsBoolean() ) {
            _index.stopMotor();
        }
    }
    public void Shoot() {
        _index.set( ControlMode.PercentOutput, 0.40);
    }
}