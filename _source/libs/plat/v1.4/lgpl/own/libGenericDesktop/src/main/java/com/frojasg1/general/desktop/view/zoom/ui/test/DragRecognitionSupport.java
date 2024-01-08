/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.general.desktop.view.zoom.ui.test;

import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.frojasg1.general.desktop.view.javadesktopmodule.ReflectionToJavaDesktop;
//import com.frojasg1.sun.awt.AppContext;
//import com.frojasg1.sun.awt.dnd.SunDragSourceContextPeer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
class DragRecognitionSupport {
    private int motionThreshold;
    private MouseEvent dndArmedEvent;
    private JComponent component;

    /**
     * This interface allows us to pass in a handler to mouseDragged,
     * so that we can be notified immediately before a drag begins.
     */
    public static interface BeforeDrag {
        public void dragStarting(MouseEvent me);
    }

    /**
     * Returns the DragRecognitionSupport for the caller's AppContext.
     */
    private static DragRecognitionSupport getDragRecognitionSupport() {
        DragRecognitionSupport support =
                (DragRecognitionSupport) ReflectionToJavaDesktop.instance().AppContext_getAppContext_get(DragRecognitionSupport.class);
//            (DragRecognitionSupport)AppContext.getAppContext().
//                get(DragRecognitionSupport.class);

        if (support == null) {
            support = new DragRecognitionSupport();
            ReflectionToJavaDesktop.instance().AppContext_getAppContext_put(DragRecognitionSupport.class, support);
//            AppContext.getAppContext().put(DragRecognitionSupport.class, support);
        }

        return support;
    }

    /**
     * Returns whether or not the event is potentially part of a drag sequence.
     */
    public static boolean mousePressed(MouseEvent me) {
        return getDragRecognitionSupport().mousePressedImpl(me);
    }

    /**
     * If a dnd recognition has been going on, return the MouseEvent
     * that started the recognition. Otherwise, return null.
     */
    public static MouseEvent mouseReleased(MouseEvent me) {
        return getDragRecognitionSupport().mouseReleasedImpl(me);
    }

    /**
     * Returns whether or not a drag gesture recognition is ongoing.
     */
    public static boolean mouseDragged(MouseEvent me, BeforeDrag bd) {
        return getDragRecognitionSupport().mouseDraggedImpl(me, bd);
    }

    private void clearState() {
        dndArmedEvent = null;
        component = null;
    }

    private int mapDragOperationFromModifiers(MouseEvent me,
                                              TransferHandler th) {

        if (th == null || !SwingUtilities.isLeftMouseButton(me)) {
            return TransferHandler.NONE;
        }

//        return SunDragSourceContextPeer.
//            convertModifiersToDropAction(me.getModifiersEx(),
//                                         th.getSourceActions(component));
        return ReflectionToJavaDesktop.instance().SunDragSourceContextPeer_convertModifiersToDropAction(me.getModifiersEx(),
                                         th.getSourceActions(component));
    }

    /**
     * Returns whether or not the event is potentially part of a drag sequence.
     */
    private boolean mousePressedImpl(MouseEvent me) {
        component = (JComponent)me.getSource();

        if (mapDragOperationFromModifiers(me, component.getTransferHandler())
                != TransferHandler.NONE) {

            motionThreshold = DragSource.getDragThreshold();
            dndArmedEvent = me;
            return true;
        }

        clearState();
        return false;
    }

    /**
     * If a dnd recognition has been going on, return the MouseEvent
     * that started the recognition. Otherwise, return null.
     */
    private MouseEvent mouseReleasedImpl(MouseEvent me) {
        /* no recognition has been going on */
        if (dndArmedEvent == null) {
            return null;
        }

        MouseEvent retEvent = null;

        if (me.getSource() == component) {
            retEvent = dndArmedEvent;
        } // else component has changed unexpectedly, so return null

        clearState();
        return retEvent;
    }

    /**
     * Returns whether or not a drag gesture recognition is ongoing.
     */
    private boolean mouseDraggedImpl(MouseEvent me, BeforeDrag bd) {
        /* no recognition is in progress */
        if (dndArmedEvent == null) {
            return false;
        }

        /* component has changed unexpectedly, so bail */
        if (me.getSource() != component) {
            clearState();
            return false;
        }

        int dx = Math.abs(me.getX() - dndArmedEvent.getX());
        int dy = Math.abs(me.getY() - dndArmedEvent.getY());
        if ((dx > motionThreshold) || (dy > motionThreshold)) {
            TransferHandler th = component.getTransferHandler();
            int action = mapDragOperationFromModifiers(me, th);
            if (action != TransferHandler.NONE) {
                /* notify the BeforeDrag instance */
                if (bd != null) {
                    bd.dragStarting(dndArmedEvent);
                }
                th.exportAsDrag(component, dndArmedEvent, action);
                clearState();
            }
        }

        return true;
    }
}
