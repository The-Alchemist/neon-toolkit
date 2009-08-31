/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 16.06.2009
 * Created by: diwe
 ******************************************************************************/
package org.neontoolkit.gui.properties.project;

import org.eclipse.swt.widgets.Composite;
import org.neontoolkit.core.project.IOntologyProject;

/**
 * @author Dirk Wenke
 *
 */
public interface IDatamodelConfigurationGroup {

    /**
     * Create the content of the group
     * @param composite
     */
    Composite createContents(Composite composite);
    
    /**
     * Passes the configuration of the current selected project.
     * @param properties
     */
    void setSelection(IOntologyProject selectedProject);
    
    /**
     * Returns true, if this group can be used to display the properties
     * of the given project.
     * @param project
     * @return
     */
    boolean isValidGroup(IOntologyProject project);
    
    /**
     * Returns the control created when createContents() has been called.
     * @return
     */
    Composite getControl();
}
