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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.michaelpigg.xbeelib.protocol.IoSample;
import net.michaelpigg.xbeelib.protocol.ReceiveIoDataFrame;
import net.michaelpigg.xbeelib.protocol.XbeeAddress;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * Unit test for {@link TemperatureSensorTransformer}
 */
@Test
public class TemperatureSensorTransformerTest {

    private static final Double TEMP_TOLERANCE = 0.3;
    private static final Double REF_VOLTAGE = 3.0;

    @Test(dataProvider="defaultTempData")
    public void testTransformerDefault(final Integer adcValue, final Double temp, final Double correction, final String description) {
        TemperatureSensorTransformer transformer = TemperatureSensorTransformer.builder().outputCorrection(correction).vRef(REF_VOLTAGE).build();
        List<IoSample> samples = new ArrayList<IoSample>();
        samples.add(new IoSample(1, adcValue));
        ReceiveIoDataFrame frame = new ReceiveIoDataFrame(1, 1, XbeeAddress.getAddress("01"), 30, false, false, 1, Short.valueOf("1"), new ArrayList<IoSample>(), samples);
        final double result = transformer.transform(frame);
        Assert.assertEquals(result, temp, TEMP_TOLERANCE, description);
    }

    @DataProvider(name="defaultTempData")
    private Iterator<Object[]> testData() {
        final ArrayList<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[] {171, 0.15, 0.0, "0 degrees"});
        data.add(new Object[] {256, 25.07, 0.0, "77 degrees"});
        data.add(new Object[] {256, 35.07, 10.0, "25.07+10 degrees"});
        return data.iterator();
    }
}
