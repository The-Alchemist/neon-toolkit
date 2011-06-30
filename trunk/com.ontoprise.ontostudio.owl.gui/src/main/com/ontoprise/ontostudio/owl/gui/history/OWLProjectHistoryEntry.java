/**
 * written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.gui.history;

import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.history.AbstractHistroryEntry;
import org.neontoolkit.gui.history.OWLHistoryManager;
import org.neontoolkit.gui.navigator.elements.TreeElement;
import org.neontoolkit.gui.navigator.project.ProjectTreeElement;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;

/**
 * @author Nico Stieler
 * Created on: 14.04.2011
 */
public class OWLProjectHistoryEntry extends AbstractHistroryEntry  {

    private ProjectTreeElement treeElement;
    private String projectName;
    private boolean empty = false;

    /**
     * @param string
     * @param ontologyUri
     * @param projectName
     */
    public OWLProjectHistoryEntry(final ProjectTreeElement treeElement) {
        this.treeElement = treeElement;
        this.projectName = treeElement.getProjectName();
    }
    @Override
    public void restoreLocation() throws NeOnCoreException{
        OWLHistoryManager.getInstance().entitySelected(this);
        OWLGUIUtilities.jumpToEntity(treeElement, null);
    }
    @Override
    public String getEntityURI() {
        return null;
    }
    @Override
    public String getOntologyUri() {
        return null;
    }
    @Override
    public String getProjectName() {
        return projectName;
    }
    @Override
    public boolean isEmpty() {
        empty  = empty || (projectName == null);
        return empty;
    }
    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof OWLProjectHistoryEntry){
            final OWLProjectHistoryEntry other = (OWLProjectHistoryEntry) obj;
            if(other.projectName == projectName){
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
        return projectName;
    }
    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.PROJECT);
    }
    @Override
    public TreeElement getTreeElement(){
        return treeElement;
    }
}
