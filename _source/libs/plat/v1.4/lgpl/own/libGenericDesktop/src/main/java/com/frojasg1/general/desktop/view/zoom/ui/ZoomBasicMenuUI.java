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

import com.frojasg1.general.desktop.view.zoom.imp.ZoomIconImp;
import com.frojasg1.general.number.DoubleReference;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomBasicMenuUI extends BasicMenuUI implements ComponentUIforZoomInterface
{
	protected DoubleReference _zoomFactor = null;

	public ZoomBasicMenuUI()
	{
		super();
	}

    public static ComponentUI createUI( JComponent x ) {
        return new ZoomBasicMenuUI();
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
    protected void installDefaults() {
		super.installDefaults();

		arrowIcon = createZoomIcon( arrowIcon );
		checkIcon = createZoomIcon( checkIcon );
	}

	protected ZoomIconImp createZoomIcon( Icon icon )
	{
		ZoomIconImp result = null;
		if( icon != null )
		{
			result = new ZoomIconImp( icon, _zoomFactor );
//			result.setAdditionalFactor( 1.33D );
		}
		return( result );
	}

}
