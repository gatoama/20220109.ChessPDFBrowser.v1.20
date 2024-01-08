/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color.uimanagers.impl;

import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.uimanagers.UIManagerParamColorsForInversionBase;
import java.awt.Color;
import java.util.Map;
import javax.swing.plaf.ButtonUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UIManagerParamColorsForInversionImpl extends UIManagerParamColorsForInversionBase
{
	protected static UIManagerParamColorsForInversionImpl _instance;

//	protected List _originalButtonGradient = null;

	public static UIManagerParamColorsForInversionImpl instance()
	{
		if( _instance == null )
			_instance = createInstance();

		return( _instance );
	}

	private synchronized static UIManagerParamColorsForInversionImpl createInstance()
	{
		if( _instance == null )
			_instance = new UIManagerParamColorsForInversionImpl();

		return( _instance );
	}

	private UIManagerParamColorsForInversionImpl()
	{
		init();
	}

	protected void init()
	{
		super.init();

		addKey( "SplitPane.dividerFocusColor" );
		addKey( "SplitPaneDivider.draggingColor" );
//		addKey( "ProgressBar.background" );
//		addKey( "ProgressBar.foreground" );
		addKey( "ProgressBar.selectionBackground" );
		addKey( "ProgressBar.selectionForeground" );
		addKey( "TabbedPane.selected" );
//		addKey( "ToggleButton.select" );
		addKey("ComboBox.selectionForeground");
		addKey("ComboBox.selectionBackground");
		addKey("ComboBox.disabledBackground");
		addKey("ComboBox.disabledForeground");
//		addKey("Button.select");
	}

	protected synchronized Map<String, Color> getOrCreateOriginalMap()
	{
//		if( _originalButtonGradient == null )
//			_originalButtonGradient = ((java.util.List)UIManager.get("Button.gradient"));

		return( super.getOrCreateOriginalMap() );
	}

	protected synchronized void setDarkModeInternal( boolean value, ColorInversor colorInversor )
	{
		super.setDarkModeInternal( value, colorInversor );

//		List gradient = value ? invertColors(_originalButtonGradient, colorInversor) : _originalButtonGradient;
//		UIManager.put( "Button.gradient", gradient );
	}
/*
	protected List<Object> invertColors( List<Object> gradient, ColorInversor colorInversor )
	{
		return( gradient.stream().map( el -> invertIfColor(el, colorInversor) )
					.collect( Collectors.toList() ) );
	}

	protected Object invertIfColor(Object obj, ColorInversor colorInversor)
	{
		Object result = obj;
		if( result instanceof Color )
			result = colorInversor.invertColor( (Color) obj );

		return( result );
	}
*/
}
