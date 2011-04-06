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
import junit.framework.Assert;
import net.michaelpigg.xbeelib.protocol.IoSample;
import net.michaelpigg.xbeelib.protocol.ReceiveIoDataFrame;
import net.michaelpigg.xbeelib.protocol.XbeeAddress;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** Unit tests for {@link TempSensorFarenheitConvertor} */
@Test
public class TempSensorFarenheitConvertorTest {

    @Test(dataProvider="TempSensorData")
    public void testConversion(double input, double expected, String description) {
        TempSensorFarenheitConvertor convertor = new TempSensorFarenheitConvertor(new TestTempSensor(input));
        ReceiveIoDataFrame receiveIoDataFrame = new ReceiveIoDataFrame(1, 1, XbeeAddress.getAddress("01"), 30, false, false, 1, Short.valueOf("1"), new ArrayList<IoSample>(), new ArrayList<IoSample>());
        Assert.assertEquals(expected, convertor.transform(receiveIoDataFrame));
    }

    @DataProvider(name="TempSensorData")
    public Iterator<Object[]> testConversionData() {
        final ArrayList<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{0, 32, "Zero degrees"});
        data.add(new Object[]{25, 77, "25 degrees"});
        return data.iterator();
    }

    private class TestTempSensor extends TemperatureSensorTransformer
    {
        private final double result;

        public TestTempSensor(double result) {
            this.result = result;
        }

        @Override
        public Double transform(ReceiveIoDataFrame ioDataFrame) {
            return result;
        }

    }
}
