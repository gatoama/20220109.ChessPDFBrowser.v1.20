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

import com.frojasg1.general.zoom.ZoomInterface;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.zoom.ZoomIcon;
import com.frojasg1.general.number.DoubleReference;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JScrollBar;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomIconImp implements ZoomIcon {
/*
	protected static final int FLOOR = 0;
	protected static final int CENTER = 1;
	protected static final int CEILING = 2;

	protected static final int HORIZONTAL_TEXT_POSITION_TRAILING = FLOOR;
	protected static final int HORIZONTAL_TEXT_POSITION_CENTER = CENTER;
	protected static final int HORIZONTAL_TEXT_POSITION_LEADING = CEILING;

	protected static final int HORIZONTAL_TEXT_POSITION_TRAILING = FLOOR;
	protected static final int HORIZONTAL_TEXT_POSITION_CENTER = CENTER;
	protected static final int HORIZONTAL_TEXT_POSITION_LEADING = CEILING;
*/
	protected DoubleReference _zoomFactor = null;

	protected Icon _originalIcon = null;
	protected double _additionalFactor = 1.0D;
	protected boolean _centerSmaller = false;

	protected boolean _canInvertColors = true;
	protected boolean _colorsAreInverted = false;

	public ZoomIconImp( Icon originalIcon )
	{
		_originalIcon = originalIcon;
		_zoomFactor = new DoubleReference( 1.0D );
	}

	public ZoomIconImp( Icon originalIcon, DoubleReference zoomFactor )
	{
		this( originalIcon, zoomFactor, false );
	}

	public ZoomIconImp( Icon originalIcon, DoubleReference zoomFactor,
						boolean centerSmaller )
	{
		_originalIcon = originalIcon;
		_zoomFactor = zoomFactor;
		_centerSmaller = centerSmaller;
	}

	public void setAdditionalFactor( double additionalFactor )
	{
		_additionalFactor = additionalFactor;
	}

	public double getAdditionalFactor()
	{
		return( _additionalFactor );
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
//		if( _zoomFactor._value == 1.0D )
		{
//			_originalIcon.paintIcon( c, g, x, y );
		}
//		else
		{
			int zoomedWidth = getZoomedIconWidth();
			int zoomedHeight = getZoomedIconHeight();
		
			int xxIncrement = 0;
			int yyIncrement = 0;

			int newXXcoordinate;
			int newYYcoordinate;
			if( _centerSmaller )
			{
				if( hasToCenterSmaller() )
				{
					xxIncrement = ( _originalIcon.getIconWidth() - zoomedWidth ) / 2;
					yyIncrement = ( _originalIcon.getIconHeight() - zoomedHeight ) / 2;
				}

				newXXcoordinate = IntegerFunctions.roundToInt( _additionalFactor * _zoomFactor._value * x ) + xxIncrement;
				newYYcoordinate = IntegerFunctions.roundToInt( _additionalFactor * _zoomFactor._value * y ) + yyIncrement;
			}
			else
			{
//				newXXcoordinate = calculateNormalNewCoordinate( x, getHorizontalPositionOfIcon(), getReferenceXX() );
//				newYYcoordinate = calculateNormalNewCoordinate( y, getVerticalPositionOfIcon(), getReferenceYY() );
				newXXcoordinate = calculateXXNewCoordinate( x );
				newYYcoordinate = calculateYYNewCoordinate( y );
			}

//			g.translate( IntegerFunctions.roundToInt( _additionalFactor * _zoomFactor._value * x ),
//							IntegerFunctions.roundToInt( _additionalFactor * _zoomFactor._value * y ) );

			BufferedImage bi = new BufferedImage( _originalIcon.getIconWidth(),
													_originalIcon.getIconHeight(),
													BufferedImage.TYPE_INT_ARGB );

			Graphics g2 = bi.getGraphics();
			_originalIcon.paintIcon(c, g2, 0, 0);

			BufferedImage zoomedIcon = ImageFunctions.instance().resizeImageAccurately(bi, zoomedWidth,
															zoomedHeight );

			g.drawImage( zoomedIcon,
								newXXcoordinate,
								newYYcoordinate,
								newXXcoordinate + zoomedWidth,
								newYYcoordinate + zoomedHeight,
								0,
								0,
								zoomedWidth,
								zoomedHeight,
								null );

//          g.translate( -IntegerFunctions.roundToInt( _additionalFactor * _zoomFactor._value * x ),
//							-IntegerFunctions.roundToInt( _additionalFactor * _zoomFactor._value * y ) );

		}
	}

	protected int calculateXXNewCoordinate( int xx )
	{
		return( xx );
	}
	
	protected int calculateYYNewCoordinate( int yy )
	{
		return( yy );
	}
	
	protected int getZoomedIconWidth()
	{
		int result = _originalIcon.getIconWidth();
		result = IntegerFunctions.roundToInt( _additionalFactor * _zoomFactor._value * result );

		return( result );
	}

	protected boolean hasToCenterSmaller()
	{
		return(  _centerSmaller && ( _additionalFactor * _zoomFactor._value < 1.0D ) );
	}

	@Override
	public int getIconWidth()
	{
		int result = 0;

		if( hasToCenterSmaller() )
			result = _originalIcon.getIconWidth();
		else
			result = getZoomedIconWidth();

		return( result );
	}

	public int getZoomedIconHeight()
	{
		int result = _originalIcon.getIconHeight();
		result = IntegerFunctions.roundToInt( _additionalFactor * _zoomFactor._value * result );

		return( result );
	}

	@Override
	public int getIconHeight()
	{
		int result = 0;

		if( hasToCenterSmaller() )
			result = _originalIcon.getIconHeight();
		else
			result = getZoomedIconHeight();

		return( result );
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
		return( _zoomFactor );
	}

	public Icon getOriginalIcon()
	{
		return( _originalIcon );
	}

	public void setOriginalIcon( Icon originalIcon )
	{
		_originalIcon = originalIcon;
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
		_originalIcon = original;
		_colorsAreInverted = !_colorsAreInverted;
	}
}
