/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.ui;

import org.neontoolkit.search.command.AbstractSearchCommand;
import org.neontoolkit.search.ui.Scope;

import com.ontoprise.ontostudio.search.owl.ui.OwlSearchCommand.SearchScope;

/**
 * @author Dirk Wenke
 * @author Nico Stieler
 */
public class OwlSearchQuery extends AbstractSearchQuery {

    private boolean _caseSensitive;
    private int _IDDisplayStyleForQuery;

    /**
     * @param searchString
     * @param string
     * @param searchFlags
     * @param scope
     */
    public OwlSearchQuery(String searchString, boolean caseSensitive, int searchFlags, int IDDisplayStyleForQuery, Scope scope) {
        super(searchString, searchFlags, scope.getProjects_ontologies());
        _caseSensitive = caseSensitive;
        _IDDisplayStyleForQuery = IDDisplayStyleForQuery;
    }

    @Override
    protected AbstractSearchCommand getSearchCommand(String project, String ontology) {
        SearchScope searchArea;
        if(ontology == null){
            searchArea = SearchScope.PROJECT;
        }else{
            searchArea = SearchScope.ONTOLOGY;
        }
        return new OwlSearchCommand(project, ontology, _expression, _searchFlags, _caseSensitive, _IDDisplayStyleForQuery, searchArea);
    }
}
