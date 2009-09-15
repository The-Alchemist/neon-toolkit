/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.datatype;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.ontoprise.ontostudio.owl.gui.Messages;

/**
 * Wizard showing all options that this delete class refactoring provides.
 */

public class DeleteDatatypeWizard extends RefactoringWizard {

    /**
     * @param refactoring
     * @param flags
     */
    public DeleteDatatypeWizard(Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | NO_PREVIEW_PAGE);
        setDefaultPageTitle(Messages.DeleteDatatypeWizard_0); 
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.ui.refactoring.RefactoringWizard#addUserInputPages()
     */
    @Override
    protected void addUserInputPages() {
    }

}
