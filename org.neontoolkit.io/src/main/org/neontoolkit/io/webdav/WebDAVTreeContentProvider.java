/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.io.webdav;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.wvcm.ControllableFolder;
import javax.wvcm.Folder;
import javax.wvcm.Location;
import javax.wvcm.PropertyNameList;
import javax.wvcm.Resource;
import javax.wvcm.WvcmException;
import javax.wvcm.PropertyNameList.PropertyName;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/* 
 * Created on 11.10.2004
 * Created by Mika Maier-Collin
 *
 * Keywords: ContentProvider, WebDAV
 */

/**
 * Content provider for WebDAV takes a WebDAVSiteManager, a WebDAVConnection as input
 */
public class WebDAVTreeContentProvider implements ITreeContentProvider {

    public boolean _showFolder = true;
    public boolean _showFiles = true;
    private org.neontoolkit.io.filter.FileFilter _fileFilter;
    private org.neontoolkit.io.filter.FileFilter[] _fileFilters;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
	public Object[] getChildren(Object parentElement) {
        ArrayList<Resource> elements = new ArrayList<Resource>();
        if (parentElement instanceof WebDAVSiteManager) {
            return ((WebDAVSiteManager) parentElement).getSites().values().toArray();
        } else if (parentElement instanceof WebDAVConnection) {
            Resource resource = null;
            try {
                resource = ((WebDAVConnection) parentElement).connect();
            } catch (Exception e) {
            }
            if (!((WebDAVConnection) parentElement).isConnected()) {
                return elements.toArray();
            }
            parentElement = resource;
        } else if (parentElement instanceof File) {
            return ((File) parentElement).listFiles(new FileFilter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see java.io.FileFilter#accept(java.io.File)
                 */
                public boolean accept(File arg0) {
                    if (arg0.isDirectory()) {
                        if (_showFolder) {
                            return true;
                        }
                    } else {
                        if (_showFiles) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        if (parentElement instanceof Resource) {
            Resource resource = (Resource) parentElement;
            Location location = resource.location();
            ControllableFolder folder = (ControllableFolder) location.folder();
            PropertyNameList pnl = new PropertyNameList(new PropertyName[] {PropertyName.LAST_MODIFIED});
            try {
                Iterator<Resource> mlIter = folder.doReadMemberList(pnl, false);
                while (mlIter.hasNext()) {
                    Resource r = (Resource) mlIter.next();
                    if (r instanceof Folder) {
                        if (_showFolder) {
                            elements.add(r);
                        }
                    } else {
                        if (_showFiles) {
                            if (_fileFilters != null) {
                            	for (int i = 0; i < _fileFilters.length; i++) {
									org.neontoolkit.io.filter.FileFilter fileFilter = _fileFilters[i];
	                                if (fileFilter.accept(new File(r.location().lastSegment()))) {
	                                    elements.add(r);
	                                    break;
	                                }									
								}
                            } else {
                                elements.add(r);
                            }
                        }
                    }
                }
            } catch (WvcmException e) {
            //ignore
            }
        }
        return elements.toArray();
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object element) {
        if (element instanceof Resource) {
            if (element instanceof Folder) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
        ArrayList<Resource> elements = new ArrayList<Resource>();
        if (inputElement instanceof WebDAVSiteManager) {
            return ((WebDAVSiteManager) inputElement).getSites().values().toArray();
        } else if (inputElement instanceof WebDAVConnection) {
            Resource resource = null;
            boolean connected = false;
            try {
                connected = ((WebDAVConnection) inputElement).isConnected();
                if (connected) {
                    resource = ((WebDAVConnection) inputElement).getResource();
                } else {
                    resource = ((WebDAVConnection) inputElement).connect();
                    connected = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!connected) {
                return elements.toArray();
            }

            inputElement = resource;
        } else if (inputElement instanceof File) {
            return ((File) inputElement).listFiles(new FileFilter() {

                public boolean accept(File arg0) {
                    if (arg0.isDirectory()) {
                        if (_showFolder) {
                            return true;
                        }
                    } else {
                        if (_showFiles) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        if (inputElement instanceof Resource) {
            Resource resource = (Resource) inputElement;
            Location location = resource.location();
            ControllableFolder folder = (ControllableFolder) location.folder();
            try {
                Iterator<Resource> mlIter = folder.doReadMemberList(getLastModifiedPropertyNameList(), false);
                while (mlIter.hasNext()) {
                    Resource r = (Resource) mlIter.next();
                    if (r instanceof Folder) {
                        if (_showFolder) {
                            elements.add(r);
                        }
                    } else {
                        if (_showFiles) {
                            if (_fileFilter != null) {
                                if (_fileFilter.accept(new File(r.location().lastSegment()))) {
                                    elements.add(r);
                                }
                            } else {
                                elements.add(r);
                            }
                        }
                    }
                }
            } catch (WvcmException e) {
				//ignore
            }
        }
        return elements.toArray();
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof Resource) {
        }
    }

    /**
     * Returns FileFilter for the ontology-file
     * 
     * @return the used FileFilter
     */
    public org.neontoolkit.io.filter.FileFilter getFileFilter() {
        return _fileFilter;
    }

    /**
     * Sets the FileFilter for the ontology-file
     * 
     * @param fileFilter
     */
    public void setFileFilter(org.neontoolkit.io.filter.FileFilter fileFilter) {
        this._fileFilter = fileFilter;
    }

    public void setFileFilters(org.neontoolkit.io.filter.FileFilter[] fileFilters) {
        this._fileFilters = fileFilters;
    }

    private PropertyNameList getLastModifiedPropertyNameList() {
        PropertyNameList pnl = new PropertyNameList(new PropertyName[] {PropertyName.LAST_MODIFIED, PropertyNameList.PropertyName.DISPLAY_NAME});
        return pnl;
    }
}
