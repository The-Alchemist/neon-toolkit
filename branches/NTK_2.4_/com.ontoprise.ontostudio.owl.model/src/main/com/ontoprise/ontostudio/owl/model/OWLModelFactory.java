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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.exception.OntologyNotOpenedException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.IOntologyProjectListener;
import org.neontoolkit.core.project.IProjectFailureListener;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

public class OWLModelFactory {
    private static class OntologyProjectListener implements IOntologyProjectListener, IProjectFailureListener {
        @Override
        public void ontologyAdded(String projectName, String ontologyUri) {
            // do nothing
        }
        @Override
        public void ontologyModified(String projectName, String ontologyUri, boolean modified) {
            // do nothing
        }
        @Override
        public void ontologyRemoved(String projectName, String ontologyUri) {
            forgetOntology(ontologyUri, projectName);
        }
        @Override
        public void ontologyRenamed(String projectName, String oldOntologyUri, String newOntologyUri) {
            OWLModelFactory.ontologyRenamed(oldOntologyUri, newOntologyUri, projectName);
        }
        @Override
        public void projectAdded(String projectName) {
            // do nothing
        }
        @Override
        public void projectRemoved(String projectName) {
            OWLModelFactory.projectRemoved(projectName);
        }
        @Override
        public void projectRenamed(String oldProjectName, String newProjectName) {
            OWLModelFactory.projectRenamed(oldProjectName, newProjectName);
        }
        @Override
        public void projectFailureOccured(String projectId, Exception exception) {
            synchronized (_lock) {
                if (!_owlModels.containsKey(projectId)) {
                    return;
                }
                _owlModels.remove(projectId);
            }
        }
        @Override
        public void ontologyStructureModified(String projectName, String ontologyUri, boolean modified) {
        }
    }
    /** Cached models by projectId/ontologyURI. */
    private static final Map<String,Map<String,OWLModel>> _owlModels = new LinkedHashMap<String,Map<String,OWLModel>>();
    /** A listener updating the cache. */
    private static IOntologyProjectListener _ontologyProjectListener = new OntologyProjectListener();
    static {
        NeOnCorePlugin.getDefault().addOntologyProjectListenerByLanguage(_ontologyProjectListener, OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE);
    }
    
    /**
     * A synchronize lock to access the member variables.
     * 
     * <p>Note that one must not synchronize code on this lock which may "leave" the code inside this class, e.g. do not call {@link OWLModel} or
     * {@link OWLOntologyManager} methods while holding this lock since such method calls might cause events which might be processed in a different
     * {@link Thread} when using the collab server.</p>
     */
    private static final Object _lock = new Object();

    private static NeOnCoreException checkConnectionFailure(NeOnCoreException e, String projectId) throws NeOnCoreException {
        return NeOnCorePlugin.getDefault().getOntologyProject(projectId).checkProjectFailure(e);
    }
    private static RuntimeException checkConnectionFailure(RuntimeException e, String projectId) throws NeOnCoreException {
        return NeOnCorePlugin.getDefault().getOntologyProject(projectId).checkProjectFailure(e);
    }
    
    private static final String getOntologyURI(OWLOntology ontology) {
        return OWLUtilities.toString(ontology.getOntologyID());
    }
    
    /**
     * Notify about a renamed project. 
     * 
     * <p>The client is responsible for calling this method since the {@link OWLModelFactory} does not register a 
     * (GUI related) listener for "project name changed" events.</p>
     * 
     * <p>All currently cached models remain valid.</p>
     * 
     * @param oldProjectId                       the old project id
     * @param newProjectId                       the new project id
     */
    private static void projectRenamed(String oldProjectId, String newProjectId) {
        synchronized (_lock) {
            Map<String,OWLModel> models = _owlModels.get(oldProjectId);
            if (models != null) {
                if (_owlModels.containsKey(newProjectId)) {
                    throw new IllegalArgumentException();
                }
                _owlModels.remove(oldProjectId);
                _owlModels.put(newProjectId, models);
            }
        }
    }
    
    private static void projectRemoved(String projectId) {
        synchronized (_lock) {
            _owlModels.remove(projectId);
        }
    }

    private static void ontologyRenamed(String oldOntologyId, String newOntologyId, String projectId) {
        synchronized (_lock) {
            Map<String,OWLModel> models = _owlModels.get(projectId);
            if (models != null) {
                OWLModel model = models.get(oldOntologyId);
                if (model != null) {
                    if (_owlModels.containsKey(newOntologyId)) {
                        throw new IllegalArgumentException();
                    }
                    models.remove(oldOntologyId);
                    models.put(newOntologyId, model);
                }
            }
        }
    }
    
    /**
     * Removes the {@link OWLModel} from the cache that has the given ontology URI and project ID.
     * 
     * @param ontologyId                         the URI of an ontology
     * @param projectId                          the project ID representing a KAON2Connection
     * @return                                   <code>true</code> if the model was contained in the cache, <code>false</code> otherwise
     */
    private static boolean forgetOntology(String ontologyId, String projectId) {
        synchronized (_lock) {
            Map<String,OWLModel> ontologyToModel = _owlModels.get(projectId);
            if (ontologyToModel != null) {
                if (ontologyToModel.containsKey(ontologyId)) {
                    ontologyToModel.remove(ontologyId);
                    if (ontologyToModel.size() == 0) {
                        _owlModels.remove(projectId);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private static IOntologyProject getOntologyProject(String projectId) throws NeOnCoreException {
        return NeOnCorePlugin.getDefault().getOntologyProject(projectId);
    }


    /**
     * Convenience method to retrieve the instance of {@link OWLOntologyManager} that is attached to the project with the given ID.
     * 
     * @param projectId                          the project ID representing a {@link OWLOntologyManager}
     * @return
     */
    private static OWLOntologyManager getOntologyManager(String projectId) throws NeOnCoreException {
        IOntologyProject project = OntologyProjectManager.getDefault().getOntologyProject(projectId);
        return project.getAdapter(OWLOntologyManager.class);
    }
    
    /**
     * Returns an {@link OWLModel} instance for the given ontology URI and project name. 
     * 
     * <p>If the {@link OWLModel} does not exist yet, it is created.</p>
     * 
     * @param ontologyId                         the URI of an ontology
     * @param projectId                          the project ID representing a KAON2Connection
     * @return
     * @throws NeOnCoreException
     * @throws OntologyNotOpenedException        thrown if the project does not contain an ontology with the specified URI.
     */
    public static OWLModel getOWLModel(String ontologyId, String projectId) throws NeOnCoreException {
        try {
            // might acquire locks -> call  without holding a lock on _lock to avoid deadlocks, but we might get a problem with lost update here
            IOntologyProject ontologyProject = getOntologyProject(projectId);
        	OWLOntologyManager manager = getOntologyManager(projectId);
        	synchronized (_lock) {
            	// get existing resources
                OWLModel model = (_owlModels.containsKey(projectId)) ? _owlModels.get(projectId).get(ontologyId) : null;
            	// create new resources
            	if (model == null) {
                	try {
                    	model = new ConnectionFailureAwareOWLModel(
                    	            new OWLModelCore(
                    	                manager.getOntology(IRI.create(new URI(ontologyId))), 
                    	                manager, 
                    	                ontologyProject), 
                    	            ontologyProject);
                	} catch (UnknownOWLOntologyException e) {
                    	throw new OntologyNotOpenedException(ontologyId);
                	} catch (URISyntaxException e) {
                    	throw new InternalNeOnException(e);
                	}
            	}
                // update internal data structures (expecting no exceptions)
                if (!_owlModels.containsKey(projectId))
                    _owlModels.put(projectId, new LinkedHashMap<String,OWLModel>());
                Map<String,OWLModel> modelByOntologyId = _owlModels.get(projectId);
                if (!modelByOntologyId.containsKey(ontologyId)) {
                    modelByOntologyId.put(ontologyId, model);
                }
                return model;
        	}
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e, projectId);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e, projectId);
        }
    }

    /**
     * Convenience method to retrieve all {@link OWLModel} instances for the project with the given ID.
     * 
     * @param projectId                          the project ID representing a KAON2Connection
     * @return                                   the set of {@link OWLModel} instances that are created by this {@link OWLOntologyManager}
     * @throws NeOnCoreException
     */
    public static Set<OWLModel> getOWLModels(String projectId) throws NeOnCoreException {
        try {
        	OWLOntologyManager connection = getOntologyManager(projectId);
        	if (connection == null) {
            	return null;
        	}
        	IOntologyProject ontologyProject = getOntologyProject(projectId);
        	Set<OWLModel> result = new LinkedHashSet<OWLModel>();
        	for (String ontologyURI: ontologyProject.getOntologies()) {
        	    if (ontologyURI != null){
        	        OWLOntology ontology = connection.getOntology(IRI.create(ontologyURI));
        	        if (ontology != null) {
        	            result.add(getOWLModel(getOntologyURI(ontology), projectId));
        	        } else {
                        //System.err.println("Ontology is null for ontologyURI <"+ontologyURI+">");
        	        }
        	    }
        	}
        	return result;
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e, projectId);
        } catch (RuntimeException e) {
            if (e instanceof UnknownOWLOntologyException) {
                return null;
            }
            throw checkConnectionFailure(e, projectId);
        }
    }
    

    /**
     * Retrieve the {@link OWLDataFactory} for a project.
     * 
     * @param projectId                         The project id.
     * @return                                  The factory associated with the project.
     * @throws NeOnCoreException
     */
    public static OWLDataFactory getOWLDataFactory(String projectId) throws NeOnCoreException {
        try {
            OWLOntologyManager manager = getOntologyManager(projectId);
            return manager.getOWLDataFactory();
        } catch (NeOnCoreException e) {
            throw checkConnectionFailure(e, projectId);
        } catch (RuntimeException e) {
            throw checkConnectionFailure(e, projectId);
        }
    }
}
