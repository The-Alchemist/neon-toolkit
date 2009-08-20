/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 08.07.2009
 * Created by: janiko
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.util;

import java.util.HashSet;
import java.util.Set;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLConstants;

/**
 * @author janiko
 *
 */
public class DatatypeVerifier extends InputVerifier{

//    private OWLModel _owlModel;
    private Set<String> _dataTypes;
    
    public DatatypeVerifier(){
//        _owlModel = owlModel;
        _dataTypes = new HashSet<String>();
//        for(OWLDatatype dataType :_owlModel.getAllDatatypes()){
//            _dataTypes.add(dataType.getURI().toString());
//        }
        _dataTypes.addAll(OWLConstants.OWL_DATATYPE_URIS);
    }
    @Override
    public String verify(String... input) {
        if(input == null || input[0]== null || input[0].length() == 0 || (input[0].lastIndexOf("#")+1) == input[0].length())  //$NON-NLS-1$
            throw new IllegalArgumentException(Messages.InputVerifier_3);
        else if(!_dataTypes.contains(input[0]))
            throw new UnknownDatatypeException(Messages.InputVerifier_0 + input[0].substring(input[0].indexOf("#")+1));//$NON-NLS-1$
        else
            return input[0];
    }

}
