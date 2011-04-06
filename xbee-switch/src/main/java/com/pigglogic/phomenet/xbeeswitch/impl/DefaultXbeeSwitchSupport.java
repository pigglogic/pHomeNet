package com.pigglogic.phomenet.xbeeswitch.impl;

import java.util.Calendar;
import java.util.Random;
import net.michaelpigg.xbeelib.XbeeService;
import net.michaelpigg.xbeelib.protocol.AtCommand;
import net.michaelpigg.xbeelib.protocol.XbeeAddress;
import net.michaelpigg.xbeelib.protocol.XbeeAtCommands;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/** Provides generic XBee-based switch services */
public class DefaultXbeeSwitchSupport implements XbeeSwitchSupport {

    private final static int OFF_VALUE = 0x0;
    private final static int ON_VALUE = 0x2;

    private final ServiceTracker xbeeServiceTracker;
    private final Random random = new Random(Calendar.getInstance().getTimeInMillis());

    public DefaultXbeeSwitchSupport(BundleContext bundleContext) {
        xbeeServiceTracker = new ServiceTracker(bundleContext, XbeeService.class.getName(), null);
        xbeeServiceTracker.open();
    }

    @Override
    protected void finalize() throws Throwable {
        xbeeServiceTracker.close();
        super.finalize();
    }


    public SwitchState getSwitchState(XbeeAddress address) {
        throw new UnsupportedOperationException("Not implemented  yet.");
    }

    public void setSwitchState(XbeeAddress address, SwitchState newState) {
        final int cmdValue = newState == SwitchState.ON ? ON_VALUE : OFF_VALUE;
        AtCommand command = new AtCommand(XbeeAtCommands.IO, cmdValue, address, random.nextInt(256));
        final XbeeService xbeeService = (XbeeService)xbeeServiceTracker.getService();
        if (xbeeService != null)
        {
            xbeeService.sendCommand(command);
        } else {
            throw new RuntimeException("Unable to set switch state because XbeeService is not available");
        }
    }


}
