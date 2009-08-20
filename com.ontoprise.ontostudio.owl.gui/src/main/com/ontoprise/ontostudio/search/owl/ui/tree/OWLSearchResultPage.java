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

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search2.internal.ui.basic.views.INavigate;
import org.eclipse.search2.internal.ui.basic.views.TableViewerNavigator;
import org.eclipse.ui.PartInitException;
import org.neontoolkit.search.Messages;
import org.neontoolkit.search.ui.SearchResultPage;
import org.neontoolkit.search.ui.SearchTableContentProvider;
import org.neontoolkit.search.ui.SearchTableLabelProvider;

import com.ontoprise.ontostudio.search.owl.match.OwlSearchMatch;

/*
 * Created on 15.04.2005
 * 
 */
@SuppressWarnings("restriction")
public class OWLSearchResultPage extends SearchResultPage implements ISearchResultPage {

    private SearchTableContentProvider _contentProvider;
    private SearchTableLabelProvider _labelProvider;
    private int _currentPathIndex = 0;

    // private TreeContentProvider _treeContentProvider;

    public OWLSearchResultPage() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
     */
    @Override
    protected void elementsChanged(Object[] objects) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#clear()
     */
    @Override
    protected void clear() {
        if (_contentProvider != null) {
            _contentProvider.clear();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
     */
    @Override
    protected void configureTreeViewer(TreeViewer viewer) {
        // viewer.setUseHashlookup(true);
        // viewer.setContentProvider(getContentProvider());
        // _labelProvider = new SearchTableLabelProvider();
        // viewer.setLabelProvider(_labelProvider);
        // viewer.setInput("root");
    }

    // private IContentProvider getContentProvider() {
    // if (_treeContentProvider == null) {
    // _treeContentProvider = new TreeContentProvider();
    // }
    // return _treeContentProvider;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
     */
    @Override
    protected void configureTableViewer(TableViewer viewer) {
        viewer.setUseHashlookup(true);
        // viewer.setLabelProvider(new ColorDecoratingLabelProvider(new SortingLabelProvider(this),
        // PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
        _contentProvider = new SearchTableContentProvider(this);
        _labelProvider = new SearchTableLabelProvider();
        viewer.setContentProvider(_contentProvider);
        viewer.setLabelProvider(_labelProvider);
        // setSortOrder(fCurrentSortOrder);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#getViewer()
     */
    @Override
    public StructuredViewer getViewer() {
        return super.getViewer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#handleOpen(org.eclipse.jface.viewers.OpenEvent)
     */
    @Override
    protected void handleOpen(OpenEvent event) {
        OwlSearchMatch match = (OwlSearchMatch) ((IStructuredSelection) event.getSelection()).getFirstElement();

        // show first match
        _currentPathIndex = 0;
        match.show(_currentPathIndex);
        // activate view
        match.setFocus();

        super.handleOpen(event);
    }

    private OwlSearchMatch getSelectedMatch() {
        Object element = ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
        if (element instanceof OwlSearchMatch) {
            OwlSearchMatch match = (OwlSearchMatch) element;
            if (_currentPathIndex >= 0 && _currentPathIndex < match.getOccurenceCount()) {
                return match;
            }
        }
        return null;
    }

    @Override
    public void gotoNextMatch() {
        gotoNextMatch(false);
    }

    private void gotoNextMatch(boolean activateEditor) {
        _currentPathIndex++;
        OwlSearchMatch nextMatch = getSelectedMatch();
        if (nextMatch == null) {
            _currentPathIndex = 0;
            navigateNext(true);
        }
        showCurrentMatch();
    }

    /**
     * Selects the element corresponding to the previous match and shows the match in an editor. Note that this will cycle back to the last match after the
     * first match.
     */
    @Override
    public void gotoPreviousMatch() {
        gotoPreviousMatch(false);
    }

    private void gotoPreviousMatch(boolean activateEditor) {
        _currentPathIndex--;
        OwlSearchMatch nextMatch = getSelectedMatch();
        if (nextMatch == null) {
            navigateNext(false);
            _currentPathIndex = 0;
            nextMatch = getSelectedMatch();
            if (nextMatch != null) {
                _currentPathIndex = nextMatch.getOccurenceCount() - 1;
            }
        }
        showCurrentMatch();
    }

    private void navigateNext(boolean forward) {
        INavigate navigator = new TableViewerNavigator((TableViewer) getViewer());
        navigator.navigateNext(forward);
    }

    private boolean showCurrentMatch() {
        OwlSearchMatch currentMatch = getSelectedMatch();
        if (currentMatch != null) {
            showMatch(currentMatch);
            return true;
        }
        return false;
    }

    private void showMatch(final OwlSearchMatch match) {
        ISafeRunnable runnable = new ISafeRunnable() {
            public void handleException(Throwable exception) {
                if (exception instanceof PartInitException) {
                    PartInitException pie = (PartInitException) exception;
                    ErrorDialog.openError(getSite().getShell(), Messages.SearchResultPage_1, Messages.SearchResultPage_2, pie.getStatus()); 
                }
            }

            public void run() throws Exception {
                match.show(_currentPathIndex);
            }
        };
        SafeRunner.run(runnable);
    }
}
