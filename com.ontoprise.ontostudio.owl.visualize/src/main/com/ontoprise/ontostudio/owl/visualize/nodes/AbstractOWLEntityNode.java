package com.ontoprise.ontostudio.owl.visualize.nodes;

import org.eclipse.core.runtime.CoreException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;

public class AbstractOWLEntityNode extends LabelImageNode {

    private OWLEntity _entity;

    public AbstractOWLEntityNode(OWLEntity entity, String ontologyUri, String projectId, boolean isRootNode) {
        super(entity != null ? entity.getURI().toString() : ontologyUri, ontologyUri, projectId, isRootNode);
        _entity = entity;
    }

    public AbstractOWLEntityNode() {
    }

    public AbstractOWLEntityNode(OWLEntity entity, String ontologyUri, String projectId) {
        this(entity, ontologyUri, projectId, false);
    }

    @Override
    public String guiRepresentation() {
        try {
            return OWLGUIUtilities.getEntityLabel(_entity, getOntologyId(), getProjectId());
        } catch (NeOnCoreException e) {
            return _entity.getURI().toString();
        }
    }

    @Override
    public boolean canBeNavigatedTo() {
        return false;
    }

    public Object create() throws CoreException {
        return new AbstractOWLEntityNode();
    }

    public OWLEntity getEntity() {
        return _entity;
    }
}
