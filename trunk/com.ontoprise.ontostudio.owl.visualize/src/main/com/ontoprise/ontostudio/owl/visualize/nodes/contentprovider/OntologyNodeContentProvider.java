/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.visualize.nodes.contentprovider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.ontovisualize.AbstractNodeContentProvider;
import org.neontoolkit.ontovisualize.OntologyGraph;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;

import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.visualize.VisualizerConfiguration;
import com.ontoprise.ontostudio.owl.visualize.nodes.OntologyNode;

public class OntologyNodeContentProvider extends AbstractNodeContentProvider {

    public OntologyNodeContentProvider() {
        super(OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE);
    }

    public Object create() throws CoreException {
        return new OntologyNodeContentProvider();
    }

    public OntologyGraph getOntologyGraph(AbstractOntologyTreeElement element, int hierarchyLevel) throws NeOnCoreException {
        OntologyGraph graph = new OntologyGraph();
        List<LabelImageNode> nodes = new ArrayList<LabelImageNode>();
        String projectId = element.getProjectName();
        if (element instanceof OntologyTreeElement) {
            String moduleId = ((OntologyTreeElement) element).getOntologyUri();
            LabelImageNode node = getNode(moduleId, moduleId, projectId, VisualizerConfiguration.ONTOLOGY_TYPE, _ontologyLanguage);
            node.setFixed(true);
            node.setIsRoot(true);
            addNode(node, nodes);
        }
        graph.addNodes(nodes);
        return graph;
    }

    @Override
    public OntologyGraph getOntologyGraph(LabelImageNode node, int hierarchyLevel) throws NeOnCoreException {
        OntologyGraph graph = new OntologyGraph();
        List<LabelImageNode> nodes = new ArrayList<LabelImageNode>();
        if (node instanceof OntologyNode) {
            addNode(node, nodes);
        }
        graph.addNodes(nodes);
        return graph;
    }

}
