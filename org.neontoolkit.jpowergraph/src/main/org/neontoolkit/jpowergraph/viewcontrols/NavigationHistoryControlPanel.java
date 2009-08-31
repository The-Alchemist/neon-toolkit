/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.jpowergraph.viewcontrols;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.neontoolkit.jpowergraph.Messages;
import org.neontoolkit.jpowergraph.history.NavigationHistory;
import org.neontoolkit.jpowergraph.pane.OntoVisualizerViewPane;


/*
 * Created by Werner Hihn
 */

public class NavigationHistoryControlPanel extends Composite {

    private Composite _parent;
    private Image _back;
    private Image _forth;
    private Text _searchText;
    private Button _searchButton;
    private Button _enableHistory;
    private Button _backButton;
    private Button _forthButton;

    public NavigationHistoryControlPanel(Composite parent, final NavigationHistory theHistory) {
        super(parent, SWT.NONE);
        _parent = parent;
        GridData data = new GridData();
        data.heightHint = 30;
        parent.setLayoutData(data);
        _back = ImageFactory.get(ImageFactory.BACK_NAV);
        _forth = ImageFactory.get(ImageFactory.FORWARD_NAV);
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 0;
        container.setLayout(gridLayout);

        _backButton = new Button(container, SWT.PUSH);
        _backButton.setImage(_back);
        _backButton.setToolTipText(Messages.NavigationHistoryControlPanel_1);
        _backButton.addSelectionListener(new HistorySelectionAdapter());

        _forthButton = new Button(container, SWT.PUSH);
        _forthButton.addSelectionListener(new HistorySelectionAdapter());
        _forthButton.setImage(_forth);
        _forthButton.setToolTipText(Messages.NavigationHistoryControlPanel_0);

        _searchText = new Text(container, SWT.BORDER);
        GridData textData = new GridData();
        textData.widthHint = 150;
        _searchText.setLayoutData(textData);
        _searchText.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
            }

            @Override
            public void focusGained(FocusEvent e) {
                _searchText.selectAll();
            }

        });
        _searchText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {

                        public void run() {
                            search(_searchText.getText());
                        }

                    });

                }
            }

        });
        _searchText.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                _searchText.selectAll();
            }

        });

        _searchButton = new Button(container, SWT.PUSH);
        _searchButton.setText(Messages.NavigationHistoryControlPanel_3);
        _searchButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                search(_searchText.getText());
            }

        });
        _enableHistory = new Button(container, SWT.CHECK);
        _enableHistory.setText(Messages.NavigationHistoryControlPanel_4);
        _enableHistory.setSelection(theHistory.isDrawHistory());
        _enableHistory.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (theHistory.isDrawHistory() != _enableHistory.getSelection()) {
                    ((Button) e.getSource()).getParent().getParent().setCursor(new Cursor(getShell().getDisplay(), SWT.CURSOR_WAIT));
                    theHistory.setDrawHistory(_enableHistory.getSelection());
                    // FIXME: this is only a workaround. We don't really want to re-instantiate the whole visualizer here, but a
                    // simple repaint doesn't work.
                    refresh();
                    ((Button) e.getSource()).getParent().getParent().setCursor(new Cursor(getShell().getDisplay(), SWT.CURSOR_ARROW));
                }
            }

        });
    }

    private void search(String concept) {
        ((OntoVisualizerViewPane) _parent).goTo(_searchText.getText());
    }

    private void refresh() {
        ((OntoVisualizerViewPane) _parent).refresh();
    }

    public void activateBackButton(boolean activate) {
        _backButton.setEnabled(activate);
    }

    public void activateForthButton(boolean activate) {
        _forthButton.setEnabled(activate);
    }

    class HistorySelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            String text = ((Button) e.getSource()).getToolTipText();
            if (text.equals(Messages.NavigationHistoryControlPanel_5)) {
                ((OntoVisualizerViewPane) _parent).fireNavigationBack();
            } else if (text.equals(Messages.NavigationHistoryControlPanel_6)) {
                ((OntoVisualizerViewPane) _parent).fireNavigationForth();
            }
        }
    }

    @Override
    public boolean setFocus() {
        return _searchText.setFocus();
    }

}
