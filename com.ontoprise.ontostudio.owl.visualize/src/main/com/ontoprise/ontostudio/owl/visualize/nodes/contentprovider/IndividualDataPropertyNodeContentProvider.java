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
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.ontovisualize.AbstractNodeContentProvider;
import org.neontoolkit.ontovisualize.OntologyGraph;
import org.neontoolkit.ontovisualize.edges.OntoStudioDefaultEdge;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.individual.GetIndividuals;
import com.ontoprise.ontostudio.owl.visualize.VisualizerConfiguration;
import com.ontoprise.ontostudio.owl.visualize.edges.IndividualEdge;
import com.ontoprise.ontostudio.owl.visualize.nodes.ClazzNode;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public class IndividualDataPropertyNodeContentProvider extends AbstractNodeContentProvider {

    public IndividualDataPropertyNodeContentProvider() {
        super(OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.ontovisualize.INodeContentProvider#getOntologyGraph(org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement,
     * int)
     */
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

            addInstancePropertyValues(projectId, ontologyUri, nodes, edges, clazz, hierarchyLevel);
        } else {
            return graph;
        }
        graph.addNodes(nodes);
        graph.addEdges(edges);
        return graph;
    }

    /**
     * @param projectId
     * @param ontologyUri
     * @param nodes
     * @param edges
     * @param clazz
     * @param hierarchyLevel
     * @throws NeOnCoreException
     * @throws ControlException 
     */
    private void addInstancePropertyValues(String projectId, String ontologyUri, List<LabelImageNode> nodes, List<OntoStudioDefaultEdge> edges, OWLEntity clazz, int hierarchyLevel) throws NeOnCoreException, NeOnCoreException {
        try {
            OWLModel owlModel = OWLModelFactory.getOWLModel(ontologyUri, projectId);
            OWLOntology ontology = owlModel.getOntology();
            String[] individualUris = new GetIndividuals(projectId, ontologyUri, clazz.getIRI().toString()).getResults();
            if (individualUris.length > 100) {
                return;
            }
            for (String individualUri: individualUris) {
                OWLNamedIndividual individual = OWLModelFactory.getOWLDataFactory(projectId).getOWLNamedIndividual(OWLUtilities.toIRI(individualUri));
                Set<LocatedItem<OWLDataPropertyAssertionAxiom>> objMem = owlModel.getDataPropertyMemberHits(individual.getIRI().toString());
                for (LocatedItem<OWLDataPropertyAssertionAxiom> mem: objMem) {
                    OWLDataPropertyAssertionAxiom member = mem.getItem();
                    OWLDataPropertyExpression dataProperty = member.getProperty();
                    OWLLiteral targetValue = member.getObject();
    
                    LabelImageNode sourceInstanceNode = getNode(individual.getIRI().toString(), ontologyUri, projectId, VisualizerConfiguration.INDIVIDUAL_TYPE, _ontologyLanguage);
                    LabelImageNode targetValueNode = getNode(OWLUtilities.toString(targetValue, ontology), ontologyUri, projectId, VisualizerConfiguration.DATA_TYPE, _ontologyLanguage);
                    LabelImageNode dataPropertyNode = getNode(OWLUtilities.toString(dataProperty, ontology), ontologyUri, projectId, VisualizerConfiguration.DATA_PROPERTY_TYPE, _ontologyLanguage);
                    addNode(targetValueNode, nodes);
                    addNode(dataPropertyNode, nodes);
                    addEdge(new IndividualEdge(targetValueNode, dataPropertyNode), edges);
                    addEdge(new IndividualEdge(dataPropertyNode, sourceInstanceNode), edges);
                }
            }
        } catch (CommandException ce) {
            throw new InternalNeOnException(ce);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.ontovisualize.INodeContentProvider#getOntologyGraph(org.neontoolkit.ontovisualize.nodes.LabelImageNode, int)
     */
    @Override
    public OntologyGraph getOntologyGraph(LabelImageNode node, int hierarchyLevel) throws NeOnCoreException {
        OntologyGraph graph = new OntologyGraph();
        List<LabelImageNode> nodes = new ArrayList<LabelImageNode>();
        List<OntoStudioDefaultEdge> edges = new ArrayList<OntoStudioDefaultEdge>();
        String ontologyUri = node.getOntologyId();
        String projectId = node.getProjectId();

        if (node instanceof ClazzNode) {
            OWLEntity clazz = ((ClazzNode) node).getEntity();
            addInstancePropertyValues(projectId, ontologyUri, nodes, edges, clazz, hierarchyLevel);
        }
        graph.addNodes(nodes);
        graph.addEdges(edges);
        return graph;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IExecutableExtensionFactory#create()
     */
    @Override
    public Object create() throws CoreException {
        return new IndividualDataPropertyNodeContentProvider();
    }

}
