/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 30.04.2009
 * Created by: Dirk Wenke
 ******************************************************************************/
package org.neontoolkit.core.project;

/**
 * Additional interface for listeners that want to react on problems of
 * IOntologyProjects.
 * @author Dirk Wenke
 */
public interface IProjectFailureListener {
    
    /**
     * Listener method notifying of a project failure. The project and the exception
     * that occured are passed as arguments.
     * If the failure is only temporary, this method might be called again with the exception
     * parameter being null. This means that the project is valid again.
     * @param project
     * @param exception
     */
    void projectFailureOccured(String project, Exception exception);

}
