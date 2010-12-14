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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.refactor.ui.RefactorUIStarter;


/* 
 * Created on: 18.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 */
/**
 * This abstract class provides methods to open the refactoring dialog and active
 * the refactoring.
 */

public class RefactorSupport {
	private RefactoringHandler _handler;
	private ProcessorBasedRefactoring _refactoring;
	private RefactoringStatus _preCheckStatus;

	public RefactorSupport(RefactoringHandler handler, ProcessorBasedRefactoring refactoring) {
		_refactoring = refactoring;
		_handler = handler;
	}
	/**
	 * Opens the refactoring dialog for this rename support. 
	 * 
	 * @param parent a shell used as a parent for the refactoring dialog.
	 * @throws CoreException if an unexpected exception occurs while opening the
	 * dialog.
	 */
	public int openDialog(Shell parent) throws CoreException {
		ensureChecked();
		if (_preCheckStatus.hasFatalError() || _handler == null) {
			showInformation(parent, _preCheckStatus);
			return IDialogConstants.CANCEL_ID; 
		}
		RefactorUIStarter starter = _handler.getStarter(_refactoring);
		return starter.activate(_refactoring, parent);
	}

	private void ensureChecked() throws CoreException {
		if (_preCheckStatus == null) {
			if (!_refactoring.isApplicable()) {
				//TODO
				_preCheckStatus= RefactoringStatus.createFatalErrorStatus("");//RenameSupport_not_available);  //$NON-NLS-1$
			} else {
				_preCheckStatus= new RefactoringStatus();
			}
		}
	}

	public RefactoringStatus getPreCheckStatus() throws CoreException {
		if (_preCheckStatus == null) {
			ensureChecked();
		}
		return _preCheckStatus;
	}

	void showInformation(Shell parent, RefactoringStatus status) {
		String message= status.getMessageMatchingSeverity(RefactoringStatus.FATAL);
		//TODO
		MessageDialog.openInformation(parent, "", message);//RenameSupport_dialog_title, message);  //$NON-NLS-1$
	}
}
