/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.search.owl.ui.OwlSearchCommand.FieldTypes;

/**
 * This is a simple IOwlSearchHelper, that checks the URIs of the selected 
 * element types if the search expression is part of it. 
 * @author janiko
 * @author Nico Stieler
 *
 */
public class SimpleOwlSearchHelper{

    private OWLModel _owlModel;
    private OWLObjectVisitorEx<?> _visitor;
    private boolean _includeImported;
    private boolean _caseSensitive;
    private String _searchExpression;

    public SimpleOwlSearchHelper(OWLModel owlModel){
        this._owlModel = owlModel;
    }

    public SearchResults search(String ontologyURI, Collection<FieldTypes> fields, String searchText, boolean includeAllImported, boolean caseSensitive, int start, int maxCount) throws NeOnCoreException {
        return search(ontologyURI, fields, searchText, includeAllImported, caseSensitive, NeOnUIPlugin.DISPLAY_URI, start, maxCount);
    }
    
    public SearchResults search(String ontologyURI, Collection<FieldTypes> fields, String searchText, boolean includeAllImported, boolean caseSensitive, int idDisplayStyle, int start, int maxCount) throws NeOnCoreException {
        if (fields == null || fields.size() == 0 || searchText == null || searchText.length() == 0) {
            throw new IllegalArgumentException("Search-types or search-text must not be empty"); //$NON-NLS-1$
        }
        if (maxCount <= 0) {
            throw new IllegalArgumentException("maxCount must > 0"); //$NON-NLS-1$
        }
        
        _caseSensitive = caseSensitive;
        _includeImported = includeAllImported;
        _searchExpression = searchText;

//        _visitor =  OWLPlugin.getDefault().getSyntaxManager().getVisitor(_owlModel, NeOnUIPlugin.DISPLAY_URI);
//        _visitor =  OWLPlugin.getDefault().getSyntaxManager().getVisitor(_owlModel, NeOnUIPlugin.getDefault().getIdDisplayStyle());
        _visitor =  OWLPlugin.getDefault().getSyntaxManager().getVisitor(_owlModel, idDisplayStyle);

        List<SearchElement> resultList = new LinkedList<SearchElement>();
        for(FieldTypes type : fields){
            resultList.addAll(getElements(type));
        }
        
        return  new SearchResults(start, resultList.size(), resultList);
    }


    private Collection<? extends SearchElement> getElements(FieldTypes type) throws NeOnCoreException {
        List<SearchElement> owlObjects = new LinkedList<SearchElement>();
        switch (type) {
            case ANNOTATION_PROPERTIES:
                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllAnnotationProperties(_includeImported), type));
                break;
            case ANNOTATION_VALUES:
                Set<OWLAnnotationAssertionAxiom> annotationAxioms = _owlModel.getAllAnnotationAxioms(_includeImported);
                owlObjects.addAll(compareToAnnotationSearchExpression(annotationAxioms));

                Set<OWLAnnotation> ontoAnnotations = _owlModel.getOntology().getAnnotations();
                owlObjects.addAll(compareToOntoAnnotationSearchExpression(ontoAnnotations));
                break;
            case CLASSES:
                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllClasses(_includeImported), type)); 
                break;
            case DATA_PROPERTIES:
                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllDataProperties(_includeImported), type));  
                break;
            case DATA_PROPERTY_VALUES:
                owlObjects.addAll(compareToDataValueSearchExpression(_owlModel.getAllDataPropertyAssertionAxioms(_includeImported)));
                break;
            case DATATYPES:
                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllDatatypes(), type));   
                break;
            case INDIVIDUALS: 
                owlObjects.addAll(compareToIndividualSearchExpression(_owlModel.getAllIndividuals(_includeImported)));   
                break;
            case OBJECT_PROPERTIES:
                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllObjectProperties(_includeImported), type));   
                break;
            case ONTOLOGY: 
                owlObjects.addAll(compareToOntologySearchExpression(OWLModelFactory.getOWLModels(_owlModel.getProjectId())));  
                break;
            default:
                break;
        }
        
        return owlObjects;
    }    
    
    private List<SearchElement> compareToSearchExpression(Collection<? extends OWLEntity> entities, FieldTypes type) throws NeOnCoreException{
        List<SearchElement> result = new ArrayList<SearchElement>();

        for (OWLEntity entity: entities) {
            String[] array = (String[]) entity.accept(_visitor); 
            if(containsSearchExpression(array[0])) {
                result.add(new SearchElement(_owlModel.getOntologyURI(), null, type, entity.toStringID(), null));
            }
        }
        return result;
    }
    
    private List<SearchElement> compareToIndividualSearchExpression(Collection<OWLIndividual> individuals) throws NeOnCoreException{
        List<SearchElement> result = new ArrayList<SearchElement>();

        for (OWLIndividual individual: individuals) {
            String[] array = (String[]) individual.accept(_visitor); 
            if(containsSearchExpression(array[0])) {
                result.add(new SearchElement(_owlModel.getOntologyURI(), null, FieldTypes.INDIVIDUALS, individual.toStringID(), null)); 
            }
        }
        return result;
    }

    private List<SearchElement> compareToOntologySearchExpression(Collection<OWLModel> ontologies) throws NeOnCoreException{
        List<SearchElement> result = new ArrayList<SearchElement>(ontologies.size());

        for (OWLModel ontology: ontologies) {
            String ontologyURI = ontology.getOntologyURI();
            if(containsSearchExpression(ontologyURI)) {
                result.add(new SearchElement(ontologyURI, null, FieldTypes.ONTOLOGY, ontologyURI, null)); 
            }
        }
        return result;
    }

    private List<SearchElement> compareToAnnotationSearchExpression(Collection<OWLAnnotationAssertionAxiom> axioms) throws NeOnCoreException {
        List<SearchElement> result = new ArrayList<SearchElement>();

        for (OWLAnnotationAssertionAxiom axiom: axioms) {
            OWLAnnotationValue annotationValue = axiom.getValue();
            String annotationValueString = annotationValue.toString();
            // TODO, the annotationValue can have different types, 
            //       with toString() this distinction is lost, e.g. plain literal.toString() includes the language, e.g. @de
            if(containsSearchExpression(annotationValueString)) {
                Set<OWLEntity> entities = _owlModel.getEntity(axiom.getSubject().toString());
                if(!entities.isEmpty()){

                    OWLEntity entity = _owlModel.getEntity(axiom.getSubject().toString()).iterator().next();
                    result.add(new SearchElement(_owlModel.getOntologyURI(), 
                            axiom, 
                            FieldTypes.ANNOTATION_VALUES, 
                            ((String[])entity.accept(_visitor))[0], 
                            annotationValueString));
                }
            }
        }
        return result;
    }

    private List<SearchElement> compareToOntoAnnotationSearchExpression(Collection<OWLAnnotation> annotations) throws NeOnCoreException {
        OWLDataFactory factory =_owlModel.getOntology().getOWLOntologyManager().getOWLDataFactory();
    
        List<SearchElement> result = new ArrayList<SearchElement>();
    
        for (OWLAnnotation annotationValue: annotations) {
            String annotationValueString = annotationValue.toString();
            // TODO, the annotationValue can have different types, 
            //       with toString() this distinction is lost, e.g. plain literal.toString() includes the language, e.g. @de
            if(containsSearchExpression(annotationValueString)) {
                
                OWLAnnotationAssertionAxiom axiom = factory.getOWLAnnotationAssertionAxiom(IRI.create(_owlModel.getOntologyURI()), annotationValue);
    
                result.add(new SearchElement(_owlModel.getOntologyURI(), 
                    axiom, 
                    FieldTypes.ANNOTATION_VALUES, 
                    ((String[])_owlModel.getOntology().accept(_visitor))[0], 
                    annotationValueString));
            }
        }
        return result;
    }
    
    private List<SearchElement> compareToDataValueSearchExpression(Collection<OWLDataPropertyAssertionAxiom> axioms) throws NeOnCoreException{
        List<SearchElement> result = new ArrayList<SearchElement>();

        for (OWLDataPropertyAssertionAxiom axiom: axioms) {
            String propertyValue = axiom.getObject().getLiteral();
            if((axiom.getSubject() instanceof OWLEntity) && (containsSearchExpression(propertyValue))) {
                OWLEntity entity = (OWLEntity)axiom.getSubject();
                result.add(new SearchElement(_owlModel.getOntologyURI(), 
                        axiom, 
                        FieldTypes.DATA_PROPERTY_VALUES, 
                        ((String[])entity.accept(_visitor))[0], 
                        propertyValue)); 
            }
        }
        return result;
    }
    
    private boolean containsSearchExpression(String owlObject) {
        if(!_caseSensitive){
            _searchExpression = _searchExpression.toLowerCase();
            owlObject = owlObject.toLowerCase();
        }
        if (owlObject != null && owlObject.indexOf(_searchExpression) > -1) {
            return true;
        }
        return false;
    }
    
}
