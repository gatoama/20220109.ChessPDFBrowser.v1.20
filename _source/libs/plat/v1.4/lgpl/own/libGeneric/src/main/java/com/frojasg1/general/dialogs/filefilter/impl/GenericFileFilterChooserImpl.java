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
package com.frojasg1.general.dialogs.filefilter.impl;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.GenericConstants;
import com.frojasg1.general.dialogs.filefilter.GenericFileFilter;
import com.frojasg1.general.dialogs.filefilter.GenericFileFilterChooser;
import com.frojasg1.general.threads.ThreadFunctions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericFileFilterChooserImpl implements GenericFileFilterChooser,
													InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "GenericFileFilterChooserImpl.properties";
	protected static final String CONF_GENERIC_FILE_FILTER_FOR_EXECUTABLE_BY_ATTRIBUTES_DESCRIPTION = "CONF_GENERIC_FILE_FILTER_FOR_EXECUTABLE_BY_ATTRIBUTES_DESCRIPTION";

	public static final int GENERIC_FILE_FILTER_FOR_EXECUTABLE_BY_ATTRIBUTES = 0;

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	protected Map<Integer, GenericFileFilter> _map;
	protected Map<Integer, Supplier<GenericFileFilter>> _mapOfBuilders;

	public void init()
	{
		ThreadFunctions.instance().delayedSafeInvoke( () -> {
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								GenericConstants.sa_PROPERTIES_PATH_IN_JAR );
		registerInternationalizedStrings();
		},
			2000 );

		_map = createMap();
		_mapOfBuilders = createMapOfBuilders();
	}

	protected <KK, VV> Map<KK, VV> createMap()
	{
		return( new ConcurrentHashMap<>() );
	}

	protected Map<Integer, Supplier<GenericFileFilter>> createMapOfBuilders()
	{
		Map<Integer, Supplier<GenericFileFilter>> result = createMap();

		result.put(GENERIC_FILE_FILTER_FOR_EXECUTABLE_BY_ATTRIBUTES,
					() -> createGenericFileFilterForExecutable() );

		return( result );
	}

	@Override
	public synchronized GenericFileFilter getGenericFileFilter( int id )
	{
		GenericFileFilter result = _map.get( id );
		if( result == null )
		{
			Supplier<GenericFileFilter> builder = _mapOfBuilders.get( id );
			if( builder != null )
			{
				result = builder.get();
				if( result != null )
					_map.put(id, result);
			}
		}
		return( result );
	}

	public GenericFileFilter createGenericFileFilterForExecutable()
	{
		GenericFileFilterForExecutable result = new GenericFileFilterForExecutable(this);
		return( result );
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_GENERIC_FILE_FILTER_FOR_EXECUTABLE_BY_ATTRIBUTES_DESCRIPTION, "File filter for executable attributes" );
	}
}
