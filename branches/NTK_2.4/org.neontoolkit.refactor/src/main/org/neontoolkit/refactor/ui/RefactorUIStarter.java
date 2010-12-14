/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.refactor.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.refactor.RefactorStarter;


/* 
 * Created on: 04.04.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 */
/**
 * This class invokes the user interface for the given refactoring
 */

public class RefactorUIStarter {
	private RefactoringWizard _wizard;
	
	/**
	 * Initializes this user interface starter with the given
	 * wizard.
	 * 
	 * @param wizard the refactoring wizard to use
	 */
	public void initialize(RefactoringWizard wizard) {
		_wizard= wizard;		
	}
	
	/**
	 * Actually activates the user interface. This default implementation
	 * assumes that the configuration element passed to <code>initialize
	 * </code> has an attribute wizard denoting the wizard class to be
	 * used for the given refactoring.
	 * <p>
	 * Subclasses may override to open a different user interface
	 * 
	 * @param refactoring the refactoring for which the user interface
	 *  should be opened
	 * @param parent the parent shell to be used
	 * @param mustSaveEditors <code>true</code> iff dirty editors must be
	 *  saved before the refactoring is started, <code>false</code> otherwise
	 * 
	 * @exception CoreException if the user interface can't be activated
	 */
	public int activate(Refactoring refactoring, Shell parent) {
		return new RefactorStarter().activate(refactoring, _wizard, parent, _wizard.getDefaultPageTitle());
	}
}
