/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 */
package com.frojasg1.general.jersey.queries;

import com.frojasg1.general.BooleanFunctions;
import com.frojasg1.general.desktop.queries.InetQuery;
import com.frojasg1.general.desktop.queries.InetQueryException;
import com.frojasg1.general.desktop.queries.InetQueryResult;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author fjavier.rojas
 */
public abstract class InetQueryBase<CC extends InetQueryResult > implements InetQuery<CC>
{
	protected String _uri = null;
	protected String _path = null;

	@Override
	public void init( String uri, String path )
	{
		_uri = uri;
		_path = path;
	}

	public abstract void init( String uri );

	protected JSONObject getJsonObject( String jsonString )
	{
		return( new JSONObject( jsonString ) );
	}

	protected JSONArray getJsonArray( String jsonString ) throws JSONException
	{
		return( new JSONArray( jsonString ) );
	}

	protected MultivaluedMap<String, String> createMultimap( String ... strArr )
	{
		MultivaluedMap<String, String> result = new MultivaluedMapImpl();

		for( int ii=0; ii<strArr.length-1; ii+=2 )
		{
			result.add( strArr[ii], strArr[ii+1] );
		}

		return( result );
	}

	@Override
	public CC queryGen( String ... params ) throws InetQueryException
	{
		return( queryGen( createMultimap( params ) ) );
	}

	public CC queryGen( MultivaluedMap<String, String> params ) throws InetQueryException
	{
		CC result = null;

		Client client = Client.create();
		WebResource webResource = client.resource(_uri);

		ClientResponse clientResponse = null;
		try
		{
			clientResponse = webResource .path(_path)
										.queryParams(params)
			//                          .header(HttpHeaders.AUTHORIZATION, AuthenticationHelper.getBasicAuthHeader())
										.get(ClientResponse.class);
			result = convert( clientResponse );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			result = createInetQueryResult( ex );
		}

		return( result );
	}

	protected boolean isOk( ClientResponse clientResponse )
	{
		return( clientResponse.getStatus() == ClientResponse.Status.OK.getStatusCode() );
	}

	protected abstract CC convert( String jsonStr ) throws InetQueryException;
	protected abstract CC createInetQueryResult();

	protected CC createInetQueryResult( Exception exception )
	{
		CC result = createInetQueryResult();

		result.setException(exception);

		return( result );
	}

	protected CC convert( ClientResponse clientResponse ) throws InetQueryException
	{
		CC result = null;
		if( clientResponse != null )
		{
			if( isOk( clientResponse ) )
			{
				String jsonStr = getBody(clientResponse);
				result = convert( jsonStr );
			}
			else
			{
				result = createInetQueryResult( createInetQueryException( clientResponse ) );
			}
		}
/*
		if( result == null )
		{
			throwInetQueryException( clientResponse );
		}
*/
		return( result );
	}

	// https://stackoverflow.com/questions/18086621/read-response-body-in-jax-rs-client-from-a-post-request
	protected String getBody( ClientResponse clientResponse )
	{
		String result = null;

		// it works on Jersey 1.x
		String jsonStr = clientResponse.getEntity( String.class );

		// for Jersey 2.x
		// String jsonStr = clientResponse.readEntity( String.class );

		result = jsonStr;

		return( result );
	}

	protected InetQueryException createInetQueryException( ClientResponse clientResponse )
	{
		return( new InetQueryException( clientResponse.getStatus(),
										getBody( clientResponse ) ) );
	}

	protected void throwInetQueryException( ClientResponse clientResponse ) throws InetQueryException
	{
		throw( createInetQueryException( clientResponse ) );
	}

	protected String booleanToString( boolean value )
	{
		return( BooleanFunctions.instance().booleanToString( value ) );
	}
}
