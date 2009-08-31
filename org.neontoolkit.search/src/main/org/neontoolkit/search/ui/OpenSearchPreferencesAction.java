/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 12.05.2009
 * Created by: ulsi
 ******************************************************************************/

/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.neontoolkit.search.ui;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.neontoolkit.search.Messages;


/**
 * Opens the search preferences dialog
 */
public class OpenSearchPreferencesAction extends Action {
    public OpenSearchPreferencesAction() {
        super(Messages.OpenSearchPreferencesAction_label); 
        setToolTipText(Messages.OpenSearchPreferencesAction_tooltip); 
        //PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IWorkbenchHelpContextIds.OPEN_PREFERENCES_ACTION);
    }

    /* (non-Javadoc)
     * Method declared on Action.
     */
    @Override
    public void run() {
        Shell shell= new Shell();
        PreferencesUtil.createPreferenceDialogOn(shell, "org.eclipse.search.preferences.SearchPreferencePage" /*original: SearchPreferencePage.PAGE_ID*/, null, null).open(); //$NON-NLS-1$
    }

}
