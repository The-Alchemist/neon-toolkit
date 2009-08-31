/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 20.01.2009
 * Created by: krekeler
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.hierarchy;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author krekeler
 *
 */
public interface EntityHierarchy<E> {
    interface Node<E> {
        E getEntity();
        Set<OWLAxiom> getDeclarationAxioms();
        
        Set<E> getChildren();
        Set<E> getParents();

        Set<OWLAxiom> getChildRelationDefiningAxioms(E child);
        Set<OWLAxiom> getParentRelationDefiningAxioms(E parent);
    }
    
    interface EquivalenceClass<E> {
        Set<E> getEntities();
        E getRepresentative();
        
        /**
         * 
         * <p>Take care that an equivalence class can be a child of itself.</p>
         * 
         * @return                               The representatives of the child equivalence classes 
         *                                       (may contain the representative of this equivalence class).
         */
        Set<E> getChildRepresentatives();
        /**
         * 
         * <p>Take care that an equivalence class can be a parent of itself.</p>
         * 
         * @return                               The representatives of the parent equivalence classes 
         *                                       (may contain the representative of this equivalence class).
         */
        Set<E> getParentRepresentatives();
    }

    Node<E> getNode(E entity);
    EquivalenceClass<E> getEquivalenceClass(E entity);
    Set<OWLOntology> getOntologyIDs(OWLAxiom axiom);
    boolean containsAxiom(OWLAxiom axiom);
    
    /**
     * All entities which have at most itself as parent.
     * 
     * @return
     */
    Set<E> getRootEntities();
    /**
     * All representatives of equivalence classes which have at most itself as parent.
     * 
     * @return
     */
    Set<E> getRootEquivalenceClassRepresentatives();
}
