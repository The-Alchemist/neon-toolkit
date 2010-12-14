/*****************************************************************************
 * Copyright (c) 2010 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.io.filefilters;

import java.io.File;

import org.neontoolkit.io.filter.FileFilter;

import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;

/**
 * Provides information for filtering ontology files from a directory.
 */
public abstract class AbstractOWLFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        String[] extensions = getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            if ((f.getName().toLowerCase().endsWith(extensions[i])) || f.isDirectory()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getOntologyFileFormat() {
    	return getDefaultExtension();
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
