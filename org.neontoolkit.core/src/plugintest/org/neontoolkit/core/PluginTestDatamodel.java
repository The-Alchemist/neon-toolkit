/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 17.02.2009
 * Created by: josp
 ******************************************************************************/
package org.neontoolkit.core;

import java.text.DecimalFormat;

/**
 * @author josp
 * 
 */
public class PluginTestDatamodel {
    /**
     * @see /headless.build/build-reports.xml
     */
    public static final boolean IS_BUILD_SERVER //
    = System.getProperty("ontoprise.plugin.test.build.server") != null; //$NON-NLS-1$
    private static final DecimalFormat DOUBLE_FORMATTER = new DecimalFormat("0.00"); //$NON-NLS-1$
    private static final double ONE_MINUTE = 60000.0;
    private static long sleepTotal = 0;
    private static int sleepCount = 0;
    private static String lastTestClassName = null;
    private static int testNr = 0;

    /**
     * @param millis use 0, to switch of sleep and mark code as "maybe sleep"
     */
    public static void sleep(int millis) {
        if (millis == 0) {
            return;
        }
        sleepCount++;
        sleepTotal += millis;
        if (sleepCount % 10 == 0) {
            System.err.println("PluginTestDatamodel.sleep=" // //$NON-NLS-1$
                    + millis
                    + " millis\t total=" //$NON-NLS-1$
                    + DOUBLE_FORMATTER.format(sleepTotal / ONE_MINUTE)
                    + " minutes\t sleepCount=" //$NON-NLS-1$
                    + sleepCount);
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void logStartTest(Object testClass) {
        if (IS_BUILD_SERVER) {
            String testClassName = (testClass == null) ? "?" : testClass.getClass().getName(); //$NON-NLS-1$
            // Only log, when class name changes
            if (lastTestClassName == null || !lastTestClassName.equals(testClassName)) {
                testNr++;
                System.err.println(testNr + "\tStart:\t" + testClassName); //$NON-NLS-1$
            }
            lastTestClassName = testClassName;
        }
    }
}
