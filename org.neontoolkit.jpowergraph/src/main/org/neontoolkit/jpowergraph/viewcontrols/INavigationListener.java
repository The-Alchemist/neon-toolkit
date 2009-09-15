/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.jpowergraph.viewcontrols;

import java.util.EventListener;

import net.sourceforge.jpowergraph.Node;

/*
 * Created by Werner Hihn
 */

/**
 * A NavigationListener is responsible for listening to Navigation Events such as 
 * navigating back and forth in the navigation history. 
 */
public interface INavigationListener extends EventListener {
    
	/**
	 * The back button was pressed. In contrast to selecting a node in the painted navigation
	 * history, this function doesn't paint the node we are jumping to after the last one, 
	 * but removes the last one of navigation history. 
	 *
	 */
    void back();
    
    /**
     * The forth button was pressed. Only works if the back button was pressed before. 
     * We are jumping to the lastly visited node before having pressed the back button.   
     *
     */
    void forth();

    /**
     * Is called when the search function is used to directly jump to a concept node.
     *  
     * @param node		The id of the node we want to navigate to. 
     */
    void goTo(String node);
    
    /**
     * 
     * @param node
     */
    void goTo(Node node);
    
    /**
     * The graph is refreshed. This is called when we want to disable the whole navigation history,
     * because the graph must be repainted then.  
     *
     */
    void refresh();
}
