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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.search.Messages;
import org.neontoolkit.search.ui.SearchMatch;

import com.ontoprise.ontostudio.search.owl.match.ITreeObject;
import com.ontoprise.ontostudio.search.owl.match.TreeParent;


/* 
 * Created on: 28.04.2005
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

public class SearchTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
//		if (element instanceof SearchMatch) {
//			return ((SearchMatch)element).getImage();
//		}
//		return null;
	    return getImage(element);
	}
	
	@Override
    public Image getImage(Object element){

        if (element instanceof SearchMatch) {
            return ((SearchMatch)element).getImage();
        }
        if (element instanceof ITreeObject) {
            return ((ITreeObject)element).getImage();
        }
        return null;
	    
	}

    /**
     * for TableSearch
     */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
        StringBuffer s = new StringBuffer(element.toString()); //content
        if (element instanceof SearchMatch) {
            int results = ((SearchMatch)element).getOccurenceCount();
            s.append("("); //$NON-NLS-1$
            s.append(results);
            if (results == 1) {
                s.append(Messages.SearchTableLabelProvider_1); //match
            }
            else {
                s.append(Messages.SearchTableLabelProvider_2); //matches
            }
            s.append(")"); //$NON-NLS-1$
        }
        return s.toString();
	}
	/**
	 * for TreeSearch
	 */
	@Override
    public String getText(Object element){
        StringBuffer s = new StringBuffer(element.toString()); //content
//        System.out.println(element.toString());
//        if(element instanceof OwlSearchMatch){
//            OwlSearchMatch sM = (OwlSearchMatch) element;
//            
//            if(sM.getMatch() instanceof TreeElement){
//                String projectId = ((OwlSearchMatch) element).getProjectId();
//                if(projectId != null){
//                    try {
//                    InternalParser intPars = new InternalParser(element.toString(),OWLNamespaces.EMPTY_INSTANCE,OWLModelFactory.getOWLDataFactory(projectId));
//                    OWLAxiom out = intPars.parseOWLAxiom();
//                    System.out.println(out);
//                    } catch (InternalParserException e) {
//                        e.printStackTrace();
//                    } catch (NeOnCoreException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
        if(element instanceof TreeParent){
            int results = ((TreeParent)element).numberOfLeafs();
            s.append(" ("); //$NON-NLS-1$
            s.append(results);
            if (results == 1) {
                s.append(Messages.SearchTableLabelProvider_1); //match
            }
            else {
                s.append(Messages.SearchTableLabelProvider_2); //matches
            }
            s.append(")"); //$NON-NLS-1$
        }
//        else{
//            if (element instanceof ITreeObject) {
////                int results = ((ITreeObject)element).numberOfLeafs();
////                s.append("("); //$NON-NLS-1$
////                s.append(results);
////                if (results == 1) {
////                    s.append(Messages.SearchTableLabelProvider_1); //match
////                }
////                else {
////                    s.append(Messages.SearchTableLabelProvider_2); //matches
////                }
////                s.append(")"); //$NON-NLS-1$
//            }
//        }
        return s.toString();
	}
}
