package frc.robot;

import java.util.function.BooleanSupplier;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class IntakeIndexer {
    private final WPI_TalonFX _index   = new WPI_TalonFX(31);
    private BooleanSupplier   ballLow;
    private BooleanSupplier   indexFull;
    private Boolean           collecting = false;

    public IntakeIndexer( BooleanSupplier new_ballLow,
                          BooleanSupplier new_indexFull) {
        _index.configFactoryDefault();
        _index.setNeutralMode(NeutralMode.Brake);
        ballLow   = new_ballLow;
        indexFull = new_indexFull;
    }

    public void disable() {
        _index.stopMotor();
        _index.setNeutralMode(NeutralMode.Coast);
    }
    public void Collect() {
        if ( ! collecting && ballLow.getAsBoolean() && ! indexFull.getAsBoolean() ) {
            collecting = true;
            _index.set( ControlMode.PercentOutput, 0.40 );
        }
        else {
            StopAfterBall();
        }
    }
    public void Flush() {
        _index.set( ControlMode.PercentOutput, -0.40);
    }
    public void StopAfterBall() {
        if ( collecting ){
            if ( ! ballLow.getAsBoolean() ) {
                collecting = false;
                _index.stopMotor();
            }
        } else {
            _index.stopMotor();
        }
    }
    public void Shoot() {
        _index.set( ControlMode.PercentOutput, 0.40);
    }
}