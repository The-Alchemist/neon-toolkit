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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

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

public interface IEntityPropertyPage {
    
    /**
     * Invoked to create the content of the property page
     * @param parent
     * @return
     */
	public Composite createContents(Composite parent);

	/**
	 * The image that should be displayed in the title bar
	 * @return
	 */
	public Image getImage();

	/**
	 * Called if the data has changed and it is necessary to init the component with
	 * new data.
	 *
	 */
	public void refresh();
	
	/**
	 * Provides the selected element in the GUI
	 * @param part
	 * @param selection
	 */
	public void setSelection(IWorkbenchPart part, IStructuredSelection selection);

	/**
	 * This method is called if a different property page is about to show because another 
	 * element has been selected. Clean up operations can be done in this method.
	 *
	 */
	public void deSelect();
	
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
	 * called if redrawing is needed, but no need to retrieve the data again from the
	 * datamodel.
	 *
	 */
	public void update();
	
	/**
	 * Returns the workbench site that controls this property page. Normally this is the EntityPropertiesView.
	 * @return
	 */
	public IWorkbenchPartSite getSite();

	/**
	 * Sets the workbench site that controls this property page. Normally this is the EntityPropertiesView.
	 * @return
	 */
	public void setSite(IWorkbenchPartSite site);
}
