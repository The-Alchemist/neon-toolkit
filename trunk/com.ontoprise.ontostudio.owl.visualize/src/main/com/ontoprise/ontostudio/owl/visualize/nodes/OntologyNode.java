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
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.ontovisualize.Messages;
import org.neontoolkit.ontovisualize.OntovisualizePlugin;
import org.neontoolkit.ontovisualize.VisualizerSharedImages;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;


/*
 * Created by Werner Hihn
 */

public class OntologyNode extends AbstractOWLEntityNode {

    public OntologyNode(String id, String projectId) {
        this(id, projectId, false);
    }

    public OntologyNode(String id, String projectId, boolean isRoot) {
        super(null, id, projectId, isRoot);
        setImage(OntovisualizePlugin.getDefault().getImageRegistry().get(VisualizerSharedImages.ONTOLOGY));
    }

    public OntologyNode() {
        super();
        setImage(OntovisualizePlugin.getDefault().getImageRegistry().get(VisualizerSharedImages.ONTOLOGY));
    }

    @Override
    public String getNodeType() {
        return Messages.OntologyNode_0;
    }

    @Override
    public String guiRepresentation() {
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        String result = getOntologyId();
        if (idDisplayStyle == NeOnUIPlugin.DISPLAY_LOCAL) {
            result = OWLNamespaces.guessLocalName(result);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IExecutableExtensionFactory#create()
     */
    @Override
    public Object create() throws CoreException {
        return new OntologyNode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.ontovisualize.nodes.LabelImageNode#canBeNavigatedTo()
     */
    @Override
    public boolean canBeNavigatedTo() {
        return true;
    }

}
