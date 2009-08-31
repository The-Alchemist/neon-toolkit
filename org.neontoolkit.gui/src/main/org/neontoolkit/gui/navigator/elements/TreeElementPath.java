/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator.elements;

import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.ITreeElementPath;

import java.util.ArrayList;

/* 
 * Created on: 13.07.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator
 */
/**
 * A class for paths of ITreeElements in a tree.
 */

public class TreeElementPath implements ITreeElementPath, Cloneable, Comparable<Object> {

	private ArrayList<ITreeElement> _pathElements;

	/**
	 * 
	 * @param elements
	 */
	public TreeElementPath(ITreeElement[] elements) {
		this();
		for (int i = 0; i < elements.length; i++) {
			_pathElements.add(elements[i]);
		}
	}
	
	public TreeElementPath() {
		_pathElements = new ArrayList<ITreeElement>();
	}
	
	public void append(ITreeElement element) {
		_pathElements.add(element);
	}
	
	public void append(ITreeElementPath path) {
		for (int i = 0; i < path.length(); i++) {
			_pathElements.add(path.get(i));
		}
	}
	
	public ITreeElement get(int index) {
		return (ITreeElement) _pathElements.get(index);
	}

	public void insert(int index, ITreeElement element) {
		_pathElements.add(index, element);
	}

	public ITreeElement[] toArray() {
		return (ITreeElement[]) _pathElements.toArray(new ITreeElement[0]);
	}
	
	public int length() {
		return _pathElements.size();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new TreeElementPath(toArray());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (_pathElements != null) {
			StringBuffer s = new StringBuffer();
			for (int i=0; i<_pathElements.size(); i++) {
				if (i>0) {
					s.append('/');
				}
				s.append(_pathElements.get(i).toString());
			}
			return s.toString();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object o) {
		return toString().compareTo(o.toString());
	}
}
