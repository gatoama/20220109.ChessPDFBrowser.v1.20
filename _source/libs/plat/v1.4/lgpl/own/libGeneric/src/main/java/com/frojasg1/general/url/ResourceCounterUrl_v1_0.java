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
package com.frojasg1.general.url;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ResourceCounterUrl_v1_0 extends ResourceCounterUrlBase
									implements ResourceCounterUrl
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceCounterUrl_v1_0.class);

	protected static ResourceCounterUrl_v1_0 _instance = null;
	
	public static ResourceCounterUrl_v1_0 instance()
	{
		if( _instance == null )
			_instance = new ResourceCounterUrl_v1_0();

		return( _instance );
	}

	public String createResourceCounterUrl( String realUrl )
	{
		String result = null;

		try
		{
			URIBuilder builder = new URIBuilder( getAppliConf().getUrlForResourceCounter() )
				.addParameter( "operation", "countAndForward" )
				.addParameter( "url", realUrl )
				.addParameter( "origin", "appli" )
				.addParameter( "versionOfService", "v1.0" )
				.addParameter( "applicationBaseResourceName", getApplicationNameResourceBase() )
				.addParameter( "applicationLanguage", getAppliConf().getLanguage() );

			result = builder.build().toASCIIString();
		}
		catch( Exception ex )
		{
			LOGGER.error( "Error creating ResourceCounterUrl", ex );
		}

		return( result );
	}
}
