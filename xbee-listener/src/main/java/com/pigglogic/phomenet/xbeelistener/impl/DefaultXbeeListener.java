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
import com.pigglogic.phomenet.service.ObservationType;
import com.pigglogic.phomenet.xbeelistener.XbeeAnalogDataTransformer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.michaelpigg.xbeelib.XbeeService;
import net.michaelpigg.xbeelib.protocol.ReceiveIoDataFrame;
import net.michaelpigg.xbeelib.XbeeHandlerCallbackAdapter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for XBee data, attempts to transform it and store it as an observation
 * @author michaelpigg
 */
public class DefaultXbeeListener extends XbeeHandlerCallbackAdapter implements ServiceTrackerCustomizer {

    private Logger logger = LoggerFactory.getLogger(DefaultXbeeListener.class);

    private final XbeeTransformerTracker transformerTracker;
    private final ServiceTracker eventAdminTracker;
    private final ServiceTracker xbeeServiceTracker;
    private final BundleContext context;

    public DefaultXbeeListener(XbeeTransformerTracker transformerTracker, ServiceTracker eventAdminTracker, BundleContext context) {
        this.transformerTracker = transformerTracker;
        this.eventAdminTracker = eventAdminTracker;
        this.context = context;
        this.xbeeServiceTracker = new ServiceTracker(context, XbeeService.class.getName(), this);
        xbeeServiceTracker.open();
    }

    @Override
    public void IoDataReceived(ReceiveIoDataFrame frame) {
        final ServiceReference ref = transformerTracker.getService(String.valueOf(frame.getSourceAddress()));
        if (ref != null) {
            final String locationName = (String) ref.getProperty("location.name");
            final XbeeAnalogDataTransformer transformer = (XbeeAnalogDataTransformer)context.getService(ref);
            final Double value = transformer.transform(frame);
            final ObservationType type = transformer.getObservationType();
            final ObservationRecorder observationRecorder = BundleActivator.getObservationRecorder();
            if (observationRecorder != null)
            {
                observationRecorder.recordObservation(locationName, value, type, new Date());
            }
            logger.debug("Value from {} is {}, signal strength {}", new Object[] {frame.getSourceAddress(), value, frame.getSignalStrength()});
            final EventAdmin eventAdmin = (EventAdmin)eventAdminTracker.getService();
            if (eventAdmin != null) {
                Map<String, Object> eventProperties = new HashMap<String, Object>();
                eventProperties.put("location.name", locationName);
                final String addressString = frame.getSourceAddress().toString();
                eventProperties.put("location.address", addressString);
                eventProperties.put("observedValue", value);
                eventAdmin.postEvent(new Event("phomenet/Observation/Temperature/" + addressString, eventProperties));
            }
        } else {
            logger.warn("Frame is being ignored because no transformer is registered for the source address: {}", frame);
        }
    }

    public Object addingService(ServiceReference reference) {
        final XbeeService service = (XbeeService)context.getService(reference);
        logger.info("Xbee listener added to xbee service");
        service.addListener(this);
        return service;
    }

    public void removedService(ServiceReference reference, Object service) {
        context.ungetService(reference);
        final XbeeService xbeeService = (XbeeService)service;
        xbeeService.removeListener(this);
    }

    public void modifiedService(ServiceReference reference, Object service) {
        // no action
    }


}
