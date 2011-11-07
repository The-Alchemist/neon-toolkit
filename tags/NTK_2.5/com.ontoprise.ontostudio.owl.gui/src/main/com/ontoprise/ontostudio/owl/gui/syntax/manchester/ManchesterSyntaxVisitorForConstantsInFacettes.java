/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.syntax.manchester;

import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;

import com.ontoprise.ontostudio.owl.model.visitors.OWLKAON2VisitorAdapter;

/**
 * KAON2 visitor which returns for constants that are used in Facettes the values with the types
 * abbreviated without the namespace.
 *      
 * @author Michael
 * @author Nico Stieler
 */
public class ManchesterSyntaxVisitorForConstantsInFacettes extends OWLKAON2VisitorAdapter {


    public ManchesterSyntaxVisitorForConstantsInFacettes() {
    }
    
    @Override
    public String visit(OWLLiteral typedConstant) {
        // TODO: migration, skipped special handling for numbers
//        if (value instanceof Long) {
//            return value.toString();
//
//        } else if (value instanceof Double) {
//            return value.toString();


        OWLDatatype datatype = typedConstant.getDatatype();//TODO OWL API 3.1.0 : there are more datatypes now
        String xsdTypeURI = datatype.getIRI().toString();
        if(xsdTypeURI.equals("http://www.w3.org/2001/XMLSchema#unsignedInt")) { //$NON-NLS-1$
            xsdTypeURI="integer"; //$NON-NLS-1$
        } else if(typedConstant.isInteger()) {
            xsdTypeURI="integer"; //$NON-NLS-1$
        } else if(typedConstant.isFloat()) {
            xsdTypeURI="float"; //$NON-NLS-1$
        } else if(xsdTypeURI.equals("http://www.w3.org/2001/XMLSchema#string")) { //$NON-NLS-1$
            xsdTypeURI="string"; //$NON-NLS-1$
        } else if(typedConstant.isRDFPlainLiteral()) {
            String string = typedConstant.getLiteral();
            String language = typedConstant.getLang();
            if (language != null) {
                return ManchesterSyntaxVisitor.quoteLiteral(string).concat("@").concat(language).toString(); //$NON-NLS-1$
            } else {
                return ManchesterSyntaxVisitor.quoteLiteral(string);
            }
        }
        return new StringBuilder(ManchesterSyntaxVisitor.quoteLiteral(typedConstant.getLiteral())).append("^^").append(xsdTypeURI).toString(); //$NON-NLS-1$
    }
}
