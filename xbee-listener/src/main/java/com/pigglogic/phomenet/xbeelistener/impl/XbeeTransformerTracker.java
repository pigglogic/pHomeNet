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

package com.pigglogic.phomenet.xbeelistener.impl;

import com.pigglogic.phomenet.xbeelistener.XbeeAnalogDataTransformer;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Maintains a collection of tracked transformers keyed by source address. */
public class XbeeTransformerTracker extends ServiceTracker {

    final Logger logger = LoggerFactory.getLogger(XbeeTransformerTracker.class);

    final Map<String, ServiceReference> servicesByAddress = new HashMap<String, ServiceReference>();

    public XbeeTransformerTracker(BundleContext context) {
        super(context, XbeeAnalogDataTransformer.class.getName(), null);
    }

    public ServiceReference getService(String address) {
        return servicesByAddress.get(address);
    }

    @Override
    public Object addingService(ServiceReference reference) {
        final Object address = reference.getProperty("location.address");
        if (address != null) {
            servicesByAddress.put((String)address, reference);
        } else {
            logger.warn("XbeeAnalogDataTransformer is missing expected address property, so the transformer will not be used.");
        }

        return super.addingService(reference);
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        if (servicesByAddress.containsValue(reference)) {
            final String address = (String)reference.getProperty("location.address");
            if (address != null)
            {
                servicesByAddress.remove(address);
            }
        }
        super.removedService(reference, service);
    }

}
