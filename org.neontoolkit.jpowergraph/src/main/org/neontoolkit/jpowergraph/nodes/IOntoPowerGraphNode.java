/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.jpowergraph.nodes;

import net.sourceforge.jpowergraph.Node;

/*
 * Created by Werner Hihn
 */

/**
 * The root interface of all nodes used in OntoVisualizer. It only knows if the node
 * is a root node, i.e. it is centered by the graph, and all other nodes are arranged
 * around it. 
 */
public interface IOntoPowerGraphNode extends Node {
    
	/**
	 * Returns <code>true</code> if this is a root node, <code>false</code> otherwise.
	 * @return
	 */
    boolean isRoot();

}
