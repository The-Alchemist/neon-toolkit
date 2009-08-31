/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.jpowergraph.nodes;

import net.sourceforge.jpowergraph.DefaultNode;

/*
 * Created by Werner Hihn
 */

/**
 * This node is a default implementation for nodes that contain a data object.
 * This is similiar to e.g. the TreeItem for Trees. 
 * @author Dirk Wenke
 */
public class DataNode extends DefaultNode {
    //The data object
    private Object _data;
    
    /**
     * Default constructor with the given data object
     * @param dataObject
     */
    public DataNode(Object dataObject) {
        _data = dataObject;
    }

    /**
     * Returns the data object
     * @return
     */
    public Object getData() {
        return _data;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _data.toString();
    }
}
