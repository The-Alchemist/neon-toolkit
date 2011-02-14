/**
 *
 */
package com.ontoprise.ontostudio.search.owl.match;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.properties.IPropertyPage;
import org.neontoolkit.search.SearchPlugin;

import com.ontoprise.ontostudio.owl.gui.individualview.IndividualView;

/**
 * @author Nico Stieler
 * Created on: 28.10.2010
 */
public abstract class OWLComplexSearchMatch extends OwlSearchMatch {

    protected ClassSearchMatch _classMatch;
    protected IndividualView _individualView;

    /**
     * @param element
     */
    public OWLComplexSearchMatch(ITreeElement element) {
        super(element);
    }

    protected ClassSearchMatch getClassMatch(){
        return _classMatch;
    }

    protected IndividualView getInstanceView() {
        if (_individualView == null) {
            try {
                _individualView = (IndividualView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IndividualView.ID);
            } catch (PartInitException e) {
                SearchPlugin.logError(org.neontoolkit.search.Messages.InstanceSearchMatch_0, e);
            }
        }
        return _individualView;
    }
    protected abstract boolean isCorrespondingTab(IPropertyPage tab);
}
