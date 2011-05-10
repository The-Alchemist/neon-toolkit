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
package com.ontoprise.ontostudio.owl.gui.views.propertymember;

import org.semanticweb.owlapi.model.OWLObject;

/**
 * Represents an item in the property-member view, which can hold a subject, property and object triple. 
 * * Subject can be any OWL object.
 * * Property can be any OWL property.
 * * Object, is either an individual or a literal.
 * Additionally this class holds the ontology and project of the propertyMemberAxiom and iff this 
 * axiom was directly contained in the ontology or imported.
 * 
 * @author Michael Erdmann
 * @author Nico Stieler
 */
public class PropertyMember {

    private OWLObject _subject;
    private OWLObject _property;
    private OWLObject _selectedProperty;
    private OWLObject _value;
    private boolean _isImported;
    private String _ontology;
    private String _project;

    protected PropertyMember(OWLObject subject, OWLObject value, boolean isImported, String ontology, String project) {
        this(subject, null, null, value, isImported, ontology, project);
    }
    protected PropertyMember(OWLObject subject, OWLObject property,  OWLObject selectedProperty, OWLObject value, boolean isImported, String ontology, String project) {
        _subject = subject;
        _property = property;
        _selectedProperty = selectedProperty;
        _value = value;
        _isImported = isImported;
        _ontology = ontology;
        _project = project;
    }
    public OWLObject getSubject(){
        return _subject;
    }
    public OWLObject getProperty(){
        return _property;
    }
    public OWLObject getValue(){
        return _value;
    }
    public boolean isImported() {
        return _isImported;
    }
    public String getOntology() {
        return _ontology;
    }
    public String getProject() {
        return _project;
    }
    public boolean isDirect(){
        if(_property == null || _selectedProperty == null)
            return false;
        return _property.equals(_selectedProperty);
    }
}
