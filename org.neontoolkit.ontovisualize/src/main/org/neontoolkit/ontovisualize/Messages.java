/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.ontovisualize;

import org.eclipse.osgi.util.NLS;

/* 
 * Created on: 16.11.2004
 * Created by: Dirk Wenke
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Dirk Wenke
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.neontoolkit.ontovisualize.messages";//$NON-NLS-1$

    public static String OntologyNode_0;
    public static String OntoVisualizerPreferencePage_0;

    public static String OntoVisualizerPreferencePage_1;
    public static String OntoVisualizerPreferencePage_3;
    public static String OntoVisualizerPreferencePage_4;
    public static String OntoVisualizerPreferencePage_5;
    public static String OntoVisualizerLegendPainter_0;
    public static String OntoVisualizerView2_8;
    public static String OntoVisualizerView2_11;
    public static String OntoVisualizerView2_15;
    public static String OntoVisualizerView2_28;
    public static String OntoVisualizerView2_1;
    public static String OntoVisualizerView2_2;
    public static String OntoVisualizerView2_27;
    public static String OntoVisualizerView2_29;

    static {
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}