/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.natures;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.neontoolkit.core.Messages;
import org.neontoolkit.core.NeOnCorePlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xml.internal.serialize.Method;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.Serializer;
import com.sun.org.apache.xml.internal.serialize.SerializerFactory;

/**
 * This nature handles the properties of an ontology development project in
 * OntoStudio/eclipse. The nature stores the ontologies contained in a project.
 * It also removes the ontology file from the workspace when an ontology is deleted.
 */
public class OntologyProjectNature implements IProjectNature {
    /**
     * The id of the ontology nature.
     */
    public static final String ID = "org.neontoolkit.core.natures.ontologyNature"; //$NON-NLS-1$
    /**
     * The id that was used up to OntoStudio 2.1
     */
    public static final String PREVIOUS_ID = "com.ontoprise.ontostudio.datamodel.natures.ontologyNature"; //$NON-NLS-1$
    public static final String PREVIOUS_ID2 = "org.neontoolkit.datamodel.natures.ontologyNature"; //$NON-NLS-1$
    /**
     * The name of the file where the properties are saved.
     */
    public static final String ONTOLOGY_REF_FILENAME = ".ontologies"; //$NON-NLS-1$
    /**
     * The name of the file where the project settings are saved.
     */
    public static final String PROJECT_SETTINGS_FILENAME = "settings.prp"; //$NON-NLS-1$
    /**
     * The default block size.
     */
    private static final int DEFAULT_READING_SIZE = 8192;
    /**
     * tag and attribute names used in the .ontologies file
     */
    private static final String CONTAINER = "container"; //$NON-NLS-1$
    private static final String ONTOLOGY_LANGUAGE = "ontologyLanguage"; //$NON-NLS-1$
    private static final String ONTOLOGY = "ontology"; //$NON-NLS-1$
    private static final String KEY = "id"; //$NON-NLS-1$
    private static final String SHOWIMPORTS = "showImports"; //$NON-NLS-1$

    /**
     * The ids of the ontologies contained in the project.
     */
    private Set<String> _ontologyIds;

    /**
     * The project this nature is belonging to.
     */
    private IProject _project;

    /**
     * This is the id of the factory that created the project with this nature.
     */
    private String _factoryId;
    
    /**
     * A map to store the flag for each ontology indicating whether imported facts
     * should be shown or not.
     */
    private Map<String, Boolean> _showImports = new HashMap<String,Boolean>();

    /**
     * only temporary
     * Returns true if the nature did not already contain the given ontology URI.
     * Otherwise false is returned.
     */
    public boolean addOntology(String ontologyUri) throws CoreException {
        getOntologies();
        if (_ontologyIds.add(ontologyUri)) {
            persistNature();
            return true;
        }
        return false;
    }
    
    public void setShowOntologyImports(String ontologyId, boolean showImports) throws CoreException {
        getOntologies();
        if (_ontologyIds.contains(ontologyId)) {
            _showImports.put(ontologyId, showImports);
            persistNature();
        }
    }

    public void configure() throws CoreException {
    }

    public void deconfigure() throws CoreException {
    }

    public IProject getProject() {
        return _project;
    }
    
    public void setProject(IProject project) {
        this._project = project;
    }

    /**
     * Returns the identifier of the OntologyProject type that is associated
     * with this project
     * @return
     */
    public String getProjectFactoryId() {
        if (_factoryId == null) {
            try {
                decodeNatureContent(restoreNature(ONTOLOGY_REF_FILENAME));
            } catch (CoreException e) {
            }
        }
        if (_factoryId == null || _factoryId.length() == 0) {
            _factoryId = "FLogic";//legacy reasons //$NON-NLS-1$
        }
        return _factoryId;
    }

    /**
     * Sets the identifier of the OntologyProject factory that is created the project
     * with this nature
     * @param id
     */
    public void setProjectFactoryId(String language) throws CoreException {
        _factoryId = language;
        _ontologyIds = new LinkedHashSet<String>();
        persistNature();
    }

    /**
     * Reads the project settings from the file and sets the container and the
     * ontology values. If an ontology no longer exists in the container, it
     * will automatically be removed from the list of ontologies.
     */
    public void decodeNatureContent(String s) {
        try {
            StringReader reader = new StringReader(s);
            Element cpElement;
            try {
                DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                cpElement = parser.parse(new InputSource(reader)).getDocumentElement();
            } catch (SAXException e) {
                throw new IOException(e.getLocalizedMessage());
            } catch (ParserConfigurationException e) {
                throw new IOException(e.getLocalizedMessage()); 
            } finally {
                reader.close();
            }

            if (!cpElement.getNodeName().equalsIgnoreCase(CONTAINER)) {
                throw new IOException("No container definition found"); //$NON-NLS-1$
            }
            _factoryId = cpElement.getAttribute(ONTOLOGY_LANGUAGE);
            NodeList list = cpElement.getElementsByTagName(ONTOLOGY);
            int length = list.getLength();

            _ontologyIds = new HashSet<String>();
            for (int i = 0; i < length; ++i) {
                Node node = list.item(i);
                NamedNodeMap map = node.getAttributes();
                Node id = map.getNamedItem(KEY); 
                Node si = map.getNamedItem(SHOWIMPORTS);
                _ontologyIds.add(id.getNodeValue());
                if(si != null) {
                    _showImports.put(id.getNodeValue(), si.getNodeValue().equals("true")); //$NON-NLS-1$
                }
                else {
                    _showImports.put(id.getNodeValue(), true);
                }
            }
        } catch (Exception e) {
            NeOnCorePlugin.getDefault().logError(Messages.OntologyProjectNature_0, e);
        }
    }

    public String encodeNatureContent(Set<String> ontos, 
            boolean useLineSeparator) {
        Document document = new DocumentImpl();
        Element cpElement = document.createElement(CONTAINER);
        cpElement.setAttribute(ONTOLOGY_LANGUAGE, _factoryId);
        document.appendChild(cpElement);

        // referenced ontologies
        for (Iterator<String> it = ontos.iterator(); it.hasNext();) {
            String ontologyUri = it.next();
            Boolean showImports = _showImports.get(ontologyUri);
            if (showImports == null) {
                showImports = Boolean.TRUE;
            }
            Element oElement = document.createElement(ONTOLOGY);
            oElement.setAttribute(KEY, ontologyUri); 
            oElement.setAttribute(SHOWIMPORTS, String.valueOf(showImports));
            cpElement.appendChild(oElement);
        }
                
        // produce a String output
        try {
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            OutputFormat format = new OutputFormat();
            if (useLineSeparator) {
                format.setIndenting(true);
                format.setLineSeparator(System.getProperty("line.separator")); //$NON-NLS-1$
            } else {
                format.setPreserveSpace(true);
            }
            Serializer serializer = SerializerFactory.getSerializerFactory(Method.XML).makeSerializer(new OutputStreamWriter(s, "UTF-8"), //$NON-NLS-1$
                    format);
            serializer.asDOMSerializer().serialize(document);
            return s.toString("UTF-8"); //$NON-NLS-1$
        } catch (IOException e) {
            throw new RuntimeException(Messages.OntologyProjectNature_2, e); 
        }
    }
    
    private void persistNature() throws CoreException {
        persistNature(ONTOLOGY_REF_FILENAME, encodeNatureContent(_ontologyIds, true));
    }

    /**
     * Stores the content in the file
     * @param filename the name of the file which is located in the project directory
     * @param content the content that should be stored in the file
     * @throws CoreException
     * @throws IOException 
     */
    private void persistNature(String filename, String content) throws CoreException {

        IFile file = getProject().getFile(filename);
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        try {
	        if (file.exists()) {
	            if (file.isReadOnly()) {
	                ResourcesPlugin.getWorkspace().validateEdit(new IFile[] {file}, null);
	            }
	            file.setContents(inputStream, IResource.FORCE, null);
	        } else {
	            file.create(inputStream, IResource.FORCE, null);
	        }
        } finally {
            try {
            	inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Restores the contents of the nature from the given file
     * @param filename the name of the file
     * @return a string containing the contents of the file
     * @throws CoreException
     */
    public String restoreNature(String filename) throws CoreException {

        IFile file = getProject().getFile(filename);
        if (file.exists()) {
            return new String(getResourceContentsAsByteArray(file));
        }
        return null;
    }

    /**
     * Returns an array of all ontologies contained in the project
     */
    public String[] getOntologies() throws CoreException {
        if (_ontologyIds == null) {
            decodeNatureContent(restoreNature(ONTOLOGY_REF_FILENAME));
        }
        return _ontologyIds.toArray(new String[0]);
    }
    
    public boolean getShowOntologyImports(String ontologyId) throws CoreException {
        getOntologies();
        return Boolean.FALSE.equals(_showImports.get(ontologyId));
    }
    
    /**
     * Removes the given ontology from the project. Only the reference to the
     * ontology is deleted, the ontology itself is still contained in the
     * ontology container.
     */
    public boolean removeOntology(String ontologyUri) throws CoreException {
        return removeOntologyIntern(ontologyUri);
    }
    
   private boolean removeOntologyIntern(String ontologyUri) throws CoreException {
       getOntologies();
        Boolean removedShowImports = _showImports.remove(ontologyUri);
        if (removedShowImports == null) {
            removedShowImports = new Boolean(false);
        }
        if (_ontologyIds.remove(ontologyUri) || removedShowImports) {
            persistNature();
            return true;
        }
        return false;
    }
    
    public boolean renameOntology(String oldOntologyUri, String newOntologyUri) throws CoreException {
       if (removeOntologyIntern(oldOntologyUri)){
           _ontologyIds.add(newOntologyUri);
           persistNature();
           return true;
       }
       return false;
    }
    
    /**
     * Returns the contents of the given file as an array of bytes
     * @param file the input file
     * @return a byte array containing the input of the file
     * @throws CoreException
     */
    private static byte[] getResourceContentsAsByteArray(IFile file) throws CoreException {
        InputStream stream = null;
        stream = new BufferedInputStream(file.getContents(true));
        try {
            return getInputStreamAsByteArray(stream, -1);
        } catch (IOException e) {
            throw new CoreException(new Status(Status.ERROR, "", Status.OK, Messages.OntologyProjectNature_1 + file.getName(), e)); //$NON-NLS-1$ 
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Returns the contents of the given input stream as an array of bytes array.
     * If length is >= 0 only the given number of bytes is returned, otherwise all bytes are returned.
     * @param stream
     * @param length
     * @return
     * @throws IOException
     */
    private static byte[] getInputStreamAsByteArray(InputStream stream, int length) throws IOException {
        byte[] contents;
        if (length == -1) {
            contents = new byte[0];
            int contentsLength = 0;
            int amountRead = -1;
            do {
                int amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE); // read
                                                                                          // at
                                                                                          // least
                                                                                          // 8K

                // resize contents if needed
                if (contentsLength + amountRequested > contents.length) {
                    byte[] oldcontents = contents;
                    contents = new byte[contentsLength + amountRequested];
                    System.arraycopy(oldcontents, 0, contents, 0, contentsLength);
                }

                // read as many bytes as possible
                amountRead = stream.read(contents, contentsLength, amountRequested);

                if (amountRead > 0) {
                    // remember length of contents
                    contentsLength += amountRead;
                }
            } while (amountRead != -1);

            // resize contents if necessary
            if (contentsLength < contents.length) {
                byte[] oldcontents = contents;
                contents = new byte[contentsLength];
                System.arraycopy(oldcontents, 0, contents, 0, contentsLength);
            }
        } else {
            contents = new byte[length];
            int len = 0;
            int readSize = 0;
            while ((readSize != -1) && (len != length)) {
                // See PR 1FMS89U
                // We record first the read size. In this case len is the actual
                // read size.
                len += readSize;
                readSize = stream.read(contents, len, length - len);
            }
        }

        return contents;
    }
    
    /**
     * Returns the URI for a given file name within the project
     * @param fileName
     * @return
     */
    //TODO MIKA: Is this method required?
//    public URI getOntologyUri(String fileName) {
//    	IFile file = getProject().getFile(fileName);
//    	URI uri = file.getLocationURI();
//    	return uri;
//    }
    
    //TODO MIKA: Is this method required?
//    public boolean existsFile(String fileName) {
//    	return getProject().getFile(fileName).exists();
//    }
    
    /**
     * returns the uri of all ontology files (except internal files) within the project
     * @return
     * @throws CoreException
     */
    //TODO MIKA: Is this method required?
//    public URI[] getOntologyFiles() throws CoreException {
//    	List<URI> uriList = new ArrayList<URI>();
//    	getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
//    	IResource[] resources = getProject().members(false);
//    	for (IResource resource : resources) {
//    	    if(!isInternalResource(resource) && hasLegalOntologyExtension(resource.getName())) {                 
//				uriList.add(resource.getLocationURI());
//			}
//		}
//    	return uriList.toArray(new URI[0]);
//    }
    
    /**
     * returns true if the resource is known as project configuration file
     * @param resource
     * @return
     */
    //TODO MIKA: Is this method required?
//    public boolean isInternalResource(IResource resource) {
//		String resName = resource.getName();
//		if(resource.getType() != IFile.FILE) {
//			return true;
//		}
//		if (OntologyProjectNature.ONTOLOGY_REF_FILENAME.equals(resName)
//				|| OntologyProjectNature.PROJECT_SETTINGS_FILENAME.equals(resName) 
//				|| resName.equals(".project") //$NON-NLS-1$
//				|| resName.endsWith(".rptdesign")) { //$NON-NLS-1$
//			return true;
//		}
//		return false;
//    }
    
}
