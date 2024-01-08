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
package com.frojasg1.general.desktop.files;

import com.frojasg1.general.ResourceFunctions;
import com.frojasg1.general.desktop.files.charset.CharsetManager;
import com.frojasg1.general.desktop.image.ImageFunctions;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopResourceFunctions extends ResourceFunctions
{
	protected static DesktopResourceFunctions _instance;

	public static void changeInstance( DesktopResourceFunctions inst )
	{
		_instance = inst;
	}

	public static DesktopResourceFunctions instance()
	{
		if( _instance == null )
			_instance = new DesktopResourceFunctions();
		return( _instance );
	}
/*
	public BufferedImage loadResourceImage( String resourcePath )
	{
		BufferedImage result = null;

		InputStream in = null;
		try
		{
			in = getInputStreamOfResource( resourcePath );
			result = ImageIO.read(in);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			result = null;
		}
		finally
		{
			try
			{
				if( in != null )
					in.close();
			}
			catch( Throwable th )
			{
				
			}
		}
		return( result );
	}
*/
	public BufferedImage loadResourceImage( String resourcePath )
	{
		BufferedImage result = null;

		InputStream in = getInputStreamOfResource( resourcePath );
		result = ImageFunctions.instance().loadImage( in );

		return result;
	}

	public Document loadResourceRTFDocument( String resourcePath )
	{
		Document result = null;

		InputStream is = null;
		InputStreamReader isr = null;
		try
		{
			is = getInputStreamOfResource( resourcePath );
			if( is != null )
			{
//				is.mark( 2000000000 );
				String charsetName = CharsetManager.instance().M_detectCharset(is );

				try
				{
					is.close();
				}
				catch( Throwable th )
				{
				}

				is = getInputStreamOfResource( resourcePath );

//				is.reset();		// does not work
				if( charsetName != null )	isr = new InputStreamReader( is, charsetName );
				else							isr = new InputStreamReader( is );


				RTFEditorKit ek = new RTFEditorKit();
				Document doc = new DefaultStyledDocument();
				ek.read( isr, doc, 0);

				result = doc;
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			result = null;
		}
		finally
		{
			try
			{
				if( isr != null )
					isr.close();
				if( is != null )
					is.close();
			}
			catch( Throwable th )
			{
			}
		}
		return( result );
	}


	// https://stackoverflow.com/questions/35338990/read-all-files-from-a-package-in-java
	public Resource[] getResources(String package_) throws IOException
	{
		PathMatchingResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver();
		// turns com.myapp.mypackage into /com/myapp/mypackage/*
		return pmrpr.getResources("/" + package_.replace(".", "/") + "/*");
	}
}
