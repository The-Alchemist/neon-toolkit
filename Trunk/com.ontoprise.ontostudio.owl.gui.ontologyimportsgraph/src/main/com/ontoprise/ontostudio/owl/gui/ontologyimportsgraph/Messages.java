/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.ontologyimportsgraph;

import org.eclipse.osgi.util.NLS;

/**
 * @author werner
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.ontoprise.ontostudio.owl.gui.ontologyimportsgraph.messages"; //$NON-NLS-1$
    public static String OntologyImportsGraphPropertyPage_0;
    public static String OntologyImportsGraphPropertyPage_1;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
