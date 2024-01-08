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
package com.frojasg1.general;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author Usuario
 */
public class ResourceFunctions
{
	protected static ResourceFunctions _instance;

	public static void changeInstance( ResourceFunctions inst )
	{
		_instance = inst;
	}

	public static ResourceFunctions instance()
	{
		if( _instance == null )
			_instance = new ResourceFunctions();
		return( _instance );
	}

	public InputStream getInputStreamOfResource( String resourcePath )
	{
		InputStream in = null;
		try
		{
			ClassLoader loader = ClassLoader.getSystemClassLoader ();
			in = loader.getResourceAsStream (resourcePath);

			if( in == null )
			{
				try
				{
					in = ResourceFunctions.class.getClassLoader().getResource(resourcePath).openStream();
				}
				catch( Throwable th )
				{
					in = null;
				}
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			
			try
			{
				if( in != null )
					in.close();
			}
			catch( Throwable th2 )
			{
				
			}
		}
		
		return( in );
	}

	public boolean copyBinaryResourceToFile( String longResourceName,
												String longFileName )
	{
		boolean result = true;
		
		InputStream is = null;

		try
		{
			is = getInputStreamOfResource( longResourceName );
			copyInputStreamToFile( is, longFileName );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			result = false;
		}
		finally
		{
			if( is != null )
			{
				try
				{
					is.close();
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		}

		return( result );
	}

	public void copyInputStreamToFile( InputStream is,
										String longFileName ) throws IOException
	{
		OutputStream fos = null;

		boolean exception = false;
		try
		{
			fos = new FileOutputStream(longFileName);
			copyInputStreamToOutputStream( is, fos );
		}
		catch( Exception ex )
		{
			exception = true;
		}
		finally
		{
			if( fos != null )
			{
				try
				{
					fos.close();
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}

			if( exception )
			{
				File file = new File( longFileName );
				file.delete();
			}
		}
	}

	public void copyInputStreamToOutputStream( InputStream is,
												OutputStream os ) throws IOException
	{
		byte[] buffer = new byte[65536];
		int noOfBytes = 0;

		// read bytes from source file and write to destination file
		while ((noOfBytes = is.read(buffer)) != -1)
		{
			os.write(buffer, 0, noOfBytes);
		}
	}

	public Properties loadPropertiesFromJar( String resourceName )
	{
		Properties result = new Properties( );

		try( InputStream is = getInputStreamOfResource(resourceName) )
		{
			result.load( is );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			result = null;
		}

		return( result );
	}
}
