/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 05.02.2009
 * Created by: josp
 ******************************************************************************/
package org.neontoolkit.gui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.PluginTestDatamodel;

/**
 * This class should be called to handle all problems for plugin tests on build server
 * 
 * @author josp
 */
@SuppressWarnings("nls")
public class PluginTest {
    private static boolean DISPLAY_PROBLEM;
    private static boolean WORKBENCH_PROBLEM;
    private static final boolean IS_PLUGIN_TEST_IDE;
    //
    static {
        try {
            Display.getDefault();
            DISPLAY_PROBLEM = false;
        } catch (Throwable ex) {
            DISPLAY_PROBLEM = true;
        }
        try {
            PlatformUI.getWorkbench();
            WORKBENCH_PROBLEM = false;
        } catch (Throwable ex) {
            WORKBENCH_PROBLEM = true;
        }
        if (PluginTestDatamodel.IS_BUILD_SERVER) {
            System.err.println("PluginTest hasDisplay  = " + !DISPLAY_PROBLEM);
            System.err.println("PluginTest hasWorkbench= " + !WORKBENCH_PROBLEM);
        }

        List<String> argList = Arrays.asList(Platform.getCommandLineArgs());
        IS_PLUGIN_TEST_IDE = argList.contains("org.eclipse.jdt.junit4.runtime") //
                && argList.contains("org.eclipse.pde.junit.runtime.coretestapplication");
        if (isPluginTest()) {
            System.err.println("PluginTest isPluginTest= true");
        }
    }

    public static boolean hasWorkbench() {
        return !WORKBENCH_PROBLEM;
    }

    public static boolean isPluginTest() {
        return PluginTestDatamodel.IS_BUILD_SERVER || isPluginTestEclipseIDE();
    }

    private static boolean isPluginTestEclipseIDE() {
        return IS_PLUGIN_TEST_IDE;
    }
}
// -----------------------------------------------------------------------------
// ONTOSTUDIO
// -----------------------------------------------------------------------------
// commandLineArgs=2
// -product
// com.ontoprise.ontostudio.plugin.ide
// -----------------------------------------------------------------------------
// PLUGIN TESTS IDE
// -----------------------------------------------------------------------------
// commandLineArgs=14 ----------------------------------------------------------
// -version
// 3
// -port
// 2823
// -testLoaderClass
// org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader
// -loaderpluginname
// org.eclipse.jdt.junit4.runtime
// -classNames
// com.ontoprise.ontostudio.plugintests.AllPluginTests
// -application
// org.eclipse.pde.junit.runtime.coretestapplication
// -testpluginname
// com.ontoprise.ontostudio.plugintests

