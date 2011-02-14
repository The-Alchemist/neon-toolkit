/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.hierarchy;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author krekeler
 *
 */
public interface MutableEntityHierarchy<E> extends EntityHierarchy<E> {
    boolean addAxiom(OWLAxiom axiom, OWLOntology ontologyID);
    boolean addNodeAxiom(E entity, OWLAxiom axiom);
    boolean addEdgeAxiom(E child, E parent, OWLAxiom axiom);

    boolean removeAxiom(OWLAxiom axiom, OWLOntology ontologyID);
    boolean removeNodeAxiom(E entity, OWLAxiom axiom);
    boolean removeEdgeAxiom(E child, E parent, OWLAxiom axiom);
}
