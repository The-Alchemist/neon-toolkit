/**
 * written be the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.search.owl.ui;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.internal.ui.OpenSearchDialogAction;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler;

/**
 * @author Nico Stieler
 * Created on: 05.05.2011
 */
@SuppressWarnings("restriction")
public class SearchFromPopupHandler  extends AbstractSelectionBasedHandler {

    @Override
    public Object executeWithSelection(IWorkbenchPart part, IStructuredSelection selection) throws ExecutionException {
        OpenSearchDialogAction action = new OpenSearchDialogAction();
        OwlSearchPage.setPresetScopeValue(ISearchPageContainer.SELECTION_SCOPE);
        action.run();
        OwlSearchPage.activatePresetScope(false);
        return null;
    }


}
