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

import org.semanticweb.owlapi.model.OWLAxiom;

import com.ontoprise.ontostudio.search.owl.ui.OwlSearchCommand.FieldTypes;


/**
 * Simple reimplementation of com.ontoprise.indexer.owl.SearchElement to be independent of datamodel
 * @author janiko
 *
 */
public class SearchElement implements Comparable<SearchElement> {
	private OWLAxiom _axiom;
	private String _ontologyUri;
    private String _entityUri;
    private String _valueString;
    private FieldTypes _type;



	
    /**
	 * 
	 */
	public SearchElement(String ontologyUri, OWLAxiom axiom, FieldTypes type, String entityUri, String value) {
		_ontologyUri = ontologyUri;
		_axiom = axiom;
		_type = type;
		_entityUri = entityUri;
		_valueString = value;
	}

	/**
     * @return axiom that was hit 
     */
    public OWLAxiom getAxiom() {
        return _axiom;
    }
    
    /**
     * @return thing that was hit (if it was an entity)
     */
    public String getEntityUri() {
        return _entityUri;
    }
    
    /**
     * @return thing that was hit (if it was a (data or annotation) property value
     */
    public String getValueString() {
        return _valueString;
    }
    
    /**
     * @return term that was hit
     */
    public String getOntologyUri() {
        return _ontologyUri;
    }
    
    /**
     * @return the type of hit
     */
    public FieldTypes getType() {
        return _type;
    }
    
    
    public int compareTo(SearchElement that) {
        
        if(this._type != null && that._type != null) {
            int typeComp = this._type.compareTo(that._type);
            if(typeComp != 0) {
                return typeComp;
            }
        }
        
        if(this._entityUri != null && that._entityUri != null) {
            int entityComp = this._entityUri.compareTo(that._entityUri);
            if(entityComp != 0) {
                return entityComp;
            }
        }
        
        if(this._valueString != null && that._valueString != null) {
            int valueComp = this._valueString.compareTo(that._valueString);
            if(valueComp != 0) {
                return valueComp;
            }
        }
        
        if(this._axiom != null && that._axiom != null) {
            int axiomComp = this._axiom.compareTo(that._axiom);
            if(axiomComp != 0) {
                return axiomComp;
            }
        }

        int ontoComp = this._ontologyUri.compareTo(that._ontologyUri);
        if(ontoComp != 0) {
            return ontoComp;
        }
        
        return 0;
    }
    
    @Override
    public boolean equals(Object thatObject) {
        if (thatObject instanceof SearchElement) {
            SearchElement that = (SearchElement) thatObject;
            return safeEquals(_axiom, that._axiom)
                && safeEquals(_ontologyUri, that._ontologyUri)
                && safeEquals(_entityUri, that._entityUri)
                && safeEquals(_valueString, that._valueString)
                && _type == that._type;
        }
        return false;
    }
    
    private static boolean safeEquals(Object a, Object b) {
        if (a == null || b == null) {
            return a == b;
        } else {
            return a.equals(b);
        }
    }
    
    @Override
    public int hashCode() {
        return (_type+_entityUri+_valueString+_axiom+_ontologyUri).hashCode();
    }
}
