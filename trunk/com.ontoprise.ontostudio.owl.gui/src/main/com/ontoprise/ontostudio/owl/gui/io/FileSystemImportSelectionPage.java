/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.io;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizard;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.io.filter.FileFilter;
import org.neontoolkit.io.util.ImportExportUtils;
import org.neontoolkit.io.wizard.AbstractImportSelectionPage;
import org.neontoolkit.io.wizard.AbstractImportWizard;

import com.ontoprise.ontostudio.owl.gui.Messages;

/* 
 * Created on 7.04.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Wizard, WizardPage, Import, FileSystem, Ontology
 */

/**
 * Page for FileSystemImportWizard to specify from where to load an ontology.
 * Selection of projectname and file
 */
public class FileSystemImportSelectionPage extends AbstractImportSelectionPage {

    protected Button _browseButton;
    protected IWizardPage _prevPage;
    protected IWizard _wizard;
    protected Composite _composite;

    class EventHandler implements SelectionListener, ModifyListener {

        public void widgetSelected(SelectionEvent se) {
            if (se.getSource().equals(_browseButton)) {
                browse();
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

    public FileSystemImportSelectionPage(FileFilter filter) {
        super(filter);
        setTitle(Messages.FileSystemImportSelectionPage_1);
        setDescription(Messages.FileSystemImportSelectionPage_2); 
    }

    @Override
    public boolean isFileSelected() {
        if (_fileInput.getText().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl(Composite parent) {
        _composite = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout(3, false);
        _composite.setLayout(gridLayout);
        GridData gridData;
        EventHandler listener = new EventHandler();

        Label containerLabel = new Label(_composite, SWT.NONE);
        containerLabel.setText(Messages.FileSystemImportSelectionPage_5);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        containerLabel.setLayoutData(gridData);
        
        Composite projectComposite = new Composite(_composite, SWT.NONE);
        GridData gridData1 = new GridData();
        gridData1.horizontalSpan = 2;
        projectComposite.setLayoutData(gridData1);
        createProjectControl(projectComposite, 300);

        Label fileLabel = new Label(_composite, SWT.NONE);
        fileLabel.setText(Messages.FileSystemImportSelectionPage_3);
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
        _browseButton.setText(Messages.FileSystemImportSelectionPage_4);
        _browseButton.addSelectionListener(listener);
        
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = GridData.FILL;
        _browseButton.setLayoutData(gridData);

        Label dummyLabel = new Label(_composite, SWT.NONE);
        gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = GridData.FILL;
        dummyLabel.setLayoutData(gridData);
        dummyLabel.setVisible(false);
        
//        _importAsRDFButton = new Button(_composite, SWT.CHECK);
//        _importAsRDFButton.setText(Messages.FileSystemImportSelectionPage_0);
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
//        gridData.horizontalSpan = 3;
//        gridData.grabExcessHorizontalSpace = false;
//        gridData.horizontalAlignment = GridData.FILL;
//        _importAsRDFButton.setLayoutData(gridData);
        
        initValues();
        setControl(_composite);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent ,IHelpContextIds.FILE_SYSTEM_IMPORT);    
    }

    protected String browse() {
        // check whether extension is specified
        // if not -> directory import
        if (_fileFilter != null && _fileFilter.getExtension().length() > 0) {
            FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN | SWT.MULTI);
            // for compatibility with existing import plugins
            String ext = _fileFilter.getExtension();
            if (ext.indexOf("*") < 0) { //$NON-NLS-1$
                ext = "*" + ext; //$NON-NLS-1$
            }
            
            String[] extension = new String[]{ext};
            if (((AbstractImportWizard) getWizard()).isImportDialog()) {
                extension = new String[]{ext, "*.*"}; //$NON-NLS-1$
            }
            String[] fileFilters = ext.split(";"); //$NON-NLS-1$
            if(fileFilters.length > 1) {            	
            	extension = new String[fileFilters.length + 2];
            	extension[0] = ext;
            	for (int i = 0; i < fileFilters.length; i++) {
					extension[i + 1] = fileFilters[i];
				}
                if (((AbstractImportWizard) getWizard()).isImportDialog()) {
                	extension[fileFilters.length + 1] = "*.*"; //$NON-NLS-1$
                }
            }            
            
            String[] description = new String[]{};// {_fileFilter.getDescription(), "*.*"};
            description = extension;
            if (_fileInput.getText().length() > 0) {
                fileDialog.setFileName(_fileInput.getText());
            }
            fileDialog.setFilterExtensions(extension);
            fileDialog.setFilterNames(description);
            fileDialog.open();
            String fileNames = ""; //$NON-NLS-1$
            for (int i = 0; i < fileDialog.getFileNames().length; i++) {
                String fileName = fileDialog.getFilterPath() + System.getProperty("file.separator") + fileDialog.getFileNames()[i]; //$NON-NLS-1$
                if (i != 0) {
                    fileNames += ";"; //$NON-NLS-1$
                }
                fileNames += fileName;
            }
            if (!fileNames.equals("")) { //$NON-NLS-1$
                _fileInput.setText(fileNames);
                return _fileInput.getText();
            }
        } else {
            DirectoryDialog dirDialog = new DirectoryDialog(getShell());
            dirDialog.setText(Messages.FileSystemImportSelectionPage_12);
            dirDialog.setMessage(Messages.FileSystemImportSelectionPage_13);
            if (_fileInput.getText().length() > 0) {
                dirDialog.setFilterPath(_fileInput.getText());
            } else {
                dirDialog.setFilterPath("C:" + System.getProperty("file.separator")); //$NON-NLS-1$ //$NON-NLS-2$
            }
            String dir = dirDialog.open();
            if (dir != null) {
                _fileInput.setText(dir);
            }
        }
        return null;
    }
    
    @Override
	public void checkStatus() {
        String status = null;
        String warning = null;
        String error = null;
        boolean pageComplete = true;
        //check for possible errors
        String filename = _fileInput.getText();
        if (filename == null || filename.length() == 0) {
            status = Messages.AbstractImportSelectionPage_6;
            pageComplete = false;
        }
        if (getSelectedURLS() == null) {
            status = Messages.AbstractImportSelectionPage_7;
            pageComplete = false;
        }
        if (getSelectedProject().length() == 0) {
            status = Messages.AbstractImportSelectionPage_8;
            pageComplete = false;
        }
        if(pageComplete) {
            try {                
            	String projectName = getSelectedProject();
                String ontologyProjectLanguage = NeOnCorePlugin.getDefault().getOntologyProject(projectName).getOntologyLanguage();
            	//check if transformation is needed    
            	ArrayList<String> filesToBeTransformed = new ArrayList<String>();
            	ArrayList<String> filesNotSupported = new ArrayList<String>();
            	String[] files = getFiles();
            	for (int i = 0; i < files.length; i++) {
    				String file = files[i];
                	URI physicalURI = new File(file).toURI();
                	try {
        				String ontologyFileFormat = getFileFilter().getOntologyFileFormat();
        				if(ontologyFileFormat == null || !getFileFilter().accept(new File(file))) {
        				    ontologyFileFormat = ImportExportUtils.getOntologyFileFormat(projectName, physicalURI);
        				}
    
//        				if (ontologyFileFormat != null && ontologyFileFormat.equals(OntoBrokerOntologyFileFormat.OWL_RDF) && files.length == 1) {
//        				    _importAsRDFButton.setVisible(true);
//        				} else {
//        				    _importAsRDFButton.setVisible(false);
//        				}
        				
        				if (_selectedFileFormat != null) {
        				    ontologyFileFormat = _selectedFileFormat;
        				}
//        				boolean rdfProject = ontologyProjectLanguage != null && ontologyProjectLanguage.equals(OntologyLanguage.RDF.toString());
//        				if (rdfProject) {
//        				    // in case of an RDF project (and if OWL/RDF is detected as file format) the user is asked if he wants to 
//        				    // import as plain RDF/XML or if he wants to import as OWL and transform afterwards. 
//                            if(_selectedFileFormat != null && ImportExportUtils.isTransformationRequired(projectName, ontologyFileFormat)) {
//                                filesToBeTransformed.add(file);
//                            }                       
//        				} else {
//                            if(ImportExportUtils.isTransformationRequired(projectName, ontologyFileFormat)) {
//                                filesToBeTransformed.add(file);
//                            }                       
//        				}
    				} catch (CommandException e2) {
    					filesNotSupported.add(file);
                    }
    			}
            	String messages = ""; //$NON-NLS-1$
            	if(filesNotSupported.size() > 0) {        		
            		messages = Messages.AbstractImportSelectionPage_11 + " "; //$NON-NLS-1$
            		for (int i = 0; i < filesNotSupported.size(); i++) {
    					String file = filesNotSupported.get(i);
    					if(i != 0) {
    						messages += ","; //$NON-NLS-1$
    					}
            			messages += " '" + file + "'";  //$NON-NLS-1$//$NON-NLS-2$
    				}
            		messages += " " + Messages.AbstractImportSelectionPage_12; //$NON-NLS-1$

            		error = messages;
    				pageComplete = false;        		
            	} else if(filesToBeTransformed.size() > 0) {      
            		messages += Messages.AbstractImportSelectionPage_9 + " " + ontologyProjectLanguage + " " + Messages.AbstractImportSelectionPage_10;  //$NON-NLS-1$ //$NON-NLS-2$
            		for (int i = 0; i < filesToBeTransformed.size(); i++) {
    					String file = filesToBeTransformed.get(i);
    					if(i != 0) {
    						messages += ","; //$NON-NLS-1$
    					}
            			messages += " '" + file + "'"; //$NON-NLS-1$ //$NON-NLS-2$
    				}
            		warning = messages;
            	}
            } catch (NeOnCoreException e) {
                error = e.getMessage();
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
