/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.syntax.manchester;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxClassExpressionParser;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.util.URIUtils;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;

/**
 * @author Michael Erdmann
 * @author Nico Stieler
 */
public class ManchesterSyntaxManager implements ISyntaxManager {

    private String _syntaxName;
    
    public static final String RESERVED_NAMESPACE = Messages.NewClazzAction_0;

    private static IToken NAMESPACE_TOKEN = null;
    public static IToken URI_TOKEN = null;
    private static IToken STRING_TOKEN = null;
    private static IToken SPECIAL_CHAR_TOKEN = null;
    private static IToken KEYWORD_TOKEN = null;
    private static IToken FRAMES_SLOT_TOKEN = null;
    private static IToken FRAMES_TOKEN = null;
//    private static final IToken FACET_TOKEN = new Token(new TextAttribute(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED), null, SWT.BOLD));
//    private static final IToken CLASS_TOKEN = new Token(new TextAttribute(Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE), null, SWT.BOLD));

    static {
            Display display = Display.getDefault();
            NAMESPACE_TOKEN     = new Token(new TextAttribute(display.getSystemColor(SWT.COLOR_BLUE), null, SWT.NONE));
            URI_TOKEN           = new Token(new TextAttribute(display.getSystemColor(SWT.COLOR_BLUE), null, SWT.NONE));
            STRING_TOKEN        = new Token(new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_GREEN), null, SWT.NONE));
            SPECIAL_CHAR_TOKEN  = new Token(new TextAttribute(display.getSystemColor(SWT.COLOR_MAGENTA), null, SWT.BOLD));
            KEYWORD_TOKEN       = new Token(new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_MAGENTA), null, SWT.BOLD));
            FRAMES_SLOT_TOKEN   = new Token(new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_MAGENTA), null, SWT.ITALIC));
            FRAMES_TOKEN        = new Token(new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_MAGENTA), null, SWT.BOLD | SWT.ITALIC));
    }

    String[] WS = {" ", "\t", "\n", "\r", ","}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    //Saves a scanner for each namespace. This prevent a reload every time a user types a character.
    private Map<OWLNamespaces, RuleBasedScanner> scanners = new HashMap<OWLNamespaces,RuleBasedScanner>();

    public ManchesterSyntaxManager() {
    }

    @Override
    public OWLDataRange parseDataRange(String value, OWLModel owlModel) throws NeOnCoreException {
        try {
            return getManchesterOWLSyntaxEditorParser(owlModel, value).parseDataRange();
        } catch (ParserException e) {
            throw new InternalNeOnException(e);
        }
    }

    @Override
    public String parseUri(String value, OWLModel owlModel) throws NeOnCoreException {
        try {
            String expandedValue = owlModel.getNamespaces().expandString(value);
            String error = URIUtils.isValidURI(IRIUtils.ensureValidIdentifierSyntax(expandedValue));
            if (error != null) {
                expandedValue = RESERVED_NAMESPACE + expandedValue;
            }
            expandedValue = IRIUtils.ensureValidIRISyntax(expandedValue);
            return new ManchesterOWLSyntaxEditorParser(owlModel.getOWLDataFactory(), expandedValue).parseIRI().toString();//NICO problem is maybe here??

        } catch (ParserException e) {
            throw new InternalNeOnException(e);
        } catch (IllegalArgumentException e) {
            // hack for mapi bug
            if (e.getCause() instanceof URISyntaxException) {
                throw new InternalNeOnException(e);
            }
            throw e;
        }
    }

    @Override
    public OWLClassExpression parseDescription(String value, OWLModel owlModel) throws NeOnCoreException {
        try {
//            Logger.getLogger(getClass()).error("TODO: migration"); //$NON-NLS-1$
            if (value.startsWith(IRIUtils.TURTLE_IRI_OPEN) && value.endsWith(IRIUtils.TURTLE_IRI_CLOSE)) {
                return owlModel.getOWLDataFactory().getOWLClass(OWLUtilities.toIRI(value.substring(1, value.length() - 1)));
            } else {
                ManchesterOWLSyntaxClassExpressionParser parser = new ManchesterOWLSyntaxClassExpressionParser(owlModel.getOWLDataFactory(), new DefaultEntityChecker(owlModel));
                return parser.parse(value);
            }
        } catch (ParserException e) {
            if(new DefaultEntityChecker(owlModel).getOWLIndividual(value) != null){
                throw new InternalNeOnException("Range "+value+" is an individual but a description is expected. \nChoose "+OWLCommandUtils.HAS_VALUE+" as quantifier or a description as range.");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            throw new InternalNeOnException(e);
        } catch (IllegalArgumentException e) {
            // hack for mapi bug
            if (e.getCause() instanceof URISyntaxException) {
                throw new InternalNeOnException(e);
            }
            throw e;
        } catch (NullPointerException e) {
            throw new InternalNeOnException(e);
        }
    }

    private class DefaultEntityChecker implements OWLEntityChecker {

        private OWLDataFactory dataFactory;
        private OWLModel owlModel;
        private boolean allowUndeclared;

        private List<OWLModel> ontologies;

        private Map<String,OWLDatatype> dataTypeNameMap;

        public DefaultEntityChecker(OWLModel owlModel) throws NeOnCoreException {
            this(owlModel, false);
        }
        
        public DefaultEntityChecker(OWLModel owlModel, boolean allowUndeclared) throws NeOnCoreException {
            this.dataFactory = owlModel.getOWLDataFactory();
            this.owlModel = owlModel;
            this.allowUndeclared = allowUndeclared;

            this.ontologies = getOntologies(owlModel, true);

            dataTypeNameMap = new HashMap<String,OWLDatatype>();
            for (XSDVocabulary v: XSDVocabulary.values()) {
                dataTypeNameMap.put(v.getIRI().toURI().getFragment(), dataFactory.getOWLDatatype(v.getIRI())); //NICO OWL API 3.1.0 URI --> IRI: maybe its possible to remove the toURI()
                dataTypeNameMap.put("xsd:" + v.getIRI().toURI().getFragment(), dataFactory.getOWLDatatype(v.getIRI())); //$NON-NLS-1$ //NICO OWL API 3.1.0 URI --> IRI: maybe its possible to remove the toURI()
            }
        }

        @Override
        public OWLClass getOWLClass(String name) {
            if (name.equals("Thing") || name.equals("owl:Thing")) { //$NON-NLS-1$ //$NON-NLS-2$
                return dataFactory.getOWLThing();
            } else if (name.equals("Nothing") || name.equals("owl:Nothing")) { //$NON-NLS-1$ //$NON-NLS-2$
                return dataFactory.getOWLNothing();
            } else
                // don' t first check if a class with the passed URI is contained in our 
                // ontologies, always return a class. see issue http://buggy.ontoprise.de/bugs/show_bug.cgi?id=12849
            return dataFactory.getOWLClass(getIRI(name));

        }

        private IRI getIRI(String name) {
            String uri = null;
            try {
                uri = OWLPlugin.getDefault().getSyntaxManager().parseUri(name, owlModel);
            } catch (NeOnCoreException e) {
                return null;
            }
            return OWLUtilities.toIRI(uri);//NICO changed
        }

        @Override
        public OWLObjectProperty getOWLObjectProperty(String name) {
            try {
                IRI iri;
                try {
                    iri = getIRI(name);
                } catch (Exception e) {
                    return null;
                }
                for (OWLModel model: ontologies) {
                    if (model.getOntology().containsObjectPropertyInSignature(iri) || allowUndeclared) {
                        return dataFactory.getOWLObjectProperty(getIRI(name));
                    }
                }
                return null;
            } catch (NeOnCoreException e) {
                return null;
            }
        }

        private List<OWLModel> getOntologies(OWLModel owlModel, boolean includeImportedOntologies) throws NeOnCoreException {
            ontologies = new LinkedList<OWLModel>();
            ontologies.add(owlModel);
            if (includeImportedOntologies)
                ontologies.addAll(owlModel.getAllImportedOntologies());
            return ontologies;
        }

        @Override
        public OWLDataProperty getOWLDataProperty(String name) {
            try {
                IRI iri;
                try {
                    iri = getIRI(name);
                } catch (Exception e) {
                    return null;
                }
                for (OWLModel model: ontologies) {
                    if (model.getOntology().containsDataPropertyInSignature(iri) || allowUndeclared) {
                        return dataFactory.getOWLDataProperty(getIRI(name));
                    }
                }
                return null;

            } catch (NeOnCoreException e) {
                return null;
            }
        }

        @Override
        public OWLNamedIndividual getOWLIndividual(String name) {
            
            //A check if a individuals is in the KB is not necessary, because this 
            //method is only called if HAS_VALUE is selected. Selecting a not existing individual 
            //is allowed.
            if(getIRI(name) != null) {
                IRI iri;
                try {
                    iri = getIRI(name);
                } catch (Exception e) {
                    return null;
                }
                return dataFactory.getOWLNamedIndividual(iri);
            } else {
                return null;
            }
        }

        @Override
        public OWLDatatype getOWLDatatype(String name) {
            try {
                IRI iri;
                try {
                    iri = getIRI(name);
                } catch (Exception e) {
                    return null;
                }
                for (OWLModel model: ontologies) {
                    if (model.getOntology().containsDatatypeInSignature(iri) || dataTypeNameMap.containsKey(name) || allowUndeclared) {
                        return dataFactory.getOWLDatatype(getIRI(name));
                    }
                }
                return null;

            } catch (NeOnCoreException e) {
                return null;
            }
        }

        @Override
        public OWLAnnotationProperty getOWLAnnotationProperty(String name) {
            try {
                IRI iri;
                try {
                    iri = getIRI(name);
                } catch (Exception e) {
                    return null;
                }
                for (OWLModel model: ontologies) {
                    if (model.getOntology().containsAnnotationPropertyInSignature(iri) || allowUndeclared) {
                        return dataFactory.getOWLAnnotationProperty(getIRI(name));
                    }
                }
                return null;

            } catch (NeOnCoreException e) {
                return null;
            }
        }
    }
    /**
     * @param owlModel
     * @param value
     * @return
     * @throws NeOnCoreException 
     * @throws NeOnCoreException 
     * @throws ParserException 
     * @throws OWLOntologyChangeException 
     * @throws OWLOntologyCreationException 
     */
    private ManchesterOWLSyntaxEditorParser getManchesterOWLSyntaxEditorParser(OWLModel owlModel, String value) throws NeOnCoreException{
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(owlModel.getOWLDataFactory(), value);
        // if the current context is not parseDescription() we know exactly what entity we want to parse,  
        // so we also allow new, undeclared entities to be returned.
        boolean allowUndeclared = true;
        parser.setOWLEntityChecker(new DefaultEntityChecker(owlModel, allowUndeclared));
        return parser;
    }

    @Override
    public OWLLiteral parseConstant(String value, OWLModel owlModel) throws NeOnCoreException {
        try {
            return getManchesterOWLSyntaxEditorParser(owlModel, value).parseLiteral();//parseConstant() does not exist anymore
        } catch (ParserException e) {
            throw new InternalNeOnException(e);
        } catch (IllegalArgumentException e) {
            // hack for mapi bug
            if (e.getCause() instanceof URISyntaxException) {
                throw new InternalNeOnException(e);
            }
            throw e;
        }
    }

    @Override
    public OWLIndividual parseIndividual(String value, OWLModel owlModel) throws NeOnCoreException {
        try {
            return getManchesterOWLSyntaxEditorParser(owlModel, value).parseIndividual();
        } catch (ParserException e) {
            throw new InternalNeOnException(e);
        } catch (IllegalArgumentException e) {
            // hack for mapi bug
            if (e.getCause() instanceof URISyntaxException) {
                throw new InternalNeOnException(e);
            }
            throw e;
        }
    }

    @Override
    public OWLDataProperty parseDataProperty(String value, OWLModel owlModel) throws NeOnCoreException {
        try {
            return getManchesterOWLSyntaxEditorParser(owlModel, value).parseDataProperty();
        } catch (ParserException e) {
            throw new InternalNeOnException(e);
        } catch (IllegalArgumentException e) {
            // hack for mapi bug
            if (e.getCause() instanceof URISyntaxException) {
                throw new InternalNeOnException(e);
            }
            throw e;
        }
    }

    @Override
    public OWLObjectPropertyExpression parseObjectProperty(String value, OWLModel owlModel) throws NeOnCoreException {
        try {
            return getManchesterOWLSyntaxEditorParser(owlModel, value).parseObjectPropertyExpression();
        } catch (ParserException e) {
            throw new InternalNeOnException(e);
        } catch (IllegalArgumentException e) {
            // hack for mapi bug
            if (e.getCause() instanceof URISyntaxException) {
                throw new InternalNeOnException(e);
            }
            throw e;
        }
    }

    @Override
    public List<OWLObjectPropertyExpression> parseObjectPropertyChain(String value, OWLModel owlModel) throws NeOnCoreException {
        try {
            return getManchesterOWLSyntaxEditorParser(owlModel, value).parseObjectPropertyChain();
        } catch (ParserException e) {
            throw new InternalNeOnException(e);
        } catch (IllegalArgumentException e) {
            // hack for mapi bug
            if (e.getCause() instanceof URISyntaxException) {
                throw new InternalNeOnException(e);
            }
            throw e;
        }
    }


    @Override
    public OWLAnnotationProperty parseAnnotationProperty(String value, OWLModel owlModel) throws NeOnCoreException {
        try {
//            OWLDataFactory factory = owlModel.getOWLDataFactory();
//            return factory.getOWLAnnotationProperty(getManchesterOWLSyntaxEditorParser(owlModel, value).parseIRI());

            return getManchesterOWLSyntaxEditorParser(owlModel, value).parseAnnotationProperty();
        } catch (ParserException e) {
            throw new InternalNeOnException(e);
        } catch (IllegalArgumentException e) {
            // hack for mapi bug
            if (e.getCause() instanceof URISyntaxException) {
                throw new InternalNeOnException(e);
            }
            throw e;
        }
    }

    
    @Override
    public ITokenScanner getComplexClassScanner(OWLNamespaces namespaces) {
        RuleBasedScanner scanner = scanners.get(namespaces);
        if(scanner == null){
            List<IRule> rules = new ArrayList<IRule>();
            createUriRule(rules);
            createQuotedStringRule(rules);
            createBracketRule(rules);
            createFacetRule(rules);
            createQNameRule(rules, namespaces);
            createKeywordRule(rules);
            createFrameSlotRule(rules);
            createFramesRule(rules);
    
            scanner = new RuleBasedScanner();
            scanner.setRules(rules.toArray(new IRule[rules.size()]));
            scanners.put(namespaces, scanner);
        }
        return scanner;
    }

    @Override
    public IContentProposalProvider getProposalProvider() {
        return new ManchesterSyntaxProposalProvider();
    }

    @Override
    public String getTextualRepresentation(OWLEntity p, OWLModel owlModel) {
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx<Object> visitor = getVisitor(owlModel, idDisplayStyle);
        String[] array = (String[]) p.accept(visitor);
        String id = OWLGUIUtilities.getEntityLabel(array);
        return id;
    }

    @Override
    public String getTextualRepresentation(OWLClassExpression p, OWLModel owlModel) {
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx<Object> visitor = getVisitor(owlModel, idDisplayStyle);
        String[] array = (String[]) p.accept(visitor);
        String id = OWLGUIUtilities.getEntityLabel(array);
        return id;
    }

    @Override
    public String getTextualRepresentation(OWLDataRange p, OWLModel owlModel) {
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx<Object> visitor = getVisitor(owlModel, idDisplayStyle);
        String[] array = (String[]) p.accept(visitor);
        String id = OWLGUIUtilities.getEntityLabel(array);
        return id;
    }

    @Override
    public String getSyntaxName() {
        return _syntaxName;
    }

    @Override
    public void setSyntaxName(String syntaxName) {
        _syntaxName = syntaxName;
    }

    private void createBracketRule(List<IRule> rules) {
        String[] brackets = ManchesterSyntaxConstants.getBrackets();
        rules.add(new WordRule(new ManchesterSingleCharacterDetector(brackets), SPECIAL_CHAR_TOKEN));
    }

    private void createFacetRule(List<IRule> rules) {
        String[] facets = ManchesterSyntaxConstants.getFacets();
        getKeywordRule(facets, SPECIAL_CHAR_TOKEN, rules);
    }

    private void createKeywordRule(List<IRule> rules) {
        String[] keywords = ManchesterSyntaxConstants.getClassConstructorKeywords();
        getKeywordRule(keywords, KEYWORD_TOKEN, rules);

        // NOT can also be at the start of the description, thus a leading WS is not required
        for (String end: WS) {
            rules.add(new KeywordPatternRule(ManchesterSyntaxConstants.NOT + end, KEYWORD_TOKEN, (char) 0, true));
        }

        String[] facetKeywords = ManchesterSyntaxConstants.getFacetKeywords();
        getKeywordRule(facetKeywords, KEYWORD_TOKEN, rules);

        String[] restrictions = ManchesterSyntaxConstants.getRestrictionKeywords();
        getKeywordRule(restrictions, KEYWORD_TOKEN, rules);
        
        String[] o = ManchesterSyntaxConstants.getPropertyChainKeyword();
        getKeywordRule(o, KEYWORD_TOKEN, rules);
    }

    private void createFramesRule(List<IRule> rules) {
        String[] keywords = ManchesterSyntaxConstants.getFramesKeywords();
        for (String keyword: keywords) {
            rules.add(new KeywordPatternRule(keyword, FRAMES_TOKEN, (char) 0, true));
        }
    }

    private void createFrameSlotRule(List<IRule> rules) {
        String[] keywords = ManchesterSyntaxConstants.getFrameSlotKeywords();
        getKeywordRule(keywords, FRAMES_SLOT_TOKEN, rules);
    }

    private void createUriRule(List<IRule> rules) {
        rules.add(new PatternRule(IRIUtils.TURTLE_IRI_OPEN, IRIUtils.TURTLE_IRI_CLOSE, URI_TOKEN, (char) 0, true)); 
    }

    private void createQuotedStringRule(List<IRule> rules) {
        rules.add(new PatternRule("\"", "\"", STRING_TOKEN, '\\', false));  //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void createQNameRule(List<IRule> rules, OWLNamespaces namespaces) {
        Iterator<String> iter = namespaces.prefixes();
        while (iter.hasNext()) {
            String prefix = (String) iter.next();
            PatternRule rule = new KeywordPatternRule(prefix + ":", NAMESPACE_TOKEN, (char) 0, true); //$NON-NLS-1$
            rules.add(rule);
        }
    }

    private void getKeywordRule(String[] keywords, IToken token, List<IRule> rules) {
        for (int i = 0; i < keywords.length; i++) {

            for (String start: WS) {
                for (String end: WS) {
                    rules.add(new KeywordPatternRule(start + keywords[i] + end, token, (char) 0, true));
                }
            }
        }
    }

    private static class ManchesterSingleCharacterDetector implements IWordDetector {
        List<String> _characters;

        public ManchesterSingleCharacterDetector(String[] characters) {
            _characters = Arrays.asList(characters);
        }

        @Override
        public boolean isWordPart(char character) {
            return false;
        }

        @Override
        public boolean isWordStart(char character) {
            return _characters.contains(character + ""); //$NON-NLS-1$
        }
    }

    private static class KeywordPatternRule extends PatternRule {
        public KeywordPatternRule(String keyword, IToken token, char escapeCharacter, boolean breaksOnEOL) {
            super(keyword, null, token, escapeCharacter, breaksOnEOL);
        }

        @Override
        protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
            int c = scanner.read();
            if (c == fStartSequence[0]) {
                for (int i = 1; i < fStartSequence.length; i++) {
                    c = scanner.read();
                    if (c != fStartSequence[i]) {
                        for (int j = i; j >= 0; j--) {
                            scanner.unread();
                        }
                        return Token.UNDEFINED;
                    }
                }
                return fToken;
            }
            scanner.unread();
            return Token.UNDEFINED;
        }

        @Override
        protected boolean endSequenceDetected(ICharacterScanner scanner) {
            return false;
        }

    }

    public OWLObjectVisitorEx<Object> getVisitor() {
        return getVisitor(null);
    }

    @Override
    public OWLObjectVisitorEx<Object> getVisitor(OWLModel owlModel) {
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        String language = store.getString(OWLPlugin.SPECIFIC_LANGUAGE_PREFERENCE);

        return new ManchesterSyntaxVisitor(owlModel, language);
    }

    @Override
    public OWLObjectVisitorEx<Object> getVisitor(OWLModel owlModel, int idDisplayStyle) {
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        String language = store.getString(OWLPlugin.SPECIFIC_LANGUAGE_PREFERENCE);

        return new ManchesterSyntaxVisitorIdType(owlModel, language, idDisplayStyle);
    }

}
