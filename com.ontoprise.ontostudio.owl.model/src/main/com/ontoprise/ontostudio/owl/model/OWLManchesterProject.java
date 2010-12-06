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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.exception.OntologyAlreadyExistsException;
import org.neontoolkit.core.project.AbstractOntologyProject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.DefaultChangeBroadcastStrategy;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitor;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyLoaderListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import com.ontoprise.ontostudio.owl.model.commands.ontology.CreateOntology;
import com.ontoprise.ontostudio.owl.model.commands.project.RestoreProject;
import com.ontoprise.ontostudio.owl.model.commands.project.SaveOntology;
import com.ontoprise.ontostudio.owl.model.util.Cast;
import com.ontoprise.ontostudio.owl.model.util.file.OWLFileUtilities;
import com.ontoprise.ontostudio.owl.model.util.file.OWLOntologyInfo;
import com.ontoprise.ontostudio.owl.model.util.file.UnknownOWLOntologyFormatException;

/**
 * @author diwe
 * 
 */
public class OWLManchesterProject extends AbstractOntologyProject {
    private static final String DEFAULT_NAMESPACE_PREFIX = ""; //$NON-NLS-1$
    
    private static void removeOntologies(OWLOntologyManager manager, Set<OWLOntologyID> ontologyURIs) {
        for (OWLOntologyID ontologyID: ontologyURIs) {
            try {
                // IRI ontologyIRI = ontologyID.getOntologyIRI(); // TODO: or getDefault... ??? IRI.create(ontologyUri);
                OWLOntology ontology = manager.getOntology(ontologyID);
                if (ontology != null) {
                    manager.removeOntology(ontology);
                }
            } catch (RuntimeException e) {
                Logger.getLogger(OWLManchesterProject.class).error(e);
            }
        }
    }
    
    private class OntologyChangeVisitor implements OWLOntologyChangeVisitor {
        private void setOntologyModified(OWLOntologyChange change) {
            OWLManchesterProject.this.setOntologyDirty(OWLManchesterProject.toString(change.getOntology().getOntologyID()), true);
        }

        @Override
        public void visit(AddAxiom change) {
            setOntologyModified(change);
        }

        @Override
        public void visit(RemoveAxiom change) {
            setOntologyModified(change);
        }

        @Override
        public void visit(AddOntologyAnnotation change) {
            setOntologyModified(change);
        }

        @Override
        public void visit(RemoveOntologyAnnotation change) {
            setOntologyModified(change);
        }

        @Override
        public void visit(SetOntologyID change) {
            setOntologyModified(change);
            String oldOntologyURI = OWLManchesterProject.toString(change.getOriginalOntologyID());
            String newOntologyURI = OWLManchesterProject.toString(change.getNewOntologyID());
            try {
                getOntologyProjectNature().renameOntology(oldOntologyURI, newOntologyURI);
                removeOntology(oldOntologyURI, false);
                fireRenameEvent(EventTypes.ONTOLOGY_RENAMED, oldOntologyURI, newOntologyURI);
                OWLManchesterProject.this.setOntologyDirty(newOntologyURI, true);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void visit(AddImport change) {
            setOntologyModified(change);
            fireOntologyStructureChanged(change.getOntology().getOntologyID().getOntologyIRI().toURI().toString(), true);
        }

        @Override
        public void visit(RemoveImport change) {
            setOntologyModified(change);
            fireOntologyStructureChanged(change.getOntology().getOntologyID().getOntologyIRI().toURI().toString(), true);
        }
    }
    private class OWLModelOntologyUpdateListener implements OWLOntologyChangeListener {
        
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
            Set<OWLOntology> ontologiesToCleanCachesFor = new LinkedHashSet<OWLOntology>();
            Map<OWLOntology,List<OWLAxiomChange>> changesByOntology = new LinkedHashMap<OWLOntology,List<OWLAxiomChange>>();
            for (OWLOntologyChange change: changes) {
                if (change instanceof OWLAxiomChange) {
                    OWLOntology ontology = change.getOntology();
                    if (!changesByOntology.containsKey(ontology)) {
                        changesByOntology.put(ontology, new ArrayList<OWLAxiomChange>());
                    }
                    ((List<OWLAxiomChange>)changesByOntology.get(ontology)).add((OWLAxiomChange)change);
                } else if (change instanceof AddImport || change instanceof RemoveImport) {
                    ontologiesToCleanCachesFor.add(change.getOntology());
                }
            }
            for (OWLOntology ontology: ontologiesToCleanCachesFor) {
                try {
                    OWLModelFactory.getOWLModel(OWLUtilities.toString(ontology.getOntologyID()), _projectName).cleanCaches();
                } catch (NeOnCoreException e) {
                    Logger.getLogger(OWLManchesterProject.class).error(e);
                }
            }
            for (Map.Entry<OWLOntology,List<OWLAxiomChange>> entry: changesByOntology.entrySet()) {
                OWLOntology ontology = entry.getKey();
                List<? extends OWLAxiomChange> localChanges = entry.getValue();
                Set<OWLEntity> potentiallyAddedEntities = new LinkedHashSet<OWLEntity>();
                Set<OWLEntity> removedEntities = new LinkedHashSet<OWLEntity>();
                getChangedEntities(ontology, localChanges, potentiallyAddedEntities, removedEntities);
                try {
                    OWLModel model = OWLModelFactory.getOWLModel(OWLUtilities.toString(ontology.getOntologyID()), getName());
                    Set<OWLModel> models = new LinkedHashSet<OWLModel>();
                    models.add(model);
                    models.addAll(model.getAllImportingOntologies());
                    for (OWLModel m: models) {
                        try {
                            m.ontologyUpdated(ontology, localChanges, potentiallyAddedEntities, removedEntities);
                        } catch (NeOnCoreException e) {
                            Logger.getLogger(OWLManchesterProject.class).error(e);
                        }
                    }
                } catch (NeOnCoreException e) {
                    Logger.getLogger(OWLManchesterProject.class).error(e);
                }
            }
        }

        private void getChangedEntities(OWLOntology ontology, List<? extends OWLAxiomChange> changesInOntology, Set<OWLEntity> potentiallyAdded, Set<OWLEntity> removedEntities) {
            Map<OWLEntity,Integer> changeCount = new LinkedHashMap<OWLEntity,Integer>();
            for (OWLAxiomChange change: changesInOntology) {
                Set<OWLEntity> changedEntities = change.getEntities();
                for (OWLEntity entity: changedEntities) {
                    Integer oldValue = changeCount.get(entity);
                    int newValue = 0;
                    if (oldValue != null) {
                        newValue = oldValue;
                    }
                    if (change instanceof AddAxiom) {
                        newValue++;
                    } else {
                        newValue--;
                    }
                    changeCount.put(entity, newValue);
                }
            }
            for (Map.Entry<OWLEntity,Integer> entry: changeCount.entrySet()) {
                OWLEntity entity = entry.getKey();
                int count = entry.getValue();
                if (count == 0) {
                    // neither added nor removed
                } else if (count < 0) {
                    // may have been removed
                    if (!ontology.containsEntityInSignature(entity)) {
                        removedEntities.add(entity);
                    }
                } else {
                    // may have been added
                    // Note: there is no way to check if entity is really new!
                    potentiallyAdded.add(entity);
                }
            }
        }
    }

    private class OntologyChangeListener implements OWLOntologyChangeListener {
        @Override
        public void ontologiesChanged(final List<? extends OWLOntologyChange> changes) throws OWLException {
            if (Thread.currentThread() != Display.getDefault().getThread()) {
                Logger.getLogger(OWLManchesterProject.class).error("OWLOntologyChangeListener called by a non GUI thread. The Manchester OWL API implementation is not thread safe. Asynchronously executing by GUI thead."); //$NON-NLS-1$
            }
            // always perform events asynchronously
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    _owlModelOntologyUpdateListener.ontologiesChanged(changes);
                    for (OWLOntologyChange change: changes) {
                        change.accept(_ontologyChangeVisitor);
                    }
                }
            });
        }
    }

    private static String toString(IRI iri) {
        if (iri == null) {
            return null;
        }
        return iri.toString();
    }

    private static String toString(OWLOntologyID id) {
        return toString(id.getDefaultDocumentIRI());
    }

    private static IRI toOntologyIRI(String ontologyURI) {
        return IRI.create(ontologyURI);
    }

    private OWLOntologyManager _ontologyManager;
    private OWLOntologyChangeVisitor _ontologyChangeVisitor;
    private OWLOntologyChangeListener _ontologyChangeListener;
    private OWLModelOntologyUpdateListener _owlModelOntologyUpdateListener;

    public OWLManchesterProject(String name) {
        super(name);
    }

    private OWLOntology getOntology(String ontologyURI) {
        return _ontologyManager.getOntology(toOntologyIRI(ontologyURI));
    }

    @Override
    public <T> T getAdapter(Class<T> adapterClass) {
        if (adapterClass == OWLOntologyManager.class) {
            return Cast.cast(getOntologyManager());
        }
        return null;
    }

    @Override
    public Properties getConfiguration() {
        return null;
    }

    @Override
    public String getOntologyLanguage() {
        return OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE;
    }

    public OWLOntologyManager getOntologyManager() {
        return _ontologyManager;
    }

    @Override
    public String[] getUsers(String ontologyUri) throws NeOnCoreException {
        return null;
    }

    @Override
    public void init() {
        _ontologyManager = OWLManager.createOWLOntologyManager();
        _ontologyManager.setSilentMissingImportsHandling(false);
        _ontologyChangeVisitor = new OntologyChangeVisitor();
        _ontologyChangeListener = new OntologyChangeListener();
        _ontologyManager.addOntologyChangeListener(_ontologyChangeListener, new DefaultChangeBroadcastStrategy());
        _owlModelOntologyUpdateListener = new OWLModelOntologyUpdateListener();
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void removeOntology(String ontologyUri, boolean removeContent) throws NeOnCoreException {
        Logger.getLogger(getClass()).debug(String.format("Trying to remove ontology %1$s...", ontologyUri));
        OWLOntologyManager manager = getAdapter(OWLOntologyManager.class);
        Set<String> ontologies = getAllImportingOntologyURIs(ontologyUri);
        ontologies.add(ontologyUri);
        for (String uri: ontologies) {
            OWLOntology ontology = manager.getOntology(IRI.create(uri));
            // ontology can be null here if the ontology was already removed (e.g. because it appeared in the imported list)
            if (ontology != null) {
                try {
                    String physicalURIForOntology = getPhysicalURIForOntology(uri);
                    if (physicalURIForOntology != null) {
                        URI physicalURI = new URI(physicalURIForOntology);
                        removeOntologyFile(physicalURI);
                    } else {
                        Logger.getLogger(getClass()).warn(String.format("No physical URI for ontology %1$s. Assuming ontology has not been saved yet.", physicalURIForOntology));
                    }
                    manager.removeOntology(ontology);
                    setOntologyDirty(ontologyUri, false);
                } catch (CoreException e) {
                        IStatus status = e.getStatus();
                        Logger.getLogger(getClass()).error(status.toString());
                    throw new InternalNeOnException(e);
                } catch (URISyntaxException e) {
                    throw new InternalNeOnException(e);
                }
            }
            else {
                setOntologyDirty(ontologyUri, false);
            }
        }
        Logger.getLogger(getClass()).debug(String.format("Finished removing [%1$s]", ontologyUri));

        // fire events late
        for (String uri: ontologies) {
            super.removeOntology(uri, removeContent);
        }
    }

    private void removeOntologyFile(URI uri) throws CoreException {
        if (!isPersistent()) {
            if (uri != null && uri.getScheme().equals("file")) { //$NON-NLS-1$
                String fileName = (new File(uri)).getName();
                getResource().refreshLocal(1, null);
                IFile file = getResource().getFile(fileName);

                if (file.exists()) {
                    Logger.getLogger(getClass()).debug(String.format("Removing dependent ontology %1$s...", fileName));
                    file.delete(true, null);
                    Logger.getLogger(getClass()).debug(String.format("Done. [%1$s]", fileName));
                }
            }
        }
    }

    @Override
    public void renameOntology(String oldOntologyUri, String newOntologyUri) throws NeOnCoreException {
        try {
            OWLOntologyManager manager = getAdapter(OWLOntologyManager.class);
            OWLOntology ontology = manager.getOntology(IRI.create(oldOntologyUri));
            if (ontology != null) {
                manager.applyChange(new SetOntologyID(ontology, new OWLOntologyID(IRI.create(newOntologyUri))));
            }
        } catch (Exception e) {
            throw new InternalNeOnException(e);
        }
    }

    @Override
    public void restoreProject(IProgressMonitor monitor) throws NeOnCoreException {
        try {
            new RestoreProject(getName(), monitor).run();
        } catch (CommandException e) {
            throw new InternalNeOnException(e);
        }
    }

    @Override
    public void restoreProject() throws NeOnCoreException {
        try {
            new RestoreProject(getName()).run();
        } catch (CommandException e) {
            throw new InternalNeOnException(e);
        }
    }
    
    @Override
    public void saveOntology(String ontologyUri) throws NeOnCoreException {
        try {
            IRI physicalURI = _ontologyManager.getOntologyDocumentIRI(_ontologyManager.getOntology(IRI.create(ontologyUri)));
            IProject project = this.getResource();
            URI projURI = project.getLocationURI();
            if (!physicalURI.toString().startsWith(projURI.toString())){
                throw new NeOnCoreException("Ontology not stored in workspace") {
                    public String getErrorCode() {
                        return "SavingRemoteOntology";}};
            }
            else new SaveOntology(getName(), ontologyUri).run();
        } catch(NeOnCoreException e1){
            throw e1;
        } catch (Exception e) {
            throw new InternalNeOnException(e);
        }
    }

    @Override
    public void readAndDispatchWhileWaitingForEvents() throws NeOnCoreException {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                while (Display.getDefault().readAndDispatch()) {
                    // do nothing
                }
            }
        });
    }

    @Override
    public Set<String> getAvailableOntologyURIs() throws NeOnCoreException {
        Set<String> ontoUris = new LinkedHashSet<String>();
        for (OWLOntology ontology: getOntologyManager().getOntologies()) {
            ontoUris.add(OWLUtilities.toString(ontology.getOntologyID()));
        }
        return ontoUris;
    }

    @Override
    public List<String> getSupportedOntologyFileFormats() throws NeOnCoreException {
        List<String> supportedFormats = new ArrayList<String>();
        supportedFormats.add(new RDFXMLOntologyFormat().toString());
        return supportedFormats;
    }

    @Override
    public String getDefaultOntologyFileFormatExtension() {
        return ".owl";
    }

    @Override
    public String retrieveOntologyUri(String physicalUri) throws NeOnCoreException {
        return "http://OWLManchesterProject/defaultOntologyUri";
    }

    @Override
    public void createOntology(String ontologyURI, String defaultNamespace) throws NeOnCoreException {
        try {
            new CreateOntology(getName(), ontologyURI, defaultNamespace, getNameFromUri(ontologyURI)).run();
            setOntologyDirty(ontologyURI, true);
        } catch (CommandException e) {
            throw new InternalNeOnException(e);
        }
    }

    @SuppressWarnings("unused")
    public void ontologyRemoved(String ontologyURI) throws NeOnCoreException {
        fireAddRemoveEvent(EventTypes.ONTOLOGY_REMOVED, ontologyURI);
    }
    
    public Set<String> importOntologies(URI[] physicalURIs, boolean restoringFromWorkspace, IProgressMonitor monitor) throws UnknownOWLOntologyFormatException, OWLOntologyCreationException, NeOnCoreException {
        try {
            if (monitor == null){
                monitor = new NullProgressMonitor();
            }

            monitor.subTask("Checking ontologies ..."); //$NON-NLS-1$ 

            List<SimpleIRIMapper> iriMappers = new ArrayList<SimpleIRIMapper>();
            List<IRI> ontologyIRIs = new ArrayList<IRI>();
            final Map<OWLOntologyID,String> owlOntologyUris = new HashMap<OWLOntologyID,String>();
            try {
                if (physicalURIs.length == 1) {
                    // the usual case which can be handled easy and has a performance benefit
                    // we do not need a mapper but just open the ontology by its physical uri
                } else {
                    int count=0;
                    for (URI physicalURI: physicalURIs) {
                        monitor.subTask("Checking ontology: "+physicalURI+"  ("+(++count)+"/"+ physicalURIs.length+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        
                        OWLOntologyInfo ontologyInfo = OWLFileUtilities.getOntologyInfo(physicalURI);
                        String ontologyUri = OWLUtilities.toString(ontologyInfo.getOntologyID());
                        if (ontologyUri == null){
                            ontologyUri = physicalURI.toString();
                        }
                        URI ontologyURI = URI.create(ontologyUri);
                        IRI ontologyIRI = IRI.create(ontologyURI);
                        ontologyIRIs.add(ontologyIRI);
                        SimpleIRIMapper mapper = new SimpleIRIMapper(IRI.create(ontologyURI), IRI.create(physicalURI));
                        _ontologyManager.addIRIMapper(mapper);
                        iriMappers.add(mapper);
                    }
                }

                final AtomicReference<OWLOntologyCreationException> exception = new AtomicReference<OWLOntologyCreationException>();
                OWLOntologyLoaderListener owlOntologyLoaderListener = new OWLOntologyLoaderListener() {
                    @Override
                    public void finishedLoadingOntology(LoadingFinishedEvent event) {
                        if (event.getException() == null) {
                            if (event.getOntologyID().getOntologyIRI() == null){
                                OWLOntology onto = _ontologyManager.getOntology(event.getOntologyID());
                                // Change the null URI into the physicalURI so that NeOn Toolkit is happy.
                                OWLOntologyID newID = new OWLOntologyID(event.getDocumentIRI());
                                try {
                                    //NICO has to be implemented
                                    if(onto != null){
                                        if(newID != null){
                                            _ontologyManager.applyChange(new SetOntologyID(onto, newID));
                                            owlOntologyUris.put(newID, event.getDocumentIRI().toString());
                                        }else{
                                            MessageDialog.openWarning(null, Messages.OWLManchasterProject_OntologyCantBeSet, Messages.OWLManchasterProject_OntologyCantBeSet_NewOID); 
                                            exception.set(new OWLOntologyCreationException(Messages.OWLManchasterProject_OntologyCantBeSet_NewOID));
                                            
                                        }
                                    }else{
                                        if(newID == null){
                                            MessageDialog.openWarning(null, Messages.OWLManchasterProject_OntologyCantBeSet, Messages.OWLManchasterProject_OntologyCantBeSet_NewOID_Onto); 
                                            exception.set(new OWLOntologyCreationException(Messages.OWLManchasterProject_OntologyCantBeSet_NewOID_Onto));
                                        }else{
                                            MessageDialog.openWarning(null, Messages.OWLManchasterProject_OntologyCantBeSet, Messages.OWLManchasterProject_OntologyCantBeSet_Onto); 
                                            exception.set(new OWLOntologyCreationException(Messages.OWLManchasterProject_OntologyCantBeSet_Onto));
                                        }
                                    }                              
                                } catch (OWLOntologyChangeException e) {
                                    e.printStackTrace();
                                }
                            }
                            else owlOntologyUris.put(event.getOntologyID(), event.getDocumentIRI().toString());
                        } else {
                            if (exception.get() == null) {
                                exception.set(event.getException());
                            }
                        }
                    }

                    @Override
                    public void startedLoadingOntology(LoadingStartedEvent event) {
                        // do nothing
                    }
                };

                _ontologyManager.addOntologyLoaderListener(owlOntologyLoaderListener);
                Set<String> unknownHosts = new HashSet<String>(); 
                try {
                    
                    if (physicalURIs.length == 1) {
                        monitor.subTask("Loading ontology "+physicalURIs[0]); //$NON-NLS-1$
                        try {
                            _ontologyManager.loadOntologyFromOntologyDocument(IRI.create(physicalURIs[0]));
                        } catch (OWLOntologyCreationException e) {
                            removeOntologies(_ontologyManager, owlOntologyUris.keySet());
                            throw e;
                        } catch (RuntimeException e) {
                            removeOntologies(_ontologyManager, owlOntologyUris.keySet());
                            throw e;
                        }
                        
                    } else {
                        int count=0;
                        for (IRI ontologyIRI: ontologyIRIs) {
                            try {
                                monitor.subTask("Loading ontology: "+ ontologyIRI +"  ("+(++count)+"/"+ ontologyIRIs.size()+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                                _ontologyManager.loadOntology(ontologyIRI);
                                if (exception.get() != null) {
                                    throw exception.get();
                                }

                            } catch (OWLOntologyCreationException e) {
                                Logger.getLogger(OWLManchesterProject.class).error(e);
                                exception.set(null);
                                if(e.getCause()!=null) {
                                    unknownHosts.add(e.getCause().getLocalizedMessage());
                                } else {
                                    unknownHosts.add(e.getLocalizedMessage());
                                }
//                                if(e.getCause() instanceof UnknownHostException) {
//                                    // try to load (via import) an onto from the web, which is not accessible right now 
//                                    unknownHosts.add(e.getCause().getLocalizedMessage());
//                                } else if(e.getCause() instanceof FileNotFoundException) {
//                                    // try to load (via import) an onto from the web, which does not exist at that position, right now 
//                                    unknownHosts.add(e.getCause().getLocalizedMessage());
//                                } else if(e.getCause() instanceof UnparsableOntologyException) {
//                                    unknownHosts.add(e.getCause().getLocalizedMessage());
//                                } else {
//                                    removeOntologies(_ontologyManager, owlOntologyUris.keySet());
//                                    throw e;
//                                }
                            } catch (RuntimeException e) {
                                removeOntologies(_ontologyManager, owlOntologyUris.keySet());
                                throw e;
                            }
                        }
                    }
                    
                    
                    Set<String> registeredOntologies = new LinkedHashSet<String>();
                    try {
                        for (OWLOntologyID ontologyUri: owlOntologyUris.keySet()) {
                            if (ontologyUri.getOntologyIRI()==null) {
                                removeOntologies(_ontologyManager, owlOntologyUris.keySet());
                                throw new OWLOntologyCreationException("Cannot load an ontology without a URI"); //$NON-NLS-1$
                            }
                            else {
                                this.addOntology(ontologyUri.getOntologyIRI().toString());
                                registeredOntologies.add(ontologyUri.getOntologyIRI().toString());
                                String physicalUri = owlOntologyUris.get(ontologyUri);
                                _ontologyManager.setOntologyDocumentIRI(_ontologyManager.getOntology(ontologyUri), IRI.create(physicalUri));
                            }
                        }
                    } catch (NeOnCoreException e) {
                        unregisterOntologies(registeredOntologies);
                        removeOntologies(_ontologyManager, owlOntologyUris.keySet());
                        throw e;
                    } catch (UnknownOWLOntologyException e) {
                        unregisterOntologies(registeredOntologies);
                        removeOntologies(_ontologyManager, owlOntologyUris.keySet());
                        throw e;
                    } catch (RuntimeException e) {
                        unregisterOntologies(registeredOntologies);
                        removeOntologies(_ontologyManager, owlOntologyUris.keySet());
                        throw e;
                    }
                } finally {
                    _ontologyManager.removeOntologyLoaderListener(owlOntologyLoaderListener);
                    if(unknownHosts.size()>0) {
                        String s = "Some Ontologies could not be loaded, e.g.:"; //$NON-NLS-1$
                        for (String string: unknownHosts) {
                            s += "\n\t"+string; //$NON-NLS-1$
                        }
                        throw new RuntimeException(new UnknownHostException(s));
                    }
                }
            } finally {
                for (SimpleIRIMapper simpleIRIMapper: iriMappers) {
                    _ontologyManager.removeIRIMapper(simpleIRIMapper);
                }
            }
            Set<String> loadedURIs = new HashSet<String>();
            for (OWLOntologyID oid : owlOntologyUris.keySet())
                   loadedURIs.add(oid.getOntologyIRI().toString());
            return loadedURIs;
        } finally {
            // hack to avoid pending file handles, see issue 12863
            System.gc();
        }
    }

    private void unregisterOntologies(Set<String> registeredOntologies) throws NeOnCoreException {
        List<Exception> exceptions = new ArrayList<Exception>();
        for (String ontologyURI: registeredOntologies) {
            try {
                super.removeOntology(ontologyURI, true);
            } catch (NeOnCoreException e) {
                exceptions.add(e);
            } catch (RuntimeException e) {
                exceptions.add(e);
            }
        }
        if (exceptions.size() > 0) {
            Exception exception = exceptions.get(0);
            if (exception instanceof NeOnCoreException) {
                throw (NeOnCoreException)exception;
            }
            throw (RuntimeException)exception;
        }
    }

    @Override
    public String getProjectFactoryId() {
        return OWLManchesterProjectFactory.FACTORY_ID;
    }

    @Override
    protected boolean hasLegalOntologyExtension(String fileName) {
        if (fileName == null) {
            return false;
        }
        String lowerCase = fileName.toLowerCase();
        return lowerCase.endsWith(".owl") || lowerCase.endsWith(".owl2"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean existsPhysicalUri(String fileName) {
        try {
            String physicalOntologyUri = getResource().getFile(fileName).getLocationURI().toString();
            Set<OWLOntology> ontologies = getOntologyManager().getOntologies();
            for (OWLOntology ontology: ontologies) {
                if (getOntologyManager().getOntologyDocumentIRI(ontology).toString().equals(physicalOntologyUri)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public void openOntology(String ontologyURI) throws NeOnCoreException {
        try {
            OWLOntology ontology = getOntology(ontologyURI);
            if (ontology != null) {
                throw new OntologyAlreadyExistsException(ontologyURI);
            }
            _ontologyManager.loadOntology(toOntologyIRI(ontologyURI));
        } catch (UnknownOWLOntologyException e) {
            throw new InternalNeOnException(e);
        } catch (OWLOntologyCreationException e) {
            throw new InternalNeOnException(e);
        } finally {
            // hack to avoid pending file handles, see issue 12863
            System.gc();
        }
    }

    @Override
    public Set<String> getAllImportedOntologyURIs(String ontologyURI) throws NeOnCoreException {
        // TODO: could be more efficient, removing ontologyURI at the end is not really correct
        Set<String> result = new LinkedHashSet<String>();
        result.add(ontologyURI);
        boolean modified = true;
        while (modified) {
            modified = false;
            for (String uri: new LinkedHashSet<String>(result)) {
                modified |= result.addAll(getImportedOntologyURIs(uri));
            }
        }
        result.remove(ontologyURI);
        return result;
    }

    @Override
    public Set<String> getAllImportingOntologyURIs(String ontologyURI) throws NeOnCoreException {
        Set<String> result = new HashSet<String>();
        for (String o: getOntologies()) {
            if (getAllImportedOntologyURIs(o).contains(ontologyURI)) {
                result.add(o);
            }
        }
        return result;
    }

    @Override
    public Set<String> getImportedOntologyURIs(String ontologyURI) throws NeOnCoreException {
        Set<String> result = new LinkedHashSet<String>();
        OWLOntology mainOntology = getOntology(ontologyURI);
        if (mainOntology == null) {
            return result;
        }
//      for (OWLOntology ontology: mainOntology.getDirectImports()) {
//      result.add(OWLUtilities.toString(ontology.getOntologyID()));
//  }
        // use import declarations instead of getDirectImports to make things work with OWL API 3.0.0 revision 1310
        for (OWLImportsDeclaration d: mainOntology.getImportsDeclarations()) {
            result.add(d.getURI().toString());
        }
        return result;
    }

    @Override
    public String getPhysicalURIForOntology(String ontologyURI) throws NeOnCoreException {
        return _ontologyManager.getOntologyDocumentIRI(getOntology(ontologyURI)).toString();
    }
    
    @Override
    public void setPhysicalURIForOntology(String ontologyURI, String physicalURI) throws NeOnCoreException {
        _ontologyManager.setOntologyDocumentIRI(getOntology(ontologyURI), IRI.create(physicalURI));
    }

    public String getPhysicalURIForOntology(OWLOntology ontology) {
        return _ontologyManager.getOntologyDocumentIRI(ontology).toString();
    }

    @Override
    public NeOnCoreException checkProjectFailure(NeOnCoreException e) {
        return e;
    }

    @Override
    public CommandException checkProjectFailure(CommandException e) {
        return e;
    }

    @Override
    public RuntimeException checkProjectFailure(RuntimeException e) {
        return e;
    }

    @Override
    public Exception checkProjectFailure(Exception e) {
        return e;
    }

    private String manchesterToNTKPrefix(String prefix) {
        if (prefix == null) {
            return null;
        }
        if (prefix.endsWith(":")) {
            return prefix.substring(0, prefix.length() - 1);
        }
        return prefix;
    }

    private String ntkToManchesterPrefix(String prefix) {
        return prefix + ":";
    }

    private PrefixOWLOntologyFormat getPrefixOWLOntologyFormat(String ontologyURI) {
        OWLOntology ontology = getOntology(ontologyURI);
        OWLOntologyFormat format = _ontologyManager.getOntologyFormat(ontology);
        if (format != null && format instanceof PrefixOWLOntologyFormat) {
            return (PrefixOWLOntologyFormat) format;
        }
        return null;
    }

    @Override
    public String getDefaultNamespace(String ontologyURI) throws NeOnCoreException {
        PrefixOWLOntologyFormat format = getPrefixOWLOntologyFormat(ontologyURI);
        if (format == null) {
            return null;
        }
        return manchesterToNTKPrefix(format.getDefaultPrefix());
    }

    @Override
    public Map<String,String> getNamespacePrefixes(String ontologyURI) throws NeOnCoreException {
        PrefixOWLOntologyFormat format = getPrefixOWLOntologyFormat(ontologyURI);
        if (format == null) {
            return new LinkedHashMap<String,String>();
        }
        Map<String,String> result = new LinkedHashMap<String,String>();
        for (Map.Entry<String,String> entry: format.getPrefixName2PrefixMap().entrySet()) {
            result.put(manchesterToNTKPrefix(entry.getKey()), entry.getValue());
        }
        return result;
    }

    @Override
    public void setDefaultNamespace(final String ontologyURI, final String namespace) throws NeOnCoreException {
        PrefixOWLOntologyFormat format = getPrefixOWLOntologyFormat(ontologyURI);
        if (format == null) {
            OWLOntology ontology = getOntology(ontologyURI);
            format = new RDFXMLOntologyFormat();
            _ontologyManager.setOntologyFormat(ontology, format);
        }
        if (namespace != null) {
            format.setDefaultPrefix(namespace);
        } else {
            OWLOntology ontology = getOntology(ontologyURI);
            PrefixOWLOntologyFormat newFormat = new RDFXMLOntologyFormat();
            for (Map.Entry<String,String> entry: format.getPrefixName2PrefixMap().entrySet()) {
                if (!manchesterToNTKPrefix(DEFAULT_NAMESPACE_PREFIX).equals(entry.getKey())) {
                    newFormat.setPrefix(entry.getKey(), entry.getValue());
                }
            }
            _ontologyManager.setOntologyFormat(ontology, newFormat);
        }
        try {
            fireNamespacePrefixChangedEvent(ontologyURI, DEFAULT_NAMESPACE_PREFIX, namespace);
        } finally {
            setOntologyDirty(ontologyURI, true);
        }
    }

    @Override
    public void setNamespacePrefix(String ontologyURI, String prefix, String namespace) throws NeOnCoreException {
        PrefixOWLOntologyFormat format = getPrefixOWLOntologyFormat(ontologyURI);
        if (format == null) {
            OWLOntology ontology = getOntology(ontologyURI);
            format = new RDFXMLOntologyFormat();
            _ontologyManager.setOntologyFormat(ontology, format);
        }
        String manchesterPrefix = ntkToManchesterPrefix(prefix);
        if (namespace != null) {
            format.setPrefix(manchesterPrefix, namespace);
        } else {
            OWLOntology ontology = getOntology(ontologyURI);
            PrefixOWLOntologyFormat newFormat = new RDFXMLOntologyFormat();
            for (Map.Entry<String,String> entry: format.getPrefixName2PrefixMap().entrySet()) {
                if (!manchesterPrefix.equals(entry.getKey())) {
                    newFormat.setPrefix(entry.getKey(), entry.getValue());
                }
            }
            _ontologyManager.setOntologyFormat(ontology, newFormat);
        }
        try {
            fireNamespacePrefixChangedEvent(ontologyURI, prefix, namespace);
        } finally {
            setOntologyDirty(ontologyURI, true);
        }
    }

    private void fireNamespacePrefixChangedEvent(final String ontologyURI, final String prefix, final String namespace) throws NeOnCoreException {
        final OWLModel model = OWLModelFactory.getOWLModel(ontologyURI, getName());
        // we have only one "listener" which is directly executed here:

        // always perform events asynchronously
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    model.namespacePrefixChanged(prefix, namespace);
                } catch (NeOnCoreException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    
    @Override
    public void addToImportedOntologies(String ontologyURI, Set<String> ontologyURIsToImport) throws NeOnCoreException {
        try {
            OWLDataFactory factory = _ontologyManager.getOWLDataFactory();
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (String ontologyURIToImport: ontologyURIsToImport) {
                changes.add(new AddImport(getOntology(ontologyURI), factory.getOWLImportsDeclaration(OWLUtilities.toIRI(ontologyURIToImport))));
            }
            _ontologyManager.applyChanges(changes);
        } catch (OWLOntologyChangeException e) {
            throw new InternalNeOnException(e);
        }
    }
    
    @Override
    public void removeFromImportedOntologies(String ontologyURI, Set<String> ontologyURIsToImport) throws NeOnCoreException {
        try {
            OWLDataFactory factory = _ontologyManager.getOWLDataFactory();
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (String ontologyURIToImport: ontologyURIsToImport) {
                changes.add(new RemoveImport(getOntology(ontologyURI), factory.getOWLImportsDeclaration(OWLUtilities.toIRI(ontologyURIToImport))));
            }
            _ontologyManager.applyChanges(changes);
        } catch (OWLOntologyChangeException e) {
            throw new InternalNeOnException(e);
        }
    }
}
