/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.individualview.IIndividualTreeElement;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualView;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewContentProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

public class NewIndividualHandler extends AbstractOWLIndividualViewHandler {
    private boolean _enabled = false;

    private ISelectionListener _selectionListener = new ISelectionListener() {
        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
         */
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {
            if (part instanceof MTreeView) {
                changedSelection(selection);
            }
        }
    };

    public NewIndividualHandler() {
        super();
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            ISelectionService service = window.getSelectionService();
            service.addSelectionListener(_selectionListener);
            changedSelection(service.getSelection(MTreeView.ID));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.AbstractHandler#dispose()
     */
    @Override
    public void dispose() {
        if(PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
            ISelectionService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
            service.removeSelectionListener(_selectionListener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.flogic.ui.instanceview.actions.AbstractNewHandler#runWithArgumentsSet()
     */
    @Override
    protected Object runWithArgumentsSet() {
        if (_view == null) {
            _view = (IndividualView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IndividualView.ID);
            if (_view == null) {
                return null;
            }
        }
        _view.setFocus();
        IndividualViewContentProvider contentProvider = _view.getContentProvider();
        TreeViewer treeViewer = _view.getTreeViewer();
        try {
            _view.stopEditing(true);
        } catch (CommandException e) {
            OWLPlugin.logError("", e); //$NON-NLS-1$
        } catch (NeOnCoreException e) {
            OWLPlugin.logError("", e); //$NON-NLS-1$
        }
        final IIndividualTreeElement<OWLNamedIndividual> item = contentProvider.createNewEditableItem();
        treeViewer.refresh();
        _view.prepareItemForEdit(item);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return _enabled;
    }

    public void changedSelection(ISelection selection) {
        IStructuredSelection sel = (IStructuredSelection)selection;
        boolean oldEnabled = _enabled;
       _enabled = sel.size() == 1 && sel.getFirstElement() instanceof ClazzTreeElement;
        if (oldEnabled != _enabled) {
            fireHandlerChanged(new HandlerEvent(this, true, false));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.gui.navigator.actions.AbstractNewHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID, OWLPlugin.getDefault(), NeOnUIPlugin.ASK_FOR_PRESPECTIVE_SWITCH);
        return super.execute(arg0);
    }
}
