/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.refactor;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public interface IRefactoringFactory {

	ProcessorBasedRefactoring createRefactoring(Object... parameters);
	
	RefactoringWizard createWizard(Refactoring refactoring);
	
}
