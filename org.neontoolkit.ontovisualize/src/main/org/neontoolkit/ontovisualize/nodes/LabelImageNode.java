/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.ontovisualize.nodes;

import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.neontoolkit.jpowergraph.nodes.DefaultImageNode;


/*
 * Created by Werner Hihn
 */

/**
 * This is an abstract implementation of a node consisting of an image and a label. Additionally we store if it is a root node, because the background color of
 * root nodes' labels is different (dark grey). This holds for real root nodes in the ontology and for the node the user just clicked on.
 * 
 */
public abstract class LabelImageNode extends DefaultImageNode implements IExecutableExtensionFactory {

    private boolean _isRoot = false;
    private boolean _isImported = false;
    private String _id;
    private String _projectId;
    private String _ontologyId;

    public LabelImageNode(String id, String ontologyId, String projectId, boolean isRoot) {
        super();
        _isRoot = isRoot;
        _id = id;
        _ontologyId = ontologyId;
        _projectId = projectId;
    }

    public LabelImageNode(String id, String ontologyId, String projectId) {
        this(id, ontologyId, projectId, false);
    }

    /**
     * called from framework when createExecutableExtension() is processed
     */
    public LabelImageNode() {
        super();
    }

    @Override
    public String toString() {
        return _id;
    }

    @Override
    public boolean isRoot() {
        return _isRoot;
    }

    public void setIsRoot(boolean isRoot) {
        _isRoot = isRoot;
    }

    public void setId(String id) {
        _id = id;
    }
    
    public void setImported(boolean isImported) {
        _isImported = isImported;
    }
    
    public boolean isImported() {
        return _isImported;
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 == null || !(arg0 instanceof LabelImageNode)) {
            return false;
        }
        return ((LabelImageNode) arg0).getId().equals(getId()) && ((LabelImageNode) arg0).getOntologyId().equals(getOntologyId()) && ((LabelImageNode) arg0).getProjectId().equals(getProjectId());
    }

    @Override
    public int hashCode() {
        int hash = getId().hashCode();
        hash += _ontologyId != null ? _ontologyId.hashCode() : 0;
        hash += _projectId != null ? _projectId.hashCode() : 0;
        return hash;
    }

    /**
     * Return qualified id with namespace & local part if showNamespaces is on, else only the local part.
     */
    public abstract String guiRepresentation();

    @Override
    public String getLabel() {
        return guiRepresentation();
    }

    public String getId() {
        return _id;
    }
    
    public String getInternalId() {
        return _id;
    }

    public String getOntologyId() {
        return _ontologyId;
    }

    public String getProjectId() {
        return _projectId;
    }

    /**
     * Concatenation of 2 strings in the form "attributeName :: range". I use this to create a unique ID for nodes which are displayed multiple times, e.g. for
     * inherited attributes.
     */
    public static String concat(String attributeName, String range) {
        return attributeName + " :: " + range; //$NON-NLS-1$
    }

    public abstract boolean canBeNavigatedTo();
}
