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

import static org.testng.Assert.*;

import com.pigglogic.phomenet.service.Observation;
import com.pigglogic.phomenet.service.ObservationType;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Unit test for named queries in Observation.
 */
@Test
public class ObservationTest {

    Logger logger = LoggerFactory.getLogger(ObservationTest.class);

    public void testTempsByLocationQuery() {
        final Map map = new HashMap();
        map.put("javax.persistence.jdbc.url", "jdbc:derby:target/phomenetdb;create=true");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("phomenet", map);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query deleteQuery = em.createQuery("delete from Observation");
        deleteQuery.executeUpdate();
        em.getTransaction().commit();
        em.getTransaction().begin();
        Observation obs1 = createObservation("LOC1", 30.0);
        em.persist(obs1);
        Observation obs2 = createObservation("LOC1", 30.2);
        em.persist(obs2);
        Observation obs3 = createObservation("LOC2", 40.0);
        em.persist(obs3);
        em.getTransaction().commit();
        Query query = em.createNamedQuery("tempsByLocation");
        List results = query.getResultList();
        for (Object result : results)
        {
            logger.info("observation : {}", result);

        }
        assertEquals(results.size(), 2, "Should get one row for each location");
        Set<String> locations = new HashSet<String>();
        for (Object actual : results) {
            Object[] fields = (Object[])actual;
            locations.add((String)fields[0]);
        }
        assertEquals(locations.toArray(), new Object[] {"LOC1", "LOC2"}, "Expected one of each location");

    }

    private Observation createObservation(String location, Double value) {
        Observation observation = new Observation();
        observation.setDate(new Date());
        observation.setType(ObservationType.TEMPERATURE);
        observation.setLocation(location);
        observation.setObservedValue(value);
        return observation;
    }
}
