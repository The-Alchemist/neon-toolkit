/*****************************************************************************
 * based on com.ontoprise.ontostudio.owl.gui.domainview.DomainViewLabelProvider
 * developed by ontoprise GmbH
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.views.rangeView;

import java.util.Set;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyExtraDomainRangeinfoTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
/**
 * 
 * @author Nico Stieler
 * Created on: 08.10.2010
 */
public class RangeViewLabelProvider extends LabelProvider implements IColorProvider, IFontProvider{

    @Override
    public Color getBackground(Object element) {
        if (element instanceof PropertyExtraDomainRangeinfoTreeElement) {
            PropertyExtraDomainRangeinfoTreeElement treeElement = (PropertyExtraDomainRangeinfoTreeElement) element;
            if (treeElement.isImported()) {
                return OWLGUIUtilities.COLOR_FOR_IMPORTED_AXIOMS;
            }
        }
        return null;
    }

    @Override
    public Font getFont(Object element) {
        if (element instanceof PropertyExtraDomainRangeinfoTreeElement) {
            PropertyExtraDomainRangeinfoTreeElement treeElement = (PropertyExtraDomainRangeinfoTreeElement) element;
            if (!treeElement.isDirect()) {
                return OWLGUIUtilities.FONT_FOR_INHERITED_AXIOMS;
            }
        }
        return null;
    }
    @Override
    public Color getForeground(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof PropertyExtraDomainRangeinfoTreeElement) {
            PropertyExtraDomainRangeinfoTreeElement treeElement = (PropertyExtraDomainRangeinfoTreeElement) element;
            if (!treeElement.isDirect()) {
                String string = treeElement.toString();
                string += " ["; //$NON-NLS-1$
                Set<String> entityUris = treeElement.getOWLEntities();
                if(entityUris.size() > 0){
                    for(String entityUri : entityUris){
                        try {
                            string += OWLGUIUtilities.getEntityLabel(OWLUtilities.description(IRIUtils.ensureValidIRISyntax(entityUri)), 
                                    treeElement.getOntologyUri(), 
                                    treeElement.getProjectName());
                        } catch (NeOnCoreException e) {
                            string += entityUri;
                        }
                        string += ", "; //$NON-NLS-1$
                    }
                    string = string.substring(0, string.length() - 2);
                }
                string += "]"; //$NON-NLS-1$
                return string;
            }
        }
        return element.toString();
    }


    @Override
    public Image getImage(Object element) {
        if (element instanceof DataPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
        } else if (element instanceof ObjectPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
        } else if (element instanceof AnnotationPropertyTreeElement) {
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
        } else { 
            return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY); // error case
        }
    }

}
