
package com.pigglogic.phomenet.xbeeswitch.impl;

import java.util.ArrayList;
import java.util.Iterator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link TemperatureLimitSwitch}
 */
@Test(enabled=false)
public class TemperatureLimitSwitchTest {

    public void testSwitch(final Integer setPoint, final Integer current, final SwitchState curentState, final SwitchState expected, final String description)
    {

    }

    @DataProvider(name="switchData")
    public Iterator<Object[]> switchTestData()
    {
        final ArrayList<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[] {25, 26, SwitchState.OFF, SwitchState.ON, "Transition from off to on when above set point" });
        data.add(new Object[] {25, 25, SwitchState.OFF, SwitchState.OFF, "Remain off when at set point" });
        data.add(new Object[] {25, 25, SwitchState.ON, SwitchState.OFF, "Transition to off when at set point" });
        data.add(new Object[] {25, 26, SwitchState.ON, SwitchState.ON, "Remain on when above set point" });
        return data.iterator();
    }

}
