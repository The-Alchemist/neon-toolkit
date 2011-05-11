/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.views.domainview;

import java.util.Set;

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
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
/**
 * 
 * @author Nico Stieler
 */
public class DomainViewLabelProvider extends LabelProvider implements IColorProvider, IFontProvider{

    @Override
    public Color getBackground(Object element) {
        if (element instanceof PropertyItem) {
            PropertyItem item = (PropertyItem) element;
            if (item.isImported()) {
                return OWLGUIUtilities.COLOR_FOR_IMPORTED_AXIOMS;
            }
        }
        return null;
    }

    @Override
    public Font getFont(Object element) {
        if (element instanceof PropertyItem) {
            PropertyItem item = (PropertyItem) element;
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

    @Override
    public String getText(Object element) {
        if (element instanceof PropertyItem) {
            PropertyItem item = (PropertyItem) element;
            if (!item.isDirect()) {
                String string = item.toString();
                string += " ["; //$NON-NLS-1$
                Set<String> clazzUris = item.getOWLClasses();
                if(clazzUris.size() > 0){
                    for(String clazzUri : clazzUris){
                        try {
                            string += OWLGUIUtilities.getEntityLabel(OWLUtilities.description(IRIUtils.ensureValidIRISyntax(clazzUri)), 
                                    item.getOntologyUri(), 
                                    item.getProjectName());
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
    public Image getImage(Object element) {
        if(element instanceof PropertyItem){
            PropertyItem item = (PropertyItem) element;
            if (item.getPropertyTreeElement() instanceof DataPropertyTreeElement) {
                return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
            } else if (item.getPropertyTreeElement() instanceof ObjectPropertyTreeElement) {
                return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
            } else if (item.getPropertyTreeElement() instanceof AnnotationPropertyTreeElement) {
                return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
            }
        }
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY); // error case
    }
}
