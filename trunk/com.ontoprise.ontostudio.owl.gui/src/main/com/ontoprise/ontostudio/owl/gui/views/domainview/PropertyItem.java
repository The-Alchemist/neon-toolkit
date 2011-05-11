/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.views.domainview;

import java.util.HashMap;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObject;

import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;

/**
 * Represents an item in the domain view, which can hold an ObjectProperty, a DataProperty 
 * or an AnnotationProperty.
 * 
 * @author Michael
 * @author Nico Stieler
 */
public class PropertyItem{

    private OWLObject _property;
    private String _ontologyUri;
    private String _projectName;
    private HashMap<String, PropertyTreeElement> _propertyTreeElements;
    private OWLClass _selectedClass;

    protected PropertyItem(PropertyTreeElement treeElement, OWLClass selectedClass, String owlClass) {
        _property = treeElement.getEntity();
        _ontologyUri = treeElement.getOntologyUri();
        _projectName = treeElement.getProjectName();
        _propertyTreeElements = new HashMap<String, PropertyTreeElement>();
        _propertyTreeElements.put(owlClass, treeElement);
        _selectedClass = selectedClass;
    }
    public OWLObject getProperty(){
        return _property;
    }
    public boolean add(PropertyTreeElement treeElement, OWLClass selectedClass, String owlClass){
        if(selectedClass != _selectedClass)
            return (Boolean) null;
        if(_propertyTreeElements.put(owlClass, treeElement) != null)
            return true;
        return false;
    }
    public boolean contains(OWLClass owlClass){
        return _propertyTreeElements.containsKey(owlClass);
    }
    /**
     * a PropertyItem is imported if one of its propertyitems is imported
     * @return
     */
    public boolean isImported() {
        for(String key : _propertyTreeElements.keySet()){
            if(_propertyTreeElements.get(key).isImported())
                return true;
        }
        return false;
    }
    /**
     * a PropertyItem is imported if one of its propertyitems is imported
     * @return
     */
    public boolean isDirect() {
        if(_selectedClass == null)
            return false;
        if(_propertyTreeElements.size() > 1){
            return false;
        }
        return getOWLClasses().contains(_selectedClass.toStringID());
    }
    /**
     * @return the _selectedClass
     */
    public OWLClass getSelectedClass() {
        return _selectedClass;
    }
    /**
     * @return the _ontologyURI
     */
    public String getOntologyUri() {
        return _ontologyUri;
    }
    /**
     * @return the _projectName
     */
    public String getProjectName() {
        return _projectName;
    }
    /**
     * @return the _owlClasses
     */
    public Set<String> getOWLClasses() {
        return _propertyTreeElements.keySet();
    }
    @Override
    public boolean equals(Object object) {
        if(super.equals(object))
            return true;
        if(object instanceof PropertyItem){
            return _property.equals(((PropertyItem) object).getProperty());
        }else if(object instanceof PropertyTreeElement){
            return _property.equals((PropertyTreeElement) object);
            
        }
        return false;
    }
    @Override
    public String toString() {
        for(String key : _propertyTreeElements.keySet()){
            return _propertyTreeElements.get(key).toString();
        }
        return _property.toString();
    }
    /**
     * 
     */
    public PropertyTreeElement getPropertyTreeElement() {
        for(String key : _propertyTreeElements.keySet())
            return _propertyTreeElements.get(key);
        return null;
    }
}
