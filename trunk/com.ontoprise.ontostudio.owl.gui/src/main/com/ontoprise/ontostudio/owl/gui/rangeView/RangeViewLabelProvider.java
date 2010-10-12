/*****************************************************************************
 * based on com.ontoprise.ontostudio.owl.gui.domainview.DomainViewLabelProvider
 * developed by ontoprise GmbH
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.rangeView;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
/**
 * 
 * @author Nico Stieler
 * Created on: 08.10.2010
 */
public class RangeViewLabelProvider extends LabelProvider implements IColorProvider {

    public Color getBackground(Object element) {
        if (element instanceof PropertyTreeElement) {
            PropertyTreeElement item = (PropertyTreeElement) element;
            if (item.isImported()) {
                return OWLGUIUtilities.COLOR_FOR_IMPORTED_AXIOMS;
            }
        }
        return null;
    }

    public Color getForeground(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof PropertyTreeElement) {
            PropertyTreeElement item = (PropertyTreeElement) element;
            String y = item.toString();
            return y;
        }
        return null;
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
