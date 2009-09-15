/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.search.command;

import java.util.Set;

import org.eclipse.search.ui.text.Match;
import org.neontoolkit.core.EntityType;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.LoggedCommand;

/*
 * Created on 14.07.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class AbstractSearchCommand extends LoggedCommand {
	protected Match[] _results;
	
	public AbstractSearchCommand(String project, String expression, boolean caseSensitive) {
		super(project, expression, caseSensitive);
	}

	protected String getProject() {
		return (String)getArgument(0);
	}

	protected String getExpression() {
		return (String)getArgument(1);
	}

	protected boolean isCaseSensitive() {
		return (Boolean)getArgument(2);
	}
	
	public Match[] getResults() throws CommandException {
		if (_results == null) {
			run();
		}
		return _results;
		
	}

	protected abstract Set<EntityType> getTypes();
}
