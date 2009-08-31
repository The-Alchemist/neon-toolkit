/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 19.12.2008
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.visualize.nodes;

import org.eclipse.core.runtime.CoreException;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.visualize.Messages;

/**
 * @author werner
 *
 */
public class ObjectPropertyNode extends AbstractOWLEntityNode {

    public ObjectPropertyNode(OWLEntity entity, String ontologyId, String projectId, boolean isRoot) {
        super(entity, ontologyId, projectId, isRoot);
        setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY));
    }
    
    public ObjectPropertyNode() {
        super();
        setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY));
    }

    public ObjectPropertyNode(OWLEntity entity, String ontologyId, String projectId) {
        this(entity, ontologyId, projectId, false);
    }
    
    @Override
    public String getNodeType() {
        return Messages.ObjectPropertyNode_0;
    }

    @Override
    public Object create() throws CoreException {
        return new ObjectPropertyNode();
    }

    @Override
    public boolean canBeNavigatedTo() {
        return false;
    }
}
