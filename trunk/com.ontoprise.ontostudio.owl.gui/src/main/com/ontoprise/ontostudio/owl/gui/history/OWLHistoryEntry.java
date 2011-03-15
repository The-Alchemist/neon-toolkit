 /*****************************************************************************
 * written by the NeOn technologies Foundation Ltd.
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.history;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.history.IOWLHistoryEntry;
import org.neontoolkit.gui.history.OWLHistoryManager;

import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/**
 * @author Nico Stieler
 * Created on: 10.03.2011
 * <code>OWLHistoryEntry</code> is the implementation of <code>IOWLHistoryEntry</code> for history entities
 */
public class OWLHistoryEntry implements IOWLHistoryEntry{

    
    private int historyPosition;
    private String entity;
    private String ontologyUri;
    private String projectName;
    private OWLModel model;

    /**
     * @param string
     * @param ontologyUri
     * @param projectName
     */
    public OWLHistoryEntry(final String entity, final String ontologyUri, final String projectName) {
        this.entity = entity;
        this.ontologyUri = ontologyUri;
        this.projectName = projectName;
    }
    @Override
    public void restoreLocation() throws NeOnCoreException{
        OWLHistoryManager.entitySelected(this);
        if( model == null)
            model = OWLModelFactory.getOWLModel(ontologyUri, projectName);
        OWLGUIUtilities.jumpToEntity(entity, model);
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
    public String getEntity() {
        return entity;
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
        return (entity == null || ontologyUri == null || projectName == null);
    }
    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof OWLHistoryEntry){
            final OWLHistoryEntry other = (OWLHistoryEntry) obj;
            if(other.projectName == projectName && other.ontologyUri == ontologyUri && other.entity == entity){
                return true;
            }
        }
        return false;
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
