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
 *
 */

/**
 * @author Dirk Wenke
 * @author Nico Stieler
 */
public abstract class AbstractSearchQuery implements ISearchQuery {
	protected SearchResult _result;
	protected String _expression;
    protected String[][] _projects_ontologies;
	protected int _searchFlags;
	
	
	public AbstractSearchQuery(String searchString, int searchFlags, String[][] projects_ontologies) {
		_expression = searchString;
		_searchFlags = searchFlags;
        _projects_ontologies = projects_ontologies;
	}

	@Override
    public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		final SearchResult textResult = (SearchResult) getSearchResult();
		textResult.removeAll();
		// Don't need to pass in working copies in 3.0 here

        int work = _projects_ontologies.length;
        monitor.beginTask(Messages.AbstractSearchQuery_0, work);
        for (String[] project_ontology : _projects_ontologies) {
            try {
                if(project_ontology.length == 2){
                    Match[] matches = getSearchCommand(project_ontology[0], project_ontology[1]).getResults();
//                    for (Match match: matches) {
//                        System.out.println(match.getElement());//NICO remove
//                        textResult.addMatch(match);
//                    }
                    textResult.addMatches(matches);
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

	@Override
    public String getLabel() {
		return Messages.AbstractSearchQuery_4; 
	}

	@Override
    public boolean canRerun() {
		return true;
	}

	@Override
    public boolean canRunInBackground() {
		return false;
	}

	@Override
    public ISearchResult getSearchResult() {
		if (_result == null) {
			_result = new SearchResult(this);
		}
		return _result;
	}

	public String getExpression() {
		return _expression;
	}
	
	protected abstract AbstractSearchCommand getSearchCommand(String project, String ontology);
}
