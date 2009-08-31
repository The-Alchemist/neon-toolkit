/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 12.06.2009
 * Created by: Dirk Wenke
 ******************************************************************************/
package org.neontoolkit.core.project;

import org.eclipse.core.resources.IProject;
import org.neontoolkit.core.IOntologyProjectFactory;

/**
 * @author Dirk Wenke
 *
 */
public class TestOntologyProjectFactory implements IOntologyProjectFactory {
    public static final String FACTORY_ID = "org.neontoolkit.core.project.TestOntologyProjectFactory"; //$NON-NLS-1$

    @Override
    public IOntologyProject createOntologyProject(IProject project) {
        return new TestOntologyProject(project.getName());
    }

    @Override
    public String getIdentifier() {
        return FACTORY_ID;
    }

}
