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
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.visualize.Messages;

/*
 * Created by Werner Hihn
 */

public class ClazzNode extends AbstractOWLEntityNode {

    public ClazzNode(OWLEntity entity, String ontologyId, String projectId, boolean isRoot) {
        super(entity, ontologyId, projectId, isRoot);
        setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ));
    }

    public ClazzNode() {
        super();
        setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ));
    }

    public ClazzNode(OWLEntity entity, String ontologyId, String projectId) {
        this(entity, ontologyId, projectId, false);
    }

    @Override
    public String getNodeType() {
        return Messages.ClazzNode_0;
    }

    @Override
    public Object create() throws CoreException {
        return new ClazzNode();
    }

    @Override
    public boolean canBeNavigatedTo() {
        return true;
    }

}
