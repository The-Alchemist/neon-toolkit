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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.util.Cast;

/**
 * @author krekeler
 * @author Nico Stieler
 *
 */
public class EntityHierarchyUpdater<E> {
    /**
     * Extracts the information from an axiom if it is a node or edge defining axiom 
     * and what the declared entity respectively the child and parent entities are.
     *
     * @param <E>
     */
    public static interface EntityHandler<E> {
        Class<? extends E> getEntityType();
        
        Set<AxiomType<? extends OWLAxiom>> getHandledAxiomTypes();

        boolean handleNodeEntity(E entity);
        
        boolean isNodeAxiom(OWLAxiom axiom);
        E getDeclaredEntity(OWLAxiom axiom);
        
        boolean isEdgeAxiom(OWLAxiom axiom);
        List<E> getParentEntities(OWLAxiom axiom);
        List<E> getChildEntities(OWLAxiom axiom);
    }
    
    private final EntityHandler<E> _entityHandler;
    private final OWLModel _model;
    private final OWLOntology _ontology;
    private final boolean _includeImportedOntologies;
    private final OWLDataFactory _factory;
    private MutableEntityHierarchyCore<E> _hierarchy;
    private boolean _closed;

    public EntityHierarchyUpdater(OWLModel model, OWLDataFactory factory, boolean includeImportedOntologies, EntityHandler<E> entityHandler) {
        _model = model;
        try {
            _ontology = model.getOntology();
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
        _factory = factory;
        _includeImportedOntologies = includeImportedOntologies;
        _closed = false;
        _entityHandler = entityHandler;
    }
    
    private void invalidate() {
        _hierarchy = null;
    }
    
    public void close() {
        invalidate();
        _closed = true;
    }
    
    public boolean isIncludeImportedOntologies() {
        return _includeImportedOntologies;
    }
    
    public EntityHierarchy<E> getHierarchy() {
        if (_closed) {
            throw new IllegalStateException();
        }
        if (_hierarchy == null) {
            refresh();
        }
        return _hierarchy;
    }
    
    public void refresh() {
        _hierarchy = new MutableEntityHierarchyCore<E>();
        if (_ontology == null) {
            return;
        }
        Set<OWLOntology> relevantOntologies = null;
        if (_includeImportedOntologies) {
            try {
                relevantOntologies = new LinkedHashSet<OWLOntology>();
                for (OWLModel model: _model.getAllImportedOntologies()) {
                    relevantOntologies.add(model.getOntology());
                }
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
            relevantOntologies.add(_ontology);
        } else {
            relevantOntologies = Collections.singleton(_ontology);
        }
        for (OWLOntology o: relevantOntologies) {
            if (OWLEntity.class.isAssignableFrom(_entityHandler.getEntityType())) {
                Class<? extends OWLEntity> entityType = Cast.cast(_entityHandler.getEntityType());
                Set<? extends OWLEntity> entities = OWLUtilities.getReferencedEntities(o, entityType);
                for (OWLEntity entity : entities) {
                    E concreteEntity = Cast.cast(entity);
                    if (_entityHandler.handleNodeEntity(concreteEntity)) {
                        OWLAxiom axiom = _factory.getOWLDeclarationAxiom(entity);
                        _hierarchy.addAxiom(axiom, o);
                        _hierarchy.addNodeAxiom(concreteEntity, axiom);
                    }
                }
            }
            for (AxiomType<? extends OWLAxiom> axiomType: _entityHandler.getHandledAxiomTypes()) {
                Set<? extends OWLAxiom> axioms = o.getAxioms(axiomType);
                for (OWLAxiom axiom: axioms) {
                    if (_entityHandler.isNodeAxiom(axiom)) {
                        assertNonDeclarationAxiom(axiom);
                        _hierarchy.addAxiom(axiom, o);
                        _hierarchy.addNodeAxiom(_entityHandler.getDeclaredEntity(axiom), axiom);
                    } else if (_entityHandler.isEdgeAxiom(axiom)) {
                        _hierarchy.addAxiom(axiom, o);
                        List<E> parents = _entityHandler.getParentEntities(axiom);
                        List<E> children = _entityHandler.getChildEntities(axiom);
                        for (int i = 0; i < parents.size(); i++) {
                            E parent = parents.get(i);
                            E child = children.get(i);
                            _hierarchy.addEdgeAxiom(child, parent, axiom);
                        }
                    }
                }
            }
        }
        if(_entityHandler instanceof OWLClassHandler){// && E == OWLClassExpression
//            insert implicit edges owl:Thing - "root" class
            OWLClassExpression[] rootEntities = new OWLClassExpression[0];
            rootEntities = _hierarchy.getRootEntities().toArray(rootEntities);
            for(OWLClassExpression x : rootEntities){
//                System.out.println(x);
                if(! x.equals(_factory.getOWLThing())){
                    ImplicitOWLSubClassOfAxiomImpl implicitAxiom = new ImplicitOWLSubClassOfAxiomImpl(_factory, (OWLClassExpression) x, _factory.getOWLThing());
                    _hierarchy.addAxiom(implicitAxiom, _ontology);
                    _hierarchy.addEdgeAxiom((E) x, (E) _factory.getOWLThing(), implicitAxiom);
                }
            }
        }
    }
    
    public void processEvent(OWLOntology ontology, List<? extends OWLAxiomChange> changes, Set<OWLEntity> potentiallyAddedEntities, Set<OWLEntity> removedEntities) {
        if (_hierarchy == null) {
            return;
        }
        for (OWLAxiomChange e: changes) {
            OWLAxiom axiom = (OWLAxiom)e.getAxiom();
            if (_entityHandler.isNodeAxiom(axiom)) {
                assertNonDeclarationAxiom(axiom);
                if (e instanceof AddAxiom) {
                    if (_hierarchy.addAxiom(axiom, ontology)) {
                        _hierarchy.addNodeAxiom(_entityHandler.getDeclaredEntity(axiom), axiom);
                    } else {
                        // missed update or implementation bug
                    }
                } else {
                    if (_hierarchy.containsAxiom(axiom, ontology)) {
                        if (_hierarchy.getOntologyIDs(axiom).size() == 1) {
                            _hierarchy.removeNodeAxiom(_entityHandler.getDeclaredEntity(axiom), axiom);
                        }
                        _hierarchy.removeAxiom(axiom, ontology);
                    } else {
                        // missed update or implementation bug
                    }
                }
            } else if (_entityHandler.isEdgeAxiom(axiom)) {
                if (e instanceof AddAxiom) {
                    if (_hierarchy.addAxiom(axiom, ontology)) {
                        List<E> parents = _entityHandler.getParentEntities(axiom);
                        List<E> children = _entityHandler.getChildEntities(axiom);
                        for (int i = 0; i < parents.size(); i++) {
                            E parent = parents.get(i);
                            E child = children.get(i);
                            if(_hierarchy.addEdgeAxiom(child, parent, axiom) && _entityHandler instanceof OWLClassHandler ){
                                ImplicitOWLSubClassOfAxiomImpl removeAxiom = new ImplicitOWLSubClassOfAxiomImpl(_factory, (OWLClassExpression) child, _factory.getOWLThing());
                                if(_hierarchy.removeAxiom(removeAxiom, ontology)){
                                    _hierarchy.removeEdgeAxiom(child, (E) _factory.getOWLThing(), removeAxiom);
                                }
                            }
                        }
                    } else {
                        // missed update or implementation bug
                    }
                } else {
                    if (_hierarchy.containsAxiom(axiom, ontology)) {
                        if (_hierarchy.getOntologyIDs(axiom).size() == 1) {
                            List<E> parents = _entityHandler.getParentEntities(axiom);
                            List<E> children = _entityHandler.getChildEntities(axiom);
                            for (int i = 0; i < parents.size(); i++) {
                                E parent = parents.get(i);
                                E child = children.get(i);
                                _hierarchy.removeEdgeAxiom(child, parent, axiom);
                                //NICO if Entity still exists and its root add implicit edge to owl:Thing: it should still exist
                            }
                        }
                        _hierarchy.removeAxiom(axiom, ontology);
                    } else {
                        // missed update or implementation bug
                    }
                }
            }
        }
        Class<? extends E> entityType = _entityHandler.getEntityType();
        if (OWLEntity.class.isAssignableFrom(entityType)) {
            for (OWLEntity entity: removedEntities) {
                if (_entityHandler.getEntityType().isInstance(entity)) {
                    E concreteEntity = Cast.cast(entity);
                    if (_entityHandler.handleNodeEntity(concreteEntity)) {
                        OWLAxiom axiom = _factory.getOWLDeclarationAxiom((OWLEntity)concreteEntity);
                        if (_hierarchy.containsAxiom(axiom, ontology)) {
                            if (_hierarchy.getOntologyIDs(axiom).size() == 1) {
                                _hierarchy.removeNodeAxiom(concreteEntity, axiom);
                            }
                            _hierarchy.removeAxiom(axiom, ontology);
                            if(_entityHandler instanceof OWLClassHandler ){
                                ImplicitOWLSubClassOfAxiomImpl removeAxiom = new ImplicitOWLSubClassOfAxiomImpl(_factory, (OWLClassExpression) concreteEntity, _factory.getOWLThing());
                                if(_hierarchy.removeAxiom(removeAxiom, ontology)){
                                    _hierarchy.removeEdgeAxiom(concreteEntity, (E) _factory.getOWLThing(), removeAxiom);
                                }
                            }
                        } else {
                            // missed update or implementation bug
                        }
                    }
                }
            }
            for (OWLEntity entity: potentiallyAddedEntities) {
                if (_entityHandler.getEntityType().isInstance(entity)) {
                    E concreteEntity = Cast.cast(entity);
                    if (_entityHandler.handleNodeEntity(concreteEntity)) {
                        OWLAxiom axiom = _factory.getOWLDeclarationAxiom((OWLEntity)concreteEntity);
                        if (_hierarchy.addAxiom(axiom, ontology)) {
                            _hierarchy.addNodeAxiom(concreteEntity, axiom);
                            if(_entityHandler instanceof OWLClassHandler && _hierarchy.getRootEntities().contains(concreteEntity)){
                                ImplicitOWLSubClassOfAxiomImpl implicitAxiom = new ImplicitOWLSubClassOfAxiomImpl(_factory, (OWLClassExpression) concreteEntity, _factory.getOWLThing());
                                if(_hierarchy.addAxiom(implicitAxiom, _ontology))
                                    _hierarchy.addEdgeAxiom((E) concreteEntity, (E) _factory.getOWLThing(), implicitAxiom);
                            }
                        } else {
                            // can happen regularly since a potentially added entity must not necessarily be new
                        }
                    }
                }
            }
        }
    }

    /**
     * The implementation uses {@link OWLDeclarationAxiom} axioms to store entities in the hierarchy.
     * The {@link EntityHandler} must not handle this type of axioms explicit as node axioms.
     * 
     * @param axiom
     */
    private static void assertNonDeclarationAxiom(OWLAxiom axiom) {
        assert(!(axiom instanceof OWLDeclarationAxiom));
    }
}
