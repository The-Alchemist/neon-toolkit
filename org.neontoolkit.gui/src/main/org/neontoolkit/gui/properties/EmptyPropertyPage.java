/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.neontoolkit.gui.Messages;

/* 
 * Created on: 01.02.2005
 * Created by: Dirk Wenke
 *
 * Function: UI, Properties
 */
/**
 * Page that will be shown in the EntityProperties view if no element is selected or an
 * element which has no property page associated with.
 */

public class EmptyPropertyPage implements IEntityPropertyPage, IPropertyPage {
	Composite _emptyComponent;

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.properties.IEntityPropertyPage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createContents(Composite parent) {
		_emptyComponent = new Composite(parent, SWT.NONE);
		_emptyComponent.setLayout(new FillLayout(SWT.VERTICAL));
		new Label(_emptyComponent, SWT.NONE).setText(Messages.EmptyPropertyPage_1); 
		return _emptyComponent;
	}

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.properties.IEntityPropertyPage#setSelection(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void setSelection(IWorkbenchPart part, IStructuredSelection selection) {
	}

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.properties.IEntityPropertyPage#refreshData()
	 */
	public void refresh() {
	}

    /* (non-Javadoc)
     * @see com.ontoprise.ontostudio.gui.properties.IEntityPropertyPage#getImage()
     */
    public Image getImage() {
        return null;
    }
    
    
    /* (non-Javadoc)
     * @see com.ontoprise.ontostudio.gui.properties.IEntityPropertyPage#isDisposed()
     */
    public boolean isDisposed() {
        return _emptyComponent == null || _emptyComponent.isDisposed();
    }
    
    
	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.properties.IEntityPropertyPage#deSelect()
	 */
	public void deSelect() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IEntityPropertyPage#update()
	 */
	public void update() {
    }

	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.properties.IEntityPropertyPage#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IEntityPropertyPage#getSite()
	 */
	public IWorkbenchPartSite getSite() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IEntityPropertyPage#setSite(org.eclipse.ui.IWorkbenchSite)
	 */
	public void setSite(IWorkbenchPartSite site) {
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#deSelectTab()
	 */
	public void deSelectTab() {
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#getMainPage()
	 */
	public IMainPropertyPage getMainPage() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#setMainPage(org.neontoolkit.gui.properties.IMainPropertyPage)
	 */
	public void setMainPage(IMainPropertyPage propertyPage) {
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#getSelection()
	 */
	public IStructuredSelection getSelection() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#resetSelection()
	 */
	public void resetSelection() {
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#selectTab()
	 */
	public void selectTab() {
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#getPart()
	 */
	public IWorkbenchPart getPart() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#setPart(org.eclipse.ui.IWorkbenchPart)
	 */
	public void setPart(IWorkbenchPart part) {
	}
}
