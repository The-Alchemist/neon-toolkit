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

import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.search.ui.AbstractSearchPage;
import org.neontoolkit.search.ui.Scope;
import org.neontoolkit.search.ui.SearchPageOption;
import org.neontoolkit.search.ui.SearchPatternData;

import com.ontoprise.ontostudio.owl.gui.Messages;

/* 
 * Created on 04.04.2008
 * @author Dirk Wenke
 *
 * Function:
 * Keywords:
 */
/**
 * Type comment
 * @author Dirk Wenke
 * @author Nico Stieler
 */
public class OwlSearchPage extends AbstractSearchPage {

    /**
	 * 
	 */
    public OwlSearchPage() {
        super(new SearchPageOption[] {new SearchPageOption(Messages.OwlSearchPage_0, OWLSearchFlags.OWL_CLASS_SEARCH_FLAG), 
                new SearchPageOption(Messages.OwlSearchPage_1, OWLSearchFlags.OWL_OBJECT_PROPERTY_SEARCH_FLAG), 
                new SearchPageOption(Messages.OwlSearchPage_7, OWLSearchFlags.OWL_INDIVIDUAL_SEARCH_FLAG), 
                new SearchPageOption(Messages.OwlSearchPage_3, OWLSearchFlags.OWL_ANNOTATION_PROPERTY_SEARCH_FLAG), 
                new SearchPageOption(Messages.OwlSearchPage_4, OWLSearchFlags.OWL_DATATYPE_SEARCH_FLAG), 
                new SearchPageOption(Messages.OwlSearchPage_2, OWLSearchFlags.OWL_DATA_PROPERTY_SEARCH_FLAG), 
                new SearchPageOption(Messages.OwlSearchPage_5, OWLSearchFlags.OWL_DATA_PROPERTY_VALUES_SEARCH_FLAG), 
                new SearchPageOption(Messages.OwlSearchPage_6, OWLSearchFlags.OWL_ANNOTATION_VALUES_SEARCH_FLAG),}); 
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.ui.AbstractSearchPage#getSearchQuery()
     */
    @Override
    protected ISearchQuery getSearchQuery() {
        SearchPatternData patternData = getPatternData();
        return new OwlSearchQuery(patternData.getPattern(), patternData.isIgnoreCase(), patternData.getSearchFlags(), getScope());
    }
    
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.ontoprise.ontostudio.search.ui.AbstractSearchPage#getProjectsInScope()
//     */
//    @Override
//    protected String[] getProjectsInScope() {
//        return super.getProjectsInScope();
//        try {
//            String[] projects = OWLPlugin.getDefault().getOntologyProjects();
//            List<String> list = new ArrayList<String>();
//            for (String projectName: projects) {
//                IProject project = NeOnCorePlugin.getDefault().getProject(projectName);
//                if (project.isOpen() && project.hasNature(OntologyProjectNature.ID)) {
//                    list.add(projectName);
//                }
//            }
//            return list.toArray(new String[0]);
//        } catch (CoreException ce) {
//            SearchPlugin.logError(Messages.OwlSearchPage_8, ce); 
//        } catch (NeOnCoreException ce) {
//            SearchPlugin.logError(Messages.OwlSearchPage_8, ce); 
//        }
//        return new String[0];
//    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.ui.AbstractSearchPage#getScope()
     */
    @Override
    protected Scope getScope() {
        return super.getScope();
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.ui.AbstractSearchPage#performAction()
     */
    @Override
    public boolean performAction() {
        return super.performAction();
    }

    @Override
    protected String getDialogSectionName() {
        return "OwlSearchPageOptions"; //$NON-NLS-1$
    }
    
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl() ,IHelpContextIds.OWL_SEARCH);
    }

    @Override
    public String getHelpContextId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getOntologyLanguage() {
       return "OWL2"; //$NON-NLS-1$//NICO search is restricted on OWL2?
    }
}
