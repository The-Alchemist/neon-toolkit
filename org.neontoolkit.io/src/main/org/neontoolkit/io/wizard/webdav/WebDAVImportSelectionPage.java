/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.io.wizard.webdav;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.wvcm.Resource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.io.IOPlugin;
import org.neontoolkit.io.Messages;
import org.neontoolkit.io.filter.FileFilter;
import org.neontoolkit.io.util.ImportExportUtils;
import org.neontoolkit.io.util.WebDAVUtils;
import org.neontoolkit.io.webdav.IWebDAVSelectionPage;
import org.neontoolkit.io.webdav.WebDAVConnection;
import org.neontoolkit.io.webdav.WebDAVLabelProvider;
import org.neontoolkit.io.webdav.WebDAVSiteManager;
import org.neontoolkit.io.webdav.WebDAVTreeContentProvider;
import org.neontoolkit.io.wizard.AbstractImportSelectionPage;

/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Wizard, WizardPage, Import, WebDAV, Ontology
 */

/**
 * Page for WebDAVImportWizard to specify from where to load an ontology
 * depending on a WebDAV-Connection. Selection of projectname, webDAVConnection,
 * folder and file
 */
public class WebDAVImportSelectionPage extends AbstractImportSelectionPage implements IWebDAVSelectionPage {

    //Combo to select existing webDAV-Site
    private Combo _webDAVSiteCombo; 
    //Tree to show the folder of selected webdav-connection
    private Tree _webDAVFolderTree; 
    //TreeViewer for folder of webdav-connection
    private TreeViewer _webDAVFolderTreeViewer; 
    //provides the folder of a webdav-connection or webdav-folder
    private WebDAVTreeContentProvider _webDAVFolderContentProvider; 
    //Tree to show the files of selected webdav-connection/-folder
    private Tree _webDAVFileTree; 
    //TreeViewer for files of webdav-connection/-folder
    private TreeViewer _webDAVFileTreeViewer; 
    //provides the files of a webdav-connectino or webdav-folder
    private WebDAVTreeContentProvider _webDAVFileContentProvider;
    //provides labels for webdav-folder and webdav-files
    private WebDAVLabelProvider _webDAVLabelProvider; 
    private Object _webDAVFolderSelection;
    private Object _webDAVFileSelection;
    //selected webdav-connection
    private WebDAVConnection _webDAVConnection = null;
    private URL _webDAVConnectionURL;
    //access to existing webDAV-Sites/Connections
    private WebDAVSiteManager _webDAVSiteManager = WebDAVSiteManager.getManager(); 

    private Composite _composite;

    private WebDAVSiteSettings _webDAVSiteSettingsAction = new WebDAVSiteSettings();
    
    private class WebDAVConnectionRunnableWithProgress implements IRunnableWithProgress {

        private WebDAVConnection _webDAVConn;

        public WebDAVConnectionRunnableWithProgress(WebDAVConnection conn) {
            this._webDAVConn = conn;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                monitor.beginTask(Messages.getString("WebDAVImportSelectionPage.28") + _webDAVConn.getServerURL().toExternalForm(), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                _webDAVConn.connect();
                monitor.done();
            } catch (Exception e) {
                throw new InvocationTargetException(e);
            }
        }
    }

    class EventHandler implements SelectionListener, ModifyListener {

        public void widgetSelected(SelectionEvent se) {
//            if (se.getSource() == _createButton) {
//                createNewProject();
//                initSiteCombo();
//                _webDAVSiteCombo.select(0);
//                if(_webDAVSiteCombo.getItemCount() > 0) {
//                	siteComboChanged(_webDAVSiteCombo.getItem(0));
//                } else {
//                	siteComboChanged(""); //$NON-NLS-1$
//                }
//            }
            checkStatus();
        }

        public void widgetDefaultSelected(SelectionEvent se) {
            widgetSelected(se);
        }

        public void modifyText(ModifyEvent me) {
            checkStatus();
        }
    }

    public WebDAVImportSelectionPage(FileFilter fileFilter) {
        super(fileFilter);
        setTitle(Messages.getString("WebDAVImportSelectionPage.0")); //$NON-NLS-1$
        setDescription(Messages.getString("WebDAVImportSelectionPage.1")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.AbstractImportSelectionPage#setFileFilter(com.ontoprise.ontostudio.io.FileFilter)
     */
    @Override
    public void setFileFilter(FileFilter fileFilter) {
        super.setFileFilter(fileFilter);
 
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(!visible) {
            return;
        }
        if(super._fileFilter == null || this._webDAVFileTreeViewer.getInput() == null) {
            this._webDAVFileContentProvider.setFileFilter(super._fileFilter);
            setPreselectedFileInput();
        } else {
            if(!this._webDAVFileContentProvider.getFileFilter().getClass().toString().equals(super._fileFilter.getClass().toString())) {
                this._webDAVFileContentProvider.setFileFilter(super._fileFilter);
                _webDAVFileTreeViewer.setInput(_webDAVFileTreeViewer.getInput());
            }
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl(Composite parent) {
        _composite = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout(6, true);
        _composite.setLayout(gridLayout);
        GridData gridData;
        EventHandler listener = new EventHandler();

        Label containerLabel = new Label(_composite, SWT.NONE);
        containerLabel.setText(Messages.getString("WebDAVImportSelectionPage.2")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 6;
        containerLabel.setLayoutData(gridData);

        Composite projectComposite = new Composite(_composite, SWT.NONE);
        GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData1.horizontalSpan = 6;
        projectComposite.setLayoutData(gridData1);
        createProjectControl(projectComposite);

        Label browseLabel = new Label(_composite, SWT.NONE);
        browseLabel.setText(Messages.getString("WebDAVImportSelectionPage.3")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 6;
        browseLabel.setLayoutData(gridData);

        _webDAVSiteCombo = new Combo(_composite, SWT.NONE | SWT.READ_ONLY);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData.horizontalSpan = 5;
        _webDAVSiteCombo.setLayoutData(gridData);
        _webDAVSiteCombo.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                siteComboChanged(_webDAVSiteCombo.getText());
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Button siteSettingsButton = new Button(_composite, SWT.NONE);
        siteSettingsButton.setText(Messages.getString("WebDAVImportSelectionPage.6")); //$NON-NLS-1$
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData.horizontalSpan = 1;
        siteSettingsButton.setLayoutData(gridData);
        siteSettingsButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                _webDAVSiteSettingsAction.run();
                if (_webDAVSiteSettingsAction.getStatus() == Window.OK) {
                    initSiteCombo();
                    setPreselectedFileInput();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Label browseFolderLabel = new Label(_composite, SWT.NONE);
        browseFolderLabel.setText(Messages.getString("WebDAVImportSelectionPage.4")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 3;
        browseFolderLabel.setLayoutData(gridData);

        Label browseFileLabel = new Label(_composite, SWT.NONE);
        browseFileLabel.setText(Messages.getString("WebDAVImportSelectionPage.5")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 3;
        browseFileLabel.setLayoutData(gridData);

        _webDAVFolderTreeViewer = new TreeViewer(_composite, SWT.BORDER);
        _webDAVFolderTreeViewer.setSorter(new ViewerSorter());
        _webDAVFolderContentProvider = new WebDAVTreeContentProvider();
        _webDAVFolderContentProvider._showFiles = false;
        _webDAVFolderTreeViewer.setContentProvider(_webDAVFolderContentProvider);
        _webDAVLabelProvider = new WebDAVLabelProvider();
        _webDAVFolderTreeViewer.setLabelProvider(_webDAVLabelProvider);
        _webDAVFolderTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            public void selectionChanged(SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    _webDAVFolderSelection = ((IStructuredSelection) event.getSelection()).getFirstElement();
                    TreeItem[] selectedItems = _webDAVFolderTree.getSelection();
                    if (selectedItems.length > 0) {
                        TreeItem selectedItem = selectedItems[0];
                        while (selectedItem.getParentItem() != null) {
                            selectedItem = selectedItem.getParentItem();
                        }
                        if (selectedItem.getData() instanceof WebDAVConnection) {
                            _webDAVConnection = (WebDAVConnection) selectedItem.getData();
                            _webDAVConnectionURL = _webDAVConnection.getServerURL();
                        }
                    }
                    _webDAVFileTreeViewer.setInput(_webDAVFolderSelection);
                }
                _fileInput.setText(""); //$NON-NLS-1$
                _fileInput.setToolTipText(null);
                checkStatus();
            }
        });
        _webDAVFolderTree = _webDAVFolderTreeViewer.getTree();
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        _webDAVFolderTree.setLayoutData(gridData);

        _webDAVFileTreeViewer = new TreeViewer(_composite, SWT.BORDER | SWT.SINGLE);
        _webDAVFileTreeViewer.setSorter(new ViewerSorter());
        _webDAVFileContentProvider = new WebDAVTreeContentProvider();
        _webDAVFileContentProvider._showFolder = false;
        _webDAVFileContentProvider.setFileFilter(super._fileFilter);
        _webDAVFileTreeViewer.setContentProvider(_webDAVFileContentProvider);
        _webDAVFileTreeViewer.setLabelProvider(_webDAVLabelProvider);
        _webDAVFileTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            public void selectionChanged(SelectionChangedEvent event) {
                String file = ""; //$NON-NLS-1$
                if (!event.getSelection().isEmpty()) {
                    IStructuredSelection selections = (IStructuredSelection) event.getSelection();

                    String server = ""; //$NON-NLS-1$
                    server = _webDAVConnectionURL.toExternalForm().substring(0,
                            _webDAVConnectionURL.toExternalForm().length() - _webDAVConnectionURL.getPath().length());
                    Object[] selectionArray = selections.toArray();
                    for (int i = 0; i < selectionArray.length; i++) {
                        Object fileSelection = selectionArray[i];
                        if (fileSelection instanceof Resource) {
                            try {
                                file = URLDecoder.decode(server + ((Resource) fileSelection).location().toString(), "UTF-8"); //$NON-NLS-1$                                
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                _fileInput.setText(file);
                _fileInput.setToolTipText(file);
            }
        });
        _webDAVFileTree = _webDAVFileTreeViewer.getTree();
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        _webDAVFileTree.setLayoutData(gridData);

        Label fileLabel = new Label(_composite, SWT.NONE);
        fileLabel.setText(Messages.getString("WebDAVImportSelectionPage.7")); //$NON-NLS-1$
        gridData = new GridData();
        gridData.horizontalSpan = 6;
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = false;
        fileLabel.setLayoutData(gridData);

        //extended Inputfield for filename
        _fileInput = new Text(_composite, SWT.BORDER);
        gridData = new GridData();
        gridData.horizontalSpan = 6;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        _fileInput.addModifyListener(listener);
        _fileInput.setLayoutData(gridData);
        _fileInput.setEditable(false);

//        _importAsRDFButton = new Button(_composite, SWT.CHECK);
//        _importAsRDFButton.setText(Messages.getString("FileSystemImportSelectionPage.0")); //$NON-NLS-1$
//        _importAsRDFButton.setToolTipText(Messages.getString("FileSystemImportSelectionPage.tooltip")); //$NON-NLS-1$
//        _importAsRDFButton.addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                if (_importAsRDFButton.getSelection()) {
//                    _selectedFileFormat = OntoBrokerOntologyFileFormat.RDF_XML;
//                } else {
//                    _selectedFileFormat = null;
//                }
//                checkStatus();
//            }
//        });
//        _importAsRDFButton.setVisible(false);
//        gridData = new GridData();
//        gridData.horizontalSpan = 6;
//        gridData.grabExcessHorizontalSpace = true;
//        gridData.horizontalAlignment = GridData.FILL;
//        _importAsRDFButton.setLayoutData(gridData);
        
        
        initValues();
        setControl(_composite);
        checkStatus();
    }

    @Override
    public URL getSelectedURL() {
        try {
            URL url = new URL(_fileInput.getText());
            return url;
        } catch (MalformedURLException e) {
            try {
                return new File(_fileInput.getText()).toURI().toURL();
            } catch (MalformedURLException e1) {
                IOPlugin.getDefault().logError("", e1); //$NON-NLS-1$
                return null;
            }
        }
    }

    @Override
    public void initValues() {
        super.initValues();
        initSiteCombo();
        _webDAVSiteSettingsAction.setSelectionPage(this);
    }

    
    public void setPreselectedFileInput() {
        URL fileUrl = getWebDAVUrlToSelect();
        //set WebDAVConnection selected
        if (!selectWebDAVSite(fileUrl)) {
            return;
        }
        //set Folder selection
        if (!selectWebDAVFolder(fileUrl)) {
            return;
        }
    }

    private URL getWebDAVUrlToSelect() {
        URL url = null;
        String folderInputString = IOPlugin.getDefault().getPluginPreferences().getString(IOPlugin.KEY_WEBDAV_ACTUAL_PATH);
        if (folderInputString.equals("")) { //$NON-NLS-1$
            url = getDefaultWebDAVPath();
            if (url != null) {
                folderInputString = url.toExternalForm();
            }
        }
        try {
            url = new URL(folderInputString);
        } catch (MalformedURLException e1) {
        }
        super.setSelectedFile(folderInputString);
        return url;
    }

    private URL getDefaultWebDAVPath() {
        URL url = null;
        String defaultWebDAVPath = ""; //$NON-NLS-1$

        if (_webDAVSiteCombo.getItemCount() > 0) {
            defaultWebDAVPath = _webDAVSiteCombo.getItem(0);
        }
        if (!_webDAVSiteCombo.getText().equals("")) { //$NON-NLS-1$
            defaultWebDAVPath = _webDAVSiteCombo.getText();
        }

        String defaultFolder = IOPlugin.getDefault().getPluginPreferences().getString(IOPlugin.KEY_WEBDAV_DEFAULT_PATH);
        if (defaultFolder.equals("")) { //$NON-NLS-1$
            defaultFolder = DEFAULT_WEBDAV_FOLDER_PATH;
            IOPlugin.getDefault().getPluginPreferences().setValue(IOPlugin.KEY_WEBDAV_DEFAULT_PATH, defaultFolder);
        }
        defaultWebDAVPath += "/" + defaultFolder; //$NON-NLS-1$

        try {
            url = new URL(defaultWebDAVPath);
        } catch (MalformedURLException e1) {
        }
        return url;
    }

    /**
     * selects the webdav-site for the given url within the combo
     * 
     * @param url
     * @return true if succeeds
     */
    private boolean selectWebDAVSite(URL url) {
        if (url == null) {
            return false;
        }
        WebDAVConnection conn = _webDAVSiteManager.getSiteFromUrl(url);
        if (conn == null) {
            return false;
        }
        _webDAVSiteCombo.setText(conn.getServerURL().toExternalForm());
        siteComboChanged(_webDAVSiteCombo.getText());
        return true;
    }

    private boolean selectWebDAVFolder(URL url) {
        boolean selected = false;
        if (url == null) {
            return selected;
        }
        String urlString = url.toExternalForm().replaceFirst(_webDAVSiteCombo.getText(), ""); //$NON-NLS-1$
        if(urlString.startsWith("/")) { //$NON-NLS-1$
            urlString = urlString.substring(1, urlString.length());
        }
        String[] folderArray = urlString.split("/"); //$NON-NLS-1$
        TreeItem[] items = _webDAVFolderTree.getItems();
        TreeItem folderItemToSelect = null;
        for (int i = 0; i < folderArray.length; i++) {
            String folderName = folderArray[i];
            for (int j = 0; j < items.length; j++) {
                TreeItem item = items[j];
                if (item.getText().equals(folderName)) {
                    folderItemToSelect = item;
                    _webDAVFolderTreeViewer.setExpandedState(folderItemToSelect.getData(), true);            
                    break;
                }
            }
            if (folderItemToSelect != null) {
                items = folderItemToSelect.getItems();
            }
        }

        if (folderItemToSelect != null) {
            selected = true;
            _webDAVFolderTree.setSelection(new TreeItem[] {folderItemToSelect});
            _webDAVFolderTree.showSelection();
            _webDAVFolderSelection = folderItemToSelect.getData();
            _webDAVFileTreeViewer.setInput(folderItemToSelect.getData());
        }
        return selected;
    }

    private void initSiteCombo() {
        _webDAVSiteCombo.removeAll();
        Object[] sites = _webDAVSiteManager.getSites().values().toArray();
        for (int i = 0; i < sites.length; i++) {
            Object site = sites[i];
            if (site instanceof WebDAVConnection) {
                URL url = ((WebDAVConnection) site).getServerURL();
                if (url != null) {
                    _webDAVSiteCombo.add(url.toExternalForm());
                }
            }
        }
        _webDAVFolderTreeViewer.setInput(null);
        _webDAVFileTreeViewer.setInput(null);
        _webDAVConnectionURL = null;
        _webDAVConnection = null;
        _fileInput.setText(""); //$NON-NLS-1$
        _fileInput.setToolTipText(null); 
    }

    private void siteComboChanged(String text) {
        _webDAVConnection = null;
        _webDAVConnectionURL = null;
        Object[] sites = _webDAVSiteManager.getSites().values().toArray();
        for (int i = 0; i < sites.length; i++) {
            Object site = sites[i];
            if (site instanceof WebDAVConnection) {
                URL url = ((WebDAVConnection) site).getServerURL();
                if (url != null) {
                    if (url.toExternalForm().equals(text)) {
                        _webDAVConnection = (WebDAVConnection) site;
                        _webDAVConnectionURL = url;
                        _webDAVFileTreeViewer.setInput(null);
                        _webDAVFolderTreeViewer.setInput(null);
                        IRunnableWithProgress op = new WebDAVConnectionRunnableWithProgress(_webDAVConnection);
                        try {
                            getContainer().run(false, false, op);
                            _webDAVFolderTreeViewer.setInput(site);
                        } catch (Exception e) {
                            WebDAVConnectException ex;
                            String serverName = _webDAVConnection.getServerURL().toExternalForm();
                            Throwable e1;
                            if (e instanceof InvocationTargetException) {
                            	e1 = ((InvocationTargetException) e).getTargetException();
                            } else {
                            	e1 = e;
                            }
                            ex = new WebDAVConnectException(serverName, e1);
                            
                            if (isCurrentPage()) {
                            	NeonToolkitExceptionHandler handler = new NeonToolkitExceptionHandler();
                                handler.handleException(ex, e1, getShell());
                                return;
                            }
                        }
                        _fileInput.setText(""); //$NON-NLS-1$
                        _fileInput.setToolTipText(null);
                        selectWebDAVFolder(getDefaultWebDAVPath());
                    }
                }
            }

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.webdav.IWebDAVSelectionPage#addSite(com.ontoprise.ontostudio.io.webdav.WebDAVConnection)
     */
    public void addSite(WebDAVConnection site) {
        this._webDAVSiteManager.addSite(site);
        _webDAVFolderTreeViewer.setInput(_webDAVSiteManager);
        _webDAVConnection = null;
        _webDAVFolderSelection = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.webdav.IWebDAVSelectionPage#deleteSite(com.ontoprise.ontostudio.io.webdav.WebDAVConnection)
     */
    public void deleteSite(WebDAVConnection site) {
        this._webDAVSiteManager.removeSite(site.getServerURL());
        _webDAVFolderTreeViewer.setInput(_webDAVSiteManager);
        _webDAVConnection = null;
        _webDAVFolderSelection = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.webdav.IWebDAVSelectionPage#refresh()
     */
    public void refresh() {
        this._webDAVFolderTreeViewer.refresh();
        this._webDAVFileTreeViewer.refresh();
    }

    public WebDAVConnection getSelectedWebDAVConnection() {
        return _webDAVConnection;
    }

    public Resource getSelectedWebDAVFile() {
        if (_webDAVFileSelection != null && _webDAVFileSelection instanceof Resource) {
            return (Resource) _webDAVFileSelection;
        }
        return null;
    }
    
    @Override
    public String[] getFiles() {
//        version url zurückgeben!!
        String file = _fileInput.getText();
        if(file.equals("")) { //$NON-NLS-1$
            return new String[0];
        }
        try {
            String fileName = getLastElement(new URL(file));
            String fileNameEnc = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            file = file.substring(0, file.length() - fileName.length());
            file += fileNameEnc;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new String[]{file};
    }
    
	/**
	 * Returns the last element in the given URLs path, or <code>null</code>
	 * if the URL is the root.
	 * <table>
	 * <caption>Example</caption>
	 * <tr>
	 *   <th>Given URL</th>
	 *   <th>Last Element</th>
	 * <tr>
	 *   <td>"http://hostname/"</td>
	 *   <td>null</td>
	 * <tr>
	 *   <td>"http://hostname/folder/</td>
	 *   <td>folder</td>
	 * <tr>
	 *   <td>"http://hostname/folder/file</td>
	 *   <td>file</td>
	 * </table>
	 * @param url a URL
	 * @return    the last element in the given URLs path, or
	 *            <code>null</code> if the URL is the root
	 */
	public static String getLastElement(URL url) {
		String file = url.getFile();
		int len = file.length();
		if (len == 0 || len == 1 && file.charAt(0) == '/')
			return null;

		int lastSlashIndex = -1;
		for (int i = len - 2; lastSlashIndex == -1 && i >= 0; --i) {
			if (file.charAt(i) == '/')
				lastSlashIndex = i;
		}
		boolean isDirectory = file.charAt(len - 1) == '/';
		if (lastSlashIndex == -1) {
			if (isDirectory) {
				return file.substring(0, len - 1);
			} else {
				return file;
			}
		} else {
			if (isDirectory) {
				return file.substring(lastSlashIndex + 1, len - 1);
			} else {
				return file.substring(lastSlashIndex + 1, len);
			}
		}
	}


    @Override
    public void checkStatus() {
        String status = null;
        String warning = null;
        String error = null;
        boolean pageComplete = true;

        setErrorMessage(null);
        super.checkStatus();
        if (_webDAVSiteCombo.getText().equals("")) { //$NON-NLS-1$
            updateStatus(null);
            setDescription(Messages.getString("WebDAVImportSelectionPage.30")); //$NON-NLS-1$
            setPageComplete(false);
        }

        if (_webDAVConnection != null && !_webDAVConnection.isConnected()) {
            if (_webDAVConnection.getConnectionException() != null) {
                setErrorMessage(Messages.getString("WebDAVImportSelectionPage.12") + _webDAVConnection.getServerURL().toExternalForm() //$NON-NLS-1$
                        + ": " //$NON-NLS-1$
                        + _webDAVConnection.getConnectionException().getMessage()
                        + "\n" //$NON-NLS-1$
                        + Messages.getString("WebDAVImportSelectionPage.29")); //$NON-NLS-1$
            } else {
                setErrorMessage(Messages.getString("WebDAVImportSelectionPage.14") + _webDAVConnection.getServerURL().toExternalForm() //$NON-NLS-1$
                        + "\n" //$NON-NLS-1$
                        + Messages.getString("WebDAVImportSelectionPage.29")); //$NON-NLS-1$
            }
            setPageComplete(false);
        }
        
        if(pageComplete) {
            String projectName = getSelectedProject();
            //check if transformation is needed    
            ArrayList<String> filesToBeTransformed = new ArrayList<String>();
            ArrayList<String> filesNotSupported = new ArrayList<String>();
            String[] files = getFiles();
            for (int i = 0; i < files.length; i++) {
                String file = files[i];
                String physicalUri = file;
                try {
                    URL authUrl = WebDAVUtils.getAuthImportUrl(physicalUri);
                    physicalUri = authUrl.toURI().toString();
                } catch (URISyntaxException e) {
                    //ignore
                }

                try {
                    String ontologyFileFormat = getFileFilter().getOntologyFileFormat();
                    if(ontologyFileFormat == null || !getFileFilter().accept(new File(file))) {
                        ontologyFileFormat = ImportExportUtils.getOntologyFileFormat(projectName, physicalUri);
                    }

//                    if (ontologyFileFormat != null && ontologyFileFormat.equals(OntoBrokerOntologyFileFormat.OWL_RDF) && files.length == 1) {
//                        _importAsRDFButton.setVisible(true);
//                    } else {
//                        _importAsRDFButton.setVisible(false);
//                    }
                    
                    if (_selectedFileFormat != null) {
                        ontologyFileFormat = _selectedFileFormat;
                    }
//                    String ontoLang = NeOnCorePlugin.getDefault().getOntologyProject(projectName).getOntologyLanguage();
//                    boolean rdfProject = ontoLang != null && ontoLang.equals(OntologyLanguage.RDF.toString());
//                    if (rdfProject) {
//                        // in case of an RDF project (and if OWL/RDF is detected as file format) the user is asked if he wants to 
//                        // import as plain RDF/XML or if he wants to import as OWL and transform afterwards. 
//                        if(_selectedFileFormat != null && ImportExportUtils.isTransformationRequired(projectName, ontologyFileFormat)) {
//                            filesToBeTransformed.add(file);
//                        }                       
//                    } else {
                        if(ImportExportUtils.isTransformationRequired(projectName, ontologyFileFormat)) {
                            filesToBeTransformed.add(file);
                        }                       
//                    }
                } catch (CommandException e2) {
                    filesNotSupported.add(file);
                }
            }
            String messages = ""; //$NON-NLS-1$
            if(filesNotSupported.size() > 0) {              
                messages = Messages.getString("AbstractImportSelectionPage.11") + " "; //$NON-NLS-1$ //$NON-NLS-2$
                for (int i = 0; i < filesNotSupported.size(); i++) {
                    String file = filesNotSupported.get(i);
                    if(i != 0) {
                        messages += ","; //$NON-NLS-1$
                    }
                    messages += " '" + file + "'";  //$NON-NLS-1$//$NON-NLS-2$
                }
                messages += " " + Messages.getString("AbstractImportSelectionPage.12"); //$NON-NLS-1$ //$NON-NLS-2$
                error = messages;
                pageComplete = false;               
            } else if(filesToBeTransformed.size() > 0) {      
                String projectLang = ""; //$NON-NLS-1$
                try {
                    projectLang = NeOnCorePlugin.getDefault().getOntologyProject(projectName).getOntologyLanguage();
                } catch (NeOnCoreException e) {
                    e.printStackTrace();
                }
                messages += Messages.getString("AbstractImportSelectionPage.9") + " " + projectLang + " " + Messages.getString("AbstractImportSelectionPage.10");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
                for (int i = 0; i < filesToBeTransformed.size(); i++) {
                    String file = filesToBeTransformed.get(i);
                    if(i != 0) {
                        messages += ","; //$NON-NLS-1$
                    }
                    messages += " '" + file + "'"; //$NON-NLS-1$ //$NON-NLS-2$
                }
                warning = messages;
            }
        }
        if(error != null) {
            setMessage(error, IMessageProvider.ERROR);          
        } else if(warning != null) {
            setMessage(warning, IMessageProvider.WARNING);          
        } else {
            updateStatus(status);
        }
        setPageComplete(pageComplete);
    }
}
