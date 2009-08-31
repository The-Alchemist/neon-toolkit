/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.search.ui;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.Match;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.search.Messages;
import org.neontoolkit.search.SearchPlugin;
import org.neontoolkit.search.command.AbstractSearchCommand;


/* 
 * Created on 15.04.2005
 * Created by Dirk Wenke 
 *
 * Function:
 * Keywords:
 * @author Dirk Wenke
 *
 */
/**
 * 
 */
public abstract class AbstractSearchQuery implements ISearchQuery {
	private SearchResult _result;
	protected String _expression;
	protected String[] _projects;
	protected int _searchFlags;
	
	
	public AbstractSearchQuery(String searchString, int searchFlags, String[] projects) {
		_expression = searchString;
		_searchFlags = searchFlags;
		_projects = projects;
	}

	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		final SearchResult textResult = (SearchResult) getSearchResult();
		textResult.removeAll();
		// Don't need to pass in working copies in 3.0 here

		int work = _projects.length;
		monitor.beginTask(Messages.AbstractSearchQuery_0, work);
		for (String project:_projects) {
			try {
				Match[] matches = getSearchCommand(project).getResults();
				for (Match match: matches) {
					textResult.addMatch(match);
				}
				if (monitor.isCanceled()) {
				    throw new OperationCanceledException();
				}
			} catch (CommandException e) {
	            SearchPlugin.logError("", e); //$NON-NLS-1$
			} finally {
				monitor.worked(1);
			}
		}
		String message = Messages.AbstractSearchQuery_3; 
		MessageFormat.format(message, new Object[] {new Integer(textResult.getMatchCount()) });
		return new Status(IStatus.OK, SearchPlugin.getDefault().getBundle().getSymbolicName(), 0, message, null);
	}

	public String getLabel() {
		return Messages.AbstractSearchQuery_4; 
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return false;
	}

	public ISearchResult getSearchResult() {
		if (_result == null) {
			_result = new SearchResult(this);
		}
		return _result;
	}

	public String getExpression() {
		return _expression;
	}
	
	protected abstract AbstractSearchCommand getSearchCommand(String project);
}
