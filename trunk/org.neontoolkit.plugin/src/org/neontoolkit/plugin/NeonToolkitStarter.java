/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.plugin;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * This class gets information from the following files:
 * <ul>
 * <li><b>productName</b>: file /com.ontoprise.ontostudio.plugin/plugin.properties, key "productName" is used</li>
 * <li><b>version</b>: file /com.ontoprise.ontostudio.plugin/META-INF/MANIFEST.MF, key "Bundle-Version" is used</li>
 * </ul>
 * This information is necessary to create an fingerprint like "OntoStudio 2.3.0-B1001"
 * <p>
 * Note: NeOnToolKit changes the mentioned files above!
 * <p>
 * See also <a href="http://buggy.ontoprise.de/bugs/show_bug.cgi?id=9936"> Issue 9936 - Serialized ontologies should indicate the tool and version with which
 * they were created</a> See also:
 * <ul>
 * <li>Issue 8263</li>
 * <li><code>FlogicUIPlugin.getLicenseString()<code></li>
 * <li>/com.ontoprise.ontostudio.plugin/about.mappings</li>
 * </ul>
 *
 * @author Joerg Spieler
 * @created 2009-01-20
 */
public class NeonToolkitStarter implements IStartup {

    public void earlyStartup() {
        setupLog4j();
        String productName = null;
        IProduct product = Platform.getProduct();
        if (product != null) {
            productName = product.getName();
        }
        //
        String version = null;
        Bundle bundle = Platform.getBundle("org.neontoolkit.plugin"); //$NON-NLS-1$
        if (bundle != null && bundle.getHeaders() != null) {
            version = String.valueOf(bundle.getHeaders().get(Constants.BUNDLE_VERSION));
        }
        // e.g. NeOnToolkit 2.3.0-B149
        String message = String.format("Starting '%s' %s-%s", productName, version, RegisterTypeData.BUILD_ID); //$NON-NLS-1$
        Logger.getLogger("NeonToolkit").info(message); //$NON-NLS-1$
        // See: /org.neontoolkit.plugin/about.mappings
        System.setProperty("neon.BUILD_ID", RegisterTypeData.BUILD_ID); //$NON-NLS-1$
    }

    private static void setupLog4j() {
        URL installUrl = null;
        try {
            URL installUrlOriginal = Platform.getInstallLocation().getURL();
            installUrl = new URL(installUrlOriginal.toString().replaceAll(" ", "%20")); //$NON-NLS-1$//$NON-NLS-2$
            PropertyConfigurator.configure(new URL(installUrl, "log4j.properties")); //$NON-NLS-1$
        } catch (Exception e) {
            System.err.println("Problems setting log4j.properties (" + installUrl + "): " + e); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
