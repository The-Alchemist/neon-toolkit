/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.command;

import java.lang.reflect.Array;

/*
 * Created on 10.11.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class ObjectRepresentation {

	public static String toLoggedString(Class<?> type, Object object) {
		if (object instanceof String) {
			return quote(object.toString());
		} else if (type.isArray()) {
			return arrayToLoggedString(object);
		} else {
			return String.valueOf(object);
		}
	}
	
	public static String toJavaAssignmentString(Class<?>type, Object object) {
		if (object instanceof String) {
			return quote(object.toString());
		} else if (type.isArray()) {
			StringBuffer buffer = new StringBuffer("new "); //$NON-NLS-1$
			buffer.append(type.getSimpleName());
			buffer.append(arrayToLoggedString(object));
			return buffer.toString();
		} else {
			return String.valueOf(object);
		}
	}

	private static String arrayToLoggedString(Object object) {
		StringBuffer buffer = new StringBuffer();
        buffer.append("{"); //$NON-NLS-1$
        if (object != null) {
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(object, i);
                if (element == null) {
                    buffer.append(String.valueOf(element));
                } else {
                    if (element.getClass().isArray()) {
                        buffer.append(arrayToLoggedString(element));
                    } else {
                        if (element instanceof String) {
                            buffer.append(quote(element.toString()));
                        } else {
                            buffer.append(element.toString());
                        }
                    }
                }
                if (i < length - 1) {
                    buffer.append(","); //$NON-NLS-1$
                }
            }
        }
        buffer.append("}"); //$NON-NLS-1$
        return buffer.toString();
	}
	
    /**
     * Used to quote an expression. Appends a quotation mark to the beginning 
     * and the end of the input string and returns the result. All inner quotation 
     * marks (") are substituted by escaped qoutes (\"), and all backslashes (\) by
     * escaped backslashes (\\).
     * 
     * @return String
     * @param in  String
     */
    private static String quote(String in) {
        if (in != null) {
            char ch[] = in.toCharArray();
            StringBuffer buf = new StringBuffer("\""); //$NON-NLS-1$
            for (int i = 0; i < ch.length; i++) {
                if (ch[i] == '"' || ch[i] == '\\') {
                    buf.append('\\');
                }
                buf.append(ch[i]);
            }
            buf.append('"');
            return buf.toString();
        } else {
            return "\"\""; //$NON-NLS-1$
        }
    }

}
