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
import java.net.URL;

import org.eclipse.jface.wizard.IWizardPage;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.io.IOPlugin;
import org.neontoolkit.io.Messages;
import org.neontoolkit.io.filter.FileFilter;

/* 
 * Created on 11.03.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Wizard, WizardPage, Import, Ontology
 */

/**
 * Abstract WizardPage to specify from where to load an ontology.
 * Selection of projectname and file
 */
public abstract class AbstractImportSelectionPage extends ImportWizardPage {

    protected Text _fileInput;
    protected Button _browseButton;
    protected FileFilter _fileFilter;
    protected IWizardPage _prevPage;
    protected Composite _composite;
    protected String _preselectedProject;
    protected String _preselectedFile;
    protected String _selectedFileFormat;
    protected Button _importAsRDFButton;

    class EventHandler implements SelectionListener, ModifyListener {
        @Override
        public void widgetSelected(SelectionEvent se) {
            checkStatus();
        }
        @Override
        public void widgetDefaultSelected(SelectionEvent se) {
            widgetSelected(se);
        }
        @Override
        public void modifyText(ModifyEvent me) {
            checkStatus();
        }
    }

    public AbstractImportSelectionPage(FileFilter filter) {
        super("wizardPage"); //$NON-NLS-1$
        setTitle(Messages.getString("AbstractImportSelectionPage.1")); //$NON-NLS-1$
        setDescription(Messages.getString("AbstractImportSelectionPage.2")); //$NON-NLS-1$
        setFileFilter(filter);
        setPageComplete(false);
    }

    public boolean isFileSelected() {
        return _fileInput.getText().length() > 0;
    }
    
    @Override
    public void createControl(Composite parent) {
        _composite = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout(3, false);
        _composite.setLayout(gridLayout);
        GridData gridData;
        EventHandler listener = new EventHandler();

        Label fileLabel = new Label(_composite, SWT.NONE);
        fileLabel.setText(Messages.getString("AbstractImportSelectionPage.3")); //$NON-NLS-1$
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = false;
        fileLabel.setLayoutData(gridData);

        _fileInput = new Text(_composite, SWT.BORDER);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        _fileInput.addModifyListener(listener);
        _fileInput.setLayoutData(gridData);

        _browseButton = new Button(_composite, SWT.PUSH);
        _browseButton.setText(Messages.getString("AbstractImportSelectionPage.4")); //$NON-NLS-1$
        _browseButton.addSelectionListener(listener);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = GridData.FILL;
        _browseButton.setLayoutData(gridData);

        Label containerLabel = new Label(_composite, SWT.NONE);
        containerLabel.setText(Messages.getString("AbstractImportSelectionPage.5")); //$NON-NLS-1$
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        containerLabel.setLayoutData(gridData);
        
        Composite projectComposite = new Composite(_composite, SWT.NONE);
        GridData gridData1 = new GridData();
        gridData1.horizontalSpan = 2;
        projectComposite.setLayoutData(gridData1);
        createProjectControl(projectComposite);


//        _projectCombo = new Combo(_composite, SWT.READ_ONLY);
//        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
//        _projectCombo.setLayoutData(gridData);
//        _projectCombo.addSelectionListener(listener);
        
        initValues();
        setControl(_composite);
        checkStatus();
    }

    public URL getSelectedURL() {
        try {
            return new URL(_fileInput.getText());
        } catch (MalformedURLException e) {
            try {
                return new File(_fileInput.getText()).toURI().toURL();
            } catch (MalformedURLException e1) {
                IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
                return null;
            }
        }
    }
    
    public String[] getFiles() {
        String files = _fileInput.getText();
        return files.split(";"); //$NON-NLS-1$
    }

    public final URL[] getSelectedURLS() {
        String[] filesArray = getFiles(); 
        URL[] urls = new URL[filesArray.length];
        for (int i = 0; i < filesArray.length; i++) {
            URL url = null;
            try {
                url = new URL(filesArray[i]);
            } catch (MalformedURLException e) {
                try {
                    url = new File(filesArray[i]).toURI().toURL();
                } catch (MalformedURLException e1) {
                    IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
                    return null;
                }
            }
            urls[i] = url;
        }
        return urls;
    }

//    public String getSelectedProject() {
//    	if(_projectCombo.getSelectionIndex() == -1) {
//    		return null;
//    	}
//    	String projectSelection = _projectCombo.getItem(_projectCombo.getSelectionIndex());
//    	int lastIndex = projectSelection.lastIndexOf(" ["); //$NON-NLS-1$
//        return projectSelection.substring(0, lastIndex);
//    }

    @Override
    public void initValues() {
        super.initValues();
        if (_preselectedFile != null) {
            setFileInput(_preselectedFile);
        }
        checkStatus();// josp 2009-05-12: Issue 12215 - ImportDialog cannot be finished although correct values applied
    }

    
//    protected void initCombo() {
//        try {
//    		if(_supportedProjectLanguages == null) {
//    			_supportedProjectLanguages = new ArrayList<String>();
//    	    	OntologyLanguage[] allLangs = OntologyLanguage.values();
//    	    	for (OntologyLanguage language : allLangs) {
//    	    		_supportedProjectLanguages.add(language.toString());
//    			}
//    		}
//            String[] allProjects = DatamodelPlugin.getDefault().getOntologyProjects();
//        	List<String> possibleProjects = new ArrayList<String>();
//        	for (int i = 0; i < allProjects.length; i++) {
//				String project = allProjects[i];
//				String projectOntoLang = DatamodelPlugin.getDefault().getConnectionProperties(project).get(IConfig.ONTOLOGY_LANGUAGE).toString();
//				if(_supportedProjectLanguages.contains(projectOntoLang)) {
//					possibleProjects.add(project + " [" + projectOntoLang + "]"); //$NON-NLS-1$ //$NON-NLS-2$
//				}
//			}
//            _projectCombo.setItems(possibleProjects.toArray(new String[0]));
//            if (_projectCombo.getItemCount() > 0) {
//            	int projectIndex = -1;
//            	if(_preselectedProject != null) {
//            		String ontoLang = DatamodelPlugin.getDefault().getConnectionProperties(_preselectedProject).get(IConfig.ONTOLOGY_LANGUAGE).toString();
//            		projectIndex = _projectCombo.indexOf(_preselectedProject + " [" + ontoLang + "]"); //$NON-NLS-1$ //$NON-NLS-2$
//                    _projectCombo.select(projectIndex);
//            	}
//            }
//        } catch (Exception e) {
//        	e.printStackTrace();
//            NeOnUIPlugin.getDefault().logError("", e); //$NON-NLS-1$
//        }
//    }
//
//    protected void createNewProject() {
//    	//TODO: move newOntologyProjectWizard to org.neontoolkit.ui
//        NewOntologyProjectWizard wizard = new NewOntologyProjectWizard();
//        wizard.init(PlatformUI.getWorkbench(), null);
//		Shell parent = getShell();
//		WizardDialog dialog = new WizardDialog(parent, wizard);
//		dialog.create();
//		dialog.open();
//		initControls();
//    }
//    
//    protected void updateStatus(String message) {
//        setMessage(message, IMessageProvider.INFORMATION);
//    }
//
//    public void setSelectedProject(String projectName) {
//        _preselectedProject = projectName;
//    }

    public void setSelectedFile(String file) {
        _preselectedFile = file;
    }

    public void setFileInput(String file) {
        //
    }

    public void checkStatus() {
        String status = null;
        boolean pageComplete = true;
        //check for possible errors
        String filename = _fileInput.getText();
        if (filename == null || filename.length() == 0) {
            status = Messages.getString("AbstractImportSelectionPage.6"); //$NON-NLS-1$
            pageComplete = false;
        }
        if (_fileInput.getText().length() == 0) {
            status = Messages.getString("AbstractImportSelectionPage.7"); //$NON-NLS-1$
            pageComplete = false;
        }
        if (getSelectedProject().length() == 0) {
            status = Messages.getString("AbstractImportSelectionPage.8"); //$NON-NLS-1$
            pageComplete = false;
        }
//        if(pageComplete) {
//        	ImportExportControl iec = new ImportExportControl();
//        	String projectName = getSelectedProject();
//        	//check if transformation is needed    
//        	ArrayList<String> filesToBeTransformed = new ArrayList<String>();
//        	ArrayList<String> filesNotSupported = new ArrayList<String>();
//        	String[] files = getFiles();
//        	for (int i = 0; i < files.length; i++) {
//				String file = files[i];
//            	URI physicalURI = new File(file).toURI();
//            	try {
//    				String ontologyFileFormat = iec.getOntologyFileFormat(projectName, physicalURI);
//    				if(iec.isTransformationRequired(projectName, ontologyFileFormat)) {
////    					if(TransformationControl.isFileTransformationSupported(ontologyFileFormat, projectName)) {
//    						filesToBeTransformed.add(file);
////    					} else {
////    						filesNotSupported.add(file);
////    					}
//    				}						
//				} catch (ControlException e2) {
//					filesNotSupported.add(file);
//				}
//			}
//        	String messages = "";
//        	if(filesNotSupported.size() > 0) {        		
//        		messages = Messages.getString("AbstractImportSelectionPage.11") + " ";
//        		for (int i = 0; i < filesNotSupported.size(); i++) {
//					String file = filesNotSupported.get(i);
//					if(i != 0) {
//						messages += ",";
//					}
//        			messages += " '" + file + "'";
//				}
//        		messages += " " + Messages.getString("AbstractImportSelectionPage.12");
//        		error = messages;
//				pageComplete = false;        		
//        	} else if(filesToBeTransformed.size() > 0) {      
//        		String projectLang = DatamodelPlugin.getDefault().getConnectionProperties(projectName).get(IConfig.ONTOLOGY_LANGUAGE).toString();
//        		messages += Messages.getString("AbstractImportSelectionPage.9") + " " + projectLang + " " + Messages.getString("AbstractImportSelectionPage.10");
//        		for (int i = 0; i < filesToBeTransformed.size(); i++) {
//					String file = filesToBeTransformed.get(i);
//					if(i != 0) {
//						messages += ",";
//					}
//        			messages += " '" + file + "'";
//				}
//        		warning = messages;
//        	}
//        }
//        if(error != null) {
//            setMessage(error, IMessageProvider.ERROR);        	
//        } else if(warning != null) {
//            setMessage(warning, IMessageProvider.WARNING);        	
//        } else {
        	updateStatus(status);
//        }
        setPageComplete(pageComplete);
    }

    public FileFilter getFileFilter() {
        return _fileFilter;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this._fileFilter = fileFilter;
    }
    
    public String getSelectedFileFormat() {
        return _selectedFileFormat;
    }
    
    @Override
    public void performHelp() {
        String helpContextId = org.neontoolkit.gui.IHelpContextIds.OWL_IMPORT_ONTOLOGY;
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(helpContextId);
    }
}
