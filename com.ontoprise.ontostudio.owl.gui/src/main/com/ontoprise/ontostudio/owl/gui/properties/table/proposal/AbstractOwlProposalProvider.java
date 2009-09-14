/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.table.proposal;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

/**
 * @author mer
 * 
 */
public abstract class AbstractOwlProposalProvider implements IContentProposalProvider {

    protected OWLModel _owlModel;
    protected OWLNamespaces _namespace;
    protected OWLObjectVisitorEx _visitor;


    public AbstractOwlProposalProvider(OWLModel owlModel) {
        _owlModel = owlModel;
        try {
            _namespace = _owlModel.getNamespaces();
        } catch (NeOnCoreException e) {
            _namespace = OWLNamespaces.INSTANCE;
        }
        // always use QName for autocompletion (see http://buggy.ontoprise.de/bugs/show_bug.cgi?id=10525)
        _visitor =  OWLPlugin.getDefault().getSyntaxManager().getVisitor(_owlModel, NeOnUIPlugin.DISPLAY_QNAME);
    }

    public static boolean checkProposal(String[] array, String contents) {
        if (array[0] != null && array[0].toLowerCase().indexOf(contents.toLowerCase()) > -1) {
            // check URI
            return true;
        }
        return false;
    }

    public static String findRelevantSubString(String contents) {
        int position = contents.length();
        if (position == 0) {
            // all proposals are OK
            return ""; //$NON-NLS-1$
        }

        if (Character.isWhitespace(contents.charAt(position - 1))) {
            // all proposals are OK
            return ""; //$NON-NLS-1$
        }

        for (int i = position - 1; i >= 0; i--) {
            char c = contents.charAt(i);
            switch (c) {
                case ' ':
                    // fall through
                case '(':
                    // fall through
                case ')':
                    // fall through
                case '\n':
                    // fall through
                case '\t':
                    // fall through
                case '\r':
                    return contents.substring(i + 1, position);
                default:
                    break;
            }
        }
        return contents.substring(0, position);
    }

}
