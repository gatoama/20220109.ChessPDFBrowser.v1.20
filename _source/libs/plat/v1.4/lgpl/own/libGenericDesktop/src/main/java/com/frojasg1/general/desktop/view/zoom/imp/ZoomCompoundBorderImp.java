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
package com.frojasg1.general.desktop.view.zoom.imp;

import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.zoom.ZoomBorder;
import com.frojasg1.general.number.DoubleReference;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.JScrollBar;
import javax.swing.border.CompoundBorder;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomCompoundBorderImp extends CompoundBorder implements ZoomBorder {
	protected DoubleReference _zoomFactor = null;

	protected Insets _originalInsets = null;
	protected boolean _centerSmaller = false;

	public ZoomCompoundBorderImp( Insets originalInsets, CompoundBorder originalBorder )
	{
		this( originalInsets, originalBorder, new DoubleReference( 1.0D ) );
	}

	public ZoomCompoundBorderImp( Insets originalInsets, CompoundBorder originalBorder, DoubleReference zoomFactor )
	{
		super( originalBorder.getInsideBorder(), originalBorder.getOutsideBorder() );
		_zoomFactor = zoomFactor;
		_originalInsets = originalInsets;
	}

	public Insets getBorderInsets(Component c)
	{
		return( getBorderInsets(c, null) );
	}

	public Insets getBorderInsets(Component c, Insets insets )
	{
		return( ViewFunctions.instance().getNewInsets(_originalInsets, _zoomFactor._value ) );
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
	public void setZoomFactorReference( DoubleReference zoomFactor )
	{
		_zoomFactor = zoomFactor;
	}

	@Override
	public DoubleReference getZoomFactorReference()
	{
		JScrollBar sb;
		return( _zoomFactor );
	}

	public Insets getOriginalInsets()
	{
		return( _originalInsets );
	}
}
