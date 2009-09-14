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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.neontoolkit.io.filter.FileFilter;
import org.neontoolkit.io.util.ImportExportUtils;
import org.neontoolkit.io.wizard.AbstractExportSelectionPage;

import com.ontoprise.ontostudio.owl.gui.Messages;

/* 
 * Created on 7.4.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: Wizard, WizardPage, Export, FileSystem, Ontology
 */

/**
 * Page for FileSystemExportWizard to specify where to save an ontology
 * Selection of projectname, module and file
 */
public class FileSystemExportSelectionPage extends AbstractExportSelectionPage {

    protected Button _browseButton;
    protected IWizardPage _prevPage;
    protected IWizard _wizard;
    protected Composite _composite;

    public class EventHandler implements SelectionListener, ModifyListener {

        public void widgetSelected(SelectionEvent se) {
            if (se.getSource().equals(_browseButton)) {
                // check whether extension is specified
                // if not -> directory import
                String fileExtension = ""; //$NON-NLS-1$
                if (_fileFilters != null) {
                    FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);

                    if (_fileInput.getText().length() > 0) {
                        String fileName = _fileInput.getText();
                        String fileNameWithoutExtension = getFileWithoutKnownFileExtension();
                        fileDialog.setFileName(fileNameWithoutExtension);
                        if(!fileName.equals(fileNameWithoutExtension)) {
                            fileExtension = "." + ImportExportUtils.getFileExtension(fileName); //$NON-NLS-1$
                        }
                    }
                    
                    String[] extension = new String[_fileFilters.length];
                    String[] description = new String[_fileFilters.length];
                    int index = 0;
                    boolean moveFileFilterUp = !fileExtension.equals(""); //$NON-NLS-1$
                    if(moveFileFilterUp) {
                        index = 1;
                    }
                    for (FileFilter fileFilter: _fileFilters) { 
                        if(moveFileFilterUp) {
    						String[] extensions = fileFilter.getExtensions();
    						for (String extens: extensions) {
                                if(extens.equals(fileExtension)) {
                                    extension[0] = fileFilter.getExtension();
                                    description[0] = fileFilter.getDescription();
                                    moveFileFilterUp = false;
                                    continue;
                                }
                            }
    						if(!moveFileFilterUp){
    						    continue;
    						}
                        }
						extension[index] = fileFilter.getExtension();
						description[index] = fileFilter.getDescription();
						index++;
					}
                    fileDialog.setFilterExtensions(extension);
                    fileDialog.setFilterNames(description);
                    fileDialog.open();
                    if(fileDialog.getFilterPath().equals("")) { //$NON-NLS-1$
                        //canceled
                        return;
                    }
                    String fileName = fileDialog.getFileName();
                    FileFilter usedFileFilter = null;
                    if (fileName.length() > 0) {
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
                        }
                        _fileFilter = usedFileFilter;
                        if(!fileName.endsWith(usedFileFilter.getDefaultExtension())) {
                            fileName += usedFileFilter.getDefaultExtension();
                        }
                        _fileInput.setText(fileDialog.getFilterPath() + System.getProperty("file.separator") + fileName); //$NON-NLS-1$
                    }
                } else {
                    DirectoryDialog dirDialog = new DirectoryDialog(getShell());
                    dirDialog.setText(Messages.FileSystemExportSelectionPage_3);
                    dirDialog.setMessage(Messages.FileSystemExportSelectionPage_4);
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
            } else if (se.getSource().equals(_projectCombo)) {
                //The selection of the project Combo has changed
                //update the items in the onto Combo
                initOntologyCombo();
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

    public FileSystemExportSelectionPage(FileFilter[] filters) {
        super(filters); 
        setTitle(Messages.FileSystemExportSelectionPage_7);
        setDescription(Messages.FileSystemExportSelectionPage_8);
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

        Label projectLabel = new Label(_composite, SWT.NONE);
        projectLabel.setText(Messages.FileSystemExportSelectionPage_9);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        projectLabel.setLayoutData(gridData);

        _projectCombo = new Combo(_composite, SWT.READ_ONLY);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        _projectCombo.setLayoutData(gridData);
        _projectCombo.addSelectionListener(listener);

        new Label(_composite, SWT.NONE);

        Label ontoLabel = new Label(_composite, SWT.NONE);
        ontoLabel.setText(Messages.FileSystemExportSelectionPage_10);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        ontoLabel.setLayoutData(gridData);

        _ontoCombo = new Combo(_composite, SWT.READ_ONLY);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        _ontoCombo.setLayoutData(gridData);
        _ontoCombo.addSelectionListener(listener);

        new Label(_composite, SWT.NONE);

        Label fileLabel = new Label(_composite, SWT.NONE);
        fileLabel.setText(Messages.FileSystemExportSelectionPage_11);
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
        _browseButton.setText(Messages.FileSystemExportSelectionPage_12);
        _browseButton.addSelectionListener(listener);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = GridData.FILL;
        _browseButton.setLayoutData(gridData);

        initControls();
        setControl(_composite);
    }
}
