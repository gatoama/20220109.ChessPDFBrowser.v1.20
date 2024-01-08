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
package com.frojasg1.general.desktop.view.color.components;

import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatusBuilder;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableForCustomComponent;
import com.frojasg1.general.number.DoubleReference;
import com.frojasg1.general.zoom.ZoomInterface;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ColorInversorJEditorPane  extends JEditorPane implements ZoomInterface,
															ColorThemeInvertible,
															ColorThemeChangeableStatusBuilder
{
	protected DoubleReference _zoomFactor = new DoubleReference( 1.0D );

	protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

	public ColorInversorJEditorPane()
	{
		super();
		init();
	}

    public ColorInversorJEditorPane(URL initialPage) throws IOException {
        super(initialPage);
		init();
    }

	public ColorInversorJEditorPane(String url) throws IOException {
        super(url);
		init();
    }

	public ColorInversorJEditorPane(String type, String text) {
        super(type, text);
		init();
    }

	protected void init()
	{
		_colorThemeStatus = createColorThemeChangeableStatus();
	}

	@Override
	public void setZoomFactorReference(DoubleReference zoomFactor)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setZoomFactor(double zoomFactor)
	{
		_zoomFactor._value = zoomFactor;

		repaint();
	}

	@Override
	public double getZoomFactor()
	{
		return( _zoomFactor._value );
	}

	@Override
	public DoubleReference getZoomFactorReference()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setPreferredSize( Dimension dimension )
	{
		super.setPreferredSize( dimension );
	}

	@Override
	public Dimension getPreferredSize()
	{
		return( super.getPreferredSize() );
	}

	@Override
	public void setBounds( int xx, int yy, int width, int height )
	{
		super.setBounds( xx, yy, width, height );
	}

	@Override
	public Dimension getMaximumSize()
	{
		return( super.getMaximumSize() );
	}

	@Override
	public void paint( Graphics grp )
	{
		_colorThemeStatus.paint(grp);
	}

	protected void superPaint( Graphics grp )
	{
		super.paint( grp );
	}

	@Override
	public ColorThemeChangeableForCustomComponent createColorThemeChangeableStatus()
	{
		if( _colorThemeStatus == null )
			_colorThemeStatus = new ColorThemeChangeableForCustomComponent( this, grp -> super.paint(grp) , false);
		return( _colorThemeStatus );
	}

	@Override
	public void invertColors(ColorInversor colorInversor)
	{
		// Intentionally left blank
	}
}
