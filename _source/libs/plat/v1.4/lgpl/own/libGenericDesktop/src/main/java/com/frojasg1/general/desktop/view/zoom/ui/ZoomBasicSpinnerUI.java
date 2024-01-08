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
package com.frojasg1.general.desktop.view.zoom.ui;

import com.frojasg1.general.desktop.view.zoom.components.ZoomBasicArrowButton_;
import com.frojasg1.general.number.DoubleReference;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomBasicSpinnerUI extends BasicSpinnerUI implements ComponentUIforZoomInterface
{
	protected DoubleReference _zoomFactor = null;

	protected ZoomBasicArrowButton_ _nextButton = null;
	protected ZoomBasicArrowButton_ _previousButton = null;

	public ZoomBasicSpinnerUI()
	{
		super();
	}

    public static ComponentUI createUI( JComponent x ) {
        return new ZoomBasicSpinnerUI();
    }

	@Override
	public void init()
	{
	}
	
	public void setZoomFactorReference( DoubleReference zoomFactor )
	{
		_zoomFactor = zoomFactor;
	}

	@Override
	public void setZoomFactor(double zoomFactor)
	{
		_zoomFactor._value = zoomFactor;
	}

	@Override
	public double getZoomFactor()
	{
		return( _zoomFactor._value );
	}

	@Override
	public DoubleReference getZoomFactorReference() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
    protected Component createPreviousButton() {
        _previousButton = createArrowButtonChild(SwingConstants.SOUTH);
        _previousButton.setName("Spinner.previousButton");
        installPreviousButtonListeners(_previousButton);
        return _previousButton;
    }

	@Override
    protected Component createNextButton() {
        _nextButton = createArrowButtonChild(SwingConstants.NORTH);
        _nextButton.setName("Spinner.nextButton");
        installNextButtonListeners(_nextButton);
        return _nextButton;
    }

    protected ZoomBasicArrowButton_ createArrowButtonChild(int direction) {
        ZoomBasicArrowButton_ b = new ZoomBasicArrowButton_(direction);
        Border buttonBorder = UIManager.getBorder("Spinner.arrowButtonBorder");
        if (buttonBorder instanceof UIResource) {
            // Wrap the border to avoid having the UIResource be replaced by
            // the ButtonUI. This is the opposite of using BorderUIResource.
            b.setBorder(new CompoundBorder(buttonBorder, null));
        } else {
            b.setBorder(buttonBorder);
        }
        b.setInheritsPopupMenu(true);
        return b;
    }

}
