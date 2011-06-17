/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.wizard;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.ontology.CreateUniqueOntologyUri;
import org.neontoolkit.core.command.ontology.ExistsOntology;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.util.URIUtils;

import com.ontoprise.ontostudio.owl.gui.Messages;


/* 
 * Created on: 12.11.2004
 * Created by: Dirk Wenke
 *
 * Function: UI, Ontology, Wizard
 */
/**
 * This class provides the wizard page that is displayed in the NewOntologyWizard.
 * @author Nico Stieler
 */
public class NewOntologyWizardPage extends WizardPage {

    private IStructuredSelection _selection;
    private Composite _container;
    private Combo _projectCombo;
    protected boolean _projectComboFixed = false;
    private Button _createButton;
    private Text _ontologyText;
    private Text _namespaceText;
    protected IInputValidator _uriValidator = getInputValidator();
    private String _ontologyURI;
    private String _projectName;
    private boolean _initDone;

    public NewOntologyWizardPage(IStructuredSelection selection) {
        super("NewOntologyWizardPage"); //$NON-NLS-1$
        setTitle(Messages.NewOntologyWizardPage_1); 
        setDescription(Messages.NewOntologyWizardPage_2); 
        _selection = selection;
    }

    protected IInputValidator getInputValidator() {
        return URIUtils.getOntologyUriValidator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        _container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(3, false);
        _container.setLayout(layout);
        layout.verticalSpacing = 9;
        Label label = new Label(_container, SWT.NULL);
        label.setText(Messages.NewOntologyWizardPage_3); 

        _ontologyText = new Text(_container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        _ontologyText.setLayoutData(gd);
        _ontologyText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if(_initDone)
                    updateStatus();
            }
        });

        gd = new GridData(GridData.FILL_HORIZONTAL);
        Label dummy1 = new Label(_container, SWT.NONE);
        dummy1.setVisible(false);
        dummy1.setLayoutData(gd);
        
        label = new Label(_container, SWT.NONE);
        label.setText(Messages.NewOntologyWizardPage_4); 

        _namespaceText = new Text(_container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        _namespaceText.setLayoutData(gd);
        _namespaceText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if(_initDone)
                    updateStatus();
            }
        });

        gd = new GridData(GridData.FILL_HORIZONTAL);
        Label dummy2 = new Label(_container, SWT.NONE);
        dummy2.setVisible(false);
        dummy2.setLayoutData(gd);

        label = new Label(_container, SWT.NONE);
        label.setText(Messages.NewOntologyWizardPage_5); 

        _projectCombo = new Combo(_container, SWT.BORDER | SWT.READ_ONLY);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
        _projectCombo.setLayoutData(gd);
        _projectCombo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if(_initDone)
                    updateStatus();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        _createButton = new Button(_container, SWT.PUSH);
        _createButton.setText(Messages.NewOntologyWizardPage_11); 
        _createButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                NewOntologyProjectWizard wizard = new NewOntologyProjectWizard();
                wizard.init(PlatformUI.getWorkbench(), null);
        		Shell parent = getShell();
        		WizardDialog dialog = new WizardDialog(parent, wizard);
        		dialog.create();
//        		WorkbenchHelp.setHelp(dialog.getShell(), org.eclipse.ui.internal.IHelpContextIds.NEW_WIZARD_SHORTCUT);
        		dialog.open();
        		initialize();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
            
        });
        _createButton.setVisible(false);
        gd = new GridData(GridData.GRAB_HORIZONTAL);
        _createButton.setLayoutData(gd);
                
        GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
        Label dummy3 = new Label(_container, SWT.NONE);
        dummy3.setVisible(false);
        dummy3.setLayoutData(gd3);

        
        initialize();
        updateStatus();
        setControl(_container);
    }

    private void initialize() {
        //initialize project combo
        String[] projects;
        try {
            projects = OntologyProjectManager.getDefault().getOntologyProjects();
            _projectCombo.setItems(projects);
            if (projects.length == 0) {
                _createButton.setVisible(true);
            } else if (projects.length == 1) {
                _projectCombo.select(_projectCombo.indexOf(projects[0]));
            }
            //set the initial values for the id and namespace
            if(_projectComboFixed){
                _ontologyText.setText(_ontologyURI);
                _ontologyText.setEnabled(false);
                _namespaceText.setText(_ontologyURI+"#"); //$NON-NLS-1$
                //set the current selected item
                if(_projectName != null && !_projectName.equals("")) //$NON-NLS-1$
                    _projectCombo.select(_projectCombo.indexOf(_projectName));
            }else{
                //set the current selected item
                if (_selection != null) {
                    Object selection = _selection.getFirstElement();
                    if (selection instanceof IProjectElement) {
                        _projectCombo.select(_projectCombo.indexOf(((IProjectElement) selection).getProjectName()));
                    }
                }
                String newId = new CreateUniqueOntologyUri(getProjectName()).getOntologyUri();
                _ontologyText.setText(newId);
                if (newId.equals("")) { //$NON-NLS-1$
                    _namespaceText.setText(""); //$NON-NLS-1$
                } else {
                    _namespaceText.setText(newId+"#"); //$NON-NLS-1$
                }
            }
            //set the current selected item
            if(_projectName != null && !_projectName.equals("")) //$NON-NLS-1$
                _projectCombo.select(_projectCombo.indexOf(_projectName));
            _projectCombo.setEnabled(!_projectComboFixed);
            _container.layout(true);
        } catch (Exception e) {
        	e.printStackTrace();
            NeOnUIPlugin.getDefault().logError("", e); //$NON-NLS-1$
        }
        _initDone = true;
    }

    private void updateStatus() {
        if (_projectCombo.getItemCount() <= 0) {
            updateStatus(Messages.NewOntologyWizardPage_6); 
            return;
        } else {
            try {
            	String projectName = getProjectName();
                if (NeOnCorePlugin.getDefault().getOntologyProject(projectName) != null) {
                	String ontologyUri = _ontologyText.getText();
                	String errorMsg = _uriValidator.isValid(ontologyUri);
                	if (errorMsg != null) {
                	    updateStatus(errorMsg);
                	    return;
                	}
                	errorMsg = validateNamespace(_namespaceText.getText());
                    if (errorMsg != null) {
                        updateStatus(errorMsg);
                        return;
                    }
                    if (new ExistsOntology(projectName, ontologyUri).exists()) {
                        updateStatus(Messages.NewOntologyWizardPage_15); 
                        return;
                    }
                }
            } catch (CommandException e) {
                NeOnUIPlugin.getDefault().logError("", e); //$NON-NLS-1$
            } catch (NeOnCoreException e) {
            	NeOnUIPlugin.getDefault().logError(Messages.NewOntologyWizardPage_14, e); 
            }
        }

        if (_projectCombo.getSelectionIndex() == -1) {
            updateStatus(Messages.NewOntologyWizardPage_7); 
            return;
        }
        
        String ontologyUri = _ontologyText.getText();
        String namespace = _namespaceText.getText();

        if ("".equals(ontologyUri)) { //$NON-NLS-1$
            updateStatus(Messages.NewOntologyWizardPage_8); 
            return;
        }

        if ("".equals(namespace)) { //$NON-NLS-1$
            updateStatus(Messages.NewOntologyWizardPage_9); 
            return;
        }
 
        updateStatus(null);
    }

    protected void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }
    
    private String validateNamespace(String input) {
        return URIUtils.validateNamespace(input, _uriValidator);
    }

    public String getProjectName() {
    	int index = _projectCombo.getSelectionIndex();
    	if (index != -1) {
            return _projectCombo.getItem(index);
    	}
    	return ""; //$NON-NLS-1$
    }

    public String getOntologyUri() {
        return _ontologyText.getText();
    }

    public String getDefaultNamespace() {
  		return _namespaceText.getText();
    }
    
    @Override
    public void performHelp() {
        String helpContextId = org.neontoolkit.gui.IHelpContextIds.OWL_CREATE_ONTOLOGY;
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(helpContextId);
    }
    /**
     * @param projectName
     */
    public void setFixed(String projectName, String ontologyURI) {
        _projectName = projectName;
        _ontologyURI = ontologyURI;
        _projectComboFixed = true;
    }
}
