/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.search.ui;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
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
