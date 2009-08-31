/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 09.12.2008
 * Created by: werner
 ******************************************************************************/
package org.neontoolkit.ontovisualize;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.ontovisualize.edges.OntoStudioDefaultEdge;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;


/**
 * @author werner
 * 
 */
public abstract class AbstractNodeContentProvider implements INodeContentProvider {

    private HashMap<String,LabelImageNode> _nodes;

    protected String _ontologyLanguage;
    protected IPreferenceStore _store;

    public AbstractNodeContentProvider(String ontologyLanguage) {
        _ontologyLanguage = ontologyLanguage;
        _nodes = new HashMap<String,LabelImageNode>();
        _store = OntovisualizePlugin.getDefault().getPreferenceStore();
    }

    public void addNode(LabelImageNode node, List<LabelImageNode> nodes) {
        String id = node.getInternalId();
        LabelImageNode cachedNode = _nodes.get(id);
        if (cachedNode != null && cachedNode.getClass() == node.getClass()) {
            if (!nodes.contains(cachedNode)) {
                nodes.add(cachedNode);
            }
        } else {
            _nodes.put(id, node);
            if (!nodes.contains(node)) {
                nodes.add(node);
            }
        }
    }

    public LabelImageNode getNode(String id, String ontologyId, String projectId, int nodeType, String ontologyLanguage) {
        LabelImageNode cachedNode = _nodes.get(id);
        if (cachedNode != null) {
            return cachedNode;
        } else {
            return (LabelImageNode) OntovisualizePlugin.getDefault().getVisualizerConfigurator(ontologyLanguage).createNode(id, ontologyId, projectId, nodeType);
        }
    }
    
    public LabelImageNode getNode(String id, String internalId, String ontologyId, String projectId, int nodeType, String ontologyLanguage) {
        // empty implementation, may be overwritten in concrete plugins.
        return null;
    }

    public void addEdge(OntoStudioDefaultEdge edge, List<OntoStudioDefaultEdge> edges) {
        if (edge != null && !edges.contains(edge)) {
            edges.add(edge);
        }
    }

    public void addEdges(List<OntoStudioDefaultEdge> targetList, List<OntoStudioDefaultEdge> sourceList) {
        for (OntoStudioDefaultEdge edge: sourceList) {
            addEdge(edge, targetList);
        }
    }

    public void addNodes(List<LabelImageNode> targetList, List<LabelImageNode> sourceList) {
        for (LabelImageNode node: sourceList) {
            addNode(node, targetList);
        }
    }

    public HashMap<String,LabelImageNode> getNodes() {
        return _nodes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.ontovisualize.INodeContentProvider#clearNodes()
     */
    public void clearNodes() {
        _nodes.clear();
    }

    protected void disableNode(LabelImageNode node) {
        Image image = node.getImage();
        ImageDescriptor id = ImageDescriptor.createFromImage(image);
        node.setImage(ImageDescriptor.createWithFlags(id, SWT.IMAGE_GRAY).createImage());
        node.setImported(true);
    }
}
