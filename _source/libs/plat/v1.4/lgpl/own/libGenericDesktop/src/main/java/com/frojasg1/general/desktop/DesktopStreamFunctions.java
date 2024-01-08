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
package com.frojasg1.general.desktop;

import com.frojasg1.general.StreamFunctions;
import com.frojasg1.general.desktop.files.charset.CharsetManager;
import com.frojasg1.general.docformats.rtf.ZoomFontToRtfStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopStreamFunctions extends StreamFunctions
{
	protected static DesktopStreamFunctions _instance;

	public static void changeInstance( DesktopStreamFunctions inst )
	{
		_instance = inst;
	}

	public static DesktopStreamFunctions instance()
	{
		if( _instance == null )
			_instance = new DesktopStreamFunctions();
		return( _instance );
	}

	public StyledDocument loadRtfInputStream( InputStreamReader isr )
	{
		RTFEditorKit ek = new RTFEditorKit();
		StyledDocument result = new DefaultStyledDocument();
		
		try
		{
			ek.read( isr, result, 0);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			result = null;
		}

		return( result );
	}

	public StyledDocument loadRtfInputStream( InputStream is ) throws IOException
	{
		StyledDocument result = null;
		
		byte[] bytes = new byte[ is.available() ];
		is.read( bytes );

		try( ByteArrayInputStream bais = new ByteArrayInputStream( bytes ); )
		{
			String charsetName = CharsetManager.instance().M_detectCharset(bais);

			try( ByteArrayInputStream bais2 = new ByteArrayInputStream( bytes );
					InputStreamReader isr = ( charsetName != null ) ?
											new InputStreamReader( bais2, charsetName ) :
											new InputStreamReader( bais2 )
				)
			{
				result = loadRtfInputStream( isr );
			}
		}

		return( result );
	}

	public StyledDocument loadAndZoomRtfInputStream( InputStream is, double zoomFactor ) throws IOException
	{
		StyledDocument result = null;

		ZoomFontToRtfStream zftrs = new ZoomFontToRtfStream();
		try( InputStream is2 = zftrs.getInputStream(is, null, zoomFactor ) )
		{
//			System.out.println( "ZoomFontToRtfStream not null" );
			result = DesktopStreamFunctions.instance().loadRtfInputStream(is2);
/*			if( result != null )
				System.out.println( "rtfDoc not null" );
			else
				System.out.println( "rtfDoc is null" );
*/
		}

		return( result );
	}
}
