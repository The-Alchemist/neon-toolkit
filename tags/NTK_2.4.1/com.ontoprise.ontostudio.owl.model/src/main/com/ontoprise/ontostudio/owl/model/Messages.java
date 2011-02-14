/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;


public class Messages  extends NLS {

    private static final String BUNDLE_NAME = "com.ontoprise.ontostudio.owl.model.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String OWLManchasterProject_OntologyCantBeSet;
    public static String OWLManchasterProject_OntologyCantBeSet_NewOID;
    public static String OWLManchasterProject_OntologyCantBeSet_Onto;
    public static String OWLManchasterProject_OntologyCantBeSet_NewOID_Onto;

    
    private Messages() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    
    static {
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
