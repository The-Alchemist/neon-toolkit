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

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLConstants;

/**
 * Provides information for filtering ontology files from a directory.
 */
public class AnyOWL2OntologyFileFilter extends AbstractOWLFileFilter {
    
    private static final String WILDCARD_EXTENSIONS = getWildCards();
    
    @SuppressWarnings("nls")
    private static final String getWildCards() {
        return    "*"+OWLConstants.OWL_EXTENSION + 
        		"; *" + OWLConstants.RDF_EXTENSION + 
        		"; *" + OWLConstants.OWLXML_EXTENSION + 
        		"; *" + OWLConstants.MANCHESTER_SYNTAX_EXTENSION + 
        		"; *" + OWLConstants.FUNCTIONAL_SYNTAX_EXTENSION + 
        		"; *" + OWLConstants.TURTLE_EXTENSION;
    }

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
                OWLConstants.OWLXML_EXTENSION,
                OWLConstants.MANCHESTER_SYNTAX_EXTENSION,
                OWLConstants.FUNCTIONAL_SYNTAX_EXTENSION,
                OWLConstants.TURTLE_EXTENSION
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
        return Messages.OWL2OntologyFileFilter_0 + "( " + getExtension()+")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
