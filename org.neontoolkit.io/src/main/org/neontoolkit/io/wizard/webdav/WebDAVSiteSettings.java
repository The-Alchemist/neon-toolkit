/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.io.wizard.webdav;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.neontoolkit.io.webdav.IWebDAVSelectionPage;
import org.neontoolkit.io.webdav.NewWebDAVConnectionWizard;
import org.neontoolkit.io.webdav.WebDAVConnection;


/* 
 * Created on 08.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: WebDAV, Connection, Wizard, Settings
 */

/**
 * Action to open the Settings for WebDAV
 */
public class WebDAVSiteSettings extends Action {

    private IWebDAVSelectionPage _page;
    private int _status;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        NewWebDAVConnectionWizard newWebDAVWizard = new NewWebDAVConnectionWizard();
        WizardDialog wizardDialog = new WizardDialog(_page.getShell(), newWebDAVWizard);
        if (_page != null) {
            _status = wizardDialog.open();
        }
    }

    public void setSelectionPage(IWebDAVSelectionPage page) {
        this._page = page;
    }

    public void setSelectedSite(WebDAVConnection site) {
    }
    
    public int getStatus() {
        return _status;
    }
}
