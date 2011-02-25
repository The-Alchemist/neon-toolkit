/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.references;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.search.owl.ui.OwlSearchCommand.FieldTypes;
import com.ontoprise.ontostudio.search.owl.ui.SearchElement;
import com.ontoprise.ontostudio.search.owl.ui.SearchResults;
 
/**
  * @author Nico Stieler
  */
public class FindReferencesHelper {

    public static SearchResults search(OWLEntity entity, String project) throws NeOnCoreException {
        Set<OWLModel> owlModels = OWLModelFactory.getOWLModels(project);
        List<SearchElement> resultList = new LinkedList<SearchElement>();

        for (OWLModel owlModel: owlModels) {
            resultList.addAll(getElements(owlModel, entity));
        }

        return new SearchResults(0, resultList.size(), resultList);
    }

    private static List<SearchElement> getElements(OWLModel owlModel, OWLEntity entity) throws NeOnCoreException {
        Set<OWLAxiom> axioms = owlModel.getReferencingAxioms(entity);
        List<SearchElement> result = new ArrayList<SearchElement>();
 
        for (OWLAxiom axiom: axioms) {
            result.add(new SearchElement(owlModel.getOntologyURI(), axiom, findType(axiom, owlModel), OWLUtilities.toString(entity,  owlModel.getOntology()), null));
        }

        return result;
    }
    
    /**
     * @param entity
     * @return
     */
    public static FieldTypes findType(OWLAxiom axiom, OWLModel owlModel) {
        AxiomType axiomType = axiom.getAxiomType();
        
        if((axiomType == AxiomType.EQUIVALENT_CLASSES) ||
                (axiomType == AxiomType.SUBCLASS_OF) ||
                (axiomType == AxiomType.DISJOINT_CLASSES) ||
                (axiomType == AxiomType.DISJOINT_UNION))
                    return FieldTypes.CLASSES;

        if((axiomType == AxiomType.CLASS_ASSERTION) ||
                (axiomType == AxiomType.SAME_INDIVIDUAL) ||
                (axiomType == AxiomType.DIFFERENT_INDIVIDUALS) ||
                (axiomType == AxiomType.DATA_PROPERTY_ASSERTION) ||
                (axiomType == AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION) ||
                (axiomType == AxiomType.OBJECT_PROPERTY_ASSERTION) ||
                (axiomType == AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION))
                    return FieldTypes.INDIVIDUALS;

        if((axiomType == AxiomType.EQUIVALENT_OBJECT_PROPERTIES) ||
                (axiomType == AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY) ||
                (axiomType == AxiomType.INVERSE_OBJECT_PROPERTIES) ||
                (axiomType == AxiomType.IRREFLEXIVE_OBJECT_PROPERTY) ||
                (axiomType == AxiomType.OBJECT_PROPERTY_DOMAIN) ||
                (axiomType == AxiomType.OBJECT_PROPERTY_RANGE) ||
                (axiomType == AxiomType.REFLEXIVE_OBJECT_PROPERTY) ||
                (axiomType == AxiomType.ASYMMETRIC_OBJECT_PROPERTY) ||
                (axiomType == AxiomType.DISJOINT_OBJECT_PROPERTIES) ||
                (axiomType == AxiomType.FUNCTIONAL_OBJECT_PROPERTY) ||
                (axiomType == AxiomType.SUB_PROPERTY_CHAIN_OF) ||
                (axiomType == AxiomType.SUB_OBJECT_PROPERTY) ||
                (axiomType == AxiomType.SYMMETRIC_OBJECT_PROPERTY) ||
                (axiomType == AxiomType.TRANSITIVE_OBJECT_PROPERTY) ||
                (axiomType == AxiomType.HAS_KEY))
                    return FieldTypes.OBJECT_PROPERTIES;

        if((axiomType == AxiomType.DATA_PROPERTY_DOMAIN) ||
                (axiomType == AxiomType.DATA_PROPERTY_RANGE) ||
                (axiomType == AxiomType.SUB_DATA_PROPERTY) ||
                (axiomType == AxiomType.DISJOINT_DATA_PROPERTIES) ||
                (axiomType == AxiomType.EQUIVALENT_DATA_PROPERTIES) ||
                (axiomType == AxiomType.FUNCTIONAL_DATA_PROPERTY))
                    return FieldTypes.DATA_PROPERTIES;
                
        if((axiomType == AxiomType.ANNOTATION_PROPERTY_DOMAIN) ||
                (axiomType == AxiomType.ANNOTATION_PROPERTY_RANGE) ||
                (axiomType == AxiomType.SUB_ANNOTATION_PROPERTY_OF))
                    return FieldTypes.ANNOTATION_PROPERTIES;
                
        if(axiomType == AxiomType.DATATYPE_DEFINITION)
                    return FieldTypes.DATATYPES;

        if(axiomType == AxiomType.DECLARATION) {
            OWLEntity entity = ((OWLDeclarationAxiom)axiom).getEntity();
            return findType(entity);
        }
        
        if(axiomType == AxiomType.ANNOTATION_ASSERTION) {
            OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom)axiom).getSubject();
            Set<OWLEntity> entities;
            try {
                entities = owlModel.getEntity(subject.toString());
                for (OWLEntity entity: entities) {
                    return findType(entity);
                }
            } catch (NeOnCoreException e) {
                // ignore
            }
        }
            
        // default  
        return FieldTypes.ONTOLOGY;
    }


    /**
     * @param axiom - the source axiom
     * @param ontology - the source ontology, where the subject can be found
     * @param project - the source project, where the subject can be found 
     * @return
     * @throws NeOnCoreException 
     * 
     * finds the subject of the axiom, also in case it is just an IRI
     * 
     */
    public static OWLEntity findSubject(OWLAxiom axiom, String ontology, String project) throws NeOnCoreException {
  
        SubjectExtractionVisitor subjectVisitor = new SubjectExtractionVisitor();
        axiom.accept(subjectVisitor);
        
        if(subjectVisitor.getSubject() instanceof OWLEntity) {
            return (OWLEntity)subjectVisitor.getSubject();
        }else if(subjectVisitor.getSubject() instanceof IRI){
            for(OWLEntity entity : OWLModelFactory.getOWLModel(ontology, project).getEntity(subjectVisitor.getSubject().toString())){
                return entity;
            }
        }
        return null;
    }
    /**
     * @param axiom - the source axiom, to find it's subject
     * @return
     * 
     * finds the subject of the axiom
     * 
     */
    public static OWLEntity findSubject(OWLAxiom axiom) {
  
        SubjectExtractionVisitor subjectVisitor = new SubjectExtractionVisitor();
        axiom.accept(subjectVisitor);
        
        if(subjectVisitor.getSubject() instanceof OWLEntity)
            return (OWLEntity)subjectVisitor.getSubject();
        return null;
    }

    
    public static FieldTypes findType(OWLEntity entity) {
        if(entity instanceof OWLClass) {
            return FieldTypes.CLASSES;
            
        } else if(entity instanceof OWLDataProperty) {
            return FieldTypes.DATA_PROPERTIES;

        } else if(entity instanceof OWLObjectProperty) {
            return FieldTypes.OBJECT_PROPERTIES;
        
        } else if(entity instanceof OWLAnnotationProperty) {
            return FieldTypes.ANNOTATION_PROPERTIES;
        
        } else if(entity instanceof OWLIndividual) {
            return FieldTypes.INDIVIDUALS;
        
        } else if(entity instanceof OWLDatatype) {
            return FieldTypes.DATATYPES;
        
        } else if(entity instanceof OWLOntology) {
            return FieldTypes.ONTOLOGY;
        } 
        
        //default
        return FieldTypes.ONTOLOGY;
    }
}
