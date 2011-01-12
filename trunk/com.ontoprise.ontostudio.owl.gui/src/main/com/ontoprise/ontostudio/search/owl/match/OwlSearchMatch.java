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
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.gui.util.ItemSorter;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.neontoolkit.search.ui.NavigatorSearchMatch;

import com.ontoprise.ontostudio.owl.gui.individualview.IIndividualTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

/**
 * @author Dirk Wenke
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
        AbstractOntologyTreeElement element = (AbstractOntologyTreeElement) getMatch();
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
        AbstractOntologyTreeElement element = (AbstractOntologyTreeElement) getMatch();

        if (element instanceof OntologyTreeElement) {
            return element.getOntologyUri();
        } else {
            try {
                AbstractOwlEntityTreeElement element0 = (AbstractOwlEntityTreeElement) element;
                return OWLGUIUtilities.getEntityLabel((element0).getEntity(), element.getOntologyUri(), element.getProjectName());
            } catch (NeOnCoreException e) {
                return element.toString();
            }
        }
    }

    private String getId() {
        AbstractOntologyTreeElement element = (AbstractOntologyTreeElement) getMatch();
        if (element instanceof OntologyTreeElement) {
            return element.getOntologyUri();
        } else {
            AbstractOwlEntityTreeElement element0 = (AbstractOwlEntityTreeElement) element;
            return element0.getId();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OwlSearchMatch)) {
            return false;
        }
        OwlSearchMatch other = (OwlSearchMatch) obj;
        AbstractOntologyTreeElement element1 = (AbstractOntologyTreeElement) getMatch();
        AbstractOntologyTreeElement element2 = (AbstractOntologyTreeElement) other.getMatch();
        return this.getId().equals(other.getId())
            && element1.getProjectName().equals(element2.getProjectName());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
    
    @Override
    public int getOccurenceCount() {
        return 1;//NICO always 1???
    }
    
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setParent(ITreeParent parent){
        this.parent = parent;
    }
    @Override
    public ITreeParent getParent() {
        return parent;
    }
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class key) {
        return null;
    }
    @Override
    public int numberOfLeafs(){
        return 1;
    }
    @Override
    public String getProjectId(){
        return parent.getProjectId();
    }
    @Override
    public void setFocus(){
        super.setFocus();
    }
}
