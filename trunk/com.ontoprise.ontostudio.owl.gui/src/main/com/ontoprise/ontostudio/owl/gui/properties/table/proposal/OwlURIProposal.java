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

import java.util.Set;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OntoStudioOWLConstants;

/**
 * @author mer
 * 
 */
public abstract class OwlURIProposal implements IContentProposal {

    private String[] _array;
    private int _offset;
    private OWLModel _owlModel;
    private OWLEntity _entity;

    /**
     * @param array
     * @param sourceOwlModel 
     * @param ontology
     * @param project
     * @param cursorPos
     */
    public OwlURIProposal(OWLEntity entity, String[] array, int offset, OWLModel owlModel) {
        _array = array;
        _offset = offset;
        _owlModel = owlModel;
        _entity = entity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.fieldassist.IContentProposal#getContent()
     */
    public String getContent() {
        int mode = OWLGUIUtilities.getEntityLabelMode();
        if (mode == NeOnUIPlugin.DISPLAY_LOCAL || mode == OWLPlugin.DISPLAY_LANGUAGE) {
            // local name and labels are not sufficient for display, use QName or URI instead
            if (_array.length == 2) {
                return _array[1]; // ManchesterSyntaxVisitorIdType is used, so _array[1] is always QName
            }
            if (_array.length > 2) {
                return _array[2];
            }
            return _array[0];
        }
        return getLabel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.fieldassist.IContentProposal#getLabel()
     */
    public String getLabel() {
        return OWLGUIUtilities.getEntityLabel(_array);
    }

    /**
     * 
     * @see org.eclipse.jface.fieldassist.IContentProposal#getCursorPosition()
     */
    public int getCursorPosition() {
        return getContent().length() + _offset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.fieldassist.IContentProposal#getDescription()
     */
    public String getDescription() {
        // uri local qname label
        StringBuffer sb = new StringBuffer();
        if (_array.length == 2) {
            OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(_owlModel);//NICO are you sure
            String[] completeIDArray = (String[]) _entity.accept(visitor);
            if (completeIDArray.length == 4) {
                sb.append(Messages.OwlURIProposal_0  + "\t\t\t" + completeIDArray[0] + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
                sb.append(Messages.OwlURIProposal_1 + "\t\t\t" + completeIDArray[1] + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
                sb.append(Messages.OwlURIProposal_2 + "\t\t" + completeIDArray[2] + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
                if (!completeIDArray[3].equals(completeIDArray[2])) {
                    String lang = NeOnUIPlugin.getDefault().getPreferenceStore().getString(OWLPlugin.SPECIFIC_LANGUAGE_PREFERENCE);  
                    sb.append(Messages.OwlURIProposal_3 + "'" + lang + "'" + ":\t" + completeIDArray[3]); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            } else {
                for (int i = 0; i < completeIDArray.length; i++) {
                    sb.append(completeIDArray[i] + "\n"); //$NON-NLS-1$
                }
            }
        } else {
            for (int i = 0; i < _array.length; i++) {
                sb.append(_array[i] + "\n"); //$NON-NLS-1$
            }
        }
        try {
            Set<OWLAnnotationValue> comments = _owlModel.getAnnotations(_entity.getIRI().toString(),OntoStudioOWLConstants.RDFS_COMMENT);//NICO are you sure
            if(comments.size()>0) {
                sb.append("--------------------------------------------------\n"); //$NON-NLS-1$
            }
            for (OWLAnnotationValue comment: comments) {
                if(comment instanceof OWLLiteral) {
                    sb.append(((OWLLiteral)comment).getLiteral()).append("\n"); //$NON-NLS-1$
                } 
            }
        } catch (NeOnCoreException e) {
            // ignore
        }
        
        return sb.toString();
    }

}