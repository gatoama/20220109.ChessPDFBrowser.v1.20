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
import com.frojasg1.general.desktop.view.zoom.mapper.CustomComponent;
import com.frojasg1.general.number.DoubleReference;
import java.awt.Graphics;
import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomJSlider extends JSlider implements CustomComponent,
															ColorThemeChangeableStatusBuilder
{
	protected ComponentForZoomOverriden _overridenMethods = null;
	protected DoubleReference _zoomFactor = new DoubleReference( 1.0D );

	protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

    public ZoomJSlider() {
		super();

		init();
	}

	public ZoomJSlider(int orientation) {
        super(orientation);

		init();
    }

    public ZoomJSlider(int min, int max) {
        super(min, max);

		init();
    }

    public ZoomJSlider(int min, int max, int value) {
        super(min, max, value);

		init();
    }

    public ZoomJSlider(int orientation, int min, int max, int value)
    {
        super(orientation, min, max, value);

		init();
    }

    public ZoomJSlider(BoundedRangeModel brm)
    {
		super( brm );

		init();
	}

	protected void init()
	{
		_colorThemeStatus = createColorThemeChangeableStatus();
		setOnChangeRepaint();
	}

	protected void setOnChangeRepaint()
	{
		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				repaint();
			}
		});
		
	}

	@Override
	public void initBeforeCopyingAttributes()
	{
		if( _overridenMethods == null )
			_overridenMethods = createOverridenMethodsObject();
	}

	protected ComponentForZoomOverriden createOverridenMethodsObject()
	{
		return( new ComponentForZoomOverriden( this, _zoomFactor ) );
	}
	
	@Override
	public void setUI( ComponentUI ui )
	{
		super.setUI( ui );
	}

	@Override
	public void switchToZoomUI()
	{
		_overridenMethods.switchToZoomUI();
	}

	@Override
	public void setZoomFactor( double zoomFactor )
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
	public void setZoomFactorReference(DoubleReference zoomFactor)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public DoubleReference getZoomFactorReference() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void initAfterCopyingAttributes()
	{
		
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

	public void setValuePreventingFromChange( int value )
	{
		getModel().setValue(value);
	}
}

