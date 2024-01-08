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

import com.frojasg1.general.desktop.files.charset.CharsetManager;
import com.frojasg1.general.structures.Pair;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class StreamFunctions
{
	protected static StreamFunctions _instance;

	public static void changeInstance( StreamFunctions inst )
	{
		_instance = inst;
	}

	public static StreamFunctions instance()
	{
		if( _instance == null )
			_instance = new StreamFunctions();
		return( _instance );
	}

	public InputStream getInputStream( String str, Charset charset )
	{
		if( charset == null )
			charset = StandardCharsets.UTF_8;

		InputStream result = null;
		try
		{
			result = new ByteArrayInputStream( str.getBytes( charset.name() ));
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		
		return( result );
	}

	public String readTextFromInputStream( InputStream is )
	{
		return( readCharsetNameAndTextFromInputStreamInternal( is, false ).getValue() );
	}

	public Pair<String, String> readCharsetNameAndTextFromInputStreamException( InputStream is )
	{
		return( readCharsetNameAndTextFromInputStreamInternal( is, true ) );
	}

	protected Pair<String, String> readCharsetNameAndTextFromInputStreamInternal( InputStream is, boolean throwException )
	{
		Pair<String, String> result = new Pair<>(null, null);

		try( InputStream is1 = is )
		{
			byte[] bytes = new byte[ is1.available() ];
			is1.read( bytes );

			String charsetName = CharsetManager.instance().M_detectCharset(bytes);

			String text = null;
			if( charsetName != null )
				text = new String( bytes, charsetName );
			else
				text = new String( bytes );
			
			result.setKey(charsetName);
			result.setValue(text);
		}
		catch( Exception ex )
		{
			if( throwException )
				throw( new RuntimeException( "Error reading input stream", ex ) );
			else
				ex.printStackTrace();
		}

		return( result );
	}

	public String readTextFromFile( String fileName )
	{
		return( readCharsetNameAndTextFromFileInternal( fileName, false ).getValue() );
	}

	public Pair<String, String> readCharsetNameAndTextFromFileException( String fileName )
	{
		return( readCharsetNameAndTextFromFileInternal( fileName, true ) );
	}

	protected Pair<String, String> readCharsetNameAndTextFromFileInternal( String fileName, boolean throwException )
	{
		Pair<String, String> result = null;

		try
		{
			FileInputStream fis = new FileInputStream(fileName);
			result = readCharsetNameAndTextFromInputStreamInternal( fis, throwException );
		}
		catch( Exception ex )
		{
			if( throwException )
				throw( new RuntimeException( "Error reading file " + fileName, ex ) );
			else
				ex.printStackTrace();
		}

		return( result );
	}
}
