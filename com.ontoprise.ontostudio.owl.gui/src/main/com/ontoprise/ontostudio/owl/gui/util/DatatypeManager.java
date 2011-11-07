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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ontoprise.ontostudio.owl.gui.Messages;

/**
 * This is the registry of all datatypes. Applications with custom datatypes should register
 * appropriate datatype handlers with this class.
 * 
 * @author Nico Stieler
 */

public class DatatypeManager {

    public static final DatatypeManager INSTANCE = new DatatypeManager();

    static {
        BuiltInDatatypes.registerBuiltInDatatypes();
    }
    
    /** The map of all registered handlers indexed by the datatype URI. */
    protected final Map<String,DatatypeHandler> m_handlersByDatatypeURI;


    protected DatatypeManager() {
        m_handlersByDatatypeURI=new HashMap<String,DatatypeHandler>();
    }
    
    public synchronized void registerDatatypeHandler(DatatypeHandler datatypeHandler) {
        registerDatatypeHandler(datatypeHandler.getDatatypeURI(),datatypeHandler);
    }

    public synchronized void registerDatatypeHandler(String datatypeUri, DatatypeHandler datatypeHandler) {
        m_handlersByDatatypeURI.put(datatypeUri,datatypeHandler);
    }
    
    public synchronized void unregisterDatatypeHandler(String datatypeUri) {
        m_handlersByDatatypeURI.remove(datatypeUri);
    }
    
    public synchronized DatatypeHandler getRegisteredDatatypeHandler(String datatypeUri) {//NICO synchronized??
        return m_handlersByDatatypeURI.get(datatypeUri);
    }
    
    public synchronized Set<String>getSupportedDatatypeURIs() {
        return m_handlersByDatatypeURI.keySet();
    }

    public synchronized Object parseObject(String objectValue,String datatypeURI) throws IllegalArgumentException, UnknownDatatypeException {
        DatatypeHandler handler=m_handlersByDatatypeURI.get(datatypeURI);
        if (handler==null)
            throw new UnknownDatatypeException(Messages.InputVerifier_0+" "+datatypeURI); //$NON-NLS-1$
        return handler.parseObject(objectValue);
    }
}
