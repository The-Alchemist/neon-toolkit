/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 09.07.2009
 * Created by: janiko
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.util;

/**
 * @author janiko
 *
 */
public class UnknownDatatypeException extends IllegalArgumentException{
    /**
     * @param string
     */
    public UnknownDatatypeException(String string) {
        super(string);
    }

    private static final long serialVersionUID = 1L;

}
