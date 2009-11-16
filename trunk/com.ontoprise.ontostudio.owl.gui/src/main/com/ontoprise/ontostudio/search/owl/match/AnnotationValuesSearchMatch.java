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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.properties.EntityPropertiesView;
import org.neontoolkit.search.SearchPlugin;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;

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

    private static EntityPropertiesView _entityPropertiesView;

    private String _annotationValue;
    private String _searchString;
    private OWLAnnotationProperty _annotProperty;

    public AnnotationValuesSearchMatch(IProjectElement element, OWLAnnotationProperty annotProperty, String annotationValue, String searchString) {
        super(element);
        _annotationValue = annotationValue;
        _searchString = searchString;
        _annotProperty = annotProperty;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#getImage()
     */
    @Override
    public Image getImage() {
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();
        if (element instanceof ClazzTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
        } else if (element instanceof ObjectPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
        } else if (element instanceof DataPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
        } else if (element instanceof AnnotationPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
        } else if (element instanceof DatatypeTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
        }
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
    }

    protected static EntityPropertiesView getEntityPropertiesView() {
        if (_entityPropertiesView == null) {
            try {
                _entityPropertiesView = (EntityPropertiesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(EntityPropertiesView.ID);
            } catch (PartInitException e) {
                SearchPlugin.logError(Messages.AnnotationValuesSearchMatch_0, e); 
            }
        }
        return _entityPropertiesView;
    }

    @Override
    public void show(int index) {
        super.show(index);
        if (getEntityPropertiesView() != null) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    // FIXME make this generic
//                    _entityPropertiesView.selectTab(2);
                }

            });
        }

    }

    @Override
    public String toString() {
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();
        String projectName = element.getProjectName();
        String ontologyUri = element.getOntologyUri();

        String value = "\"... "; //$NON-NLS-1$
        int index = _annotationValue.indexOf(_searchString);
        if (index > -1) {
            if (index > 20) {
                value += _annotationValue.substring(index - 19, Math.min(index + 20, _annotationValue.length()));
            } else {
                value += _annotationValue.substring(0, Math.min(index + 20, _annotationValue.length()));
            }
        }
        String elementId = ""; //$NON-NLS-1$
        String annotationProperty = ""; //$NON-NLS-1$
        try {
            elementId = OWLGUIUtilities.getEntityLabel(element.getEntity(), ontologyUri, projectName);
            annotationProperty = OWLGUIUtilities.getEntityLabel(_annotProperty, ontologyUri, projectName);

        } catch (NeOnCoreException e) {
        }
        return elementId + " - " + annotationProperty + " - " + value + " ...\"" + Messages.AnnotationValuesSearchMatch_1 + ontologyUri + "] " + Messages.AnnotationValuesSearchMatch_2 + projectName + "]  "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 
    }
}
