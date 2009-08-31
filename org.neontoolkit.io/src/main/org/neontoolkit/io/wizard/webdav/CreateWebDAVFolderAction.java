/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.io.wizard.webdav;

import java.io.IOException;
import java.net.URLEncoder;

import javax.wvcm.Folder;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.neontoolkit.io.Messages;
import org.neontoolkit.io.webdav.IWebDAVSelectionPage;
import org.neontoolkit.io.webdav.WebDAVConnection;


/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Action, Export, WebDAV, Folder
 */

/**
 * Action to create a WebDAV folder
 */
public class CreateWebDAVFolderAction extends Action {

    private IWebDAVSelectionPage _page;
    private WebDAVConnection _site;
    private Folder _folder;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if (_folder != null) {
            createNewFolder(_folder.location().toString());
        } else if (_site != null) {
            createNewFolder(_site.getServerURL().getPath());
        }
        this._page.refresh();
    }

    private void createNewFolder(String folder) {
        IInputValidator validator = new IInputValidator() {

            public String isValid(String newText) {
                try {
                    String file2 = URLEncoder.encode(newText);
                    if (!file2.equals(newText)) {
                        return Messages.getString("ActionCreateFolder.0"); //$NON-NLS-1$
                    }
                } catch (Exception e) {
                    return Messages.getString("ActionCreateFolder.0"); //$NON-NLS-1$
                }
                return null;
            }
        };
        InputDialog folderNameDialog = new InputDialog(this._page.getShell(),
                Messages.getString("ActionCreateFolder.2"), Messages.getString("ActionCreateFolder.3"), "", validator); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (folderNameDialog.open() == Window.OK) {
            String folderName = folderNameDialog.getValue();
            try {
                this._site.createFolder(folder, folderName);
            } catch (IOException e) {
                MessageDialog.openError(this._page.getShell(), Messages.getString("ActionCreateFolder.5"), e.getMessage()); //$NON-NLS-1$
            }
        }
    }

    
    public void setSelectionPage(IWebDAVSelectionPage page) {
        this._page = page;
    }

    /*
     * WebDAV connection for folder creation
     */
    public void setSelectedSite(WebDAVConnection site) {
        this._site = site;
    }

    /*
     * Parent WebDAV folder
     */
    public void setSelectedFolder(Folder folder) {
        this._folder = folder;
    }
}
