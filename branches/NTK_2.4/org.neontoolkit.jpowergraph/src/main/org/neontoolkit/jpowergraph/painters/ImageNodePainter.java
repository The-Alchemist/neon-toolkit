/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.jpowergraph.painters;

import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.painters.NodePainter;
import net.sourceforge.jpowergraph.pane.JGraphPane;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.neontoolkit.jpowergraph.nodes.DefaultImageNode;
import org.neontoolkit.jpowergraph.nodes.IOntoPowerGraphNode;


/*
 * Created by Werner Hihn
 */

public class ImageNodePainter implements NodePainter {

    private Image _image;

    protected FontMetrics _fontMetrics;
    private GC _gc;

    protected Color _backgroundColor;
    protected Color _textColor;

    private Composite _parent;
    private Color _lightGrey;
    private Color _black;

    public ImageNodePainter(Composite parent, Image image, Color backgroundColor, Color textColor) {
        this(parent, image);
        _backgroundColor = backgroundColor;
        _textColor = textColor;
    }

    public ImageNodePainter(Composite parent, Image image) {
        this();
        _image = image;
        _parent = parent;
    }

    /**
     * Called when a NodePainter is created when initializing extension point <code>org.neontoolkit.ontovisualize.visualizerContribution</code>.<br>
     * If this happens, the methods <code>setParent(Composite parent)</code> and <code>setImage(Image image)</code> have to be called.
     */
    public ImageNodePainter() {
        super();
        _black = new Color(null, 0, 0, 0);
        _lightGrey = new Color(null, 220, 220, 220);
    }

    public void setParent(Composite parent) {
        _parent = parent;
    }

    public void setImage(Image image) {
        _image = image;
    }

    public void paintNode(JGraphPane graphPane, GC g, Node node, int size) {
        if (_fontMetrics == null) {
            _fontMetrics = g.getFontMetrics();
        }

        Point nodePoint = graphPane.getScreenPointForNode(node);

        Image image = ((DefaultImageNode) node).getImage();
        if (image != null) {
            g.drawImage(image, nodePoint.x - 7, nodePoint.y - 7);
        }
        Color oldColor = g.getBackground();
        if (((IOntoPowerGraphNode) node).isRoot()) {
            g.setBackground(new Color(graphPane.getDisplay(), 180, 180, 180));
        } else {
            g.setBackground(new Color(graphPane.getDisplay(), 230, 230, 250));
        }
        drawText(g, node, nodePoint, graphPane);
        g.setBackground(oldColor);
    }

    protected void drawText(GC g, Node node, Point nodePoint, JGraphPane graphPane) {
        g.drawText(node.getLabel(), nodePoint.x + _image.getImageData().width, nodePoint.y + _image.getImageData().y - 5, false);
    }

    public boolean isInNode(JGraphPane graphPane, Node node, Point point, int size) {
        Rectangle nodeScreenRectangle = new Rectangle(0, 0, 0, 0);
        getNodeScreenBounds(graphPane, node, size, nodeScreenRectangle);
        return nodeScreenRectangle.contains(point);
    }

    public void getNodeScreenBounds(JGraphPane graphPane, Node node, int size, Rectangle nodeScreenRectangle) {
        Point nodePoint = graphPane.getScreenPointForNode(node);
        String label = node.getLabel();

        if (_gc == null) {
            Image im = new Image(_parent.getDisplay(), 100, 100);
            _gc = new GC(im);
        }

        if (_fontMetrics == null) {
            _fontMetrics = _gc.getFontMetrics();
        }

        if (size == NodePainter.LARGE) {
            int width = 1;
            int height = 1;
            if (label != null) {
                width += stringWidth(_gc, label) + 6;
                width += _image.getBounds().width + 10;
                height += _fontMetrics.getAscent() + _fontMetrics.getDescent() + 4;
            } else {
                width += 40;
                height += 20;
            }

            nodeScreenRectangle.x = nodePoint.x - 10;
            nodeScreenRectangle.y = nodePoint.y - 10;
            nodeScreenRectangle.width = width;
            nodeScreenRectangle.height = height;
        } else if (size == NodePainter.SMALL) {
            int width = 20 + stringWidth(_gc, label);
            int height = 8;
            nodeScreenRectangle.x = nodePoint.x - width / 2;
            nodeScreenRectangle.y = nodePoint.y - height / 2;
            nodeScreenRectangle.width = width;
            nodeScreenRectangle.height = height;
        }
    }

    public void paintLegendItem(JGraphPane graphPane, GC g, Point thePoint, String legendText) {
        Color oldColor = g.getBackground();
        g.setBackground(_lightGrey);
        g.setForeground(_black);
        g.drawImage(_image, thePoint.x, thePoint.y - 2);
        g.drawText(legendText, thePoint.x + _image.getImageData().width + 5, thePoint.y + _image.getImageData().y - 3);
        g.setBackground(oldColor);
    }

    public Point getLegendItemSize(JGraphPane graphPane, String legendText) {
        if (_gc == null) {
            Image im = new Image(graphPane.getParent().getDisplay(), 100, 100);
            _gc = new GC(im);
        }

        if (_fontMetrics == null) {
            _fontMetrics = _gc.getFontMetrics();
        }

        int padding = 2;
        int imageWidth = 18;
        int imageHeight = 18;
        int stringWidth = stringWidth(_gc, legendText);
        int width = imageWidth + stringWidth + (padding * 3);
        int height = Math.max(imageHeight, _fontMetrics.getAscent() + _fontMetrics.getDescent() + 4);
        return new Point(width, height);
    }

    private int stringWidth(GC g, String s) {
        return g.stringExtent(s).x;
    }

    public void dispose() {

    }

    public boolean isDisposed() {
        return false;
    }

}
