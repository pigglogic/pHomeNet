
package com.pigglogic.phomenet.xbeeswitch.impl;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/** Create instances of {@link TemperatureLimitSwitch} */
public class TemperatureLimitSwitchFactory implements ManagedServiceFactory {

    final BundleContext bundleContext;
    final Map<String, ServiceRegistration> switches = new HashMap<String, ServiceRegistration>();
    final DefaultXbeeSwitchSupport xbeeSwitchSupport;

    public TemperatureLimitSwitchFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.xbeeSwitchSupport = new DefaultXbeeSwitchSupport(bundleContext);
    }

    public void deleted(String pid) {
        final ServiceRegistration registration = switches.remove(pid);
        if (registration != null) {
            registration.unregister();
        }
    }

    public String getName() {
        return "Temperature Limit switch factory";
    }

    public void updated(String pid, Dictionary properties) throws ConfigurationException {
        final ServiceRegistration registration = switches.get(pid);
        final String address = (String)properties.get("switch.address");
        final double setPoint = (Double)properties.get("switch.setpoint");
        final String monitorTemp = (String)properties.get("monitor.temp.name");

        if (registration == null) {
            String[] topics = new String[] {"phomenet/Observation/Temperature/" + monitorTemp};
            Hashtable props = new Hashtable();
            props.put(EventConstants.EVENT_TOPIC, topics);
            final TemperatureLimitSwitch tlSwitch = new TemperatureLimitSwitch(address, xbeeSwitchSupport);
            tlSwitch.setSetPoint(setPoint);

            ServiceRegistration sr = bundleContext.registerService(EventHandler.class.getName(), tlSwitch, props);
            switches.put(pid, sr);
        } else {
            TemperatureLimitSwitch tls = (TemperatureLimitSwitch)bundleContext.getService(registration.getReference());
            tls.setSetPoint(setPoint);
        }
    }


}
