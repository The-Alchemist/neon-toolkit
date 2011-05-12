/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
/**
 * 
 * @author Nico Stieler
 */
public class IndividualViewLabelProvider extends LabelProvider implements IColorProvider, IFontProvider{

    @Override
    @SuppressWarnings("rawtypes")
    public Color getBackground(Object element) {
        if (element instanceof IIndividualTreeElement) {
            IIndividualTreeElement item = (IIndividualTreeElement) element;
            if (item.isImported()) {
                return OWLGUIUtilities.COLOR_FOR_IMPORTED_AXIOMS;
            } else {
                return null;
            }
        }
        return null;
    }
    @SuppressWarnings("rawtypes")
    @Override
    public Font getFont(Object element) {
        if (element instanceof IIndividualTreeElement) {
            IIndividualTreeElement item = (IIndividualTreeElement) element;
            if (!item.isDirect()) {
                return OWLGUIUtilities.FONT_FOR_INHERITED_AXIOMS;
            }
        }
        return null;
    }
    @Override
    public Color getForeground(Object element) {
        return null;
    }
    @SuppressWarnings("rawtypes")
    @Override
    public String getText(Object element) {
        if (element instanceof IIndividualTreeElement) {
            IIndividualTreeElement item = (IIndividualTreeElement) element;
            if (!item.isDirect()) {
                String string = item.toString();
                string += " ["; //$NON-NLS-1$
                String[] clazzUris = item.getClazzUris();
                if(clazzUris.length > 0){
                    for(String clazzUri : clazzUris){
                        try {
                            string += OWLGUIUtilities.getEntityLabel(OWLUtilities.description(IRIUtils.ensureValidIRISyntax(clazzUri)), item.getOntologyUri(), item.getProjectName());
                        } catch (NeOnCoreException e) {
                            string += clazzUri;
                        }
                        string += ", "; //$NON-NLS-1$
                    }
                    string = string.substring(0, string.length() - 2);
                }
                string += "]"; //$NON-NLS-1$
                return string;
            }
        }
        return element.toString();
    }
    @Override
    public Image getImage(Object obj) {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL);
    }
}