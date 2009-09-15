/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model;

import com.ontoprise.ontostudio.owl.model.util.Cast;

public class LocatedItemCore<E> implements LocatedItem<E> {

    private String _ontologyUri;
    private E _item;

    public LocatedItemCore(E item, String ontologyUri) {
        _item = item;
        _ontologyUri = ontologyUri;
    }

    public E getItem() {
        return _item;
    }

    public String getOntologyURI() {
        return _ontologyUri;
    }

    @Override
    public boolean equals(Object rhs) {
        if (!(rhs instanceof LocatedItemCore)) {
            return false;
        }
        LocatedItemCore<E> x = Cast.cast(rhs);
        return ((_item == null) ? x._item == null : _item.equals(x._item)) && ((_ontologyUri == null) ? x._ontologyUri == null : _ontologyUri.equals(x._ontologyUri));
    }
}
