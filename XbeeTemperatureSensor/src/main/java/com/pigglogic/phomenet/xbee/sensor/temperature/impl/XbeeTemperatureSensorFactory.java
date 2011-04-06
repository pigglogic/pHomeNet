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

import com.pigglogic.phomenet.xbeelistener.XbeeAnalogDataTransformer;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

/** Creates and registers services for each configred temperature sensor. */
public class XbeeTemperatureSensorFactory implements ManagedServiceFactory {
    private static final String LOCATION_ADDRESS = "location.address";
    private static final String LOCATION_CORRECTION = "location.correction";
    private static final String LOCATION_NAME = "location.name";
    private static final String LOCATION_VREF = "location.vref";

    final BundleContext bundleContext;
    final Map<String, ServiceRegistration> transformers = new HashMap<String, ServiceRegistration>();

    public XbeeTemperatureSensorFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void stop() {
        for (ServiceRegistration registration : transformers.values())
        {
            registration.unregister();            
        }
        transformers.clear();
    }

    public void deleted(String pid) {
        final ServiceRegistration registration = transformers.remove(pid);
        if (registration != null) {
            registration.unregister();
        }
    }

    public String getName() {
        return "XBee Listener Service Factory";
    }

    public void updated(String pid, Dictionary properties) throws ConfigurationException {
        final ServiceRegistration transformerReg = transformers.get(pid);
        final String address = (String)properties.get(LOCATION_ADDRESS);
        final String name = (String)properties.get(LOCATION_NAME);
        final Double correction = (Double)properties.get(LOCATION_CORRECTION);
        final Double vref = (Double)properties.get(LOCATION_VREF);
        final Dictionary<String, String> registrationProperties = new Hashtable<String, String>();
        registrationProperties.put( LOCATION_NAME, name);
        registrationProperties.put( LOCATION_ADDRESS, address);

        if (transformerReg == null)
        {
            final TemperatureSensorTransformer transformer = new TemperatureSensorTransformer();
            if (correction != null) {
                transformer.setOffset(correction);
            }
            if (vref != null) {
                transformer.setvRef(vref);
            }

            final ServiceRegistration registration = bundleContext.registerService(XbeeAnalogDataTransformer.class.getName(), transformer, registrationProperties);
            transformers.put(pid, registration);
        } else {
            final TemperatureSensorTransformer transformer = (TemperatureSensorTransformer)bundleContext.getService(transformerReg.getReference());
            transformer.setOffset(correction);
            transformerReg.setProperties(properties);
        }
    }

}
