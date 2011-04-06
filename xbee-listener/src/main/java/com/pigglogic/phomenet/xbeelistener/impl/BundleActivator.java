/**
 * Copyright (c) 2010, Pigg Logic, LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of Pigg Logic, LLC nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.pigglogic.phomenet.xbeelistener.impl;

import com.pigglogic.phomenet.service.ObservationRecorder;
import java.util.Hashtable;
import net.michaelpigg.xbeelib.XbeeService;
import org.apache.felix.service.command.CommandProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.LoggerFactory;

/**
 * Registers the the XBee listener service
 * @author michaelpigg
 */
public class BundleActivator implements org.osgi.framework.BundleActivator {

    private DefaultXbeeListener listener;
    private static ServiceTracker xbeeTracker;
    private static ServiceTracker recorderTracker;
    private static XbeeTransformerTracker transformersTracker;
    private static BundleContext context;
    private ServiceRegistration sampleCommandRegistration;
    private ServiceTracker eventAdminTracker;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(BundleActivator.class);
    
    public void start(BundleContext context) throws Exception {
        BundleActivator.context = context;
        transformersTracker = new XbeeTransformerTracker(context);
        transformersTracker.open();
        xbeeTracker = new ServiceTracker(context, XbeeService.class.getName(), null);
        xbeeTracker.open();
        recorderTracker = new ServiceTracker(context, ObservationRecorder.class.getName(), null);
        recorderTracker.open();
        eventAdminTracker = new ServiceTracker(context, EventAdmin.class.getName(), null);
        eventAdminTracker.open();
        listener = new DefaultXbeeListener(transformersTracker, eventAdminTracker, context);

        Hashtable<String, Object> commandProps = new Hashtable<String, Object>();
        commandProps.put(CommandProcessor.COMMAND_FUNCTION, new String[] {"xsample"});
        commandProps.put(CommandProcessor.COMMAND_SCOPE, "xbee");
        sampleCommandRegistration = context.registerService(SampleXbeeCommand.class.getName(), new SampleXbeeCommand(context, xbeeTracker, transformersTracker), commandProps);

        logger.info("Xbee listener bundle started");
    }

    public void stop(BundleContext context) throws Exception {
        if (sampleCommandRegistration != null) {
            sampleCommandRegistration.unregister();
        }
        if (listener != null) {

        }
        if (xbeeTracker != null)
        {
            xbeeTracker.close();
        }
        if (recorderTracker != null) {
            recorderTracker.close();
        }
        if (xbeeTracker != null)
        {
            xbeeTracker.close();
        }
        if (eventAdminTracker != null)
        {
            eventAdminTracker.close();
        }
        logger.info("Xbee listener bundle stopped");
    }

    public static ObservationRecorder getObservationRecorder()
    {
        return (ObservationRecorder)recorderTracker.getService();
    }

    public static ServiceTracker getTransformerServiceTracker()
    {
        return transformersTracker;
    }

    public static BundleContext getContext()
    {
        return context;
    }
    
    public static Class loadClass(String className) throws ClassNotFoundException {
        return context.getBundle().loadClass(className);
    }
}
