/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package org.neontoolkit.gui.properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.neontoolkit.gui.navigator.elements.IEntityElement;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;

/*
 * Created on 29.09.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class AbstractIDPropertyPage implements IPropertyPage {
	private IMainPropertyPage _mainPage;
	
    /*
     * selection specific variables
     */
    protected String _project;
    protected String _ontologyUri;
    protected String _id;
    
    protected boolean _enabled;

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#getMainPage()
	 */
	public IMainPropertyPage getMainPage() {
		return _mainPage;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#setMainPage(org.neontoolkit.gui.properties.IMainPropertyPage)
	 */
	public void setMainPage(IMainPropertyPage propertyPage) {
		_mainPage = propertyPage;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#selectTab()
	 */
	public void selectTab() {
		initSelection();
		refresh();
	}

	public void setEnabled(boolean enabled) {
		_enabled = enabled;
	}

	public boolean isEnabled() {
		return _enabled;
	}
	
	protected void initSelection() {
		IStructuredSelection selection;
		if (_mainPage != null) {
			selection = _mainPage.getSelection();
		}
		else if (this instanceof IMainPropertyPage) {
			selection = ((IMainPropertyPage)this).getSelection();
		}
		else {
			return;
		}
		
	    Object first = selection.getFirstElement();
		if (first instanceof IEntityElement) {
			IEntityElement element = (IEntityElement) first;
			_id = element.getId();
		}
		if (first instanceof IProjectElement) {
			_project = ((IProjectElement)first).getProjectName();
		}
		if (first instanceof IOntologyElement) {
			IOntologyElement element = (IOntologyElement)first;
			_ontologyUri = element.getOntologyUri();
			_enabled = !element.isImported();
		}
	}

	protected abstract void switchPerspective();
}
