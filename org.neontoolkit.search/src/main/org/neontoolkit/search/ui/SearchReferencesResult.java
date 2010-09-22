package org.neontoolkit.search.ui;

/**
 * @author Nico Stieler
 * Created on: 21.09.2010
 */
public class SearchReferencesResult extends SearchResult {

    /**
     * @param query
     */
    public SearchReferencesResult(AbstractSearchQuery query) {
        super(query);
    }

    public String getLabel() {
        int matchCount = getMatchCount();
        String label = "\"" + _query.getExpression() + "\" - " + matchCount + " Reference"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (matchCount != 1) {
            label += "s"; //$NON-NLS-1$
        }
        label += " found."; //$NON-NLS-1$
        return label;
    }

}
