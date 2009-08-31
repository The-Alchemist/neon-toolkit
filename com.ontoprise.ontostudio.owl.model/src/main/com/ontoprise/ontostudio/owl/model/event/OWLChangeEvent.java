/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.event;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class OWLChangeEvent {

    private OWLOntology _changedOntology;
    private OWLOntology _sourceOntology;
    private List<OWLAxiomChange> _changes;
    private Set<OWLEntity> _addedEntities;
    private Set<OWLEntity> _removedEntities;
    private String _projectId;

    public OWLChangeEvent(OWLOntology changedOntology, OWLOntology sourceOntology, List<OWLAxiomChange> changes, Set<OWLEntity> addedEntities, Set<OWLEntity> removedEntities, String projectId) {
        _changedOntology = changedOntology;
        _sourceOntology = sourceOntology;
        _changes = changes;
        _addedEntities = addedEntities;
        _removedEntities = removedEntities;
        _projectId = projectId;
    }

    public OWLOntology getChangedOntology() {
        return _changedOntology;
    }

    public OWLOntology getSourceOntology() {
        return _sourceOntology;
    }

    public List<OWLAxiomChange> getChanges() {
        return _changes;
    }

    /**
     * Get the set of potentially added entities within a change to the ontology.
     * 
     * <p>Note that the result may contain entities which actually have not been added,
     * but were in the ontology before.</p>
     * 
     * <p> The result can contain {@link OWLAnnotationProperty} objects.
     * {@link OWLAnnotationProperty} is not defined by the OWL API
     * but by the owl model plug-in for symmetry reasons with respect to {@link OWLObjectProperty} and {@link OWLDataProperty}.</p>
     * 
     * @return The set of potentially added entities.
     */
    public Set<OWLEntity> getPotentiallyAddedEntities() {
        return _addedEntities;
    }

    public Set<OWLEntity> getRemovedEntities() {
        return _removedEntities;
    }

    public String getProjectId() {
        return _projectId;
    }

}
