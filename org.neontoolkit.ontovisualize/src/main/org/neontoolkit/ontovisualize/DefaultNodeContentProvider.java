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
import java.util.List;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.ontovisualize.edges.OntoStudioDefaultEdge;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;


/**
 * @author werner
 *
 */
public class DefaultNodeContentProvider {

    /**
     * The singleton instance of this class.
     */
    
    /**
     * A list of all content providers for the passed ontology language as defined in extension point <code>visualizerContext</code>. 
     */
    private List<INodeContentProvider> _providers;
    
    /**
     * An integer value indicating how many levels of subconcepts shall be displayed. Can be configured in preferences. 
     */
    private int _hierarchyLevel;
    
    public DefaultNodeContentProvider(String ontologyLanguage, int hierarchyLevel) {
        _providers = OntovisualizePlugin.getDefault().getContentProviders(ontologyLanguage);
        _hierarchyLevel = hierarchyLevel;
    }
    
    public List<LabelImageNode> getNodes(AbstractOntologyTreeElement element) {
        List<LabelImageNode> nodeList = new ArrayList<LabelImageNode>();
        try {
            for (INodeContentProvider provider: _providers) {
                provider.clearNodes();
                List<LabelImageNode> nodes = provider.getOntologyGraph(element, _hierarchyLevel).getNodes();
                for (LabelImageNode n: nodes) {
                    if (!nodeList.contains(n)) {
                        nodeList.add(n);
                    }
                }
            }
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        return nodeList;
    }
    
    public List<LabelImageNode> getNodes(LabelImageNode node) {
        List<LabelImageNode> nodeList = new ArrayList<LabelImageNode>();
        try {
            for (INodeContentProvider provider: _providers) {
                provider.clearNodes();
                List<LabelImageNode> nodes = provider.getOntologyGraph(node, _hierarchyLevel).getNodes();
                for (LabelImageNode n: nodes) {
                    if (!nodeList.contains(n)) {
                        nodeList.add(n);
                    }
                }
            }
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        return nodeList;
    }

    public List<OntoStudioDefaultEdge> getEdges(AbstractOntologyTreeElement element) {
        List<OntoStudioDefaultEdge> edgeList = new ArrayList<OntoStudioDefaultEdge>();
        try {
            for (INodeContentProvider provider: _providers) {
                List<OntoStudioDefaultEdge> edges = provider.getOntologyGraph(element, _hierarchyLevel).getEdges();
                for (OntoStudioDefaultEdge e: edges) {
                    if (!edgeList.contains(e)) {
                        edgeList.add(e);
                    }
                }
            }
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        return edgeList;
    }
    
    public List<OntoStudioDefaultEdge> getEdges(LabelImageNode node) {
        List<OntoStudioDefaultEdge> edgeList = new ArrayList<OntoStudioDefaultEdge>();
        try {
            for (INodeContentProvider provider: _providers) {
                List<OntoStudioDefaultEdge> edges = provider.getOntologyGraph(node, _hierarchyLevel).getEdges();
                for (OntoStudioDefaultEdge e: edges) {
                    if (!edgeList.contains(e)) {
                        edgeList.add(e);
                    }
                }
            }
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        return edgeList;
    }

}
