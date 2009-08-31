/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model;

import com.ontoprise.ontostudio.owl.model.util.Filter;

/**
 * A filter which checks the type of an item.
 * 
 * @author krekeler
 * 
 * @param <E>
 */
public class TypeFilter<E> implements Filter<E> {

    protected Class<?> _requiredType;

    public TypeFilter(Class<?> requiredType) {
        if (_requiredType == null) {
            throw new IllegalArgumentException();
        }
        _requiredType = requiredType;
    }

    public boolean matches(E item) {
        return _requiredType.isInstance(item);
    }

}
