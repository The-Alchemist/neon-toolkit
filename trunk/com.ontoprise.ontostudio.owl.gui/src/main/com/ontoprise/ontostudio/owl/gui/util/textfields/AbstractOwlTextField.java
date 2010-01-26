/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.textfields;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.DefaultOWLProposalLabelProvider;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.gui.util.StyledTextContentAdapter;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

/**
 * @author mer
 * 
 */
public abstract class AbstractOwlTextField {

    protected final OWLModel _owlModel;
    protected final Composite _parent;
    protected final OWLNamespaces _namespaces;

    protected boolean _toolbarEditMode = false;

    private FocusAdapter _focusAdapter;

    private TextViewer _textViewer;
    private StyledText _styledText;
    private ISyntaxManager _syntaxManager;
    private ContentProposalAdapter _adapter;

    private boolean _highlighting;

    /**
	 * 
	 */
    public AbstractOwlTextField(Composite parent, OWLModel owlModel) {
        _parent = parent;
        _owlModel = owlModel;

        OWLNamespaces ns = OWLNamespaces.INSTANCE;
        if (owlModel != null) {
            try {
                ns = owlModel.getNamespaces();
            } catch (NeOnCoreException e) {
                // log
            }
        }
        _namespaces = ns;
    }

    /**
     * creates the StyledText without autocompletion feature.
     * 
     * @param comp
     * @param layoutData
     * @param style
     * @param multiLine
     * @param highlighting
     */
    protected void createTextWidget(Composite comp, GridData layoutData, boolean multiLine, boolean highlighting) {
        createTextWidget(comp, layoutData, null, multiLine, highlighting);
    }

    public void setProposalAdapterEnabled(boolean enabled) {
        if (_adapter != null) {
            _adapter.setEnabled(enabled);
        }
    }

    protected void createTextWidget(Composite parent, GridData layoutData, IContentProposalProvider proposalProvider, boolean multiLine, boolean highlighting, boolean imported) {
        _highlighting = highlighting;

        int style = SWT.LAST_LINE_SELECTION | SWT.FULL_SELECTION | SWT.BORDER;

        if (multiLine) {
            style |= SWT.WRAP;
        }

        Document document = new Document(); // the model

        _textViewer = new TextViewer(parent, style);
        _textViewer.setDocument(document);

        _styledText = _textViewer.getTextWidget();
        _styledText.setLayoutData(layoutData);
        _styledText.setWordWrap(multiLine);
        _styledText.setEditable(true);
        _styledText.setData(this);

        addAllListeners(multiLine, imported);

        if (proposalProvider != null) {
            getCompletionEditor(proposalProvider, ContentProposalAdapter.PROPOSAL_REPLACE);
        }

        if (highlighting) {
            initSyntaxHighlighting(_textViewer);
        }

        configureUndoRedo(_textViewer);
    }

    /**
     * 
     * @param parent
     * @param layoutData
     * @param style
     * @param proposalProvider
     * @param multiLine
     * @param highlighting
     */
    protected void createTextWidget(Composite parent, GridData layoutData, IContentProposalProvider proposalProvider, boolean multiLine, boolean highlighting) {
        createTextWidget(parent, layoutData, proposalProvider, multiLine, highlighting, false);
    }

    /**
     * Adds: <br>
     * - a TraverseListener, that enables traversing the _textViewer widgets via Tab<br>
     * - a FocusListener, that opens the complex class editor on focusGained()<br>
     * 
     * @param _textViewer: the StyledText widget to be processed
     */
    private void addAllListeners(final boolean multiLine, boolean imported) {
        addTraverseListener(multiLine);

        if (multiLine && !imported) {
            addFocusListener();
        }

        addMouseListenerForJumpToEntity();
    }

    private void addFocusListener() {
        _focusAdapter = new StyledTextFocusAdapter();
        _styledText.addFocusListener(_focusAdapter);
    }

    public FocusAdapter getFocusAdapter() {
        return _focusAdapter;
    }

    private void addTraverseListener(final boolean multiLine) {
        _styledText.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
                    e.doit = true;
                } else if (e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                    e.doit = true;
                } else if (!multiLine && e.character == SWT.CR) {
                    if ((e.stateMask & SWT.SHIFT) > 0) {
                        e.detail = SWT.TRAVERSE_TAB_PREVIOUS;
                    } else {
                        e.detail = SWT.TRAVERSE_TAB_NEXT;
                    }
                    e.doit = true;
                }
            }
        });
    }

    /**
     * Adds MouseListeners, that allow jumping to an entity via Ctrl & MouseClick.
     * 
     * @param _styledText
     */
    protected void addMouseListenerForJumpToEntity() {
        // listen for mouse click
        _styledText.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                if ((e.stateMask & SWT.CTRL) == 0) {
                    return;
                }

                int offset = 0;
                try {
                    offset = _styledText.getOffsetAtLocation(new Point(e.x, e.y));
                } catch (IllegalArgumentException iae) {
                    return;
                }

                String selectedText = getSelectedTextForOffset(offset);

                if (selectedText != null) {
                    try {
                        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
                        selectedText = manager.parseUri(selectedText, _owlModel);
                        OWLGUIUtilities.jumpToEntity(selectedText, _owlModel);
                    } catch (NeOnCoreException e1) {
                        new NeonToolkitExceptionHandler().handleException(e1);
                        return;
                    }
                }
            }
        });

        // listen for mouse movement in order to highlight entities
        _styledText.addMouseMoveListener(new MouseMoveListener() {
            StyleRange oldRange = null;

            public void mouseMove(MouseEvent e) {
                if ((e.stateMask & SWT.CTRL) == 0) {
                    _styledText.setCursor(null);
                    if (_highlighting) {
                        initSyntaxHighlighting(_textViewer);
                    }
                    return;
                }

                int offset = 0;
                try {
                    offset = _styledText.getOffsetAtLocation(new Point(e.x, e.y));
                } catch (IllegalArgumentException iae) {
                    return;
                }

                StyleRange styleRange = getStyleRangeForOffset(offset);
                if (styleRange == null) {
                    return;
                }

                if (oldRange != null && oldRange.start != styleRange.start) {
                    if (_highlighting) {
                        initSyntaxHighlighting(_textViewer);
                    }
                    oldRange = (StyleRange) styleRange.clone();
                } else if (oldRange == null) {
                    oldRange = (StyleRange) styleRange.clone();
                }
                styleRange = (StyleRange) styleRange.clone();

                _styledText.setCursor(new Cursor(_styledText.getDisplay(), SWT.CURSOR_HAND));
                Color blue = new Color(_styledText.getDisplay(), 0, 0, 255);
                styleRange.fontStyle = SWT.BOLD;
                styleRange.foreground = blue;
                styleRange.underline = true;
                _styledText.setStyleRange(styleRange);
            }

        });
    }

    private String getSelectedTextForOffset(int offset) {
        StyleRange[] ranges = _styledText.getStyleRanges();

        StyleRange oldStyleRange = null;
        StyleRange currentStyleRange = null;
        for (int i = 0; i < ranges.length; i++) {
            oldStyleRange = currentStyleRange;
            currentStyleRange = ranges[i];
            int start = currentStyleRange.start;
            int end = currentStyleRange.start + currentStyleRange.length;
            if (offset >= start && offset < end) {
                String textStr = _styledText.getText();
                String selectedText = textStr.substring(start, end);
                if (oldStyleRange != null) {
                    String oldText = textStr.substring(oldStyleRange.start, oldStyleRange.start + oldStyleRange.length);
                    if (oldText.endsWith(":")) { //$NON-NLS-1$
                        selectedText = oldText + selectedText;
                    }
                }
                selectedText = selectedText.trim();
                if(selectedText.endsWith(",")) {
                    selectedText = selectedText.substring(0, selectedText.length()-1);
                }
                return selectedText;
            }
        }
        return null;
    }

    private StyleRange getStyleRangeForOffset(int offset) {
        StyleRange styleRange;
        StyleRange[] ranges = _styledText.getStyleRanges();

        for (int i = 0; i < ranges.length; i++) {
            styleRange = ranges[i];
            int start = styleRange.start;
            int end = styleRange.start + styleRange.length;
            if (offset >= start && offset < end) {
                return styleRange;
            }
        }
        return null;
    }

    private void initSyntaxHighlighting(ITextViewer textViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getSyntaxManager().getComplexClassScanner(_namespaces));
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.install(textViewer);
        dr.setDocument(textViewer.getDocument());
    }

    /**
     * Undo/Redo via Ctrl&Z/Ctrl&Y
     * 
     * @param _textViewer
     */
    private void configureUndoRedo(final TextViewer text) {
        TextViewerUndoManager undoManager = new TextViewerUndoManager(100);
        text.setUndoManager(undoManager);
        undoManager.connect(text);
        text.getTextWidget().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.stateMask == SWT.CTRL) {
                    if (e.keyCode == 122) {
                        text.getUndoManager().undo();
                    } else if (e.keyCode == 121) {
                        text.getUndoManager().redo();
                    }
                }
            }
        });
    }

    private void getCompletionEditor(IContentProposalProvider proposalProvider, int acceptanceStyle) {
        KeyStroke keyStroke = null;
        try {
            keyStroke = KeyStroke.getInstance("Ctrl+Space");  //$NON-NLS-1$
        } catch (ParseException e) {
            // nothing to do
        }

        _adapter = new ContentProposalAdapter(_styledText, new StyledTextContentAdapter(), proposalProvider, keyStroke, null);
        _adapter.setAutoActivationDelay(1000);
        _adapter.setAutoActivationCharacters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMONPQRSTUVWXYZ".toCharArray()); //$NON-NLS-1$
        _adapter.setLabelProvider(new DefaultOWLProposalLabelProvider());
        _adapter.setProposalAcceptanceStyle(acceptanceStyle);
    }

    private ISyntaxManager getSyntaxManager() {
        if (_syntaxManager == null) {
            _syntaxManager = OWLPlugin.getDefault().getSyntaxManager();
        }
        return _syntaxManager;
    }

    public StyledText getStyledText() {
        return _styledText;
    }

    private void layoutParents() {
        Composite parent = _parent;
        while (parent != null) {
            parent.layout(true);
            parent = parent.getParent();
            if (parent instanceof ScrolledForm) {
                ((ScrolledForm) parent).reflow(true);
                break;
            }
        }
    }

    class StyledTextFocusAdapter extends FocusAdapter {

        Color oldColor = null;
        int oldHeight = -1;

        @Override
        public void focusGained(FocusEvent e) {
            if (_styledText.getEditable()) {
                oldColor = _parent.getBackground();
                _parent.setBackground(new Color(null, 250, 250, 210));
                _styledText.setFocus();
                int height = _styledText.getLineHeight() * 5 + 5;
                GridData heightData = (GridData) _styledText.getLayoutData();
                if (_styledText.getSize().y < height) {
                    oldHeight = _styledText.getSize().y;
                    heightData.heightHint = height;
                    _styledText.setLayoutData(heightData);
                    layoutParents();
                }
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            // if(oldColor != null) {
            // _parent.setBackground(oldColor);
            // }
            //			
            // if(oldHeight > -1) {
            // GridData heightData = (GridData) _styledText.getLayoutData();
            // heightData.heightHint = 18;
            // _styledText.setLayoutData(heightData);
            // layoutParents();
            // }
        }

    }
}
