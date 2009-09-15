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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.search.owl.ui.OwlSearchCommand.FieldTypes;

/**This is a simple IOwlSearchHelper, that checks the URIs of the selected 
 * element types if the search expression is part of it. 
 * @author janiko
 *
 */
public class SimpleOwlSearchHelper{

    private OWLModel _owlModel;
    private OWLObjectVisitorEx _visitor;
    private boolean _includeImported;
    private boolean _caseSensitive;
    private String _searchExpression;

    public SimpleOwlSearchHelper(OWLModel owlModel){
        this._owlModel = owlModel;
    }


    public SearchResults search(String ontologyURI, Collection<FieldTypes> fields, String searchText, boolean includeAllImported, int start, int maxCount) throws NeOnCoreException {
        return search(ontologyURI, fields, searchText, includeAllImported, false, start, maxCount);
    }


    public SearchResults search(String ontologyURI, Collection<FieldTypes> fields, String searchText, boolean includeAllImported, boolean caseSensitive, int start, int maxCount) throws NeOnCoreException {
        if (fields == null || fields.size() == 0 || searchText == null || searchText.length() == 0) {
            throw new IllegalArgumentException("Search-types or search-text must not be empty"); //$NON-NLS-1$
        }
        if (maxCount <= 0) {
            throw new IllegalArgumentException("maxCount must > 0"); //$NON-NLS-1$
        }

        
        _caseSensitive = caseSensitive;
        _includeImported = includeAllImported;
        _searchExpression = searchText;
        
        _visitor =  OWLPlugin.getDefault().getSyntaxManager().getVisitor(_owlModel, NeOnUIPlugin.DISPLAY_URI);
        List<SearchElement> resultList = new ArrayList<SearchElement>();
        
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
//                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllAnnotationProperties(_includeImported), type));//TODO visitor for values
                break;
            case CLASSES:
                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllClasses(_includeImported), type)); 
                break;
            case DATA_PROPERTIES:
                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllDataProperties(_includeImported), type));  
                break;
            case DATA_PROPERTY_VALUES:
                //TODO value visitor
                break;
            case DATATYPES:
                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllDatatypes(), type));   
                break;
            case INDIVIDUALS: 
                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllIndividuals(_includeImported), type));   
                break;
            case OBJECT_PROPERTIES:
                owlObjects.addAll(compareToSearchExpression(_owlModel.getAllObjectProperties(_includeImported), type));   
                break;
            case ONTOLOGY:
                owlObjects.addAll(compareToSearchExpression(Collections.singleton(_owlModel.getOntology()), type));  
                break;
            default:
                break;
        }

        
        
        return owlObjects;
    }
    
    
    private List<SearchElement> compareToSearchExpression(Collection<? extends OWLObject> entities, FieldTypes type) throws NeOnCoreException{
        List<SearchElement> result = new ArrayList<SearchElement>(entities.size());

        for (OWLObject entity: entities) {
            String[] array = (String[]) entity.accept(_visitor); //TODO uri visitor
            if(containsSearchExpression(array[0])) {
                result.add(new SearchElement(_owlModel.getOntologyURI(), null, type, array[0].replace("<", "").replace(">", ""), null)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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
