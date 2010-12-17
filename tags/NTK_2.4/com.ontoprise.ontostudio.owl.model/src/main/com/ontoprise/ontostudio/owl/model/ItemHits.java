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

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * An item, an entity or a description for example, can occure within an ontology in several axioms. It can also occur in several axioms of imported ontologies.<br/>
 * <br/>
 * This class provides information about the occurrences of an item within ontologies. An occurrence - or hit - is represented by a <code>LocatedItem</code> of
 * type <code>Axiom</code>.
 * 
 * @author krekeler
 * 
 * @param <E>
 * @param <A>
 */
public interface ItemHits<E, A extends OWLAxiom> {
    /**
     * Get the item.
     * 
     * @return
     */
    public E getItem();

    /**
     * Get the axioms in which the item occurs.
     * 
     * @return
     */
    public Set<LocatedItem<A>> getAxioms();
}
