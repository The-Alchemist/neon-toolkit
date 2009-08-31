/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package com.ontoprise.ontostudio.owl.visualize.nodes;

import org.eclipse.core.runtime.CoreException;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.visualize.Messages;

/*
 * Created by Werner Hihn
 */

public class IndividualNode extends AbstractOWLEntityNode {

    private String _propertyUri;
    
    public IndividualNode(OWLEntity entity, String ontologyId, String projectId){
    	super(entity, ontologyId, projectId);
        setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL));
    }
    
    public IndividualNode(OWLEntity entity, String propertyUri, String ontologyId, String projectId) {
        this(entity, ontologyId, projectId);
        _propertyUri = propertyUri;
    }

    public IndividualNode() {
        super();
        setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL));
    }
    
    @Override
    public String getInternalId() {
        if (_propertyUri == null) {
            return getId();
        } else {
            return concat(_propertyUri ,super.getInternalId());
        }
    }

    @Override
    public String getNodeType(){
        return Messages.IndividualNode_0;
    }

    @Override
    public Object create() throws CoreException {
        return new IndividualNode();
    }

    @Override
    public boolean canBeNavigatedTo() {
        return false;
    }
    
    @Override
    public String toString() {
        return getInternalId();
    }

}
