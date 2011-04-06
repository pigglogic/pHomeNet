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

package com.pigglogic.phomenet.service.impl;

import com.pigglogic.phomenet.service.Observation;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.apache.felix.service.command.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Query observations by source
 */
public class ObservationQueryCommand {

    private EntityManagerFactory entityManagerFactory;
    private Logger logger = LoggerFactory.getLogger(ObservationQueryCommand.class);

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Descriptor("Get latest observations for a location")
    public void qo(@Descriptor("Location") String location) {

        final EntityManager em = entityManagerFactory.createEntityManager();

        try {
            final List observations = getObservationsBySource(location, em);
            if (observations.size() > 0)
            {
                for (Object observation : observations)
                {
                    Observation o = (Observation)observation;
                    System.out.printf("%tc     %f %n", o.getDate(), o.getObservedValue());

                }
            } else {
                System.out.println("No observations found for " + location);
            }
        } finally {
            em.close();
        }
    }


    public String getName() {
        return "qo";
    }

    public String getShortDescription() {
        return "Query recorded observations";
    }

    public String getUsage() {
        return "qo source <-n count>";
    }

    private List getObservationsBySource(String source, EntityManager em) {
        final Query query = em.createQuery("select o from Observation o where o.location=:location order by o.observationDate DESC");
        query.setParameter("location", source);
        query.setMaxResults(5);
        return query.getResultList();


    }

    @Descriptor("Get list of temperature observations from all locations")
    public void temps() {
        final EntityManager em = entityManagerFactory.createEntityManager();

        try {
            List results = em.createNamedQuery("tempsByLocation").getResultList();

            if (!results.isEmpty())
            {
                System.out.println("Location    Temperature       Date");
                System.out.println("----------------------------------");
                for (Object result : results)
                {
                    Object[] o = (Object[])result;
                    System.out.printf("%s  %f   %tc", o[0], o[2], o[1]);
                }
            } else {
                System.out.println("No temperature observations found.");
            }
        } catch (RuntimeException re) {
            logger.debug("Exception querying temperature data", re);
            System.err.println("Error getting temperature data.");
        } finally {
            em.close();
        }

    }

}
