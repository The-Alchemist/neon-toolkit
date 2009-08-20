/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.ui.tree;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.neontoolkit.search.ui.AbstractSearchQuery;
import org.neontoolkit.search.ui.SearchResult;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;

/*
 * Created on 15.04.2005
 *
 */
public class OWLSearchResult extends SearchResult implements ISearchResult {
    // private AbstractSearchQuery _query;

    public OWLSearchResult(AbstractSearchQuery query) {
        super(query);
        // _query = query;
    }

    @Override
    public IEditorMatchAdapter getEditorMatchAdapter() {
        return null;
    }

    @Override
    public IFileMatchAdapter getFileMatchAdapter() {
        return null;
    }

    @Override
    public String getLabel() {
        int matchCount = getMatchCount();
        String label = "\"" + ((AbstractSearchQuery) getQuery()).getExpression() + "\" - " + matchCount + " Reference"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (matchCount > 1) {
            label += "s"; //$NON-NLS-1$
        }
        label += " found."; //$NON-NLS-1$
        return label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.search.ui.ISearchResult#getTooltip()
     */
    @Override
    public String getTooltip() {
        return "Tooltip"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
        return OWLPlugin.getDefault().getImageRegistry().getDescriptor(OWLSharedImages.CLAZZ);
    }

}
