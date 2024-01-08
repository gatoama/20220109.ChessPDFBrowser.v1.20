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
package com.frojasg1.general.commandline.files;

import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.desktop.files.charset.CharsetManager;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

/**
 *
 * @author Usuario
 */
public class DesktopFileFunctions extends FileFunctions
{
	protected static DesktopFileFunctions _instance;

	public static void changeInstance( DesktopFileFunctions inst )
	{
		_instance = inst;
	}

	public static DesktopFileFunctions instance()
	{
		if( _instance == null )
			_instance = new DesktopFileFunctions();
		return( _instance );
	}

	public Document loadRtfDocumentFromFile( String fileName )
	{
		Document result = null;

		FileInputStream fis = null;
		InputStreamReader isr = null;
		try
		{
			String charsetName = CharsetManager.instance().M_detectCharset( fileName );

			fis = new FileInputStream( fileName );

			if( charsetName != null )	isr = new InputStreamReader( fis, charsetName );
			else							isr = new InputStreamReader( fis );

			RTFEditorKit ek = new RTFEditorKit();
			Document doc = new DefaultStyledDocument();
			ek.read( isr, doc, 0);

			result = doc;
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
				if( fis != null )
					fis.close();
			}
			catch( Throwable th )
			{
			}
		}
		return( result );
	}

}
