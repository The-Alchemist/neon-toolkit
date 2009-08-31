/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.refactor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;


public class GenericRefactoringExecutionStarter {

	public static int startRefactoring(Shell shell, String id, Object... parameters) throws CoreException {
		RefactorSupport support = RefactoringPlugin.getDefault().getRefactoringHandler(id).createRefactorSupport(parameters);
		if (support != null && support.getPreCheckStatus().isOK()) {
			return support.openDialog(shell);
		}
		return IDialogConstants.CANCEL_ID;
	}
}
