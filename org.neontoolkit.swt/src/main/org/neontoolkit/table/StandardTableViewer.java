/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table;

import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.neontoolkit.swt.Messages;
import org.neontoolkit.table.cursor.AbstractColumnCursor;
import org.neontoolkit.table.cursor.TableCursor;
import org.neontoolkit.table.cursor.TableCursorCellHighlighter;
import org.neontoolkit.table.model.ITableContentListener;
import org.neontoolkit.table.model.ITableContentModel;
import org.neontoolkit.table.model.TableChangeEvent;
import org.neontoolkit.table.model.TableContentAdapter;


public class StandardTableViewer {
    
    public static final String REMOVE_COLUMN = "REMOVE_COLUMN"; //$NON-NLS-1$
	public static int MULTI_ROWED     = 1 << 0;
	public static int CURSOR          = 1 << 1;
	public static int REMOVEABLE_ROWS = 1 << 2;
	
	private TableViewer _viewer;
	private int _style;
	
	private ITableContentModel _contentModel;
	private ITableContentListener _contentListener;
	
	private AbstractColumnCursor _cursor;
	
	private boolean _ascending = false;

	public StandardTableViewer(Composite parent, String[] tableHeaders, int style) {
		_style = style;
		Table table = createTable(parent, tableHeaders);
		createTableViewer(table);
	}
	
	private void createTableViewer(final Table table) {
		_viewer = new TableViewer(table);
		_viewer.setComparator(new TableViewerComparator(0, true));
		TableColumn[] columns = table.getColumns();
		TableViewerColumn[] viewerColumns = new TableViewerColumn[columns.length];
		for (int i=0; i<columns.length; i++) {
		    viewerColumns[i] = new TableViewerColumn(_viewer, columns[i]);
		}
		createTooltipForDelete(table);
		initColumnProperties();

		_cursor = new TableCursor(_viewer, viewerColumns);
		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(_viewer,new TableCursorCellHighlighter(_viewer,_cursor));
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(_viewer) {
			@Override
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				if (event.sourceEvent instanceof MouseEvent) {
					int stateMask = ((MouseEvent)event.sourceEvent).stateMask;
					if (stateMask == SWT.CTRL) {
						return false;
					}
				}
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
					|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
					|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && isValidEditingKey(event))
					|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		
		TableViewerEditor.create(_viewer, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
	}
	
	private boolean isValidEditingKey(ColumnViewerEditorActivationEvent event) {
		int keyCode = event.keyCode;
		if ((event.keyCode & SWT.MODIFIER_MASK) == event.keyCode) {
			//only modifier pressed, nothing to do
			return false;
		}
		else if (keyCode == SWT.ARROW_DOWN ||
				keyCode == SWT.ARROW_UP ||
				keyCode == SWT.ARROW_LEFT ||
				keyCode == SWT.ARROW_RIGHT) {
			return false;
		}
		else if (keyCode == SWT.ESC ||
                keyCode == SWT.BS ||
                keyCode == SWT.CAPS_LOCK ||
		        keyCode == SWT.TAB) {
			return false;
		}
		return true;
	}
	
	private Table createTable(Composite parent, String[] tableHeaders) {
		Table table = new Table(parent, SWT.BORDER | SWT.HIDE_SELECTION | SWT.FULL_SELECTION | SWT.V_SCROLL);
//		Table table = new Table(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		createTableColumns(table, tableHeaders);
		return table;
	}
	
	private TableViewerComparator getComparator() {
	    if (_viewer.getComparator() instanceof TableViewerComparator) {
	        return (TableViewerComparator)_viewer.getComparator();
	    }
	    return null;
	}
	
	private void createTableColumns(final Table table, final String[] tableHeaders) {
		for (final String colHeader:tableHeaders) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(colHeader);
	        column.addListener(SWT.Selection, new Listener() {
	            public void handleEvent(Event e) {
	                TableColumn[] cols = table.getColumns();
	                int columnIndex = 0;
	                for (int i=0; i<cols.length; i++) {
	                    if (cols[i] == e.widget) {
	                        columnIndex = i;
	                        break;
	                    }
	                }
	                TableViewerComparator _comparator = getComparator();
	                if (_comparator != null) {
    	                if (_comparator.getSortColumn() == columnIndex) {
    	                    _comparator.setAscendingOrder(!_comparator.isAscendingOrder());
    	                }
    	                else {
    	                    _comparator.setSortColumn(columnIndex);
    	                }
    	                _ascending = !_ascending;
    	                _viewer.refresh();
	                }
//	                int index = getIndex(tableHeaders, colHeader);
//	                TableItem[] items = table.getItems();
//	                Collator collator = Collator.getInstance(Locale.getDefault());
//	                for (int i = 1; i < items.length; i++) {
//	                    String value1 = items[i].getText(index);
//	                    Object data = items[i].getData();
//	                    int cols = table.getColumnCount();
//	                    Image[] images = new Image[cols];
//	                    for(int x = 0; x < cols-1; x++) {
//	                        images[x] = items[i].getImage(x);
//	                    }
//	                    if (!value1.equals("")) { //$NON-NLS-1$
//	                        for (int j = 0; j < i; j++) {
//	                            String value2 = items[j].getText(index);
//	                            if (_ascending) {
//	                                if (collator.compare(value1, value2) < 0) {
//	                                    String[] values = new String[items.length];
//	                                    for (int k = 0; k < items.length; k++) {
//	                                        values[k] = items[i].getText(k);
//	                                    }
//	                                    items[i].dispose();
//	                                    TableItem item = new TableItem(table, SWT.NONE, j);
//	                                    item.setData(data);
//	                                    item.setText(values);
//	                                    for(int x = 0; x < cols; x++) {
//	                                        Image image = images[x];
//	                                        if (image == null) {
//	                                            image = StandardTableLabelProvider.REMOVE_IMAGE;
//	                                        }
//	                                        item.setImage(x, image);
//	                                    }
//	                                    items = table.getItems();
//	                                    break;
//	                                }
//	                            } else {
//	                                if (collator.compare(value2, value1) < 0) {
//	                                    String[] values = new String[items.length];
//	                                    for (int k = 0; k < items.length; k++) {
//	                                        values[k] = items[i].getText(k);
//	                                    }
//	                                    items[i].dispose();
//	                                    TableItem item = new TableItem(table, SWT.NONE, j);
//	                                    item.setData(data);
//	                                    item.setText(values);
//	                                    for(int x = 0; x < cols; x++) {
//	                                        Image image = images[x];
//	                                        if (image == null) {
//	                                            image = StandardTableLabelProvider.REMOVE_IMAGE;
//	                                        }
//	                                        item.setImage(x, image);
//	                                    }
//	                                    items = table.getItems();
//	                                    break;
//	                                }
//	                            }
//	                       }
//	                    }
//	                }
	            }
	        });
		}
		if ((_style & REMOVEABLE_ROWS) != 0) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(""); //$NON-NLS-1$
			column.setToolTipText(Messages.StandardTableViewer_2); 
		}
	}
	
    private void initColumnProperties() {
		String[] headers = new String[getTable().getColumnCount()];
		for (int i=0; i<headers.length; i++) {
			TableColumn column = getTable().getColumns()[i];
			headers[i] = column.getText();
		}
		if ((getStyle() & REMOVEABLE_ROWS) > 0) {
			headers[headers.length-1] = REMOVE_COLUMN;
		}
		_viewer.setColumnProperties(headers);

	}
	
    
    private static void createTooltipForDelete(final Table table) {
        Listener tableListener = new Listener() {

            private Shell _tip = null;
            private Label _label = null;

            public void handleEvent(Event event) {
                switch (event.type) {
                case SWT.Dispose:
                case SWT.KeyDown:
                case SWT.MouseMove:
                    if (_tip == null) {
                        break;
                    }
                    _tip.dispose();
                    _tip = null;
                    _label = null;
                    break;
                case SWT.MouseHover:
                    TableItem item = table.getItem(new Point(event.x, event.y));
                    if (item != null && item.getBounds(table.getColumnCount() - 1).contains(event.x, event.y)) {
                        if (_tip != null && !_tip.isDisposed()) {
                            _tip.dispose();
                        }
                        _tip = new Shell(table.getShell(), SWT.ON_TOP);
                        _tip.setLayout(new FillLayout());
                        _label = new Label(_tip, SWT.NONE);
                        _label.setForeground(table.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                        _label.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                        _label.setData("_TABLEITEM", item); //$NON-NLS-1$
                        _label.setText(Messages.StandardTableViewer_1); 
                        Point size = _tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                        Point pt = table.toDisplay(event.x, event.y);
                        _tip.setBounds(pt.x, pt.y - size.y, size.x, size.y);
                        _tip.setVisible(true);
                    }
                }
            }
        };
        table.addListener(SWT.Dispose, tableListener);
        table.addListener(SWT.KeyDown, tableListener);
        table.addListener(SWT.MouseMove, tableListener);
        table.addListener(SWT.MouseHover, tableListener);

    }
    
    
    public void setContentModel(ITableContentModel model) {
    	if (_contentListener == null) {
    		_contentListener = new TableContentAdapter() {
    			@Override
				public void cellChanged(TableChangeEvent event) {
    				_viewer.update(event.getSource(), null);
    				if ((_style & MULTI_ROWED) > 0) {
    					if (event.getSource() == _contentModel.getLastRow()) {
    						//last row edited, add aditional row
    						_viewer.add(_contentModel.addRow());
    					}
    				}
    			}
    			@Override
				public void rowAdded(TableChangeEvent event) {
    				_viewer.add(event.getSource());
    			}
    			@Override
				public void rowRemoved(TableChangeEvent event) {
    				_viewer.remove(event.getSource());
    			}
    		};
    	}

    	//if the listener was already registered for a previous model, remove the listener
    	if (_contentModel != null) {
    		_contentModel.removeContentListener(_contentListener);
    	}

    	_contentModel = model;
    	_viewer.setContentProvider(new DefaultContentProvider(model));
    	_viewer.setLabelProvider(new StandardTableLabelProvider(true));
    	_viewer.setCellModifier(new StandardTableCellModifier(this, model));
    	_contentModel.addContentListener(_contentListener);
    }
    
    public ITableContentModel getContentModel() {
    	return _contentModel;
    }
    
    public TableViewer getTableViewer() {
    	return _viewer;
    }
    
    public Table getTable() {
    	return _viewer.getTable();
    }
    
    public int getStyle() {
    	return _style;
    }
    
    public int getColumnIndex(String columnProperty) {
    	return Arrays.asList(_viewer.getColumnProperties()).indexOf(columnProperty);
    }
    
    public CellEditor[] getCellEditors() {
    	return _viewer.getCellEditors();
    }
    
    public void setCellEditors(CellEditor[] editors) {
    	CellEditor[] cellEditors;
    	if ((getStyle() & REMOVEABLE_ROWS) > 0) {
    		cellEditors = new CellEditor[editors.length+1];
    		System.arraycopy(editors, 0, cellEditors, 0, editors.length);
    		cellEditors[cellEditors.length-1] = new TextCellEditor(getTable());
    	}
    	else {
    		cellEditors = editors;
    	}
		_viewer.setCellEditors(cellEditors);
    }
    
    public void setCellModifier(ICellModifier cellModifier) {
        _viewer.setCellModifier(cellModifier);
    }
    
    public void setColumnWidth(int index, int width) {
    	_viewer.getTable().getColumn(index).setWidth(width);
    }
    
    public AbstractColumnCursor getCursor() {
    	return _cursor;
    }
}
