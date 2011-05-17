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
import org.neontoolkit.gui.navigator.elements.IIndividualTreeElement;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.neontoolkit.search.Messages;
import org.neontoolkit.search.SearchPlugin;
import org.neontoolkit.search.ui.NavigatorSearchMatch;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualView;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/* 
 * Created on: 22.03.2006
 * Created by: Dirk Wenke
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Dirk Wenke
 */

public class IndividualSearchMatch extends OwlSearchMatch {
    private IndividualView _individualView;
    private ClassSearchMatch _classMatch;
    
    /**
     * @param element
     */
    @SuppressWarnings("unchecked")
    public IndividualSearchMatch(IIndividualTreeElement instance, ClassSearchMatch classMatch) {
        super(instance);
        _classMatch = classMatch;
    }

    protected IndividualView getInstanceView() {
        if (_individualView == null) {
            try {
                _individualView = (IndividualView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IndividualView.ID);
            } catch (PartInitException e) {
                SearchPlugin.logError(Messages.InstanceSearchMatch_0, e);
            }
        }
        return _individualView;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#show(int)
     */
    @Override
    public void show(int index) {
        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID);
        if (getInstanceView() != null) {
            _individualView.selectionChanged(
                    NavigatorSearchMatch.getNavigator(), 
                    new StructuredSelection(_classMatch.getMatch()));
            _individualView.getTreeViewer().setSelection(new StructuredSelection(getMatch()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#getImage()
     */
    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.flogic.match.SearchMatch#setFocus()
     */
    @Override
    public void setFocus() {
        if (getInstanceView() != null) {
            _individualView.setFocus();
        }
    }

}
