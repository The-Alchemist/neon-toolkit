/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.project;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.exception.ProjectFailureException;
import org.neontoolkit.core.natures.OntologyProjectNature;

/**
 * 
 * @author Mika Maier-Collin
 */
public interface IOntologyProject {
    
    /**
     * Returns an array of all available ontology uris. This list may only differ 
     * from the projects ontologies in persistent datamodels. 
     * @return
     */
    Set<String> getAvailableOntologyURIs() throws NeOnCoreException;
    
    /**
     * Called after the project has been created and added to the list of projects
     * to do some initialization stuff.
     */
    void init();
    
    /**
     * Returns the name of the project.
     * @return
     */
    String getName();
    
    /**
     * Returns the handle to the project resource for the project.
     * @return IProject
     */
    IProject getResource() throws NeOnCoreException;
    
    /**
     * Returns the ontology language of this project.
     * @return
     */
    String getOntologyLanguage();
    
    /**
     * Returns the unique identifier of the factory that created the project.
     * @return
     */
    String getProjectFactoryId();
    /**
     * Returns the configuration of the project. This should contain datamodel
     * specific configuration information. Might be null if the datamodel does
     * not require any configuration.
     * @return
     */
    Properties getConfiguration();
    
    /**
     * Returns true if the underlying datamodel is a persistent datamodel and 
     * does not require explicit save operations.
     * @return
     */
    boolean isPersistent();
    
    /**
     * Disposes this project, cleans up the project and finally deletes the 
     * project resource. For non persistent datamodels, the removeContent
     * flag does not have any effect. For persistent datamodels, all ontologies
     * will be removed from the datamodel if removeContent is true, otherwise
     * the ontologies remain in the datamodel.
     * @param removeContent
     * @throws NeOnCoreException
     */
    void dispose(boolean removeContent) throws NeOnCoreException;
    
    /**
     * Renames the project and the underlying resource.
     * @param newName
     * @throws NeOnCoreException
     */
    void renameProject(String newName) throws NeOnCoreException;
    
    /**
     * Registers an ontology project listener.
     * @param listener
     */
    void addOntologyProjectListener(IOntologyProjectListener listener);
    
    /**
     * Removes a registered listener.
     * @param listener
     */
    void removeOntologProjectListener(IOntologyProjectListener listener);

    /**
     * Adds an ontology to this ontology project. This method does not trigger the
     * real add operation. It should be called after the ontology has been added to the
     * datamodel to update the ontology project.
     * @param ontologyUri
     * @throws NeOnCoreException
     */
    void addOntology(String ontologyUri) throws NeOnCoreException;

    /**
     * Creates a new ontology.
     * 
     * @param ontologyURI                       URI for the newly created ontology.
     * @param defaultNamespace                  Default namespace.
     * @throws NeOnCoreException
     */
    void createOntology(String ontologyURI, String defaultNamespace) throws NeOnCoreException;

    /**
     * Open an ontology from its ontology URI.
     * 
     * @param ontologyURI                       URI of the ontology to open.
     * @throws NeOnCoreException
     */
    void openOntology(String ontologyURI) throws NeOnCoreException;
    
    /**
     * Removes an ontology from the ontology project. For not persistent datamodels, the 
     * removeContent option does not have any effect. For persistent datamodels, the
     * ontology will be erased from the datamodel if removeContent is true, otherwise
     * only the reference in the project will be removed.
     * @param ontologyUri
     * @throws NeOnCoreException
     */
    void removeOntology(String ontologyUri, boolean removeContent) throws NeOnCoreException;

    /**
     * Renames the ontology with the given URI to the given new URI.
     * @param oldOntologyUri
     * @param newOntologyUri
     * @throws NeOnCoreException
     */
    void renameOntology(String oldOntologyUri, String newOntologyUri) throws NeOnCoreException;
    
    /**
     * If the boolean parameter is true, the ontology with the given URI is marked as to
     * be saved. If the flag is false, the ontology is removed from the list of ontologies
     * that contain unsaved changes.
     * @param ontologyUri
     * @param isDirty
     */
    void setOntologyDirty(String ontologyUri, boolean isDirty);
    
    /**
     * Returns true, if the ontology with the given URI contains unsaved changes, false
     * otherwise.
     * @param ontologyUri
     * @return
     */
    boolean isOntologyDirty(String ontologyUri);
    
    /**
     * Returns all ontologies that are contained in this ontology project which have
     * unsaved changes.
     * @return
     */
    String[] getDirtyOntologies() throws NeOnCoreException;

    /**
     * Returns all ontologies that are contained in this ontology project.
     * @return
     */
    String[] getOntologies() throws NeOnCoreException;
    
    /**
     * Restores the given project. This is called during startup to be able to restore
     * the project and its ontologies.
     */
    void restoreProject() throws NeOnCoreException;
    
    /**
     * Triggers a save operation of the ontology with the given URI.
     * @param ontologyUri
     */
    void saveOntology(String ontologyUri) throws NeOnCoreException;
    
    /**
     * Method that should only be called by the system. This method is required to
     * update the ontology project if a rename operation is triggered by the system.
     * @param projectName
     */
    void setName(String projectName);
    
    /**
     * Sets the flag indicating whether imported facts should be shown for the ontology
     * with the given URI.
     * @param ontologyUri
     * @param isVisible
     */
    void setImportsVisible(String ontologyUri, boolean isVisible);
    
    /**
     * Returns true, if the display of imported facts is enabled for the given ontology,
     * false otherwise.
     * @param ontologyUri
     * @return
     */
    boolean isImportsVisible(String ontologyUri);

    /**
     * Returns the OntologyProjectNature of this project
     * @return
     * @throws NeOnCoreException
     */
    OntologyProjectNature getOntologyProjectNature() throws CoreException;
    
    /**
     * Returns an adapter for the given class or null of none exists.
     * @param <T>
     * @param adapterClass
     * @return
     */
    <T> T getAdapter(Class<T> adapterClass);
    
    /**
     * Can be called by plugins to indicate that the project is erroneous.
     * This information can e.g. be used by an implementing class to decorate the
     * project with an error marker.
     * If e is null, any previously stored exception is removed.  
     * @param t
     */
    void setProjectFailure(Exception e);
    
    /**
     * Checks if the project becomes erroneous by the given exception.
     * Marks the project as erroneous if so.
     * 
     * <p>Used by the NTK framework, plugin programmers usually do not need to call this method.</p>
     * 
     * @param e                                 The exception to check.
     * @return                                  <code>e</code>.
     * @throws ProjectFailureException          Thrown if the project has become erroneous.
     */
    NeOnCoreException checkProjectFailure(NeOnCoreException e) throws ProjectFailureException;

    /**
     * Checks if the project becomes erroneous by the given exception.
     * Marks the project as erroneous if so.
     * 
     * <p>Used by the NTK framework, plugin programmers usually do not need to call this method.</p>
     * 
     * @param e                                 The exception to check.
     * @return                                  <code>e</code>.
     * @throws ProjectFailureException          Thrown if the project has become erroneous.
     */
    CommandException checkProjectFailure(CommandException e) throws ProjectFailureException;

    /**
     * Checks if the project becomes erroneous by the given exception.
     * Marks the project as erroneous if so.
     * 
     * <p>Used by the NTK framework, plugin programmers usually do not need to call this method.</p>
     * 
     * @param e                                 The exception to check.
     * @return                                  <code>e</code>.
     * @throws ProjectFailureException          Thrown if the project has become erroneous.
     */
    RuntimeException checkProjectFailure(RuntimeException e) throws ProjectFailureException;

    /**
     * Checks if the project becomes erroneous by the given exception.
     * Marks the project as erroneous if so.
     * 
     * <p>Used by the NTK framework, plugin programmers usually do not need to call this method.</p>
     * 
     * @param e                                 The exception to check.
     * @return                                  <code>e</code>.
     * @throws ProjectFailureException          Thrown if the project has become erroneous.
     */
    Exception checkProjectFailure(Exception e) throws ProjectFailureException;
    
    /**
     * Returns an exception that has been attached to the project by setProjectFailure(e).
     * If no failure has occured, null is returned.
     * @return
     */
    Exception getProjectFailure();
    
    /**
     * Returns the users that are working on the ontology with the given URI.
     * Only valid for collaborative datamodels. All other datamodels should 
     * return null.
     * @param ontologyUri
     * @return
     */
    String[] getUsers(String ontologyUri) throws NeOnCoreException;
    
    /**
     * Wait for pending events. UI events are processed until all pending
     * events have been processed.
     * @throws NeOnCoreException
     */
    void readAndDispatchWhileWaitingForEvents() throws NeOnCoreException;

    /**
     * @return
     * @throws NeOnCoreException 
     */
    List<String> getSupportedOntologyFileFormats() throws NeOnCoreException;

    /**
     * @return
     * @throws  
     */
    String getDefaultOntologyFileFormatExtension();

    /**
     * @param physicalUri
     * @return
     */
    String retrieveOntologyUri(String physicalUri) throws NeOnCoreException;

    /**
     * returns the uri of all ontology files (except internal files) within the project
     * @return
     * @throws CoreException
     */
    URI[] getOntologyFiles() throws CoreException;

    /**
     * Get a new file name from a proposal.
     * 
     * @param filenameProposal                  The proposal.
     * @param fileExtension                     The file extension.
     * @return                                  A file name unique within the project.
     */
    String getNewOntologyFilename(String filenameProposal, String fileExtension);

    /**
     * Get a new file name from a given URI.
     * 
     * @param uri                               The uri.
     * @param fileExtension                     The file extension.
     * @return                                  A file name unique within the project.
     */
    String getNewOntologyFilenameFromURI(String uri, String fileExtension);
    
    boolean existsPhysicalUri(String fileName);
    
    /**
     * Returns the physical uri for an ontology if one exists.
     * 
     * @param ontologyURI                       The ontology URI.
     * @return
     * @throws NeOnCoreException
     */
    String getPhysicalURIForOntology(String ontologyURI) throws NeOnCoreException;
    
    /**
     * Change the place where the ontology is located and therefore saved.
     * 
     * @param ontologyURI                       The ontology URI.
     */
    void setPhysicalURIForOntology(String ontologyURI, String physicalURI) throws NeOnCoreException;
    
    /**
     * Get the URIs of all ontologies directly imported by a given ontology.
     * 
     * @param ontologyURI                       URI of the ontology to get the imported ontology URIs from.
     * @return                                  The URIs of the ontologies imported by <code>ontologyURI</code>.
     */
    Set<String> getImportedOntologyURIs(String ontologyURI) throws NeOnCoreException;
    
    /**
     * Get the URIs of all ontologies (in-)directly imported by a given ontology.
     * 
     * @param ontologyURI                       URI of the ontology to get the imported ontology URIs from.
     * @return                                  The URIs of the ontologies imported by <code>ontologyURI</code>.
     */
    Set<String> getAllImportedOntologyURIs(String ontologyURI) throws NeOnCoreException;
    
    /**
     * Get the URIs of all ontologies (in-)directly importing a given ontology.
     * 
     * @param ontologyURI                       URI of the ontology which is imported.
     * @return                                  The URIs of the ontologies importing <code>ontologyURI</code>.
     */
    Set<String> getAllImportingOntologyURIs(String ontologyURI) throws NeOnCoreException;
    
    /**
     * Set the default namespace for an ontology.
     * 
     * @param ontologyURI                       The ontology URI.
     * @param namespace                         The new default namespace or <code>null</code> to remove the default namespace.
     */
    void setDefaultNamespace(String ontologyURI, String namespace) throws NeOnCoreException;
    
    /**
     * Get the default namespace for an ontology.
     * 
     * @param ontologyURI                       The ontology URI.
     * @return                                  The default namespace or <code>null</code> if none is set.
     * @throws NeOnCoreException
     */
    String getDefaultNamespace(String ontologyURI) throws NeOnCoreException;
    
    /**
     * Set the namespace for a prefix for an ontology.
     * 
     * @param ontologyURI                       The ontology URI.
     * @param prefix                            The prefix. The empty string can be used to set the default namespace.
     * @param namespace                         The new namespace or <code>null</code> to remove the prefix.
     */
    void setNamespacePrefix(String ontologyURI, String prefix, String namespace) throws NeOnCoreException;
    
    /**
     * Get the registered namespace prefixes for an ontology.
     * 
     * <p>If the ontology has a default namespace set, the result will contain the empty string as key identifying the default namespace.</p>
     * 
     * <p>The returned value is a copy and can be freely used.</p>
     * 
     * @param ontologyURI                       The ontology URI.
     * @return                                  A map mapping prefixes to namespaces.
     */
    Map<String,String> getNamespacePrefixes(String ontologyURI) throws NeOnCoreException;

    /**
     * Adds some ontologies to the imported ontologies of an ontology.
     * 
     * @param ontologyURI                       Identifies the ontology to modify.
     * @param ontologyURIsToImport              The ontology URIs to be added to the import.
     */
    void addToImportedOntologies(String ontologyURI, Set<String> ontologyURIsToImport) throws NeOnCoreException;

    /**
     * Removes some ontologies form the imported ontologies of an ontology.
     * 
     * @param ontologyURI                       Identifies the ontology to modify.
     * @param ontologyURIsToImport              The ontology URIs to be removed from the import.
     */
    void removeFromImportedOntologies(String ontologyURI, Set<String> ontologyURIsToImport) throws NeOnCoreException;

    /**
     * @param monitor
     * @throws NeOnCoreException
     */
    void restoreProject(IProgressMonitor monitor) throws NeOnCoreException;
}
