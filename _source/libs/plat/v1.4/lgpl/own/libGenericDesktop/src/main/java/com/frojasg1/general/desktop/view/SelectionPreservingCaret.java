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
package com.frojasg1.general.desktop.view;

/**
 *
 * @author Usuario
 */
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;
import java.awt.event.FocusEvent;

/**
 * http://javatechniques.com/blog/fixing-disappearing-text-selections-when-a-menu-is-opened/
 * 
 * Caret implementation that doesn't blow away the selection when
 * we lose focus.
 */
public class SelectionPreservingCaret extends DefaultCaret
{
    /**
     * The last SelectionPreservingCaret that lost focus
     */
    private static SelectionPreservingCaret last = null;

    /**
     * The last event that indicated loss of focus
     */
    private static FocusEvent lastFocusEvent = null;

    public SelectionPreservingCaret() {
        // The blink rate is set by BasicTextUI when the text component
        // is created, and is not (re-) set when a new Caret is installed.
        // This implementation attempts to pull a value from the UIManager,
        // and defaults to a 500ms blink rate. This assumes that the
        // look and feel uses the same blink rate for all text components
        // (and hence we just pull the value for TextArea). If you are
        // using a look and feel for which this is not the case, you may
        // need to set the blink rate after creating the Caret.
//        int blinkRate = 500;
        int blinkRate = 125;
        Object o = UIManager.get("TextArea.caretBlinkRate");
        if ((o != null) && (o instanceof Integer)) {
            Integer rate = (Integer) o;
            blinkRate = rate.intValue();
        }
        setBlinkRate(blinkRate);
    }

    /**
     * Called when the component containing the caret gains focus. 
     * DefaultCaret does most of the work, while the subclass checks
     * to see if another instance of SelectionPreservingCaret previously
     * had focus.
     *
     * @param e the focus event
     * @see java.awt.event.FocusListener#focusGained
     */
    public void focusGained(FocusEvent evt) {
        super.focusGained(evt);

        // If another instance of SelectionPreservingCaret had focus and
        // we defered a focusLost event, deliver that event now.
        if ((last != null) && (last != this)) {
            last.hide();
        }
    }

    /**
     * Called when the component containing the caret loses focus. Instead
     * of hiding both the caret and the selection, the subclass only 
     * hides the caret and saves a (static) reference to the event and this
     * specific caret instance so that the event can be delivered later
     * if appropriate.
     *
     * @param e the focus event
     * @see java.awt.event.FocusListener#focusLost
     */
    public void focusLost(FocusEvent evt) {
        setVisible(false);
        last = this;
        lastFocusEvent = evt;
    }

    /**
     * Delivers a defered focusLost event to this caret.
     */
    protected void hide() {
        if (last == this) {
            super.focusLost(lastFocusEvent);
            last = null;
            lastFocusEvent = null;
        }
    }
}