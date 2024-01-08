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

import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.lookAndFeel.ToolTipLookAndFeel;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.zoom.ZoomIcon;
import com.frojasg1.general.number.DoubleReference;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalComboBoxButton;
import javax.swing.plaf.metal.MetalComboBoxIcon;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomMetalComboBoxIcon extends MetalComboBoxIcon implements ZoomIcon {

	protected double _zoomFactor = 1.0D;

	protected MetalComboBoxButton _parent = null;
	protected int _originalComboBoxHeight = -1;

	protected boolean _canInvertColors = true;
	protected boolean _colorsAreInverted = false;

	public ZoomMetalComboBoxIcon( ) //MetalComboBoxButton parent )
	{
//		_originalComboBoxHeight = parent.getComboBox().getPreferredSize().height;
//		_parent = parent;
	}

	public void setParentButton( MetalComboBoxButton parent )
	{
		_parent = parent;
	}

	@Override
    public void paintIcon(Component c, Graphics g, int x, int y){
        JComponent component = (JComponent)c;
        int iconWidth = getIconWidth();

        g.translate( x, y );

        g.setColor( invertColorIfNecessary( component.isEnabled()
											? ToolTipLookAndFeel.instance().getTheme().getOriginalControlInfo()
											: ToolTipLookAndFeel.instance().getTheme().getOriginalControlShadow())
//											? MetalLookAndFeel.getControlInfo()
//											: MetalLookAndFeel.getControlShadow())
		);
        g.fillPolygon(new int[]{0, iconWidth/2, iconWidth},
                      new int[]{0, iconWidth/2 , 0}, 3);
        g.translate( -x, -y );
    }

	protected Color invertColorIfNecessary( Color original )
	{
		Color result = original;
		if( _colorsAreInverted )
		{
			Color invertedColor = NullFunctions.instance().getIfNotNull( FrameworkComponentFunctions.instance().getColorInversor(_parent),
				ci -> ci.invertColor( original ) );
			if( invertedColor != null )
				result = invertedColor;
		}
		return result;
	}

	@Override
	public int getIconWidth()
	{
//		int total = IntegerFunctions.zoomValueFloor(_originalComboBoxHeight, _zoomFactor);
		int total = _parent.getComboBox().getSize().height;

		Insets insets = _parent.getInsets();

		int result = total;

		if( insets != null )
		{
			result = result - insets.left - insets.right;
		}

		return( result );
	}


	@Override
	public int getIconHeight()
	{
		return( getIconWidth() / 2 );
	}

	@Override
	public void setZoomFactorReference(DoubleReference zoomFactor) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setZoomFactor(double zoomFactor) {
		_zoomFactor = zoomFactor;
	}

	@Override
	public double getZoomFactor() {
		return( _zoomFactor );
	}

	@Override
	public DoubleReference getZoomFactorReference() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean canInvertColors() {
		return( _canInvertColors );
	}

	@Override
	public void setCanInvertColors(boolean value) {
		_canInvertColors = value;
	}

	@Override
	public boolean areColorsInverted() {
		return( _colorsAreInverted );
	}

	@Override
	public void setIconWithInvertedColors(Icon original) {
		_colorsAreInverted = !_colorsAreInverted;
	}
	
}
