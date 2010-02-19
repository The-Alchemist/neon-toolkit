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
import org.semanticweb.owlapi.model.OWLAxiom;
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

public class AxiomSearchMatch extends OwlSearchMatch {

    private IndividualView _individualView;

    private OWLAxiom _axiom;

    public AxiomSearchMatch(ITreeElement element, OWLAxiom axiom) {
        super(element);
        _axiom = axiom;
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
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();

        String subject = ""; //$NON-NLS-1$
        String axiomString = ""; //$NON-NLS-1$
        String projectName = ""; //$NON-NLS-1$
        String ontology = ""; //$NON-NLS-1$

        try {
            if(element != null) {
                OWLModel owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
                int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
                OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle);
                subject = OWLGUIUtilities.getEntityLabel((String[]) element.getEntity().accept(visitor));
                axiomString = OWLGUIUtilities.getEntityLabel((String[]) _axiom.accept(visitor));
                projectName = element.getProjectName();
                ontology = element.getOntologyUri();
                return subject + ": " + axiomString + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_0 + ontology + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_1 + projectName + "]  "; //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (NeOnCoreException e) {
            // nothing to do
        }
        return _axiom.toString();
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
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AxiomSearchMatch)) {
            return false;
        }
        AxiomSearchMatch that = (AxiomSearchMatch) object;
        
        return this._axiom.equals(that._axiom);
    }

    @Override
    public int hashCode() {
        return _axiom.hashCode();
    }

}
