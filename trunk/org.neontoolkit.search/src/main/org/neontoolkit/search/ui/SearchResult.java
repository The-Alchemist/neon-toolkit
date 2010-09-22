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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;

/**
 * 
 * @author 
 * @author Nico Stieler
 * Created on 15.04.2005
 *
 */
public class SearchResult extends AbstractTextSearchResult implements ISearchResult {
	protected AbstractSearchQuery _query;
	
	public SearchResult(AbstractSearchQuery query) {
		_query = query;
	}
	

	@Override
    public IEditorMatchAdapter getEditorMatchAdapter() {
		return null;
	}

	@Override
    public IFileMatchAdapter getFileMatchAdapter() {
		return null;
	}

	public String getLabel() {
		int matchCount = getMatchCount();
		String label = "\"" + _query.getExpression() + "\" - " + matchCount + " Result"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (matchCount != 1) {
			label += "s"; //$NON-NLS-1$
		}
		label += " found."; //$NON-NLS-1$
		return label;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getTooltip()
	 */
	public String getTooltip() {
		return "Tooltip"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getQuery()
	 */
	public ISearchQuery getQuery() {
		return _query;
	}

    @Override
    public int getMatchCount() {
        Object[] elements = getElements();
        return elements.length;
//        int count= 0;
//        synchronized (super.fElementsToMatches) {
//            for (Iterator elements= fElementsToMatches.values().iterator(); elements.hasNext();) {
//                List element= (List) elements.next();
//                if (element != null)
//                    count+= element.size();
//            }
//        }
//        return count;
    }
}
