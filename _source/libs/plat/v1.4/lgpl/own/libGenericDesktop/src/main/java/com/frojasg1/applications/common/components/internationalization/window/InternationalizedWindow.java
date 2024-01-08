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
package com.frojasg1.applications.common.components.internationalization.window;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorClientInterface;
import com.frojasg1.general.desktop.generic.view.DesktopViewWindow;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.awt.Component;
import java.util.Locale;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.general.context.ApplicationContext;
import com.frojasg1.general.context.ApplicationContextGetter;
import com.frojasg1.general.desktop.view.color.ColorInversorOwner;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatus;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.desktop.view.zoom.mapper.ContainerOfInternallyMappedComponent;

/**
 *
 * @author Usuario
 */
public interface InternationalizedWindow<CC extends ApplicationContext >
									extends ComponentWithOverlappedImage,
											InternationalizedStringConf,
											DesktopViewWindow,
											ChangeZoomFactorClientInterface,
											InternationalizationOwner,
											InternationalizationInitializationEndCallback,
											ApplicationContextGetter< CC >,
											ColorThemeChangeableStatus,
											ColorInversorOwner,
											InternallyMappedComponent,
											ContainerOfInternallyMappedComponent
{
	public void changeLanguage( String language ) throws ConfigurationException, InternException;
	public String getLanguage();

	public Locale getOutputLocale();

	public BaseApplicationConfigurationInterface getAppliConf();
	public void applyConfiguration() throws ConfigurationException, InternException;

//	@Deprecated
//	public void changeFontSize( float factor );

	public boolean getAlwaysHighlightFocus();
//	public void setAlwaysHighlightFocus( boolean value );
	public void setAlwaysHighlightFocus(boolean value);

	public void focusAndHighlightComponent( ViewComponent vc );
	public void highlightComponent( ViewComponent vc );
	public boolean hasToClearMarkedComponent();

	public Object getSynchronizedLockForPaint();
	public Component getLastFocusedComponentDrawn();

	public void changeToWaitCursor();
	public void revertChangeToWaitCursor();
	
	public void formWindowClosing( boolean closeWindow );
	public void formWindowClosingEvent();
//	public void releaseResources();

	public void setAppliConf( BaseApplicationConfigurationInterface applicationConfiguration );

	public void setInitialized();

	public void invokeConfigurationParameterColorThemeChanged();
}
