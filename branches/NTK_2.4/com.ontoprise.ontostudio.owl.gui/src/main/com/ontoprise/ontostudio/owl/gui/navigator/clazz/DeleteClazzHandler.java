/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.clazz;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.actions.AbstractDeleteHandler;
import org.neontoolkit.refactor.GenericRefactoringExecutionStarter;

/**
 * Action to delete classes in the tree.
 */

public class DeleteClazzHandler extends AbstractDeleteHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#doDelete(java.lang.Object[], java.lang.Object[])
     */
    @Override
    public boolean doDelete(Object[] items, Object[] parentItems) {
        assert items.length == parentItems.length;
        int result = 0;
        try {
            ClazzTreeElement[][] res = getClazzes(items, parentItems);
            result = GenericRefactoringExecutionStarter.startRefactoring(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "com.ontoprise.ontostudio.owl.gui.refactor.deleteClazz", //$NON-NLS-1$
                    res[0],
                    res[1]);
        } catch (CoreException ce) {
            new NeonToolkitExceptionHandler().handleException(ce);
            return false;
        }
        return result == IDialogConstants.CANCEL_ID ? false : true;
    }

    private ClazzTreeElement[][] getClazzes(Object[] items, Object[] parentItems) {
        List<ClazzTreeElement> clazzes = new ArrayList<ClazzTreeElement>();
        List<ClazzTreeElement> parentClazzes = new ArrayList<ClazzTreeElement>();
        for (int i = 0; i < items.length; i++) {
            if (items[i] instanceof ClazzTreeElement) {
                clazzes.add((ClazzTreeElement) items[i]);
                parentClazzes.add(parentItems[i] instanceof ClazzTreeElement ? (ClazzTreeElement) parentItems[i] : null);
            }
        }
        ClazzTreeElement[][] res = new ClazzTreeElement[2][clazzes.size()];
        clazzes.toArray(res[0]);
        parentClazzes.toArray(res[1]);
        return res;
    }
}
