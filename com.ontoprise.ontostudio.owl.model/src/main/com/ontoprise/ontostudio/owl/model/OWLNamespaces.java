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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;


/**
 * This interface contains some well-known namespaces.
 * 
 * <p>An {@link OWLNamespaces} instance allows to register prefix to namespace mappings.
 * Several prefixes may be mapped to the same namespace.</p>
 * 
 * <p>The default namespace is a special namespace within the user scope which should/can be used as "default". 
 * The default namespace is identified by the prefix {@link DEFAULT_NAMESPACE_PREFIX} which is equal to the empty string.</p>
 */
public class OWLNamespaces implements Serializable {
    private static final long serialVersionUID=-158185482289831766L;

    /** The prefix identifying the default namespace. */
    public static final String DEFAULT_NAMESPACE_PREFIX = "";
    /** The namespace for OWL ontologies. */
    public static final String OWL_NS="http://www.w3.org/2002/07/owl#";
    /** The namespace for OWL 1.1 ontologies. */
    public static final String OWL_1_1_NS="http://www.w3.org/2006/12/owl11#";
    /** The namespace for OWL XML syntax. */
    public static final String OWLX_NS="http://www.w3.org/2003/05/owl-xml#";
    /** The namespace for OWL 1.1 XML syntax. */
    public static final String OWL_1_1_XML_NS="http://www.w3.org/2006/12/owl11-xml#";
    /** The namespace for XSD datatypes. */
    public static final String XSD_NS="http://www.w3.org/2001/XMLSchema#";
    /** The namespace for RDF elements. */
    public static final String RDF_NS="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    /** The namespace for RDFS elements. */
    public static final String RDFS_NS="http://www.w3.org/2000/01/rdf-schema#";
    /** The namespace for SWRL elements. */
    public static final String SWRL_NS="http://www.w3.org/2003/11/swrl#";
    /** The namespace for SWRL built-ins. */
    public static final String SWRLB_NS="http://www.w3.org/2003/11/swrlb#";
    /** The namespace for SWRL XML syntax elements. */
    public static final String SWRLX_NS="http://www.w3.org/2003/11/swrlx#";
    /** The namespaces for RULE-ML syntax elements. */
    public static final String RULEML_NS="http://www.w3.org/2003/11/ruleml#";
    /** The namespaces for KAON2 elements. */
    public static final String KAON2_NS="http://kaon2.semanticweb.org/internal#";
    /** The map of well-known namespaces and prefixes. */
    protected static final Map<String,String> s_wellKnownNamespaces=new TreeMap<String,String>();
    static {
        s_wellKnownNamespaces.put("owl",OWL_NS);
        s_wellKnownNamespaces.put("owlx",OWLX_NS);
        s_wellKnownNamespaces.put("xsd",XSD_NS);
        s_wellKnownNamespaces.put("rdf",RDF_NS);
        s_wellKnownNamespaces.put("rdfs",RDFS_NS);
        s_wellKnownNamespaces.put("swrl",SWRL_NS);
        s_wellKnownNamespaces.put("swrlb",SWRLB_NS);
        s_wellKnownNamespaces.put("swrlx",SWRLX_NS);
        s_wellKnownNamespaces.put("ruleml",RULEML_NS);
        s_wellKnownNamespaces.put("kaon2",KAON2_NS);
    }
    /** 
     * The inverse set of well-known namespaces and prefixes. 
     * Implementation note: Using a HashMap to provide a fast test within <code>isWellKnownNamespace</code>.
     * */
    protected static final Map<String,String> s_inverseWellKnownNamespaces=new HashMap<String,String>();
    static {
        for(Entry<String,String> e:s_wellKnownNamespaces.entrySet()) {
            s_inverseWellKnownNamespaces.put(e.getValue(), e.getKey());
        }
    }
    
    private static final String[] ILLEGAL_FOR_OWL = {"lt", "gt", "amp", "apos", "quot"};
    private static final String[] ILLEGAL_FOR_FLOGIC = {"rule", "query", "and", "or", "not", "is", "forall", "exists"};
    private static final String[] ILLEGAL_FOR_FLOGIC2 = {"is", "if", "then", "else", "and", "or", "not", "forall", "exist", "mod"};
    /** The set of reserved namespaces and prefixes. */
    protected static final Set<String> s_reservedPrefixes = new HashSet<String>();
    static {
        for (String s: ILLEGAL_FOR_OWL) {
            s_reservedPrefixes.add(s);
        }
        for (String s: ILLEGAL_FOR_FLOGIC) {
            s_reservedPrefixes.add(s);
        }
        for (String s: ILLEGAL_FOR_FLOGIC2) {
            s_reservedPrefixes.add(s);
        }
    }

    /** The global static instance. */
    public static final OWLNamespaces INSTANCE;
    static {
        OWLNamespaces namespaces=new OWLNamespaces();
        namespaces.registerStandardPrefixes();
        INSTANCE=getImmutable(namespaces);
    }
    public static final OWLNamespaces EMPTY_INSTANCE=getImmutable(new OWLNamespaces());

    /** The map of prefixes to the corresponding URI. */
    protected final Map<String,String> m_namespaceByPrefix;
    /** The map of URIs to prefixes. */
    protected final Map<String,String> m_prefixByNamespace;
    /** The index of the next automatic prefix. */
    protected int m_nextAutomaticPrefix;

    /**
     * Creates an instance of this class not containing any mappings.
     */
    public OWLNamespaces() {
        m_namespaceByPrefix=new TreeMap<String,String>();
        m_prefixByNamespace=new TreeMap<String,String>();
        m_nextAutomaticPrefix=0;
    }
    /**
     * Creates an instance of this class, which is a copy of the argument namespaces object.
     *
     * @param source                                the namespace object whose mappings are copied
     */
    public OWLNamespaces(OWLNamespaces source) {
        m_namespaceByPrefix=new TreeMap<String,String>(source.m_namespaceByPrefix);
        m_prefixByNamespace=new TreeMap<String,String>(source.m_prefixByNamespace);
        m_nextAutomaticPrefix=0;
    }
    /**
     * Registers started prefixes to this object.
     */
    public synchronized void registerStandardPrefixes() {
        for (Map.Entry<String,String> entry : s_wellKnownNamespaces.entrySet())
            registerPrefix(entry.getKey(),entry.getValue());
     }
    /**
     * Sets the default namespace. It is used only in abbreviateAsNamespace method.
     *
     * @param defaultNamespace                     the default namespace, or <code>null</code> if the default namespace should be removed
     */
    public synchronized void setDefaultNamespace(String defaultNamespace) {
        if (defaultNamespace == null) {
            unregisterPrefix(DEFAULT_NAMESPACE_PREFIX);
        } else {
            registerPrefix(DEFAULT_NAMESPACE_PREFIX, defaultNamespace);
        }
    }
    /**
     * Returns the default namespace.
     *
     * @return                                  the default namespace, or <code>null</code> if not set
     */
    public synchronized String getDefaultNamespace() {
        return getNamespaceForPrefix(DEFAULT_NAMESPACE_PREFIX);
    }
    /**
     * Deregisters a prefix.
     *
     * @param prefix                            the prefix of the URI
     */
    public synchronized void unregisterPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(); // note: the default TreeMap<String, String> implementation does not support null values.
        }
        
        String namespace=m_namespaceByPrefix.remove(prefix);
        if (namespace != null) {
            m_prefixByNamespace.remove(namespace);
            if (m_namespaceByPrefix.containsValue(namespace)) {
                for (Map.Entry<String,String> entry: m_namespaceByPrefix.entrySet()) {
                    if (!DEFAULT_NAMESPACE_PREFIX.equals(entry.getKey()) && namespace.equals(entry.getValue())) {
                        m_prefixByNamespace.put(namespace, entry.getKey());
                        break;
                    }
                }
            }
		}
    }
    /**
     * Registers a prefix for the URI.
     *
     * @param prefix                            the prefix of the URI
     * @param namespace                         the namespace URI
     */
    public synchronized void registerPrefix(String prefix,String namespace) {
        if (prefix == null) {
            throw new IllegalArgumentException(); // note: the default TreeMap<String, String> implementation does not support null values.
        }
        if (namespace == null) {
            throw new IllegalArgumentException();
        }
        
        m_namespaceByPrefix.put(prefix,namespace);
        if (!DEFAULT_NAMESPACE_PREFIX.equals(prefix)) {
            m_prefixByNamespace.put(namespace,prefix);
        }
    }

    /**
     * Checks is a prefix is allowed for the given ontology language.<br>
     * E.g., in F-logic and ObjectLogic reserved words are forbidden and a prefix
     * must have the format [A-Za-z][A-Za-z_0-9]* 
     * 
     * @param prefix prefix to check
     * @param language ontology language (if null all restrictions are for languages are checked)
     * @return true if prefix can be used
     */
    public static boolean isValidPrefix(String prefix) {
        if (prefix == null) {
            return false;
        }
        if (prefix.length() == 0) {
            return true; // default prefix
        }

        return isValidXMLPrefix(prefix) && !isReservedOwlPrefix(prefix);
    }
    
    private static boolean isValidXMLPrefix(String prefix) {
        // see http://www.w3.org/TR/REC-xml-names/#NT-NCNameChar
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            if (!Character.isLetter(ch) && ch != '_') {
                if (i == 0) {
                    return false;
                }
                // Checking for XML NameStartChar - ':'
                // Allowed characters according to XML:
                // [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
                // plus
                // "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
                if (!(ch >= 0xC0 && ch <= 0xD6)
                        && !(ch >= 0xD8 && ch <= 0xF6)
                        && !(ch >= 0xF8 && ch <= 0x2FF)
                        && !(ch >= 0x370 && ch <= 0x37D)
                        && !(ch >= 0x37F && ch <= 0x1FFF)
                        && !(ch >= 0x200C && ch <= 0x200D)
                        && !(ch >= 0x2070 && ch <= 0x218F)
                        && !(ch >= 0x2C00 && ch <= 0x2FEF)
                        && !(ch >= 0x3001 && ch <= 0xD7FF)
                        && !(ch >= 0xF900 && ch <= 0xFDCF)
                        && !(ch >= 0xFDF0 && ch <= 0xFFFD) 
                        && ch != '-' && ch != '.' && ch != 0xbf
                        && ch != 0x203f && ch != 0x2040
                        && !(ch >= '0' && ch <= '9')
                        && !(ch >= 0x300 && ch <= 0x036f)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean isReservedOwlPrefix(String prefix) {
        final String lower = prefix.toLowerCase();
        if (s_reservedPrefixes.contains(lower)) {
            for (String reserved : ILLEGAL_FOR_OWL) {
                if (lower.equals(reserved)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Returns the namespace URI for the given prefix.
     *
     * @param prefix                            the prefix
     * @return                                  the namespace URI for the prefix (or <code>null</code> if the namespace for the prefix is not registered)
     */
    public synchronized String getNamespaceForPrefix(String prefix) {
        return m_namespaceByPrefix.get(prefix);
    }
    /**
     * Returns a (non default) prefix for the given namespace URI if one exists.
     * 
     * <p>The method will return <code>null</code> 
     * if <code>namespace</code> is the default namespace but no (non default) prefix is registered with <code>namespace</code>.
     * Thereby the returned value can be <code>null</code> but never {@link DEFAULT_NAMESPACE_PREFIX}, i.e. the empty string.</p>
     * 
     * @param namespace                         the namespace URI
     * @return                                  the prefix for the namespace URI or <code>null</code>, if no non default prefix is set for <code>namespace</code>
     */
    public synchronized String getPrefixForNamespace(String namespace) {
        return m_prefixByNamespace.get(namespace);
    }
    /**
     * Returns the prefix used to abbreviate the URI.
     *
     * @param uri                               the URI
     * @return                                  the prefix, or <code>null</code> if the URI cannot be abbreviated
     */
    public synchronized String getAbbreviationPrefix(String uri) {
        String namespace=guessNamespace(uri);
        if (namespace==null)
            return null;
        else
            return getPrefixForNamespace(namespace);
    }
    /**
     * Abbreviates given URI into the form prefix:local_name if possible.
     *
     * @param uri                               the URI
     * @return                                  the abbreviated form, or the original URI if abbreviation is not possible
     */
    public synchronized String abbreviateAsNamespace(String uri) {
        int namespaceEnd=guessNamespaceEnd(uri);
        if (namespaceEnd<0)
            return abbreviateAsNamespace(null,uri);
        else
            return abbreviateAsNamespace(uri.substring(0,namespaceEnd+1),uri.substring(namespaceEnd+1));
    }
    /**
     * Abbreviates given namespace URI and local name into the form prefix:local_name if possible.
     *
     * @param namespace                         the namespace (can be <code>null</code>)
     * @param localName                         the local name 
     * @return                                  the abbreviated form, or namespace+localName if abbreviation is not possible
     */
    public synchronized String abbreviateAsNamespace(String namespace,String localName) {
        if (namespace==null)
            return localName;
        if (namespace.equals(getDefaultNamespace()))
            return localName;
        String prefix=getPrefixForNamespace(namespace);
        if (prefix==null)
            return namespace+localName;
        else
            return prefix+":"+localName;
    }
    /**
     * Abbreviates given URI into the form prefix:local_name if possible.
     *
     * @param uri                               the URI
     * @return                                  the abbreviated form, or the original URI if abbreviation is not possible
     */
    public synchronized String abbreviateAsNamespaceNoDefault(String uri) {
        int namespaceEnd=guessNamespaceEnd(uri);
        if (namespaceEnd<0)
            return abbreviateAsNamespaceNoDefault(null,uri);
        else
            return abbreviateAsNamespaceNoDefault(uri.substring(0,namespaceEnd+1),uri.substring(namespaceEnd+1));
    }
    /**
     * Abbreviates given URI into the form prefix:local_name if possible.
     *
     * @param namespace                         the namespace (can be <code>null</code>)
     * @param localName                         the local name 
     * @return                                  the abbreviated form, or namespace+localName if abbreviation is not possible
     */
    public synchronized String abbreviateAsNamespaceNoDefault(String namespace,String localName) {
        if (namespace==null)
            return localName;
        String prefix=getPrefixForNamespace(namespace);
        if (prefix==null)
            return namespace+localName;
        else
            return prefix+":"+localName;
    }
    /**
     * Abbreviates given URI into the form &prefix;local_name if possible.
     *
     * @param uri                               the URI
     * @return                                  the abbreviated form, or the original URI if abbreviation is not possible
     */
    public synchronized String abbreviateAsEntity(String uri) {
        int namespaceEnd=guessNamespaceEnd(uri);
        if (namespaceEnd<0)
            return abbreviateAsEntity(null,uri);
        else
            return abbreviateAsEntity(uri.substring(0,namespaceEnd+1),uri.substring(namespaceEnd+1));
    }
    /**
     * Abbreviates given URI into the form &prefix;local_name if possible.
     *
     * @param namespace                         the namespace (can be <code>null</code>)
     * @param localName                         the local name 
     * @return                                  the abbreviated form, or namespace+localName if abbreviation is not possible
     */
    public synchronized String abbreviateAsEntity(String namespace,String localName) {
        if (namespace==null)
            return localName;
        String prefix=getPrefixForNamespace(namespace);
        if (prefix==null)
            return namespace+localName;
        else
            return "&"+prefix+";"+localName;
    }
    /**
     * Attempts to expand given string (either of the form prefix:local_name or of the form &prefix;local_name) into an URI.
     * If the input is not of the above form, the method assumes that the default namespace should be used to expand the string. 
     *
     * @param string                            the string
     * @return                                  the expanded URI
     */
    public synchronized String expandString(String string) {

    	if (string.length()>0 && string.charAt(0)=='&') {
            int lastSemicolonPosition=string.lastIndexOf(';');
            if (lastSemicolonPosition>=0) {
                String prefix=string.substring(1,lastSemicolonPosition);
                String namespace=getNamespaceForPrefix(prefix);
                if (namespace!=null)
                    return namespace+string.substring(lastSemicolonPosition+1);
            }
            return string;
        }
        
        int lastColonPosition=string.lastIndexOf(':');
        if (lastColonPosition>=0) {
            String prefix=string.substring(0,lastColonPosition);
            String namespace=getNamespaceForPrefix(prefix);
            if (namespace!=null)
                return namespace+string.substring(lastColonPosition+1);
            return string;
        }
        
        String defaultNamespace=getDefaultNamespace();
        if(defaultNamespace!=null) {
            return defaultNamespace+string;
        }
        
        return string;
    }
    /**
     * Returns the iterator of all prefixes.
     *
     * @return                                  all prefixes
     */
    public synchronized Iterator<String> prefixes() {
        return Collections.unmodifiableSet(m_namespaceByPrefix.keySet()).iterator();
    }
    /**
     * Makes sure that a prefix for given uri exists. If a prefix for this URI does not exist,
     * a new prefix is generated.
     *
     * @param uri                               the URI
     * @return                                  the prefix (<code>null</code> if the URI does not have a namespace)
     */
    public synchronized String ensureNamespacePrefixExists(String uri) {
        String namespace=guessNamespace(uri);
        String prefix=null;
        if (namespace!=null && namespace.length()!=0) {
            prefix=getPrefixForNamespace(namespace);
            if (prefix==null) {
                for (Map.Entry<String,String> entry : s_wellKnownNamespaces.entrySet())
                    if (entry.getValue().equals(namespace) && getNamespaceForPrefix(entry.getKey())==null) {
                        prefix=entry.getKey();
                        break;
                    }
                if (prefix==null)
                    do {
                        prefix=getNextNamespacePrefix();
                    } while (getNamespaceForPrefix(prefix)!=null || s_wellKnownNamespaces.containsKey(prefix) || s_reservedPrefixes.contains(prefix));
                registerPrefix(prefix,namespace);
            }
        }
        return prefix;
    }
    /**
     * Returns the next new namespace prefix.
     *
     * @return                                  the next new namespace prefix
     */
    protected String getNextNamespacePrefix() {
        StringBuffer buffer=new StringBuffer();
        int index=m_nextAutomaticPrefix++;
        do {
            buffer.append((char)('a'+(index % 26)));
            index=index/26;
        } while (index!=0);
        return buffer.toString();
    }
    /**
     * Returns the index of the last character of the namespace.
     *
     * @param uri                               the URI of the namespace
     * @return                                  the index of the last characted of the namespace
     */
    public static int guessNamespaceEnd(String uri) {
        int index = uri.lastIndexOf('#');
        if (index >= 0)
            return index;
        for (int i=uri.length()-1;i>=0;i--) {
            char c=uri.charAt(i);
            if (c==':')
                return i;
            if (c=='/') {
                // CUE after switching to new F-logic this method should be removed (see issue #10091) 
                if (i>0 && uri.charAt(i-1)=='/')
                    return -1;
                return i;
            }
        }
        return -1;
    }

    public static int guessNamespaceEnd2(String uri) {
        int index = uri.lastIndexOf('#');
        if (index >= 0)
            return index;
        for (int i=uri.length()-1;i>=0;i--) {
            char c = uri.charAt(i);
            if (c == ':' || c == '/')
                return i;
        }
        return -1;
    }
    
    /**
     * Guesses a namespace prefix of a URI.
     *
     * @param uri                               the URI for which the namespace prefix is guessed
     * @return                                  the namespace prefix or <code>null</code> if the prefix cannot be guessed
     */
    public static String guessNamespace(String uri) {
        int index=guessNamespaceEnd(uri);
        return index>=0 ? uri.substring(0,index+1) : null;
    }
    /**
     * Guesses the local name of a URI.
     *
     * @param uri                               the URI for which the local name is guessed
     * @return                                  the local name or the whole URI if the local name cannot be guessed
     */
    public static String guessLocalName(String uri) {
        return uri.substring(guessNamespaceEnd(uri)+1);
    }
    /**
     * Returns the immutable namespaces for the given namespace object.
     * 
     * @param namespaces                        the namespaces object
     * @return                                  the immutable namespaces object
     */
    public static OWLNamespaces getImmutable(OWLNamespaces namespaces) {
        return new ImmutableNamespaces(namespaces);
    }
    
    protected static class ImmutableNamespaces extends OWLNamespaces {
        private static final long serialVersionUID=-6871335627786888403L;

        public ImmutableNamespaces(OWLNamespaces namespaces) {
            super(namespaces);
        }
        @Override
        public void registerPrefix(String prefix,String namespace) {
            throw new UnsupportedOperationException("The global Namespaces instance cannot be changed.");
        }
        @Override
        public void unregisterPrefix(String prefix) {
            throw new UnsupportedOperationException("The global Namespaces instance cannot be changed.");
        }
    }
    
    /**
     * Test if a namespace is well known.
     * @param uri The namespace to test.
     * @return <code>true</code> iff <code>namespace</code> is one of the declared namespaces by this class.
     */
    public static boolean isWellKnownNamespace(String uri) {
        return s_inverseWellKnownNamespaces.containsKey(uri);
    }
    
    /**
     * Checks all prefix if they are valid
     * @param language
     * @throws NeOnCoreException if Namespaces contains invalid prefices for given ontology language
     */
    public void checkValidPrefices() throws NeOnCoreException {
        for (Iterator<String> it = prefixes(); it.hasNext(); ) {
            String prefix = it.next();
            if (!isValidPrefix(prefix)) {
                throw new InternalNeOnException("Invalid prefix " + prefix + " for " + "OWL");
            }
        }
        
    }
}
