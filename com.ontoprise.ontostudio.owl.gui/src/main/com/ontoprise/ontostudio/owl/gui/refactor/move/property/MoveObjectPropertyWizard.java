/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.move.property;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.ontoprise.ontostudio.owl.gui.Messages;

/* 
 * Created on: 18.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 */
/**
 * Wizard for the move concept refactoring
 */

public class MoveObjectPropertyWizard extends RefactoringWizard {

    public MoveObjectPropertyWizard(Refactoring ref) {
        super(ref, DIALOG_BASED_USER_INTERFACE | NO_PREVIEW_PAGE);
        setDefaultPageTitle(Messages.MovePropertyWizard_0); 
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.ui.refactoring.RefactoringWizard#addUserInputPages()
     */
    @Override
    protected void addUserInputPages() {
         addPage(new MoveObjectPropertyPage((MoveObjectPropertyProcessor)getRefactoring().getAdapter(MoveObjectPropertyProcessor.class)));
    }
}
