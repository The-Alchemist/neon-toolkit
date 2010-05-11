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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.ontovisualize.AbstractNodeContentProvider;
import org.neontoolkit.ontovisualize.OntologyGraph;
import org.neontoolkit.ontovisualize.edges.OntoStudioDefaultEdge;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetIndividuals;
import com.ontoprise.ontostudio.owl.visualize.Messages;
import com.ontoprise.ontostudio.owl.visualize.VisualizerConfiguration;
import com.ontoprise.ontostudio.owl.visualize.edges.IndividualEdge;
import com.ontoprise.ontostudio.owl.visualize.nodes.ClazzNode;

public class IndividualNodeContentProvider extends AbstractNodeContentProvider {

    public IndividualNodeContentProvider() {
        super(OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE);
    }

    public Object create() throws CoreException {
        return new IndividualNodeContentProvider();
    }

    @Override
    public OntologyGraph getOntologyGraph(AbstractOntologyTreeElement element, int hierarchyLevel) throws NeOnCoreException {
        OntologyGraph graph = new OntologyGraph();

        String projectId = element.getProjectName();
        String ontologyUri;
        List<LabelImageNode> nodes = new ArrayList<LabelImageNode>();
        List<OntoStudioDefaultEdge> edges = new ArrayList<OntoStudioDefaultEdge>();

        if (element instanceof ClazzTreeElement) {
            ontologyUri = ((ClazzTreeElement) element).getOntologyUri();
            OWLEntity clazz = ((ClazzTreeElement) element).getEntity();

            addInstances(projectId, ontologyUri, nodes, edges, clazz, hierarchyLevel);
        } else {
            return graph;
        }
        graph.addNodes(nodes);
        graph.addEdges(edges);
        return graph;
    }

    @Override
    public OntologyGraph getOntologyGraph(LabelImageNode node, int hierarchyLevel) throws NeOnCoreException {
        OntologyGraph graph = new OntologyGraph();
        List<LabelImageNode> nodes = new ArrayList<LabelImageNode>();
        List<OntoStudioDefaultEdge> edges = new ArrayList<OntoStudioDefaultEdge>();
        String moduleId = node.getOntologyId();
        String projectId = node.getProjectId();

        if (node instanceof ClazzNode) {
            addInstances(projectId, moduleId, nodes, edges, ((ClazzNode) node).getEntity(), hierarchyLevel);
        }
        graph.addNodes(nodes);
        graph.addEdges(edges);
        return graph;
    }

    private void addInstances(String projectId, String moduleId, List<LabelImageNode> nodes, List<OntoStudioDefaultEdge> edges, OWLEntity clazz, int hierarchyLevel) throws NeOnCoreException {
        try {
            String[] individualUris = new GetIndividuals(projectId, moduleId, clazz.getIRI().toString()).getResults();
            LabelImageNode clazzNode = getNode(clazz.getIRI().toString(), moduleId, projectId, VisualizerConfiguration.CLAZZ_TYPE, _ontologyLanguage);
            if (individualUris.length > 100) {
                // show an error message and don't display instances if there are more than 100
                MessageDialog.openInformation(new Shell(), Messages.OntoVisualizerView2_27, Messages.OntoVisualizerView2_29 + Messages.OntoVisualizerView2_2 + individualUris.length + Messages.OntoVisualizerView2_1);
            } else if (individualUris.length > 10) {
                // group instances around an InstanceMultiNode if there are more than 10 instances
                LabelImageNode instanceMultiNode = getNode(Messages.IndividualNodeContentProvider_0, moduleId, projectId, VisualizerConfiguration.INDIVIDUAL_MULTI_TYPE, _ontologyLanguage);
                addNode(instanceMultiNode, nodes);
                addEdge(new IndividualEdge(instanceMultiNode, clazzNode), edges);
                for (String individualUri: individualUris) {
                    LabelImageNode individualNode = getNode(individualUri, moduleId, projectId, VisualizerConfiguration.INDIVIDUAL_TYPE, _ontologyLanguage);
                    addNode(individualNode, nodes);
                    addEdge(new IndividualEdge(individualNode, instanceMultiNode), edges);
                }
            } else {
                for (String individualUri: individualUris) {
                    LabelImageNode individualNode = getNode(individualUri, moduleId, projectId, VisualizerConfiguration.INDIVIDUAL_TYPE, _ontologyLanguage);
                    addNode(individualNode, nodes);
                    addEdge(new IndividualEdge(individualNode, clazzNode), edges);
                }
            }
        } catch (CommandException ce) {
            throw new InternalNeOnException(ce);
        }
    }

}
