/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.ontovisualize;

import java.util.ArrayList;

import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.pane.JGraphPane;

import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;

/**
 * @author werner
 * 
 */
public interface IVisualizerConfiguration {

    /**
     * Called when a checkmark is set in the displayed legend for a concrete node.
     * 
     * @param clazz
     * @param status
     * @param legend
     */
    public void setFilterValue(Class<?> clazz, boolean status, Legend legend);

    /**
     * Returns a List of filterable nodes, i.e. nodes that can be hidden by setting a checkmark in the displayed legend.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Class> getFilterableNodes();

    /**
     * Used to set painters for concrete edges. Call {@link JGraphPane}.setEdgePainter({@link Edge} edge, {@link AbstractEdgePainter} painter).
     * 
     * @param graphPane
     */
    public void setEdgePainters(JGraphPane graphPane);

    /**
     * Used to set painters for concrete nodes. Call {@link JGraphPane}.setNodePainter(Class, {@link NodePainter} painter).
     * 
     * @param graphPane
     */
    public void setNodePainters(JGraphPane graphPane);

    /**
     * Returns a Node that represents the first node beneath an ontology node. In most cases this will be a Concept or Class Node.
     * 
     * @param id
     * @param ontologyId
     * @param projectId
     * @return
     */
    public Node getRootElementNode(String id, String ontologyId, String projectId);

    /**
     * Returns a Node that represents an ontology element. Have to do this in every concrete plugin, since the IDs will differ.
     * 
     * @param element
     * @return
     */
    public Node getOntologyNode(AbstractOntologyTreeElement element);

    /**
     * If we want to navigate to a node, we always need the fully qualified ID/URI. Nevertheless the UI might only show the local name or QName. This method
     * expands a local name or QName to the needed fully qualified ID.
     * 
     * @param id
     * @param ontologyId
     * @param projectId
     * @return
     */
    public String expandId(String id, String ontologyId, String projectId);

    /**
     * Returns a node of the passed <code>nodeType</code>. Those types are defined as constants in the implementation of this interface.
     * 
     * @param id
     * @param ontologyId
     * @param projectId
     * @param nodeType
     * @return
     */
    public Node createNode(String id, String ontologyId, String projectId, int nodeType);
}
