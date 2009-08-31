/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/
package org.neontoolkit.io.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.IOntologyProjectFilter;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.core.util.OntologyProjectFilter;
import org.neontoolkit.io.IOPlugin;

public abstract class ImportWizardPage extends WizardPage {

    protected String[] _projectsNames;
    protected Combo _projectsCombo;
    private String _project;
    protected Button _createButton;
    protected Text _ontologyUri;
    protected Combo _ns;
    protected IOntologyProjectFilter _filter;

    
    protected ImportWizardPage(String pageName) {
        super(pageName);
    }

    public void setOntologyProjectFilter(IOntologyProjectFilter filter) {
        _filter = filter;
    }  

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);        

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        composite.setLayout(layout);
        createProjectControl(composite);
    }
    
    public void updateStatus(){        
    }
    
    public void updateStatus(String message) {
        setMessage(message, IMessageProvider.INFORMATION);
        setPageComplete(message == null);
    }
    
    public void userSelection(TreeItem item) {    	
    }

    public void setSelectedProject(String project) {
        _project = project;
    }
    
    public String getSelectedProject() {
        String projectSel;
        int tmp = _projectsCombo.getSelectionIndex();
        if(tmp == -1) {
            return ""; //$NON-NLS-1$
        }
        projectSel = _projectsNames[tmp];
        return projectSel;
    }
    
    public void setSelectedOntologyUri(String ontologyUri) {
        _ontologyUri.setText(ontologyUri);
    }
    
    public String getSelectedOntologyUri() {
        return _ontologyUri.getText();
    }

    public void setSelectedNamespace(String ns) {
        _ns.setText(ns);
    }

    public String getSelectedNamespace() {
        return _ns.getText();
    }
    

    public void createProjectControl(Composite projectComposite) {
        createProjectControl(projectComposite, 200);
    }

    public void createProjectControl(Composite projectComposite, int comboSize) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        projectComposite.setLayout(layout);

        _projectsCombo = new Combo(projectComposite, SWT.READ_ONLY);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.widthHint = comboSize;
        _projectsCombo.setLayoutData(gridData);
        _projectsCombo.addSelectionListener(new ProjectComboSelectionListener());
        
        _createButton = new Button(projectComposite, SWT.PUSH);
        _createButton.setText("Create..."); //$NON-NLS-1$
        _createButton.setVisible(false);
//        _createButton.addSelectionListener(new SelectionListener() {
//
//            public void widgetSelected(SelectionEvent e) {
//                NewOntologyProjectWizard wizard = new NewOntologyProjectWizard();
//                wizard.init(PlatformUI.getWorkbench(), null);
//                Shell parent = getShell();
//                WizardDialog dialog = new WizardDialog(parent, wizard);
//                dialog.create();
//                dialog.open();
//                initValues();
//            }
//
//            public void widgetDefaultSelected(SelectionEvent e) {
//            }
//            
//        });

        GridData gdButton = new GridData(GridData.GRAB_HORIZONTAL);
        _createButton.setLayoutData(gdButton);

    }
    
    public void initValues() {
        initCombo();
    }
    
    public void initCombo() {
        try {
            if(_filter == null) {
                // match any project
                _filter = new OntologyProjectFilter(null, null);
            }
            
            String[] allProjects = OntologyProjectManager.getDefault().getOntologyProjects();
            List<String> possibleProjects = new ArrayList<String>();
            List<String> projectNames = new ArrayList<String>();
            for (int i = 0; i < allProjects.length; i++) {
                IOntologyProject project = NeOnCorePlugin.getDefault().getOntologyProject(allProjects[i]);
                if((_filter.matches(project)) && project.getProjectFailure() == null) {
                    projectNames.add(project.getName());
                    possibleProjects.add(project.toString());
                }
            }
            _projectsNames = projectNames.toArray(new String[0]);
            _projectsCombo.setItems(possibleProjects.toArray(new String[0]));
            if (_projectsNames.length > 0) {
                int projectIndex = -1;
                if(_project != null) {
                    IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(_project);
                    if(_filter.matches(ontologyProject)) {
                        projectIndex = _projectsCombo.indexOf(ontologyProject.toString());
                        _projectsCombo.select(projectIndex);
                    } else {
                        _project = null;
                    }
                }
                if(_project == null) {
                    _projectsCombo.select(0);
                    _project = getSelectedProject();
                }
                if(projectIndex == -1) {
                    projectIndex = 0;
                }
            }            
        } catch (Exception e) {
            e.printStackTrace();
            IOPlugin.getDefault().logError("", e); //$NON-NLS-1$
        }
//        if (_projectsCombo.getItemCount() == 0) {
//            _createButton.setVisible(true);
//        }  else {
//            _createButton.setVisible(false);
//        }

    }
    
    public class ProjectComboSelectionListener implements SelectionListener {

        public ProjectComboSelectionListener() {
        }

        public void widgetSelected(SelectionEvent e) {
            //refresh namespace and modules combo
//            Combo pCombo = (Combo) e.getSource();
//            int idx = pCombo.getSelectionIndex();
//            refreshCombo(_projectsNames[idx]);
            updateStatus();
        }

//        private void refreshCombo(String projectName) {
//            try {
//                //namespaces
//                String[] nsnames = _control.getAllNamespaces(projectName);
//                _ns.removeAll();
//                _ns.setItems(nsnames);
//                _ns.select(0);
//            } catch (ImportException e) {
//                e.printStackTrace();
//                //module.removeAll();
//                _ns.removeAll();
//            }
//
//        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }
    }

    
}
