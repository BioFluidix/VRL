/* 
 * SelectionRectangleController.java
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007–2018 by Michael Hoffer,
 * Copyright (c) 2015–2018 G-CSC, Uni Frankfurt,
 * Copyright (c) 2009–2015 Steinbeis Forschungszentrum (STZ Ölbronn)
 * 
 * This file is part of Visual Reflection Library (VRL).
 *
 * VRL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * see: http://opensource.org/licenses/LGPL-3.0
 *      file://path/to/VRL/src/eu/mihosoft/vrl/resources/license/lgplv3.txt
 *
 * VRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * This version of VRL includes copyright notice and attribution requirements.
 * According to the LGPL this information must be displayed even if you modify
 * the source code of VRL. Neither the VRL Canvas attribution icon nor any
 * copyright statement/attribution may be removed.
 *
 * Attribution Requirements:
 *
 * If you create derived work you must do three things regarding copyright
 * notice and author attribution.
 *
 * First, the following text must be displayed on the Canvas:
 * "based on VRL source code". In this case the VRL canvas icon must be removed.
 * 
 * Second, the copyright notice must remain. It must be reproduced in any
 * program that uses VRL.
 *
 * Third, add an additional notice, stating that you modified VRL. A suitable
 * notice might read
 * "VRL source code modified by YourName 2012".
 * 
 * Note, that these requirements are in full accordance with the LGPL v3
 * (see 7. Additional Terms, b).
 *
 * Please cite the publication(s) listed below.
 *
 * Publications:
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */

package eu.mihosoft.vrl.visual;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class SelectionRectangleController {

    private SelectionRectangle rectangle;
    private RectMousecontrol mouseControl;

    public SelectionRectangleController(Canvas canvas,
            SelectionRectangle rectangle) {
        this.rectangle = rectangle;
        mouseControl = new RectMousecontrol(canvas, rectangle);
    }

    public MouseListener getMouseListener() {
        return mouseControl;
    }

    public MouseMotionListener getMouseMotionListener() {
        return mouseControl;
    }
}

class RectMousecontrol implements MouseListener, MouseMotionListener {

    private Point firstPoint;
    private Point secondPoint;
    private SelectionRectangle rectangle;
    private Canvas canvas;

    public RectMousecontrol(Canvas canvas, SelectionRectangle rectangle) {
        this.rectangle = rectangle;
        this.canvas = canvas;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            firstPoint = e.getPoint();
        } else {
            firstPoint = null;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        // find selected windows

        for (CanvasWindow w : canvas.getWindows()) {
            if (rectangle.getBounds() != null
                    && w.isVisible()
                    && w.getWindowBounds().intersects(rectangle.getBounds())) {
                canvas.getClipBoard().select(w);

                w.getMainCanvas().getWindows().setActive(w);

                // reset location (the internal location variable of w)
                w.setWindowLocation(
                        w.getLocation().x + w.getInsets().left,
                        w.getLocation().y + w.getInsets().top);
            }
        }

        rectangle.reset();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        secondPoint = e.getPoint();

        if (firstPoint == null) {
            return;
        }

        firstPoint.x = Math.max(firstPoint.x, 0);
        firstPoint.y = Math.max(firstPoint.y, 0);

        secondPoint.x = Math.max(secondPoint.x, 0);
        secondPoint.y = Math.max(secondPoint.y, 0);

        int x = Math.min(firstPoint.x, secondPoint.x);
        int y = Math.min(firstPoint.y, secondPoint.y);

        int width = Math.abs(secondPoint.x - firstPoint.x);
        int height = Math.abs(secondPoint.y - firstPoint.y);

        rectangle.setBounds(new Rectangle(x, y, width, height));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
