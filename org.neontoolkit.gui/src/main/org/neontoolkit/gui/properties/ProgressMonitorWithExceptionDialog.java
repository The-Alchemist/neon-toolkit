/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.properties;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

/* 
 * Created on: 27.04.2007
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Progress, Rename
 */
/**
 * Progress monitor dialog for operations.
 */
public class ProgressMonitorWithExceptionDialog extends ProgressMonitorDialog {

	public ProgressMonitorWithExceptionDialog(Shell parent) {
		super(parent);
	}

	public void runWithException(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException, Exception {
		try {
			super.run(fork, cancelable, runnable);
		} catch (InvocationTargetException ite) {
			if (ite.getCause() instanceof Exception) {
				throw (Exception)ite.getCause();
			}
			throw ite;
		}
	}

}
