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

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * An <code>AxiomRequest</code> represents a request for axioms.<br/>
 * <br/>
 * In principle there is only one relevant method which is <code>getLocatedAxioms(boolean includeImportedOntologies, Object...parameters)</code>. This
 * method will fetch all axioms of interest and returns them with "attached" informations about the ontology in which the axiom is located.<br/>
 * <br/>
 * The other methods are just for comfort.
 * 
 * @author krekeler
 * 
 * @param <AxiomType>
 */
public interface AxiomRequest<AxiomType extends OWLAxiom> {
    /**
     * Get all axioms of interest.
     * 
     * @param includeImportedOntologies If <code>true</code> include axioms from imported ontologies. If <code>false</code> all returned axioms will be
     *            located within the ontology associated with this <code>OWLModel</code> instance.
     * @param parameters A list of parameters to be passed to the concret implementor of this interface.
     * @return All interesting axioms together with informations about the ontology they come from.
     * @throws NeOnCoreException
     */
    public Set<LocatedItem<AxiomType>> getLocatedAxioms(boolean includeImportedOntologies, Object... parameters) throws NeOnCoreException;

    /**
     * Returns all axioms of interest. The information to include imported ontologies or not is taken from the associated <code>OWLModel</code>.
     * 
     * @param parameters A list of parameters to be passed to the concret implementor of this interface.
     * @return All interesting axioms together with informations about the ontology they come from.
     * @throws NeOnCoreException
     */
    public Set<LocatedItem<AxiomType>> getLocatedAxioms(Object... parameters) throws NeOnCoreException;

    /**
     * Get all axioms of interest without the information about the ontology in which they are located.<br/>
     * <br/>
     * See <code>getLocatedAxioms(boolean includeImportedOntologies, Object...parameters)</code> for the parameters.
     * 
     * @param includeImportedOntologies
     * @param parameters
     * @return
     * @throws NeOnCoreException
     */
    public Set<AxiomType> getAxioms(boolean includeImportedOntologies, Object... parameters) throws NeOnCoreException;

    /**
     * Returns all axioms of interest without the information about the ontology in which they are located. The information to include imported ontologies
     * or not is taken from the associated <code>OWLModel</code>.<br/>
     * <br/>
     * See <code>getLocatedAxioms(boolean includeImportedOntologies, Object...parameters)</code> for the parameters.
     * 
     * @param parameters
     * @return
     * @throws NeOnCoreException
     */
    public Set<AxiomType> getAxioms(Object... parameters) throws NeOnCoreException;
}
