/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io.wizard.webdav;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.wvcm.Folder;
import javax.wvcm.Resource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.io.IOPlugin;
import org.neontoolkit.io.Messages;
import org.neontoolkit.io.filter.FileFilter;
import org.neontoolkit.io.webdav.IWebDAVSelectionPage;
import org.neontoolkit.io.webdav.WebDAVConnection;
import org.neontoolkit.io.webdav.WebDAVLabelProvider;
import org.neontoolkit.io.webdav.WebDAVSiteManager;
import org.neontoolkit.io.webdav.WebDAVTreeContentProvider;
import org.neontoolkit.io.wizard.AbstractExportSelectionPage;

/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Wizard, WizardPage, Export, WebDAV, Ontology
 */

/**
 * Page for WebDAVExportWizard to specify where to save an ontology depending on
 * a WebDAV-Connection. Selection of projectname, module, webDAVConnection,
 * folder and file
 */
public class WebDAVExportSelectionPage extends AbstractExportSelectionPage implements IWebDAVSelectionPage {
//  Combo to select existing webDAV-Site
    private Combo _webDAVSiteCombo; 
//  Combo to select the file type    
    private Combo _fileTypeCombo;
//  Viewer for _fileTypeCombo    
    private ComboViewer _fileTypeComboViewer;
//  Tree to show the folder of selected webdav-connection
    private Tree _webDAVFolderTree; 
//  TreeViewer for folder of webdav-connection
    private TreeViewer _webDAVFolderTreeViewer; 
//  provides the folder of a webdav-connection or -folder
    private WebDAVTreeContentProvider _webDAVFolderContentProvider;
//  Tree to show the files of selected webdav-connection/-folder
    private Tree _webDAVFileTree; 
//  TreeViewer for files of webdav-connection/-folder
    private TreeViewer _webDAVFileTreeViewer; 
//  provides the files of a webdav-connection or webdav-folder
    private WebDAVTreeContentProvider _webDAVFileContentProvider;
//  provides labels for webdav-folder and -file    
    private WebDAVLabelProvider _webDAVLabelProvider;
//  Text showing the selected folder-path, read-only    
    private Text _folderInputText; 
    private Object _webDAVFolderSelection;
    private Object _webDAVFileSelection;
//  selected  webdav-connection
    private WebDAVConnection _webDAVConnection = null; 
    //access to existing webDAV-Sites/Connections
    private WebDAVSiteManager _webDAVSiteManager = WebDAVSiteManager.getManager(); 
    private Composite _composite;

    private CreateWebDAVFolderAction _createWebDAVFolderAction;

    private WebDAVSiteSettings _webDAVSiteSettingsAction = new WebDAVSiteSettings();

    private class WebDAVConnectionRunnableWithProgress implements IRunnableWithProgress {

        private WebDAVConnection _webDAVConn;

        public WebDAVConnectionRunnableWithProgress(WebDAVConnection conn) {
            this._webDAVConn = conn;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                monitor.beginTask(Messages.getString("WebDAVExportSelectionPage.28") + _webDAVConn.getServerURL().toExternalForm(), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                _webDAVConn.connect();
                monitor.done();
            } catch (Exception e) {
                throw new InvocationTargetException(e);
            }
        }
    }

    public WebDAVExportSelectionPage(FileFilter[] fileFilters) {
        super(fileFilters);
        setTitle(Messages.getString("WebDAVExportSelectionPage.0")); //$NON-NLS-1$
        setDescription(Messages.getString("WebDAVExportSelectionPage.1")); //$NON-NLS-1$
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
        containerLabel.setText(Messages.getString("WebDAVExportSelectionPage.2")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 6;
        containerLabel.setLayoutData(gridData);

        //extended Combo to select a project
        _projectCombo = new Combo(_composite, SWT.READ_ONLY);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData.horizontalSpan = 6;
        _projectCombo.setLayoutData(gridData);
        _projectCombo.addSelectionListener(listener);

        Label ontoLabel = new Label(_composite, SWT.NONE);
        ontoLabel.setText(Messages.getString("WebDAVExportSelectionPage.3")); //$NON-NLS-1$
        gridData.horizontalSpan = 6;
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        ontoLabel.setLayoutData(gridData);

        //extended Combo to select a module/ontology
        _ontoCombo = new Combo(_composite, SWT.READ_ONLY);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData.horizontalSpan = 6;
        _ontoCombo.setLayoutData(gridData);
        _ontoCombo.addSelectionListener(listener);

        Label browseLabel = new Label(_composite, SWT.NONE);
        browseLabel.setText(Messages.getString("WebDAVExportSelectionPage.4")); //$NON-NLS-1$
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
        siteSettingsButton.setText(Messages.getString("WebDAVExportSelectionPage.31")); //$NON-NLS-1$
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
        browseFolderLabel.setText(Messages.getString("WebDAVExportSelectionPage.5")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 3;
        browseFolderLabel.setLayoutData(gridData);

        Label browseFileLabel = new Label(_composite, SWT.NONE);
        browseFileLabel.setText(Messages.getString("WebDAVExportSelectionPage.6")); //$NON-NLS-1$
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
                _webDAVFolderSelection = null;
                if (!event.getSelection().isEmpty()) {
                    _webDAVFolderSelection = ((IStructuredSelection) event.getSelection()).getFirstElement();
                    TreeItem[] selectedItems = _webDAVFolderTree.getSelection();
                    if (selectedItems.length > 0) {
                        TreeItem selectedItem = selectedItems[0];
                        while (selectedItem.getParentItem() != null) {
                            selectedItem = selectedItem.getParentItem();
                        }
                    }
                    _webDAVFileTreeViewer.setInput(_webDAVFolderSelection);
                }
                setFileInput();
                updateCreateWebDAVFolderAction();
            }
        });
        _webDAVFolderTree = _webDAVFolderTreeViewer.getTree();
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        _webDAVFolderTree.setLayoutData(gridData);

        initWebDAVFolderTreeContextMenu();

        _webDAVFileTreeViewer = new TreeViewer(_composite, SWT.BORDER);
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
                _webDAVFileSelection = null;
                if (!event.getSelection().isEmpty()) {
                    _webDAVFileSelection = ((IStructuredSelection) event.getSelection()).getFirstElement();
                    setFileInput();
                }
            }
        });
        _webDAVFileTree = _webDAVFileTreeViewer.getTree();
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        _webDAVFileTree.setLayoutData(gridData);

        Label folderLabel = new Label(_composite, SWT.NONE);
        folderLabel.setText(Messages.getString("WebDAVExportSelectionPage.7")); //$NON-NLS-1$
        gridData = new GridData();
        gridData.horizontalSpan = 6;
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = false;
        folderLabel.setLayoutData(gridData);

        _folderInputText = new Text(_composite, SWT.BORDER);
        gridData = new GridData();
        gridData.horizontalSpan = 6;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        _folderInputText.addModifyListener(listener);
        _folderInputText.setLayoutData(gridData);
        _folderInputText.setEditable(false);
        
        Label fileLabel = new Label(_composite, SWT.NONE);
        fileLabel.setText(Messages.getString("WebDAVExportSelectionPage.8")); //$NON-NLS-1$
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

        Label fileTypeLabel = new Label(_composite, SWT.NONE);
        fileTypeLabel.setText(Messages.getString("WebDAVExportSelectionPage.9")); //$NON-NLS-1$
        gridData = new GridData();
        gridData.horizontalSpan = 6;
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = false;
        fileTypeLabel.setLayoutData(gridData);
        
        _fileTypeCombo = new Combo(_composite, SWT.READ_ONLY);
        _fileTypeComboViewer = new ComboViewer(_fileTypeCombo);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData.horizontalSpan = 6;
        _fileTypeCombo.setLayoutData(gridData);
        
        _fileTypeComboViewer.setContentProvider(new ArrayContentProvider());
        _fileTypeComboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof FileFilter) {
                    FileFilter filter = (FileFilter) element;
                    return filter.getDescription();
                }
                return super.getText(element);
            }
        });
        _fileTypeComboViewer.setInput(_fileFilters);
        _fileTypeComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
        
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                if (selection instanceof StructuredSelection) {
                    FileFilter selectedFilter = (FileFilter) ((StructuredSelection)selection).getFirstElement();
                    if (selectedFilter != null) {
                        _fileFilter = selectedFilter;
                        _webDAVFileContentProvider.setFileFilter(_fileFilter);
                        _webDAVFileTreeViewer.refresh(true);
                        refreshFileName();
                    }
                }
            }
        });
        initControls();
        setControl(_composite);
        updateCreateWebDAVFolderAction();
        initFileFormat();
        checkStatus();
    }
    
    private void refreshFileName() {
        String fileName = _fileInput.getText(); 
        if(!fileName.equals("")&& _fileFilter != null) { //$NON-NLS-1$
            String ext = _fileFilter.getDefaultExtension();
            if(!fileName.endsWith(ext)) {
                fileName = getFileWithoutKnownFileExtension();
                _fileInput.setText(fileName + ext);
            }
        }
    }
    
    private void initFileFormat() {
        
        try {
            String project = getSelectedProject();
            if (project != null) {
                /*String language = */NeOnCorePlugin.getDefault().getOntologyProject(project).getOntologyLanguage();
//                StructuredSelection selection = new StructuredSelection(new OxmlFileFilter());
//                if (language.equals(OntologyLanguage.RDF)) {
//                    selection = new StructuredSelection(new RdfFileFilter());
//                } else if (language.equals(OntologyLanguage.OWL)) {
//                    selection = new StructuredSelection(new OwlFileFilter());
//                }
//                _fileTypeComboViewer.setSelection(selection, true);
            }
        } catch (Exception e1) {
            // nothing to do, F_LOGIC is taken as default
        }
    }

    /**
     * creates the webdav-folder create action and adds it to the context menu
     * of FolderTree
     */
    void initWebDAVFolderTreeContextMenu() {
        Control menuControl = _webDAVFolderTree;
        MenuManager menuMgr = new MenuManager("PopUp"); //$NON-NLS-1$
        _createWebDAVFolderAction = new CreateWebDAVFolderAction();
        _createWebDAVFolderAction.setText(Messages.getString("WebDAVExportSelectionPage.24")); //$NON-NLS-1$
        _createWebDAVFolderAction.setSelectionPage(this);
        menuMgr.add(_createWebDAVFolderAction);

        Menu menu = menuMgr.createContextMenu(menuControl);
        menuControl.setMenu(menu);
    }

    /**
     * inits the project-, ontology- and webdavsite-combos sets preselected
     * webdav-folder/file
     */
    @Override
    protected void initControls() {
        super.initControls(); //inits project- and ontology-combo
        initSiteCombo();
        _webDAVSiteSettingsAction.setSelectionPage(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.wizard.AbstractExportSelectionPage#setFileInput(java.lang.String)
     */
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
        //set file selection
        selectWebDAVFile(fileUrl);
    }

    private URL getWebDAVUrlToSelect() {
        URL url = null;
//        try {
//            if (super.getSelectedOntologyUri() != null) {
//            	Ontology ontology = DatamodelPlugin.getDefault().getOntologyManager(super.getSelectedProject()).getOntology(super.getSelectedOntologyUri());
//                URI uri = DatamodelPlugin.getDefault().getPhysicalOntologyUri(super.getSelectedProject(), ontology);
//                if(uri != null) {
//                	url = uri.toURL();
//                }
//            }
//            if (url != null) {
//                super.setSelectedFile(url.toExternalForm());
//            } else {
                String folderInputString = IOPlugin.getDefault().getPluginPreferences().getString(IOPlugin.KEY_WEBDAV_ACTUAL_PATH);
                if (folderInputString.equals("")) { //$NON-NLS-1$
                    url = getDefaultWebDAVPath();
                    if (url != null) {
                        folderInputString = url.toExternalForm();
                    }
                }
                String defaultFileName = getDefaultFileName();
                if (!defaultFileName.equals("")) { //$NON-NLS-1$
                    folderInputString += "/" + defaultFileName; //$NON-NLS-1$
                }
                try {
                    url = new URL(folderInputString);
                } catch (MalformedURLException e1) {
                }
                super.setSelectedFile(folderInputString);
//            }
//        } catch (MalformedURLException e) {
////			e.printStackTrace();
//		} catch (KAON2Exception e) {
//			e.printStackTrace();
//		}
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

    private String getDefaultFileName() {
        String defaultFileName = ""; //$NON-NLS-1$
        try {
            if (super.getSelectedOntologyUri() != null) {
            	defaultFileName = super.getSelectedOntologyUri();
//            	Ontology ontology = DatamodelPlugin.getDefault().getOntologyManager(super.getSelectedProject()).getOntology(super.getSelectedOntologyUri());
//            	URI uri = DatamodelPlugin.getDefault().getPhysicalOntologyUri(super.getSelectedProject(), ontology);
//            	defaultFileName = uri.toString();
            }
        } catch (Exception e) {
            IOPlugin.getDefault().logError("not valid: " + getSelectedOntologyUri(), e); //$NON-NLS-1$
        }
//        defaultFileName = StringUtil.unQuote(defaultFileName);
        if (defaultFileName.endsWith("/")) { //$NON-NLS-1$
            defaultFileName = defaultFileName.substring(0, defaultFileName.length() - 1);
        }
        if (defaultFileName.lastIndexOf("/") != -1 && defaultFileName.lastIndexOf("/") != defaultFileName.length()) { //$NON-NLS-1$ //$NON-NLS-2$
            defaultFileName = defaultFileName.substring(defaultFileName.lastIndexOf("/") + 1, defaultFileName.length()); //$NON-NLS-1$
        }
        if (!defaultFileName.equals("")) { //$NON-NLS-1$
            if (_fileFilter != null && !defaultFileName.endsWith(_fileFilter.getDefaultExtension())) {
                defaultFileName += _fileFilter.getDefaultExtension();
            }
        }
        return defaultFileName;
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
                    _webDAVFolderTreeViewer.setExpandedState(item.getData(), true);
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
        setFileInput();
        return selected;
    }

    private void selectWebDAVFile(URL url) {
        if (url == null) {
            return;
        }
        TreeItem[] fileItems = _webDAVFileTree.getItems();
        String fileString = url.getFile();
        if (fileString.lastIndexOf("/") == fileString.length()) { //$NON-NLS-1$
            return;
        }
        if (fileString.lastIndexOf("/") != -1) { //$NON-NLS-1$
            fileString = fileString.substring(fileString.lastIndexOf("/") + 1, fileString.length()); //$NON-NLS-1$
        }
        for (int i = 0; i < fileItems.length; i++) {
            TreeItem item = fileItems[i];
            if (item.getText().equals(fileString) || item.getText().startsWith(fileString + " (")) { //$NON-NLS-1$
                _webDAVFileTree.setSelection(new TreeItem[] {item});
                _webDAVFileTree.showItem(item);
                break;
            }
        }
        try {
            if (_webDAVConnection.isFileExisting(url)) {
                _webDAVFileSelection = _webDAVConnection.getFileFromUrl(url);
                setFileInput();
            } else {
                _webDAVFileSelection = null;
                _fileInput.setText(URLDecoder.decode(fileString, "UTF-8")); //$NON-NLS-1$
            }
        } catch (WebDAVConnectException e) {
            IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
		}
    }

    @Override
    protected void ontologyComboChanged() {
        setPreselectedFileInput();
    }

    /**
     * fills the folderpath and filename depending on the selected folder and
     * file
     *  
     */
    protected void setFileInput() {
        String server = ""; //$NON-NLS-1$
        URL serverURL = null;
        if (_webDAVConnection != null) {
            serverURL = _webDAVConnection.getServerURL();
        }
        if (_webDAVFolderSelection != null && serverURL != null) {
            server = serverURL.toExternalForm().substring(0, serverURL.toExternalForm().length() - serverURL.getPath().length());
            server += ((Resource) _webDAVFolderSelection).location().toString();
        }
        _folderInputText.setText(server);
        _folderInputText.setToolTipText(server);
        if(!_folderInputText.getText().equals("")) { //$NON-NLS-1$
        	_fileInput.setEditable(true);
        }
        String fileName = ""; //$NON-NLS-1$
        if (_webDAVFileSelection != null) {
            if (_webDAVFileSelection instanceof Resource) {
                Resource resource = (Resource) _webDAVFileSelection;
                fileName = resource.location().lastSegment();
                try {
                    _fileInput.setText(URLDecoder.decode(fileName, "UTF-8")); //$NON-NLS-1$
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public URI getSelectedURI() {
        try {
            String folderName = _folderInputText.getText();
            if (_webDAVConnection.getServerURL().toExternalForm().equals(folderName)) {
                return null;
            }
            if (folderName.equals("")) { //$NON-NLS-1$
                return null;
            }
            if (!folderName.endsWith("/")) { //$NON-NLS-1$
                folderName += "/"; //$NON-NLS-1$
            }
            if (_webDAVConnection == null) {
                return null;
            }
            String fileName = _fileInput.getText();
            ISelection selection = _fileTypeComboViewer.getSelection();
            String fileType = getSelectedFileType(selection);
            if (fileName.equals("")) { //$NON-NLS-1$
                return null;
            }
            if (fileType != null) {
                if (!fileName.endsWith(fileType)) {
                    fileName += fileType;
                }
            }
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            String fileInputString = folderName + fileName;

            FileFilter usedFileFilter = null;
            for (int i = 0; i < _fileFilters.length; i++) {
				FileFilter fileFilter = _fileFilters[i];
				String[] ext = fileFilter.getExtensions();
				for (int j = 0; j < ext.length; j++) {
                    if (fileName.endsWith(ext[j])) {
                        usedFileFilter = fileFilter;
                        break;
                    }								
				}
				if(usedFileFilter != null) {
					break;
				}
            }             
            if(usedFileFilter == null) {
            	usedFileFilter = _fileFilters[0];
            	fileInputString += usedFileFilter.getDefaultExtension();
            }
            _fileFilter = usedFileFilter;
            
            URI uri = new URI(fileInputString);
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getSelectedFileType(ISelection selection) {
        String fileType = null;
        if (selection instanceof StructuredSelection) {
            FileFilter selectedFilter = (FileFilter) ((StructuredSelection)selection).getFirstElement();
            if (selectedFilter != null) {
                fileType = selectedFilter.getDefaultExtension();
            }
        }
        return fileType;
    }
    
    @Override
    public URL getSelectedURL() {
        try {
            String folderName = _folderInputText.getText();
            if (_webDAVConnection.getServerURL().toExternalForm().equals(folderName)) {
                return null;
            }
            if (folderName.equals("")) { //$NON-NLS-1$
                return null;
            }
            if (!folderName.endsWith("/")) { //$NON-NLS-1$
                folderName += "/"; //$NON-NLS-1$
            }
            if (_webDAVConnection == null) {
                return null;
            }
            String fileName = _fileInput.getText();
            if (fileName.equals("")) { //$NON-NLS-1$
                return null;
            }
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            String fileInputString = folderName + fileName;

//            FileFilter usedFileFilter = null;
//            for (int i = 0; i < _fileFilters.length; i++) {
//				FileFilter fileFilter = _fileFilters[i];
//				String[] ext = fileFilter.getExtensions();
//				for (int j = 0; j < ext.length; j++) {
//                    if (fileName.endsWith(ext[j])) {
//                        usedFileFilter = fileFilter;
//                        break;
//                    }								
//				}
//				if(usedFileFilter != null) {
//					break;
//				}
//            }             
//            if(usedFileFilter == null) {
//            	usedFileFilter = _fileFilters[0];
//            	fileInputString += usedFileFilter.getDefaultExtension();
//            }
//            _fileFilter = usedFileFilter;
            
            URL url = new URL(fileInputString);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.gui.ExportSelectionPage#checkStatus()
     */
    @Override
    public void checkStatus() {
        setErrorMessage(null);
        super.checkStatus();
        if (isPageComplete()) {
            if (getSelectedURL() == null) {
                updateStatus(Messages.getString("WebDAVExportSelectionPage.15")); //$NON-NLS-1$
                setPageComplete(false);
            }
            if (_webDAVConnection != null && _webDAVConnection.getServerURL().toExternalForm().equals(_folderInputText.getText())) {
                updateStatus(Messages.getString("WebDAVExportSelectionPage.16")); //$NON-NLS-1$
                setPageComplete(false);
            }
        }
        if (_webDAVSiteCombo.getText().equals("")) { //$NON-NLS-1$
            updateStatus(null);
            setDescription(Messages.getString("WebDAVExportSelectionPage.30")); //$NON-NLS-1$
            setPageComplete(false);
        }
        if (_webDAVConnection != null && !_webDAVConnection.isConnected()) {
            if (_webDAVConnection.getConnectionException() != null) {
                setErrorMessage(Messages.getString("WebDAVExportSelectionPage.12") + _webDAVConnection.getServerURL().toExternalForm() //$NON-NLS-1$
                        + ": " //$NON-NLS-1$
                        + _webDAVConnection.getConnectionException().getMessage()
                        + "\n" //$NON-NLS-1$
                        + Messages.getString("WebDAVExportSelectionPage.29")); //$NON-NLS-1$
            } else {
                setErrorMessage(Messages.getString("WebDAVExportSelectionPage.14") + _webDAVConnection.getServerURL().toExternalForm() //$NON-NLS-1$
                        + "\n" //$NON-NLS-1$
                        + Messages.getString("WebDAVExportSelectionPage.29")); //$NON-NLS-1$
            }
        }
    }

    @Override
    protected FileFilter detectFileFilter(String url) {
        return _fileFilter;
    }
    
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
        _webDAVConnection = null;
        _fileInput.setText(""); //$NON-NLS-1$
        if (sites.length > 0) {
            _webDAVSiteCombo.select(0);
            siteComboChanged(_webDAVSiteCombo.getText());
        }
    }

    private void siteComboChanged(String text) {
        _webDAVConnection = null;
        Object[] sites = _webDAVSiteManager.getSites().values().toArray();
        for (int i = 0; i < sites.length; i++) {
            Object site = sites[i];
            if (site instanceof WebDAVConnection) {
                URL url = ((WebDAVConnection) site).getServerURL();
                if (url != null) {
                    if (url.toExternalForm().equals(text)) {
                        _webDAVConnection = (WebDAVConnection) site;
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
                        URL fileUrl = getWebDAVUrlToSelect();
                        selectWebDAVFolder(fileUrl);
                        selectWebDAVFile(fileUrl);
                    }
                }
            }

        }
    }

    /**
     * updates settings for the webdav-folder create action depending on the
     * selected webdav-connection/-folder
     */
    private void updateCreateWebDAVFolderAction() {
        _createWebDAVFolderAction.setEnabled(false);
        if (_webDAVFolderSelection instanceof WebDAVConnection) {
            _createWebDAVFolderAction.setEnabled(true);
            _createWebDAVFolderAction.setSelectedSite((WebDAVConnection) _webDAVFolderSelection);
            _createWebDAVFolderAction.setSelectedFolder(null);
        } else if (_webDAVFolderSelection instanceof Folder) {
            _createWebDAVFolderAction.setSelectedSite(_webDAVConnection);
            _createWebDAVFolderAction.setSelectedFolder((Folder) _webDAVFolderSelection);
            _createWebDAVFolderAction.setEnabled(true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.io.webdav.IWebDAVSelectionPage#refresh()
     */
    public void refresh() {
        refreshSelected();
    }

    private void refreshSelected() {
        Object selection = ((IStructuredSelection) _webDAVFolderTreeViewer.getSelection()).getFirstElement();
        this._webDAVFolderTreeViewer.expandToLevel(selection, 1);
        this._webDAVFolderTreeViewer.refresh(selection);
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
        updateCreateWebDAVFolderAction();
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
        updateCreateWebDAVFolderAction();
    }

    /**
     * @return selected WebDAVConnection
     */
    public WebDAVConnection getSelectedWebDAVConnection() {
        return _webDAVConnection;
    }

    /**
     * @return selected webdavFile (Resource)
     */
    public Resource getSelectedWebDAVFile() {
        if ((_webDAVFileSelection != null) && (_webDAVFileSelection instanceof Resource)) {
            return (Resource) _webDAVFileSelection;
        }
        return null;
    }

}
