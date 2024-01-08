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
package com.frojasg1.applications.common.configuration.imp;


import com.frojasg1.applications.common.configuration.ConfigurationParent;
import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
public class FormGeneralConfiguration extends ConfigurationParent
{
    public static final String CONF_ANCHO_VENTANA = "ANCHO_VENTANA";
    public static final String CONF_ALTO_VENTANA = "ALTO_VENTANA";
    public static final String CONF_POSICION_X = "POSICION_X";
    public static final String CONF_POSICION_Y = "POSICION_Y";
    public static final String CONF_ZOOM_FACTOR = "ZOOM_FACTOR";

    public static final String CONF_WIDTH = "WIDTH";
    public static final String CONF_HEIGHT = "HEIGHT";

	protected Properties a_defaultPropertiesReadFromComponents = null;
	
	public FormGeneralConfiguration(	String mainFolder,
										String applicationName, String group,
										String configurationFileName,
										Properties defaultPropertiesReadFromComponents )
	{
		super( mainFolder, applicationName, group, null, configurationFileName );
		
		a_defaultPropertiesReadFromComponents = defaultPropertiesReadFromComponents;
	}

	protected Properties M_getDefaultProperties( String language )
	{
		Properties result = a_defaultPropertiesReadFromComponents;
		
		a_defaultPropertiesReadFromComponents.setProperty( CONF_ALTO_VENTANA, "500" );
		a_defaultPropertiesReadFromComponents.setProperty( CONF_ANCHO_VENTANA, "200" );
		a_defaultPropertiesReadFromComponents.setProperty( CONF_POSICION_X, "400" );
		a_defaultPropertiesReadFromComponents.setProperty( CONF_POSICION_Y, "150" );

		a_defaultPropertiesReadFromComponents.setProperty( CONF_ZOOM_FACTOR, "1.0" );

		result = M_makePropertiesAddingDefaults(result, a_defaultPropertiesReadFromComponents );

		return( result );
	}
}

