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

import com.frojasg1.general.number.DoubleReference;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalRadioButtonUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomMetalRadioButtonUI extends MetalRadioButtonUI implements MetalComponentWithIconUIforZoomInterface
{
	protected MetalComponentWithIconUIforZoomOverriden _overridenMethods = null;
	protected DoubleReference _zoomFactor = null;

	public ZoomMetalRadioButtonUI()
	{
		super();
	}

    public static ComponentUI createUI( JComponent x ) {
        return new ZoomMetalRadioButtonUI();
    }

	@Override
	public void init()
	{
		_overridenMethods = createOverridenMethodsObject();
	}

	protected MetalComponentWithIconUIforZoomOverriden createOverridenMethodsObject()
	{
		return( new MetalComponentWithIconUIforZoomOverriden( this, _zoomFactor ) );
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
	public Icon getDefaultIcon()
	{
		return( _overridenMethods.getDefaultIcon() );
	}

	@Override
	public Icon superGetDefaultIcon()
	{
		return( super.getDefaultIcon() );
	}

	@Override
	public DoubleReference getZoomFactorReference() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
