/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util;

import java.util.HashSet;
import java.util.Set;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDatatype;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * @author janiko
 * @author Nico Stieler
 *
 */
public class DatatypeVerifier extends InputVerifier{

    private OWLModel _owlModel;
    private Set<String> _dataTypes;
    private Set<String> _owlDataTypes;
    
    public DatatypeVerifier(OWLModel owlModel){
        _owlModel = owlModel;
        _dataTypes = new HashSet<String>();
        _owlDataTypes = new HashSet<String>();
        _owlDataTypes.addAll(OWLConstants.OWL_DATATYPE_URIS);
    }
    @Override
    public String verify(String... input) {
        _dataTypes = new HashSet<String>();
        try {
            for(OWLDatatype dataType :_owlModel.getAllDatatypes()){
                _dataTypes.add(dataType.getIRI().toString());
            }
        } catch (NeOnCoreException e) {
            e.printStackTrace();
        }
        if(input == null || input[0]== null || input[0].length() == 0 || (input[0].lastIndexOf("#")+1) == input[0].length())  //$NON-NLS-1$
            throw new IllegalArgumentException(Messages.InputVerifier_3);
//        else if(!_dataTypes.contains(IRIUtils.ensureValidIdentifierSyntax(input[0])))
        else if(!_dataTypes.contains(input[0]) && !_owlDataTypes.contains(input[0]))            
                throw new UnknownDatatypeException(Messages.InputVerifier_0 + input[0].substring(input[0].indexOf("#")+1));//$NON-NLS-1$
        else
            return input[0];
        }
}
