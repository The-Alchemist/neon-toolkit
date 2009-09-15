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

/* 
 * Created on: 07.02.2008
 * Created by: Dirk Wenke
 */
public class RefactoringService {
	
	private Map<String, String> _renameRefactorings = new HashMap<String, String>();
	private Map<String, String> _deleteRefactorings = new HashMap<String, String>();
	
	/**
	 * 
	 */
	protected RefactoringService() {
		// TODO Auto-generated constructor stub
	}

	public void registerRenameRefactoring(String className, String refactoringId) {
		_renameRefactorings.put(className, refactoringId);
	}
	
	public void unregisterRenameRefactoring(String className, String refactoringId) {
		_renameRefactorings.remove(className);
	}
	
	public String getRenameRefactoring(String className) {
		return _renameRefactorings.get(className);
	}

	public void registerDeleteRefactoring(String className, String refactoringId) {
		_deleteRefactorings.put(className, refactoringId);
	}
	
	public void unregisterDeleteRefactoring(String className, String refactoringId) {
		_deleteRefactorings.remove(className);
	}
	
	public String getDeleteRefactoring(String className) {
		return _deleteRefactorings.get(className);
	}
}
