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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author krekeler
 *
 */
public class MutableEntityHierarchyCore<E> implements MutableEntityHierarchy<E> {
    private static final boolean ASSERT_VALID = false;
    private static class NodeCore<E> implements Node<E> {
        private E _entity;
        private Set<OWLAxiom> _declarationAxioms;
        private Map<E,Set<OWLAxiom>> _axiomsByChild;
        private Map<E,Set<OWLAxiom>> _axiomsByParent;

        public NodeCore(E entity) {
            _entity = entity;
        }

        private Set<E> emptyEntitySet() {
            return Collections.emptySet();
        }
        private Set<OWLAxiom> emptyAxiomSet() {
            return Collections.emptySet();
        }
        
        public E getEntity() {
            return _entity;
        }
        public Set<OWLAxiom> getDeclarationAxioms() {
            return _declarationAxioms == null ? emptyAxiomSet() : _declarationAxioms;
        }
        public Set<E> getChildren() {
            return _axiomsByChild == null ? emptyEntitySet() : _axiomsByChild.keySet();
        }
        public Set<E> getParents() {
            return _axiomsByParent == null ? emptyEntitySet() : _axiomsByParent.keySet();
        }
        public Set<OWLAxiom> getChildRelationDefiningAxioms(E child) {
            return _axiomsByChild == null ? emptyAxiomSet() : _axiomsByChild.get(child);
        }
        public Set<OWLAxiom> getParentRelationDefiningAxioms(E parent) {
            return _axiomsByParent == null ? emptyAxiomSet() : _axiomsByParent.get(parent);
        }
        
        public boolean addDeclarationAxiom(OWLAxiom axiom) {
            if (_declarationAxioms == null) {
                _declarationAxioms = new LinkedHashSet<OWLAxiom>(Collections.singleton(axiom));
                return true;
            } else {
                return _declarationAxioms.add(axiom);
            }
        }
        public boolean addChildRelationDefiningAxiom(E child, OWLAxiom axiom) {
            if (_axiomsByChild == null) {
                _axiomsByChild = new LinkedHashMap<E,Set<OWLAxiom>>();
            }
            if (!_axiomsByChild.containsKey(child)) {
                _axiomsByChild.put(child, new LinkedHashSet<OWLAxiom>(Collections.singleton(axiom)));
                return true;
            } else {
                return _axiomsByChild.get(child).add(axiom);
            }
        }
        public boolean addParentRelationDefiningAxiom(E parent, OWLAxiom axiom) {
            if (_axiomsByParent == null) {
                _axiomsByParent = new LinkedHashMap<E,Set<OWLAxiom>>(1);
            }
            if (!_axiomsByParent.containsKey(parent)) {
                _axiomsByParent.put(parent, new LinkedHashSet<OWLAxiom>(Collections.singleton(axiom)));
                return true;
            } else {
                return _axiomsByParent.get(parent).add(axiom);
            }
        }
        public boolean removeDeclarationAxiom(OWLAxiom axiom) {
            if (_declarationAxioms == null) {
                return false;
            }
            if (!_declarationAxioms.remove(axiom)) {
                return false;
            }
            if (_declarationAxioms.size() == 0) {
                _declarationAxioms = null;
            }
            return true;
        }
        public boolean removeChildRelationDefiningAxiom(E child, OWLAxiom axiom) {
            if (_axiomsByChild == null || !_axiomsByChild.containsKey(child)) {
                return false;
            }
            Set<OWLAxiom> axioms = _axiomsByChild.get(child);
            if (!axioms.remove(axiom)) {
                return false;
            }
            if (axioms.size() == 0) {
                _axiomsByChild.remove(child);
            }
            if (_axiomsByChild.size() == 0) {
                _axiomsByChild = null;
            }
            return true;
        }
        public boolean removeParentRelationDefiningAxiom(E parent, OWLAxiom axiom) {
            if (_axiomsByParent == null || !_axiomsByParent.containsKey(parent)) {
                return false;
            }
            Set<OWLAxiom> axioms = _axiomsByParent.get(parent);
            if (!axioms.remove(axiom)) {
                return false;
            }
            if (axioms.size() == 0) {
                _axiomsByParent.remove(parent);
            }
            if (_axiomsByParent.size() == 0) {
                _axiomsByParent = null;
            }
            return true;
        }
        @Override
        public String toString() {
            if (_entity == null) {
                return ""; //$NON-NLS-1$
            }
            return _entity.toString();
        }
    }
    private static class EquivalenceClassCore<E> implements EquivalenceClass<E> {
        private E _representative;
        private Set<E> _entities;
        private Set<E> _childRepresentants;
        private Set<E> _parentRepresentants;

        public EquivalenceClassCore(E representative, Set<E> entities) {
            this(representative, entities, null, null);
        }
        public EquivalenceClassCore(E representative, Set<E> entities, Set<E> childRepresentatives, Set<E> parentRepresentatives) {
            assert(entities.contains(representative));
            _representative = representative;
            _entities = entities;
            _childRepresentants = childRepresentatives;
            _parentRepresentants = parentRepresentatives;
        }
        
        private Set<E> emptyEntitySet() {
            return Collections.emptySet();
        }
        
        public E getRepresentative() {
            return _representative;
        }
        public Set<E> getEntities() {
            return _entities == null ? emptyEntitySet() : _entities;
        }
        public Set<E> getChildRepresentatives() {
            return _childRepresentants == null ? emptyEntitySet() : _childRepresentants;
        }
        public Set<E> getParentRepresentatives() {
            return _parentRepresentants == null ? emptyEntitySet() : _parentRepresentants;
        }
        
        public boolean addEntity(E entity) {
            if (_entities == null) {
                _entities = new LinkedHashSet<E>(Collections.singleton(entity));
                return true;
            }
            return _entities.add(entity);
        }
        public boolean addChildRepresentative(E entity) {
            if (_childRepresentants == null) {
                _childRepresentants = new LinkedHashSet<E>(Collections.singleton(entity));
                return true;
            }
            return _childRepresentants.add(entity);
        }
        public boolean addChildRepresentatives(Set<E> entities) {
            if (_childRepresentants == null) {
                if (entities.size() == 0) {
                    return false;
                }
                _childRepresentants = new LinkedHashSet<E>(entities);
                return true;
            }
            return _childRepresentants.addAll(entities);
        }
        public boolean addParentRepresentative(E entity) {
            if (_parentRepresentants == null) {
                _parentRepresentants = new LinkedHashSet<E>(Collections.singleton(entity));
                return true;
            }
            return _parentRepresentants.add(entity);
        }
        public boolean addParentRepresentatives(Set<E> entities) {
            if (_parentRepresentants == null) {
                if (entities.size() == 0) {
                    return false;
                }
                _parentRepresentants = new LinkedHashSet<E>(entities);
                return true;
            }
            return _parentRepresentants.addAll(entities);
        }
        public boolean removeEntity(E entity) {
            if (_entities == null) {
                return false;
            }
            if (!_entities.remove(entity)) {
                return false;
            }
            if (_entities.size() == 0) {
                _entities = null;
            }
            return true;
        }
        public boolean removeChildRepresentative(E child) {
            if (_childRepresentants == null) {
                return false;
            }
            if (!_childRepresentants.remove(child)) {
                return false;
            }
            if (_childRepresentants.size() == 0) {
                _childRepresentants = null;
            }
            return true;
        }
        public boolean removeParentRepresentative(E parent) {
            if (_parentRepresentants == null) {
                return false;
            }
            if (!_parentRepresentants.remove(parent)) {
                return false;
            }
            if (_parentRepresentants.size() == 0) {
                _parentRepresentants = null;
            }
            return true;
        }
        @Override
        public String toString() {
            if (_representative == null) {
                return "[]"; //$NON-NLS-1$
            }
            return "[" + _representative.toString() + "]=" + _entities.toString();  //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    private Map<E,Node<E>> _nodeByEntity;
    private Map<E,EquivalenceClass<E>> _equivalenceClassByEntity;
    private Map<OWLAxiom,Set<OWLOntology>> _ontologyIDsByAxiom;
    private Set<E> _rootEntities;
    private Set<E> _rootEquivalenceClassRepresentants;

    public MutableEntityHierarchyCore() {
        _nodeByEntity = new LinkedHashMap<E,Node<E>>();
        _equivalenceClassByEntity = new LinkedHashMap<E,EquivalenceClass<E>>();
        _ontologyIDsByAxiom = new LinkedHashMap<OWLAxiom,Set<OWLOntology>>();
        _rootEntities = new LinkedHashSet<E>();
        _rootEquivalenceClassRepresentants = new LinkedHashSet<E>();
    }
    
    public EquivalenceClass<E> getEquivalenceClass(E entity) {
        return _equivalenceClassByEntity.get(entity);
    }
    public Node<E> getNode(E entity) {
        return _nodeByEntity.get(entity);
    }
    public Set<OWLOntology> getOntologyIDs(OWLAxiom axiom) {
        return _ontologyIDsByAxiom.get(axiom);
    }
    public boolean containsAxiom(OWLAxiom axiom) {
        return _ontologyIDsByAxiom.containsKey(axiom);
    }
    public Set<E> getRootEntities() {
        return _rootEntities;
    }
    public Set<E> getRootEquivalenceClassRepresentatives() {
        return _rootEquivalenceClassRepresentants;
    }

    private void checkAxiom(OWLAxiom axiom) {
        if (!_ontologyIDsByAxiom.containsKey(axiom)) {
            throw new IllegalStateException("Axiom must be added first."); //$NON-NLS-1$
        }
    }
    
    private boolean assertNode(E entity) {
        if (!_nodeByEntity.containsKey(entity)) {
            _nodeByEntity.put(entity, new NodeCore<E>(entity));
            _equivalenceClassByEntity.put(entity, new EquivalenceClassCore<E>(entity, new LinkedHashSet<E>(Collections.singleton(entity))));
            _rootEntities.add(entity);
            _rootEquivalenceClassRepresentants.add(entity);
            return true;
        }
        return false;
    }
    
    private E chooseRepresentative(E oldRepresentative, Set<E> entities) {
        assert(entities.size() > 0);
        return entities.iterator().next();
    }
    
    private boolean checkRemoveNode(Node<E> node) {
        if (node.getDeclarationAxioms().size() > 0 || node.getChildren().size() > 0 || node.getParents().size() > 0) {
            return false;
        }
        E entity = node.getEntity();

        EquivalenceClassCore<E> equivalenceClass = (EquivalenceClassCore<E>)_equivalenceClassByEntity.get(entity);
        _equivalenceClassByEntity.remove(entity);
        equivalenceClass.removeEntity(entity);
        if (equivalenceClass.getRepresentative().equals(entity)) {
            assert(_rootEquivalenceClassRepresentants.contains(entity));
            _rootEquivalenceClassRepresentants.remove(entity);
            if (equivalenceClass.getEntities().size() > 0) {
                // choose another representative
                _rootEquivalenceClassRepresentants.add(chooseRepresentative(entity, equivalenceClass.getEntities()));
            } else {
                removeEquivalenceClassRelationships(equivalenceClass);
            }
        } else {
            assert(!(_rootEquivalenceClassRepresentants.contains(entity)));
        }
        
        _rootEntities.remove(entity);
        _nodeByEntity.remove(entity);
        
        return true;
    }
    
    private boolean removeEquivalenceClassRelationships(EquivalenceClassCore<E> equivalenceClass) {
        E entity = equivalenceClass.getRepresentative();
        for (E child: equivalenceClass.getChildRepresentatives()) {
            boolean check = ((EquivalenceClassCore<E>)_equivalenceClassByEntity.get(child)).removeParentRepresentative(entity);
            assert(check);
        }
        for (E parent: equivalenceClass.getParentRepresentatives()) {
            boolean check = ((EquivalenceClassCore<E>)_equivalenceClassByEntity.get(parent)).removeChildRepresentative(entity);
            assert(check);
        }
        return equivalenceClass.getChildRepresentatives().size() > 0 || equivalenceClass.getParentRepresentatives().size() > 0;
    }
    
    public boolean addAxiom(OWLAxiom axiom, OWLOntology ontologyID) {
        return checkAssertValid(addAxiomInternal(axiom, ontologyID));
    }
    private boolean addAxiomInternal(OWLAxiom axiom, OWLOntology ontologyID) {
        if (!_ontologyIDsByAxiom.containsKey(axiom)) {
            _ontologyIDsByAxiom.put(axiom, new LinkedHashSet<OWLOntology>(Collections.singleton(ontologyID)));
            return true;
        }
        return _ontologyIDsByAxiom.get(axiom).add(ontologyID);
    }
    
    public boolean removeAxiom(OWLAxiom axiom, OWLOntology ontologyID) {
        return checkAssertValid(removeAxiomInternal(axiom, ontologyID));
    }
    private boolean removeAxiomInternal(OWLAxiom axiom, OWLOntology ontologyID) {
        if (!_ontologyIDsByAxiom.containsKey(axiom)) {
            return false;
        }
        Set<OWLOntology> ontologyIDs = _ontologyIDsByAxiom.get(axiom);
        if (!ontologyIDs.remove(ontologyID)) {
            return false;
        }
        if (ontologyIDs.size() == 0) {
            _ontologyIDsByAxiom.remove(axiom);
        }
        return true;
    }

    public boolean addNodeAxiom(E entity, OWLAxiom axiom) {
        return checkAssertValid(addNodeAxiomInternal(entity, axiom));
    }
    private boolean addNodeAxiomInternal(E entity, OWLAxiom axiom) {
        checkAxiom(axiom);
        assertNode(entity);
        return ((NodeCore<E>)_nodeByEntity.get(entity)).addDeclarationAxiom(axiom);
    }
    
    public boolean removeNodeAxiom(E entity, OWLAxiom axiom) {
        return checkAssertValid(removeNodeAxiomInternal(entity, axiom));
    }
    private boolean removeNodeAxiomInternal(E entity, OWLAxiom axiom) {
        if (!_nodeByEntity.containsKey(entity)) {
            return false;
        }
        NodeCore<E> node = (NodeCore<E>)_nodeByEntity.get(entity);
        if (!node.removeDeclarationAxiom(axiom)) {
            return false;
        }
        checkRemoveNode(node);
        return true;
    }
    
    public boolean addEdgeAxiom(E child, E parent, OWLAxiom axiom) {
        return checkAssertValid(addEdgeAxiomInternal(child, parent, axiom));
    }
    private boolean addEdgeAxiomInternal(E child, E parent, OWLAxiom axiom) {
        checkAxiom(axiom);
        boolean modified = assertNode(child);
        modified |= assertNode(parent);
        NodeCore<E> childNode = (NodeCore<E>)_nodeByEntity.get(child);
        NodeCore<E> parentNode = (NodeCore<E>)_nodeByEntity.get(parent);
        
        EquivalenceClassCore<E> childEquivalenceClass = (EquivalenceClassCore<E>)_equivalenceClassByEntity.get(child);
        EquivalenceClassCore<E> parentEquivalenceClass = (EquivalenceClassCore<E>)_equivalenceClassByEntity.get(parent);
        if (!childEquivalenceClass.equals(parentEquivalenceClass)) {
            boolean closedCycle = false;
            if (!childEquivalenceClass.getChildRepresentatives().isEmpty()) { // this condition is just a performance optimization
                Set<E> inCycleEntities = new LinkedHashSet<E>();
                inCycleEntities.add(childEquivalenceClass.getRepresentative());
                if (isDescendantRepresentative(parentEquivalenceClass.getRepresentative(), inCycleEntities)) {
                    closedCycle = true;
                    modified = true;
                    // detected a new cycle containing all representatives in inCycleEntities
                    E representative = chooseRepresentative(null, inCycleEntities);

                    // compute child equivalence classes
                    Set<E> children = new LinkedHashSet<E>();
                    for (E inCycleEntity: inCycleEntities) {
                        children.addAll(getEquivalenceClass(inCycleEntity).getChildRepresentatives());
                    }
                    if (children.removeAll(inCycleEntities)) {
                        children.add(representative);
                    }
                    
                    // compute parent equivalence classes
                    Set<E> parents = new LinkedHashSet<E>();
                    for (E inCycleEntity: inCycleEntities) {
                        parents.addAll(getEquivalenceClass(inCycleEntity).getParentRepresentatives());
                    }
                    if (parents.removeAll(inCycleEntities)) {
                        parents.add(representative);
                    }
                    
                    // replace old parent representatives with new one
                    for (E ecChild: children) {
                        if (ecChild.equals(representative)) {
                            continue;
                        }
                        EquivalenceClassCore<E> equivalenceClass = (EquivalenceClassCore<E>)getEquivalenceClass(ecChild);
                        boolean check = equivalenceClass.getParentRepresentatives().removeAll(inCycleEntities);
                        assert(check);
                        equivalenceClass.addParentRepresentative(representative);
                    }
                    
                    // replace old child representatives with new one
                    for (E ecParent: parents) {
                        if (ecParent.equals(representative)) {
                            continue;
                        }
                        EquivalenceClassCore<E> equivalenceClass = (EquivalenceClassCore<E>)getEquivalenceClass(ecParent);
                        boolean check = equivalenceClass.getChildRepresentatives().removeAll(inCycleEntities);
                        assert(check);
                        equivalenceClass.addChildRepresentative(representative);
                    }

                    // get all entities contained in all old equivalence classes
                    Set<E> entities = new LinkedHashSet<E>();
                    for (E inCycleEntity: inCycleEntities) {
                        entities.addAll(_equivalenceClassByEntity.get(inCycleEntity).getEntities());
                    }
                    
                    // remove old root equivalence classes
                    _rootEquivalenceClassRepresentants.removeAll(inCycleEntities);

                    // add new root equivalence class
                    EquivalenceClassCore<E> equivalenceClass = new EquivalenceClassCore<E>(representative, entities, children, parents);
                    if (isRoot(equivalenceClass)) {
                        _rootEquivalenceClassRepresentants.add(representative);
                    }
                    
                    // set reference to new equivalence class
                    for (E entity: entities) {
                        _equivalenceClassByEntity.put(entity, equivalenceClass);
                    }
                }
            }
            if (!closedCycle) {
                modified |= childEquivalenceClass.addParentRepresentative(parentEquivalenceClass.getRepresentative());
                modified |= parentEquivalenceClass.addChildRepresentative(childEquivalenceClass.getRepresentative());
            }
        } else {
            modified |= childEquivalenceClass.addParentRepresentative(parentEquivalenceClass.getRepresentative());
            modified |= parentEquivalenceClass.addChildRepresentative(childEquivalenceClass.getRepresentative());
        }

        modified |= childNode.addParentRelationDefiningAxiom(parent, axiom);
        modified |= parentNode.addChildRelationDefiningAxiom(child, axiom);

        if (!isRoot(childEquivalenceClass)) {
            modified |= _rootEquivalenceClassRepresentants.remove(childEquivalenceClass.getRepresentative());
        }
        if (!isRoot(childNode)) {
            modified |= _rootEntities.remove(child);
        }
        
        return modified;
    }
    
    private boolean isRoot(EquivalenceClass<E> equivalenceClass) {
        Set<E> parents = equivalenceClass.getParentRepresentatives();
        return parents.isEmpty() || (parents.size() == 1 && equivalenceClass.getRepresentative().equals(parents.iterator().next()));
    }
    
    /**
     * The method tests if <code>descendant</code> is a descendant of at least one entity in <code>ancestors</code>
     * with respect to the {@link EquivalenceClass#getParentRepresentatives()} method.
     * 
     * <p>Let -> be the ancestor relation <em>without self cycles like "a -> a"</em> and *-> its reflexive closure.</p>
     * 
     * @param descendant                         The given entity, must be a representative of an equivalence class.
     * @param ancestors                          The ancestors (all of which must be equivalence class representatives).
     *                                           
     *                                           <p>Let B := { b | <tt>EXISTS</tt> a <tt>IN</tt> <code>ancestors</code>: <code>entity</code> -> b *-> a }.
     *                                           Then the method adds some elements of B to <code>ancestors</code>, not necessarily all.
     *                                           However, it is granted that if B contains elements not (already) contained in <code>ancestors</code>,
     *                                           then at least one of these elements is added to <code>ancestors</code>.</p>
     *                                           
     *                                           <p>If the visited sub-graph is anti-cyclic, then all elements of B will be added to <code>ancestors</code>.</p> 
     * @return                                   <code>true</code> iff <tt>EXISTS</tt> a <tt>IN</tt> <code>ancestors</code>: <code>entity</code> *-> a.
     */
    private boolean isDescendantRepresentative(E descendant, Set<E> ancestors) {
        return isDescendantRepresentative(descendant, ancestors, new LinkedHashSet<E>());
    }

    /**
     * The method tests if <code>descendant</code> is a descendant of at least one entity in <code>ancestors</code>
     * with respect to the {@link Node#getParents()} method.
     * 
     * <p>Let -> be the ancestor relation (which is transitive, but not necessarily reflexive) and *-> its reflexive closure.</p>
     * 
     * @param descendant                         The given entity.
     * @param ancestors                          The ancestors.
     *                                           
     *                                           <p>Let B := { b | <tt>EXISTS</tt> a <tt>IN</tt> <code>ancestors</code>: <code>entity</code> -> b *-> a }.
     *                                           Then the method adds some elements of B to <code>ancestors</code>, not necessarily all.
     *                                           However, it is granted that if B contains elements not (already) contained in <code>ancestors</code>,
     *                                           then at least one of these elements is added to <code>ancestors</code>.</p>
     *                                           
     *                                           <p>If the visited sub-graph is anti-cyclic, then all elements of B will be added to <code>ancestors</code>.</p> 
     * @return                                   <code>true</code> iff <tt>EXISTS</tt> a <tt>IN</tt> <code>ancestors</code>: <code>entity</code> *-> a.
     */
    private boolean isDescendant(E descendant, Set<E> ancestors) {
        return isDescendant(descendant, ancestors, new LinkedHashSet<E>());
    }

    private boolean isDescendantRepresentative(E descendant, Set<E> ancestors, Set<E> seenEntities) {
        assert(getEquivalenceClass(descendant).getRepresentative() == descendant);
        if (seenEntities.contains(descendant)) {
            return ancestors.contains(descendant);
        }
        seenEntities.add(descendant);
        EquivalenceClass<E> equivalenceClass = getEquivalenceClass(descendant);
        for (E parent: equivalenceClass.getParentRepresentatives()) {
            if (!descendant.equals(parent)) {
                if (isDescendantRepresentative(parent, ancestors, seenEntities)) {
                    ancestors.add(descendant);
                }
            }
        }
        return ancestors.contains(descendant);
    }
    
    private boolean isDescendant(E descendant, Set<E> ancestors, Set<E> seenEntities) {
        if (seenEntities.contains(descendant)) {
            return ancestors.contains(descendant);
        }
        seenEntities.add(descendant);
        for (E parent: _nodeByEntity.get(descendant).getParents()) {
            if (isDescendant(parent, ancestors, seenEntities)) {
                ancestors.add(descendant);
            }
        }
        return ancestors.contains(descendant);
    }
    
    public boolean removeEdgeAxiom(E child, E parent, OWLAxiom axiom) {
        return checkAssertValid(removeEdgeAxiomInternal(child, parent, axiom));
    }
    private boolean removeEdgeAxiomInternal(E child, E parent, OWLAxiom axiom) {
        if (!_nodeByEntity.containsKey(child) || !_nodeByEntity.containsKey(parent)) {
            return false;
        }
        NodeCore<E> childNode = (NodeCore<E>)_nodeByEntity.get(child);
        NodeCore<E> parentNode = (NodeCore<E>)_nodeByEntity.get(parent);
        
        EquivalenceClassCore<E> childEquivalenceClass = (EquivalenceClassCore<E>)_equivalenceClassByEntity.get(child);
        EquivalenceClassCore<E> parentEquivalenceClass = (EquivalenceClassCore<E>)_equivalenceClassByEntity.get(parent);
        boolean sameEquivalenceClass = childEquivalenceClass.equals(parentEquivalenceClass);
        
        if (!childNode.removeParentRelationDefiningAxiom(parent, axiom)) {
            return false;
        }
        boolean checkParent = parentNode.removeChildRelationDefiningAxiom(child, axiom);
        assert(checkParent);
        
        if (isRoot(childNode)) {
            _rootEntities.add(child);
        }
        
        checkRemoveNode(childNode);
        checkRemoveNode(parentNode);
        
        if (sameEquivalenceClass) {
            E representative = null;
            if (_nodeByEntity.containsKey(child)) { // child may have been removed...
                representative = _equivalenceClassByEntity.get(child).getRepresentative();
            } else if (_nodeByEntity.containsKey(parent)) { // parent may have been removed
                representative = _equivalenceClassByEntity.get(parent).getRepresentative();
            }
            if (representative != null) {
                EquivalenceClass<E> equivalenceClass = getEquivalenceClass(representative);
                Set<E> requiredEntities = getEquivalentEntities(representative);
                if (requiredEntities.size() != equivalenceClass.getEntities().size()) {
                    // remove equivalence class
                    removeEquivalenceClassRelationships((EquivalenceClassCore<E>)equivalenceClass);
                    removeAllKeys(_equivalenceClassByEntity, equivalenceClass.getEntities());
                    _rootEquivalenceClassRepresentants.remove(equivalenceClass.getRepresentative());
                    // create new ones for all entities contained in the old one
                    createEquivalenceClasses(equivalenceClass.getEntities());
                } else {
                    // equivalence class still contains all entities, we do not need to split it up...
                    assert(requiredEntities.equals(equivalenceClass.getEntities()));
                    // ... but we may need to remove a self cycle
                    if (equivalenceClass.getEntities().size() == 1 && !_nodeByEntity.get(representative).getChildren().contains(representative)) {
                        boolean check = ((EquivalenceClassCore<E>)equivalenceClass).removeChildRepresentative(representative);
                        assert(check);
                        check = ((EquivalenceClassCore<E>)equivalenceClass).removeParentRepresentative(representative);
                        assert(check);
                    }
                }
            }
        } else {
            if (_nodeByEntity.containsKey(child) && _nodeByEntity.containsKey(parent)) {
                Set<E> parentEntities = parentEquivalenceClass.getEntities();
                boolean hasEdge = false;
                for (E equivalentChild: childEquivalenceClass.getEntities()) {
                    for (E equivalentChildParent: _nodeByEntity.get(equivalentChild).getParents()) {
                        if (parentEntities.contains(equivalentChildParent)) {
                            hasEdge = true;
                            break;
                        }
                    }
                }
                if (!hasEdge) {
                    // remove now obsolete equivalence class references (has already been done if child or parent has been removed)
                    boolean check = childEquivalenceClass.removeParentRepresentative(parent);
                    assert(check);
                    check = parentEquivalenceClass.removeChildRepresentative(child);
                    assert(check);
                    if (isRoot(childEquivalenceClass)) {
                        _rootEquivalenceClassRepresentants.add(child);
                    }
                }
            }
        }
        
        return true;
    }
    
    private static <K,V> boolean removeAllKeys(Map<K,V> map, Collection<K> keys) {
        boolean modified = false;
        for (K key: keys) {
            modified |= map.containsKey(key);
            map.remove(key);
        }
        return modified;
    }
    
    private LinkedHashSet<E> getEquivalentEntities(E entity) {
        LinkedHashSet<E> equivalentEntities = new LinkedHashSet<E>();
        equivalentEntities.add(entity);
        int oldSize = 0;
        while (oldSize != equivalentEntities.size()) {
            oldSize = equivalentEntities.size();
            isDescendant(entity, equivalentEntities);
        }
        equivalentEntities.add(entity);
        return equivalentEntities;
    }
    
    private boolean isRoot(Node<E> node) {
        Set<E> parents = node.getParents();
        return parents.isEmpty() || (parents.size() == 1 && node.getEntity().equals(parents.iterator().next()));
    }
    
    private void createEquivalenceClasses(Set<E> entities) {
        assert(!containsOneOf(_equivalenceClassByEntity.keySet(), entities));
        if (entities.size() == 0) {
            return;
        }
        Set<EquivalenceClassCore<E>> equivalenceClasses = new LinkedHashSet<EquivalenceClassCore<E>>();
        Set<E> equivalentEntities = getEquivalentEntities(entities.iterator().next());
        while (entities.size() > 0) {
            EquivalenceClassCore<E> equivalenceClass = new EquivalenceClassCore<E>(chooseRepresentative(null, equivalentEntities), equivalentEntities);
            equivalenceClasses.add(equivalenceClass);
            
            for (E entity: equivalentEntities) {
                boolean check = _equivalenceClassByEntity.put(entity, equivalenceClass) == null;
                assert(check);
            }
            
            entities.removeAll(equivalentEntities);
            if (entities.size() > 0) {
                equivalentEntities = getEquivalentEntities(entities.iterator().next());
            }
        }
        for (EquivalenceClassCore<E> equivalenceClass: equivalenceClasses) {
            createEquivalenceClassRelationships(equivalenceClass);
        }
        for (EquivalenceClassCore<E> equivalenceClass: equivalenceClasses) {
            if (isRoot(equivalenceClass)) {
                boolean check = _rootEquivalenceClassRepresentants.add(equivalenceClass.getRepresentative());
                assert(check);
            }
        }
    }
    
    private static <E> boolean containsOneOf(Set<E> items, Set<E> keys) {
        for (E key: keys) {
            if (items.contains(key)) {
                return true;
            }
        }
        return false;
    }

    private void createEquivalenceClassRelationships(EquivalenceClassCore<E> equivalenceClass) {
        assert(_equivalenceClassByEntity.keySet().equals(_nodeByEntity.keySet()));
        Set<E> children = new LinkedHashSet<E>();
        Set<E> parents = new LinkedHashSet<E>();
        for (E entity: equivalenceClass.getEntities()) {
            Node<E> node = _nodeByEntity.get(entity);
            for (E child: node.getChildren()) {
                children.add(_equivalenceClassByEntity.get(child).getRepresentative());
            }
            for (E parent: node.getParents()) {
                parents.add(_equivalenceClassByEntity.get(parent).getRepresentative());
            }
        }
        equivalenceClass.addChildRepresentatives(children);
        equivalenceClass.addParentRepresentatives(parents);
        E representative = equivalenceClass.getRepresentative();
        for (E child: children) {
            ((EquivalenceClassCore<E>)_equivalenceClassByEntity.get(child)).addParentRepresentative(representative);
        }
        for (E parent: parents) {
            ((EquivalenceClassCore<E>)_equivalenceClassByEntity.get(parent)).addChildRepresentative(representative);
        }
    }
    
    public boolean containsAxiom(OWLAxiom axiom, OWLOntology ontologyID) {
        return _ontologyIDsByAxiom.containsKey(axiom) && _ontologyIDsByAxiom.get(axiom).contains(ontologyID);
    }
    
    private boolean checkAssertValid(boolean returnValue) {
        if (ASSERT_VALID) {
            assertValid();
        }
        return returnValue;
    }
    
    /**
     * This method is very slow, should be used in explicit testing sessions only.
     */
    private void assertValid() {
        for (E entity: _nodeByEntity.keySet()) {
            Node<E> node = _nodeByEntity.get(entity);
            assert(entity.equals(node.getEntity()));
            for (E parent: node.getParents()) {
                assert(_nodeByEntity.containsKey(parent));
                assert(getNode(parent).getChildren().contains(entity));
                for (OWLAxiom axiom: node.getParentRelationDefiningAxioms(parent)) {
                    assert(_ontologyIDsByAxiom.containsKey(axiom));
                }
            }
            for (E child: node.getChildren()) {
                assert(_nodeByEntity.containsKey(child));
                assert(getNode(child).getParents().contains(entity));
                for (OWLAxiom axiom: node.getChildRelationDefiningAxioms(child)) {
                    assert(_ontologyIDsByAxiom.containsKey(axiom));
                }
            }
            for (OWLAxiom axiom: node.getDeclarationAxioms()) {
                assert(_ontologyIDsByAxiom.containsKey(axiom));
            }
            
            assert(_equivalenceClassByEntity.containsKey(entity));
        }
        for (E entity: _equivalenceClassByEntity.keySet()) {
            EquivalenceClass<E> equivalenceClass = _equivalenceClassByEntity.get(entity);
            if (entity.equals(equivalenceClass.getRepresentative())) {
                assert(_rootEquivalenceClassRepresentants.contains(entity) == isRoot(equivalenceClass));
            }
            assert(equivalenceClass.getEntities().contains(entity));
            Set<E> expectedEntities = getEquivalentEntities(entity);
            assert(equivalenceClass.getEntities().equals(expectedEntities));
            Set<E> expectedChildren = new LinkedHashSet<E>();
            Set<E> expectedParents = new LinkedHashSet<E>();
            for (E e: equivalenceClass.getEntities()) {
                assert(isDescendant(entity, new LinkedHashSet<E>(Collections.singleton(e))));
                assert(isDescendant(e, new LinkedHashSet<E>(Collections.singleton(entity))));
                for (E child: getNode(e).getChildren()) {
                    expectedChildren.add(getEquivalenceClass(child).getRepresentative());
                }
                for (E parent: getNode(e).getParents()) {
                    expectedParents.add(getEquivalenceClass(parent).getRepresentative());
                }
            }
            assert(expectedChildren.equals(equivalenceClass.getChildRepresentatives()));
            assert(expectedParents.equals(equivalenceClass.getParentRepresentatives()));
        }
        assert(_nodeByEntity.keySet().containsAll(_equivalenceClassByEntity.keySet()));
        assert(_nodeByEntity.keySet().containsAll(_rootEntities));
        assert(_nodeByEntity.keySet().containsAll(_rootEquivalenceClassRepresentants));
    }
}
