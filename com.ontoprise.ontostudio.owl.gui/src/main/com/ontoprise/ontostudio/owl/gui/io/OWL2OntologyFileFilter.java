/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package com.ontoprise.ontostudio.owl.gui.io;

import java.io.File;

import org.neontoolkit.io.filter.FileFilter;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;

/* 
 * Created on 18.5.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: FileFilter, Ontology, Format
 */

/**
 * Provides information for filtering ontology files from a directory.
 */
public class OWL2OntologyFileFilter extends FileFilter {
    
    private static final String WILDCARD_EXTENSIONS = "*.owl;*.owl2;*.rdf;*.owlx"; //$NON-NLS-1$
    private static final String OWL_EXTENSION = ".owl"; //$NON-NLS-1$
    private static final String OWL2_EXTENSION = ".owl2"; //$NON-NLS-1$
    private static final String RDF_EXTENSION = ".rdf"; //$NON-NLS-1$
    private static final String OWLX_EXTENSION = ".owlx"; //$NON-NLS-1$

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
        		OWL_EXTENSION,
                OWL2_EXTENSION,
                RDF_EXTENSION,
                OWLX_EXTENSION,
        		};
    }
    
    @Override
    public String getDefaultExtension() {
        return OWL_EXTENSION;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return Messages.OWL2OntologyFileFilter_0;
    }

    @Override
    public boolean accept(File f) {
        String[] extensions = getExtensions();
        boolean accept = false;
        for (int i = 0; i < extensions.length; i++) {
            if ((f.getName().toLowerCase().indexOf(extensions[i]) > -1) || f.isDirectory()) {
                accept = true;
            }
        }
        return accept;
    }
    
    @Override
    public String getOntologyFileFormat() {
    	return OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE;
    }
    @Override
    public String getWarning() {
    	return ""; //$NON-NLS-1$
    }
    
    @Override
    public String getOntologyLanguage() {
        return OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE;
    }
}
