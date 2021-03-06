/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util;


/**
 * @author janiko
 *
 */
public interface DatatypeHandler {
    public String getDatatypeURI();
    public Object parseObject(String objectValue) throws IllegalArgumentException;
}
