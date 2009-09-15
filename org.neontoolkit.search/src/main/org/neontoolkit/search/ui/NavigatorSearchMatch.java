/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.search.ui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.navigator.ITreeElementPath;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyEntity;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.properties.EntityPropertiesView;
import org.neontoolkit.gui.util.ItemSorter;
import org.neontoolkit.search.Messages;
import org.neontoolkit.search.SearchPlugin;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;


/* 
 * Created on: 22.03.2006
 * Created by: Dirk Wenke
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Dirk Wenke
 */

public abstract class NavigatorSearchMatch extends SearchMatch {
	protected ITreeElementPath[] _paths;
	protected static MTreeView _navigator;
	protected static EntityPropertiesView _entityPropertyView;

	public NavigatorSearchMatch(IProjectElement element) {
		super(element);
	}

	protected ITreeElementPath[] getPaths() {
		if (_paths == null) {
			_paths = getNavigator().getExtensionHandler().computePathsToRoot((AbstractOntologyTreeElement)getMatch());
			ItemSorter.quickSort(_paths);
		}
		return _paths;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.neontoolkit.search.flogic.match.SearchMatch#getOccurenceCount()
	 */
	@Override
    public int getOccurenceCount() {
		return getPaths().length;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
    public String toString() {
		return ((AbstractOntologyEntity)getMatch()).toString() + "  [" + ((AbstractOntologyEntity)getMatch()).getProjectName() + "]  "; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static MTreeView getNavigator() {
		if (_navigator == null) {
			_navigator = (MTreeView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);
		}
		return _navigator;
	}
	
    public static EntityPropertiesView getPropertyView() {
        if (_entityPropertyView == null)
            _entityPropertyView = (EntityPropertiesView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(EntityPropertiesView.ID);
        return _entityPropertyView;
    }
	   
	/* (non-Javadoc)
	 * @see org.neontoolkit.search.flogic.match.SearchMatch#show(int)
	 */
	@Override
    public void show(int index) {
		if (getNavigator() != null) {
			TreeItem item = (_navigator.getTreeViewer()).setPathExpanded(getPaths()[index]);
			if (item != null) {
//				ArrayList<TreeItem> list = new ArrayList<TreeItem>();
//				list.add(item);
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MTreeView.ID);
					
					//Bugfix: Selecting a list of TreeItems results in a empty EntityPropertyPage. 
					//Use a StructuredSelection with the specific TreeElement from getMatch() 
//					_navigator.getTreeViewer().setSelection(new StructuredSelection());
//					_navigator.getTreeViewer().selection(list);
					_navigator.getTreeViewer().setSelection(new StructuredSelection(getMatch()));
		            
		            
				} catch (PartInitException e) {
					SearchPlugin.logError(Messages.NavigatorSearchMatch_0, e);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.search.flogic.match.SearchMatch#setFocus()
	 */
	@Override
    public void setFocus() {
        if (getPropertyView() != null)
            getPropertyView().setFocus();
    }
}
