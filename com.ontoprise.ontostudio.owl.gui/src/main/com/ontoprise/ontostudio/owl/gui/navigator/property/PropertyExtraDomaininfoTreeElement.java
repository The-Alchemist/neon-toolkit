/**
 *
 */
package com.ontoprise.ontostudio.owl.gui.navigator.property;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;


/**
 * @author Nico Stieler
 * Created on: 11.05.2011
 * this class should be used in the Domain view and can be used as well for the Range view
 * 
 */
public abstract class PropertyExtraDomaininfoTreeElement extends PropertyTreeElement {

    private boolean _isDirect;
    private OWLObject _property;
    private String _ontologyUri;
    private String _projectName;
    private HashSet<String> _owlClasses;
    private OWLClass _selectedClass;
    /**
     * @param entity
     * @param ontologyUri
     * @param project
     * @param provider
     */
    public PropertyExtraDomaininfoTreeElement(OWLEntity entity, String ontologyUri, String project, ITreeDataProvider provider) {
        super(entity, ontologyUri, project, provider);
    }
    public PropertyExtraDomaininfoTreeElement(String uri, String ontologyUri, String projectId, ITreeDataProvider provider) {
        super(uri, ontologyUri, projectId, provider);
    }
    /**
     * @param entity
     * @param ontologyUri
     * @param project
     * @param provider
     */
    public PropertyExtraDomaininfoTreeElement(OWLEntity entity, String ontologyUri, String project, ITreeDataProvider provider, OWLClass selectedClass, String owlClass) {
        super(entity, ontologyUri, project, provider);
        _isDirect = true;
        _owlClasses = new HashSet<String>();
        _owlClasses.add(owlClass);
        _selectedClass = selectedClass;
    }
    public boolean add(OWLClass selectedClass, String owlClass){
        if(selectedClass != _selectedClass)
            return (Boolean) null;
        return _owlClasses.add(owlClass);
    }
    public void resetIsDirect(boolean isDirect) {
        _isDirect = isDirect() && isDirect;
    }
    public boolean isDirect() {
        return _isDirect;
    }

    public boolean contains(OWLClass owlClass){
        return _owlClasses.contains(owlClass);
    }
    public boolean resetImported(boolean imported){
        setIsImported(isImported() || imported);
        return isImported();
    }

    /**
     * @return the _owlClasses
     */
    public Set<String> getOWLClasses() {
        return _owlClasses;
    }
    //NICO TODO equals ????
}
