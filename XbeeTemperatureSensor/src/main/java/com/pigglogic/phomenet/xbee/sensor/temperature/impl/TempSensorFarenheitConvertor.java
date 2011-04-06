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

import com.pigglogic.phomenet.service.ObservationType;
import com.pigglogic.phomenet.xbeelistener.XbeeAnalogDataTransformer;
import net.michaelpigg.xbeelib.protocol.ReceiveIoDataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Decorates a TemperatueSensorTransformer to convert it's result farenheit. */
public class TempSensorFarenheitConvertor implements XbeeAnalogDataTransformer {

    private static final double C_TO_F_CONSTANT = 1.8;
    private static final double C_TO_F_OFFSET = 32;

    private Logger logger = LoggerFactory.getLogger(TempSensorFarenheitConvertor.class);
    private final TemperatureSensorTransformer tempTransformer;

    public TempSensorFarenheitConvertor(TemperatureSensorTransformer tempTransformer) {
        this.tempTransformer = tempTransformer;
    }

    public ObservationType getObservationType() {
        return tempTransformer.getObservationType();
    }

    public Double transform(ReceiveIoDataFrame ioDataFrame) {
        final double tempC = tempTransformer.transform(ioDataFrame);
        final double tempF = (tempC * C_TO_F_CONSTANT) + C_TO_F_OFFSET;
        logger.debug("Temp celsius {} converted to temp farenheit {}", tempC, tempF);
        return tempF;
    }

}
