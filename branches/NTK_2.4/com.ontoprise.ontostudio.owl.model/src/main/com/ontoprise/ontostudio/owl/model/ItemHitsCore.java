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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

public class ItemHitsCore<E, A extends OWLAxiom> implements ItemHits<E,A> {

    protected E _entity;
    /** The set of (located) axioms in which <code>_entity</code> occurs. */
    protected Set<LocatedItem<A>> _axioms;

    public ItemHitsCore(E entity, LocatedItem<A>... axioms) {
        _entity = entity;
        _axioms = Collections.unmodifiableSet(new LinkedHashSet<LocatedItem<A>>(Arrays.asList(axioms)));
    }

    public ItemHitsCore(E entity, LocatedItem<A> axiom) {
        _entity = entity;
        _axioms = Collections.unmodifiableSet(Collections.singleton(axiom));
    }

    public ItemHitsCore(E entity, Set<LocatedItem<A>> axioms) {
        _entity = entity;
        _axioms = Collections.unmodifiableSet(new HashSet<LocatedItem<A>>(axioms));
    }

    /**
     * Get the (located) axioms in each of them the entity occurs.
     */
    public Set<LocatedItem<A>> getAxioms() {
        return _axioms;
    }

    public E getItem() {
        return _entity;
    }

}
