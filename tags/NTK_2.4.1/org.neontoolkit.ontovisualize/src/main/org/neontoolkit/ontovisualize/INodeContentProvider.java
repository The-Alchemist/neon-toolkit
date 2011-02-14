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

import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;


/**
 * @author Werner Hihn
 * 
 */
public interface INodeContentProvider extends IExecutableExtensionFactory {
    
    /**
     * Returns an {@link OntologyGraph} that contains both nodes and edges needed to visualize an ontology. 
     * @param element
     * @param hierarchyLevel The level of subconcepts to display. Is read from Preferences. 
     * @return
     * @throws CommandException
     * @throws KAON2Exception
     */
    public OntologyGraph getOntologyGraph(AbstractOntologyTreeElement element, int hierarchyLevel) throws NeOnCoreException;

    /**
     * Returns an {@link OntologyGraph} that contains both nodes and edges needed to visualize an ontology. 
     * @param node The node that shall be visualized.
     * @param hierarchyLevel The level of subconcepts to display. Is read from Preferences. 
     * @return
     * @throws CommandException
     * @throws KAON2Exception
     */
    public OntologyGraph getOntologyGraph(LabelImageNode node, int hierarchyLevel) throws NeOnCoreException;
    
    /**
     * Clears the List of nodes. 
     */
    public void clearNodes();

}
