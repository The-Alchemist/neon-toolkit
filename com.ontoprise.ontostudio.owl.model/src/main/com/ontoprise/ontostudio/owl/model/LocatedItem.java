/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model;

/**
 * Tags an item from an ontology with the ontology URI.
 * 
 * @author krekeler
 * 
 * @param <E>
 */
public interface LocatedItem<E> {
    /**
     * Get the item.
     * 
     * @return
     */
    public E getItem();

    /**
     * Get the ontology URI to which the item belongs.
     * 
     * @return
     */
    public String getOntologyURI();
}
