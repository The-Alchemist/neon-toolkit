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

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.coode.owlapi.functionalparser.OWLFunctionalSyntaxParserFactory;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxParserFactory;
import org.coode.owlapi.obo.parser.OBOOntologyFormat;
import org.coode.owlapi.obo.parser.OBOParserFactory;
import org.coode.owlapi.owlxmlparser.OWLXMLParserFactory;
import org.coode.owlapi.rdfxml.parser.RDFXMLParserFactory;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.ReaderDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.xml.sax.SAXException;

import uk.ac.manchester.cs.owl.owlapi.turtle.parser.TurtleOntologyParserFactory;
import de.uulm.ecs.ai.owlapi.krssparser.KRSS2OWLParserFactory;
import de.uulm.ecs.ai.owlapi.krssparser.KRSS2OntologyFormat;

/**
 * @author krekeler
 *
 */
public class OWLFileUtilities {
    private static final int ADD_AXIOM_THRESHOLD = 10000;
    private static abstract class OntologyFormatFactory {
        private final String _formatName;
        private final OWLParserFactory _parserFactory;
        public OntologyFormatFactory(OWLParserFactory parserFactory) {
            _formatName = createOntologyFormat().toString();
            _parserFactory = parserFactory;
        }
        public String getFormatName() {
            return _formatName;
        }
        public OWLParserFactory getParserFactory() {
            return _parserFactory;
        }
        public abstract OWLOntologyFormat createOntologyFormat();
    }
    
    private static Set<OntologyFormatFactory> _factories;
    static {
        Set<OntologyFormatFactory> factories = new LinkedHashSet<OntologyFormatFactory>();
        factories.add(new OntologyFormatFactory(new RDFXMLParserFactory()) {
            @Override
            public OWLOntologyFormat createOntologyFormat() {
                return new RDFXMLOntologyFormat();
            }
        });
        factories.add(new OntologyFormatFactory(new OWLXMLParserFactory()) {
            @Override
            public OWLOntologyFormat createOntologyFormat() {
                return new OWLXMLOntologyFormat();
            }
        });
        factories.add(new OntologyFormatFactory(new TurtleOntologyParserFactory()) {
            @Override
            public OWLOntologyFormat createOntologyFormat() {
                return new TurtleOntologyFormat();
            }
        });
        factories.add(new OntologyFormatFactory(new ManchesterOWLSyntaxParserFactory()) {
            @Override
            public OWLOntologyFormat createOntologyFormat() {
                return new ManchesterOWLSyntaxOntologyFormat();
            }
        });
        factories.add(new OntologyFormatFactory(new OWLFunctionalSyntaxParserFactory()) {
            @Override
            public OWLOntologyFormat createOntologyFormat() {
                return new OWLFunctionalSyntaxOntologyFormat();
            }
        });
        factories.add(new OntologyFormatFactory(new KRSS2OWLParserFactory()) {
            @Override
            public OWLOntologyFormat createOntologyFormat() {
                return new KRSS2OntologyFormat();
            }
        });
        factories.add(new OntologyFormatFactory(new OBOParserFactory()) {
            @Override
            public OWLOntologyFormat createOntologyFormat() {
                return new OBOOntologyFormat();
            }
        });
        _factories = Collections.unmodifiableSet(factories);
    }
    
    public static Set<OWLOntologyFormat> getParsableOntologyFormats() {
        Set<OWLOntologyFormat> result = new LinkedHashSet<OWLOntologyFormat>();
        for (OntologyFormatFactory factory: _factories) {
            result.add(factory.createOntologyFormat());
        }
        return result;
    }
    
    public static OWLOntologyInfo getOntologyInfo(URI physicalURI) throws UnknownOWLOntologyFormatException {
        return getOntologyInfo(IRI.create(physicalURI));
    }
    public static OWLOntologyInfo getOntologyInfo(IRI physicalURI) throws UnknownOWLOntologyFormatException {
        return getOntologyInfo(physicalURI, ADD_AXIOM_THRESHOLD);
    }
    public static OWLOntologyInfo getOntologyInfo(IRI physicalURI, int addAxiomThreshold) throws UnknownOWLOntologyFormatException {
        return getOntologyInfo(new IRIDocumentSource(physicalURI), addAxiomThreshold);
    }
    public static OWLOntologyInfo getOntologyInfo(OWLOntologyDocumentSource source) throws UnknownOWLOntologyFormatException {
        return getOntologyInfo(source, ADD_AXIOM_THRESHOLD);
    }
    public static OWLOntologyInfo getOntologyInfo(OWLOntologyDocumentSource source, int addAxiomThreshold) throws UnknownOWLOntologyFormatException {
        // if source is reader or stream based we need to do resets between trying different parsers
        ResetReader resetReader = null;
        if (source.isReaderAvailable()) {
            resetReader = new ResetReader(source.getReader());
        } else if (source.isInputStreamAvailable()) {
            resetReader = new ResetReader(new InputStreamReader(source.getInputStream()));
        }
        OntologyInfoOntologyManager manager = new OntologyInfoOntologyManager(addAxiomThreshold);
        OWLOntology ontology;
        try {
            ontology = OWLManager.createOWLOntologyManager().createOntology(IRI.create("internal:iri:never:used:by:someone:needed:to:avoid:owlapi:implementation:bugs:" + ((long)(Long.MAX_VALUE * Math.random())))); //$NON-NLS-1$
        } catch (OWLOntologyCreationException e) {
            // should not happen
            throw new OWLRuntimeException(e);
        }
        Map<String,Exception> triedOntologyFormatExceptions = new LinkedHashMap<String,Exception>();
        for (OntologyFormatFactory factory: _factories) {
            manager.resetException();
            OWLParser parser = factory.getParserFactory().createParser(manager);
            // the factory should set the manager already... but some don't
            parser.setOWLOntologyManager(manager);
            try {
                if (resetReader != null) {
                    resetReader.resetToStart();
                    parser.parse(new ReaderDocumentSource(resetReader, source.getDocumentIRI()), ontology);
                } else {
                    parser.parse(source, ontology);
                }
                // some parser may skip the exception...
                if (manager.getException() != null) {
                    throw manager.getException();
                }
                // no parser exceptions, no ontology id found => format is correct and the ontology is anonymous 
                return new OWLOntologyInfo(new OWLOntologyID(), factory.createOntologyFormat());
            } catch (OWLOntologyCreationException e) {
                // we don't know here if e is caused a an invalid format or an error in the ontology... ignore
                OWLOntologyInfo info;
                if (manager.getException() != null) {
                    info = checkFormatValidatedException(factory, manager.getException());
                } else {
                    info = checkFormatValidatedException(factory, e);
                }
                if (info != null) {
                    return info;
                }
                triedOntologyFormatExceptions.put(factory.getFormatName(), e);
            } catch (Exception e) {
                // MAPI implementation bug, should never happen
                OWLOntologyInfo info;
                if (manager.getException() != null) {
                    info = checkFormatValidatedException(factory, manager.getException());
                } else {
                    info = checkFormatValidatedException(factory, e);
                }
                if (info != null) {
                    return info;
                }
                triedOntologyFormatExceptions.put(factory.getFormatName(), e);
            }
        }
        throw new UnknownOWLOntologyFormatException(triedOntologyFormatExceptions);
    }
    
    private static OWLOntologyInfo checkFormatValidatedException(OntologyFormatFactory factory, Throwable t) {
        Throwable cause = t;
        while (cause != null) {
            if (cause instanceof OntologyInfoOntologyManager.FormatValidatedBySetOntologyIDException) {
                return new OWLOntologyInfo(((SetOntologyID)((OntologyInfoOntologyManager.FormatValidatedBySetOntologyIDException)cause).getChange()).getNewOntologyID(), factory.createOntologyFormat());
            } else if (cause instanceof OntologyInfoOntologyManager.FormatValidatedByAddAxiomCount) {
                // no ontology id found after reading at least ASSUME_ANONYMOUS_ONTOLOGY_AXIOM_THRESHOLD axioms... assume an anonymous ontology
                return new OWLOntologyInfo(new OWLOntologyID(), factory.createOntologyFormat());
            }
            if (cause instanceof SAXException) {
                // some special handling for SAXExceptions
                Exception exception = ((SAXException)cause).getException();
                if (exception != cause) {
                    OWLOntologyInfo info = checkFormatValidatedException(factory, exception);
                    if (info != null) {
                        return info;
                    }
                }
            }
            if (cause.getCause() == cause) {
                cause = null;
            } else {
                cause = cause.getCause();
            }
        }
        return null;
    }
    
    public static void transformOntology(URI physicalSourceURI, OWLOntologyFormat ontologyFormat, URI physicalTargetURI) throws OWLOntologyCreationException, UnknownOWLOntologyException, OWLOntologyStorageException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        manager.setSilentMissingImportsHandling(true);
        manager.addIRIMapper(new OWLOntologyIRIMapper() {
            @Override
            public IRI getDocumentIRI(IRI ontologyIRI) {
                return IRI.create("file:unknownSSP#unknownFragment");
            }
        });
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(physicalSourceURI));
        manager.saveOntology(ontology, ontologyFormat, IRI.create(physicalTargetURI));
    }
}
