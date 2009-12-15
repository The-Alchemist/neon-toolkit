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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;

import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;

public interface OWLModel {

    /**
     * Indicates how the occurrences of an item within a set of axioms should be grouped.<br/>
     * <br/>
     * Please have a look at <code>ItemCollector.getItems(...)</code>.
     */
    public enum ItemHitsGrouping {
        OnePerAxiom,OnePerOntology,OneForThisOneForOtherOntologies,OneAtAll;
    }

    /**
     * Indicates if the model has been closed before and thereby calling any other methods my cause exceptions.
     * 
     * @return                                  <code>true</code> if the model has been closed.
     */
    boolean isClosed() throws NeOnCoreException;
    
    /**
     * @return the _projectId
     */
    public String getProjectId() throws NeOnCoreException;

    /**
     * Get the ontology project to which the ontology belongs to.
     * 
     * @return
     * @throws NeOnCoreException
     */
    IOntologyProject getOntologyProject() throws NeOnCoreException;
    
    /**
     * registers an OwlAxiomListener for this OwlModel
     * 
     * @param listener
     * @param clazzes
     */
    void addAxiomListener(OWLAxiomListener listener, Class<? extends OWLAxiom>[] clazzes) throws NeOnCoreException;

    /**
     * removes the OwlAxiomListener from this OwlModel
     * 
     * @param listener
     */
    void removeAxiomListener(OWLAxiomListener listener) throws NeOnCoreException;

    /**
     * Cleans internal caches, e.g. for the class hierarchy.
     */
    void cleanCaches() throws NeOnCoreException;
    
    /**
     * @param owlEntityId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLAnnotationAssertionAxiom> getAnnotations(String owlEntityId) throws NeOnCoreException;

    /**
     * @param owlEntityId
     * @param annotationPropertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLAnnotationValue> getAnnotations(String owlEntityId, String annotationPropertyId) throws NeOnCoreException;

    /**
     * @param owlAnonymousIndividual
     * @param annotationPropertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLAnnotationValue> getAnnotations(OWLAnonymousIndividual owlAnonymousIndividual, String annotationPropertyId) throws NeOnCoreException;

    /**
     * @return directly imported ontologies
     */
    public Set<OWLModel> getImportedOntologies() throws NeOnCoreException;

    /**
     * @return (also indirectly) imported ontologies
     */
    public Set<OWLModel> getAllImportedOntologies() throws NeOnCoreException;

    /**
     * adds an open ontology to the list of imports for this owl ontology
     * 
     * @param newOntology
     * @throws NeOnCoreException
     */
    public void addToImports(OWLModel newOntology) throws NeOnCoreException;

    /**
     * removes an ontology from the list of imports for this owl ontology
     * 
     * @param oldOntology
     * @throws NeOnCoreException
     */
    public void removeFromImports(OWLModel oldOntology) throws NeOnCoreException;

    /**
     * @return the default namespace of this ontology
     */
    public String getDefaultNamespace() throws NeOnCoreException;

    /**
     * sets the default namespace of this ontology
     */
    public void setDefaultNamespace(String namespace) throws NeOnCoreException;

    /**
     * @return
     * @throws NeOnCoreException
     */
    public OWLNamespaces getNamespaces() throws NeOnCoreException;

    /**
     * @param prefix
     * @param namespace
     * @throws NeOnCoreException
     */
    public void setNamespacePrefix(String prefix, String namespace) throws NeOnCoreException;

    /**
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLAnnotation> getOntologyAnnotations() throws NeOnCoreException;

    /**
     * @param annotationPropertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<Object> getOntologyAnnotations(String annotationPropertyId) throws NeOnCoreException;

    /**
     * Get all entities of a given type occurring in the ontology.
     * 
     * @param <E>
     * @param entityType
     * @param includeImportedOntologies
     * @return
     * @throws NeOnCoreException
     */
    public <E extends OWLObject> Set<E> getEntities(Class<E> entityType, boolean includeImportedOntologies) throws NeOnCoreException;

    /**
     * Get all <code>Datatype</code>s 'contained' in the ontology.
     * 
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLDatatype> getAllDatatypes() throws NeOnCoreException;

    /**
     * Returns a list of named classes that do not have named super descriptions.
     * 
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLClass> getRootClasses() throws NeOnCoreException;

    /**
     * Test if a named class is a root class.
     * 
     * @param clazz
     * @return
     * @throws NeOnCoreException
     */
    public boolean isRootClass(OWLClass clazz) throws NeOnCoreException;

    /**
     * returns a list of all named classes
     * 
     */
    public Set<OWLClass> getAllClasses() throws NeOnCoreException;

    /**
     * returns a list of all named classes. Optionally includes imported axioms
     * 
     */
    public Set<OWLClass> getAllClasses(boolean includeImported) throws NeOnCoreException;

    /**
     * returns a list of all individuals
     * 
     */
    public Set<OWLIndividual> getAllIndividuals() throws NeOnCoreException;

    /**
     * returns a list of all individuals. Optionally includes imported axioms
     * 
     */
    public Set<OWLIndividual> getAllIndividuals(boolean includeImported) throws NeOnCoreException;

    /**
     * @param superClassId an OWLClass for which to return its subOWLClasses
     * @param ontologId
     * @param projectId the project ID representing a KAON2Connection
     * @return a list of named classes with given class as super description
     */
    public Set<OWLClass> getSubClasses(String superClassId) throws NeOnCoreException;

    /**
     * Get the super classes of a class in the class hierarchy.<br/>
     * <br/>
     * This is the counterpart to <code>getSubClasses(String)</code>.<br/>
     * <br/>
     * Note that <code>getSuperClasses(String)</code> does only return such classes which are super classes thru a <code>SubClassOf</code> axiom, while this
     * method might take other axioms into account.
     * 
     * @param subClassId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLClass> getSuperClassesInClassHierarchy(String subClassId) throws NeOnCoreException;

    /**
     * Get the super descriptions of a description in the class hierarchy.<br/>
     * <br/>
     * This is the counterpart to <code>getSubDescriptions(Description)</code>.<br/>
     * <br/>
     * Note that <code>getSuperDescriptions(String)</code> does only return such descriptions which are super descriptions thru a <code>SubClassOf</code> axiom,
     * while this method might take other axioms into account.
     * 
     * @param subClassId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLClassExpression> getSuperDescriptionsInClassHierarchy(OWLClassExpression subDescription) throws NeOnCoreException;

    /**
     * 
     * @param classId
     * @param ontologyId
     * @param projectId the project ID representing a KAON2Connection
     * @return a list of all (named) superclasses of passed class
     * @throws NeOnCoreException
     */
    public Set<OWLClass> getSuperClasses(String classId) throws NeOnCoreException;

    /**
     * 
     * @param classId
     * @param ontologyId
     * @param projectId the project ID representing a KAON2Connection
     * @return a list of all equivalent descriptions of passed class
     * @throws NeOnCoreException
     */
    public Set<OWLClass> getEquivalentClasses(String classId) throws NeOnCoreException;

    /**
     * 
     * @param classId
     * @param ontologyId
     * @param projectId the project ID representing a KAON2Connection
     * @return a list of all superdescriptions (excluding named classes and excluding restrictions) of passed class
     * @throws NeOnCoreException
     */
    public Set<OWLClassExpression> getSuperDescriptions(String classId) throws NeOnCoreException;

    /**
     * 
     * @param classId
     * @param ontologyId
     * @param projectId the project ID representing a KAON2Connection
     * @return a list of all equivalent descriptions (excluding named classes and excluding restrictions) of passed class
     * @throws NeOnCoreException
     */
    public Set<OWLClassExpression> getEquivalentDescriptions(String classId) throws NeOnCoreException;

    /**
     * 
     * @param classId
     * @param ontologyId
     * @param projectId the project ID representing a KAON2Connection
     * @return a list of all restriction of which passed class is a subclass
     * @throws NeOnCoreException
     */
    public Set<OWLClassExpression> getSuperRestrictions(String classId) throws NeOnCoreException;

    /**
     * 
     * @param classId
     * @param ontologyId
     * @param projectId
     * @return a list of all restriction to which passed class is equivalent
     * @throws NeOnCoreException
     */
    public Set<OWLClassExpression> getEquivalentRestrictions(String classId) throws NeOnCoreException;

    /**
     * 
     * @param classId
     * @param ontologyId
     * @param projectId
     * @return a list of all Descriptions that are disjoint topassed class
     * @throws NeOnCoreException
     */
    public Set<OWLClassExpression> getDisjointDescriptions(String classId) throws NeOnCoreException;

    /**
     * returns a list of all direct individuals of the passed class
     */
    public Set<OWLIndividual> getIndividuals(String classId) throws NeOnCoreException;

    /**
     * returns a list of all individuals of the passed class (including inherited ones) Note: This method includes trivial reasoning.
     * 
     * @param classId
     * @param ontologyId
     * @param projectId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLIndividual> getAllIndividuals(String classId) throws NeOnCoreException;

    /**
     * Returns a list of all complex classes that do not have named classes as subclasses and are root descriptions.<br/>
     * <br/>
     * Note: At the moment only such complex classes C are returned for which there is a SubClassOf(D,C) axiom!
     * 
     */
    public Set<OWLClassExpression> getComplexClasses() throws NeOnCoreException;

    /**
     * returns a list of OWL object properties that do not have super properties.
     * 
     */
    public Set<OWLObjectProperty> getRootObjectProperties() throws NeOnCoreException;

    /**
     * returns a list of all OWL object properties defined in this ontology.
     * 
     */
    public Set<OWLObjectProperty> getAllObjectProperties() throws NeOnCoreException;

    /**
     * returns a list of all OWL object properties defined in this ontology. 
     * @param includeImported indicates whether imported ontologies shall be considered or not. 
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLObjectProperty> getAllObjectProperties(boolean includeImported) throws NeOnCoreException;

    /**
     * returns a list of all OWL data properties defined in this ontology.
     * 
     */
    public Set<OWLDataProperty> getAllDataProperties() throws NeOnCoreException;

    /**
     * returns a list of all OWL data properties defined in this ontology.
     * @param includeImported indicates whether imported ontologies shall be considered or not. 
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLDataProperty> getAllDataProperties(boolean includeImported) throws NeOnCoreException;
    
    /**
     * returns a list of all OWL annotation properties defined in this ontology.
     * 
     */
    public Set<OWLAnnotationProperty> getAllAnnotationProperties() throws NeOnCoreException;

    /**
     * returns a list of all OWL annotation properties defined in this ontology.
     * @param includeImported indicates whether imported ontologies shall be considered or not. 
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLAnnotationProperty> getAllAnnotationProperties(boolean includeImported) throws NeOnCoreException;

    /**
     * returns a list of all OWL ontology annotation properties as defined in http://www.w3.org/TR/owl-ref/#Header In OWL DL we cannot define new ontology
     * annotation properties.
     */
    @Deprecated //in OWL2 there is no difference between annotationPropery and ontologyAnnotationProperty 
    public Set<OWLAnnotationProperty> getAllOntologyAnnotationProperties() throws NeOnCoreException;

    /**
     * returns a list of OWL data properties that do not have super properties.
     * 
     */
    public Set<OWLDataProperty> getRootDataProperties() throws NeOnCoreException;

    /**
     * returns a list of OWL annotation properties that do not have super properties. In OWL-DL there are no other annotation properties.
     * 
     */
    public Set<OWLAnnotationProperty> getRootAnnotationProperties() throws NeOnCoreException;

    /**
     * returns a list of all direct the subpoperties of passed OWL object property
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLObjectProperty> getSubObjectProperties(String propertyId) throws NeOnCoreException;

    public Set<List<OWLObjectProperty>> getSubPropertyChains(String propertyId) throws NeOnCoreException;
    
    public Set<ItemHits<List<OWLObjectPropertyExpression>,OWLSubPropertyChainOfAxiom>> getSubPropertyChainOfHits(String propertyId) throws NeOnCoreException;
    
    /**
     * returns a list of all direct the subpoperties of passed OWL dataproperty
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLDataProperty> getSubDataProperties(String propertyId) throws NeOnCoreException;

    /**
     * returns a list of all direct the subpoperties of passed OWL annotationproperty In OWL-DL there are no sub-annotation properties. Note: not implemented in
     * Kaon2
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLAnnotationProperty> getSubAnnotationProperties(String propertyId) throws NeOnCoreException;

    /**
     * returns a list of all direct superpoperties of passed OWL object property
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLObjectProperty> getSuperObjectProperties(String propertyId) throws NeOnCoreException;

    /**
     * returns a list of all direct superpoperties of passed OWL dataproperty
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLDataProperty> getSuperDataProperties(String propertyId) throws NeOnCoreException;

    /**
     * returns a list of all direct superpoperties of passed OWL annotationproperty note: not OWL-DL
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLAnnotationProperty> getSuperAnnotationProperties(String propertyId) throws NeOnCoreException;

    /**
     * returns a list of all equivalent objectpoperties of passed OWL object property
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLObjectProperty> getEquivalentObjectProperties(String propertyId) throws NeOnCoreException;

    /**
     * returns a list of all equivalent dataproperties of passed OWL dataproperty
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLDataProperty> getEquivalentDataProperties(String propertyId) throws NeOnCoreException;

    /**
     * returns a list of all inverse object poperties of passed OWL object property
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLObjectProperty> getInverseObjectProperties(String propertyId) throws NeOnCoreException;

    /**
     * returns a list of all domains of passed OWL property
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLClassExpression> getDomainDescriptions(String propertyId) throws NeOnCoreException;

    /**
     * returns a list of all ranges of passed OWL property
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLDataRange> getDataPropertyDataRanges(String propertyId) throws NeOnCoreException;

    /**
     * returns a list of all ranges of passed OWL object property
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLClassExpression> getObjectPropertyRangeDescriptions(String propertyId) throws NeOnCoreException;

    /**
     * returns TRUE iff the passed OWL property is functional
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public boolean isFunctional(String propertyId) throws NeOnCoreException;
    public boolean isFunctional(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException;

    /**
     * returns TRUE iff the passed OWL object property is InverseFunctional
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public boolean isInverseFunctional(String propertyId) throws NeOnCoreException;
    public boolean isInverseFunctional(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException;

    /**
     * returns TRUE iff the passed OWL object property is transititve
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public boolean isTransitive(String propertyId) throws NeOnCoreException;
    public boolean isTransitive(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException;

    /**
     * returns TRUE iff the passed OWL object property is symmetric
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public boolean isSymmetric(String propertyId) throws NeOnCoreException;
    public boolean isSymmetric(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException;

    /**
     * returns TRUE iff the passed OWL object property is asymmetric
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public boolean isAsymmetric(String propertyId) throws NeOnCoreException;
    public boolean isAsymmetric(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException;

    /**
     * returns TRUE iff the passed OWL object property is reflexive
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public boolean isReflexive(String propertyId) throws NeOnCoreException;
    public boolean isReflexive(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException;

    /**
     * returns TRUE iff the passed OWL object property is irreflexive
     * 
     * @param propertyId
     * @return
     * @throws NeOnCoreException
     */
    public boolean isIrreflexive(String propertyId) throws NeOnCoreException;
    public boolean isIrreflexive(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException;

    /**
     * Returns the set of descriptions that occur in a ClassMember axiom, where this individual is the individual.
     * 
     * @param individualId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLClassExpression> getDescriptions(String individualId) throws NeOnCoreException;

    /**
     * returns a list of classes, the passed individual belongs to
     * 
     * @param individualId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLClass> getClasses(String individualId) throws NeOnCoreException;

    /**
     * returns a list of equivalent individuals
     * 
     * @param individualId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLIndividual> getEquivalentIndividuals(String individualId) throws NeOnCoreException;

    /**
     * returns a list of (explicitly declared) different individuals
     * 
     * @param individualId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLIndividual> getDifferentIndividuals(String individualId) throws NeOnCoreException;

    /**
     * returns a list of property/value quadruples for passed individual
     * 
     * @param individualId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLDataPropertyAssertionAxiom> getDataPropertyMembers(String individualId) throws NeOnCoreException;

    /**
     * returns a list of property/value quadruples for the passe property
     * 
     * @param propertyId
     * @param individualId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLDataPropertyAssertionAxiom> getDataPropertyMembers(String individualId, String propertyId) throws NeOnCoreException;

    /**
     * returns a list of property/value quadruples for passed individual
     * 
     * @param individualId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyMembers(String individualId) throws NeOnCoreException;

    /**
     * returns a list of property/value quadruples for the passe property
     * 
     * @param propertyId
     * @param individualId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyValues(String individualId, String propertyId) throws NeOnCoreException;

    /**
     * @param newEntity
     * @throws NeOnCoreException
     */
    public void addEntity(OWLEntity newEntity) throws NeOnCoreException;

    /**
     * @param newAxiom
     * @throws NeOnCoreException
     */
    public void addAxiom(OWLAxiom newAxiom) throws NeOnCoreException;

    /**
     * @param newAxioms
     * @throws NeOnCoreException
     */
    public void addAxioms(Collection<OWLAxiom> newAxioms) throws NeOnCoreException;

    /**
     * @param oldAxiom
     * @throws NeOnCoreException
     */
    public void removeAxiom(OWLAxiom oldAxiom) throws NeOnCoreException;

    /**
     * @param oldAxioms
     * @throws NeOnCoreException
     */
    public void removeAxioms(Collection<OWLAxiom> oldAxioms) throws NeOnCoreException;

    /**
     * @param oldAxiom
     * @param newAxiom
     * @throws NeOnCoreException
     */
    public void replaceAxiom(OWLAxiom oldAxiom, OWLAxiom newAxiom) throws NeOnCoreException;

    /**
     * @param oldAxioms
     * @param newAxioms
     * @throws NeOnCoreException
     */
    public void replaceAxioms(Collection<OWLAxiom> oldAxioms, Collection<OWLAxiom> newAxioms) throws NeOnCoreException;

    /**
     * @param oldEntity
     * @param monitor can be <code>null</code>, but keep in mind that this operation can take really long.
     * @throws NeOnCoreException
     */
    public void delEntity(OWLEntity oldEntity, IProgressMonitor monitor) throws NeOnCoreException,InterruptedException,InvocationTargetException;

    /**
     * Renames an entity in the ontology.
     * 
     * <p>Note that the renaming does not include imported ontologies.</p>
     * 
     * @param oldEntity
     * @param newUri
     * @param monitor can be <code>null</code>, but keep in mind that this operation can take really long.
     * @throws NeOnCoreException
     */
    public void renameEntity(OWLEntity oldEntity, String newUri, IProgressMonitor monitor) throws NeOnCoreException,InterruptedException,InvocationTargetException;

    /**
     * @param oldOntology
     * @param newUri
     * @throws NeOnCoreException
     */
    public void renameOntology(String newUri) throws NeOnCoreException;

    /**
     * @param owlEntityId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLEntity> getEntity(String owlEntityId) throws NeOnCoreException;

    /**
     * Note: This method includes trivial reasoning.
     * 
     * @param classId
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLClass> getAllSubClasses(String classId) throws NeOnCoreException;

    /**
     * Get all axioms related to an entity.
     * 
     * @param entity The entity.
     * @param includeImportedOntologies Include axioms from imported ontologies or not.
     * @return All axioms in the ontology (closure) for which each contains <code>entity</code> at least a single time.
     * @throws NeOnCoreException
     */
    public Set<OWLAxiom> getAxioms(OWLEntity entity, boolean includeImportedOntologies) throws NeOnCoreException;

    /**
     * Get all ontologies (possibly indirectly) importing the ontology of this model.
     * 
     * @return
     * @throws NeOnCoreException
     */
    public Set<OWLModel> getAllImportingOntologies() throws NeOnCoreException;

    public Set<LocatedItem<OWLAnnotationAssertionAxiom>> getAnnotationHits(String owlEntityId) throws NeOnCoreException;
    public Set<LocatedItem<OWLAnnotationAssertionAxiom>> getAnnotationHits(OWLAnnotationSubject annotationSubject) throws NeOnCoreException;
    public Set<ItemHits<OWLClassExpression,OWLDisjointClassesAxiom>> getDisjointDescriptionHits(String classId) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLEquivalentClassesAxiom>> getEquivalentDescriptionHits(String clazzUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLClassAssertionAxiom>> getClassHits(String individualUri) throws NeOnCoreException;
    Set<ItemHits<OWLDataRange,OWLDataPropertyRangeAxiom>> getDataPropertyDataRangeHits(String propertyUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLDataPropertyDomainAxiom>> getDataPropertyDomainHits(String propertyUri) throws NeOnCoreException;
    Set<LocatedItem<OWLDataPropertyAssertionAxiom>> getDataPropertyMemberHits(String individualUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLClassAssertionAxiom>> getDescriptionHits(String individualUri) throws NeOnCoreException;
    Set<ItemHits<OWLIndividual,OWLDifferentIndividualsAxiom>> getDifferentIndividualHits(String individualUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLEquivalentDataPropertiesAxiom>> getEquivalentDataPropertyHits(String propertyUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLEquivalentClassesAxiom>> getEquivalentRestrictionHits(String superClazzUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLInverseObjectPropertiesAxiom>> getInverseObjectPropertyHits(String propertyUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLObjectPropertyRangeAxiom>> getObjectPropertyRangeDescriptionHits(String propertyUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLSubClassOfAxiom>> getSubDescriptionHits(String superClazzUri) throws NeOnCoreException;
    Set<ItemHits<OWLClass,OWLSubClassOfAxiom>> getSuperClassHits(String superClazzUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLSubObjectPropertyOfAxiom>> getSuperObjectPropertyHits(String propertyUri) throws NeOnCoreException;
    Set<ItemHits<OWLClass,OWLEquivalentClassesAxiom>> getEquivalentClassesHits(String clazzUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLObjectPropertyDomainAxiom>> getObjectPropertyDomainHits(String propertyUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLEquivalentObjectPropertiesAxiom>> getEquivalentObjectPropertyHits(String propertyUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLSubDataPropertyOfAxiom>> getSuperDataPropertyHits(String propertyUri) throws NeOnCoreException;
    boolean isRootObjectProperty(OWLObjectProperty prop) throws NeOnCoreException;
    Set<ItemHits<OWLIndividual,OWLSameIndividualAxiom>> getEquivalentIndividualHits(String individualUri) throws NeOnCoreException;
    Set<LocatedItem<OWLObjectPropertyAssertionAxiom>> getObjectPropertyMemberHits(String individualUri) throws NeOnCoreException;
    Set<ItemHits<OWLObjectProperty,OWLSubObjectPropertyOfAxiom>> getSubObjectPropertyHits(String propertyUri) throws NeOnCoreException;
    boolean isRootDataProperty(OWLDataProperty prop) throws NeOnCoreException;
    Set<ItemHits<OWLDataProperty,OWLSubDataPropertyOfAxiom>> getSubDataPropertyHits(String propertyUri) throws NeOnCoreException;
    Set<ItemHits<OWLClassExpression,OWLSubClassOfAxiom>> getSuperRestrictionHits(String superClazzUri) throws NeOnCoreException;
    
    OWLDataFactory getOWLDataFactory() throws NeOnCoreException;
    String getOntologyURI() throws NeOnCoreException;
    void applyChanges(List<OWLAxiomChange> changes) throws NeOnCoreException;
    void addAnnotation(OWLAnnotation annotation) throws NeOnCoreException;
    void removeAnnotation(OWLAnnotation annotation) throws NeOnCoreException;
    boolean containsAxiom(OWLAxiom axiom, boolean includeImportedOntologies) throws NeOnCoreException;
    Set<OWLAxiom> getReferencingAxioms(OWLEntity owlEntity) throws NeOnCoreException;
    Set<OWLAxiom> getReferencingAxioms(OWLIndividual individual) throws NeOnCoreException;
    String getPhysicalURI() throws NeOnCoreException;
    OWLAxiomChange getAddAxiom(OWLAxiom axiom) throws NeOnCoreException;
    OWLAxiomChange getRemoveAxiom(OWLAxiom axiom) throws NeOnCoreException;
    
    OWLOntology getOntology() throws NeOnCoreException;
    
    /**
     * Notifies about changes within an ontology.
     * 
     * <p>Used by the NTK framework, this method is not intended to be called by plugin programmers.</p>
     * 
     * @param updatedOntology                   Ontology to which all <code>changes</code> belong to.
     * @param changes                           The changes.
     * @param potentiallyAddedEntities          Entities which <em>may</em> have been added thru the changes.
     * @param removedEntities                   Entities which have been removed by the changes.
     * @throws NeOnCoreException
     */
    void ontologyUpdated(OWLOntology updatedOntology, List<? extends OWLAxiomChange> changes, final Set<OWLEntity> potentiallyAddedEntities, Set<OWLEntity> removedEntities) throws NeOnCoreException;

    /**
     * Notifies about a changed namespace prefix.
     * 
     * <p>Used by the NTK framework, this method is not intended to be called by plugin programmers.</p>
     * 
     * @param prefix
     * @param namespace
     */
    void namespacePrefixChanged(String prefix, String namespace) throws NeOnCoreException;
}
