/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/
package org.neontoolkit.search.ui;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.search.Messages;


/* 
 * Created on: 28.04.2005
 * Created by: Dirk Wenke
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Dirk Wenke
 */

public class SearchTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof SearchMatch) {
			return ((SearchMatch)element).getImage();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		StringBuffer s = new StringBuffer(element.toString());
		if (element instanceof SearchMatch) {
			int results = ((SearchMatch)element).getOccurenceCount();
			s.append("("); //$NON-NLS-1$
			s.append(results);
			if (results == 1) {
				s.append(Messages.SearchTableLabelProvider_1); 
			}
			else {
				s.append(Messages.SearchTableLabelProvider_2); 
			}
			s.append(")"); //$NON-NLS-1$
		}
		return s.toString();
	}
}
