package com.pigglogic.phomenet.xbeeswitch.impl;

import net.michaelpigg.xbeelib.protocol.XbeeAddress;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Turn on switch if temperature exceeds set point */
public class TemperatureLimitSwitch implements EventHandler {

    private final XbeeAddress address;
    private final DefaultXbeeSwitchSupport switchSupport;

    private double setPoint;
    private double lastObserved;
    private SwitchState currentState;

    private Logger logger = LoggerFactory.getLogger(TemperatureLimitSwitch.class);

    public TemperatureLimitSwitch(String address, DefaultXbeeSwitchSupport xbeeSwitchSupport) {
        this.address = XbeeAddress.getAddress(address);
        this.switchSupport = xbeeSwitchSupport;
    }

    public void setLastObserved(double newValue) {
        final double previousObserved = this.lastObserved;
        setCurrentState(stateForObservation(this.setPoint, newValue));
    }

    protected SwitchState stateForObservation(final double setPoint, final double observedTemperature) {
        if (observedTemperature <= setPoint) {
            return SwitchState.OFF;
        } else {
            return SwitchState.ON;
        }
    }

    public double getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(double setPoint) {
        this.setPoint = setPoint;
    }

    private void setCurrentState(SwitchState newValue) {
        logger.debug("Calling setSwitchState for {} set to {}", address, newValue);
        switchSupport.setSwitchState(address, newValue);
    }

    public void handleEvent(Event event) {
        logger.debug("handleEvent called with {}", event);
        Object temp = event.getProperty("observedValue");
        setLastObserved((Double)temp);
    }


}
