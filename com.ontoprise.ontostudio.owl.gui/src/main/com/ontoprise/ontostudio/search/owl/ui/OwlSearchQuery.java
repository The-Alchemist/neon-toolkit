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
import org.neontoolkit.search.ui.AbstractSearchQuery;

/* 
 * Created on 04.04.2008
 * @author Dirk Wenke
 *
 * Function:
 * Keywords:
 */
/**
 * Type comment
 */
public class OwlSearchQuery extends AbstractSearchQuery {

    private boolean _caseSensitive;

    /**
     * @param searchString
     * @param string
     * @param searchFlags
     * @param projects
     */
    public OwlSearchQuery(String searchString, boolean caseSensitive, int searchFlags, String[] projects) {
        super(searchString, searchFlags, projects);
        _caseSensitive = caseSensitive;
    }

    @Override
    protected AbstractSearchCommand getSearchCommand(String project) {
        return new OwlSearchCommand(project, _expression, _searchFlags, _caseSensitive);
    }
}
