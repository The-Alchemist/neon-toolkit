/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.elements;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ui.IActionFilter;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;

/* 
 * Created on: 27.12.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator
 */
/**
 * Abstract implementation of the basic functions of elements in the MTreeView.
 * This class extends the PlatformObject to be able to provide Adapters to
 * different interfaces.
 * This class implements <code>IActionFilter</code> to support filtering of actions
 * in the UI.
 */

public abstract class TreeElement extends PlatformObject implements ITreeElement, IActionFilter {
	/*
	 * The provider that created this element
	 */
	protected ITreeDataProvider _dataProvider;

	/**
	 * Default constructor with the id of the provider as parameter
	 * @param providerID
	 */
	public TreeElement(ITreeDataProvider provider) {
		_dataProvider = provider;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.ITreeElement#getProvider()
	 */
	public ITreeDataProvider getProvider() {
		return _dataProvider;
	}

	protected boolean equal(String s1, String s2) {
		if (s1 != null) {
			return s1.equals(s2);
		} else {
			return s2 == null;
		}
	}

	protected boolean equal(String[] s1, String[] s2) {
		if (s1 == null) {
			return s2 == null ? true : false;
		} else {
			if (s2 == null || s1.length != s2.length) {
				return false;
			}
			else {
				boolean equal = true;
				for (int i=0; i<s1.length && equal; i++) {
					equal = equal(s1[i], s2[i]);
				}
				return equal;
			}
		}
	}
	
	
    /* (non-Javadoc)
     * @see com.ontoprise.ontostudio.gui.schemaview.SchemaTreeElement#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        TreeElement that = (TreeElement) o;
        return getProvider() == that.getProvider();
    }

	@Override
	public int hashCode() {
		return _dataProvider != null ? _dataProvider.hashCode() : 0;
	}

	public boolean testAttribute(Object target, String name, String value) {
		try {
			Method field1 = target.getClass().getMethod(name, new Class[0]);
			Object returnVal = field1.invoke(target, new Object[0]);
			String returnValue = returnVal.toString();
			return returnValue.equals(value);
		} catch (Exception e) {
			StringBuffer buffer = new StringBuffer(Messages.TreeElement_0);
			buffer.append(target.getClass().getName());
			buffer.append(Messages.TreeElement_1);
			buffer.append(name);
			NeOnUIPlugin.getDefault().logError(buffer.toString(), e);
		}
		return false;
	}
}
