/**
 * Copyright (c) 2010, Pigg Logic, LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 *  conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 * * Neither the name of Pigg Logic, LLC nor the names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 *  STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.pigglogic.phomenet.xbee.sensor.temperature.impl;

import com.pigglogic.phomenet.xbee.sensor.temperature.impl.command.TempSensorCommand;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import org.apache.felix.service.command.CommandProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

public class BundleActivator implements org.osgi.framework.BundleActivator {

    public static String PID = "com.pigglogic.phomenet.xbee.sensor.temperature";
    private ServiceRegistration factoryRegistration;
    private XbeeTemperatureSensorFactory sensorFactory;
    private ServiceRegistration commandRegistration;

    public void start(BundleContext context) throws Exception {
        sensorFactory = new XbeeTemperatureSensorFactory(context);
        Dictionary properties = new Hashtable<String, String>();
        properties.put(Constants.SERVICE_PID, PID);
        factoryRegistration = context.registerService(ManagedServiceFactory.class.getName(),
                sensorFactory, properties);
        Hashtable<String, Object> cmdProps = new Hashtable<String, Object>();
        cmdProps.put(CommandProcessor.COMMAND_SCOPE, "xbee");
        cmdProps.put(CommandProcessor.COMMAND_FUNCTION, new String[] {"xtempadd", "xtempdel", "xtemp"});
        commandRegistration = context.registerService(TempSensorCommand.class.getName(), new TempSensorCommand(context), null);
    }

    public void stop(BundleContext context) throws Exception {
        commandRegistration.unregister();
        ((XbeeTemperatureSensorFactory)context.getService(factoryRegistration.getReference())).stop();
        factoryRegistration.unregister();
    }

}
