/**
 *
 */
package com.ontoprise.ontostudio.owl.gui.navigator.property;

import java.util.HashSet;
import java.util.Set;

import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.semanticweb.owlapi.model.OWLEntity;


/**
 * @author Nico Stieler
 * Created on: 11.05.2011
 * this class should be used in the Domain view and can be used as well for the Range view
 * 
 */
public abstract class PropertyExtraDomainRangeinfoTreeElement extends PropertyTreeElement {

    private boolean _isDirect;
    private HashSet<String> _owlEntities;
    private OWLEntity _selectedEntity;
    /**
     * @param entity
     * @param ontologyUri
     * @param project
     * @param provider
     */
    public PropertyExtraDomainRangeinfoTreeElement(OWLEntity entity, String ontologyUri, String project, ITreeDataProvider provider) {
        super(entity, ontologyUri, project, provider);
    }
    public PropertyExtraDomainRangeinfoTreeElement(String uri, String ontologyUri, String projectId, ITreeDataProvider provider) {
        super(uri, ontologyUri, projectId, provider);
    }
    /**
     * @param entity
     * @param ontologyUri
     * @param project
     * @param provider
     */
    public PropertyExtraDomainRangeinfoTreeElement(OWLEntity entity, String ontologyUri, String project, ITreeDataProvider provider, OWLEntity selectedEntity, String OWLObject) {
        super(entity, ontologyUri, project, provider);
        _isDirect = true;
        _owlEntities = new HashSet<String>();
        _owlEntities.add(OWLObject);
        _selectedEntity = selectedEntity;
    }
    public boolean add(OWLEntity selectedEntity, String owlEntity){
        if(selectedEntity != _selectedEntity)
            return (Boolean) null;
        return _owlEntities.add(owlEntity);
    }
    public void resetIsDirect(boolean isDirect) {
        _isDirect = isDirect() && isDirect;
    }
    public boolean isDirect() {
        return _isDirect;
    }

    public boolean contains(OWLEntity owlEntity){
        return _owlEntities.contains(owlEntity);
    }
    public boolean resetImported(boolean imported){
        setIsImported(isImported() || imported);
        return isImported();
    }

    /**
     * @return the _OWLObjectes
     */
    public Set<String> getOWLEntities() {
        return _owlEntities;
    }
}
