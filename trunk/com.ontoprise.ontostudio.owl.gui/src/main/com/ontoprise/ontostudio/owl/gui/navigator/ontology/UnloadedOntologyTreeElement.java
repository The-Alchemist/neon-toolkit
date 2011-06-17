/**
 * written by the NeOn Technologies Foundation ltd.
 */
package com.ontoprise.ontostudio.owl.gui.navigator.ontology;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.history.IOWLHistoryEntry;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.elements.AbstractProjectTreeElement;
import org.neontoolkit.gui.navigator.ontology.IUnloadedOntologyTreeElement;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.history.UnloadedOntologyHistoryEntry;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

/**
 * @author Nico Stieler
 * Created on: 07.06.2011
 */
public class UnloadedOntologyTreeElement extends AbstractProjectTreeElement implements IUnloadedOntologyTreeElement{

    private String _ontologyURI;
    private IOWLHistoryEntry _historyEntry;
    /**
     * @param projectName
     * @param ontologyURI
     * @param provider
     */
    public UnloadedOntologyTreeElement(String projectName, String ontologyURI, ITreeDataProvider provider) {
        super(projectName, provider);
        _ontologyURI = ontologyURI;
    }
    @Override
    public String getNamespace() {
        return getId();
    }

    @Override
    public String getId() {
        return getOntologyUri();
    }

    @Override
    public String toString() {
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        String result = getOntologyUri();
        if (idDisplayStyle == NeOnUIPlugin.DISPLAY_LOCAL) {
            result = OWLNamespaces.guessLocalName(result);
            if (result.length() == 0) {
                result = getOntologyUri();
            }
        }else if (idDisplayStyle == OWLPlugin.DISPLAY_LANGUAGE){
            try {
                String[] idArray = OWLGUIUtilities.getIdArray(OWLModelFactory.getOWLModel(getOntologyUri(), getProjectName()).getOntology(), getOntologyUri(), getProjectName());
                if(idArray.length > 3)
                    result = idArray[3];
            } catch (NeOnCoreException e) {
                // nothing to do
            }
        }
//        else if(idDisplayStyle == NeOnUIPlugin.)
        return result;
    }

    @Override
    public String getLocalName() {
        return getId();
    }
    @Override
    public IOWLHistoryEntry getOWLHistoryEntry() {
        if(_historyEntry == null){
            _historyEntry = new UnloadedOntologyHistoryEntry(this);
        }
        return _historyEntry;
    }
    
    /*
     * (non-Javadoc)
     * @see org.neontoolkit.gui.navigator.elements.IOntologyElement#getOntologyUri()
     */
    @Override
    public String getOntologyUri() {
        return _ontologyURI;
    }
    @Override
    public boolean isImported() {
        return false;
    }
}
