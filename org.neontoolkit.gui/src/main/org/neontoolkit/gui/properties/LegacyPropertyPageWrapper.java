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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;

/*
 * Created on 04.09.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class LegacyPropertyPageWrapper implements IPropertyPage {
	private IEntityPropertyPage _propertyPage;
	private IMainPropertyPage _mainPage;
	
	/**
	 * 
	 */
	public LegacyPropertyPageWrapper(IEntityPropertyPage propertyPage) {
		_propertyPage = propertyPage;
	}
	
	/**
	 * Returns the property page if the legacy type IEntityPropertyPage
	 * @return
	 */
	public IEntityPropertyPage getLegacyPropertyPage() {
		return _propertyPage;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createContents(Composite parent) {
		return _propertyPage.createContents(parent);
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#deSelectTab()
	 */
	public void deSelectTab() {
		_propertyPage.deSelect();
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#dispose()
	 */
	public void dispose() {
		_propertyPage.dispose();
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#getImage()
	 */
	public Image getImage() {
		return _propertyPage.getImage();
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#getMainPage()
	 */
	public IMainPropertyPage getMainPage() {
		return _mainPage;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#isDisposed()
	 */
	public boolean isDisposed() {
		return _propertyPage.isDisposed();
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#refresh()
	 */
	public void refresh() {
		_propertyPage.refresh();
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#setMainPage(org.neontoolkit.gui.properties.IMainPropertyPage)
	 */
	public void setMainPage(IMainPropertyPage propertyPage) {
		_mainPage = propertyPage;
	}

	protected void setSite(IWorkbenchPartSite site) {
		_propertyPage.setSite(site);
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#selectTab()
	 */
	public void selectTab() {
		_propertyPage.setSelection(getMainPage().getSelectedPart(), getMainPage().getSelection());
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#update()
	 */
	public void update() {
		_propertyPage.update();
	}

}
