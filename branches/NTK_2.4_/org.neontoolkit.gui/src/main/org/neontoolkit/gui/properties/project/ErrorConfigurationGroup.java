/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.properties.project;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.neontoolkit.core.project.IOntologyProject;

/**
 * @author diwe
 *
 */
public class ErrorConfigurationGroup implements IDatamodelConfigurationGroup {
    private Group _settings;

    @Override
    public Composite createContents(Composite composite) {
        _settings = new Group(composite,SWT.NONE);
        _settings.setText("Configuration"); //$NON-NLS-1$
        _settings.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        _settings.setLayout(new FillLayout());
        
        new Text(composite, SWT.V_SCROLL | SWT.H_SCROLL).setText("No component found to show the configuration. Please take a look at the log file to find out if an exception occured while creating the appropriate control."); //$NON-NLS-1$
        return _settings;
    }

    @Override
    public Composite getControl() {
        return _settings;
    }

    @Override
    public boolean isValidGroup(IOntologyProject project) {
        return true;
    }

    @Override
    public void setSelection(IOntologyProject selectedProject) {
    }

}
