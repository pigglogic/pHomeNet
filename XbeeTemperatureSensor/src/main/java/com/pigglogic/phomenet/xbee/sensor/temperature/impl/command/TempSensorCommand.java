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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pigglogic.phomenet.xbee.sensor.temperature.impl.command;

import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.framework.BundleContext;

/**
 *
 * @author michaelpigg
 */
public class TempSensorCommand {

    private final BundleContext bundleContext;
    private final ConfigureTempSensorCommand configureTempSensorCommand;
    private final ListConfiguredSensors listConfiguredSensors;
    private final DeleteConfiguredSensor deleteConfiguredSensorCommand;
    
    public TempSensorCommand(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.configureTempSensorCommand = new ConfigureTempSensorCommand(bundleContext);
        this.listConfiguredSensors = new ListConfiguredSensors(bundleContext);
        this.deleteConfiguredSensorCommand = new DeleteConfiguredSensor(bundleContext);
        
    }

    @Descriptor("Add a temperature sensor")
    public void xtempadd(@Descriptor("XBee Address")String address, @Descriptor("Location name") String name,
            @Parameter(names={"-c"},absentValue="0.0") Double correction)
    {
        configureTempSensorCommand.execute(address, name, correction, System.out, System.err);
    }
    
    @Descriptor("Delete a temperature sensor")
    public void xtempdel(@Descriptor("XBee address") String address)
    {
        deleteConfiguredSensorCommand.execute(address, System.out, System.err);
    }

    @Descriptor("List temperature sensors")
    public void xtemp()
    {
        listConfiguredSensors.execute(System.out, System.err);
    }

}
