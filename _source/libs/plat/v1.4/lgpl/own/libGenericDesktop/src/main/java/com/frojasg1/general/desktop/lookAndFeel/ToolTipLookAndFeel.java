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

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorClientInterface;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.general.desktop.classes.Classes;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.reflection.ReflectionFunctions;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 *
 * @author Usuario
 */
	// https://blogs.oracle.com/moonocean/entry/change_properties_of_java_tooltip
public class ToolTipLookAndFeel extends MetalLookAndFeel implements ChangeZoomFactorClientInterface
{
	protected static ToolTipLookAndFeel _instance = null;

	protected Map< Object, Integer > _mapOfOriginalFontSizes = new HashMap<>();

	protected ChangeZoomFactorServerInterface _zoomFactorServer = null;

	protected ToolTipMetalOceanTheme _theme = new ToolTipMetalOceanTheme();

	protected Map< FontUIResource, Map< Integer, FontUIResource > > _map = new HashMap<>();

	protected FontUIResource _initialToolTipFont = null;

	protected double _zoomFactor = 1.0D;

	protected BaseApplicationConfigurationInterface _appliConf;

	public static ToolTipLookAndFeel instance()
	{
		if( _instance == null )
			_instance = new ToolTipLookAndFeel();

		return( _instance );
	}

	protected ToolTipLookAndFeel()
	{
/*        AppContext context = AppContext.getAppContext();		// JRE 7, 8 ...
        context.remove( "currentMetalTheme" );
		context.put( "currentMetalTheme", _theme );
*/
		Class<?> appContextClass = Classes.getAppContextClass();
		Object context = ReflectionFunctions.instance().invokeClassMethod( "getAppContext", appContextClass, null );
		ReflectionFunctions.instance().invokeMethod( "remove", appContextClass, context, "currentMetalTheme" );
		ReflectionFunctions.instance().invokeMethod( "put", appContextClass, context, "currentMetalTheme", _theme );
	}

	public void setAppliConf(BaseApplicationConfigurationInterface appliConf)
	{
		_theme.setAppliConf(appliConf);
	}

	public ToolTipMetalOceanTheme getTheme()
	{
		return( _theme );
	}

	public FontUIResource getOriginalToolTipFont()
	{
		return( _initialToolTipFont );
	}

	@Override
	protected void initSystemColorDefaults(UIDefaults table)
	{        
		super.initSystemColorDefaults(table);        
		table.put("info", new ColorUIResource(255, 247, 200));    
	}

	@Override
	protected void initComponentDefaults(UIDefaults table) {
		super.initComponentDefaults(table);

		Border border = BorderFactory.createLineBorder(new Color(76,79,83));
		table.put("ToolTip.border", border);

		if( _initialToolTipFont == null )
			_initialToolTipFont = getDefaultFont( table.get( "ToolTip.font" ) );
/*		else
		{
			table.put( "ToolTip.font", getZoomedFont( _initialToolTipFont ) );
		}
*/
	}
/*
	public ZoomFontActiveValue switchFontActiveValue( Object fontActiveValue )
	{
		ZoomFontActiveValue result = null;

		if( fontActiveValue != null )
		{
			MetalTheme theme = ReflectionFunctions.instance().getAttribute( "theme",
																			MetalTheme.class,
																			fontActiveValue );
			Integer type = ReflectionFunctions.instance().getAttribute( "type",
																		Integer.class,
																		fontActiveValue );

			if( ( theme != null ) && ( type != null ) )
			{
				result = new ZoomFontActiveValue( theme, type );
			}
		}

		return( result );
	}
*/
	public void init()
	{
/*		UIDefaults table = this.getDefaults();

		List<Object> listOfFontActiveValueKeys = new ArrayList<>();
		Iterator< Map.Entry< Object, Object > > it = table.entrySet().iterator();
		while( it.hasNext() )
		{
			Map.Entry< Object, Object > entry = it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();

			if( value instanceof FontUIResource )
			{
				FontUIResource font = (FontUIResource) value;

				Integer fontSize = font.getSize();

				_mapOfOriginalFontSizes.put( key, fontSize );
			}
			else if( ( value != null ) &&
					value.getClass().getName().equals( "javax.swing.plaf.metal.MetalLookAndFeel$FontActiveValue" ) )
			{
				listOfFontActiveValueKeys.add( key );
//				table.remove(key);
//				table.put(key, switchFontActiveValue( value ) );
			}
		}

		Iterator< Object > it2 = listOfFontActiveValueKeys.iterator();
		while( it2.hasNext() )
		{
			Object key = it2.next();
			Object value = table.get( key );
			if( value != null )
			{
				table.remove(key);
				table.put(key, switchFontActiveValue( value ) );
			}
		}
*/
	}

	@Override
	public double getZoomFactor()
	{
		double result = 1.0D;

		if( _zoomFactorServer != null )
			result = _zoomFactorServer.getZoomFactor();

		return( result );
	}

	@Override
	public void changeZoomFactor(double zoomFactor)
	{
		_theme.setZoomFactor(zoomFactor);
		_zoomFactor = zoomFactor;
//		getDefaults();

/*
		UIDefaults table = this.getDefaults();

		Map< Object, Object > changeMap = new HashMap<>();

		Iterator< Map.Entry<Object, Integer> > it = _mapOfOriginalFontSizes.entrySet().iterator();
		while( it.hasNext() )
		{
			Map.Entry<Object, Integer> entry = it.next();
			Object key = entry.getKey();
			Integer fontSize = entry.getValue();

			Object obj = table.get( key );
			if( obj instanceof FontUIResource )
			{
				FontUIResource origFont = (FontUIResource) obj;

				Object newFont = changeMap.get( origFont );

				if( newFont == null )
				{
					int newSize = FontFunctions.instance().getZoomedFontSize( fontSize, zoomFactor );

					newFont = (FontUIResource) FontFunctions.instance().getResizedFont(origFont, newSize );
					changeMap.put( origFont, newFont );
				}

				table.remove( key );
				table.put( key, newFont );
			}
		}
*/
	}

	@Override
	public void changeZoomFactor_centerMousePointer(double zoomFactor) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void unregisterFromChangeZoomFactorAsObserver()
	{
		if( _zoomFactorServer != null )
			_zoomFactorServer.unregisterZoomFactorObserver(this);
	}

	@Override
	public void registerToChangeZoomFactorAsObserver(ChangeZoomFactorServerInterface conf)
	{
		unregisterFromChangeZoomFactorAsObserver();

		if( conf != null )
		{
			conf.registerZoomFactorObserver( this );
			changeZoomFactor( conf.getZoomFactor() );
		}
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

	protected FontUIResource getDefaultFont( Object original )
	{
		FontUIResource result = null;
		if( original instanceof FontUIResource )
		{
			result = (FontUIResource) original;
		}
		else if( original instanceof UIDefaults.ActiveValue )
		{
			UIDefaults.ActiveValue av = (UIDefaults.ActiveValue) original;

			Object fontObj = av.createValue(null);
			if( fontObj instanceof FontUIResource )
			{
				result = (FontUIResource) fontObj;
			}
		}

		return( result );
	}
	
	protected FontUIResource getZoomedFont( FontUIResource original )
	{
		FontUIResource result = null;

		if( original != null )
		{
			int newSize = FontFunctions.instance().getZoomedFontSize( original.getSize(), _zoomFactor );
			result = getResizedFont( original, newSize );
		}

		return( result );
	}

	/**
     * FontActiveValue redirects to the appropriate metal theme method.
	 * 
	 * from package javax.swing.plaf.metal;
	 * class: MetalLookAndFeel
	 * subclass: FontActiveValue
     */
/*    protected class ZoomFontActiveValue implements UIDefaults.ActiveValue {
		// Contants identifying the various Fonts that are Theme can support
		protected static final int CONTROL_TEXT_FONT = 0;
		protected static final int SYSTEM_TEXT_FONT = 1;
		protected static final int USER_TEXT_FONT = 2;
		protected static final int MENU_TEXT_FONT = 3;
		protected static final int WINDOW_TITLE_FONT = 4;
		protected static final int SUB_TEXT_FONT = 5;

		protected int type;
        protected MetalTheme theme;

        ZoomFontActiveValue(MetalTheme theme, int type) {
            this.theme = theme;
            this.type = type;
        }

		@Override
        public Object createValue(UIDefaults table) {
            Object value = null;
            switch (type) {
//            case MetalTheme.CONTROL_TEXT_FONT:
            case CONTROL_TEXT_FONT:
                value = theme.getControlTextFont();
                break;
//            case MetalTheme.SYSTEM_TEXT_FONT:
            case SYSTEM_TEXT_FONT:
                value = theme.getSystemTextFont();
                break;
//            case MetalTheme.USER_TEXT_FONT:
            case USER_TEXT_FONT:
                value = theme.getUserTextFont();
                break;
//            case MetalTheme.MENU_TEXT_FONT:
            case MENU_TEXT_FONT:
                value = theme.getMenuTextFont();
                break;
//            case MetalTheme.WINDOW_TITLE_FONT:
            case WINDOW_TITLE_FONT:
                value = theme.getWindowTitleFont();
                break;
//            case MetalTheme.SUB_TEXT_FONT:
            case SUB_TEXT_FONT:
                value = theme.getSubTextFont();
                break;
            }

			return value;
        }
    }
*/
}
