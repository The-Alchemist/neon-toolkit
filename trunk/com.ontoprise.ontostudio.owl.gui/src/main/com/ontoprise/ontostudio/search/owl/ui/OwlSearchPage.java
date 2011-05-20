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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.search.ui.AbstractSearchPage;
import org.neontoolkit.search.ui.Scope;
import org.neontoolkit.search.ui.SearchPageOption;
import org.neontoolkit.search.ui.SearchPatternData;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;

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
    public static OwlSearchPage currentSearchPage;
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
        currentSearchPage = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.search.ui.AbstractSearchPage#getSearchQuery()
     */
    @Override
    protected ISearchQuery getSearchQuery() {
        SearchPatternData patternData = getPatternData();
        return new OwlSearchQuery(patternData.getPattern(), patternData.isIgnoreCase(), patternData.getSearchFlags(), patternData.getIDDisplayStyle(), getScope());//NICO change me
    }
    
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

    @Override
    protected void addDisplayStyleSelectionControl(Composite c) {        

        String[] contents = new String[]{
                Messages.OwlSearchPage_DisplayStyle_URI,
                Messages.OwlSearchPage_DisplayStyle_Local,
                Messages.OwlSearchPage_DisplayStyle_QName
//                ,
//                Messages.OwlSearchPage_DisplayStyle_Language
        };
        
        GridData data = new GridData(SWT.RIGHT, SWT.CENTER, true, true, 2, 1);
        data.widthHint = 111;
        data.verticalAlignment = SWT.LEFT;
        final CCombo combo1 = OWLGUIUtilities.createComboWidget(contents, c, data, SWT.BORDER | SWT.READ_ONLY, true);

      String selection = Messages.OwlSearchPage_DisplayStyle_URI;   //      String selection = Messages.OwlSearchPage_DisplayStyle_Language; 
        if(_IDDisplayStyle == NeOnUIPlugin.DISPLAY_LOCAL)
            selection = Messages.OwlSearchPage_DisplayStyle_Local;
        else if(_IDDisplayStyle == NeOnUIPlugin.DISPLAY_URI)
            selection = Messages.OwlSearchPage_DisplayStyle_URI;
        else if(_IDDisplayStyle == NeOnUIPlugin.DISPLAY_QNAME)
            selection = Messages.OwlSearchPage_DisplayStyle_QName;
        for(int index = 0; index < combo1.getItemCount(); index++)
            if(combo1.getItem(index).equals(selection))
                combo1.select(index);
        

//      data = new GridData();
//      data.widthHint = 111;
//      data.verticalAlignment = SWT.LEFT;
//      final CCombo combo2 = OWLGUIUtilities.createComboWidget(NeOnUIPlugin.getDefault().getLanguages(), c, data, SWT.BORDER | SWT.READ_ONLY, false);
//      combo2.setVisible(false);
      combo1.addModifyListener(new ModifyListener() {
        
        @Override
        public void modifyText(ModifyEvent e) {
//            if(combo1.getText().equals(Messages.OwlSearchPage_DisplayStyle_Language)){
//                combo2.setEnabled(true);
//                combo2.setVisible(true);
//            }else{
//                combo2.setEnabled(false);
//                combo2.setVisible(false);
                if(combo1.getText().equals(Messages.OwlSearchPage_DisplayStyle_Local)){
                    System.out.println(Messages.OwlSearchPage_DisplayStyle_Local);
                    _IDDisplayStyle = NeOnUIPlugin.DISPLAY_LOCAL;
                }else if(combo1.getText().equals(Messages.OwlSearchPage_DisplayStyle_URI)){
                    System.out.println(Messages.OwlSearchPage_DisplayStyle_URI);
                    _IDDisplayStyle = NeOnUIPlugin.DISPLAY_URI;
                }else if(combo1.getText().equals(Messages.OwlSearchPage_DisplayStyle_QName)){
                    System.out.println(Messages.OwlSearchPage_DisplayStyle_QName);
                    _IDDisplayStyle = NeOnUIPlugin.DISPLAY_QNAME;
                }
//            }
        }
    });
    
    }
    
}
