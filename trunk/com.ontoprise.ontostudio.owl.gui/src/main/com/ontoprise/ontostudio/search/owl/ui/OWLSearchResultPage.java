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

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPageSite;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.search.Messages;
import org.neontoolkit.search.ui.OpenSearchPreferencesAction;
import org.neontoolkit.search.ui.SearchMatch;

import com.ontoprise.ontostudio.search.owl.match.ITreeObject;

/**
 * @author Nico Stieler
 * Edited on: 22.09.2010
 */
public class OWLSearchResultPage extends AbstractTextSearchViewPage implements ISearchResultPage {

    private SearchTreeContentProvider _contentProvider;
    private SearchTableLabelProvider _labelProvider;
    private int _currentPathIndex = 0;
    
    private IPropertyChangeListener _propertyChangeListener = new IPropertyChangeListener() {

        // Listens to the events that change the namespace and display
        // language settings
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                getViewer().update(_contentProvider.getElements(getViewer().getInput()), null);
            }
        }
    };

    public OWLSearchResultPage() {
        super(AbstractTextSearchViewPage.FLAG_LAYOUT_TREE);
    }
	
    @Override
    public void init(IPageSite pageSite) {
        super.init(pageSite);
        IMenuManager menuManager= pageSite.getActionBars().getMenuManager();
        menuManager.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, new OpenSearchPreferencesAction());
    }

    @Override
    public void dispose() {
        super.dispose();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
     */
    @Override
    protected void elementsChanged(Object[] objects) {
        if (_contentProvider != null) {
            _contentProvider.elementsChanged(objects);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#clear()
     */
    @Override
    protected void clear() {
        if (_contentProvider != null) {
            _contentProvider.clear();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
     */
    @Override
    protected void configureTreeViewer(TreeViewer viewer) {
        viewer.setUseHashlookup(true);
        _contentProvider = new SearchTreeContentProvider(this);
        _labelProvider = new SearchTableLabelProvider();
        viewer.setContentProvider(_contentProvider);
        viewer.setLabelProvider(_labelProvider);
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        store.addPropertyChangeListener(_propertyChangeListener);
    }

    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
     */
    @Override
    protected void configureTableViewer(TableViewer viewer) {      
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#getViewer()
     */
    @Override
    public StructuredViewer getViewer() {
        return super.getViewer();
    }

    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#handleOpen(org.eclipse.jface.viewers.OpenEvent)
     */
    @Override
    protected void handleOpen(OpenEvent event) {
        ITreeObject match = (ITreeObject)((IStructuredSelection) event.getSelection()).getFirstElement();

        //show first match
        _currentPathIndex = 0;
        match.show(_currentPathIndex);
        //activate view
        match.setFocus();
        
        super.handleOpen(event);
    }
    
    private SearchMatch getSelectedMatch() {
        Object element = ((IStructuredSelection) getViewer().getSelection()).getFirstElement();
        if (element instanceof SearchMatch) {
            SearchMatch match = (SearchMatch)element;
            if (_currentPathIndex >= 0 && _currentPathIndex < match.getOccurenceCount()) {//NICO necessary ???
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
//        Match currentMatch = getCurrentMatch();
//        if(currentMatch != null){
//            Object currentSearchMatch = currentMatch.getElement();
//            System.out.println("currentMatch: " + currentSearchMatch);
//            if(currentSearchMatch != null && currentSearchMatch instanceof OwlSearchMatch){
//                SearchMatch nextMatch = _contentProvider.getNextMatch((OwlSearchMatch)currentSearchMatch);
//                System.out.println("nextMatch: " + nextMatch);
//                if (nextMatch != null) {
////                    _currentPathIndex = 0;
                    navigateNext(true);
//                    showMatch(nextMatch);
//                }
//            }
//        }
        showCurrentMatch();
    }
    
    /**
     * Selects the element corresponding to the previous match and shows the
     * match in an editor. Note that this will cycle back to the last match
     * after the first match.
     */
    @Override
    public void gotoPreviousMatch() {
        gotoPreviousMatch(false);
    }

    private void gotoPreviousMatch(boolean activateEditor) {
//        Match currentMatch = getCurrentMatch();
//        if(currentMatch != null){
//            Object currentSearchMatch = currentMatch.getElement();
//            System.out.println("currentMatch: " + currentSearchMatch);
//            if(currentSearchMatch != null && currentSearchMatch instanceof OwlSearchMatch){
//                SearchMatch previousMatch = _contentProvider.getPreviousMatch((OwlSearchMatch)currentSearchMatch);
//                System.out.println("previousMatch: " + previousMatch);
//                if (previousMatch != null) {
//                    showMatch(previousMatch);
                    navigateNext(false);
//                    _currentPathIndex = 0;
//                    nextMatch = getSelectedMatch();
//                    if (nextMatch != null) {
//                        _currentPathIndex = nextMatch.getOccurenceCount()-1;
//                    }
//                }
//            }
//        }
        showCurrentMatch();
    }


    @SuppressWarnings("restriction")
    private void navigateNext(boolean forward) {
        org.eclipse.search2.internal.ui.basic.views.INavigate navigator = new org.eclipse.search2.internal.ui.basic.views.TreeViewerNavigator(this,(TreeViewer) getViewer());
        navigator.navigateNext(forward);
    }

    private boolean showCurrentMatch() {
        SearchMatch currentMatch = getSelectedMatch();
        if (currentMatch != null) {
            showMatch(currentMatch);
            return true;
        }
        return false;
    }
    
    private void showMatch(final SearchMatch match) {
        ISafeRunnable runnable = new ISafeRunnable() {
            @Override
            public void handleException(Throwable exception) {
                if (exception instanceof PartInitException) {
                    PartInitException pie = (PartInitException) exception;
                    ErrorDialog.openError(getSite().getShell(), Messages.SearchResultPage_1, Messages.SearchResultPage_2, pie.getStatus());  
                }
            }

            @Override
            public void run() throws Exception {
                match.show(_currentPathIndex);
            }
        };
        SafeRunner.run(runnable);
    }

    @Override
    public void internalRemoveSelected() {
        super.internalRemoveSelected();
    }
    @Override
    public void restoreState(IMemento memento) {
        super.restoreState(memento);
    }
    @Override
    public void setActionBars(IActionBars actionBars) {
        super.setActionBars(actionBars);
    }
    @Override
    protected void fillContextMenu(IMenuManager mgr) {
        super.fillContextMenu(mgr);
    }
    @Override
    protected void fillToolbar(IToolBarManager tbm) {
        super.fillToolbar(tbm);
    }
    @Override
    public Match[] getDisplayedMatches(Object element) {
        return super.getDisplayedMatches(element);
    }
    @Override
    protected IDialogSettings getSettings() {
        return super.getSettings();
    }
}
