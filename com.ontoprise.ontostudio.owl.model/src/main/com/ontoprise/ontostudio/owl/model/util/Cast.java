/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.util;

/**
 * This class localizes the unsafe casts to one place, so that unsafe cast
 * warnings are located to one place only.
 */
public class Cast {
    @SuppressWarnings("unchecked")
    public static final <T> T cast(Object o) {
        return (T)o;
    }
}
