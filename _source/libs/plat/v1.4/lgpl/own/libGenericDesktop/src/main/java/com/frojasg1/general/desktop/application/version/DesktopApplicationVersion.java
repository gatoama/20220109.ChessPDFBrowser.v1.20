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
package com.frojasg1.general.desktop.application.version;

import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.application.version.ApplicationVersion;

/**
 *
 * @author fjavier.rojas
 */
public class DesktopApplicationVersion implements ApplicationVersion
{
	protected static final String DEFAULT_DOWNLOAD_FILE_FILENAME = "_newVersion/downloadFile.txt";

	protected String _downloadFileName = DEFAULT_DOWNLOAD_FILE_FILENAME;

	protected static DesktopApplicationVersion _instance;

	protected String _downloadFile = null;

	public static void changeInstance( DesktopApplicationVersion inst )
	{
		_instance = inst;
	}

	public static DesktopApplicationVersion instance()
	{
		if( _instance == null )
			_instance = new DesktopApplicationVersion();
		return( _instance );
	}

	protected String getContentsOfFile( String fileName )
	{
		return( FileFunctions.instance().loadTextFileContent(fileName) );
	}

	public void setDownloadFileFileName( String fileName )
	{
		_downloadFileName = fileName;
	}

	public String getDownloadFileFileName( )
	{
		return( _downloadFileName );
	}

	protected String calculateDownloadFile()
	{
		String result = getContentsOfFile( getDownloadFileFileName() );
		if( result != null )
		{
			String [] arr = result.split( "\\s" );
			if( arr.length > 0 )
			{
				result = arr[0];
			}
		}

		return( result );
	}

	@Override
	public String getDownloadFile()
	{
		if( _downloadFile == null )
			_downloadFile = calculateDownloadFile();

		return( _downloadFile );
	}
}
