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

import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableForCustomComponent;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableForCustomComponentUI;
import com.frojasg1.general.desktop.view.zoom.ZoomComponentInterface;
import com.frojasg1.general.desktop.view.zoom.components.ZoomMetalScrollButton_forScrollBar;

import java.awt.*;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.metal.MetalScrollBarUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomMetalScrollBarUI extends MetalScrollBarUI
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ZoomMetalScrollBarUI.class);

	protected static final int SHADOW_COLOR_INDEX = 0;
	protected static final int HIGHLIGHT_COLOR_INDEX = 1;
	protected static final int DARK_SHADOW_COLOR_INDEX = 2;
	protected static final int THUMB_COLOR_INDEX = 3;
	protected static final int THUMB_SHADOW_INDEX = 4;
	protected static final int THUMB_HIGHLIGHT_INDEX = 5;
/*
	protected static ColorData[] ORIGINAL_COLORS = new ColorData[] {
				new ColorData( "shadowColor" ),
				new ColorData( "highlightColor" ),
				new ColorData( "darkShadowColor" ),
				new ColorData( "thumbColor" ),
				new ColorData( "thumbShadow" ),
				new ColorData( "thumbHighlightColor" )
	};
*/

	protected ColorThemeChangeableForCustomComponent _colorThemeStatus;

//	protected boolean _colorsAreInverted = false;

	protected JScrollBar _scrollBar;

	protected boolean _isDarkMode = false;
	protected boolean _latestModeWasDark = false;

	public ZoomMetalScrollBarUI(boolean isDarkMode, boolean latestModeWasDark,
								JScrollBar scrollBar)
	{
		_scrollBar = scrollBar;
		createColorThemeChangeableStatus().setDarkMode(isDarkMode, getColorInversor() );

		_isDarkMode = isDarkMode;
		_latestModeWasDark = latestModeWasDark;

		if( isDarkMode != latestModeWasDark )
			invertSingleScrollBarColors( getColorInversor() );

//		_colorsAreInverted = getInitialColorsAreInverted();
//		fillInOriginalColors();
	}

	@Override
	public void installUI(JComponent c)
	{
		super.installUI(c);
	}

	protected void invertSingleScrollBarColors(ColorInversor colorInversor)
	{
//		ColorInversor colorInversor = getColorInversor();
		colorInversor.invertSingleColorsGen( _scrollBar );
//		colorInversor.invertSingleColorsGen( _scrollPane.getVerticalScrollBar() );
	}

	protected boolean getInitialColorsAreInverted()
	{
		return( FrameworkComponentFunctions.instance().isDarkModeActivated(_scrollBar) );
	}
/*
	public void setScrollBar( JScrollPane scrollPane )
	{
		_scrollPane = scrollPane;
//		if( areColorsInverted() )
//			invertSingleScrollBarsColors();
		_colorThemeStatus = createColorThemeChangeableStatus();
	}

	protected void fillInOriginalColors()
	{
		for( ColorData cd: ORIGINAL_COLORS )
		{
			if( cd._color != null )
				break;
			else
				cd._color = getColor( cd._attribName );
		}
	}
*/
	protected ZoomMetalScrollButton_forScrollBar createScrollButton( int orientation )
	{
        ZoomMetalScrollButton_forScrollBar result = new ZoomMetalScrollButton_forScrollBar( orientation, scrollBarWidth, isFreeStanding );
		result.initBeforeCopyingAttributes();
		if( getInitialColorsAreInverted() )
		{
			ColorInversor colorInversor = getColorInversor();
			result.createColorThemeChangeableStatus().setDarkMode(getInitialColorsAreInverted(), colorInversor);
		}

		return( result );
	}

	/** Returns the view that represents the decrease view.
      */
    protected JButton createDecreaseButton( int orientation )
    {
		ZoomMetalScrollButton_forScrollBar db = createScrollButton( orientation );

		decreaseButton = db;

		return decreaseButton;
    }

	/** Returns the view that represents the increase view. */
    protected JButton createIncreaseButton( int orientation )
    {
		ZoomMetalScrollButton_forScrollBar ib = createScrollButton( orientation );

		increaseButton =  ib;

		return increaseButton;
    }

	protected void initAfterCopyingAttributes( Component comp )
	{
		if( comp instanceof ZoomComponentInterface )
		{
			ZoomComponentInterface zci = (ZoomComponentInterface) comp;
			zci.initAfterCopyingAttributes();
		}
	}

	public void initAfterCopyingAttributes()
	{
		initAfterCopyingAttributes( incrButton );
		initAfterCopyingAttributes( decrButton );
	}

	protected ColorInversor getColorInversor()
	{
		return( FrameworkComponentFunctions.instance().getColorInversor( _scrollBar ) );
	}
/*
	@Override
    protected void configureScrollBarColors()
    {
		super.configureScrollBarColors();

		if( areColorsInverted() )
		{
			_colorsAreInverted = false;
			invertColors( getColorInversor() );
		}
	}
*/
	@Override
    public void paint(Graphics g, JComponent c) {
		if( _colorThemeStatus != null )
			_colorThemeStatus.paint(g);
		else
			super.paint( g, c );
	}

	protected ColorThemeChangeableForCustomComponent createColorThemeChangeableStatus()
	{
		if( _colorThemeStatus == null )
			_colorThemeStatus = new ColorThemeChangeableForCustomComponent( _scrollBar,
				(grp) ->	super.paint(grp, _scrollBar), false);
		return( _colorThemeStatus );
	}

	public void invertColors(ColorInversor colorInversor)
	{
//		invertSingleScrollBarColors(colorInversor);
		createColorThemeChangeableStatus().setDarkMode( !_colorThemeStatus.isDarkMode(), colorInversor);
	}

/*
	public void invertColors( ColorInversor colorInversor )
	{
		invertColorsAreInverted();

		try
		{
			invertColorAttributes(colorInversor);
		}
		catch( Exception ex )
		{
			LOGGER.error( "Error inverting colors of scrollbar. May be reflection of private attributes is not allowed" );
		}
		invertColorsAreInverted();
	}

	public void invertColorsAreInverted()
	{
		_colorsAreInverted = !_colorsAreInverted;
	}

	public boolean areColorsInverted()
	{
		return( _colorsAreInverted );
	}

	protected void invertColorAttributes(ColorInversor colorInversor)
	{
		for( ColorData cd: ORIGINAL_COLORS )
		{
			Color color = getColor( cd._attribName );
			if( Objects.equals( color, cd._color ) == areColorsInverted() )
				break;
			Color invertedColor = colorInversor.invertColor(color);
			setColor( cd._attribName, invertedColor );
		}

//        thumbHighlightColor = colorInversor.invertColor( thumbHighlightColor );
        thumbLightShadowColor = colorInversor.invertColor( thumbLightShadowColor );
        thumbDarkShadowColor = colorInversor.invertColor( thumbDarkShadowColor );
//        thumbColor = colorInversor.invertColor( thumbColor );
        trackColor = colorInversor.invertColor( trackColor );
        trackHighlightColor = colorInversor.invertColor( trackHighlightColor );

		invertColorsMetalScrollButton( colorInversor, increaseButton );
		invertColorsMetalScrollButton( colorInversor, decreaseButton );
	}

	protected void invertColorsMetalScrollButton( ColorInversor colorInversor,
													MetalScrollButton button )
	{
		ZoomMetalScrollButton_forScrollBar zoomButton = (ZoomMetalScrollButton_forScrollBar) button;
		zoomButton.createColorThemeChangeableStatus().setDarkMode( areColorsInverted(), colorInversor);
	}

	public void installUI(JComponent c)   {
		super.installUI(c);
	}

	protected Color getColor( String attribName )
	{
		return( ReflectionFunctions.instance().getStaticAttribute(attribName, Color.class, MetalScrollBarUI.class ) );
	}

	protected void setColor( String attribName, Color color )
	{
		ReflectionFunctions.instance().setStaticAttribute(attribName, MetalScrollBarUI.class, color);
	}

	protected static class ColorData
	{
		protected String _attribName;
		protected Color _color;
		
		public ColorData( String attribName )
		{
			_attribName = attribName;
		}
	}
*/
}
