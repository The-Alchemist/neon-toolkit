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

import com.ontoprise.ontostudio.search.owl.ui.OwlSearchCommand.FieldTypes;


/**Simple reimplementation of com.ontoprise.indexer.owl.SearchElement to be independent of datamodel
 * @author janiko
 *
 */
public class SearchElement implements Comparable<SearchElement> {
	private String _axiom;
	private String _ontologyUri;
    private String _entityUri;
    private String _valueString;
    private FieldTypes _type;



	
    /**
	 * 
	 */
	public SearchElement(String ontologyUri, String axiom, FieldTypes type, String entityUri, String value) {
		_ontologyUri = ontologyUri;
		_axiom = axiom;
		_type = type;
		_entityUri = entityUri;
		_valueString = value;
	}

	/**
     * @return axiom that was hit 
     */
    public String getAxiom() {
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
    	int ontoComp = this._ontologyUri.compareTo(that._ontologyUri);
    	if(ontoComp != 0) {
    		return ontoComp;
    	}
     
    	if(this._entityUri != null && that._entityUri != null) {
        	int entityComp = this._entityUri.compareTo(that._entityUri);
        	if(entityComp != 0) {
        		return entityComp;
        	}
    	}
    	
    	int axiomComp = this._axiom.compareTo(that._axiom);
    	if(axiomComp != 0) {
    		return axiomComp;
    	}
    	
        return _axiom.toString().compareToIgnoreCase(that._axiom.toString());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SearchElement) {
            SearchElement se = (SearchElement) obj;
            return safeEquals(_axiom, se._axiom)
                && safeEquals(_ontologyUri, se._ontologyUri)
                && safeEquals(_entityUri, se._entityUri)
                && safeEquals(_valueString, se._valueString)
                && _type == se._type;
        }
        return false;
    }
    
    private static boolean safeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        } else {
            return a.equals(b);
        }
    }
}
