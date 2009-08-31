/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package org.neontoolkit.gui.result;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;

/*
 * Created on 14.10.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
/**
 * The interface that must be implemented by all extensions of the resultPage 
 * extension point.
 */
public interface IResultPage {

	/**
	 * This method creates the content of the result page.
	 * @param parent
	 */
	public void createPage(Composite parent);

	/** 
	 * Called if the result page is going to be disposed.
	 */
	public void dispose();
	
	/**
	 * The ImageDescriptor to display an image for the result page. 
	 * @return
	 */
	public ImageDescriptor getImageDescriptor();
	
	/**
	 * Returns the text that should be displayed in the drop down menu.
	 * This method is called after the results are passed to the <code>setInput</code>
	 * method.
	 * @return
	 */
	public String getMenuText();
	
	/**
	 * Returns the text that should be displayed in the header of the result page.
	 * This method is called after the results are passed to the <code>setInput</code>
	 * method.
	 * @return
	 */
	public String getHeader();

	/**
	 * This method is called when the result of an operation is passed to 
	 * the result page.
	 * @param input
	 */
	public void setInput(Object source, Object[] result);
}
