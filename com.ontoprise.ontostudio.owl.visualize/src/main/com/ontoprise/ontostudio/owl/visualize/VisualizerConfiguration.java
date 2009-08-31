package com.ontoprise.ontostudio.owl.visualize;

import java.util.ArrayList;
import java.util.Set;

import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.painters.LineEdgePainter;
import net.sourceforge.jpowergraph.painters.ShapeNodePainter;
import net.sourceforge.jpowergraph.pane.JGraphPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.jpowergraph.painters.DashedLineEdgePainter;
import org.neontoolkit.jpowergraph.painters.ImageNodePainter;
import org.neontoolkit.jpowergraph.painters.SolidLineEdgePainter;
import org.neontoolkit.ontovisualize.IVisualizerConfiguration;
import org.neontoolkit.ontovisualize.OntovisualizePlugin;
import org.neontoolkit.ontovisualize.VisualizerSharedImages;
import org.neontoolkit.ontovisualize.edges.DashedEdge;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.visualize.edges.IndividualEdge;
import com.ontoprise.ontostudio.owl.visualize.nodes.ClazzNode;
import com.ontoprise.ontostudio.owl.visualize.nodes.DataNode;
import com.ontoprise.ontostudio.owl.visualize.nodes.DataPropertyNode;
import com.ontoprise.ontostudio.owl.visualize.nodes.IndividualMultiNode;
import com.ontoprise.ontostudio.owl.visualize.nodes.IndividualNode;
import com.ontoprise.ontostudio.owl.visualize.nodes.ObjectPropertyNode;
import com.ontoprise.ontostudio.owl.visualize.nodes.OntologyNode;

public class VisualizerConfiguration implements IVisualizerConfiguration {

    public static final int ONTOLOGY_TYPE = 0;
    public static final int CLAZZ_TYPE = 1;
    public static final int INDIVIDUAL_TYPE = 2;
    public static final int INDIVIDUAL_MULTI_TYPE = 3;
    public static final int OBJECT_PROPERTY_TYPE = 4;
    public static final int DATA_PROPERTY_TYPE = 5;
    public static final int DATA_TYPE = 6;

    private Color _propertyGreen;
    private Color _black;
    private Color _white;
    private Color _cornflowerBlue;

    public void setFilterValue(Class<?> theClass, boolean status, Legend theLegend) {
        if (theClass.equals(IndividualNode.class)) {
            theLegend.getNodeFilterLens().setFilterValue(IndividualNode.class, !status);
            theLegend.getNodeFilterLens().setFilterValue(DataNode.class, !status);
            theLegend.getNodeFilterLens().setFilterValue(DataPropertyNode.class, !status);
            theLegend.getNodeFilterLens().setFilterValue(ObjectPropertyNode.class, !status);
        } else if (theClass.equals(IndividualMultiNode.class)) {
            theLegend.getNodeFilterLens().setFilterValue(IndividualNode.class, !status);
            theLegend.getNodeFilterLens().setFilterValue(IndividualMultiNode.class, !status);
            theLegend.getNodeFilterLens().setFilterValue(DataNode.class, !status);
            theLegend.getNodeFilterLens().setFilterValue(DataPropertyNode.class, !status);
            theLegend.getNodeFilterLens().setFilterValue(ObjectPropertyNode.class, !status);
        } else if (theClass.equals(ObjectPropertyNode.class)) {
            theLegend.getNodeFilterLens().setFilterValue(ObjectPropertyNode.class, !status);
        } else if (theClass.equals(DataPropertyNode.class)) {
            theLegend.getNodeFilterLens().setFilterValue(DataPropertyNode.class, !status);
            theLegend.getNodeFilterLens().setFilterValue(DataNode.class, !status);
        } else if (theClass.equals(DataNode.class)) {
            theLegend.getNodeFilterLens().setFilterValue(DataNode.class, !status);
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Class> getFilterableNodes() {
        ArrayList<Class> filterableNodes = new ArrayList<Class>();
        filterableNodes.add(IndividualNode.class);
        filterableNodes.add(ObjectPropertyNode.class);
        filterableNodes.add(DataPropertyNode.class);
        filterableNodes.add(DataNode.class);

        return filterableNodes;
    }

    public void setEdgePainters(JGraphPane graphPane) {
        _propertyGreen = new Color(graphPane.getDisplay(), 70, 179, 118);
        _black = graphPane.getDisplay().getSystemColor(SWT.COLOR_BLACK);
        _white = graphPane.getDisplay().getSystemColor(SWT.COLOR_WHITE);
        _cornflowerBlue = new Color(graphPane.getDisplay(), 100, 149, 237);

        LineEdgePainter dashedLineEdgePainter = new DashedLineEdgePainter(_black, _white, _propertyGreen);
        LineEdgePainter individualLineEdgePainter = new SolidLineEdgePainter(_cornflowerBlue, _cornflowerBlue, _cornflowerBlue);

        graphPane.setEdgePainter(DashedEdge.class, dashedLineEdgePainter);
        graphPane.setEdgePainter(IndividualEdge.class, individualLineEdgePainter);
    }

    public void setNodePainters(JGraphPane graphPane) {
        graphPane.setNodePainter(OntologyNode.class, new ImageNodePainter(graphPane.getParent(), OntovisualizePlugin.getDefault().getImageRegistry().get(VisualizerSharedImages.ONTOLOGY)));
        graphPane.setNodePainter(ClazzNode.class, new ImageNodePainter(graphPane.getParent(), OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ)));
        graphPane.setNodePainter(IndividualNode.class, new ImageNodePainter(graphPane.getParent(), OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL)));
        graphPane.setNodePainter(IndividualMultiNode.class, new ImageNodePainter(graphPane.getParent(), OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL)));
        graphPane.setNodePainter(ObjectPropertyNode.class, new ImageNodePainter(graphPane.getParent(), OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY)));
        graphPane.setNodePainter(DataPropertyNode.class, new ImageNodePainter(graphPane.getParent(), OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY)));
        graphPane.setNodePainter(DataNode.class, new ShapeNodePainter(graphPane.getParent(), ShapeNodePainter.RECTANGLE));
    }

    public Node getRootElementNode(String id, String ontologyId, String projectId) {
        try {
            Set<OWLClass> allClasses = OWLModelFactory.getOWLModel(ontologyId, projectId).getAllClasses();
            OWLClass clazz = OWLModelFactory.getOWLDataFactory(projectId).getOWLClass(OWLUtilities.toURI(id));
            if (allClasses.contains(clazz)) {
                return new ClazzNode(clazz, ontologyId, projectId);
            }
        } catch (NeOnCoreException e) {
            return null;
        }
        return null;
    }

    public Node getOntologyNode(AbstractOntologyTreeElement element) {
        return new OntologyNode(element.getOntologyUri(), element.getProjectName());
    }

    public String expandId(String id, String ontologyId, String projectId) {
        OWLNamespaces namespaces;
        try {
            namespaces = OWLModelFactory.getOWLModel(ontologyId, projectId).getNamespaces();
            return namespaces.expandString(id);
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return id;
        }
    }

    public Node createNode(String id, String ontologyId, String projectId, int nodeType) {
        try {
            switch (nodeType) {
                case ONTOLOGY_TYPE:
                    return new OntologyNode(id, projectId);
                case CLAZZ_TYPE:
                    return new ClazzNode(OWLModelFactory.getOWLDataFactory(projectId).getOWLClass(OWLUtilities.toURI(id)), ontologyId, projectId);
                case INDIVIDUAL_TYPE:
                    return new IndividualNode(OWLModelFactory.getOWLDataFactory(projectId).getOWLNamedIndividual(OWLUtilities.toURI(id)), ontologyId, projectId);
                case INDIVIDUAL_MULTI_TYPE:
                    return new IndividualMultiNode(OWLModelFactory.getOWLDataFactory(projectId).getOWLNamedIndividual(OWLUtilities.toURI(id)), ontologyId, projectId);
                case OBJECT_PROPERTY_TYPE:
                    return new ObjectPropertyNode(OWLModelFactory.getOWLDataFactory(projectId).getOWLObjectProperty(OWLUtilities.toURI(id)), ontologyId, projectId);
                case DATA_PROPERTY_TYPE:
                    return new DataPropertyNode(OWLModelFactory.getOWLDataFactory(projectId).getOWLDataProperty(OWLUtilities.toURI(id)), ontologyId, projectId);
                case DATA_TYPE:
                    return new DataNode(id, ontologyId, projectId);
            }
            return new ClazzNode(OWLModelFactory.getOWLDataFactory(projectId).getOWLClass(OWLUtilities.toURI(id)), ontologyId, projectId);
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Node createIndividualRangeNode(String id, String propertyUri, String ontologyId, String projectId) {
        try {
            OWLEntity entity = OWLModelFactory.getOWLDataFactory(projectId).getOWLObjectProperty(OWLUtilities.toURI(id));
            return new IndividualNode(entity, propertyUri, ontologyId, projectId);
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
    }
}
