/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pigglogic.phomenet.xbeeswitch.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

/**
 *
 * @author michaelpigg
 */
public class BundleActivator implements org.osgi.framework.BundleActivator {

    ServiceRegistration registration;
    public void start(BundleContext context) throws Exception {
        final Dictionary svcProps = new Hashtable();
        svcProps.put(Constants.SERVICE_PID, "com.pigglogic.phomenet.xbee.switch.templimit");
        context.registerService(ManagedServiceFactory.class.getName(), new TemperatureLimitSwitchFactory(context), svcProps);
    }

    public void stop(BundleContext context) throws Exception {
        if (registration != null)
        {
            registration.unregister();
        }
    }

}
