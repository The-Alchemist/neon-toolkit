/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

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

public abstract class AbstractVisualizeOntologyAction implements IObjectActionDelegate {

    private ISelection _actualSelection;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        AbstractOntologyTreeElement element = (AbstractOntologyTreeElement) ((StructuredSelection) _actualSelection).getFirstElement();
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
        // add the source.
        ontoVisualizerView.visualizeElement(true, element, getOntologyLanguage());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        _actualSelection = selection;
    }
    
    protected abstract String getOntologyLanguage();

}