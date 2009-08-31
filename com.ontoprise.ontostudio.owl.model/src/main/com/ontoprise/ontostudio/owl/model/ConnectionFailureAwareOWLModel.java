/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 27.05.2009
 * Created by: krekeler
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.exception.ProjectFailureException;
import org.neontoolkit.core.project.IOntologyProject;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
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
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import com.ontoprise.ontostudio.owl.model.event.OWLAxiomListener;

/**
 * @author krekeler
 *
 */
public class ConnectionFailureAwareOWLModel implements OWLModel {
    private OWLModel _model;
    private IOntologyProject _ontologyProject;
    
    public ConnectionFailureAwareOWLModel(OWLModel model, IOntologyProject ontologyProject) {
        if (model == null || ontologyProject == null) {
            throw new IllegalArgumentException();
        }
        _model = model;
        _ontologyProject = ontologyProject;
    }
    
    public OWLModel getOWLModel() {
        return _model;
    }

    private NeOnCoreException checkConnectionFailure(NeOnCoreException e) throws ProjectFailureException {
        return _ontologyProject.checkProjectFailure(e);
    }
    
    private RuntimeException checkConnectionFailure(RuntimeException e) throws ProjectFailureException {
        return _ontologyProject.checkProjectFailure(e);
    }

    @Override
    public void addAnnotation(OWLAnnotation annotation) throws NeOnCoreException {
        try {
            _model.addAnnotation(annotation);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void addAxiom(OWLAxiom newAxiom) throws NeOnCoreException {
        try {
            _model.addAxiom(newAxiom);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void addAxiomListener(OWLAxiomListener listener, Class<? extends OWLAxiom>[] clazzes) throws NeOnCoreException {
        try {
            _model.addAxiomListener(listener, clazzes);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void addAxioms(Collection<OWLAxiom> newAxioms) throws NeOnCoreException {
        try {
            _model.addAxioms(newAxioms);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void addEntity(OWLEntity newEntity) throws NeOnCoreException {
        try {
            _model.addEntity(newEntity);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void addToImports(OWLModel newOntology) throws NeOnCoreException {
        try {
            _model.addToImports(newOntology);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void applyChanges(List<OWLAxiomChange> changes) throws NeOnCoreException {
        try {
            _model.applyChanges(changes);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void cleanCaches() throws NeOnCoreException {
        try {
            _model.cleanCaches();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean containsAxiom(OWLAxiom axiom, boolean includeImportedOntologies) throws NeOnCoreException {
        try {
            return _model.containsAxiom(axiom, includeImportedOntologies);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void delEntity(OWLEntity oldEntity, IProgressMonitor monitor) throws NeOnCoreException, InterruptedException, InvocationTargetException {
        try {
            _model.delEntity(oldEntity, monitor);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public OWLAxiomChange getAddAxiom(OWLAxiom axiom) throws NeOnCoreException {
        try {
            return _model.getAddAxiom(axiom);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAnnotationProperty> getAllAnnotationProperties() throws NeOnCoreException {
        try {
            return _model.getAllAnnotationProperties();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAnnotationProperty> getAllAnnotationProperties(boolean includeImported) throws NeOnCoreException {
        try {
            return _model.getAllAnnotationProperties(includeImported);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClass> getAllClasses() throws NeOnCoreException {
        try {
            return _model.getAllClasses();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClass> getAllClasses(boolean includeImported) throws NeOnCoreException {
        try {
            return _model.getAllClasses(includeImported);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLDataProperty> getAllDataProperties() throws NeOnCoreException {
        try {
            return _model.getAllDataProperties();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLDataProperty> getAllDataProperties(boolean includeImported) throws NeOnCoreException {
        try {
            return _model.getAllDataProperties(includeImported);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLDatatype> getAllDatatypes() throws NeOnCoreException {
        try {
            return _model.getAllDatatypes();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLModel> getAllImportedOntologies() throws NeOnCoreException {
        try {
            return _model.getAllImportedOntologies();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLModel> getAllImportingOntologies() throws NeOnCoreException {
        try {
            return _model.getAllImportingOntologies();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLIndividual> getAllIndividuals() throws NeOnCoreException {
        try {
            return _model.getAllIndividuals();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLIndividual> getAllIndividuals(boolean includeImported) throws NeOnCoreException {
        try {
            return _model.getAllIndividuals(includeImported);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLIndividual> getAllIndividuals(String classId) throws NeOnCoreException {
        try {
            return _model.getAllIndividuals(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLObjectProperty> getAllObjectProperties() throws NeOnCoreException {
        try {
            return _model.getAllObjectProperties();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLObjectProperty> getAllObjectProperties(boolean includeImported) throws NeOnCoreException {
        try {
            return _model.getAllObjectProperties(includeImported);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAnnotationProperty> getAllOntologyAnnotationProperties() throws NeOnCoreException {
        try {
            return _model.getAllOntologyAnnotationProperties();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClass> getAllSubClasses(String classId) throws NeOnCoreException {
        try {
            return _model.getAllSubClasses(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<LocatedItem<OWLAnnotationAssertionAxiom>> getAnnotationHits(String owlEntityId) throws NeOnCoreException {
        try {
            return _model.getAnnotationHits(owlEntityId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAnnotationAssertionAxiom> getAnnotations(String owlEntityId) throws NeOnCoreException {
        try {
            return _model.getAnnotations(owlEntityId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLLiteral> getAnnotations(String owlEntityId, String annotationPropertyId) throws NeOnCoreException {
        try {
            return _model.getAnnotations(owlEntityId, annotationPropertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAxiom> getAxioms(OWLEntity entity, boolean includeImportedOntologies) throws NeOnCoreException {
        try {
            return _model.getAxioms(entity, includeImportedOntologies);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLClassAssertionAxiom>> getClassHits(String individualUri) throws NeOnCoreException {
        try {
            return _model.getClassHits(individualUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClass> getClasses(String individualId) throws NeOnCoreException {
        try {
            return _model.getClasses(individualId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClassExpression> getComplexClasses() throws NeOnCoreException {
        try {
            return _model.getComplexClasses();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLDataRange,OWLDataPropertyRangeAxiom>> getDataPropertyDataRangeHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getDataPropertyDataRangeHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLDataRange> getDataPropertyDataRanges(String propertyId) throws NeOnCoreException {
        try {
            return _model.getDataPropertyDataRanges(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLDataPropertyDomainAxiom>> getDataPropertyDomainHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getDataPropertyDomainHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<LocatedItem<OWLDataPropertyAssertionAxiom>> getDataPropertyMemberHits(String individualUri) throws NeOnCoreException {
        try {
            return _model.getDataPropertyMemberHits(individualUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLDataPropertyAssertionAxiom> getDataPropertyMembers(String individualId) throws NeOnCoreException {
        try {
            return _model.getDataPropertyMembers(individualId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLDataPropertyAssertionAxiom> getDataPropertyMembers(String individualId, String propertyId) throws NeOnCoreException {
        try {
            return _model.getDataPropertyMembers(individualId, propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public String getDefaultNamespace() throws NeOnCoreException {
        try {
            return _model.getDefaultNamespace();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLClassAssertionAxiom>> getDescriptionHits(String individualUri) throws NeOnCoreException {
        try {
            return _model.getDescriptionHits(individualUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClassExpression> getDescriptions(String individualId) throws NeOnCoreException {
        try {
            return _model.getDescriptions(individualId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLIndividual,OWLDifferentIndividualsAxiom>> getDifferentIndividualHits(String individualUri) throws NeOnCoreException {
        try {
            return _model.getDifferentIndividualHits(individualUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLIndividual> getDifferentIndividuals(String individualId) throws NeOnCoreException {
        try {
            return _model.getDifferentIndividuals(individualId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLDisjointClassesAxiom>> getDisjointDescriptionHits(String classId) throws NeOnCoreException {
        try {
            return _model.getDisjointDescriptionHits(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClassExpression> getDisjointDescriptions(String classId) throws NeOnCoreException {
        try {
            return _model.getDisjointDescriptions(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClassExpression> getDomainDescriptions(String propertyId) throws NeOnCoreException {
        try {
            return _model.getDomainDescriptions(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public <E extends OWLObject> Set<E> getEntities(Class<E> entityType, boolean includeImportedOntologies) throws NeOnCoreException {
        try {
            return _model.getEntities(entityType, includeImportedOntologies);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLEntity> getEntity(String owlEntityId) throws NeOnCoreException {
        try {
            return _model.getEntity(owlEntityId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClass> getEquivalentClasses(String classId) throws NeOnCoreException {
        try {
            return _model.getEquivalentClasses(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClass,OWLEquivalentClassesAxiom>> getEquivalentClassesHits(String clazzUri) throws NeOnCoreException {
        try {
            return _model.getEquivalentClassesHits(clazzUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLDataProperty> getEquivalentDataProperties(String propertyId) throws NeOnCoreException {
        try {
            return _model.getEquivalentDataProperties(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLEquivalentDataPropertiesAxiom>> getEquivalentDataPropertyHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getEquivalentDataPropertyHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLEquivalentClassesAxiom>> getEquivalentDescriptionHits(String clazzUri) throws NeOnCoreException {
        try {
            return _model.getEquivalentDescriptionHits(clazzUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClassExpression> getEquivalentDescriptions(String classId) throws NeOnCoreException {
        try {
            return _model.getEquivalentDescriptions(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLIndividual,OWLSameIndividualAxiom>> getEquivalentIndividualHits(String individualUri) throws NeOnCoreException {
        try {
            return _model.getEquivalentIndividualHits(individualUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLIndividual> getEquivalentIndividuals(String individualId) throws NeOnCoreException {
        try {
            return _model.getEquivalentIndividuals(individualId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLObjectProperty> getEquivalentObjectProperties(String propertyId) throws NeOnCoreException {
        try {
            return _model.getEquivalentObjectProperties(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLEquivalentObjectPropertiesAxiom>> getEquivalentObjectPropertyHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getEquivalentObjectPropertyHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLEquivalentClassesAxiom>> getEquivalentRestrictionHits(String superClazzUri) throws NeOnCoreException {
        try {
            return _model.getEquivalentRestrictionHits(superClazzUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClassExpression> getEquivalentRestrictions(String classId) throws NeOnCoreException {
        try {
            return _model.getEquivalentRestrictions(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLModel> getImportedOntologies() throws NeOnCoreException {
        try {
            return _model.getImportedOntologies();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLIndividual> getIndividuals(String classId) throws NeOnCoreException {
        try {
            return _model.getIndividuals(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLObjectProperty> getInverseObjectProperties(String propertyId) throws NeOnCoreException {
        try {
            return _model.getInverseObjectProperties(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLInverseObjectPropertiesAxiom>> getInverseObjectPropertyHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getInverseObjectPropertyHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public OWLNamespaces getNamespaces() throws NeOnCoreException {
        try {
            return _model.getNamespaces();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public OWLDataFactory getOWLDataFactory() throws NeOnCoreException {
        try {
            return _model.getOWLDataFactory();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLObjectPropertyDomainAxiom>> getObjectPropertyDomainHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getObjectPropertyDomainHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<LocatedItem<OWLObjectPropertyAssertionAxiom>> getObjectPropertyMemberHits(String individualUri) throws NeOnCoreException {
        try {
            return _model.getObjectPropertyMemberHits(individualUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyMembers(String individualId) throws NeOnCoreException {
        try {
            return _model.getObjectPropertyMembers(individualId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLObjectPropertyRangeAxiom>> getObjectPropertyRangeDescriptionHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getObjectPropertyRangeDescriptionHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClassExpression> getObjectPropertyRangeDescriptions(String propertyId) throws NeOnCoreException {
        try {
            return _model.getObjectPropertyRangeDescriptions(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyValues(String individualId, String propertyId) throws NeOnCoreException {
        try {
            return _model.getObjectPropertyValues(individualId, propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public OWLOntology getOntology() throws NeOnCoreException {
        try {
            return _model.getOntology();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAnnotation> getOntologyAnnotations() throws NeOnCoreException {
        try {
            return _model.getOntologyAnnotations();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<Object> getOntologyAnnotations(String annotationPropertyId) throws NeOnCoreException {
        try {
            return _model.getOntologyAnnotations(annotationPropertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public String getOntologyURI() throws NeOnCoreException {
        try {
            return _model.getOntologyURI();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public String getPhysicalURI() throws NeOnCoreException {
        try {
            return _model.getPhysicalURI();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public String getProjectId() throws NeOnCoreException {
        try {
            return _model.getProjectId();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAxiom> getReferencingAxioms(OWLEntity owlEntity) throws NeOnCoreException {
        try {
            return _model.getReferencingAxioms(owlEntity);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAxiom> getReferencingAxioms(OWLIndividual individual) throws NeOnCoreException {
        try {
            return _model.getReferencingAxioms(individual);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public OWLAxiomChange getRemoveAxiom(OWLAxiom axiom) throws NeOnCoreException {
        try {
            return _model.getRemoveAxiom(axiom);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAnnotationProperty> getRootAnnotationProperties() throws NeOnCoreException {
        try {
            return _model.getRootAnnotationProperties();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClass> getRootClasses() throws NeOnCoreException {
        try {
            return _model.getRootClasses();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLDataProperty> getRootDataProperties() throws NeOnCoreException {
        try {
            return _model.getRootDataProperties();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLObjectProperty> getRootObjectProperties() throws NeOnCoreException {
        try {
            return _model.getRootObjectProperties();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAnnotationProperty> getSubAnnotationProperties(String propertyId) throws NeOnCoreException {
        try {
            return _model.getSubAnnotationProperties(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClass> getSubClasses(String superClassId) throws NeOnCoreException {
        try {
            return _model.getSubClasses(superClassId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLDataProperty> getSubDataProperties(String propertyId) throws NeOnCoreException {
        try {
            return _model.getSubDataProperties(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLDataProperty,OWLSubDataPropertyOfAxiom>> getSubDataPropertyHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getSubDataPropertyHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLSubClassOfAxiom>> getSubDescriptionHits(String superClazzUri) throws NeOnCoreException {
        try {
            return _model.getSubDescriptionHits(superClazzUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLObjectProperty> getSubObjectProperties(String propertyId) throws NeOnCoreException {
        try {
            return _model.getSubObjectProperties(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLObjectProperty,OWLSubObjectPropertyOfAxiom>> getSubObjectPropertyHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getSubObjectPropertyHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLAnnotationProperty> getSuperAnnotationProperties(String propertyId) throws NeOnCoreException {
        try {
            return _model.getSuperAnnotationProperties(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClass,OWLSubClassOfAxiom>> getSuperClassHits(String superClazzUri) throws NeOnCoreException {
        try {
            return _model.getSuperClassHits(superClazzUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClass> getSuperClasses(String classId) throws NeOnCoreException {
        try {
            return _model.getSuperClasses(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClass> getSuperClassesInClassHierarchy(String subClassId) throws NeOnCoreException {
        try {
            return _model.getSuperClassesInClassHierarchy(subClassId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLDataProperty> getSuperDataProperties(String propertyId) throws NeOnCoreException {
        try {
            return _model.getSuperDataProperties(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLSubDataPropertyOfAxiom>> getSuperDataPropertyHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getSuperDataPropertyHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClassExpression> getSuperDescriptions(String classId) throws NeOnCoreException {
        try {
            return _model.getSuperDescriptions(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClassExpression> getSuperDescriptionsInClassHierarchy(OWLClassExpression subDescription) throws NeOnCoreException {
        try {
            return _model.getSuperDescriptionsInClassHierarchy(subDescription);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
   }

    @Override
    public Set<OWLObjectProperty> getSuperObjectProperties(String propertyId) throws NeOnCoreException {
        try {
            return _model.getSuperObjectProperties(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLSubObjectPropertyOfAxiom>> getSuperObjectPropertyHits(String propertyUri) throws NeOnCoreException {
        try {
            return _model.getSuperObjectPropertyHits(propertyUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<ItemHits<OWLClassExpression,OWLSubClassOfAxiom>> getSuperRestrictionHits(String superClazzUri) throws NeOnCoreException {
        try {
            return _model.getSuperRestrictionHits(superClazzUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public Set<OWLClassExpression> getSuperRestrictions(String classId) throws NeOnCoreException {
        try {
            return _model.getSuperRestrictions(classId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isClosed() throws NeOnCoreException {
        try {
            return _model.isClosed();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isFunctional(String propertyId) throws NeOnCoreException {
        try {
            return _model.isFunctional(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isFunctional(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException {
        try {
            return _model.isFunctional(propertyId, includeImportedOntologies);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isInverseFunctional(String propertyId) throws NeOnCoreException {
        try {
            return _model.isInverseFunctional(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isInverseFunctional(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException {
        try {
            return _model.isInverseFunctional(propertyId, includeImportedOntologies);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isRootClass(OWLClass clazz) throws NeOnCoreException {
        try {
            return _model.isRootClass(clazz);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isRootDataProperty(OWLDataProperty prop) throws NeOnCoreException {
        try {
            return _model.isRootDataProperty(prop);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isRootObjectProperty(OWLObjectProperty prop) throws NeOnCoreException {
        try {
            return _model.isRootObjectProperty(prop);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isSymmetric(String propertyId) throws NeOnCoreException {
        try {
            return _model.isSymmetric(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isSymmetric(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException {
        try {
            return _model.isSymmetric(propertyId, includeImportedOntologies);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isTransitive(String propertyId) throws NeOnCoreException {
        try {
            return _model.isTransitive(propertyId);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public boolean isTransitive(String propertyId, boolean includeImportedOntologies) throws NeOnCoreException {
        try {
            return _model.isTransitive(propertyId, includeImportedOntologies);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void removeAnnotation(OWLAnnotation annotation) throws NeOnCoreException {
        try {
            _model.removeAnnotation(annotation);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void removeAxiom(OWLAxiom oldAxiom) throws NeOnCoreException {
        try {
            _model.removeAxiom(oldAxiom);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void removeAxiomListener(OWLAxiomListener listener) throws NeOnCoreException {
        try {
            _model.removeAxiomListener(listener);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void removeAxioms(Collection<OWLAxiom> oldAxioms) throws NeOnCoreException {
        try {
            _model.removeAxioms(oldAxioms);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void removeFromImports(OWLModel oldOntology) throws NeOnCoreException {
        try {
            _model.removeFromImports(oldOntology);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void renameEntity(OWLEntity oldEntity, String newUri, IProgressMonitor monitor) throws NeOnCoreException, InterruptedException, InvocationTargetException {
        try {
            _model.renameEntity(oldEntity, newUri, monitor);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void renameOntology(String newUri) throws NeOnCoreException {
        try {
            _model.renameOntology(newUri);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void replaceAxiom(OWLAxiom oldAxiom, OWLAxiom newAxiom) throws NeOnCoreException {
        try {
            _model.replaceAxiom(oldAxiom, newAxiom);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void replaceAxioms(Collection<OWLAxiom> oldAxioms, Collection<OWLAxiom> newAxioms) throws NeOnCoreException {
        try {
            _model.replaceAxioms(oldAxioms, newAxioms);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void setDefaultNamespace(String namespace) throws NeOnCoreException {
        try {
            _model.setDefaultNamespace(namespace);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void setNamespacePrefix(String prefix, String namespace) throws NeOnCoreException {
        try {
            _model.setNamespacePrefix(prefix, namespace);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void ontologyUpdated(OWLOntology updatedOntology, List<? extends OWLAxiomChange> changes, Set<OWLEntity> potentiallyAddedEntities, Set<OWLEntity> removedEntities) throws NeOnCoreException {
        try {
            _model.ontologyUpdated(updatedOntology, changes, potentiallyAddedEntities, removedEntities);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public IOntologyProject getOntologyProject() throws NeOnCoreException {
        try {
            return _model.getOntologyProject();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }

    @Override
    public void namespacePrefixChanged(String prefix, String namespace) throws NeOnCoreException {
        try {
            _model.namespacePrefixChanged(prefix, namespace);
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e);
        }
    }
}
