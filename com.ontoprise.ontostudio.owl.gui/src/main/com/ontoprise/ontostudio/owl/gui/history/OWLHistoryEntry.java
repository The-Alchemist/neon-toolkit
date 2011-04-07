 /*****************************************************************************
 * written by the NeOn technologies Foundation Ltd.
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.history;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.history.IOWLHistoryEntry;
import org.neontoolkit.gui.history.OWLHistoryManager;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * @author Nico Stieler
 * Created on: 10.03.2011
 * <code>OWLHistoryEntry</code> is the implementation of <code>IOWLHistoryEntry</code> for history entities
 */
public class OWLHistoryEntry implements IOWLHistoryEntry{

    
    private int historyPosition;
    private OWLEntity entity;
    private String entityString;
    private String ontologyUri;
    private String projectName;
    private OWLModel model;
    private boolean empty = false;

    /**
     * @param string
     * @param ontologyUri
     * @param projectName
     */
    public OWLHistoryEntry(final OWLEntity entity, final String ontologyUri, final String projectName) {
        this.entity = entity;
        this.entityString = OWLUtilities.toString(entity);
        this.ontologyUri = ontologyUri;
        this.projectName = projectName;
    }
    @Override
    public void restoreLocation() throws NeOnCoreException{
        OWLHistoryManager.entitySelected(this);
        if( model == null)
            model = OWLModelFactory.getOWLModel(ontologyUri, projectName);
        OWLGUIUtilities.jumpToEntity(entityString, model);
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
        return entityString;
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
        empty  = empty || (entityString == null || ontologyUri == null || projectName == null);
        return empty;
    }
    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof OWLHistoryEntry){
            final OWLHistoryEntry other = (OWLHistoryEntry) obj;
            if(other.projectName == projectName && other.ontologyUri == ontologyUri && other.entityString == entityString){
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
        try {
            return OWLGUIUtilities.getEntityLabel(entity,ontologyUri,projectName);
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return ""; //$NON-NLS-1$
        }
    }
}
