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
package com.frojasg1.general.desktop.view;

import com.frojasg1.applications.common.components.data.ComponentData;
import com.frojasg1.applications.common.components.data.MapOfComponents;
import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizationOwner;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindow;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorClientInterface;
import com.frojasg1.general.ClassFunctions;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatus;
import com.frojasg1.general.desktop.view.color.factory.impl.ColorInversorFactoryImpl;
import com.frojasg1.general.desktop.view.zoom.mapper.ContainerOfInternallyMappedComponent;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FrameworkComponentFunctions
{
	protected static FrameworkComponentFunctions _instance;

	protected ColorInversor _defaultColorInversor;

	public static void changeInstance( FrameworkComponentFunctions inst )
	{
		_instance = inst;
	}

	public static FrameworkComponentFunctions instance()
	{
		if( _instance == null )
			_instance = new FrameworkComponentFunctions();
		return( _instance );
	}

	public double getZoomFactor( Component comp )
	{
		double result = 1.0d;
		ChangeZoomFactorClientInterface zfc = FrameworkComponentFunctions.instance()
			.getOwnOrFirstParentOfClass(comp, ChangeZoomFactorClientInterface.class );
		if( zfc != null )
			result = zfc.getZoomFactor();

		return( result );
	}

	public int zoomValue( Component comp, int value )
	{
		return( IntegerFunctions.zoomValueFloor( value, getZoomFactor(comp) ) );
	}

	public void addInternallyMappedComponentToParent( InternallyMappedComponent imc,
														Component comp )
	{
		addInternallyMappedComponentElementToParentGen( comp,
			cimc -> cimc.addInternallyMappedComponent(imc) );
	}

	public void addPopupMenuToParent( JPopupMenu popupMenu,
									Component comp )
	{
		addInternallyMappedComponentElementToParentGen( comp,
			cimc -> cimc.addPopupMenu(popupMenu) );
	}

	public void addInternallyMappedComponentElementToParentGen( Component comp,
																Consumer<ContainerOfInternallyMappedComponent> method)
	{
		ContainerOfInternallyMappedComponent containerOfImc =
			FrameworkComponentFunctions.instance()
				.getOwnOrFirstParentOfClass(comp,
											ContainerOfInternallyMappedComponent.class);

		if( containerOfImc != null )
			method.accept(containerOfImc);
	}

	public <CC> CC getOwnOrFirstParentOfClass( Component comp, Class<CC> clazz )
	{
		CC result = ClassFunctions.instance().cast(comp, clazz);
		if( result == null )
			result = ComponentFunctions.instance().getFirstParentInstanceOf( clazz, comp );

		return( result );
	}

	public InternationalizationOwner getInternationalizationOwner( Component comp )
	{
		return( getOwnOrFirstParentOfClass( comp, InternationalizationOwner.class ) );
	}

	public InternationalizedWindow getInternationalizedWindow( Component comp )
	{
		return( getOwnOrFirstParentOfClass( comp, InternationalizedWindow.class ) );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}

	protected ColorInversor getDefaultColorInversor()
	{
		if( _defaultColorInversor == null )
			_defaultColorInversor = ColorInversorFactoryImpl.instance().createColorInversor();

		return( _defaultColorInversor );
	}

	public ColorInversor getColorInversor(Component comp)
	{
		ColorInversor result = getIfNotNull(  getInternationalizedWindow( comp ),
								InternationalizedWindow::getColorInversor );
		if( result == null )
			result = getDefaultColorInversor();

		return( result );
	}

	public boolean isDarkModeActivated(Component comp)
	{
		Boolean result = isDarkModeInternal(comp);

		if( result == null )
			result = getIfNotNull( GenericFunctions.instance().getAppliConf(),
								BaseApplicationConfigurationInterface::isDarkModeActivated );

		if( result == null )
			result = false;

		return( result );
	}

	public MapOfComponents getMapOfComponents(Component comp)
	{
		return( getIfNotNull(
						getIfNotNull(  getInternationalizationOwner( comp ),
										InternationalizationOwner::getInternationalization ),
							JFrameInternationalization::getMapOfComponents ) );
	}

	public ComponentData getComponentData(Component comp)
	{
		return( getIfNotNull( getMapOfComponents(comp),	moc -> moc.get(comp) ) );
	}

	public ComponentData getComponentDataOnTheFly(Component comp)
	{
		return( getIfNotNull( getMapOfComponents(comp),	moc -> moc.getOrCreateOnTheFly(comp) ) );
	}

	public ColorThemeChangeableStatus getColorThemeChangeableStatus( Component comp )
	{
		return( getIfNotNull( getComponentData( comp ), ComponentData::getColorThemeChangeableStatus ) );
	}

	protected Component getAncestor( Component comp )
	{
		return( ComponentFunctions.instance().getAncestor(comp) );
	}

	public ColorThemeChangeableStatus getAncestorColorThemeChangeableStatus( Component comp )
	{
		return( getIfNotNull( getInternationalizationOwner( comp ),
							ancestor ->
								( ancestor instanceof ColorThemeChangeableStatus) ?
								(ColorThemeChangeableStatus) ancestor :
								null ) );
	}

	protected Boolean isColorChangeableBooleanAttribute( Component comp,
		Function<ColorThemeChangeableStatus, Boolean> getter )
	{
		Boolean result = getIfNotNull( getColorThemeChangeableStatus( comp ), getter );
		if( result == null )
			result = getIfNotNull( getAncestorColorThemeChangeableStatus( comp ), getter );

		return( result );
	}

	public Boolean isDarkModeInternal(Component comp)
	{
		return( isColorChangeableBooleanAttribute( comp, ColorThemeChangeableStatus::isDarkMode ) );
	}

	protected boolean isColorThemeChangeableStatusBooleanWithDefault(Component comp,
		Function<ColorThemeChangeableStatus, Boolean> getter, boolean defaultValue)
	{
		Boolean result = isColorChangeableBooleanAttribute(comp, getter);

		if( result == null )
			result = defaultValue;

		return( result );
	}

	protected Boolean isDarkModeInternal( Object obj )
	{
		Boolean result = null;
		if( obj instanceof ColorThemeChangeableStatus )
			result = ( (ColorThemeChangeableStatus) obj ).isDarkMode();

		return( result );
	}

	public boolean defaultIsDarkMode()
	{
		return( GenericFunctions.instance().getAppliConf().isDarkModeActivated() );
	}

	public boolean isDarkMode(Component comp)
	{
		return( isDarkMode( comp, defaultIsDarkMode() ) );
	}

	public boolean isDarkMode(Component comp, boolean defaultValue)
	{
//		Boolean result = null;//isDarkModeInternal( comp );
//		if( result == null )
//			result = isDarkModeInternal( getInternationalizationOwner( comp ) );
//		if( result == null )
//			result = false;

//		return( result );
		return( isColorThemeChangeableStatusBooleanWithDefault( comp,
			ColorThemeChangeableStatus::isDarkMode, defaultValue ) );
	}

	public boolean wasLatestModeDark(Component comp)
	{
		return( isColorThemeChangeableStatusBooleanWithDefault( comp,
			ColorThemeChangeableStatus::wasLatestModeDark, false ) );
	}

	public Locale getOutputLocale(Component comp)
	{
		InternationalizedWindow window = getInternationalizedWindow(comp);
		Locale result = JComponent.getDefaultLocale();
		if( window != null )
			result = window.getOutputLocale();

		return( result );
	}
}
