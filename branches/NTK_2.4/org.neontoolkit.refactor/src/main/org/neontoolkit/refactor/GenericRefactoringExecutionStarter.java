/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
