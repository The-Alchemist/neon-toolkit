/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.ontovisualize.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.ontovisualize.OntovisualizePlugin;
import org.neontoolkit.ontovisualize.gui.OntoVisualizerView;


/*
 * Created by Werner Hihn
 */

public abstract class AbstractShowInVisualizerAction implements IObjectActionDelegate {

    private ISelection _selection;

    public void run(IAction action) {
        AbstractOntologyTreeElement element = (AbstractOntologyTreeElement) ((StructuredSelection) _selection).getFirstElement();

        IViewPart view = null;
        try {
            view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(OntoVisualizerView.ID);
        } catch (PartInitException e) {
            OntovisualizePlugin.logError("", e); //$NON-NLS-1$
            return;
        }
        if (!(view instanceof OntoVisualizerView)) {
            return;
        }
        OntoVisualizerView ontoVisualizerView = (OntoVisualizerView) view;
        ontoVisualizerView.visualizeElement(false, element, getOntologyLanguage());
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (!selection.isEmpty()) {
            _selection = selection;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }
    
    public abstract String getOntologyLanguage();

}
