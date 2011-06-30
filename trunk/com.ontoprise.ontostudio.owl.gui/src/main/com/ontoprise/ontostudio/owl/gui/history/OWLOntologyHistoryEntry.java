/**
 * written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.gui.history;

import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.history.AbstractHistroryEntry;
import org.neontoolkit.gui.history.OWLHistoryManager;
import org.neontoolkit.gui.navigator.elements.TreeElement;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/**
 * @author Nico Stieler
 * Created on: 14.04.2011
 */
public class OWLOntologyHistoryEntry extends AbstractHistroryEntry {

    protected TreeElement treeElement;
    protected String ontologyUri;
    protected String projectName;
    protected OWLModel model;
    protected boolean empty = false;

    /**
     * @param string
     * @param ontologyUri
     * @param projectName
     */
    protected OWLOntologyHistoryEntry(String ontologyUri, String projectName) {
        this.ontologyUri = ontologyUri;
        this.projectName = projectName;
    }
    /**
     * @param string
     * @param ontologyUri
     * @param projectName
     */
    public OWLOntologyHistoryEntry(OntologyTreeElement treeElement) {
        this(treeElement.getOntologyUri(), treeElement.getProjectName());
        this.treeElement = treeElement;
    }
    @Override
    public void restoreLocation() throws NeOnCoreException{
        OWLHistoryManager.getInstance().entitySelected(this);
        if( model == null)
            model = OWLModelFactory.getOWLModel(ontologyUri, projectName);
        OWLGUIUtilities.jumpToEntity(treeElement, model);
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
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY);
    }
    @Override
    public TreeElement getTreeElement(){
        return treeElement;
    }
}
