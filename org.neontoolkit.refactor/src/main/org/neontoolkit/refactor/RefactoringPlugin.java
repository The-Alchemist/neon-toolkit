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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;

public class RefactoringPlugin extends Plugin {
	private static final String EXT_POINT_ONTOLOGY_REFACTORINGS = "org.neontoolkit.refactor.ontologyRefactorings"; //$NON-NLS-1$
	
	private static RefactoringPlugin _instance;
	private Map<String, RefactoringHandler> _handlers;
	private RefactoringService _refactoringService;

	public RefactoringPlugin() {
		_instance = this;
	}
	
	public static RefactoringPlugin getDefault() {
		if (_instance == null) {
			_instance = new RefactoringPlugin();
		}
		return _instance;
	}
	
	public RefactoringService getRefactoringService() {
		if (_refactoringService == null) {
			_refactoringService = new RefactoringService();
		}
		return _refactoringService;
	}
	
	public RefactoringHandler getRefactoringHandler(String id) {
		if (_handlers == null) {
			initRefactoringHandlers();
		}
		return _handlers.get(id);
	}
	
	private void initRefactoringHandlers() {
		_handlers = new HashMap<String, RefactoringHandler>();
        IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extPoint = reg.getExtensionPoint(EXT_POINT_ONTOLOGY_REFACTORINGS);
		IExtension[] extension = extPoint.getExtensions();
		for (int i = 0; i < extension.length; i++) {
			IConfigurationElement[] confElems = extension[i].getConfigurationElements();
			IConfigurationElement confElem = null;
			for (int j = 0; j < confElems.length; j++) {
				confElem = confElems[j];
				if (confElem.getName().equals("refactoring")) { //$NON-NLS-1$
					String id = confElem.getAttribute("id"); //$NON-NLS-1$
					_handlers.put(id, new RefactoringHandler(id,confElem));
				}
			}
		}
	}
}
