/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io.wizard;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizard;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.io.IOPlugin;
import org.neontoolkit.io.Messages;
import org.neontoolkit.io.filter.FileFilter;
import org.neontoolkit.io.util.ImportExportUtils;


/* 
 * Created on 11.3.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Wizard, WizardPage, Export, Ontology
 */

/**
 * Abstract WizardPage for ExportWizard to specify where to save an ontology
 * Selection of projectname, module and file
 */
public abstract class AbstractExportSelectionPage extends WizardPage {

    protected Text _fileInput;
    protected Button _browseButton;
    protected Combo _projectCombo;
    protected Combo _ontoCombo;
    protected FileFilter _fileFilter;
    protected FileFilter[] _fileFilters;
//    protected FileFilter[] _availableFilters = new FileFilter[]{};//{new OxmlFileFilter(), new FLogicFileFilter(), new RdfFileFilter(), new RdfsFileFilter(), new NTFileFilter(), new N3FileFilter(), new TurtleFileFilter(), new OwlFileFilter(), new OwlXFileFilter(), new Owl11FileFilter()};
    protected IWizardPage _prevPage;
    protected IWizard _wizard;
    protected Composite _composite;
    private String _preselectedOntologyUri;
    private String _preselectedProject;
    protected String _preselectedFile;
    private boolean _checkForFileFilter = true;
    private List<String> _supportedProjectLanguages;    

    public class EventHandler implements SelectionListener, ModifyListener {

        public void widgetSelected(SelectionEvent se) {
            if (se.getSource().equals(_browseButton)) {
                // check whether extension is specified
                // if not -> directory import
                if (_fileFilters != null) {
                    FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
                    // for compatibility with existing import plugins
                    String[] extension = new String[_fileFilters.length];
                    String[] description = new String[_fileFilters.length];
                    for (int i = 0; i < _fileFilters.length; i++) {
						FileFilter fileFilter = _fileFilters[i];
						extension[i] = fileFilter.getExtension();
						description[i] = fileFilter.getDescription();
					}
                    if (_fileInput.getText().length() > 0) {
                        fileDialog.setFileName(_fileInput.getText());
                    }
                    fileDialog.setFilterExtensions(extension);
                    fileDialog.setFilterNames(description);
                    fileDialog.open();
                    String fileName = fileDialog.getFileName();
                    FileFilter usedFileFilter = detectFileFilter(fileName);
                    if(!fileName.endsWith(usedFileFilter.getDefaultExtension())) {
                        fileName += usedFileFilter.getDefaultExtension();
                    }
                    _fileFilter = usedFileFilter;
                    _fileInput.setText(fileDialog.getFilterPath() + System.getProperty("file.separator") + fileName); //$NON-NLS-1$
                } else {
                    DirectoryDialog dirDialog = new DirectoryDialog(getShell());
                    dirDialog.setText(Messages.getString("AbstractExportSelectionPage.3")); //$NON-NLS-1$
                    dirDialog.setMessage(Messages.getString("AbstractExportSelectionPage.4")); //$NON-NLS-1$
                    if (_fileInput.getText().length() > 0) {
                        dirDialog.setFilterPath(_fileInput.getText());
                    } else {
                        dirDialog.setFilterPath("C:" + System.getProperty("file.separator") + System.getProperty("file.separator")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                    String dir = dirDialog.open();
                    if (dir != null) {
                        _fileInput.setText(dir);
                    }
                }
            } else if (se.getSource().equals(_projectCombo)) {
                //The selection of the project Combo has changed
                projectComboChanged();
            } else if (se.getSource().equals(_ontoCombo)) {
                ontologyComboChanged();
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

    public AbstractExportSelectionPage(FileFilter[] filters) {
        super("wizardPage"); //$NON-NLS-1$
        setTitle(Messages.getString("AbstractExportSelectionPage.7")); //$NON-NLS-1$
        setDescription(Messages.getString("AbstractExportSelectionPage.8")); //$NON-NLS-1$
        _fileFilters = filters;
        setPageComplete(false);
    }
    
    public void setSupportedProjectOntologyLanguages(String[] supportedLanguages) {
        _supportedProjectLanguages = Arrays.asList(supportedLanguages);
    }

    public String[] getSupportedProjectOntologyLanguages() {
        return _supportedProjectLanguages.toArray(new String[0]);
    }


    public boolean isFileSelected() {
        if (_fileInput.getText().length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        _composite = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout(3, false);
        _composite.setLayout(gridLayout);
        GridData gridData;
        EventHandler listener = new EventHandler();

        Label projectLabel = new Label(_composite, SWT.NONE);
        projectLabel.setText(Messages.getString("AbstractExportSelectionPage.9")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        projectLabel.setLayoutData(gridData);

        _projectCombo = new Combo(_composite, SWT.READ_ONLY);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        _projectCombo.setLayoutData(gridData);
        _projectCombo.addSelectionListener(listener);

        Label dummyLabel = new Label(_composite, SWT.NONE);
        dummyLabel.setVisible(false);
        
        Label ontoLabel = new Label(_composite, SWT.NONE);
        ontoLabel.setText(Messages.getString("AbstractExportSelectionPage.10")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        ontoLabel.setLayoutData(gridData);

        _ontoCombo = new Combo(_composite, SWT.READ_ONLY);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        _ontoCombo.setLayoutData(gridData);
        _ontoCombo.addSelectionListener(listener);

        dummyLabel = new Label(_composite, SWT.NONE);

        Label fileLabel = new Label(_composite, SWT.NONE);
        fileLabel.setText(Messages.getString("AbstractExportSelectionPage.11")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        fileLabel.setLayoutData(gridData);

        _fileInput = new Text(_composite, SWT.BORDER);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        _fileInput.addModifyListener(listener);
        _fileInput.setLayoutData(gridData);

        _browseButton = new Button(_composite, SWT.PUSH);
        _browseButton.setText(Messages.getString("AbstractExportSelectionPage.12")); //$NON-NLS-1$
        _browseButton.addSelectionListener(listener);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = GridData.FILL;
        _browseButton.setLayoutData(gridData);

        initControls();
        setControl(_composite);
    }

    public URL getSelectedURL() {
        String url = validateUrl(_fileInput.getText()); 
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            try {
                return new File(url).toURL();
            } catch (MalformedURLException e1) {
                IOPlugin.getDefault().logError("", e1); //$NON-NLS-1$
                return null;
            }
        }
    }

    public URI getSelectedURI() {
        String uri = validateUri(_fileInput.getText()); 
        try {
            URI newUri = new URI(uri);
            if(newUri.getScheme() == null) {
            	newUri = new File(uri).toURI();
            }
            return newUri;
        } catch (URISyntaxException e) {
        }
        return new File(uri).toURI();
    }

    
//    protected FileFilter detectFileFilter(String url) {
//        FileFilter usedFileFilter = null;
//        if (url.length() > 0) {
//            for (int i = 0; i < _availableFilters.length; i++) {
//                FileFilter fileFilter = _availableFilters[i];
//                if (url.endsWith(fileFilter.getDefaultExtension())) {
//                    usedFileFilter = fileFilter;
//                    break;
//                }
//            }             
//        }
//        return usedFileFilter;
//    }

    protected FileFilter detectFileFilter(String url) {
        FileFilter usedFileFilter = null;
        if (url.length() > 0) {
            for (int i = 0; i < _fileFilters.length; i++) {
                FileFilter fileFilter = _fileFilters[i];
                if (url.endsWith(fileFilter.getDefaultExtension())) {
                    usedFileFilter = fileFilter;
                    break;
                }
            }             
        }
        return usedFileFilter;
    }

    String validateUrl(String url) {
        if(_fileFilter == null) {
            _fileFilter = detectFileFilter(url);
        }
        String[] extensions = _fileFilter.getExtensions();
        boolean append = true;
        for (int i = 0; i < extensions.length; i++) {
            if (url.toString().endsWith(extensions[i])) {
                append = false;
            }
        }
        if (append) {
            return url.toString() + _fileFilter.getDefaultExtension();
        }
        return url;
    }

    String validateUri(String uri) {
        if(_fileFilter == null) {
            _fileFilter = detectFileFilter(uri);
        }
        String[] extensions = _fileFilter.getExtensions();
        boolean append = true;
        for (int i = 0; i < extensions.length; i++) {
            if (uri.toString().endsWith(extensions[i])) {
                append = false;
            }
        }
        if (append) {
            return uri.toString() + _fileFilter.getDefaultExtension();
        }
        return uri;
    }

    public String getSelectedOntologyUri() {
        if (_ontoCombo.getSelectionIndex() >= 0) {
            return _ontoCombo.getItem(_ontoCombo.getSelectionIndex());
        }
        return null;
    }

    public String getSelectedProject() {
    	if(_projectCombo.getSelectionIndex() == -1) {
    		return null;
    	}
    	String projectSelection = _projectCombo.getItem(_projectCombo.getSelectionIndex());
    	int lastIndex = projectSelection.lastIndexOf(" ["); //$NON-NLS-1$
        return projectSelection.substring(0, lastIndex);
    }

    protected void initControls() {
        String[] projects = OntologyProjectManager.getDefault().getOntologyProjects();
        initProjectCombo(projects);
        reorderFileFilters();
        if (_preselectedOntologyUri != null) {
            _ontoCombo.select(_ontoCombo.indexOf(_preselectedOntologyUri));
        }
        //		if(preselectedFile != null)
        //			setFileInput(preselectedFile);
        checkStatus();
    }

    protected void updateStatus(String message) {
        setMessage(message, IMessageProvider.INFORMATION);
    }

    public void checkStatus() {
        String status = null;
        boolean pageComplete = true;
        //check for possible errors

        String filename = _fileInput.getText();
        if(_checkForFileFilter) {
        	_fileFilter = detectFileFilter(filename);
        }
        if (_projectCombo.getSelectionIndex() == -1) {
            status = Messages.getString("AbstractExportSelectionPage.15"); //$NON-NLS-1$
            pageComplete = false;
        } else if (_ontoCombo.getSelectionIndex() == -1) {
            status = Messages.getString("AbstractExportSelectionPage.14"); //$NON-NLS-1$
            pageComplete = false;
        } else if (filename == null || filename.length() == 0) {
            status = Messages.getString("AbstractExportSelectionPage.13"); //$NON-NLS-1$
            pageComplete = false;
        } else if (_checkForFileFilter && _fileFilter == null) {
            status = Messages.getString("AbstractExportSelectionPage.16"); //$NON-NLS-1$
            pageComplete = false;
        }
        if(status == null && getMessage() != null || status != null && getMessage() == null || ((status != null && getMessage() != null) && (!status.equals(getMessage())))) {
            updateStatus(status);
        }
        if(isPageComplete() != pageComplete) {
            setPageComplete(pageComplete);
        }
//        if(isPageComplete() && pageComplete) {
//            if(_checkForFileFilter && _fileFilter != null) {            	
//        		String warning = ""; //$NON-NLS-1$
//            	try {
//                	if(ImportExportUtils.isTransformationRequired(getSelectedProject(), _fileFilter.getOntologyFileFormat())) {
//                		List<String> warningList =  TransformationUtils.getFileExportTransformationInfos(getSelectedProject(), _fileFilter.getOntologyFileFormat());
//                		for (String warningString : warningList) {
//    						warning += (warningString + " "); //$NON-NLS-1$
//    					}
//                	}
//				} catch (Exception e) {
//		        	e.printStackTrace();
//		            NeOnUIPlugin.getDefault().logError("", e); //$NON-NLS-1$
//				}
//				if(warning.equals("")) { //$NON-NLS-1$
//					warning = null;
//				}
//            	setMessage(warning, IMessageProvider.WARNING);                		
//            }             	
//        }
    }
    
    public void setSelectedProject(String project) {
        _preselectedProject = project;
    }

    public void setSelectedOntologyUri(String ontologyUri) {
        _preselectedOntologyUri = ontologyUri;
    }

    public void setSelectedFile(String file) {
        _preselectedFile = file;
    }

    protected void initProjectCombo(String[] allProjects) {
        try {
            if(_supportedProjectLanguages == null) {
                _supportedProjectLanguages = new ArrayList<String>();
            }

        	List<String> possibleProjects = new ArrayList<String>();
        	for (int i = 0; i < allProjects.length; i++) {
                IOntologyProject project = NeOnCorePlugin.getDefault().getOntologyProject(allProjects[i]);
                String projectOntoLang = getProjectOntologyLanguage(project);
                if((_supportedProjectLanguages.size() == 0 || _supportedProjectLanguages.contains(projectOntoLang)) && project.getProjectFailure() == null) {
                    possibleProjects.add(project.toString());
                }
			}
        	String[] projects = possibleProjects.toArray(new String[0]);
            _projectCombo.setItems(projects);
            if (projects.length > 0) {
            	int projectIndex = -1;
            	if(_preselectedProject != null) {
            		projectIndex = _projectCombo.indexOf(_preselectedProject.toString());
            	}
            	if(projectIndex == -1) {
            		projectIndex = 0;
            	}
                _projectCombo.select(projectIndex);
                initOntologyCombo();                
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
            IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
        }

    }

    /**
     * @param project
     * @return
     */
    protected String getProjectOntologyLanguage(IOntologyProject project) {
        String projectOntoLang = project.getOntologyLanguage();
        return projectOntoLang;
    }

    public void initOntologyCombo() {
        _ontoCombo.removeAll();
        try {
            String projectName = getSelectedProject();
            IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
            _ontoCombo.setItems(ontologyProject.getOntologies());
        } catch (NeOnCoreException e) {
            IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
        }
    }

    protected void ontologyComboChanged() {
    }

    protected void projectComboChanged() {
        //update the items in the onto Combo
        reorderFileFilters();
        initOntologyCombo();
    }

    public FileFilter getFileFilter() {
        return _fileFilter;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this._fileFilter = fileFilter;
    }

    public void setFileFilters(FileFilter[] fileFilters) {
        this._fileFilters = fileFilters;
    }
    
    public void setCheckForFileFilter(boolean check) {
    	_checkForFileFilter = check;
    }
    
    public void reorderFileFilters() {
        if(getSelectedProject() == null || _fileFilters == null) {
            return;
        }
        String ontologyLanguage = ""; //$NON-NLS-1$
        try {
            ontologyLanguage = ImportExportUtils.getProjectOntologyLanguage(getSelectedProject()).toString();
        } catch (NeOnCoreException e) {
            e.printStackTrace();
        }
        List<FileFilter> fileFilterList = new ArrayList<FileFilter>();
        FileFilter[] fileFilters = _fileFilters;
        int counter = 0;
        for (int i = 0; i < fileFilters.length; i++) {
            FileFilter fileFilter = fileFilters[i];
            String fileFilterOntologyLanguage = fileFilter.getOntologyLanguage(); 
            if(fileFilterOntologyLanguage != null && fileFilterOntologyLanguage.equals(ontologyLanguage)) {
                fileFilterList.add(counter, fileFilter);
                counter++;
            } else {
                fileFilterList.add(fileFilter);
            }            
        }
        _fileFilters = fileFilterList.toArray(new FileFilter[0]);                
    }

    public String getFileWithoutKnownFileExtension() {
        String fileName = _fileInput.getText();
        if(fileName.indexOf(".") > 0) { //$NON-NLS-1$
            FileFilter[] filters = _fileFilters;
            for (FileFilter fileFilter: filters) {
                String[] fileFilterExtensions = fileFilter.getExtensions();
                for (String fileFilterExtension: fileFilterExtensions) {
                    if(fileName.endsWith(fileFilterExtension)) {
                        return fileName.substring(0, fileName.lastIndexOf(fileFilterExtension));
                    }
                }
            }
        }
        return fileName;
    }
    
}
