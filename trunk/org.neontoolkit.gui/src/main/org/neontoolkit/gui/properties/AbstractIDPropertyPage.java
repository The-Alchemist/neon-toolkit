/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.neontoolkit.gui.navigator.elements.IEntityElement;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.elements.IQualifiedIDElement;

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
		if (first instanceof IQualifiedIDElement) {
		    IQualifiedIDElement element = (IQualifiedIDElement) first;
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
