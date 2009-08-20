/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
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
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.neontoolkit.search.Messages;
import org.neontoolkit.search.SearchPlugin;
import org.neontoolkit.search.ui.NavigatorSearchMatch;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualView;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
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

public class DataPropertyValuesSearchMatch extends OwlSearchMatch {

    private static IndividualView _individualView;
    private ClassSearchMatch[] _classMatches;
    private OWLDataPropertyExpression _prop;
    private String _value;

    private String _searchString;

    public DataPropertyValuesSearchMatch(IndividualViewItem element, ClassSearchMatch[] classes, String searchString, String value, OWLDataPropertyExpression prop) {
        super(element);
        _classMatches = classes;
        _searchString = searchString;
        _value = value;
        _prop = prop;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#getImage()
     */
    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
    }

    protected static IndividualView getInstanceView() {
        if (_individualView == null) {
            try {
                _individualView = (IndividualView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IndividualView.ID);
            } catch (PartInitException e) {
                SearchPlugin.logError(Messages.InstanceSearchMatch_0, e);
            }
        }
        return _individualView;
    }

    @Override
    public void show(int index) {
        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID);
        int i = 0;
        while (index >= _classMatches[i].getOccurenceCount()) {
            index -= _classMatches[i].getOccurenceCount();
            i++;
        }
        _classMatches[i].show(index);
        if (getInstanceView() != null) {
            _individualView.selectionChanged(NavigatorSearchMatch.getNavigator(), new StructuredSelection(_classMatches[i].getMatch()));
            _individualView.getTreeViewer().setSelection(new StructuredSelection(getMatch()));
        }
    }

    @Override
    public String toString() {
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();
        String value = "\"... "; //$NON-NLS-1$
        int index = _value.indexOf(_searchString);
        if (index > 0) {
            if (index > 10) {
                value += _value.substring(index - 9, Math.min(index + 20, _value.length()));
            } else {
                value += _value.substring(0, Math.min(index + 9, _value.length()));
            }
        }
        String label = element.getId();

        OWLModel owlModel;
        try {
            owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
            int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
            OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(owlModel, idDisplayStyle);
            label = OWLGUIUtilities.getEntityLabel((String[]) _prop.accept(visitor));

        } catch (NeOnCoreException e) {
            // nothing to do
        }
        return label + " " + value + " ...\"" + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_0 + element.getOntologyUri() + "] " + com.ontoprise.ontostudio.owl.gui.Messages.DataPropertyValuesSearchMatch_1 + element.getProjectName() + "]  "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#getOccurenceCount()
     */
    @Override
    public int getOccurenceCount() {
        int count = 0;
        for (int i = 0; i < _classMatches.length; i++) {
            count += _classMatches[i].getOccurenceCount();
        }
        return count;
    }
}
