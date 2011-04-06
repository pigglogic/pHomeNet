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

import java.util.Date;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import com.pigglogic.phomenet.service.ObservationRecorder;
import com.pigglogic.phomenet.service.ObservationType;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author michaelpigg
 */
@Test
public class DefaultObservationServiceTest {

    public void testRecordObservation()
    {
        final UUID testId = UUID.randomUUID();
        final Map map = new HashMap();
        map.put("javax.persistence.jdbc.url", "jdbc:derby:target/phomenetdb;create=true");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("phomenet", map);
        EntityManager em = emf.createEntityManager();


        ObservationRecorder recorder = new DefaultObservationService();
        ((DefaultObservationService)recorder).setEmf(emf);
        recorder.recordObservation(testId.toString(), 75.0, ObservationType.TEMPERATURE, new Date());

        Object result =  em.createQuery("select o from Observation o where o.location = '" + testId.toString() + "'").getSingleResult();
        Assert.assertNotNull(result);
        em.close();
        emf.close();
    }
}
