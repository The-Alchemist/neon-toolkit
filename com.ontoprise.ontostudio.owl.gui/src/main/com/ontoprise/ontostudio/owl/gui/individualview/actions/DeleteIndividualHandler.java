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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.elements.IIndividualTreeElement;
import org.neontoolkit.refactor.GenericRefactoringExecutionStarter;

import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewContentProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.refactor.delete.individual.DeleteIndividualRefactoringFactory;

public class DeleteIndividualHandler extends AbstractOWLIndividualViewHandler {
    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.flogic.ui.instanceview.actions.AbstractInstanceViewHandler#runWithArgumentsSet()
     */
    @Override
    protected Object runWithArgumentsSet() {
        IndividualViewContentProvider contentProvider = _view.getContentProvider();
        TreeViewer treeViewer = _view.getTreeViewer();
        try {
            _view.stopEditing(true);
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        ClazzTreeElement clazz = _view.getSelectedClazz();
        IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
        List<?> list = selection.toList();
        IIndividualTreeElement[] individuals = (IIndividualTreeElement[]) list.toArray(new IIndividualTreeElement[0]);
        try {
            GenericRefactoringExecutionStarter.startRefactoring(_view.getSite().getShell(), DeleteIndividualRefactoringFactory.DELETE_INDIVIDUAL_REFACTORING_ID, individuals, clazz);
        } catch (CoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        contentProvider.forceUpdate();
        treeViewer.refresh();
        return null;
    }
}
