/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.properties.project;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.SharedImages;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.project.ProjectTreeElement;
import org.neontoolkit.gui.properties.IMainPropertyPage;
import org.neontoolkit.gui.properties.IPropertyPage;

/* 
 * Created on: 05.02.2008
 * Created by: Dirk Wenke
 *
 * Keywords: 
 */
/**
 * 
 */

public class ProjectPropertyPage implements IPropertyPage, IMainPropertyPage {
    private IWorkbenchPart _part;
    private IWorkbenchPart _selectedPart;
    private IStructuredSelection _selection;
    
	private IWorkbenchPartSite _site;
	private Label _projectName;
	private Label _location;
	
	private StackLayout _stackLayout;
    private List<IPropertyPage> _subPages = new ArrayList<IPropertyPage>();
    private List<IDatamodelConfigurationGroup> _configGroups = new ArrayList<IDatamodelConfigurationGroup>();
	private IMainPropertyPage _mainPage;
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IEntityPropertyPage#getImage()
	 */
	public Image getImage() {
		return NeOnUIPlugin.getDefault().getImageRegistry().get(SharedImages.PROJECT);
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IEntityPropertyPage#isDisposed()
	 */
	public boolean isDisposed() {
		return _location.isDisposed();
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IEntityPropertyPage#refresh()
	 */
	public void refresh() {
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IEntityPropertyPage#setSelection(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void setSelection(IWorkbenchPart part, IStructuredSelection selection) {
	    _selectedPart = part;
	    _selection = selection;
		Object element = selection.getFirstElement();
		if (element instanceof ProjectTreeElement) {
			ProjectTreeElement projectElem = (ProjectTreeElement)element;
			String projectName = projectElem.getProjectName();
			try {
    			IOntologyProject ontoProject = OntologyProjectManager.getDefault().getOntologyProject(projectName);

    			_projectName.setText(projectName);
    			_location.setText(NeOnCorePlugin.getDefault().getProject(projectName).getLocation().toString());
    			
    			for (IDatamodelConfigurationGroup group: _configGroups) {
    			    if (group.isValidGroup(ontoProject)) {
    			        group.setSelection(ontoProject);
    			        _stackLayout.topControl = group.getControl();
    			        break;
    			    }
    			}
    			_stackLayout.topControl.getParent().layout();
			} catch (NeOnCoreException nce) {
			    new NeonToolkitExceptionHandler().handleException("Could not retrieve ontology project: "+projectName, nce, part.getSite().getShell()); //$NON-NLS-1$
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IEntityPropertyPage#update()
	 */
	public void update() {
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IEntityPropertyPage#getSite()
	 */
	public IWorkbenchPartSite getSite() {
		return _site;
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IEntityPropertyPage#setSite(org.eclipse.ui.IWorkbenchSite)
	 */
	public void setSite(IWorkbenchPartSite site) {
		_site = site;
	}

    @Override
    public void dispose() {
    }

    @Override
    public void addSubPage(IPropertyPage page) {
        _subPages.add(page);
    }

    @Override
    public Composite createGlobalContents(Composite parent) {
        Composite composite  = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        Group properties = new Group(composite,SWT.NONE);
        properties.setText(Messages.ProjectPropertyPage_0);
        properties.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        properties.setLayout(new GridLayout(2,false));

        //project name
        Label projectLabel = new Label(properties, SWT.NONE);
        projectLabel.setText(Messages.ProjectPropertyPage_1);
        projectLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

        _projectName = new Label(properties, SWT.NONE);
        _projectName.setText(Messages.ProjectPropertyPage_2);
        _projectName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        
        Label locationLabel = new Label(properties, SWT.NONE);
        locationLabel.setText(Messages.ProjectPropertyPage_3);
        locationLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

        _location = new Label(properties, SWT.NONE);
        _location.setText(Messages.ProjectPropertyPage_4);
        _location.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
        
        return composite;
    }
    
    @Override
    public Composite createContents(Composite parent) {
        //Datamodel specific settings
        Composite stackedGroups = new Composite(parent, SWT.NONE);
        stackedGroups.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        _stackLayout = new StackLayout();
        stackedGroups.setLayout(_stackLayout);

        IDatamodelConfigurationGroup[] configGroups = NeOnUIPlugin.getDefault().getDatamodelConfigurationGroups();
        for (IDatamodelConfigurationGroup group: configGroups) {
            try {
                group.createContents(stackedGroups);
                _configGroups.add(group);
            } catch (Exception e) {
                NeOnCorePlugin.getDefault().logError("Configuration group could not be created:"+group.getClass().getName(), e); //$NON-NLS-1$
            }
        }
        ErrorConfigurationGroup errorGroup = new ErrorConfigurationGroup();
        errorGroup.createContents(stackedGroups);
        _configGroups.add(errorGroup);
        return stackedGroups;
    }

    @Override
    public IWorkbenchPart getPart() {
        return _part;
    }

    @Override
    public IWorkbenchPart getSelectedPart() {
        return _selectedPart;
    }

    @Override
    public IStructuredSelection getSelection() {
        return _selection;
    }

    @Override
    public IPropertyPage[] getSubPages() {
        return _subPages.toArray(new IPropertyPage[0]);
    }

    @Override
    public void resetSelection() {
    }

    @Override
    public void setPart(IWorkbenchPart part) {
        _part = part;
    }

    @Override
    public void deSelectTab() {
    }

    @Override
    public IMainPropertyPage getMainPage() {
        return _mainPage;
    }

    @Override
    public void selectTab() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setMainPage(IMainPropertyPage propertyPage) {
        _mainPage = propertyPage;
    }
}
