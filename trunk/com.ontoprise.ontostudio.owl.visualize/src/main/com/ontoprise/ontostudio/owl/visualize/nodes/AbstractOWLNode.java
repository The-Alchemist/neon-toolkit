/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.visualize.nodes;

import org.eclipse.core.runtime.CoreException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;

public class AbstractOWLNode extends LabelImageNode {

    private OWLEntity _entity;

    public AbstractOWLNode(OWLEntity entity, String ontologyUri, String projectId, boolean isRootNode) {
        super(entity.getURI().toString(), ontologyUri, projectId, isRootNode);
        _entity = entity;
    }

    public AbstractOWLNode() {
    }

    public AbstractOWLNode(OWLEntity entity, String ontologyUri, String projectId) {
        this(entity, ontologyUri, projectId, false);
    }

    @Override
    public String guiRepresentation() {
        try {
            return OWLGUIUtilities.getEntityLabel(_entity, getOntologyId(), getProjectId());
        } catch (NeOnCoreException e) {
            return _entity.getURI().toString();
        }
    }

    @Override
    public boolean canBeNavigatedTo() {
        return false;
    }

    public Object create() throws CoreException {
        return new AbstractOWLNode();
    }

    public OWLEntity getEntity() {
        return _entity;
    }
}
