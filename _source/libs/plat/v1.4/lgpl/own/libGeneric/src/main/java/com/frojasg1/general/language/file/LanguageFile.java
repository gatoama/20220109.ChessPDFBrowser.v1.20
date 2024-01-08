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
package com.frojasg1.general.language.file;

import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.ResourceFunctions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LanguageFile
{
	protected String _resourceLanguageFolder = null;
	protected String _singleFileName = null;
	protected String _diskLanguageFolder = null;
	protected boolean _useAlwaysJar = false;

	public LanguageFile( String singleFileName,
							String resourceLanguageFolder,
							String diskLanguageFolder )
	{
		this( singleFileName, resourceLanguageFolder, diskLanguageFolder, false );
	}

	public LanguageFile( String singleFileName,
							String resourceLanguageFolder,
							String diskLanguageFolder,
							boolean useAlwaysJar )
	{
		_singleFileName = singleFileName;
		_resourceLanguageFolder = resourceLanguageFolder;
		_diskLanguageFolder = diskLanguageFolder;
		_useAlwaysJar = useAlwaysJar;
	}

	protected String getLanguageFileName( String folder, String language,
											String fileName, String folderSeparator )
	{
		return( folder + folderSeparator +
			( (language != null ) ? language + folderSeparator : "" ) +
			fileName );
	}

	protected String getLongResourceName( String language )
	{
		return( getLongResourceName( language, _singleFileName ) );
	}

	protected String getLongFileName( String language )
	{
		return( getLongFileName( language, _singleFileName ) );
	}

	protected String getLongResourceName( String language, String singleFileName )
	{
		return( getLanguageFileName( _resourceLanguageFolder, language, singleFileName, "/" ) );
	}

	public String getLongFileName( String language, String singleFileName )
	{
		return( getLanguageFileName( _diskLanguageFolder, language, singleFileName, File.separator ) );
	}

	protected boolean copyResourceToDisk( String originLanguage, String toLanguage,
											String singleFileName )
	{
		boolean result = false;

		try
		{
			String longResourceName = getLongResourceName( originLanguage, singleFileName );
			String longFileName = getLongFileName( toLanguage, singleFileName );

			File dir = ( new File( longFileName ) ).getParentFile();
			dir.mkdirs();
			// if the file was not prevously created, we copy now the resource to the configuracion path.
			result = ResourceFunctions.instance().copyBinaryResourceToFile( longResourceName, longFileName );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	public InputStream getInputStreamToFile( String language ) throws FileNotFoundException
	{
		return( getInputStreamToFile( language, _singleFileName ) );
	}

	public InputStream getInputStreamToFile( String language, String singleFileName ) throws FileNotFoundException
	{
//		String longResourceName = getResourcePathToRtfLicense( language );
		String longFileName = getLongFileName( language, singleFileName );

		if( ! FileFunctions.instance().isFile(longFileName) )
			copyResourceToDisk( language, language, singleFileName );

		InputStream result = null;

//		try
		{
			result = new FileInputStream( longFileName );
		}
//		catch( Exception ex )
		{
		}

		return( result );
	}

	public Writer getFileWriterOfLanguage( String language ) throws IOException
	{
		return( getFileWriter( language, _singleFileName ) );
	}

	public Writer getFileWriter( String language, String singleFileName ) throws IOException
	{
		String longFileName = getLongFileName( language, singleFileName );
		String folder = FileFunctions.instance().getAbsoluteDirName(longFileName);
		if( (folder != null) && !folder.isEmpty() )
		{
			File folderFile = new File( folder );
			if( ! folderFile.exists() )
				folderFile.mkdirs();
		}

		return( getFileWriter( longFileName ) );
	}

	public Writer getFileWriter( String longFileName ) throws IOException
	{
		return( new FileWriter( longFileName ) );
	}

	public InputStream getInputStreamToResource( String language )
	{
		return( getInputStreamToResource( language, _singleFileName ) );
	}

	public InputStream getInputStreamToResource( String language, String singleFileName )
	{
		String longResourceName = getLongResourceName( language, singleFileName );

		return( ResourceFunctions.instance().getInputStreamOfResource(longResourceName) );
	}

	public InputStream getInputStream( String language ) throws FileNotFoundException
	{
		return( getInputStream( language, _singleFileName ) );
	}

	public InputStream getInputStream( String language, String singleFileName ) throws FileNotFoundException
	{
		InputStream result = null;
		if( _useAlwaysJar )
			result = getInputStreamToResource( language, singleFileName );
		else
			result = getInputStreamToFile( language, singleFileName );

		return( result );
	}

	public String getSingleFileName()
	{
		return( _singleFileName );
	}
}
