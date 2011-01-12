/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.util.file;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.ImpendingOWLOntologyChangeListener;
import org.semanticweb.owlapi.model.MissingImportListener;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeBroadcastStrategy;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyChangeProgressListener;
import org.semanticweb.owlapi.model.OWLOntologyChangesVetoedListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFactory;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyLoaderListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLOntologyStorer;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

/**
 * @author krekeler
 * @author Nico Stieler
 *
 */
public final class OntologyInfoOntologyManager implements OWLOntologyManager {
    public static final class FormatValidatedBySetOntologyIDException extends OWLOntologyChangeException {
        private static final long serialVersionUID = 1658873666665023868L;
        public FormatValidatedBySetOntologyIDException(SetOntologyID change) {
            super(change, (Throwable)null);
        }
    }
    public static final class FormatValidatedByAddAxiomCount extends OWLOntologyChangeException {
        private static final long serialVersionUID = -7946762945877676355L;
        private final int _addAxiomCount;
        public FormatValidatedByAddAxiomCount(int addAxiomCount) {
            super((OWLOntologyChange)null, (Throwable)null);
            _addAxiomCount = addAxiomCount;
        }
        public int getAddAxiomCount() {
            return _addAxiomCount;
        }
    }

    private OWLOntologyChangeException _exception;
    private final OWLDataFactory _factory;
    private final int _addAxiomThreshold;
    private int _addAxiomCount;
    
    public OntologyInfoOntologyManager(int addAxiomThreshold) {
        _factory = OWLManager.createOWLOntologyManager().getOWLDataFactory();
        _addAxiomThreshold = addAxiomThreshold;
        _addAxiomCount = 0;
        _exception = null;
    }
    
    public OWLOntologyChangeException getException() {
        return _exception;
    }

    public OWLOntologyChangeException resetException() {
        OWLOntologyChangeException result = _exception;
        _exception = null;
        return result;
    }
    
    private void stop(OWLOntologyChangeException exception) throws OWLOntologyChangeException {
        _exception = exception;
        throw exception;
    }
    
    @Override
    public boolean isSilentMissingImportsHandling() {
        return true;
    }
    @Override
    public OWLDataFactory getOWLDataFactory() {
        return _factory;
    }
    
    
    @Override
    public List<OWLOntologyChange> addAxiom(OWLOntology ont, OWLAxiom axiom) throws OWLOntologyChangeException {
        return addAxioms(ont, Collections.singleton(axiom));
    }
    @Override
    public List<OWLOntologyChange> addAxioms(OWLOntology ont, Set<? extends OWLAxiom> axioms) throws OWLOntologyChangeException {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLAxiom axiom: axioms) {
            changes.add(new AddAxiom(ont, axiom));
        }
        return applyChanges(changes);
    }
    @Override
    public List<OWLOntologyChange> applyChange(OWLOntologyChange change) throws OWLOntologyChangeException {
        return applyChanges(Collections.singletonList(change));
    }
    @Override
    public List<OWLOntologyChange> applyChanges(List<? extends OWLOntologyChange> changes) throws OWLOntologyChangeException {
        for (OWLOntologyChange change: changes) {
            if (change instanceof SetOntologyID) {
                stop(new FormatValidatedBySetOntologyIDException((SetOntologyID)change));
            } else if (change instanceof AddAxiom) {
                _addAxiomCount++;
            }
        }
        if (_addAxiomThreshold != -1 && _addAxiomCount >= _addAxiomThreshold) {
            stop(new FormatValidatedByAddAxiomCount(_addAxiomCount));
        }
        // we apply nothing...
        return Collections.emptyList();
    }
    @Override
    public void makeLoadImportRequest(OWLImportsDeclaration declaration) {
        // ignore
    }

    @Override
    public OWLOntology getImportedOntology(OWLImportsDeclaration declaration) {
        return null;
    }
    @Override
    public Set<OWLOntology> getImports(OWLOntology ontology) throws UnknownOWLOntologyException {
        return Collections.emptySet();
    }
    @Override
    public Set<OWLOntology> getDirectImports(OWLOntology ontology) throws UnknownOWLOntologyException {
        return Collections.emptySet();
    }
    @Override
    public Set<OWLOntology> getImportsClosure(OWLOntology ontology) throws UnknownOWLOntologyException {
        return Collections.singleton(ontology);
    }

    
    
    @Override
    public void addIRIMapper(OWLOntologyIRIMapper mapper) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addMissingImportListener(MissingImportListener listener) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addOntologyChangeListener(OWLOntologyChangeListener listener) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addOntologyChangeListener(OWLOntologyChangeListener listener, OWLOntologyChangeBroadcastStrategy strategy) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addOntologyChangeProgessListener(OWLOntologyChangeProgressListener listener) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addOntologyFactory(OWLOntologyFactory factory) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addOntologyLoaderListener(OWLOntologyLoaderListener listener) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addOntologyStorer(OWLOntologyStorer storer) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void clearIRIMappers() {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean contains(IRI ontologyIRI) {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean contains(OWLOntologyID id) {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology createOntology() throws OWLOntologyCreationException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology createOntology(Set<OWLAxiom> axioms) throws OWLOntologyCreationException, OWLOntologyChangeException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology createOntology(IRI ontologyIRI) throws OWLOntologyCreationException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology createOntology(OWLOntologyID ontologyID) throws OWLOntologyCreationException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology createOntology(Set<OWLAxiom> axioms, IRI ontologyIRI) throws OWLOntologyCreationException, OWLOntologyChangeException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology createOntology(IRI ontologyIRI, Set<OWLOntology> ontologies) throws OWLOntologyCreationException, OWLOntologyChangeException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology createOntology(IRI ontologyIRI, Set<OWLOntology> ontologies, boolean copyLogicalAxiomsOnly) throws OWLOntologyCreationException, OWLOntologyChangeException {
        throw new UnsupportedOperationException();
    }
    @Override
    public Set<OWLOntology> getOntologies() {
        throw new UnsupportedOperationException();
    }
    @Override
    public Set<OWLOntology> getOntologies(OWLAxiom axiom) {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology getOntology(IRI ontologyIRI) throws UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology getOntology(OWLOntologyID ontologyID) throws UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntologyFormat getOntologyFormat(OWLOntology ontology) throws UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public IRI getOntologyDocumentIRI(OWLOntology ontology) throws UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public List<OWLOntology> getSortedImportsClosure(OWLOntology ontology) throws UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public Set<OWLOntology> getVersions(IRI ontology) {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology loadOntology(IRI ontologyIRI) throws OWLOntologyCreationException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology loadOntologyFromOntologyDocument(InputStream inputSource) throws OWLOntologyCreationException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology loadOntologyFromOntologyDocument(IRI uri) throws OWLOntologyCreationException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology loadOntologyFromOntologyDocument(File file) throws OWLOntologyCreationException {
        throw new UnsupportedOperationException();
    }
    @Override
    public OWLOntology loadOntologyFromOntologyDocument(OWLOntologyDocumentSource documentSource) throws OWLOntologyCreationException {
        throw new UnsupportedOperationException();
    }
    @Override
    public List<OWLOntologyChange> removeAxiom(OWLOntology ont, OWLAxiom axiom) throws OWLOntologyChangeException {
        throw new UnsupportedOperationException();
    }
    @Override
    public List<OWLOntologyChange> removeAxioms(OWLOntology ont, Set<? extends OWLAxiom> axioms) throws OWLOntologyChangeException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void removeIRIMapper(OWLOntologyIRIMapper mapper) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void removeMissingImportListener(MissingImportListener listener) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void removeOntology(OWLOntology ontology) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void removeOntologyChangeListener(OWLOntologyChangeListener listener) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void removeOntologyChangeProgessListener(OWLOntologyChangeProgressListener listener) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void removeOntologyFactory(OWLOntologyFactory factory) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void removeOntologyLoaderListener(OWLOntologyLoaderListener listener) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void removeOntologyStorer(OWLOntologyStorer storer) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void saveOntology(OWLOntology ontology) throws OWLOntologyStorageException, UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void saveOntology(OWLOntology ontology, IRI physicalURI) throws OWLOntologyStorageException, UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void saveOntology(OWLOntology ontology, OWLOntologyFormat ontologyFormat) throws OWLOntologyStorageException, UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void saveOntology(OWLOntology ontology, OutputStream outputTarget) throws OWLOntologyStorageException, UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void saveOntology(OWLOntology ontology, OWLOntologyFormat ontologyFormat, IRI physicalURI) throws OWLOntologyStorageException, UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void saveOntology(OWLOntology ontology, OWLOntologyFormat ontologyFormat, OutputStream outputTarget) throws OWLOntologyStorageException, UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void saveOntology(OWLOntology ontology, OWLOntologyDocumentTarget documentTarget) throws OWLOntologyStorageException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void saveOntology(OWLOntology ontology, OWLOntologyFormat ontologyFormat, OWLOntologyDocumentTarget documentTarget) throws OWLOntologyStorageException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setDefaultChangeBroadcastStrategy(OWLOntologyChangeBroadcastStrategy strategy) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setOntologyFormat(OWLOntology ontology, OWLOntologyFormat ontologyFormat) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setOntologyDocumentIRI(OWLOntology ontology, IRI physicalURI) throws UnknownOWLOntologyException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setSilentMissingImportsHandling(boolean b) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addImpendingOntologyChangeListener(ImpendingOWLOntologyChangeListener arg0) {
        // TODO OWL API 3.1.0
    }
    @Override
    public void addOntologyChangesVetoedListener(OWLOntologyChangesVetoedListener arg0) {
        // TODO OWL API 3.1.0
    }
    @Override
    public Collection<OWLOntologyFactory> getOntologyFactories() {
        // TODO OWL API 3.1.0
        return null;
    }
    @Override
    public void removeImpendingOntologyChangeListener(ImpendingOWLOntologyChangeListener arg0) {
        // TODO OWL API 3.1.0
    }
    @Override
    public void removeOntologyChangesVetoedListener(OWLOntologyChangesVetoedListener arg0) {
        // TODO OWL API 3.1.0
    }
}
