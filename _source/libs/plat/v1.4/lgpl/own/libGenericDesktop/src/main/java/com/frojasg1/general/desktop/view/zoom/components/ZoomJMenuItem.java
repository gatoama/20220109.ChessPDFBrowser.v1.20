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
package com.frojasg1.general.desktop.view.zoom.components;

import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatusBuilder;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableForCustomComponent;
import com.frojasg1.general.desktop.view.zoom.ZoomFunctions;
import com.frojasg1.general.desktop.view.zoom.mapper.CustomComponent;
import com.frojasg1.general.number.DoubleReference;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Action;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomJMenuItem  extends JMenuItem implements CustomComponent,
															ColorThemeChangeableStatusBuilder
{
	protected DoubleReference _zoomFactor = new DoubleReference( 1.0D );

	protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

	public ZoomJMenuItem()
	{
		super();
		init();
	}

    public ZoomJMenuItem(Icon icon) {
        super(icon);
		init();
    }

	public ZoomJMenuItem(String text)
	{
		super(text);
		init();
	}

	public ZoomJMenuItem(Action a)
	{
		super(a);
		init();
	}

	public ZoomJMenuItem(String text, Icon icon)
	{
		super(text, icon);
		init();
	}

	public ZoomJMenuItem(String text, int mnemonic) {
        super(text, mnemonic);
		init();
    }

	protected void init()
	{
		_colorThemeStatus = createColorThemeChangeableStatus();
	}

	@Override
	public void setUI( ComponentUI ui )
	{
		super.setUI( ui );
	}

	@Override
	public void initBeforeCopyingAttributes()
	{
	}

	@Override
	public void initAfterCopyingAttributes()
	{
	}

	@Override
	public void switchToZoomUI()
	{
//		ZoomFunctions.instance().switchToZoomUI(this, _zoomFactor);
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
