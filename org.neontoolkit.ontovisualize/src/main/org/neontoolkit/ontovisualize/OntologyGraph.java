/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 11.12.2008
 * Created by: werner
 ******************************************************************************/
package org.neontoolkit.ontovisualize;

import java.util.ArrayList;
import java.util.List;

import org.neontoolkit.ontovisualize.edges.OntoStudioDefaultEdge;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;


/**
 * @author werner
 *
 */
public class OntologyGraph {
    private List<LabelImageNode> _nodes;
    private List<OntoStudioDefaultEdge> _edges;
    
    public OntologyGraph() {
        _nodes = new ArrayList<LabelImageNode>();
        _edges = new ArrayList<OntoStudioDefaultEdge>();
    }
    
    public List<LabelImageNode> getNodes() {
        return _nodes;
    }
    
    public List<OntoStudioDefaultEdge> getEdges() {
        return _edges;
    }
    
    public void addNodes(List<LabelImageNode> nodes) {
        _nodes.addAll(nodes);
    }
    
    public void addEdges(List<OntoStudioDefaultEdge> edges) {
        _edges.addAll(edges);
    }
    

}
