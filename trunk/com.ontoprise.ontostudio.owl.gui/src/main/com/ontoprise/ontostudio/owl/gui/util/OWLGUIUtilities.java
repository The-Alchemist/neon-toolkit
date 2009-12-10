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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.ITreeElementPath;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.TreeProviderManager;
import org.neontoolkit.gui.util.PerspectiveChangeHandler;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.commands.CreateValidUri;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualView;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewContentProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.gui.util.textfields.AbstractOwlTextField;
import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.util.InternalParser;
import com.ontoprise.ontostudio.owl.model.util.InternalParserException;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;
import com.ontoprise.ontostudio.owl.perspectives.OWLPerspective;

public class OWLGUIUtilities {

    public static final String BUTTON_LABEL_ADD = Messages.OWLGUIUtilities_0;
    public static final String BUTTON_LABEL_REMOVE = Messages.OWLGUIUtilities_1;
    public static final String BUTTON_LABEL_EDIT = Messages.OWLGUIUtilities_2;
    public static final String BUTTON_LABEL_SAVE = Messages.OWLGUIUtilities_3;
    public static final String BUTTON_LABEL_CANCEL = Messages.OWLGUIUtilities_4;

    public static final String TEXT_WIDGET_DATA_ID = "text_widget_data_id"; //$NON-NLS-1$
    
    private static final String UTF8 = "utf-8"; //$NON-NLS-1$
    private static final String HEX_CHARACTERS = "0123456789ABCDEF"; //$NON-NLS-1$

	public static final int LANGUAGE_SELECT_BOX_WIDTH = 50;

	public static final String[] QUANTOR_TYPES = {
	    OWLCommandUtils.SOME,
	    OWLCommandUtils.ALL,
	    OWLCommandUtils.HAS_SELF,
	    OWLCommandUtils.HAS_VALUE,
	    OWLCommandUtils.AT_LEAST_MIN,
	    OWLCommandUtils.AT_MOST_MAX,
	    OWLCommandUtils.EXACTLY_CARDINALITY};
	
	public static final int QUANTIFIER_COMBO_WIDTH = getQuantifierComboWidth();
	private static final int getQuantifierComboWidth() {
		int width = 111;
		if(System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) { //$NON-NLS-1$ //$NON-NLS-2$
			// not a windows platform
			width = 140;
		}
		return width;
	}
	
	// BUTTON WIDTH could be removed if the automatic width of buttons is OK, otherwise platform specific settings should be considered
	@SuppressWarnings("unused")
    private static final int BUTTON_WIDTH = getButtonWidth();
	private static final int getButtonWidth() {
		int width = 50;
		if(System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) { //$NON-NLS-1$ //$NON-NLS-2$
			// not a windows platform
			width = 80;
		}
		return width;
	}
    public static CCombo createLanguageComboBox(Composite parent, boolean enabled) {
        GridData data = new GridData();
		data.widthHint = LANGUAGE_SELECT_BOX_WIDTH;
        data.verticalAlignment = SWT.TOP;
        String[] languages = NeOnUIPlugin.getDefault().getLanguages();
        String[] languageComboContents = new String[languages.length + 1];
        languageComboContents[0] = OWLCommandUtils.EMPTY_LANGUAGE;
        System.arraycopy(languages, 0, languageComboContents, 1, languages.length);
        return createComboWidget(languageComboContents, parent, data, SWT.BORDER, enabled);
    }

    public static Button createAddButton(Composite parent, boolean enabled) {
        return createButton(parent, enabled, BUTTON_LABEL_ADD);
    }

    public static Button createAdd2Button(Composite parent, boolean enabled) {
        Button button = createButton(parent, enabled, BUTTON_LABEL_ADD);
        ((GridData) button.getLayoutData()).horizontalSpan = 2;
        return button;
    }

    public static Button createEditButton(Composite parent, boolean enabled) {
        return createButton(parent, enabled, BUTTON_LABEL_EDIT);
    }

    public static Button createRemoveButton(Composite parent, boolean enabled) {
        return createButton(parent, enabled, BUTTON_LABEL_REMOVE);
    }

    public static Button createSaveButton(Composite parent, boolean enabled) {
        return createButton(parent, enabled, BUTTON_LABEL_SAVE);
    }

    public static Button createCancelButton(Composite parent, boolean enabled) {
        return createButton(parent, enabled, BUTTON_LABEL_CANCEL);
    }

    public static Button createButton(Composite parent, boolean enabled, String label) {
        Button button = new Button(parent, SWT.PUSH | SWT.CENTER);

        GridData data = new GridData();
    	//data.widthHint = BUTTON_WIDTH;
		data.heightHint = 20;	

        data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.CENTER;
        data.grabExcessHorizontalSpace = false;

        button.setText(label);
        button.setLayoutData(data);
        button.setEnabled(enabled);
        return button;
    }

    // /**
    // * Guesses (!) the fragment of <code>uri</code> and unescapes it.
    // * @param uri
    // * @return A version of <code>uri</code> in which the fragment is not escaped.
    // * This especially means, that the returned value is not a valid URI in general.
    // */
    // public static String unescapeURI(String uri) {
    // int end = Namespaces.guessNamespaceEnd(uri);
    // if (end == -1) {
    // return unescapeFragment(uri);
    // } else {
    // return uri.substring(0, end + 1) + unescapeFragment(uri.substring(end + 1));
    // }
    // }

    /**
     * Unescape the fragment of an URI, i.e. translate an escaped fragment to its non escaped version.
     * 
     * @param fragment
     * @return The non escaped version of <code>fragment</code>.
     */
    public static String unescapeFragment(String fragment) {
        if (fragment == null || "".equals(fragment)) { //$NON-NLS-1$
            return fragment;
        }

        StringBuffer result = new StringBuffer();
        for (int i = 0; i < fragment.length();) {
            final char c = fragment.charAt(i);
            if (c == '%') {
                // count the escaped bytes first
                int byteCount = 0;
                int j = i;
                while (j < fragment.length() && fragment.charAt(j) == '%') {
                    byteCount++;
                    j += 3;
                    if (j > fragment.length()) {
                        throw new IllegalArgumentException();
                    }
                }

                byte[] bytes = new byte[byteCount];
                int b = 0;
                while (i < fragment.length() && fragment.charAt(i) == '%') {
                    bytes[b] = decode(fragment.charAt(i + 1), fragment.charAt(i + 2));
                    b++;
                    i += 3;
                }
                try {
                    result.append(new String(bytes, UTF8));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                result.append(c);
                i++;
            }
        }
        return result.toString();
    }

    private static byte decode(char x, char y) {
        return (byte) (HEX_CHARACTERS.indexOf(x) * 16 + HEX_CHARACTERS.indexOf(y));
    }

    /**
     * Test if a character is an allowed (i.e. reserved or unreserved) character for an non international URI.
     * 
     * <p>
     * See <a href="http://www.rfc-editor.org/rfc/rfc3986.txt">RFC 3986</a>.
     * 
     * @deprecated Use command {@link com.ontoprise.ontostudio.owl.gui.commands.IsAllowedNonInternationalURICharacter} instead.
     * @param c The character to test.
     * @return <code>true</code> if <code>c</code> is allowed in an non international URI.
     */
    public static boolean isAllowedNonInternationalURICharacter(char c) {
        // reserved = gen-delims / sub-delims
        //
        // gen-delims = ":" / "/" / "?" / "#" / "[" / "]" / "@"
        //
        // sub-delims = "!" / "$" / "&" / "'" / "(" / ")"
        // / "*" / "+" / "," / ";" / "="
        //
        // unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"

        // reserved
        if (":/?#[]@".indexOf(c) != -1) { //$NON-NLS-1$
            // gen-delims
            return true;
        }
        if ("!$&'()*+,;=".indexOf(c) != -1) { //$NON-NLS-1$
            // sub-delims
            return true;
        }
        // unreserved
        if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')) {
            // ALPHA
            return true;
        }
        if ('0' <= c && c <= '9') {
            // DIGIT
            return true;
        }
        if ("-._~".indexOf(c) != -1) { //$NON-NLS-1$
            return true;
        }
        return false;
    }

    /**
     * Expands and validates <code>input</code> to a valid URI. If it is invalid, the user is asked to type in a valid URI.
     * 
     * @param ontologyId
     * @param input
     * @param projectId
     * @return A valid URI which might be different from what input specifies, or null, if the user canceled to insert a valid URI.
     * @throws NeOnCoreException
     * @throws ControlException
     */
    public static String getValidURI(String input, String ontologyId, String projectId) throws NeOnCoreException,CommandException {
        String result = new CreateValidUri(projectId, ontologyId, input).getResult();
        return result;
    }

    public static CCombo createComboWidget(String[] contents, Composite comp, GridData data, int style, boolean enabled) {
        CCombo combo = new CCombo(comp, style);
        for (int i = 0; i < contents.length; i++) {
            combo.add(contents[i]);
        }
        combo.select(0);
        combo.setVisibleItemCount(10);
        combo.setEnabled(enabled);
        combo.setLayoutData(data);
        return combo;
    }

    /**
     * 
     * @param comp
     * @param layoutData
     * @param style
     * @param enabled
     * @return
     */
    public static Button createCheckBox(Composite parent, boolean enabled) {
        GridData data = new GridData();
		data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.CENTER;

        Button button = new Button(parent, SWT.CHECK | SWT.CENTER);
        button.setLayoutData(data);
        button.setEnabled(enabled);
        return button;
    }

    /**
     * Enables/Disables the text widget depending on parameter <code>enabled</code>
     * 
     * @param text
     * @param enabled
     */
    public static void enable(final Composite text, boolean enabled) {
        if (text.isDisposed()) {
            return;
        }
        if (text instanceof StyledText) {
            ((StyledText) text).setEditable(enabled);
            if (enabled) {
                text.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
            } else {
                text.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
            }
            Object o = text.getData(OWLGUIUtilities.TEXT_WIDGET_DATA_ID);
            if (o instanceof AbstractOwlTextField) {
                AbstractOwlTextField textField = (AbstractOwlTextField) o;
                if (textField != null) {
                    textField.setProposalAdapterEnabled(enabled);
                }
            }
        } else {
            text.setEnabled(enabled);
        }
    }

    /**
     * This method is passed an array <code>input</code> with size=3, where<br/>
     * input[0] = complete URI<br/>
     * input[1] = local name<br/>
     * input[2] = QName<br/>
     * 
     * Depending on the flag set for EntityLabels, one of them is returned.
     * 
     * @param input
     * @return
     */
    public static String getEntityLabel(String[] input) {
        if (input.length <= 2) {
            return input[0];
        }
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        String result = input[0];
        if (idDisplayStyle == NeOnUIPlugin.DISPLAY_URI) {
            result = input[0];
        } else if (idDisplayStyle == NeOnUIPlugin.DISPLAY_LOCAL) {
            result = input[1];
        } else if (idDisplayStyle == NeOnUIPlugin.DISPLAY_QNAME) {
            result = input[2];
        } else if (idDisplayStyle == OWLPlugin.DISPLAY_LANGUAGE) {
            result = input[3];
        }

        return result;
    }

    /**
     * returns the appropriate serialization of a URI, according to the NS-Button, e.g. QName, or Uri, or label
     * 
     * @param uri, e.g. http://w3.org/foo
     * @param owlModel
     * @return correct Syntax for the URI according to the SyntaxManager
     * 
     */
    public static String getEntityLabel(OWLEntity entity, String ontology, String project) throws NeOnCoreException {
        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontology, project);
        return manager.getTextualRepresentation(entity, owlModel);
    }

    /**
     * returns the appropriate serialization of a URI, according to the NS-Button, e.g. QName, or Uri, or label
     * 
     * @param uri, e.g. http://w3.org/foo
     * @param owlModel
     * @return correct Syntax for the URI according to the SyntaxManager
     * 
     */
    public static String getEntityLabel(OWLClassExpression description, String ontology, String project) throws NeOnCoreException {
        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontology, project);
        return manager.getTextualRepresentation(description, owlModel);
    }

    /**
     * returns the appropriate serialization of a URI, according to the NS-Button, e.g. QName, or Uri, or label
     * 
     * @param uri, e.g. http://w3.org/foo
     * @param owlModel
     * @return correct Syntax for the URI according to the SyntaxManager
     * 
     */
    public static String getEntityLabel(OWLDataRange range, String ontology, String project) throws NeOnCoreException {
        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontology, project);
        return manager.getTextualRepresentation(range, owlModel);
    }

    public static int getEntityLabelMode() {
        return NeOnUIPlugin.getDefault().getPreferenceStore().getInt(NeOnUIPlugin.ID_DISPLAY_PREFERENCE);
    }

    /**
     * @param uri, e.g. <http://w3.org/foo#bar> rdfs:comment owl:Thing myNS:bar someNameFromDefaultNamespace
     * @param ontology
     * @param project
     * @return a real URI, e.g. http://w3.org/foo#bar
     * @throws NeOnCoreException
     */
    public static String parseUri(String uri, String ontology, String project) throws NeOnCoreException {
        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontology, project);
        return manager.parseUri(uri, owlModel);
    }

    public static void jumpToEntity(String uri, OWLModel owlModel) {
        String ontologyURI;
        String project;
        try {
            ontologyURI = owlModel.getOntologyURI();
            project = owlModel.getProjectId();
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }

        PerspectiveChangeHandler.switchPerspective(OWLPerspective.ID);
        MTreeView navigator = (MTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);
        ITreeDataProvider provider = null;
        ITreeElement treeElement = null;

        // clazzes
        provider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class);
        OWLDataFactory factory;
        try {
            factory = OWLModelFactory.getOWLDataFactory(project);
        } catch (NeOnCoreException e1) {
            throw new RuntimeException(e1);
        }
        OWLClass clazz = factory.getOWLClass(OWLUtilities.toURI(uri));
        treeElement = new ClazzTreeElement(clazz, ontologyURI, project, provider);
        boolean success = doJumpToEntity(treeElement, navigator);
        if (success) {
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MTreeView.ID);
            } catch (PartInitException e) {
                // nothing to do
            }
        } else {
            // object properties
            provider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, ObjectPropertyHierarchyProvider.class);
            OWLObjectProperty prop = factory.getOWLObjectProperty(OWLUtilities.toURI(uri));
            treeElement = new ObjectPropertyTreeElement(prop, ontologyURI, project, provider);
            success = doJumpToEntity(treeElement, navigator);
            if (success) {
                try {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MTreeView.ID);
                } catch (PartInitException e) {
                    // nothing to do
                }
            } else {
                // data properties
                provider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, DataPropertyHierarchyProvider.class);
                OWLDataProperty dataProp = factory.getOWLDataProperty(OWLUtilities.toURI(uri));
                treeElement = new DataPropertyTreeElement(dataProp, ontologyURI, project, provider);
                success = doJumpToEntity(treeElement, navigator);
                if (success) {
                    try {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MTreeView.ID);
                    } catch (PartInitException e) {
                        // nothing to do
                    }
                } else {
                    // annotation properties
                    provider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, AnnotationPropertyHierarchyProvider.class);
                    OWLAnnotationProperty annotProp = factory.getOWLAnnotationProperty(OWLUtilities.toURI(uri));
                    treeElement = new AnnotationPropertyTreeElement(annotProp, ontologyURI, project, provider);
                    success = doJumpToEntity(treeElement, navigator);
                    if (success) {
                        try {
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MTreeView.ID);
                        } catch (PartInitException e) {
                            // nothing to do
                        }
                    } else {
                        // datatypes
                        provider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, DatatypeProvider.class);
                        OWLDatatype datatype = factory.getOWLDatatype(OWLUtilities.toURI(uri));
                        treeElement = new DatatypeTreeElement(datatype, ontologyURI, project, provider);
                        success = doJumpToEntity(treeElement, navigator);
                        if (success) {
                            try {
                                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MTreeView.ID);
                            } catch (PartInitException e) {
                                // nothing to do
                            }
                        } else {
                            // individuals
                            try {
                                provider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, IndividualViewContentProvider.class);
                                Set<OWLClass> clazzes = OWLModelFactory.getOWLModel(ontologyURI, project).getClasses(uri);
                                OWLIndividual individual = new InternalParser(uri, OWLNamespaces.EMPTY_INSTANCE, factory).parseOWLIndividual();// factory.getOWLNamedIndividual(OWLUtilities.toURI(uri));
                                if (clazzes.size() > 0) {
                                    // FIXME if individual exists for multiple classes, a dialog to select one would be nice
                                    // FIXME also consider individuals of OWL.Thing (displayed if class folder is selected)
                                    OWLClass firstClazz = clazzes.iterator().next();
                                    provider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class);
                                    ClazzTreeElement clazz1 = new ClazzTreeElement(firstClazz, ontologyURI, project, provider);
                                    doJumpToEntity(clazz1, navigator);
                                    MTreeView ontoNavigator = (MTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MTreeView.ID);

                                    treeElement = IndividualItem.createNewInstance(individual, firstClazz.getURI().toString(), ontologyURI, project);
                                    IViewPart individualView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IndividualView.ID);
                                    if (individualView != null) {
                                        ((IndividualView) individualView).selectionChanged(ontoNavigator, new StructuredSelection(clazz1));
                                        ((IndividualView) individualView).getTreeViewer().setSelection(new StructuredSelection(treeElement));
                                    }
                                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IndividualView.ID);
                                }
                            } catch (NeOnCoreException e) {
                                // nothing to do
                            } catch (PartInitException e) {
                                // nothing to do
                            } catch (InternalParserException e) {
                                // nothing to do
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Tries to expand the tree in OntologyNavigator to the passed entity, returns <code>false</code> if this fails, <code>true</code> otherwise.
     * 
     * @param entity
     * @param navigator
     * @return
     */
    public static boolean doJumpToEntity(ITreeElement entity, MTreeView navigator) {
        ITreeElementPath[] paths = navigator.getExtensionHandler().computePathsToRoot(entity);
        TreeItem treeItem = (navigator.getTreeViewer()).setPathExpanded(paths[0]);
        if (treeItem == null) {
            return false;
        }
        ArrayList<TreeItem> list = new ArrayList<TreeItem>();
        list.add(treeItem);
        navigator.getTreeViewer().selection(list);
        navigator.getTreeViewer().setSelection(navigator.getTreeViewer().getSelection(), true);
        navigator.getTreeViewer().expandToLevel(paths[0], 1);

        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MTreeView.ID);
        } catch (PartInitException e) {
        }
        return true;
    }

    /**
     * 
     * @param owlModel
     * @return
     */
    private static String[] getRdfsLiteral(OWLModel owlModel) {
        try {
            String[] idArray = OWLGUIUtilities.getIdArray((OWLDatatype) owlModel.getOWLDataFactory().getOWLDatatype(OWLUtilities.toURI(OWLConstants.RDFS_LITERAL)), owlModel.getOntologyURI(), owlModel.getProjectId());
            return idArray;
        } catch (NeOnCoreException e) {
            return new String[]{"Literal", OWLConstants.RDFS_LITERAL}; //$NON-NLS-1$
        }
    }

    public static void initStringOrLiteralSwitch(final StyledText typeText, final CCombo languageCombo, final OWLModel owlModel) {

        typeText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (e.widget instanceof StyledText) {
                    String text = ((StyledText) e.widget).getText();
                    String[] literalText = getRdfsLiteral(owlModel);
                    if (!text.equals(literalText[0]) && !text.equals(literalText[1])) {
                        languageCombo.select(languageCombo.indexOf(OWLCommandUtils.EMPTY_LANGUAGE));
                    }
                }
            }

        });

        languageCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String selectedText = ((CCombo) e.widget).getText();
                if (!selectedText.equals(OWLCommandUtils.EMPTY_LANGUAGE)) {
                    typeText.setText(getRdfsLiteral(owlModel)[1]);
                }
            }

        });
    }

    @SuppressWarnings("unchecked")
    public static void removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
    }

    /**
     * Returns an array containing only one value that represents the ID depending on the selected idDisplayStyle.
     * 
     * @param entity
     * @param ontologyUri
     * @param projectId
     * @return
     * @throws NeOnCoreException
     */
    public static String[] getIdArray(OWLObject entity, String ontologyUri, String projectId) throws NeOnCoreException {
        return getIdArray(entity, ontologyUri, projectId, false);
    }

    /**
     * If <code>getAllValues</code> is <code>true</code> this method returns an array containing all ID representations:
     * 
     * [0] - URI <br>
     * [1] - local name <br>
     * [2] - QName <br>
     * [3] - label (in selected language) <br>
     * 
     * If <code>getAllValues</code> is <code>false</code>, returns an array containing only one value that represents the ID depending on the selected
     * idDisplayStyle.
     * 
     * @param owlObject
     * @param ontologyUri
     * @param projectId
     * @param getAllValues
     * @return
     * @throws NeOnCoreException
     */
    public static String[] getIdArray(OWLObject owlObject, String ontologyUri, String projectId, boolean getAllValues) throws NeOnCoreException {
        if (getAllValues) {
            OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(OWLModelFactory.getOWLModel(ontologyUri, projectId));
            return (String[]) owlObject.accept(visitor);
        } else {
            int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
            OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(OWLModelFactory.getOWLModel(ontologyUri, projectId), idDisplayStyle);
            return (String[]) owlObject.accept(visitor);
        }
    }

    public static String getSingleId(OWLEntity entity, String ontologyUri, String projectId) throws NeOnCoreException {
        OWLObjectVisitorEx visitor = OWLPlugin.getDefault().getSyntaxManager().getVisitor(OWLModelFactory.getOWLModel(ontologyUri, projectId));
        return ((String[]) entity.accept(visitor))[0];
    }

    public static List<OWLAxiom> getDependentAxioms(OWLAxiom oldAxiom, List<OWLAxiom> list, List<OWLEntity> entities, List<String> uris, String projectId) {
        if (entities.size() > 0) {
            for (OWLEntity e: entities) {
                OWLAxiom newAxiom = OWLAxiomUtils.createNewAxiom(oldAxiom, e.getURI().toString(), projectId);
                if (newAxiom != null && !newAxiom.equals(oldAxiom)) {
                    list.add(newAxiom);
                }
            }
        } else {
            for (String uri: uris) {
                list.add(OWLAxiomUtils.createNewAxiom(oldAxiom, uri, projectId));
            }
        }
        return list;
    }

	/**
	 * @param parent
	 * @return
	 */
	public static CCombo createQuantifierComboBox(Composite parent, boolean enabled) {
		String[] quantifiers = OWLGUIUtilities.QUANTOR_TYPES;
	
		// quantifier
		GridData data = new GridData();
		data.widthHint = OWLGUIUtilities.QUANTIFIER_COMBO_WIDTH; 
		data.verticalAlignment = SWT.TOP;
		final CCombo quantifierCombo = OWLGUIUtilities.createComboWidget(quantifiers, parent, data, SWT.BORDER, enabled);
		quantifierCombo.setText(Messages.ClazzPropertyPage2_5);
		return quantifierCombo;
	}

    public static String getUriForSorting(OWLClassExpression desc, OWLModel owlModel) {
        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        OWLObjectVisitorEx visitor = manager.getVisitor(owlModel, idDisplayStyle);
        String[] resultArray = null;
        String result = ""; //$NON-NLS-1$
        if (desc instanceof OWLClass) {
            OWLClass clazz = (OWLClass)desc;
            resultArray = (String[]) clazz.accept(visitor);
            result = clazz.getURI().toString();
        } else if (desc instanceof OWLDataAllValuesFrom) {
            OWLDataAllValuesFrom dataAll = (OWLDataAllValuesFrom) desc;
            resultArray = (String[]) dataAll.getProperty().accept(visitor);
            result = OWLUtilities.toString(dataAll.getProperty());
        } else if (desc instanceof OWLDataCardinalityRestriction) {
            OWLDataCardinalityRestriction dataCard = (OWLDataCardinalityRestriction)desc;
            resultArray = (String[]) dataCard.getProperty().accept(visitor);
            result = OWLUtilities.toString(dataCard.getProperty());
        } else if (desc instanceof OWLDataHasValue) {
            OWLDataHasValue dataHasValue = (OWLDataHasValue) desc;
            resultArray = (String[]) dataHasValue.getProperty().accept(visitor);
            result = OWLUtilities.toString(dataHasValue.getProperty());
        } else if (desc instanceof OWLDataSomeValuesFrom) {
            OWLDataSomeValuesFrom dataSome = (OWLDataSomeValuesFrom) desc;
            resultArray = (String[]) dataSome.getProperty().accept(visitor);
            result = OWLUtilities.toString(dataSome.getProperty());
        } else if (desc instanceof OWLObjectAllValuesFrom) {
            OWLObjectAllValuesFrom objectAll = (OWLObjectAllValuesFrom) desc;
            resultArray = (String[]) objectAll.getProperty().accept(visitor);
            result = OWLUtilities.toString(objectAll.getProperty());
        } else if (desc instanceof OWLObjectIntersectionOf) {
            OWLObjectIntersectionOf objectAnd = (OWLObjectIntersectionOf) desc;
//            resultArray = (String[]) dataHasValue.getDataProperty().accept(visitor);
            result = OWLUtilities.toString(objectAnd);
        } else if (desc instanceof OWLObjectCardinalityRestriction) {
            OWLObjectCardinalityRestriction objectCard = (OWLObjectCardinalityRestriction) desc;
            resultArray = (String[]) objectCard.getProperty().accept(visitor);
            result = OWLUtilities.toString(objectCard.getProperty());
        } else if (desc instanceof OWLObjectHasSelf) {
            OWLObjectHasSelf objExistsSelf = (OWLObjectHasSelf) desc;
            resultArray = (String[]) objExistsSelf.getProperty().accept(visitor);
            result = OWLUtilities.toString(objExistsSelf.getProperty());
        } else if (desc instanceof OWLObjectHasValue) {
            OWLObjectHasValue objHasValue = (OWLObjectHasValue) desc;
            resultArray = (String[]) objHasValue.getProperty().accept(visitor);
            result = OWLUtilities.toString(objHasValue.getProperty());
        } else if (desc instanceof OWLObjectComplementOf) {
            OWLObjectComplementOf objNot = (OWLObjectComplementOf) desc;
//            resultArray = (String[]) dataHasValue.getDataProperty().accept(visitor);
            result = OWLUtilities.toString(objNot);
        } else if (desc instanceof OWLObjectOneOf) {
            OWLObjectOneOf objOneOf = (OWLObjectOneOf) desc;
//            resultArray = (String[]) dataHasValue.getDataProperty().accept(visitor);
            result = OWLUtilities.toString(objOneOf);
        } else if (desc instanceof OWLObjectUnionOf) {
            OWLObjectUnionOf objOr = (OWLObjectUnionOf) desc;
//            resultArray = (String[]) dataHasValue.getDataProperty().accept(visitor);
            result = OWLUtilities.toString(objOr);
        } else if (desc instanceof OWLObjectSomeValuesFrom) {
            OWLObjectSomeValuesFrom objectSome = (OWLObjectSomeValuesFrom) desc;
            resultArray = (String[]) objectSome.getProperty().accept(visitor);
            result = OWLUtilities.toString(objectSome.getProperty());
        }
        if (resultArray != null) {
            return getEntityLabel(resultArray);
        }
        return result;
    }

    public static String validateNamespace(String input, IInputValidator validator) {
        String message = validator.isValid(input);
        if (message == null) {
            if (!(input.endsWith("/") || (input.endsWith(":") || (input.endsWith("#"))))) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                message = com.ontoprise.ontostudio.owl.gui.Messages.NewOWLOntologyWizardPage_1;
            } else {
                URI uri;
                try {
                    uri = new URI(input);
                    String fragment = uri.getFragment();
                    // fragment should be empty or no fragment defined
                    if (fragment != null && fragment.trim().length() > 0) {
                        message = Messages.NewOWLOntologyWizardPage_0 + " - " + Messages.NewOWLOntologyWizardPage_3; //$NON-NLS-1$
                    }
                } catch (URISyntaxException e) {
                    return e.getMessage();
                }
            }
        }
        return message;
    }
    
    private static DatatypeVerifier _datatypeVerifier = new DatatypeVerifier();
    private static ValueInputVerifier _valueInputVerifier = new ValueInputVerifier();
    
    public static String verifyUserInput(String valueInput, String datatype) {
        
        if (!(datatype.equals("") || datatype.equals("<" + OWLAxiomUtils.OWL_INDIVIDUAL + ">")))  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        
            return _valueInputVerifier.verify(valueInput, _datatypeVerifier.verify(datatype));
            
        else
            return valueInput;
    }

     
    public static boolean isOWLProject(String projectName) throws NeOnCoreException {
        IOntologyProject ontoProject = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
        return ontoProject != null && OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE.equals(ontoProject.getOntologyLanguage());
    }
   
    
    public static int getNumberOfDirectInstances(OWLEntity entity, String ontologyUri, String projectName) {
        try {
            OWLModel om = OWLModelFactory.getOWLModel(ontologyUri, projectName);
            return getNumberOfDirectInstances(entity, om);
        } catch (NeOnCoreException e) {
            e.printStackTrace();
        }   
        return 0;
    }
    
    public static int getNumberOfDirectInstances(OWLEntity entity, OWLModel om) {
        if (entity instanceof OWLClass){       
            try {
                Set<OWLIndividual> res = om.getIndividuals(entity.getIRI().toString());
                return res.size();
            } catch (NeOnCoreException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    public static int getNumberOfInDirectInstances(OWLEntity entity, String ontologyUri, String projectName) {
        if (entity instanceof OWLClass){       
            try {
                OWLModel om = OWLModelFactory.getOWLModel(ontologyUri, projectName);
                Set<OWLIndividual> res = om.getAllIndividuals(entity.getIRI().toString());
                return res.size();
            } catch (NeOnCoreException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
