/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.swt.dialog.filesystem;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/* 
 * Created on 12.01.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 * 
 * Copyright (c) 2007 ontoprise technologies GmbH.
 */
public class FileSystemContentProvider implements ITreeContentProvider {
	public static int NONE = 0;
	public static int DIRECTORIES_ONLY = 1 << 0;
	
	private IResource _root;
	private int _style;

	public FileSystemContentProvider(IResource root, int style) {
		_root = root;
		_style = style;
	}
	
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IContainer) {
			try {
				IResource[] members = ((IContainer)parentElement).members();
				if (_style == 0) {
					return members;
				}
				else {
					List<IResource> result = new ArrayList<IResource>();
					for (IResource member: members) {
						if ((_style & DIRECTORIES_ONLY) > 0) {
							if (member instanceof IContainer) {
								result.add(member);
							}
						}
					}
					return result.toArray();
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new IResource[0];
	}

	public Object getParent(Object element) {
		assert(element instanceof IResource);
		return ((IResource)element).getParent();
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		if (_root != null) {
			return new IResource[]{_root};
		}
		else {
			return new IResource[0];
		}
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	public void setRoot(Object root) {
		_root = (IResource)root;
	}
}
