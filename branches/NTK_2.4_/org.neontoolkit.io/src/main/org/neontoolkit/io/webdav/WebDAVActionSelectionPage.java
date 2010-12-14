/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io.webdav;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.neontoolkit.io.Messages;


/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Manage, WebDAV, Connection
 */

/**
 * Page for creating and editing WebDAV Connections
 */
public class WebDAVActionSelectionPage extends WizardPage {

    protected Table _webDAVFolderTable;
    protected TableViewer _webDAVFolderTableViewer;
    protected WebDAVTreeContentProvider _webDAVFolderContentProvider;
    protected WebDAVLabelProvider _webDAVLabelProvider;

    protected Object _folderSelection;

    protected IWizardPage _prevPage;
    protected Composite _composite;
    protected String _preselectedProject;

    protected Button _removeSiteOption;
    private Button _editSiteOption;
    private Button _createNewSiteOption;

    protected WebDAVConnection _webDAVConnection = null;

    protected WebDAVSiteManager _webDAVSiteManager = WebDAVSiteManager.getManager();

    class EventHandler implements SelectionListener, ModifyListener {

        public void widgetSelected(SelectionEvent se) {
            if (se.getSource().equals(_createNewSiteOption)) {
                ((NewWebDAVConnectionWizard) getWizard()).setWebDAVConnection(null);
                _webDAVFolderTable.setEnabled(false);
            } else if (se.getSource() == _editSiteOption) {
                ((NewWebDAVConnectionWizard) getWizard()).setWebDAVConnection(_webDAVConnection);
                _webDAVFolderTable.setEnabled(true);
            } else if (se.getSource() == _removeSiteOption) {
                ((NewWebDAVConnectionWizard) getWizard()).setWebDAVConnection(_webDAVConnection);
                _webDAVFolderTable.setEnabled(true);
            }
            checkStatus();
        }

        public void widgetDefaultSelected(SelectionEvent se) {
            widgetSelected(se);
        }

        public void modifyText(ModifyEvent me) {
            checkStatus();
        }
    }

    public WebDAVActionSelectionPage() {
        super("wizardPage"); //$NON-NLS-1$
        setTitle(Messages.getString("WebDAVActionSelectionPage.0")); //$NON-NLS-1$
        setDescription(Messages.getString("WebDAVActionSelectionPage.1")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        _composite = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout(6, true);
        _composite.setLayout(gridLayout);
        GridData gridData;
        EventHandler listener = new EventHandler();

        _createNewSiteOption = new Button(_composite, SWT.RADIO);
        _createNewSiteOption.setText(Messages.getString("WebDAVActionSelectionPage.3")); //$NON-NLS-1$
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData.horizontalSpan = 6;
        _createNewSiteOption.setLayoutData(gridData);
        _createNewSiteOption.addSelectionListener(listener);

        _editSiteOption = new Button(_composite, SWT.RADIO);
        _editSiteOption.setText(Messages.getString("WebDAVActionSelectionPage.4")); //$NON-NLS-1$
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData.horizontalSpan = 6;
        _editSiteOption.setLayoutData(gridData);
        _editSiteOption.addSelectionListener(listener);

        _removeSiteOption = new Button(_composite, SWT.RADIO);
        _removeSiteOption.setText(Messages.getString("WebDAVActionSelectionPage.5")); //$NON-NLS-1$
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData.horizontalSpan = 6;
        _removeSiteOption.setLayoutData(gridData);
        _removeSiteOption.addSelectionListener(listener);

        Label containerLabel = new Label(_composite, SWT.NONE);
        containerLabel.setText(Messages.getString("WebDAVActionSelectionPage.2")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 6;
        containerLabel.setLayoutData(gridData);

        _webDAVFolderTableViewer = new TableViewer(_composite, SWT.BORDER);
        _webDAVFolderContentProvider = new WebDAVTreeContentProvider();
        _webDAVFolderContentProvider._showFiles = false;
        _webDAVFolderTableViewer.setContentProvider(_webDAVFolderContentProvider);
        _webDAVLabelProvider = new WebDAVLabelProvider();
        _webDAVFolderTableViewer.setLabelProvider(_webDAVLabelProvider);
        _webDAVFolderTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            public void selectionChanged(SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    _folderSelection = ((IStructuredSelection) event.getSelection()).getFirstElement();
                    TableItem[] selectedItems = _webDAVFolderTable.getSelection();
                    if (selectedItems.length > 0) {
                        TableItem selectedItem = selectedItems[0];
                        if (selectedItem.getData() instanceof WebDAVConnection) {
                            _webDAVConnection = (WebDAVConnection) selectedItem.getData();
                        }
                    }
                    ((NewWebDAVConnectionWizard) getWizard()).setWebDAVConnection(_webDAVConnection);
                }
                checkStatus();
            }
        });
        _webDAVFolderTable = _webDAVFolderTableViewer.getTable();
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 6;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        _webDAVFolderTable.setLayoutData(gridData);
        _webDAVFolderTable.setEnabled(false);

        initControls();
        setControl(_composite);
        checkStatus();
    }

    protected void initControls() {
        _webDAVFolderTableViewer.setInput(_webDAVSiteManager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.webdav.IWebDAVSelectionPage#refresh()
     */
    public void refresh() {
        this._webDAVFolderTableViewer.refresh();
    }

    public WebDAVConnection getSelectedWebDAVConnection() {
        return _webDAVConnection;
    }

    public void checkStatus() {
        setPageComplete(this._removeSiteOption.getSelection());
        if (_editSiteOption.getSelection()) {
            setDescription(Messages.getString("WebDAVActionSelectionPage.6")); //$NON-NLS-1$			
        } else if (_removeSiteOption.getSelection()) {
            setDescription(Messages.getString("WebDAVActionSelectionPage.7")); //$NON-NLS-1$
        } else {
            setDescription(Messages.getString("WebDAVActionSelectionPage.1")); //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
     */
    @Override
    public boolean isPageComplete() {
        return super.isPageComplete() || !isCurrentPage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    @Override
    public boolean canFlipToNextPage() {
        return (!this._removeSiteOption.getSelection() && (this._webDAVConnection != null) || this._createNewSiteOption.getSelection());
    }
}
