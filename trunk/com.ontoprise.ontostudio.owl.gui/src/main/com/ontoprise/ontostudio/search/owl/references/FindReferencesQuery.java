/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.references;

import org.neontoolkit.gui.navigator.elements.TreeElement;
import org.neontoolkit.search.command.AbstractSearchCommand;
import org.neontoolkit.search.ui.AbstractSearchQuery;
import org.semanticweb.owlapi.model.OWLEntity;

public class FindReferencesQuery extends AbstractSearchQuery {

    private OWLEntity _entity;

    /**
     * @param entity to search for
     * @param project in which to search
     */
    public FindReferencesQuery(OWLEntity entity, String project) {
        super(entity.getIRI().toString(), 0, new String[][]{{project,null}});
        _entity = entity;
    }

    @Override
    protected AbstractSearchCommand getSearchCommand(String project, String ontology) {
//        ontology should be always null
        return new FindReferencesCommand(_entity, project);
    }
}
