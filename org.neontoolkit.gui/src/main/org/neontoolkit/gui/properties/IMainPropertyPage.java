/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package org.neontoolkit.gui.properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;

/*
 * Created on 03.09.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public interface IMainPropertyPage extends IPropertyPage {
	/**
     * Invoked to create the global content of the main property page. The global content 
     * is shown above all sub-property pages.
     * If no global content is provided by the property page, <code>null</code> can be returned. 
	 * @param parent
	 * @return
	 */
	public Composite createGlobalContents(Composite parent);

	/**
	 * Returns all sub-property pages that are registered for this property page.
	 * If no sub-property-pages are registered, an empty array is returned.
	 * @return
	 */
	public IPropertyPage[] getSubPages();
	
	/**
	 * Adds a sub-property page to this main property page
	 * @param page
	 */
	public void addSubPage(IPropertyPage page);
	
	/**
	 * Returns the view part that controls this property page. Normally this is the EntityPropertiesView.
	 * @return
	 */
	public IWorkbenchPart getPart();
	
	/**
	 * Returns the view part that has triggered the last selection event. This is the part that was passed
	 * during the last call to the setSelection method.
	 * @return
	 */
	public IWorkbenchPart getSelectedPart();

	/**
	 * Returns the last selection.
	 * @return
	 */
	public IStructuredSelection getSelection();
	
	/**
	 * Sets the view part that controls this property page. Normally this is the EntityPropertiesView.
	 * This method is called before the creation of the real content.
	 * @return
	 */
	public void setPart(IWorkbenchPart part);

	/**
	 * Provides the selected element in the GUI
	 * @param part
	 * @param selection
	 */
	public void setSelection(IWorkbenchPart part, IStructuredSelection selection);

	/**
	 * This method is called if a selection in the workbench occured which is associated
	 * with a different property page, which is about to show. 
	 * Clean up operations can be done in this method.
	 */
	public void resetSelection();

	/**
	 * The image that should be displayed in the title bar
	 * @return
	 */
	public Image getImage();
}
