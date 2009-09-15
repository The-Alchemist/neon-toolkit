/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io.filter;

import java.io.File;

/* 
 * Created on 4.05.2002
 * Created by Dirk Wenke
 *
 * Keywords: FileFilter, Ontology, Import, Esport
 */


/**
 * An extenstion of the javax.swing.filechooser.FileFilter. Additional the
 * extension of the accepted files can be specified.
 */
public abstract class FileFilter extends javax.swing.filechooser.FileFilter {

    /**
     * FileFilter - Konstruktorkommentar.
     */
    public FileFilter() {
        super();
    }

    /**
     * Returns the extension of the filename (like "*.oxml") of the accepted
     * files (including the wildcard character "*" and the dot ".").
     * If multiple extensions are allowed, a String like "*.rdf;*.rdfs" 
     * should be returned.
     * 
     * @return java.lang.String
     */
    public abstract String getExtension();
    
    /**
     * Returns a String[] representation of all allowed extensions. Use this for 
     * the export dialog and check for each of the extensions to add it if not 
     * present! this method should return the extensions WITHOUT the wildcard "*".
     * 
     * @return
     */
    public abstract String[] getExtensions();
    
    /**
     * Need this for filters allowing multiple extensions: if no extension is 
     * provided, append the default extension. For multiple extensions I assume
     * the first one is the default.
     * 
     * @return
     */
    public abstract String getDefaultExtension();

    /*
     * @see FileFilter#accept(File)
     */
    @Override
    public boolean accept(File f) {
        if (f.getName().toLowerCase().endsWith(getDefaultExtension().toString()) || f.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }
    
    public abstract String getOntologyFileFormat();
    
    public abstract String getWarning();
    
    public abstract String getOntologyLanguage();
    
    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass();
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
}
