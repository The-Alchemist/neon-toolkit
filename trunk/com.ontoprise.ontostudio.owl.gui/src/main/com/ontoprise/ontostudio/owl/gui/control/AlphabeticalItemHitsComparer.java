/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.control;

import java.util.Comparator;

import org.neontoolkit.gui.NeOnUIPlugin;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.util.Cast;

@SuppressWarnings("unchecked")
public class AlphabeticalItemHitsComparer<A extends ItemHits> implements Comparator<ItemHits> {

    private final OWLModel _owlModel;
    private OWLObjectVisitorEx _visitor;

    /**
	 * 
	 */
    public AlphabeticalItemHitsComparer(OWLModel owlModel) {
        _owlModel = owlModel;
        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        _visitor = manager.getVisitor(_owlModel, idDisplayStyle);
        
    }

    public int compare(ItemHits o1, ItemHits o2) {
        if (o1.getItem() instanceof OWLObject && o2.getItem() instanceof OWLObject) {
            OWLObject entity1 = Cast.cast(o1.getItem());
            String[] result1 = (String[]) entity1.accept(_visitor);
            OWLObject entity2 = Cast.cast(o2.getItem());
            String[] result2 = (String[]) entity2.accept(_visitor);
            return result1[0].compareTo(result2[0]);
        } else {
            return 0;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
