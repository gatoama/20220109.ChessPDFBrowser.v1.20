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
package com.frojasg1.general.desktop.lookAndFeel;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfiguration;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterListener;
import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterObserved;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.factory.impl.ColorInversorFactoryImpl;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.OceanTheme;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ToolTipMetalOceanTheme extends OceanTheme
{
	protected Map< FontUIResource, Map< Integer, FontUIResource > > _map = new HashMap<>();

	protected double _zoomFactor = 1.0D;

	protected boolean _isDarkMode = false;

	protected ColorInversor _colorInversor = null;

// This does not override getSecondary1 (102,102,102)

	protected BaseApplicationConfigurationInterface _appliConf;
	protected ConfigurationParameterListener _colorThemeChangeListener;

//	public ToolTipMetalOceanTheme(BaseApplicationConfigurationInterface appliConf)
	public ToolTipMetalOceanTheme()
	{
		_colorInversor = ColorInversorFactoryImpl.instance().createColorInversor();

//		setAppliConf( appliConf );
	}

	public String getName() {
        return "ToolTipMetalOceanTheme";
    }

	/**
     * Returns true if this is a theme provided by the core platform.
     */
    boolean isSystemTheme() {
//        return (getClass() == ToolTipMetalOceanTheme.class);
        return (false);
    }

    /**
     * Returns the system text font. This returns Dialog, 12pt, plain.
     *
     * @return the system text font
     */
    public FontUIResource getSystemTextFont() {
        return( getZoomedFont( super.getSystemTextFont() ) );
    }

	public void setZoomFactor( double zoomFactor )
	{
		_zoomFactor = zoomFactor;
	}

	protected FontUIResource getFromFontUIResourceMap( FontUIResource original, int newSize )
	{
		FontUIResource result = null;

		Map< Integer, FontUIResource > tmpMap = _map.get( original );

		if( tmpMap != null )
			result = tmpMap.get( newSize );

		return( result );
	}

	protected Map< Integer, FontUIResource > getOrPutFontUIResourceMap( FontUIResource original )
	{
		Map< Integer, FontUIResource > result = _map.get( original );

		if( result == null )
			result = new HashMap<>();

		return( result );
	}

	protected void putInFontUIResourceMapMap( FontUIResource original, FontUIResource result )
	{
		Map< Integer, FontUIResource > tmpMap = getOrPutFontUIResourceMap( original );

		tmpMap.put( result.getSize(), result );
	}

	protected FontUIResource getResizedFont( FontUIResource original, int newSize )
	{
		FontUIResource result = null;
			
		if( original != null )
		{
			result = getFromFontUIResourceMap( original, newSize );

			if( result == null )
			{
				result = (FontUIResource) FontFunctions.instance().getResizedFont(original, newSize);

				putInFontUIResourceMapMap( original, result );
			}
		}

		return( result );
	}

	protected FontUIResource getZoomedFont( FontUIResource original )
	{
		FontUIResource result = null;
		if( original != null )
		{
			int newSize = FontFunctions.instance().getZoomedFontSize( original.getSize(), getZoomFactor() );
			result = getResizedFont( original, newSize );
		}

		return( result );
	}

	protected double getZoomFactor()
	{
		return( _zoomFactor );
	}

	@Override
    public void addCustomEntriesToTable(UIDefaults table) {
		
		super.addCustomEntriesToTable(table);

        Object[] defaults = new Object[] {
            "ScrollBar.squareButtons", true };

        table.putDefaults(defaults);
	}
	public ColorUIResource getOriginalFocusColor()
	{
		return super.getFocusColor();
	}

	@Override
	public ColorUIResource getFocusColor()
	{
		return getColor( getOriginalFocusColor() );
	}


	public ColorUIResource getOriginalControl()
	{
		return super.getControl();
	}

	@Override
	public ColorUIResource getControl()
	{
		return getColor( getOriginalControl() );
	}


	public ColorUIResource getOriginalControlShadow()
	{
		return super.getControlShadow();
	}

	@Override
	public ColorUIResource getControlShadow()
	{
		return getColor( getOriginalControlShadow() );
	}


	public ColorUIResource getOriginalControlDarkShadow()
	{
		return super.getControlDarkShadow();
	}

	@Override
	public ColorUIResource getControlDarkShadow()
	{
		return getColor( getOriginalControlDarkShadow() );
	}


	public ColorUIResource getOriginalControlInfo()
	{
		return super.getControlInfo();
	}

	@Override
	public ColorUIResource getControlInfo()
	{
		return getColor( getOriginalControlInfo() );
	}


	public ColorUIResource getOriginalControlHighlight()
	{
		return super.getControlHighlight();
	}

	@Override
	public ColorUIResource getControlHighlight()
	{
		return getColor( getOriginalControlHighlight() );
	}


	public ColorUIResource getOriginalControlDisabled()
	{
		return super.getControlDisabled();
	}

	@Override
	public ColorUIResource getControlDisabled()
	{
		return getColor( getOriginalControlDisabled() );
	}


	public ColorUIResource getOriginalPrimaryControl()
	{
		return super.getPrimaryControl();
	}

	@Override
	public ColorUIResource getPrimaryControl()
	{
		return getColor( getOriginalPrimaryControl() );
	}


	public ColorUIResource getOriginalPrimaryControlShadow()
	{
		return super.getPrimaryControlShadow();
	}

	@Override
	public ColorUIResource getPrimaryControlShadow()
	{
		return getColor( getOriginalPrimaryControlShadow() );
	}


	public ColorUIResource getOriginalPrimaryControlDarkShadow()
	{
		return super.getPrimaryControlDarkShadow();
	}

	@Override
	public ColorUIResource getPrimaryControlDarkShadow()
	{
		return getColor( getOriginalPrimaryControlDarkShadow() );
	}


	public ColorUIResource getOriginalPrimaryControlInfo()
	{
		return super.getPrimaryControlInfo();
	}

	@Override
	public ColorUIResource getPrimaryControlInfo()
	{
		return getColor( getOriginalPrimaryControlInfo() );
	}


	public ColorUIResource getOriginalPrimaryControlHighlight()
	{
		return super.getPrimaryControlHighlight();
	}

	@Override
	public ColorUIResource getPrimaryControlHighlight()
	{
		return getColor( getOriginalPrimaryControlHighlight() );
	}


	public ColorUIResource getOriginalSystemTextColor()
	{
		return super.getSystemTextColor();
	}

	@Override
	public ColorUIResource getSystemTextColor()
	{
		return getColor( getOriginalSystemTextColor() );
	}


	public ColorUIResource getOriginalControlTextColor()
	{
		return super.getControlTextColor();
	}

	@Override
	public ColorUIResource getControlTextColor()
	{
		return getColor( getOriginalControlTextColor() );
	}


	public ColorUIResource getOriginalInactiveControlTextColor()
	{
		return super.getInactiveControlTextColor();
	}

	@Override
	public ColorUIResource getInactiveControlTextColor()
	{
		return getColor( getOriginalInactiveControlTextColor() );
	}


	public ColorUIResource getOriginalInactiveSystemTextColor()
	{
		return super.getInactiveSystemTextColor();
	}

	@Override
	public ColorUIResource getInactiveSystemTextColor()
	{
		return getColor( getOriginalInactiveSystemTextColor() );
	}


	public ColorUIResource getOriginalUserTextColor()
	{
		return super.getUserTextColor();
	}

	@Override
	public ColorUIResource getUserTextColor()
	{
		return getColor( getOriginalUserTextColor() );
	}


	public ColorUIResource getOriginalTextHighlightColor()
	{
		return super.getTextHighlightColor();
	}

	@Override
	public ColorUIResource getTextHighlightColor()
	{
		return getColor( getOriginalTextHighlightColor() );
	}


	public ColorUIResource getOriginalHighlightedTextColor()
	{
		return super.getHighlightedTextColor();
	}

	@Override
	public ColorUIResource getHighlightedTextColor()
	{
		return getColor( getOriginalHighlightedTextColor() );
	}


	public ColorUIResource getOriginalWindowBackground()
	{
		return super.getWindowBackground();
	}

	@Override
	public ColorUIResource getWindowBackground()
	{
		return getColor( getOriginalWindowBackground() );
	}


	public ColorUIResource getOriginalWindowTitleBackground()
	{
		return super.getWindowTitleBackground();
	}

	@Override
	public ColorUIResource getWindowTitleBackground()
	{
		return getColor( getOriginalWindowTitleBackground() );
	}


	public ColorUIResource getOriginalWindowTitleForeground()
	{
		return super.getWindowTitleForeground();
	}

	@Override
	public ColorUIResource getWindowTitleForeground()
	{
		return getColor( getOriginalWindowTitleForeground() );
	}


	public ColorUIResource getOriginalWindowTitleInactiveBackground()
	{
		return super.getWindowTitleInactiveBackground();
	}

	@Override
	public ColorUIResource getWindowTitleInactiveBackground()
	{
		return getColor( getOriginalWindowTitleInactiveBackground() );
	}


	public ColorUIResource getOriginalWindowTitleInactiveForeground()
	{
		return super.getWindowTitleInactiveForeground();
	}

	@Override
	public ColorUIResource getWindowTitleInactiveForeground()
	{
		return getColor( getOriginalWindowTitleInactiveForeground() );
	}


	public ColorUIResource getOriginalMenuBackground()
	{
		return super.getMenuBackground();
	}

	@Override
	public ColorUIResource getMenuBackground()
	{
		return getColor( getOriginalMenuBackground() );
	}


	public ColorUIResource getOriginalMenuForeground()
	{
		return super.getMenuForeground();
	}

	@Override
	public ColorUIResource getMenuForeground()
	{
		return getColor( getOriginalMenuForeground() );
	}


	public ColorUIResource getOriginalMenuSelectedBackground()
	{
		return super.getMenuSelectedBackground();
	}

	@Override
	public ColorUIResource getMenuSelectedBackground()
	{
		return getColor( getOriginalMenuSelectedBackground() );
	}


	public ColorUIResource getOriginalMenuSelectedForeground()
	{
		return super.getMenuSelectedForeground();
	}

	@Override
	public ColorUIResource getMenuSelectedForeground()
	{
		return getColor( getOriginalMenuSelectedForeground() );
	}


	public ColorUIResource getOriginalMenuDisabledForeground()
	{
		return super.getMenuDisabledForeground();
	}

	@Override
	public ColorUIResource getMenuDisabledForeground()
	{
		return getColor( getOriginalMenuDisabledForeground() );
	}


	public ColorUIResource getOriginalSeparatorBackground()
	{
		return super.getSeparatorBackground();
	}

	@Override
	public ColorUIResource getSeparatorBackground()
	{
		return getColor( getOriginalSeparatorBackground() );
	}


	public ColorUIResource getOriginalSeparatorForeground()
	{
		return super.getSeparatorForeground();
	}

	@Override
	public ColorUIResource getSeparatorForeground()
	{
		return getColor( getOriginalSeparatorForeground() );
	}


	public ColorUIResource getOriginalAcceleratorForeground()
	{
		return super.getAcceleratorForeground();
	}

	@Override
	public ColorUIResource getAcceleratorForeground()
	{
		return getColor( getOriginalAcceleratorForeground() );
	}


	public ColorUIResource getOriginalAcceleratorSelectedForeground()
	{
		return super.getAcceleratorSelectedForeground();
	}

	@Override
	public ColorUIResource getAcceleratorSelectedForeground()
	{
		return getColor( getOriginalAcceleratorSelectedForeground() );
	}

    public ColorUIResource getOriginalDesktopColor() {
        return( super.getDesktopColor() );
    }

	@Override
    public ColorUIResource getDesktopColor() {
        return( getColor( getOriginalDesktopColor() ) );
    }

	protected ColorUIResource getColor( ColorUIResource color )
	{
		ColorUIResource result = null;
		if( color != null )
		{
			if( isDarkMode() )
				result = new ColorUIResource( getColorInversor().invertColor( color ) );
			else
				result = color;
		}

		return( result );
	}

	public void setDarkMode( boolean value )
	{
		_isDarkMode = value;
	}

	protected boolean isDarkMode()
	{
		return( _isDarkMode );
	}

	protected ColorInversor getColorInversor()
	{
		return( _colorInversor );
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	public void setAppliConf(BaseApplicationConfigurationInterface appliConf)
	{
		registerToChangeColorThemeAsObserver( appliConf );
		_appliConf = appliConf;
	}

	protected void unregisterFromChangeColorThemeAsObserver()
	{
		if( ( _colorThemeChangeListener != null ) && ( getAppliConf() != null ) )
		{
			getAppliConf().removeConfigurationParameterListener(BaseApplicationConfiguration.CONF_IS_DARK_MODE_ACTIVATED,
																_colorThemeChangeListener);
			_colorThemeChangeListener = null;
		}
	}

	public void registerToChangeColorThemeAsObserver(BaseApplicationConfigurationInterface conf)
	{
		unregisterFromChangeColorThemeAsObserver();

		if( conf != null )
		{
			_colorThemeChangeListener = this::configurationParameterColorThemeChanged;
			conf.addConfigurationParameterListener(BaseApplicationConfiguration.CONF_IS_DARK_MODE_ACTIVATED,
													_colorThemeChangeListener);
		}
	}

	protected void configurationParameterColorThemeChanged( ConfigurationParameterObserved observed, String label,
															Object oldValue, Object newValue )
	{
		_isDarkMode = (Boolean) newValue;
//		_isDarkMode = false;
	}
}
