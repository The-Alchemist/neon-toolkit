/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.refactor;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;

/* 
 * Created on: 04.04.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 */
/**
 * This class invokes the refactoring wizard and starts the refactoring
 */

public class RefactorStarter {
	RefactoringStatus _status;
	
	public RefactorStarter() {
		
	}

	public int activate(Refactoring refactoring, RefactoringWizard wizard, Shell parent, String dialogTitle) {
		int res = IDialogConstants.CANCEL_ID;
		try {
			RefactoringWizardOpenOperation op= new RefactoringWizardOpenOperation(wizard);
			res = op.run(parent, dialogTitle);
			_status= op.getInitialConditionCheckingStatus();
		} catch (InterruptedException e) {
			//operation was cancelled by user. Do nothing!
		}
		return res;
	}
	
	public RefactoringStatus getInitialConditionCheckingStatus() {
		return _status;
	}

}
