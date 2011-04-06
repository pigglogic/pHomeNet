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
package com.pigglogic.phomenet.service.impl;

import java.util.Dictionary;
import static org.eclipse.persistence.config.PersistenceUnitProperties.*;

import java.util.HashMap;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import com.pigglogic.phomenet.service.ObservationRecorder;
import java.util.Hashtable;
import org.apache.felix.service.command.CommandProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 * Register and start service
 * @author michaelpigg
 */
public class BundleActivator implements org.osgi.framework.BundleActivator, ManagedService {

    private EntityManagerFactory entityManagerFactory;
    private ServiceRegistration recorderRegistration;
    private ServiceRegistration queryCommandRegistration;
    private ServiceRegistration tempsCommandRegistration;
    private ServiceRegistration managedServiceRegistration;

    private BundleContext context;
    private DefaultObservationService observationService;

    public void start(BundleContext context) throws Exception {
        this.context = context;
        final Dictionary cmProps = new Hashtable();
        cmProps.put(Constants.SERVICE_PID, "com.pigglogic.phomenet.service.db");
        managedServiceRegistration = context.registerService(ManagedService.class.getName(), this, cmProps);
    }

    public void stop(BundleContext context) throws Exception {
        unregister(recorderRegistration);
        unregister(tempsCommandRegistration);
        unregister(queryCommandRegistration);
        if (entityManagerFactory != null)
        {
            entityManagerFactory.close();
        }
    }

    private void unregister(ServiceRegistration sr) {
        if (sr != null) {
            sr.unregister();
        }
    }
    public void updated(Dictionary properties) throws ConfigurationException {
        if (properties == null)
        {
            return;
        }
        HashMap emfProps = new HashMap();
        emfProps.put(JDBC_URL, properties.get(JDBC_URL));
        emfProps.put(JDBC_DRIVER, properties.get(JDBC_DRIVER));
        emfProps.put(JDBC_USER, properties.get(JDBC_USER));
        emfProps.put(JDBC_PASSWORD, properties.get(JDBC_PASSWORD));
        emfProps.put(CLASSLOADER, this.getClass().getClassLoader());

        entityManagerFactory = Persistence.createEntityManagerFactory("phomenet", emfProps);

        if (observationService == null) {
            observationService = new DefaultObservationService();
            recorderRegistration = context.registerService(ObservationRecorder.class.getName(), observationService, null);
            final Hashtable<String, Object> commandProps = new Hashtable<String, Object>();
            commandProps.put(CommandProcessor.COMMAND_SCOPE, "xbee");
            commandProps.put(CommandProcessor.COMMAND_FUNCTION, new String[] {"qo", "temps"});
            final ObservationQueryCommand queryCommand = new ObservationQueryCommand();
            queryCommand.setEntityManagerFactory(entityManagerFactory);
            queryCommandRegistration = context.registerService(ObservationQueryCommand.class.getName(), queryCommand, commandProps);
            
        }
        observationService.setEmf(entityManagerFactory);

    }
}
