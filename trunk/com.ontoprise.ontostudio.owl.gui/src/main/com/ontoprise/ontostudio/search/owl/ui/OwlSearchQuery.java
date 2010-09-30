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

import org.eclipse.swt.graphics.Image;
import org.neontoolkit.search.command.AbstractSearchCommand;
import org.neontoolkit.search.ui.Scope;

import com.ontoprise.ontostudio.search.owl.match.ClassSearchMatch;
import com.ontoprise.ontostudio.search.owl.match.OwlSearchMatch;
import com.ontoprise.ontostudio.search.owl.ui.OwlSearchCommand.SearchArea;

/* 
 * Created on 04.04.2008
 * @author Dirk Wenke
 * Edited on 15.09.2010
 * @author Nico Stieler
 *
 * Function:
 * Keywords:
 */
/**
 * Type comment
 * @author Dirk Wenke
 * @author Nico Stieler
 */
public class OwlSearchQuery extends AbstractSearchQuery {

    private boolean _caseSensitive;

    /**
     * @param searchString
     * @param string
     * @param searchFlags
     * @param scope
     */
    public OwlSearchQuery(String searchString, boolean caseSensitive, int searchFlags, Scope scope) {
        super(searchString, searchFlags, scope.getProjects_ontologies());
        _caseSensitive = caseSensitive;
    }

    @Override
    protected AbstractSearchCommand getSearchCommand(String project, String ontology) {
        SearchArea searchArea;
        if(ontology == null){
            searchArea = SearchArea.PROJECT;
        }else{
            searchArea = SearchArea.ONTOLOGY;
        }
        return new OwlSearchCommand(project, ontology, _expression, _searchFlags, _caseSensitive,searchArea);
    }
}
