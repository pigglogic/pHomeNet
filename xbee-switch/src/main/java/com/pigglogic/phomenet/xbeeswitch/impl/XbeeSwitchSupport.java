/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pigglogic.phomenet.xbeeswitch.impl;

import net.michaelpigg.xbeelib.protocol.XbeeAddress;

/**
 *
 * @author michaelpigg
 */
public interface XbeeSwitchSupport {

    SwitchState getSwitchState(XbeeAddress address);

    void setSwitchState(XbeeAddress address, SwitchState newState);

}
