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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pigglogic.phomenet.xbee.sensor.temperature.impl.command;

import com.pigglogic.phomenet.xbee.sensor.temperature.impl.BundleActivator;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Dictionary;
import java.util.Hashtable;
import net.michaelpigg.xbeelib.protocol.XbeeAddress;
import org.apache.commons.lang.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michaelpigg
 */
public class ConfigureTempSensorCommand {

    public static final String SENSOR_ADDRESS = "location.address";
    public static final String SENSOR_NAME = "location.name";
    public static final String SENSOR_CORRECTION = "location.offset";

    private final BundleContext context;
    
    final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigureTempSensorCommand.class);

    public ConfigureTempSensorCommand(BundleContext context) {
        this.context = context;
    }

    public void execute(String address, String name, Double correction, PrintStream out, PrintStream err) {
        try {
            final Dictionary props = new Hashtable();
            try {
                XbeeAddress.getAddress(address);
            } catch (RuntimeException re) {
                err.printf("Address %s can not be parsed as XBee address", address);
            }
            props.put(SENSOR_ADDRESS, address);
            props.put(SENSOR_NAME, StringUtils.isNotBlank(name) ? name : "Sensor " + address );
            if (correction != null) {
                props.put(SENSOR_CORRECTION, correction);
            }

            final ConfigurationAdmin admin = (ConfigurationAdmin) context.getService(
            context.getServiceReference(ConfigurationAdmin.class.getName()));
            final Configuration config = admin.createFactoryConfiguration(BundleActivator.PID);
            config.update(props);
        } catch (IOException ex) {
            err.println("Error while creating configuration");
        }
    }

}
