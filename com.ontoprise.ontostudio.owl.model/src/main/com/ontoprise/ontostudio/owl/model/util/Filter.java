/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.util;

/**
 * Provides a single method which tells the caller if an item applies to the filter which is represented by an instance of this interface.
 * 
 * @author krekeler
 * 
 * @param <E>
 */
public interface Filter<E> {
    public boolean matches(E item);
}
