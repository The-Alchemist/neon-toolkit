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
import org.neontoolkit.ontovisualize.OntovisualizePlugin;
import org.neontoolkit.ontovisualize.edges.DashedEdge;
import org.neontoolkit.ontovisualize.edges.OntoStudioDefaultEdge;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

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
 * 
 */
public class IndividualObjectPropertyNodeContentProvider extends AbstractNodeContentProvider {

    public IndividualObjectPropertyNodeContentProvider() {
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
    private void addInstancePropertyValues(String projectId, String ontologyUri, List<LabelImageNode> nodes, List<OntoStudioDefaultEdge> edges, OWLEntity clazz, int hierarchyLevel) throws NeOnCoreException,NeOnCoreException {
        try {
            OWLModel owlModel = OWLModelFactory.getOWLModel(ontologyUri, projectId);
            String[] individualUris = new GetIndividuals(projectId, ontologyUri, clazz.getURI().toString()).getResults();
            if (individualUris.length > 100) {
                return;
            }
            for (String individualUri: individualUris) {
                Set<LocatedItem<OWLObjectPropertyAssertionAxiom>> objMem = ((OWLModel) owlModel).getObjectPropertyMemberHits(individualUri);
                for (LocatedItem<OWLObjectPropertyAssertionAxiom> mem: objMem) {
                    OWLObjectPropertyAssertionAxiom member = mem.getItem();
                    OWLObjectPropertyExpression objectProperty = member.getProperty();
                    OWLIndividual targetIndividual = member.getObject();
    
                    LabelImageNode sourceInstanceNode = getNode(individualUri, ontologyUri, projectId, VisualizerConfiguration.INDIVIDUAL_TYPE, _ontologyLanguage);
                    LabelImageNode targetInstanceNode = getNode(OWLUtilities.toString(targetIndividual), OWLUtilities.toString(objectProperty), ontologyUri, projectId, VisualizerConfiguration.INDIVIDUAL_TYPE, _ontologyLanguage);
                    LabelImageNode objectPropertyNode = getNode(OWLUtilities.toString(objectProperty), ontologyUri, projectId, VisualizerConfiguration.OBJECT_PROPERTY_TYPE, _ontologyLanguage);
                    addNode(targetInstanceNode, nodes);
                    addNode(objectPropertyNode, nodes);
                    addEdge(new DashedEdge(targetInstanceNode, objectPropertyNode), edges);
                    addEdge(new IndividualEdge(objectPropertyNode, sourceInstanceNode), edges);
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
    public Object create() throws CoreException {
        return new IndividualObjectPropertyNodeContentProvider();
    }

    @Override
    public LabelImageNode getNode(String id, String propertyId, String ontologyId, String projectId, int nodeType, String ontologyLanguage) {
        LabelImageNode cachedNode = getNodes().get(LabelImageNode.concat(propertyId, id));
        if (cachedNode != null) {
            return cachedNode;
        } else {
            return (LabelImageNode) ((VisualizerConfiguration) OntovisualizePlugin.getDefault().getVisualizerConfigurator(_ontologyLanguage)).createIndividualRangeNode(id, propertyId, ontologyId, projectId);
        }
    }

}
