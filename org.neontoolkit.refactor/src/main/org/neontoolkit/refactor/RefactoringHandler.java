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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.neontoolkit.refactor.ui.RefactorUIStarter;


public class RefactoringHandler {
	private String _id;
	private IConfigurationElement _configElement;
	private IRefactoringFactory _factory;

	public RefactoringHandler(String id, IConfigurationElement configElement) {
		_id = id;
		_configElement = configElement;
	}
	
	public RefactorSupport createRefactorSupport(Object... parameters) throws CoreException {
		return new RefactorSupport(this, getRefactoringFactory().createRefactoring(parameters));
	}
	
	public String getId() {
		return _id;
	}
	
	public RefactorUIStarter getStarter(Refactoring refactoring) {
		RefactoringProcessor processor= (RefactoringProcessor)refactoring.getAdapter(RefactoringProcessor.class);
		if (processor == null)
			return null;
		try {
			RefactorUIStarter starter= new RefactorUIStarter();
			RefactoringWizard wizard= getRefactoringFactory().createWizard(refactoring);
			starter.initialize(wizard);
			return starter;
		} catch (CoreException ce) {
			return null;
		}
	}
	
	private IRefactoringFactory getRefactoringFactory() throws CoreException {
		if (_factory == null) {
			_factory = (IRefactoringFactory)_configElement.createExecutableExtension("factoryClass"); //$NON-NLS-1$
		}
		return _factory;
	}
}
