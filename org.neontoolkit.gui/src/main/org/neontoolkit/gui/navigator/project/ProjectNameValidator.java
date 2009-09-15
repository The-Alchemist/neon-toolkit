/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.project;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IInputValidator;
import org.neontoolkit.core.command.project.ExistsProject;
import org.neontoolkit.gui.Messages;

public class ProjectNameValidator implements IInputValidator {
	private static ProjectNameValidator _defaultInstance;
	
	private String _oldProjectName;
	
	public ProjectNameValidator(String oldProjectName) {
        _oldProjectName = oldProjectName;
    }
	
	public static ProjectNameValidator getDefaultValidator() {
		if (_defaultInstance == null) {
			_defaultInstance = new ProjectNameValidator();
		}
		return _defaultInstance;
	}
	
	public ProjectNameValidator() {
		this(null);
	}

    public String isValid(String projectName) {
        if (_oldProjectName != null && _oldProjectName.equals(projectName)) {
            return ""; //$NON-NLS-1$
        }
		IStatus status = ResourcesPlugin.getWorkspace().validateName(projectName, IResource.PROJECT);
		if (status.getCode() != IStatus.OK) {
			return status.getMessage();
		}
        if (projectExists(projectName)) {
            return Messages.ProjectNameValidator_1; 
        }
        return null;
    }

    private boolean projectExists(String projectName) {
    	return new ExistsProject(projectName).exists();
    }
}
