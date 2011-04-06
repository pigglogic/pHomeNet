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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.michaelpigg.xbeelib.XbeeService;
import net.michaelpigg.xbeelib.protocol.AtCommand;
import net.michaelpigg.xbeelib.protocol.AtCommandResponse;
import net.michaelpigg.xbeelib.protocol.XbeeAtCommands;
import net.michaelpigg.xbeelib.XbeeHandlerCallbackAdapter;
import net.michaelpigg.xbeelib.protocol.IoSample;
import net.michaelpigg.xbeelib.protocol.ReceiveIoDataFrame;
import net.michaelpigg.xbeelib.protocol.XbeeAddress;
import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Provides a command to be used in Felix Shell that requests a sample. */
public class SampleXbeeCommand {

    private final BundleContext bundleContext;
    private final ServiceTracker xbeeServiceTracker;
    private final XbeeTransformerTracker xbeeTransformerTracker;

    private final Random random = new Random();

    private final Logger logger = LoggerFactory.getLogger(SampleXbeeCommand.class);

    public SampleXbeeCommand(BundleContext bundleContext, ServiceTracker xbeeTracker, XbeeTransformerTracker xbeeTransformerTracker) {
        this.bundleContext = bundleContext;
        this.xbeeServiceTracker = xbeeTracker;
        this.xbeeTransformerTracker = xbeeTransformerTracker;
    }

    @Descriptor("Request sample from an XBee module")
    public void xsample(@Descriptor("XBee module address") String address) {

        final XbeeAddress destAddr;
        try {
            destAddr = XbeeAddress.getAddress(address);
        } catch (RuntimeException re)
        {
            System.err.println(String.format("Address %s can not be parsed as an xbee address", address));
            return;
        }

        final Integer frameId = random.nextInt(256);
        final AtCommand command = new AtCommand(XbeeAtCommands.IS, null, destAddr, frameId);
        final XbeeService xbeeService = (XbeeService)xbeeServiceTracker.getService();
        if (xbeeService == null)
        {
            System.err.println("Cannot execute command because the xbee service can not be found.");
            return;
        }

        final XbeeResponseWaiter waiter = new XbeeResponseWaiter(command, xbeeService);
        logger.debug("About to send xbee command: {}", command);
        Future<AtCommandResponse> future = Executors.newSingleThreadExecutor().submit(waiter);
        try {
            final AtCommandResponse response = future.get(10, TimeUnit.SECONDS);
            System.out.printf("Command returned with status %s", response.getStatus().toString());
            System.out.println();
            final byte[] responseData = response.getResponse();
            if (responseData != null && responseData.length > 0) {
                System.out.print("Raw data in response:");
                for (byte b : responseData) {
                    System.out.printf("%x", b);
                }
                System.out.println();

                final int numberOfSamples = responseData[0];
                System.out.printf("Number of samples %d", numberOfSamples).println();

                int samplesRead = 0;
                byte dataByte = 3;
                final List<IoSample> digitalSamples = new ArrayList<IoSample>();
                final List<IoSample> analogSamples = new ArrayList<IoSample>();
                System.out.println("Digital I/O");
                if (isDioEnabled(new byte[] {responseData[1], responseData[2]}))
                {
                    IoSample ioSample = new IoSample(1, Byte.valueOf(responseData[dataByte++]));
                    digitalSamples.add(ioSample);
                    System.out.printf("     %X(%d)", ioSample.getData(), ioSample.getData()).println();
                    samplesRead++;
                } else {
                    System.out.println("     No digital I/O data in response.");
                }
                System.out.println("Analog Data");
                int adcSampleCounter = 0;
                while (samplesRead < numberOfSamples)
                {
                    BigInteger adcValue = new BigInteger(new byte[] {responseData[dataByte++], responseData[dataByte++]});
                    IoSample ioSample = new IoSample(adcSampleCounter++, adcValue.intValue());
                    analogSamples.add(ioSample);
                    System.out.printf("     %d: %X(%d)", adcSampleCounter, ioSample.getData(), ioSample.getData()).println();
                    samplesRead++;
                }
                final ServiceReference service = xbeeTransformerTracker.getService(command.getCommandDestination().toString());
                if (service != null && analogSamples.size() > 0) {
                    final BigInteger controlFrame = new BigInteger(new byte[] {responseData[1], responseData[2]});
                    ReceiveIoDataFrame receiveIoDataFrame = new ReceiveIoDataFrame(0, 0, command.getCommandDestination(), 0, false, false, controlFrame.shortValue(), dataByte, digitalSamples, analogSamples);
                    final XbeeAnalogDataTransformer transformer = (XbeeAnalogDataTransformer) bundleContext.getService(service);
                    final Double transformedValue = transformer.transform(receiveIoDataFrame);
                    System.out.printf("Transformer reports value of %f", transformedValue).println();
                }
            }

        } catch (InterruptedException ex) {
            logger.error("Interrupted waiting for xbee command response", ex);
        } catch (ExecutionException ex) {
            System.err.println(String.format("Exception executing command: %s", ex.toString()));
        } catch (TimeoutException tex) {
            System.err.println("No response recieved before timeSystem.out.");
        } finally {
            future.cancel(true);
        }
    }

    private boolean isDioEnabled(byte[] b) {
        final BigInteger dioControlMask = new BigInteger(new byte[] {0x1, (byte)0xFF});
        final BigInteger dioControl = new BigInteger(b);
        final BigInteger masked = dioControl.and(dioControlMask);
        return masked.intValue() > 0;
    }

    private class XbeeResponseWaiter extends XbeeHandlerCallbackAdapter implements Callable<AtCommandResponse>
    {
        private final AtCommand command;
        private final XbeeService xbeeService;
        private AtCommandResponse response;
        private final SynchronousQueue<AtCommandResponse> responseQueue = new SynchronousQueue<AtCommandResponse>();

        public XbeeResponseWaiter(AtCommand command, XbeeService xbeeService) {
            this.command = command;
            this.xbeeService = xbeeService;
        }

        public AtCommandResponse getResponse() {
            return response;
        }

        @Override
        public void CommandResponse(AtCommandResponse response) {
            logger.debug("Command response frame id {}; command {}; status {}", new Object[] {response.getFrameId(), response.getCommand(), response.getStatus().toString()});
            if ((int)response.getFrameId() == (int)command.getFrameId())
            {
                this.response = response;
                try {
                    responseQueue.put(response);
                } catch (InterruptedException ex) {
                    logger.debug("Interrupted putting response to queue", ex);
                }
            }
        }

        public AtCommandResponse call() throws Exception {
            try {
                xbeeService.addListener(this);
                xbeeService.sendCommand(command);
                return responseQueue.take();
            } finally {
                xbeeService.removeListener(this);
            }
        }



    }
}
