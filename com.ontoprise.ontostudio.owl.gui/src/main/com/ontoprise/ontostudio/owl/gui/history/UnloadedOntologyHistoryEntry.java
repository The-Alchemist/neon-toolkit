/**
 * written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.gui.history;

import org.eclipse.swt.graphics.Image;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.UnloadedOntologyTreeElement;

/**
 * @author Nico Stieler
 * Created on: 14.04.2011
 */
public class UnloadedOntologyHistoryEntry extends OWLOntologyHistoryEntry {

    /**
     * @param string
     * @param ontologyUri
     * @param projectName
     */
    public UnloadedOntologyHistoryEntry(final UnloadedOntologyTreeElement treeElement) {
        super(treeElement.getOntologyUri(), treeElement.getProjectName());
        this.treeElement = treeElement;
        this.ontologyUri = treeElement.getOntologyUri();
        this.projectName = treeElement.getProjectName();
    }
    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof UnloadedOntologyHistoryEntry){
            final UnloadedOntologyHistoryEntry other = (UnloadedOntologyHistoryEntry) obj;
            if(other.projectName == projectName && other.ontologyUri == ontologyUri){
                return true;
            }
        }
        return false;
    }
    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.UNLOADED_ONTOLOGY);
    }
}
