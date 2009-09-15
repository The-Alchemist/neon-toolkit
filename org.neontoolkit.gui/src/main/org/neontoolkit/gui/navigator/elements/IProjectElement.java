/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.elements;

/* 
 * Created on: 13.04.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator
 */
/**
 * This interface defines the methods that can be called for elements belonging to 
 * a project.
 */
public interface IProjectElement {

	/**
	 * returns the name of the project
	 * @return
	 */
    String getProjectName();
}
