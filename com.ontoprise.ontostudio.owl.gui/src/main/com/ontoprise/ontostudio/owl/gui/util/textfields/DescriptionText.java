/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.textfields;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.syntax.manchester.ManchesterSyntaxConstants;
import com.ontoprise.ontostudio.owl.gui.syntax.manchester.ManchesterSyntaxProposalProvider;
import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * @author mer
 * 
 *         This class represents a StyledText field for OWL descriptions AKA complex class descriptions.
 * 
 *         It uses syntax highlighting. It offers auto-completion It wraps the content into multiple lines if needed. It can be edited. New-Lines are permitted.
 *         It supports CTRL-click to jump to the datatype entity.
 * 
 *         For display, it uses the namespace-setting as chosen by the user, also for editing.
 */
public class DescriptionText extends AbstractOwlTextField {

    public static final int WIDTH = 200;

    private Shell _toolbarShell;
    private Point _originalToolBarPosition;
    private FormToolkit _toolkit;

    /*
     * This value indicates the height of the complex class editor. It will cover the amount of rows defined here. (this value is changed when the
     * enlarge-button is pressed while in edit mode.
     */
    private int _toolbarRowSpan = 5;

    public DescriptionText(Composite parent, OWLModel owlModel, FormToolkit toolkit) {
        this(parent, owlModel, false, toolkit);
    }

    /**
	 * 
	 */
    public DescriptionText(Composite parent, OWLModel owlModel, boolean imported, FormToolkit toolkit) {
        super(parent, owlModel);

        _toolkit = toolkit;
        GridData data = new GridData();
        data.widthHint = WIDTH;
        data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = true;

        createTextWidget(parent, data, getComplexClassProposalProvider(owlModel), true, true, imported);
    }

    private IContentProposalProvider getComplexClassProposalProvider(OWLModel owlModel) {
        IContentProposalProvider provider = OWLPlugin.getDefault().getSyntaxManager().getProposalProvider();
        if (provider instanceof ManchesterSyntaxProposalProvider) {
            ((ManchesterSyntaxProposalProvider) provider).setOwlModel(owlModel);
        }
        return provider;
    }

    /**
     * Opens a toolbar with buttons for adding keywords and additional operations like adding <code>classes</code>, <code>individuals</code>, etc.
     * 
     * For a list of available keywords see <code>com.ontoprise.ontostudio.owl.model.NeonSyntaxConstants</code>
     * 
     * @param parent
     * @see old.com.ontoprise.ontostudio.owl.gui.syntax.neon.NeonSyntaxConstants
     */
    public void openToolBar(final StyledText parent) {
        // first make sure no other shell is open
        closeToolBar();

        // get location of textfield and place this shell beneath it
        _toolbarShell = new Shell(parent.getShell(), SWT.NO_TRIM | SWT.BORDER);

        setToolbarBounds(parent);
        _toolbarShell.setLayout(new GridLayout());
        Color color = new Color(null, new RGB(215, 250, 250));
        _toolbarShell.setBackground(color);

        Form form = _toolkit.createForm(_toolbarShell);
        Composite body = form.getBody();
        body.setBackground(color);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.verticalAlignment = GridData.BEGINNING;
        data.grabExcessVerticalSpace = true;
        body.setLayoutData(data);
        body.setLayout(new GridLayout());
        Composite buttonComp = _toolkit.createComposite(body, SWT.NONE);
        _toolkit.adapt(buttonComp);
        buttonComp.setBackground(color);
        buttonComp.setLayout(new GridLayout());

        // FIXME link keywords with ISyntaxManager

        // prepare image links for keywords
        String unionOf = createKeywordLink(ManchesterSyntaxConstants.OR);
        String complementOf = createKeywordLink(ManchesterSyntaxConstants.NOT);
        String intersectionOf = createKeywordLink(ManchesterSyntaxConstants.AND);
        // String oneOf = createKeywordLink(ManchesterSyntaxConstants.ONE_OF);
        String minCardinality = createKeywordLink(ManchesterSyntaxConstants.MIN);
        String maxCardinality = createKeywordLink(ManchesterSyntaxConstants.MAX);
        String cardinality = createKeywordLink(ManchesterSyntaxConstants.CARDINALITY);
        String someValuesFrom = createKeywordLink(ManchesterSyntaxConstants.SOME_VALUES_FROM);
        String allValuesFrom = createKeywordLink(ManchesterSyntaxConstants.ALL_VALUES_FROM);
        String hasValue = createKeywordLink(ManchesterSyntaxConstants.HAS_VALUE);

        FormText formText = _toolkit.createFormText(buttonComp, true);
        formText.setBackground(color);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = 200;
        data.verticalAlignment = GridData.BEGINNING;
        formText.setLayoutData(data);
        formText.setWhitespaceNormalized(true);
        color.dispose();

        formText.setImage(ManchesterSyntaxConstants.NOT, OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.COMPLEMENT_OF));
        formText.setImage(ManchesterSyntaxConstants.OR, OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.UNION_OF));
        formText.setImage(ManchesterSyntaxConstants.AND, OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INTERSECTION_OF));
        formText.setImage(ManchesterSyntaxConstants.MIN, OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.MIN));
        formText.setImage(ManchesterSyntaxConstants.MAX, OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.MAX));
        formText.setImage(ManchesterSyntaxConstants.CARDINALITY, OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CARDINALITY));
        formText.setImage(ManchesterSyntaxConstants.SOME_VALUES_FROM, OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.SOME_VALUES_FROM));
        formText.setImage(ManchesterSyntaxConstants.ALL_VALUES_FROM, OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ALL_VALUES_FROM));
        formText.setImage(ManchesterSyntaxConstants.HAS_VALUE, OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.HAS_VALUE));

        formText.setColor("header", _toolkit.getColors().getColor(IFormColors.TITLE)); //$NON-NLS-1$
        formText.setFont("header", JFaceResources.getHeaderFont()); //$NON-NLS-1$
        formText.setFont("code", JFaceResources.getTextFont()); //$NON-NLS-1$

        StringBuffer finalTextBuffer = new StringBuffer();
        String finalText = unionOf + complementOf + intersectionOf + "   " + minCardinality + maxCardinality + cardinality + someValuesFrom + allValuesFrom + hasValue; //$NON-NLS-1$
        finalTextBuffer.append("<form>"); //$NON-NLS-1$
        finalTextBuffer.append("<p>"); //$NON-NLS-1$
        finalTextBuffer.append(finalText);
        finalTextBuffer.append("</p>"); //$NON-NLS-1$
        finalTextBuffer.append("</form>"); //$NON-NLS-1$

        formText.setText(finalTextBuffer.toString(), true, true);
        formText.addHyperlinkListener(new HyperlinkAdapter() {

            @Override
            public void linkActivated(HyperlinkEvent e) {
                _toolbarEditMode = true;
                String text = " " + e.getHref().toString(); //$NON-NLS-1$
                int position = 0;
                if (ManchesterSyntaxConstants.isClassConstructorKeyword(e.getHref().toString())) {
                    text += " "; //$NON-NLS-1$
                    position = parent.getSelection().y + text.length();
                } else {
                    text += " "; //$NON-NLS-1$
                    position = parent.getSelection().y + text.length();
                }
                parent.insert(text);
                parent.setSelection(position);
                parent.setFocus();
                _toolbarEditMode = false;
            }

        });
        formText.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseHover(MouseEvent e) {
            }

        });

        _toolbarShell.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {
                if (_originalToolBarPosition == null)
                    return;
                Point point = Display.getDefault().map(_toolbarShell, null, e.x, e.y);
                _toolbarShell.setLocation(point.x - _originalToolBarPosition.x, point.y - _originalToolBarPosition.y);
            }
        });

        _toolbarShell.getParent().addListener(SWT.Move, new Listener() {

            public void handleEvent(Event event) {
                setToolbarBounds(parent);
            }

        });
        _toolbarShell.setVisible(true);
    }

    public void closeToolBar() {
        if (_toolbarShell != null && !_toolbarShell.isDisposed()) {
            _toolbarShell.dispose();
        }
    }

    protected void setToolbarBounds(final StyledText parent) {
        if (!parent.isDisposed()) {
            Rectangle bounds = parent.getBounds();
            int x = bounds.x + 2;
            int y = bounds.y + parent.getLineHeight() * _toolbarRowSpan + 7;
            Point targetPoint = Display.getDefault().map(parent.getParent(), null, new Point(x, y));
            if (_toolbarShell != null && !_toolbarShell.isDisposed()) {
                _toolbarShell.setBounds(targetPoint.x, targetPoint.y, 200, 50);
            }
        }
    }

    /**
     * Returns a HTML string displaying an image link for the passed keyword. Pressing this link will add the keyword to the Complex Class textfield.
     * 
     * Available keywords: <br>
     * <ul>
     * <li><code>unionOf</code>
     * <li><code>intersectionOf</code>
     * <li><code>complementOf</code>
     * <li><code>oneOf</code>
     * <li><code>minCardinality</code>
     * <li><code>maxCardinality</code>
     * <li><code>cardinality</code>
     * <li><code>someValuesFrom</code>
     * <li><code>allValuesFrom</code>
     * <li><code>hasValue</code>
     * </ul>
     * 
     * @return
     */
    protected String createKeywordLink(String keyword) {
        StringBuffer s = new StringBuffer();
        s.append("<a href=\"" + keyword + "\" title=\"" + keyword + "\"><img href=\"" + keyword + "\" alt=\"" + keyword + "\"/></a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return getStyledText().equals(((DescriptionText) obj).getStyledText());
    }

    @Override
    public int hashCode() {
        return getStyledText().hashCode();
    }
}
