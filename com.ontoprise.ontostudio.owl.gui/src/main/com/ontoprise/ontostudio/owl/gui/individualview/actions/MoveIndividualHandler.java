/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview.actions;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.refactor.GenericRefactoringExecutionStarter;

import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.refactor.move.individual.MoveIndividualRefactoringFactory;

/* 
 * Created on: 30.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Instances, Action, Refactor
 */
/**
 * Action for moving of instances.
 */

public class MoveIndividualHandler extends AbstractOWLIndividualViewHandler {
    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.flogic.ui.instanceview.actions.AbstractInstanceViewHandler#runWithArgumentsSet()
     */
    @Override
    protected Object runWithArgumentsSet() {
        try {
            IStructuredSelection selection = (IStructuredSelection) _view.getTreeViewer().getSelection();
            if (selection != null && _view != null && !selection.isEmpty()) {
                List<?> list = selection.toList();
                IndividualViewItem[] items = (IndividualViewItem[]) list.toArray(new IndividualViewItem[0]);
                GenericRefactoringExecutionStarter.startRefactoring(_view.getSite().getShell(), MoveIndividualRefactoringFactory.MOVE_INDIVIDUAL_REFACTORING_ID, items, new ClazzTreeElement[] {_view.getSelectedClazz()});
            }
        } catch (Exception e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        return null;
    }
}
