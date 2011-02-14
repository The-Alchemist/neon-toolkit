/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.visualize.nodes;

import org.eclipse.core.runtime.CoreException;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.visualize.Messages;

/**
 * @author werner
 *
 */
public class DataNode extends LabelImageNode {

    private String _value;
    
    public DataNode(String value, String ontologyId, String projectId) {
        super(value, ontologyId, projectId);
        _value = value;
    }
    
    public DataNode() {
        super();
        setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY));
    }
    
    /* (non-Javadoc)
     * @see org.neontoolkit.ontovisualize.nodes.LabelImageNode#getLabel()
     */
    @Override
    public String getLabel() {
        return _value;
    }
    
    /* (non-Javadoc)
     * @see org.neontoolkit.ontovisualize.nodes.LabelImageNode#getInternalId()
     */
    @Override
    public String getInternalId() {
        return getLabel();
    }
    
//    /* (non-Javadoc)
//     * @see org.neontoolkit.ontovisualize.nodes.LabelImageNode#hashCode()
//     */
//    @Override
//    public int hashCode() {
//        return _value.hashCode();
//    }
//    
//    /* (non-Javadoc)
//     * @see org.neontoolkit.ontovisualize.nodes.LabelImageNode#equals(java.lang.Object)
//     */
//    @Override
//    public boolean equals(Object arg0) {
//        if (!(arg0 instanceof DataNode))  {
//            return false;
//        }
//        return getLabel().equals(((DataNode)arg0).getLabel());
//    }
    
    /* (non-Javadoc)
     * @see org.neontoolkit.ontovisualize.nodes.LabelImageNode#toString()
     */
    @Override
    public String toString() {
        return _value;
    }
    
    @Override
    public String getNodeType() {
        return Messages.DataNode_0;
    }

    public Object create() throws CoreException {
        return new DataNode();
    }

    /* (non-Javadoc)
     * @see org.neontoolkit.ontovisualize.nodes.LabelImageNode#canBeNavigatedTo()
     */
    @Override
    public boolean canBeNavigatedTo() {
        return false;
    }
    
    @Override
    public String guiRepresentation() {
//TODO: Werner, please fix this
//        return IdentifierRepresentation.getRepresentation(_projectId, _ontologyId, _id);
        return getId();
    }
}
