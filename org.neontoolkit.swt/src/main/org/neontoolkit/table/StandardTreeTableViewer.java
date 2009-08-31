/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 05.02.2009
 * Created by: werner
 ******************************************************************************/
package org.neontoolkit.table;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.neontoolkit.swt.Messages;
import org.neontoolkit.table.cursor.TableCursorCellHighlighter;
import org.neontoolkit.table.cursor.TreeTableCursor;


/**
 * @author werner
 * 
 */
public class StandardTreeTableViewer extends StandardViewer {

    private TreeTableCursor _cursor;
    private Tree _tree;
    private TreeViewer _treeViewer;

    public StandardTreeTableViewer(Composite parent, String[] tableHeaders, int style) {
        _tree = createTree(parent, style);
        createTreeViewer(_tree, tableHeaders);
    }

    /**
     * @param tree
     */
    private void createTreeViewer(Tree tree, String[] tableHeaders) {
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);

        _treeViewer = new TreeViewer(tree);
        _treeViewer.setUseHashlookup(true);
        initColumnProperties(tableHeaders, tree);

        TreeColumn[] columns = tree.getColumns();
        TreeViewerColumn[] viewerColumns = new TreeViewerColumn[columns.length];
        for (int i=0; i<columns.length; i++) {
            viewerColumns[i] = new TreeViewerColumn(_treeViewer, columns[i]);
        }

        _cursor = new TreeTableCursor(_treeViewer, viewerColumns);
        TreeViewerFocusCellManager focusCellManager = new TreeViewerFocusCellManager(_treeViewer, new TableCursorCellHighlighter(_treeViewer, _cursor));
        ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(_treeViewer) {
            @Override
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                if (event.sourceEvent instanceof MouseEvent) {
                    int stateMask = ((MouseEvent) event.sourceEvent).stateMask;
                    if (stateMask == SWT.CTRL) {
                        return false;
                    }
                }
                return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
                        || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && isValidEditingKey(event))
                        || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }
        };

        TreeViewerEditor.create(_treeViewer, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
    }

    private void initColumnProperties(String[] tableHeaders, Tree tree) {
        String[] tableHeadersWithRemoveColumn = new String[tableHeaders.length + 1];
        System.arraycopy(tableHeaders, 0, tableHeadersWithRemoveColumn, 0, 2);
        tableHeadersWithRemoveColumn[tableHeaders.length] = ""; //$NON-NLS-1$
        _treeViewer.setColumnProperties(tableHeadersWithRemoveColumn);
        for (String header: tableHeadersWithRemoveColumn) {
            TreeColumn propertyColumn = new TreeColumn(tree, SWT.LEFT);
            propertyColumn.setText(header);
            if (header.equals("")) { //$NON-NLS-1$
                propertyColumn.setToolTipText(Messages.StandardTreeTableViewer_1);
            } 
        }

    }

    private Tree createTree(Composite parent, int style) {
        return new Tree(parent, style);
    }

    /**
     * @param propertyContentProvider
     */
    public void setContentProvider(ITreeContentProvider provider) {
        _treeViewer.setContentProvider(provider);

    }

    /**
     * @param propertyLabelProvider
     */
    public void setLabelProvider(ITableLabelProvider labelProvider) {
        _treeViewer.setLabelProvider(labelProvider);
    }

    /**
     * @param propertyCellModifier
     */
    public void setCellModifier(ICellModifier cellModifier) {
        _treeViewer.setCellModifier(cellModifier);
    }

    /**
     * @param editors
     */
    public void setCellEditors(CellEditor[] editors) {
        _treeViewer.setCellEditors(editors);
    }

    /**
     * @param viewerComparator
     */
    public void setComparator(ViewerComparator viewerComparator) {
        _treeViewer.setComparator(viewerComparator);
    }

    public TreeViewer getTreeViewer() {
        return _treeViewer;
    }

    public Tree getTree() {
        return _tree;
    }

    /**
     * @param string
     */
    public void setInput(Object input) {
        _treeViewer.setInput(input);
    }

    public void setColumnWidth(int index, int width) {
        _tree.getColumn(index).setWidth(width);
    }

}
