/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.views.propertymember;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;

/**
 * This class represents the label provider for the cells of the Property Member table.
 * It provides the text-labels, the icons, and also colors the background iff the axiom was imported.
 * 
 * @author Michael Erdmann
 * @author Nico Stieler
 */
public class PropertyMemberViewLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider, IFontProvider{
       
    @Override
    public Color getBackground(Object element) {
        if (element instanceof PropertyTreeElement) {
            PropertyTreeElement item = (PropertyTreeElement) element;
            if (item.isImported()) {
                return OWLGUIUtilities.COLOR_FOR_IMPORTED_AXIOMS;
            }
        }
        return null;
    }
    @Override
    public Font getFont(Object element) {
        if (element instanceof PropertyMember) {
            PropertyMember item = (PropertyMember) element;
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
    public String getColumnText(Object element, int column) {
        if (!(element instanceof PropertyMember)) {
            return null;
        }
        PropertyMember row = (PropertyMember)element;
        Object o;
        switch(column){
            case 0:
                o = row.getSubject();
                break;
            case 1:
                o = row.getProperty();
                break;
            case 2:
                o = row.getValue();
                break;
            default:
                o = row.getValue();
                break;
        }
        if(o instanceof OWLEntity) {
            String[] idArray;
            try {
                idArray = OWLGUIUtilities.getIdArray((OWLEntity)o, row.getOntology(), row.getProject());
            } catch (NeOnCoreException e) {
                idArray = new String[] {((OWLEntity)o).getIRI().toString()};
            }
            return OWLGUIUtilities.getEntityLabel(idArray);
        }
        return o.toString();
    }
    @Override
    public Image getColumnImage(Object element, int column) {
        if (!(element instanceof PropertyMember)) {
            return null;
        }
        
        PropertyMember row = (PropertyMember)element;
        Object o;
        switch(column){
            case 0:
                o = row.getSubject();
                break;
            case 1:
                o = row.getProperty();
                break;
            case 2:
                o = row.getValue();
                break;
            default:
                o = row.getValue();
                break;
        }
        if(o instanceof OWLOntology) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY); 
        } else if (o instanceof OWLClass) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
        } else if (o instanceof OWLIndividual) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL);
        } else if (o instanceof OWLDataProperty) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
        } else if (o instanceof OWLObjectProperty) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
        } else if (o instanceof OWLDataRange) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
        } else if (o instanceof OWLAnnotationProperty) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ALL_VALUES_FROM);
        } else { 
            return null; // error case
        }
    }
}