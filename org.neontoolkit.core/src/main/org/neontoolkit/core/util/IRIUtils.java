/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 14.07.2009
 * Created by: Werner
 ******************************************************************************/
package org.neontoolkit.core.util;


/**
 * @author Werner
 *
 */
public class IRIUtils {

    public static final String TURTLE_IRI_CLOSE = ">"; //$NON-NLS-1$
    public static final String TURTLE_IRI_OPEN = "<"; //$NON-NLS-1$

    public static String ensureValidIdentifierSyntax(String identifier) {
        String localUri = ""; //$NON-NLS-1$
        if (identifier.startsWith(TURTLE_IRI_OPEN) && identifier.endsWith(TURTLE_IRI_CLOSE)) {
            localUri = identifier.substring(1, identifier.lastIndexOf(TURTLE_IRI_CLOSE));
        } else {
            localUri = identifier;
        }
        return localUri;
    }

    public static String ensureValidIRISyntax(String uri) {
        String result = uri;
        if (!uri.startsWith(TURTLE_IRI_OPEN)) {
            result = TURTLE_IRI_OPEN + uri;
        }
        if (!result.endsWith(TURTLE_IRI_CLOSE)) {
            result = result + TURTLE_IRI_CLOSE;
        }
        return result;
    }
    
}
