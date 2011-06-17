/**
 * written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.gui.history;

import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.history.OWLHistoryManager;
import org.neontoolkit.gui.navigator.elements.TreeElement;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.UnloadedOntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/**
 * @author Nico Stieler
 * Created on: 14.04.2011
 */
public class UnloadedOntologyHistoryEntry extends OWLOntologyHistoryEntry {

    private int historyPosition;
    private TreeElement treeElement;
    private String ontologyUri;
    private String projectName;
    private OWLModel model;
    private boolean empty = false;

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
    public void restoreLocation() throws NeOnCoreException{
        OWLHistoryManager.getInstance().entitySelected(this);
        if( model == null)
            model = OWLModelFactory.getOWLModel(ontologyUri, projectName);
        OWLGUIUtilities.jumpToEntity(treeElement, model);
    }
    @Override
    public void setHistoryPosition(final int historyPosition) {
        this.historyPosition = historyPosition;
    }
    @Override
    public int getHistoryPosition() {
        return this.historyPosition;
    }
    @Override
    public String getEntityURI() {
        return null;
    }
    @Override
    public String getOntologyUri() {
        return ontologyUri;
    }
    @Override
    public String getProjectName() {
        return projectName;
    }
    @Override
    public boolean isEmpty() {
        empty  = empty || (ontologyUri == null || projectName == null);
        return empty;
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
    public int hashCode() {
        return super.hashCode();
    }
    @Override
    public boolean setEmpty(boolean empty) {
        this.empty = empty;
        return true;
    }
    @Override
    public String toString(){
        return ontologyUri;
    }
    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.UNLOADED_ONTOLOGY);
    }
    @Override
    public TreeElement getTreeElement(){
        return treeElement;
    }
}
