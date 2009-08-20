package com.ontoprise.ontostudio.owl.gui.ontologyimportsgraph;

import java.util.ArrayList;

import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.pane.JGraphPane;

import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.SharedImages;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.jpowergraph.painters.ImageNodePainter;
import org.neontoolkit.ontovisualize.IVisualizerConfiguration;

import com.ontoprise.ontostudio.owl.visualize.nodes.OntologyNode;

public class OntologyImportsGraphConfiguration implements IVisualizerConfiguration {

    public static final int ONTOLOGY_TYPE = 0;

    public void setFilterValue(Class<?> theClass, boolean status, Legend theLegend) {
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Class> getFilterableNodes() {
        ArrayList<Class> filterableNodes = new ArrayList<Class>();
        return filterableNodes;
    }

    public void setEdgePainters(JGraphPane graphPane) {
    }

    public void setNodePainters(JGraphPane graphPane) {
        graphPane.setNodePainter(OntologyNode.class, new ImageNodePainter(graphPane.getParent(), NeOnUIPlugin.getDefault().getImageRegistry().get(SharedImages.ONTOLOGY)));
    }

    public Node getRootElementNode(String id, String ontologyId, String projectId) {
        return null;
    }

    public Node getOntologyNode(AbstractOntologyTreeElement element) {
        return null;
    }

    public String expandId(String id, String ontologyId, String projectId) {
        return ""; //$NON-NLS-1$
    }

    public Node createNode(String id, String ontologyId, String projectId, int nodeType) {
        return new OntologyNode(id, projectId);
    }
}
