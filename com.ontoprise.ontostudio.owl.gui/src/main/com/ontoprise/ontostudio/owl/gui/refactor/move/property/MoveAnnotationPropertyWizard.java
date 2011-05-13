/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 * based on the Class MoveAnnotationPropertyProcessor with ontoprise GmbH copyrights 
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.move.property;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.ontoprise.ontostudio.owl.gui.Messages;

/**
 * Wizard for the move concept refactoring
 */

public class MoveAnnotationPropertyWizard extends RefactoringWizard {

    public MoveAnnotationPropertyWizard(Refactoring ref) {
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
         addPage(new MoveAnnotationPropertyPage((MoveAnnotationPropertyProcessor)getRefactoring().getAdapter(MoveAnnotationPropertyProcessor.class)));
    }
}
