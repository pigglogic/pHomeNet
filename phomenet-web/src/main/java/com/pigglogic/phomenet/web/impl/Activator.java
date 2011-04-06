package com.pigglogic.phomenet.web.impl;

import java.util.Hashtable;
import javax.servlet.Servlet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

public class Activator implements BundleActivator {

    private ServiceRegistration webConsoleRegistration;

    public void start(BundleContext context) throws Exception {

        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put("felix.webconsole.label", "phomenet");
        props.put("felix.webconsole.title", "pHomeNet Console");
        props.put(EventConstants.EVENT_TOPIC, "phomenet/Observation/*");
        webConsoleRegistration = context.registerService(
                new String[] {Servlet.class.getName(), EventHandler.class.getName()},
                new PhomenetConsolePlugin(context), props);
    }

    public void stop(BundleContext context) throws Exception {
        if (webConsoleRegistration != null) {
            webConsoleRegistration.unregister();
        }
    }

}
