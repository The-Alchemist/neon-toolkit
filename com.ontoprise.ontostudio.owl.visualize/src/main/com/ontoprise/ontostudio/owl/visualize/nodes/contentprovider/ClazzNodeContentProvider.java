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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetRootClazzes;
import com.ontoprise.ontostudio.owl.model.commands.clazz.GetSubClazzes;
import com.ontoprise.ontostudio.owl.visualize.VisualizerConfiguration;
import com.ontoprise.ontostudio.owl.visualize.nodes.AbstractOWLEntityNode;
import com.ontoprise.ontostudio.owl.visualize.nodes.ClazzNode;
import com.ontoprise.ontostudio.owl.visualize.nodes.OntologyNode;

public class ClazzNodeContentProvider extends AbstractNodeContentProvider {

    public ClazzNodeContentProvider() {
        super(OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE);
    }

    @Override
    public OntologyGraph getOntologyGraph(AbstractOntologyTreeElement element, int hierarchyLevel) throws NeOnCoreException {
        OntologyGraph graph = new OntologyGraph();

        String projectId = element.getProjectName();
        String ontologyUri;
        List<LabelImageNode> nodes = new ArrayList<LabelImageNode>();
        List<OntoStudioDefaultEdge> edges = new ArrayList<OntoStudioDefaultEdge>();

        if (element instanceof OntologyTreeElement) {
            // root clazzes
            ontologyUri = ((OntologyTreeElement) element).getOntologyUri();
            addRootClazzes(projectId, ontologyUri, nodes, edges, hierarchyLevel);
        } else if (element instanceof ClazzTreeElement) {
            // sub clazzes
            ontologyUri = ((ClazzTreeElement) element).getOntologyUri();
            addSubClazzes(projectId, ontologyUri, nodes, edges, ((ClazzTreeElement) element).getEntity(), hierarchyLevel);

            // finally add a node for the passed element and set it as root node
            LabelImageNode passedNode = getNode(((ClazzTreeElement) element).getEntity().getURI().toString(), ontologyUri, projectId, VisualizerConfiguration.CLAZZ_TYPE, _ontologyLanguage);
            passedNode.setFixed(true);
            addNode(passedNode, nodes);

            // parent node (either ontology node if this is a root concept, or the parent concept)
            addParentClazzes(projectId, ontologyUri, nodes, edges, ((ClazzTreeElement) element).getId(), passedNode);
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
        String ontologyUri = node.getOntologyId();
        String projectId = node.getProjectId();

        if (node instanceof OntologyNode) {
            node.setFixed(true);
            // root clazzes
            addRootClazzes(projectId, ontologyUri, nodes, edges, hierarchyLevel);
        } else if (node instanceof ClazzNode) {
            // sub clazzes
            addSubClazzes(projectId, ontologyUri, nodes, edges, ((AbstractOWLEntityNode) node).getEntity(), hierarchyLevel);

            // finally add a node for the passed element and set it as root node
            node.setIsRoot(true);
            node.setFixed(true);
            addNode(node, nodes);

            // parent node (either ontology node if this is a root concept, or the parent concept)
            addParentClazzes(projectId, ontologyUri, nodes, edges, node.getId(), node);
        }
        graph.addNodes(nodes);
        graph.addEdges(edges);
        return graph;
    }

    private void addSubClazzes(String projectId, String ontologyId, List<LabelImageNode> nodes, List<OntoStudioDefaultEdge> edges, OWLEntity superClazz, int hierarchyLevel) throws NeOnCoreException,NeOnCoreException {
        try {
            if (hierarchyLevel > -1) {
                String[] subClazzUris = new GetSubClazzes(projectId, ontologyId, superClazz.getURI().toString()).getResults();
                for (String subClazzUri: subClazzUris) {
                    OWLClass subClazz = OWLModelFactory.getOWLDataFactory(projectId).getOWLClass(OWLUtilities.toURI(subClazzUri));
                    while (hierarchyLevel > 1) {
                        // recurse according to hierarchyLevel
                        hierarchyLevel--;
                        addSubClazzes(projectId, ontologyId, nodes, edges, subClazz, hierarchyLevel);
                    }
                    LabelImageNode clazzNode = getNode(subClazzUri, ontologyId, projectId, VisualizerConfiguration.CLAZZ_TYPE, _ontologyLanguage);
                    addNode(clazzNode, nodes);
                    addEdge(new OntoStudioDefaultEdge(clazzNode, getNode(superClazz.getURI().toString(), ontologyId, projectId, VisualizerConfiguration.CLAZZ_TYPE, _ontologyLanguage)), edges);
                }
            }
        } catch (CommandException ce) {
            throw new InternalNeOnException(ce);
        }
    }

    private void addRootClazzes(String projectId, String ontologyId, List<LabelImageNode> nodes, List<OntoStudioDefaultEdge> edges, int hierarchyLevel) throws NeOnCoreException,NeOnCoreException {
        try {
            if (hierarchyLevel > -1) {
                String[] rootClazzUris = new GetRootClazzes(projectId, ontologyId).getResults();
                List<OWLClass> rootClazzes = new ArrayList<OWLClass>();
                for (String uri: rootClazzUris) {
                    rootClazzes.add(OWLModelFactory.getOWLDataFactory(projectId).getOWLClass(OWLUtilities.toURI(uri)));
                }
                for (OWLClass clazz: rootClazzes) {
                    int level = hierarchyLevel;
                    while (level > 1) {
                        // recurse according to hierarchyLevel
                        level--;
                        addSubClazzes(projectId, ontologyId, nodes, edges, clazz, level);
                    }
                    LabelImageNode clazzNode = getNode(clazz.getURI().toString(), ontologyId, projectId, VisualizerConfiguration.CLAZZ_TYPE, _ontologyLanguage);
                    addNode(clazzNode, nodes);
                    addEdge(new OntoStudioDefaultEdge(clazzNode, getNode(ontologyId, ontologyId, projectId, VisualizerConfiguration.ONTOLOGY_TYPE, _ontologyLanguage)), edges);
                }
            }
        } catch (CommandException ce) {
            throw new InternalNeOnException(ce);
        }
    }

    private void addParentClazzes(String projectId, String ontologyUri, List<LabelImageNode> nodes, List<OntoStudioDefaultEdge> edges, String clazzId, LabelImageNode passedNode) throws NeOnCoreException, NeOnCoreException {
        try {
            List<LabelImageNode> parentNodes = new ArrayList<LabelImageNode>();
            List<OntoStudioDefaultEdge> parentEdges = new ArrayList<OntoStudioDefaultEdge>();
            String[] rootClazzUris = new GetRootClazzes(projectId, ontologyUri).getResults();
            List<OWLClass> rootClazzes = new ArrayList<OWLClass>();
            for (String uri: rootClazzUris) {
                rootClazzes.add(OWLModelFactory.getOWLDataFactory(projectId).getOWLClass(OWLUtilities.toURI(uri)));
            }
            OWLClass clazz = OWLModelFactory.getOWLDataFactory(projectId).getOWLClass(OWLUtilities.toURI(clazzId));
            if (rootClazzes.contains(clazz)) {
                LabelImageNode parentNode = getNode(ontologyUri, ontologyUri, projectId, VisualizerConfiguration.ONTOLOGY_TYPE, _ontologyLanguage);
                addNode(parentNode, parentNodes);
                addEdge(new OntoStudioDefaultEdge(passedNode, parentNode), parentEdges);
            } else {
                Set<OWLClass> parentClazzes = OWLModelFactory.getOWLModel(ontologyUri, projectId).getSuperClasses(clazzId);
                if (parentClazzes.size() == 0) {
                    Set<OWLClass> equivalentClazzes = OWLModelFactory.getOWLModel(ontologyUri, projectId).getEquivalentClasses(clazzId);
                    for (OWLClass equivalentClazz: equivalentClazzes) {
                        LabelImageNode parentNode = getNode(equivalentClazz.getURI().toString(), ontologyUri, projectId, VisualizerConfiguration.CLAZZ_TYPE, _ontologyLanguage);
                        addNode(parentNode, parentNodes);
                        addEdge(new OntoStudioDefaultEdge(passedNode, parentNode), parentEdges);
                    }
                }
                for (OWLClass parentClazz: parentClazzes) {
                    LabelImageNode parentNode = getNode(parentClazz.getURI().toString(), ontologyUri, projectId, VisualizerConfiguration.CLAZZ_TYPE, _ontologyLanguage);
                    addNode(parentNode, parentNodes);
                    addEdge(new OntoStudioDefaultEdge(passedNode, parentNode), parentEdges);
                }
            }
            addNodes(nodes, parentNodes);
            addEdges(edges, parentEdges);
        } catch (CommandException ce) {
            throw new InternalNeOnException(ce);
        }
    }

    public Object create() throws CoreException {
        return new ClazzNodeContentProvider();
    }

}
