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

import java.util.List;


/**
 * Simple reimplementation of com.ontoprise.indexer.owl.SearchResults to be independent of datamodel
 * @author janiko
 *
 */
public class SearchResults {
    private int _start;
    private int _totalCount;
    private List<SearchElement> _resultList;
    
    public SearchResults(int start, int totalCount, List<SearchElement> resultList) {
        _start = start;
        _totalCount = totalCount;
        _resultList = resultList;
    }
    
    /**
     * @return start index of results (0 for the first result page)
     */
    public int getStart() {
        return _start;
    }
    
    /**
     * @return count of results
     */
    public int getCount() {
        return _resultList.size();
    }
    
    /**
     * @return total count of index query, useful for paging
     */
    public int getTotalCount() {
        return _totalCount;
    }

    /**
     * @return true if there are more results than returned in this batch
     */
    public boolean hasMoreResults() {
        return _totalCount > _start + getCount();
    }
    
    /**
     * @return list of SearchElements
     */
    public List<SearchElement> getResults() {
        return _resultList;
    }
}
