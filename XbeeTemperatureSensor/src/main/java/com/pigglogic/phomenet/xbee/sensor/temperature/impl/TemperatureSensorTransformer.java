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
import com.pigglogic.phomenet.service.ObservationType;
import org.slf4j.Logger;
import net.michaelpigg.xbeelib.protocol.ReceiveIoDataFrame;
import org.slf4j.LoggerFactory;

/**
 * Transformer for Xbee temperature sensor.
 * @author michaelpigg
 */
public class TemperatureSensorTransformer implements XbeeAnalogDataTransformer {
    private Logger logger = LoggerFactory.getLogger(TemperatureSensorTransformer.class);

    private int adcSteps = 1023;
    private double outputCorrection = 0;
    private double tempCoefficent = 0.01;
    private double vRef = 1.235;
    private double voltsZeroDegrees = 0.5;

    public TemperatureSensorTransformer(Builder builder) {
        this.outputCorrection = builder.outputCorrection;
        this.tempCoefficent = builder.tempCoefficent;
        this.vRef = builder.vRef;
        this.voltsZeroDegrees = builder.voltsZeroDegrees;
        this.adcSteps = builder.adcSteps;
    }

    public static Builder builder() {
        return new Builder();
    }

    public TemperatureSensorTransformer(double outputCorrection) {
        this.outputCorrection = outputCorrection;
    }

    public TemperatureSensorTransformer(double outputCorrection, double vref) {
        this.outputCorrection = outputCorrection;
        this.vRef = vref;
    }

    public TemperatureSensorTransformer() {
    }

    public void setOffset(double offset) {
        this.outputCorrection = offset;
    }

    public double getOffset() {
        return outputCorrection;
    }

    public double getvRef() {
        return vRef;
    }

    public void setvRef(double vRef) {
        this.vRef = vRef;
    }

    public ObservationType getObservationType() {
        return ObservationType.TEMPERATURE;
    }

    public Double transform(ReceiveIoDataFrame ioDataFrame) {
        int adcValue = ioDataFrame.getAnalogSamples().get(0).getData();
        final double voltageAdc = (vRef / adcSteps) * adcValue;
        final double tempCelsius = (voltageAdc - voltsZeroDegrees)/tempCoefficent;
        final double tempCorrected = tempCelsius + outputCorrection;
        logger.debug("Reading from sensor {}, adc = {}, tc) = {}, corrected tc = {}", new Object[] {ioDataFrame.getSourceAddress(), adcValue, tempCelsius, tempCorrected});
        return tempCorrected;
    }

    public static class Builder {
        private double outputCorrection = 0.0;
        private double tempCoefficent = 0.01;
        private double vRef = 1.235;
        private double voltsZeroDegrees = 0.5;
        private int adcSteps = 1023;

        public Builder outputCorrection(double outputCorrection) {
            this.outputCorrection = outputCorrection;
            return this;
        }

        public Builder tempCoefficent(double tempCoeffiecent) {
            this.tempCoefficent = tempCoeffiecent;
            return this;
        }

        public Builder vRef(double vRef) {
            this.vRef = vRef;
            return this;
        }

        public Builder adcSteps(int adcSteps) {
            this.adcSteps = adcSteps;
            return this;
        }

        public TemperatureSensorTransformer build() {
            return new TemperatureSensorTransformer(this);
        }

    }

}
