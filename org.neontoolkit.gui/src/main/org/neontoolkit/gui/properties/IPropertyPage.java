/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.properties;

import org.eclipse.swt.widgets.Composite;

/* 
 * Created on: 01.02.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, EntityProperties
 */
/**
 * Interface that has to be implemented by all classes that extend the entityProperties
 * extension point.
 */

public interface IPropertyPage {
    
    /**
     * Invoked to create the content of the property page.
     * @param parent
     * @return
     */
	public Composite createContents(Composite parent);
	
	/**
	 * Called if the property page is going to be disposed.
	 *
	 */
	public void dispose();
	
	/**
	 * Returns true if the component is disposed
	 * @return
	 */
	public boolean isDisposed();
	
	/**
	 * Called if the data has changed and it is necessary to init the component with
	 * new data.
	 *
	 */

	public void refresh();
	/**
	 * called if redrawing is needed, but no need to retrieve the data again from the
	 * datamodel.
	 *
	 */
	public void update();
	
	/**
	 * Invoked if that tab of this sub-property-page is selected 
	 */
	public void selectTab();

	/**
	 * Invoked if that tab of another sub-property-page is going to be
	 * selected 
	 */
	public void deSelectTab();
	
	/**
	 * Returns the main property page this property page is belonging to. If the
	 * property page is not a sub-property-page, <code>null</code> is returned.
	 * @return
	 */
	public void setMainPage(IMainPropertyPage propertyPage);

	/**
	 * Returns the main property page this property page is belonging to. If the
	 * property page is not a sub-property-page, <code>null</code> is returned.
	 * @return
	 */
	public IMainPropertyPage getMainPage();
}
