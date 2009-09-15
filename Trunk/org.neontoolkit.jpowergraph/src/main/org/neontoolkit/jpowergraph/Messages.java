/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.jpowergraph;

import org.eclipse.osgi.util.NLS;

/*
 * Created by Werner Hihn
 */

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.neontoolkit.jpowergraph.messages"; //$NON-NLS-1$

    public static String OntoPowerGraphContextMenuListener_0;
    public static String OntoPowerGraphContextMenuListener_1;
    public static String NavigationHistoryPainter_0;
    public static String NavigationHistoryControlPanel_1;
    public static String NavigationHistoryControlPanel_0;
    public static String NavigationHistoryControlPanel_2;
    public static String NavigationHistoryControlPanel_3;
    public static String NavigationHistoryControlPanel_4;
    public static String NavigationHistoryControlPanel_5;
    public static String NavigationHistoryControlPanel_6;

    static {
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
