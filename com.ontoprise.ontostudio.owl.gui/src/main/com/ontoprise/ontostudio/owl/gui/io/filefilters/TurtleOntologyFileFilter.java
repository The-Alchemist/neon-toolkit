/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.io.filefilters;

import com.ontoprise.ontostudio.owl.model.OWLConstants;

/**
 * Provides information for filtering ontology files from a directory.
 */
public class TurtleOntologyFileFilter extends AbstractOWLFileFilter {
    
    private static final String WILDCARD_EXTENSIONS = "*" + OWLConstants.TURTLE_EXTENSION;  //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.FileFilter#getExtension()
     */
    @Override
    public String getExtension() {
        return WILDCARD_EXTENSIONS; 
    }

    @Override
    public String[] getExtensions() {
        return new String[] {
                OWLConstants.TURTLE_EXTENSION
            };
    }
    
    @Override
    public String getDefaultExtension() {
        return OWLConstants.TURTLE_EXTENSION;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return "Turtle Syntax ( " + WILDCARD_EXTENSIONS + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
