/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.search.ui;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/* 
 * Created on: 20.03.2006
 * Created by: Dirk Wenke
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Dirk Wenke
 * @author Nico Stieler
 */

public class SearchPatternData {
	/**
	 * 
	 */
	private static final String SETTINGS_SEARCHFLAGS = "searchFlags"; //$NON-NLS-1$
    /**
     * 
     */
    private static final String SETTINGS_IGNORECASE = "ignoreCase"; //$NON-NLS-1$
    /**
     * 
     */
    private static final String SETTINGS_ID_DISPLAY_STYLE_FOR_QUERY = "idDisplayStyleForQuery"; //$NON-NLS-1$
    /**
     * 
     */
    private static final String SETTINGS_ID_DISPLAY_STYLE_LANGUAGE_FOR_QUERY = "idDisplayStyleLanguageForQuery"; //$NON-NLS-1$
	/**
	 * 
	 */
	private static final String SETTINGS_SCOPE = "scope"; //$NON-NLS-1$
	/**
	 * 
	 */
	private static final String SETTINGS_WORKINGSETS = "workingSets"; //$NON-NLS-1$
	/**
	 * 
	 */
	private static final String SETTINGS_PROJECTS = "projects"; //$NON-NLS-1$
	/**
	 * 
	 */
	private static final String SETTINGS_TEXTPATTERN = "textPattern"; //$NON-NLS-1$
	private String _textPattern;
    private boolean _ignoreCase;
    private int _idDisplayStyleForQuery;
    private String _idDisplayStyleLanguageForQuery;
	private int _searchFlags;
	private int _scope;
	private String[] _projectNames;
	private IWorkingSet[] _workingSets;
	
	public SearchPatternData(String textPattern, boolean ignoreCase, int IDDisplayStyleForQuery, String idDisplayStyleLanguageForQuery, int searchFlags, int scope, String[] projectNames, IWorkingSet[] workingSets) {
		_ignoreCase= ignoreCase;
        _idDisplayStyleForQuery= IDDisplayStyleForQuery;
        _idDisplayStyleLanguageForQuery= idDisplayStyleLanguageForQuery;
		_textPattern= textPattern;
		_scope= scope;
		_projectNames = projectNames; //can be null
		_workingSets= workingSets; // can be null
		_searchFlags = searchFlags;
	}
	
	public void store(IDialogSettings settings) {
		settings.put(SETTINGS_IGNORECASE, _ignoreCase); 
        settings.put(SETTINGS_ID_DISPLAY_STYLE_FOR_QUERY, _idDisplayStyleForQuery); 
        settings.put(SETTINGS_ID_DISPLAY_STYLE_LANGUAGE_FOR_QUERY, _idDisplayStyleLanguageForQuery); 
        settings.put(SETTINGS_TEXTPATTERN, _textPattern); 
		settings.put(SETTINGS_SCOPE, _scope); 
		settings.put(SETTINGS_SEARCHFLAGS, _searchFlags); 
		if (_workingSets != null) {
			String[] wsIds= new String[_workingSets.length];
			for (int i= 0; i < _workingSets.length; i++) {
				wsIds[i]= _workingSets[i].getId();
			}
			settings.put(SETTINGS_WORKINGSETS, wsIds); 
		} else {
			settings.put(SETTINGS_WORKINGSETS, new String[0]); 
		}
		if (_projectNames != null) {
			settings.put(SETTINGS_PROJECTS, _projectNames);
		}
		else {
			settings.put(SETTINGS_PROJECTS, new String[0]);
		}

	}
	
	public static SearchPatternData create(IDialogSettings settings) {
		String textPattern= settings.get(SETTINGS_TEXTPATTERN); 

		String[] projects = settings.getArray(SETTINGS_PROJECTS); 
		if(projects != null && projects.length == 0) {
			projects = null;
		}
		
		String[] wsIds= settings.getArray(SETTINGS_WORKINGSETS); 
		IWorkingSet[] workingSets= null;
		if (wsIds != null && wsIds.length > 0) {
			IWorkingSetManager workingSetManager= PlatformUI.getWorkbench().getWorkingSetManager();
			workingSets= new IWorkingSet[wsIds.length];
			for (int i= 0; workingSets != null && i < wsIds.length; i++) {
				workingSets[i]= workingSetManager.getWorkingSet(wsIds[i]);
				if (workingSets[i] == null) {
					workingSets= null;
				}
			}
		}
		try {
			int scope= settings.getInt(SETTINGS_SCOPE); 
			boolean ignoreCase= settings.getBoolean(SETTINGS_IGNORECASE); 
            int idDisplayStyleForQuery= settings.getInt(SETTINGS_ID_DISPLAY_STYLE_FOR_QUERY); 
            String idDisplayStyleLanguageForQuery= idDisplayStyleForQuery == 4?settings.get(SETTINGS_ID_DISPLAY_STYLE_LANGUAGE_FOR_QUERY):null; 
            int searchFlags= settings.getInt(SETTINGS_SEARCHFLAGS); 

			return	new SearchPatternData(textPattern, ignoreCase, idDisplayStyleForQuery, idDisplayStyleLanguageForQuery, searchFlags, scope, projects, workingSets);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * @return Returns the ignoreCase value.
	 */
	public boolean isIgnoreCase() {
		return _ignoreCase;
	}

    /**
     * @return Returns the scope.
     */
    public int getSearchFlags() {
        return _searchFlags;
    }
    
    /**
     * @return Returns the scope.
     */
    public int getIDDisplayStyle() {
        return _idDisplayStyleForQuery;
    }

	/**
	 * @return Returns the scope.
	 */
	public int getScope() {
		return _scope;
	}

	/**
	 * @return Returns the pattern.
	 */
	public String getPattern() {
		return _textPattern;
	}

	/**
	 * @return Returns the working sets.
	 */
	public IWorkingSet[] getWorkingSets() {
		return _workingSets;
	}
	
	/**
	 * @return returns the selected projects
	 */
	public String[] getProjects() {
		return _projectNames;
	}
}
