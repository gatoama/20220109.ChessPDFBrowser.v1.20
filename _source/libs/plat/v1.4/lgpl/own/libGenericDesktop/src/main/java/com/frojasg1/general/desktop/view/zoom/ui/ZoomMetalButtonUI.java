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

import com.frojasg1.general.desktop.view.zoom.helpers.ComponentSimpleColorInversor;
import com.frojasg1.general.number.DoubleReference;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalButtonUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomMetalButtonUI extends MetalButtonUI implements ComponentUIforZoomInterface
{
	protected JComponent _parent;
	protected DoubleReference _zoomFactor = null;
	protected ComponentSimpleColorInversor _helper = null;

	public ZoomMetalButtonUI()
	{
		this( ComponentSimpleColorInversor.instance() );
	}

	public ZoomMetalButtonUI(ComponentSimpleColorInversor helper)
	{
		super();
		_helper = helper;
	}

    public static ComponentUI createUI( JComponent x ) {
        return new ZoomMetalButtonUI();
    }

	public void installUI(JComponent c) {
		_parent = c;

		super.installUI(c);
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
	public void paint( Graphics grp, JComponent comp )
	{
		super.paint( grp, comp );
	}

    protected Color invertColorIfNecessary( Color color ) {
		return( _helper.invertColorIfNecessary( _parent, color ) );
	}

	// https://stackoverflow.com/questions/5808022/changing-the-background-color-of-a-selected-jtogglebutton
	@Override
    protected Color getSelectColor() {
		return( invertColorIfNecessary( super.getSelectColor() ) );
    }

	@Override
    protected Color getDisabledTextColor() {
		return( invertColorIfNecessary( super.getDisabledTextColor() ) );
    }

	@Override
    protected Color getFocusColor() {
		return( invertColorIfNecessary( super.getFocusColor() ) );
    }
}
