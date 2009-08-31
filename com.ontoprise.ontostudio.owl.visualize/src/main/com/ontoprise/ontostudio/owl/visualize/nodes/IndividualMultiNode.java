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

public class IndividualMultiNode extends IndividualNode {

    @Override
    public String getNodeType() {
        return Messages.IndividualMultiNode_0;
    }
    
    public IndividualMultiNode() {
        super();
        setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL));
    }

    public IndividualMultiNode(OWLEntity entity, String ontologyId, String projectId) {
        super(entity, ontologyId, projectId);
        setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL));
    }
    
    @Override
    public Object create() throws CoreException {
        return new IndividualMultiNode();
    }

}
