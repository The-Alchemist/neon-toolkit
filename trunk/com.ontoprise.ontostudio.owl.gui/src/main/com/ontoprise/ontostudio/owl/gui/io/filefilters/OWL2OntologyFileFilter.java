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

/* 
 * Created on 18.5.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: FileFilter, Ontology, Format
 */

/**
 * Provides information for filtering ontology files from a directory.
 */
public class OWL2OntologyFileFilter extends AbstractOWLFileFilter {
    
    private static final String WILDCARD_EXTENSIONS = 
    	        "*" + OWLConstants.OWL_EXTENSION //$NON-NLS-1$ 
            + "; *" + OWLConstants.RDF_EXTENSION //$NON-NLS-1$  
            + "; *" + OWLConstants.RDFS_EXTENSION; //$NON-NLS-1$  

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
                OWLConstants.OWL_EXTENSION,
                OWLConstants.RDF_EXTENSION,
                OWLConstants.RDFS_EXTENSION
            };
    }
    
    @Override
    public String getDefaultExtension() {
        return OWLConstants.OWL_EXTENSION;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return "OWL2 in RDF/XML ( " + WILDCARD_EXTENSIONS + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
