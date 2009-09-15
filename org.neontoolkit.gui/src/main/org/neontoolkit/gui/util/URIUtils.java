/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.util;


import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.IInputValidator;
import org.neontoolkit.core.command.ontology.CreateValidOntologyUri;
import org.neontoolkit.gui.Messages;

import com.sun.org.apache.xml.internal.utils.URI;
import com.sun.org.apache.xml.internal.utils.URI.MalformedURIException;

 
/* 
 * Created on: 26.03.2007
 * Created by: Dirk Wenke
 *
 * Function: UI, Utilities
 */
/**
 * Utility class that provides input validation of URIs.
 */
public class URIUtils {

	public static IInputValidator getInputValidator() {
		return new IInputValidator() {
			public String isValid(String newText) {
				try {
					new URI(newText);
					return null;
				} catch (MalformedURIException e) {
					return e.getLocalizedMessage();
				}
			}
		};
	}
	
	public static IInputValidator getOntologyUriValidator() {
	    return new IInputValidator() {
	        @Override
	        public String isValid(String newText) {
	            try {
	                new CreateValidOntologyUri(newText).getOntologyUri();
	                return null;
	            } catch (Exception e) {
	                if (e.getCause() != null && e.getCause() instanceof URISyntaxException) {
	                    return Messages.URIUtils_12 + " " + e.getCause().getMessage(); //$NON-NLS-1$ 
	                } else {
	                    return Messages.URIUtils_13; 
	                }
	            }
	        }
	    };
	}
	
	public static IInputValidator getUriValidator(final String generalErrorMessage) {
        return new IInputValidator() {
            @Override
            public String isValid(String newText) {
                try {
                    new CreateValidOntologyUri(newText).getOntologyUri();
                    return null;
                } catch (Exception e) {
                    if (e.getCause() != null && e.getCause() instanceof URISyntaxException) {
                        return generalErrorMessage + " " + e.getCause().getMessage(); //$NON-NLS-1$ 
                    } else {
                        return generalErrorMessage; 
                    }
                }
            }
        };
	}
	
	/**
	 * Returns null if URI is valid, error message otherwise.
	 * @return
	 */
	public static String isValidURI(String uri) {
		try {
			new URI(uri);
			return null;
		} catch (MalformedURIException e) {
			return e.getLocalizedMessage();
		}
	}

    public static String validateNamespace(String input, IInputValidator validator) {
        String message = validator.isValid(input);
        if (message == null) {
            if (!(input.endsWith("/") || (input.endsWith(":") || (input.endsWith("#"))))) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                message = Messages.URIUtils_0;
            } else {
                java.net.URI uri;
                try {
                    uri = new java.net.URI(input);
                    String fragment = uri.getFragment();
                    // fragment should be empty or no fragment defined
                    if (fragment != null && fragment.trim().length() > 0) {
                        message = "Invalid Default Namespace:" + " - " + Messages.URIUtils_2; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } catch (URISyntaxException e) {
                    return e.getMessage();
                }
            }
        }
        return message;
    }
    
}
