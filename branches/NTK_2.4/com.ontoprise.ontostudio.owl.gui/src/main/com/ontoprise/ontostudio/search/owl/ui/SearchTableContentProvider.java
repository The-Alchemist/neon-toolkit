/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;


/* 
 * Created on 15.04.2005
 * Created by Dirk Wenke 
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2005 ontoprise GmbH.
 * 
 * @author Dirk Wenke
 * 
 */
/**
 * @author Nico Stieler
 */
public class SearchTableContentProvider implements IStructuredContentProvider {
	
    protected static final Object[] EMPTY = new Object[0];
	
    protected SearchResultPage _page;
    protected SearchResult _result;

	public SearchTableContentProvider(SearchResultPage page) {
		_page = page;
	}
	
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof SearchResult) {
			Set<Object> filteredElements = new HashSet<Object>();
			Object[] rawElements = ((SearchResult) inputElement).getElements();
			for (int i = 0; i < rawElements.length; i++) {
				if (_page.getDisplayedMatchCount(rawElements[i]) > 0) {
					filteredElements.add(rawElements[i]);
				}
			}
			return filteredElements.toArray();
		}
		return EMPTY;
	}

	public void elementsChanged(Object[] updatedElements) {
		if (_result == null) {
			return;
		}

		int addCount = 0;
		int removeCount = 0;
		TableViewer viewer = (TableViewer) _page.getViewer();
		Set<Object> updated = new HashSet<Object>();
		Set<Object> added = new HashSet<Object>();
		Set<Object> removed = new HashSet<Object>();
		for (int i = 0; i < updatedElements.length; i++) {
			if (_page.getDisplayedMatchCount(updatedElements[i]) > 0) {
				if (viewer.testFindItem(updatedElements[i]) != null) {
					updated.add(updatedElements[i]);
				} else {
			    	added.add(updatedElements[i]);
				}
				addCount++;
			} else {
				removed.add(updatedElements[i]);
				removeCount++;
			}
		}
		
		viewer.add(added.toArray());
		viewer.update(updated.toArray(), null);
		viewer.remove(removed.toArray());
	}

	public void clear() {
		_page.getViewer().refresh();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		_result = (SearchResult) newInput;
	}
	
}
