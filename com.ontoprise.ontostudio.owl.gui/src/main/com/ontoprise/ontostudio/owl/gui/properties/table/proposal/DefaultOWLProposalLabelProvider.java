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

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;

public class DefaultOWLProposalLabelProvider extends LabelProvider {

    @Override
    public Image getImage(Object element) {
        if (element instanceof ObjectPropertyProposal) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
        } else if (element instanceof DataPropertyProposal) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
        } else if (element instanceof ClazzProposal) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
        } else if (element instanceof IndividualProposal) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL);
        } else if (element instanceof DatatypeProposal) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
        } else if (element instanceof AnnotationPropertyProposal) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
        }
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.EMPTY);
    }

    @Override
    public String getText(Object element) {
        IContentProposal proposal = (IContentProposal) element;
        return proposal.getLabel();
    }

}
