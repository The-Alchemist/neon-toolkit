/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.project;


import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;
import org.neontoolkit.gui.navigator.project.AbstractOntologyProjectProvider;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;

/**
 * Provider for owl ontology projects in the OntologyNavigator.
 */

public class OWLOntologyProjectProvider extends AbstractOntologyProjectProvider { // DefaultTreeDataProvider {
	private OWLProjectControl _control;
	private DecoratingLabelProvider _provider;
	
	public class OWLOntologyProjectListener extends OntologyProjectListener {
	    @Override
	    public void ontologyRenamed(String projectName, String oldOntologyUri, String newOntologyUri) {
            try {
                if(OWLGUIUtilities.isOWLProject(projectName)) {
                    refreshProject(projectName, true);
                }
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
	    }
	}
	
	public OWLOntologyProjectProvider() {
	    super();
		_control = OWLProjectControl.getDefault();
        NeOnCorePlugin.getDefault().removeOntologyProjectListener(_ontologyProjectListener);
        _ontologyProjectListener = new OWLOntologyProjectListener();
        NeOnCorePlugin.getDefault().addOntologyProjectListenerByLanguage(_ontologyProjectListener, OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE);
	}

	@Override
	public void dispose() {
		super.dispose();
        NeOnCorePlugin.getDefault().removeOntologyProjectListener(_ontologyProjectListener);
	}
	
	@Override
    public TreeElementPath[] getPathElements(ITreeElement element) {
		if (element instanceof OWLProjectTreeElement) {
			TreeElementPath path = new TreeElementPath();
			path.append(element);
			return new TreeElementPath[] {path};
		} else if (element instanceof IProjectElement) {
			TreeElementPath path = new TreeElementPath();
			path.append(new OWLProjectTreeElement(((IProjectElement) element).getProjectName(), this));
			return new TreeElementPath[] {path};
		}
		return new TreeElementPath[0];
	}

	@Override
	public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount) {
		try {
	        String[] projects = _control.getOwlOntologyProjects();
	        ITreeElement[] elements = new ITreeElement[projects.length];
	        for (int i = 0; i < projects.length; i++) {
	        	elements[i] = newTreeElement(projects[i]);
	        }
	        return elements;
		} catch (CommandException e) {
			OWLPlugin.logError(e);
			return new ITreeElement[0];
		}
	}
	
	@Override
	protected ITreeElement newTreeElement(String projectName) {		
		return new OWLProjectTreeElement(projectName, this);
	}
	
	@Override
	protected ITreeElement newTreeElement(String projectName, String ontologyLanguage) {
        if (OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE.equals(ontologyLanguage)) {
            return new OWLProjectTreeElement(projectName, this);
        }
        return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.DefaultTreeDataProvider#getImage(com.ontoprise.ontostudio.gui.navigator.ITreeElement)
	 */
	@Override
	public Image getImage(ITreeElement element) {		
	    return getLabelProvider().getImage(NeOnCorePlugin.getDefault().getProject(element.toString()));
	}
	
    @Override
    protected DecoratingLabelProvider getLabelProvider() {
        if (_provider == null){
            ILabelProvider labelProv = new LabelProvider() {
                @Override
                public Image getImage(Object element) {
                    return (element instanceof IProject) 
                            ? OWLPlugin.getDefault().getImageRegistry().getDescriptor(OWLSharedImages.PROJECT).createImage()
//                            ? PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(SharedImages.IMG_OBJ_PROJECT).createImage()
                            : null;
//                  return (element instanceof IProject) ? OntoStudioImages.get(OntoStudioImages.PROJECT) : null;
                }
                @Override
                public String getText(Object element) {
                    if (element instanceof IProject) {
                        IProject project = (IProject)element;
                        try {
                            IOntologyProject ontoProj = OntologyProjectManager.getDefault().getOntologyProject(project.getName());
                            if (ontoProj != null) {
                                return ontoProj.toString();
                            }
                        } catch (NeOnCoreException ce) {
                            OWLPlugin.logError(ce);
                        }
                    }
                    return element.toString();
                }
            };
            DecoratingLabelProvider prov = new DecoratingLabelProvider(labelProv, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
//            _provider = new DecoratingLabelProvider(prov, new OntologyProjectDecorator());
            _provider = new DecoratingLabelProvider(prov, null);
        }
        return _provider;
    }

}
