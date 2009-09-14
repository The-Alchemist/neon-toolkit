/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.complexclazz;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.semanticweb.owlapi.model.OWLClassExpression;

import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * TreeElements used for classes in the tree.
 */

public class ComplexClazzTreeElement extends AbstractOwlEntityTreeElement {

    private OWLClassExpression _description;

    /**
     * @param elementId
     * @param providerID
     * @param isRoot
     * @param projectName
     * @param ontologyId
     */
    public ComplexClazzTreeElement(OWLClassExpression description, String ontologyUri, String project, ITreeDataProvider provider) {
        super("Complex Class", ontologyUri, project, provider); //$NON-NLS-1$
        _description = description;
    }

    public OWLClassExpression getDescription() {
        return _description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ProjectModuleEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ComplexClazzTreeElement) {
            return super.equals(o);
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.gui.navigator.elements.AbstractOntologyEntity#getQName()
     */
    @Override
    public String getQName() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String toString() {
        try {
            return OWLGUIUtilities.getEntityLabel(_description, getOntologyUri(), getProjectName());
        } catch (NeOnCoreException e) {
            return OWLUtilities.toString(_description);
        }
    }
}
