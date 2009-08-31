/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

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
import org.neontoolkit.gui.util.ItemSorter;
import org.neontoolkit.search.Messages;
import org.neontoolkit.search.SearchPlugin;


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
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.search.flogic.match.SearchMatch#show(int)
	 */
	@Override
    public void show(int index) {
		if (getNavigator() != null) {
			TreeItem item = (_navigator.getTreeViewer()).setPathExpanded(getPaths()[index]);
			if (item != null) {
				ArrayList<TreeItem> list = new ArrayList<TreeItem>();
				list.add(item);
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MTreeView.ID);
					_navigator.getTreeViewer().setSelection(new StructuredSelection());
					_navigator.getTreeViewer().selection(list);
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
		if (getNavigator() != null) {
			_navigator.setFocus();
		}
	}
}
