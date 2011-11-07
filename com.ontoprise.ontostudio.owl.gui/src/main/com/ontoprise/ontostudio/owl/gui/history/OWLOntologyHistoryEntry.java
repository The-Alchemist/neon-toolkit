/**
 *
 */
package com.ontoprise.ontostudio.owl.gui.history;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.history.IOWLHistoryEntry;
import org.neontoolkit.gui.history.OWLHistoryManager;

import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/**
 * @author Nico Stieler
 * Created on: 14.04.2011
 */
public class OWLOntologyHistoryEntry implements IOWLHistoryEntry {

    private int historyPosition;
    private OntologyTreeElement treeElement;
    private String ontologyUri;
    private String projectName;
    private OWLModel model;
    private boolean empty = false;

    /**
     * @param string
     * @param ontologyUri
     * @param projectName
     */
    public OWLOntologyHistoryEntry(final OntologyTreeElement treeElement) {
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
        if(obj instanceof OWLOntologyHistoryEntry){
            final OWLOntologyHistoryEntry other = (OWLOntologyHistoryEntry) obj;
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
}
