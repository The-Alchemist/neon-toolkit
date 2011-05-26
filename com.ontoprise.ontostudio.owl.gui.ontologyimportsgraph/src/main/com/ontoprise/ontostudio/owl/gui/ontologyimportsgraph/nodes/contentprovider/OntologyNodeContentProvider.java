/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.ontologyimportsgraph.nodes.contentprovider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.ontovisualize.AbstractNodeContentProvider;
import org.neontoolkit.ontovisualize.OntologyGraph;
import org.neontoolkit.ontovisualize.edges.OntoStudioDefaultEdge;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;

import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.ontologyimportsgraph.OntologyImportsGraphConfiguration;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.visualize.nodes.OntologyNode;
/**
 * 
 * @author Nico Stieler
 */
public class OntologyNodeContentProvider extends AbstractNodeContentProvider {

    public static final int IMPORTED = 1;
    public static final int IMPORTING = 2;
    public static final int ALL = 3;
    public static int SELECTED = 3;
    
    public OntologyNodeContentProvider() {
        super("OWLOntologyImports"); //$NON-NLS-1$
    }

    @Override
    public Object create() throws CoreException {
        return new OntologyNodeContentProvider();
    }

    @Override
    public OntologyGraph getOntologyGraph(AbstractOntologyTreeElement element, int hierarchyLevel) throws NeOnCoreException {
        OntologyGraph graph = new OntologyGraph();
        List<LabelImageNode> nodes = new ArrayList<LabelImageNode>();
        List<OntoStudioDefaultEdge> edges = new ArrayList<OntoStudioDefaultEdge>();
        String projectId = element.getProjectName();
        if (element instanceof OntologyTreeElement) {
            String ontologyUri = ((OntologyTreeElement) element).getOntologyUri();
            LabelImageNode node = getNode(ontologyUri, ontologyUri, projectId, OntologyImportsGraphConfiguration.ONTOLOGY_TYPE, _ontologyLanguage);
            Set<String> usedImportedOntos = new HashSet<String>();
            Set<String> usedImportingOntos = new HashSet<String>();
            
            if((SELECTED & 1) == 1)addImportedOntos(projectId, nodes, edges, node, usedImportedOntos);
            if((SELECTED & 2) == 2)addImportingOntos(projectId, nodes, edges, node, usedImportingOntos);

            node.setIsRoot(true);
            node.setFixed(true);
            addNode(node, nodes);
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
        if (node instanceof OntologyNode) {
            Set<String> usedImportedOntos = new HashSet<String>();
            Set<String> usedImportingOntos = new HashSet<String>();
            addImportedOntos(node.getProjectId(), nodes, edges, node, usedImportedOntos);
            addImportingOntos(node.getProjectId(), nodes, edges, node, usedImportingOntos);

            node.setIsRoot(true);
            node.setFixed(true);
            addNode(node, nodes);
        }
        graph.addNodes(nodes);
        graph.addEdges(edges);
        return graph;
    }

    private void addImportedOntos(String projectName, List<LabelImageNode> nodes, List<OntoStudioDefaultEdge> edges, LabelImageNode node, Set<String> usedOntos) {
        try {
            Set<OWLModel> importedOntos = OWLModelFactory.getOWLModel(node.getOntologyId(), projectName).getImportedOntologies();
            if (!usedOntos.contains(node.getOntologyId())) {
                usedOntos.add(node.getOntologyId());
            } else {
                return;
            }
            for (OWLModel onto: importedOntos) {
                LabelImageNode subNode = getNode(onto.getOntologyURI(), onto.getOntologyURI(), projectName, OntologyImportsGraphConfiguration.ONTOLOGY_TYPE, _ontologyLanguage);
                addImportedOntos(projectName, nodes, edges, subNode, usedOntos);
                addNode(subNode, nodes);
                OntoStudioDefaultEdge edge = new OntoStudioDefaultEdge(node, subNode);
                addEdge(edge, edges);
            }
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e.getMessage(), e, new Shell());
        }
    }
    private void addImportingOntos(String projectName, List<LabelImageNode> nodes, List<OntoStudioDefaultEdge> edges, LabelImageNode node, Set<String> usedOntos) {
        try {
            Set<OWLModel> importingOntos = OWLModelFactory.getOWLModel(node.getOntologyId(), projectName).getImportingOntologies();
            if (!usedOntos.contains(node.getOntologyId())) {
                usedOntos.add(node.getOntologyId());
            } else {
                return;
            }
            for (OWLModel onto: importingOntos) {
                LabelImageNode superNode = getNode(onto.getOntologyURI(), onto.getOntologyURI(), projectName, OntologyImportsGraphConfiguration.ONTOLOGY_TYPE, _ontologyLanguage);
                addImportingOntos(projectName, nodes, edges, superNode, usedOntos);
                addNode(superNode, nodes);
                OntoStudioDefaultEdge edge = new OntoStudioDefaultEdge(superNode, node);
                addEdge(edge, edges);
            }
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e.getMessage(), e, new Shell());
        }
    }

}
