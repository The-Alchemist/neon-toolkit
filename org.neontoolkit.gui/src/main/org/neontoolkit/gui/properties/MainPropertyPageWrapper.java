/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package org.neontoolkit.gui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;

/*
 * Created on 04.09.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class MainPropertyPageWrapper implements IMainPropertyPage {
	// The wrapped propertyPage
	private IPropertyPage _propertyPage;
	private List<IPropertyPage> _subPages;
	private IStructuredSelection _selection;
	private IWorkbenchPart _part;
	private IWorkbenchPart _selectedPart;
	
	/**
	 * 
	 */
	public MainPropertyPageWrapper(IPropertyPage propertyPage) {
		_propertyPage = propertyPage;
		_subPages = new ArrayList<IPropertyPage>();
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IMainPropertyPage#createGlobalContents(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createGlobalContents(Composite parent) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IMainPropertyPage#getSubPages()
	 */
	public IPropertyPage[] getSubPages() {
		return _subPages.toArray(new IPropertyPage[0]);
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IMainPropertyPage#addSubPage(org.neontoolkit.gui.properties2.IPropertyPage)
	 */
	public void addSubPage(IPropertyPage page) {
		_subPages.add(page);
		if (_propertyPage instanceof LegacyPropertyPageWrapper && page instanceof LegacyPropertyPageWrapper) {
			IEntityPropertyPage parentPage = ((LegacyPropertyPageWrapper)_propertyPage).getLegacyPropertyPage();
			IEntityPropertyPage childPage = ((LegacyPropertyPageWrapper)page).getLegacyPropertyPage();
			if (parentPage instanceof IParentChildPropertyPage) {
				((IParentChildPropertyPage)parentPage).addChild(childPage);
			}
			if (childPage instanceof IParentChildPropertyPage) {
				((IParentChildPropertyPage)childPage).setParent(parentPage);
			}
		}
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
		_propertyPage.deSelectTab();
		
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
	    if (_propertyPage instanceof IImagePropertyPage) {
	        return ((IImagePropertyPage)_propertyPage).getImage();
	    }
		return null;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#getMainPage()
	 */
	public IMainPropertyPage getMainPage() {
		return null;
	}
	
	public IPropertyPage getWrappedPage() {
		return _propertyPage;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#getPart()
	 */
	public IWorkbenchPart getPart() {
		return _part;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#getSelection()
	 */
	public IStructuredSelection getSelection() {
		return _selection;
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
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#resetSelection()
	 */
	public void resetSelection() {
		_propertyPage.deSelectTab();
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#setMainPage(org.neontoolkit.gui.properties.IMainPropertyPage)
	 */
	public void setMainPage(IMainPropertyPage propertyPage) {
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IMainPropertyPage#setPart(org.eclipse.ui.IWorkbenchPart)
	 */
	public void setPart(IWorkbenchPart part) {
		_part = part;
		if (_propertyPage instanceof LegacyPropertyPageWrapper) {
			((LegacyPropertyPageWrapper)_propertyPage).setSite(part.getSite());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IMainPropertyPage#getSelectedPart()
	 */
	public IWorkbenchPart getSelectedPart() {
		return _selectedPart;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#setSelection(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void setSelection(IWorkbenchPart part, IStructuredSelection selection) {
		_selection = selection;
		_selectedPart = part;
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#selectTab()
	 */
	public void selectTab() {
		_propertyPage.selectTab();
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties2.IPropertyPage#update()
	 */
	public void update() {
		_propertyPage.update();
	}
	

}
