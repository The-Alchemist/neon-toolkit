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

import org.neontoolkit.gui.NeOnUIPlugin;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * @author werner
 *
 */
public class ManchesterSyntaxVisitorIdType extends ManchesterSyntaxVisitor {

    private int _idDisplayStyle;
    
    /**
     * @param owlModel
     * @param language
     * @param idDisplayStyle
     */
    public ManchesterSyntaxVisitorIdType(OWLModel owlModel, String language, int idDisplayStyle) {
       super(owlModel, language);
       _idDisplayStyle = idDisplayStyle;
    }

    @Override
    protected String[] createStandardArray(String uri) {
        switch (_idDisplayStyle) {
            case NeOnUIPlugin.DISPLAY_URI:
                return new String[] {getURI(uri), getQName(uri)};
            case NeOnUIPlugin.DISPLAY_QNAME:
                return new String[] {getQName(uri), getQName(uri)};
            case NeOnUIPlugin.DISPLAY_LOCAL:
                if (getLocalName(uri).length() == 0) {
                    return new String[] {getQName(uri), getQName(uri)};
                } else {
                    return new String[] {getLocalName(uri), getQName(uri)};
                }
            case OWLPlugin.DISPLAY_LANGUAGE:
                return new String[] {getLabel(uri), getQName(uri)};
            default:
                return new String[] {getQName(uri), getQName(uri)};
        }
    }
    
    @Override
    protected String[] createSingle(String value) {
        return new String[] {value, value};
    }
}
