/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.match;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.neontoolkit.search.SearchPlugin;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.individualview.IIndividualTreeElement;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualView;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/* 
 * Created on: 18.04.2005
 * Created by: Dirk Wenke
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Dirk Wenke
 */

public class AnnotationValuesSearchMatch extends OwlSearchMatch {

    private IndividualView _individualView;

    private String _value;
    private String _searchString;
    private OWLAnnotationProperty _property;

    public AnnotationValuesSearchMatch(ITreeElement element, OWLAnnotationProperty annotProperty, String annotationValue, String searchString) {
        super(element);
        _value = annotationValue;
        _searchString = searchString;
        _property = annotProperty;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#getImage()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Image getImage() {
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();
        if (element instanceof ClazzTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
        } else if (element instanceof IIndividualTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL);
        } else if (element instanceof ObjectPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
        } else if (element instanceof DataPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
        } else if (element instanceof AnnotationPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
        } else if (element instanceof DatatypeTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
        }
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.HAS_VALUE);
    }

    protected IndividualView getInstanceView() {
        if (_individualView == null) {
            try {
                _individualView = (IndividualView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IndividualView.ID);
            } catch (PartInitException e) {
                SearchPlugin.logError(org.neontoolkit.search.Messages.InstanceSearchMatch_0, e);
            }
        }
        return _individualView;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void show(int index) {
        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID);
        if(getMatch() instanceof IIndividualTreeElement) {
            if (getInstanceView() != null) {
//                _individualView.selectionChanged(
//                        NavigatorSearchMatch.getNavigator(), 
//                        new StructuredSelection(_classMatch.getMatch()));
                _individualView.getTreeViewer().setSelection(new StructuredSelection(getMatch()));
            }
        } else {
            super.show(index);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
        int L = 20;
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();

        String subject = "Obj"; //$NON-NLS-1$
        String prop = "P"; //$NON-NLS-1$

        try {
            OWLModel owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
            int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
            OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle);
            prop = OWLGUIUtilities.getEntityLabel((String[]) _property.accept(visitor));
            subject = OWLGUIUtilities.getEntityLabel((String[]) element.getEntity().accept(visitor));
        } catch (NeOnCoreException e) {
            // nothing to do
        }

        String value = ""; //$NON-NLS-1$
        int index = _value.indexOf(_searchString);
        if(index == -1) {
            String v2 =_value.toLowerCase();
            String s2 = _searchString.toLowerCase();
            index = v2.indexOf(s2);
        }
        int end = 0;
        if (index > L) {
            end = Math.min(index + L, _value.length());
            value = "..." + _value.substring(index - L, end); //$NON-NLS-1$
        } else if (index > -1){
            end = Math.min(index + 2*L, _value.length());
            value = _value.substring(0, end);
        }
        if(end != _value.length()) {
            value += "..."; //$NON-NLS-1$
        }
        System.out.println(subject + ": " + prop +" = "+ value);//$NON-NLS-1$ //$NON-NLS-2$
        return subject + ": " + prop +" = "+ value;//$NON-NLS-1$ //$NON-NLS-2$
//      + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_0 + element.getOntologyUri() + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_1 + element.getProjectName() + "]  ";  //$NON-NLS-3$    }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#setFocus()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setFocus() {
        if (getInstanceView() != null &&  (getMatch() instanceof IIndividualTreeElement)) {
            _individualView.setFocus();
        } else {
            super.setFocus();
        }
    }
}
