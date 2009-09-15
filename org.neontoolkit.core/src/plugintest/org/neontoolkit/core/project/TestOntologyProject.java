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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.AbstractOntologyProject;

/**
 * @author Dirk Wenke
 *
 */
public class TestOntologyProject extends AbstractOntologyProject {
    public static final String LANGUAGE_ID = "org.neontoolkit.core.test.TestOntologyLanguage"; //$NON-NLS-1$

    /**
     * @param name
     */
    public TestOntologyProject(String name) {
        super(name);
    }

    @Override
    public <T> T getAdapter(Class<T> adapterClass) {
        return null;
    }

    @Override
    public String getOntologyLanguage() {
        return LANGUAGE_ID;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void removeOntology(String ontologyUri, boolean removeContent) throws NeOnCoreException {
        // do nothing
    }

    @Override
    public void renameOntology(String oldOntologyUri, String newOntologyUri) throws NeOnCoreException {
        // do nothing
    }

    @Override
    public void restoreProject() throws NeOnCoreException {
        // do nothing
    }

    @Override
    public void saveOntology(String ontologyUri) throws NeOnCoreException {
        // do nothing
    }

    @Override
    public void init() {
        // do nothing
    }

    @Override
    public Properties getConfiguration() {
        return null;
    }
    
    @Override
    public String[] getUsers(String ontologyUri) throws NeOnCoreException {
        return null;
    }

    @Override
    public Set<String> getAvailableOntologyURIs() throws NeOnCoreException {
        return Collections.emptySet();
    }

    @Override
    public void readAndDispatchWhileWaitingForEvents() throws NeOnCoreException {
        // do nothing
    }
    
    @Override
    public String getDefaultOntologyFileFormatExtension() {
        return null;
    }
    @Override
    public List<String> getSupportedOntologyFileFormats() throws NeOnCoreException {
        return null;
    }
    
    @Override
    public String retrieveOntologyUri(String physicalUri) throws NeOnCoreException {
        return null;
    }
    
        @Override
    public void createOntology(String ontologyURI, String defaultNamespace) throws NeOnCoreException {
    }

    @Override
    public String getProjectFactoryId() {
        return TestOntologyProjectFactory.FACTORY_ID;
    }

    @Override
    protected boolean hasLegalOntologyExtension(String fileName) {
        return false;
    }
    
    @Override
    public boolean existsPhysicalUri(String fileName) {
        return false;
    }

    @Override
    public void openOntology(String ontologyURI) throws NeOnCoreException {
        // do nothing
    }

    @Override
    public Set<String> getAllImportedOntologyURIs(String ontologyURI) throws NeOnCoreException {
        return null;
    }

    @Override
    public Set<String> getAllImportingOntologyURIs(String ontologyURI) throws NeOnCoreException {
        return null;
    }

    @Override
    public Set<String> getImportedOntologyURIs(String ontologyURI) throws NeOnCoreException {
        return null;
    }

    @Override
    public String getPhysicalURIForOntology(String ontologyURI) throws NeOnCoreException {
        return null;
    }

    @Override
    public NeOnCoreException checkProjectFailure(NeOnCoreException e) {
        return null;
    }

    @Override
    public CommandException checkProjectFailure(CommandException e) {
        return null;
    }

    @Override
    public RuntimeException checkProjectFailure(RuntimeException e) {
        return null;
    }

    @Override
    public Exception checkProjectFailure(Exception e) {
        return null;
    }

    @Override
    public String getDefaultNamespace(String ontologyURI) throws NeOnCoreException {
        return null;
    }

    @Override
    public Map<String,String> getNamespacePrefixes(String ontologyURI) throws NeOnCoreException {
        return null;
    }

    @Override
    public void setDefaultNamespace(String ontologyURI, String namespace) throws NeOnCoreException {
        // do nothing
    }

    @Override
    public void setNamespacePrefix(String ontologyURI, String prefix, String namespace) throws NeOnCoreException {
        // do nothing
    }

    @Override
    public void addToImportedOntologies(String ontologyURI, Set<String> ontologyURIsToImport) throws NeOnCoreException {
        // do nothing
    }

    @Override
    public void removeFromImportedOntologies(String ontologyURI, Set<String> ontologyURIsToImport) throws NeOnCoreException {
        // do nothing
    }
}
