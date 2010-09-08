/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;

//import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;

/*
 * Created on 29.09.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class AbstractMainIDPropertyPage extends AbstractIDPropertyPage implements IMainPropertyPage {

    protected Map<String,Object> WIDGETS_FOR_TESTING = new HashMap<String,Object>();
    
	private int _style;
    private List<IPropertyPage> _children;
    protected IStructuredSelection _selection;
    private IWorkbenchPart _part;
    protected IWorkbenchPart _selectedPart;
    
    protected static boolean _showActualOntology;
    

    /*
	 * UI components
	 */
    protected StackLayout _stackLayout;
	protected Composite _composite;
    protected Composite _uriComposite;
//    protected Text _uriText;
    protected Text _uriText;
    protected Text _ontologyText;
	protected Composite _qNameComposite;
	protected Text _qNameText;
	protected Composite _localComposite;
	protected Text _localText;
    protected ScrolledComposite _scrolledComposite;

       
    /**
	 * 
	 */
	public AbstractMainIDPropertyPage() {
    	this(SWT.H_SCROLL | SWT.V_SCROLL);
    }
    
    /**
     * Supported styles are SWT.H_SCROLL and SWT.V_SCROLL to indicate that the main area
     * should be scrollable in horizontal and/or vertical order or not scrollable at all.
     * @param style
     */
    public AbstractMainIDPropertyPage(int style) {
    	_style = style;
        _children = new ArrayList<IPropertyPage>();
    }
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IMainPropertyPage#addSubPage(org.neontoolkit.gui.properties.IPropertyPage)
	 */
	public void addSubPage(IPropertyPage page) {
		_children.add(page);
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IMainPropertyPage#createGlobalContents(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createGlobalContents(Composite parent) {
    	Group group = new Group(parent, SWT.NONE);
//    	group.setText(Messages.AbstractMainIDPropertyPage_3);
    	group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
    	group.setLayout(new GridLayout(2, false));
    	
    	Composite composite = new Composite(group, SWT.NONE);
    	composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
    	_stackLayout = new StackLayout();
    	composite.setLayout(_stackLayout);

        _showActualOntology = false;
        _uriComposite = new Composite(composite, SWT.NONE);
        _uriComposite.setLayout(new GridLayout(2, false));
        createUriComposite();
    	Label label;
    	GridData data;
    	
    	_qNameComposite = new Composite(composite, SWT.NONE);
    	_qNameComposite.setLayout(new GridLayout(2, false));
    	label = new Label(_qNameComposite, SWT.NONE);
    	label.setText(Messages.AbstractMainIDPropertyPage_1);
    	data = new GridData(GridData.FILL, GridData.FILL, false, false);
        data.verticalAlignment = GridData.CENTER;
        label.setLayoutData(data);
        _qNameText = new Text(_qNameComposite, SWT.BORDER);
    	_qNameText.setText(""); //$NON-NLS-1$
    	_qNameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
    	_qNameText.setEditable(false);
    	
    	_localComposite = new Composite(composite, SWT.NONE);
    	_localComposite.setLayout(new GridLayout(2, false));
    	label = new Label(_localComposite, SWT.NONE);
    	label.setText(Messages.AbstractMainIDPropertyPage_2);
    	data = new GridData(GridData.FILL, GridData.FILL, false, false);
        data.verticalAlignment = GridData.CENTER;
        label.setLayoutData(data);
    	_localText = new Text(_localComposite, SWT.BORDER);
    	_localText.setText(""); //$NON-NLS-1$
    	_localText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
    	_localText.setEditable(false);

      _stackLayout.topControl = _uriComposite; 
		return group;
	}
	

	/**
	 * @param composite 
     * 
     */
    protected void createUriComposite() {

        Label label; 
        GridData data;
        _uriComposite = new Composite(_uriComposite.getParent(), SWT.NONE);
        _uriComposite.setLayout(new GridLayout(2, false));
        
        if(_showActualOntology){
            label = new Label(_uriComposite, SWT.NONE);
            label.setText(Messages.AbstractMainIDPropertyPage_4);
            data = new GridData(GridData.FILL, GridData.FILL, false, false);
            data.verticalAlignment = GridData.CENTER;
            label.setLayoutData(data);
            _ontologyText = new Text(_uriComposite, SWT.BORDER);
            _ontologyText.setText(""); //$NON-NLS-1$
            _ontologyText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
            _ontologyText.setEditable(false);

            
            Label label2 = new Label(_uriComposite, SWT.NONE);
            label2.setText(Messages.AbstractMainIDPropertyPage_5);
            GridData data2 = new GridData(GridData.FILL, GridData.FILL, false, false);
            data2.verticalAlignment = GridData.CENTER;
            label2.setLayoutData(data2);
            
            _uriText = new Text(_uriComposite, SWT.BORDER);
            _uriText.setText(""); //$NON-NLS-1$
            _uriText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
            _uriText.setEditable(false);
        }else{
            Label label2 = new Label(_uriComposite, SWT.NONE);
            label2.setText(Messages.AbstractMainIDPropertyPage_0);
            GridData data2 = new GridData(GridData.FILL, GridData.FILL, false, false);
            data2.verticalAlignment = GridData.CENTER;
            label2.setLayoutData(data2);

            _uriText = new Text(_uriComposite, SWT.BORDER);
            _uriText.setText(""); //$NON-NLS-1$
            _uriText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
            _uriText.setEditable(false);
        }
        
    }

    /* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IMainPropertyPage#getPart()
	 */
	public IWorkbenchPart getPart() {
		return _part;
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IMainPropertyPage#getSelectedPart()
	 */
	public IWorkbenchPart getSelectedPart() {
		return _selectedPart;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IMainPropertyPage#getSelection()
	 */
	public IStructuredSelection getSelection() {
		return _selection;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IMainPropertyPage#getSubPages()
	 */
	public IPropertyPage[] getSubPages() {
		return _children.toArray(new IPropertyPage[0]);
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createContents(Composite parent) {
        //create the parent composite
        _composite = new Composite(parent, SWT.NONE);
        _composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		_composite.setLayout(layout);

		//create scrolled composite for the main area if scroll flags are set
        boolean supportScrolling = (_style & (SWT.H_SCROLL | SWT.V_SCROLL)) > 0;
		Composite content;
        if (supportScrolling) {
            _scrolledComposite = new ScrolledComposite(
    				_composite,
    				SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
            _scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 ,1));
    		_scrolledComposite.setExpandVertical((_style & SWT.V_SCROLL) > 0);
    		_scrolledComposite.setExpandHorizontal((_style & SWT.H_SCROLL) > 0);
    		_scrolledComposite.getVerticalBar().setIncrement(10);
    		content = new Composite(_scrolledComposite, SWT.NONE);
    		_scrolledComposite.setContent(content);
    		_scrolledComposite.addControlListener(new ControlAdapter(){
    			@Override
    			public void controlResized(ControlEvent e) {
    				setSize();
    			}
    		});
        }
        else {
    		content = new Composite(_composite, SWT.NONE);
            content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 ,1));
        }

		GridLayout contentLayout = new GridLayout(1, false);
		contentLayout.marginWidth = 0;
		contentLayout.marginHeight = 0;
		contentLayout.marginRight = 0;
		contentLayout.marginTop = 0;
		contentLayout.marginBottom = 0;
		content.setLayout(contentLayout);

		createMainArea(content);

        if (supportScrolling) {
        	_scrolledComposite.setMinSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
		
		return _composite;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#isDisposed()
	 */
	public boolean isDisposed() {
		return _composite == null || _composite.isDisposed();
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#refresh()
	 */
	public void refresh() {
	    selectIdTopControl();
		refreshIdArea();
		refreshComponents();
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.gui.properties.IPropertyPage#update()
	 */
	public void update() {
		_composite.getDisplay().syncExec(new Runnable() {
			public void run() {
				selectIdTopControl();
				updateComponents();
			}
		});
	}
	
    /**
     * This method is for the creation of the main components in the property page.
     * This method has to be implemented by subclasses.
     * @param composite
     */
    protected abstract void createMainArea(Composite composite);

    /**
	 * Called if refresh is called to refresh the components defined by subclasses.
	 */
	public abstract void refreshComponents();
    
	/**
	 * Called if update is called to update the components defined by subclasses.
	 * NOTE: this method is automatically executed in the UI thread.
	 */
	public abstract void updateComponents();

    protected abstract void refreshIdArea();
    
    protected void selectIdTopControl() {
        if (_stackLayout != null) {
    		switch (NeOnUIPlugin.getDefault().getIdDisplayStyle()) {
    		case NeOnUIPlugin.DISPLAY_LOCAL:
    		    _stackLayout.topControl = _localComposite;
    			break;
    		case NeOnUIPlugin.DISPLAY_QNAME:
    		    _stackLayout.topControl = _qNameComposite;
    			break;
    		default:
    		    _stackLayout.topControl = _uriComposite;
    			break;
    		}
    		_stackLayout.topControl.getParent().layout();
        }
    }

    private void setSize() {
		Point size = _scrolledComposite.getSize();
		if (needVScroll()) {
			size.x -= _scrolledComposite.getVerticalBar().getSize().x;
		}
		_scrolledComposite.getContent().setSize(size);
    }
    
    boolean needVScroll() {
    	boolean hVisible = _scrolledComposite.getHorizontalBar().isVisible();
    	ScrollBar vBar = _scrolledComposite.getVerticalBar();
    	if (vBar == null) return false;
    	
    	Rectangle hostRect = _scrolledComposite.getBounds();
    	int border = _scrolledComposite.getBorderWidth();
    	hostRect.height -= 2*border;
    	ScrollBar hBar = _scrolledComposite.getHorizontalBar();
    	if (hVisible && hBar != null) hostRect.height -= hBar.getSize().y;
    	
    	if (_scrolledComposite.getMinHeight() > hostRect.height) return true;
    	return false;
    }
    
    /* (non-Javadoc)
     * @see org.neontoolkit.gui.properties.IMainPropertyPage#setPart(org.eclipse.ui.IWorkbenchPart)
     */
    public void setPart(IWorkbenchPart part) {
    	_part = part;
    }
    
    /* (non-Javadoc)
     * @see com.ontoprise.ontostudio.flogic.ui.properties.AbstractIDPropertyPage#setSelection(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void setSelection(IWorkbenchPart part, IStructuredSelection selection) {
        switchPerspective();
    	if (selection != getSelection()) {
    		_selection = selection;
    		_selectedPart = part;
    		initSelection();
    		refreshIdArea();
    		selectIdTopControl();
    	}
    }
    
    /* (non-Javadoc)
     * @see com.ontoprise.ontostudio.flogic.ui.properties.AbstractIDPropertyPage#selectTab()
     */
    @Override
    public void selectTab() {
    	refreshComponents();
    }

    public Map<String,Object> getWidgetsForTesting() {
        return WIDGETS_FOR_TESTING;
    }
    

    /**
     * @return the _showActualOntology
     */
    public static boolean is_showActualOntology() {
        return _showActualOntology;
    }

    /**
     * @param showActualOntology the _showActualOntology to set
     */
    public static void set_showActualOntology(boolean showActualOntology) {
        _showActualOntology = showActualOntology;
    }
}
