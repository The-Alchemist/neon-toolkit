/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.match;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.ITreeElementPath;
import org.neontoolkit.gui.util.ItemSorter;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.neontoolkit.search.ui.NavigatorSearchMatch;

import com.ontoprise.ontostudio.owl.gui.individualview.IIndividualTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/* 
 * Created on 04.04.2008
 * @author Dirk Wenke
 * Edited on 30.09.2010
 * @author Nico Stieler
 *
 * Function:
 * Keywords:
 */
/**
 * @author Nico Stieler
 */
public abstract class OwlSearchMatch extends NavigatorSearchMatch implements ITreeObject{


    private String name;
    private ITreeParent parent;
    /**
     * @param element
     */
    public OwlSearchMatch(ITreeElement element) {
        super(element);
        name = element.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ITreeElementPath[] getPaths() {
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();
        if (element instanceof IIndividualTreeElement) {
            return new ITreeElementPath[0];
        }
        if (_paths == null) {
            _paths = getNavigator().getExtensionHandler().computePathsToRoot(element);
            ItemSorter.quickSort(_paths);
        }
        return _paths;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.ui.NavigatorSearchMatch#show(int)
     */
    @Override
    public void show(int index) {
        super.show(index);
        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID);
    }

    @Override
    public String toString() {
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();
        String elementId = element.getId();

        try {
            elementId = OWLGUIUtilities.getEntityLabel(element.getEntity(), element.getOntologyUri(), element.getProjectName());
        } catch (NeOnCoreException e) {
            // nothing to do
        }

        return elementId;// + "  [Ontology: " + element.getOntologyUri() + "] " + Messages.OwlSearchMatch_0 + element.getProjectName() + "]  "; /$NON_-NLS-1$ /$NON-NLS-2$ /$NON-NLS-3$ 
    }

    private String getId() {
        AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) getMatch();
        return element.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OwlSearchMatch)) {
            return false;
        }
        OwlSearchMatch other = (OwlSearchMatch) obj;
        AbstractOwlEntityTreeElement element1 = (AbstractOwlEntityTreeElement) getMatch();
        AbstractOwlEntityTreeElement element2 = (AbstractOwlEntityTreeElement) other.getMatch();
        return this.getId().equals(other.getId())
            && element1.getProjectName().equals(element2.getProjectName());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
    
    @Override
    public int getOccurenceCount() {
        return 1;
    }
    
    public String getName() {
        return name;
    }
    public void setParent(ITreeParent parent){
        this.parent = parent;
    }
    public ITreeParent getParent() {
        return parent;
    }
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class key) {
        return null;
    }
    public int numberOfLeafs(){
        return 1;
    }
    @Override
    public String getProjectId(){
        return parent.getProjectId();
    }
}
