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
package com.frojasg1.general.desktop.generic.files;

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.GenericConstants;
import com.frojasg1.general.files.BinaryFile;
import com.frojasg1.general.files.BinaryFileException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author Usuario
 */
public class DesktopBinaryFile implements BinaryFile
{
	public static final String GLOBAL_CONF_FILE_NAME = "DesktopBinaryFile.properties";

	public static final String CONF_FILE_ALREADY_OPENED = "FILE_ALREADY_OPENED";

	protected RandomAccessFile _raf = null;
	protected FileChannel _fileChannel = null;
	protected String _fileName = null;

	protected static InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																											GenericConstants.sa_PROPERTIES_PATH_IN_JAR );

	static
	{
		try
		{
			registerInternationalizedStrings();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public DesktopBinaryFile()
	{
		
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_FILE_ALREADY_OPENED, "Error, file already opened" );
	}

	@Override
	public String getFileName()
	{
		return( _fileName );
	}

	@Override
	public void openForReading(String fileName) throws BinaryFileException
	{
		if( isOpen() )
			throw( new BinaryFileException( getInternationalString( CONF_FILE_ALREADY_OPENED ) ) );

		try
		{
			_fileName = fileName;
			Path path = FileSystems.getDefault().getPath(fileName);
			_fileChannel = FileChannel.open( path, StandardOpenOption.READ );
		}
		catch( Throwable th )
		{
			throw( new BinaryFileException( th.toString() ) );
		}
	}

	@Override
	public void openForReadingAndWriting(String fileName, boolean truncateExisting) throws BinaryFileException
	{
		if( isOpen() )
			throw( new BinaryFileException( getInternationalString( CONF_FILE_ALREADY_OPENED ) ) );

		try
		{
			_fileName = fileName;
			Path path = FileSystems.getDefault().getPath(fileName);

			if( ! truncateExisting )
			{
				if( FileFunctions.instance().isFile( fileName ) )
				{
					_raf = new RandomAccessFile( fileName, "rw" );
					_fileChannel = _raf.getChannel();
				}
				else
				{
					_fileChannel = FileChannel.open( path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE );
				}
			}
			else
				_fileChannel = FileChannel.open( path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING );
		}
		catch( Throwable th )
		{
			throw( new BinaryFileException( th.toString() ) );
		}
	}

	@Override
	public void openForReadingAndWriting_SYNC(String fileName, boolean truncateExisting) throws BinaryFileException
	{
		if( isOpen() )
			throw( new BinaryFileException( getInternationalString( CONF_FILE_ALREADY_OPENED ) ) );

		try
		{
			_fileName = fileName;
			Path path = FileSystems.getDefault().getPath(fileName);
			if( ! truncateExisting )
				_fileChannel = FileChannel.open( path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.SYNC );
			else
				_fileChannel = FileChannel.open( path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE,
														StandardOpenOption.SYNC, StandardOpenOption.TRUNCATE_EXISTING );
		}
		catch( Throwable th )
		{
			throw( new BinaryFileException( th.toString() ) );
		}
	}

	@Override
	public void openForReadingAndWriting_DSYNC(String fileName, boolean truncateExisting) throws BinaryFileException
	{
		if( isOpen() )
			throw( new BinaryFileException( getInternationalString( CONF_FILE_ALREADY_OPENED ) ) );

		try
		{
			_fileName = fileName;
			Path path = FileSystems.getDefault().getPath(fileName);
			if( ! truncateExisting )
				_fileChannel = FileChannel.open( path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.DSYNC );
			else
				_fileChannel = FileChannel.open( path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE,
														StandardOpenOption.DSYNC, StandardOpenOption.TRUNCATE_EXISTING );
		}
		catch( Throwable th )
		{
			throw( new BinaryFileException( th.toString() ) );
		}
	}

	@Override
	public int write(ByteBuffer bb) throws BinaryFileException
	{
		int result = -1;
		try
		{
			result = _fileChannel.write( bb );
		}
		catch( RuntimeException rte )
		{
			throw( rte );
		}
		catch( Throwable th )
		{
			throw( new BinaryFileException( th.toString() ) );
		}
		return( result );
	}

	@Override
	public int write(ByteBuffer bb, long position) throws BinaryFileException
	{
		int result = -1;
		try
		{
			result = _fileChannel.write( bb, position );
		}
		catch( RuntimeException rte )
		{
			throw( rte );
		}
		catch( Throwable th )
		{
			throw( new BinaryFileException( th.toString() ) );
		}

		return( result );
	}

	@Override
	public void writeText( String text ) throws BinaryFileException
	{
		ByteBuffer bb = ByteBuffer.wrap( text.getBytes() );
		write(bb);
	}

	@Override
	public void writeLine( String text ) throws BinaryFileException
	{
		text = String.format( "%s%n", text );
		writeText( text );
	}

	@Override
	public int read(ByteBuffer bb) throws BinaryFileException
	{
		int result = -1;
		try
		{
			result = _fileChannel.read( bb );
		}
		catch( RuntimeException rte )
		{
			throw( rte );
		}
		catch( Throwable th )
		{
			throw( new BinaryFileException( th.toString() ) );
		}
		return( result );
	}

	@Override
	public int read(ByteBuffer bb, long position) throws BinaryFileException
	{
		int result = -1;
		try
		{
			result = _fileChannel.read( bb, position );
		}
		catch( RuntimeException rte )
		{
			throw( rte );
		}
		catch( Throwable th )
		{
			throw( new BinaryFileException( th.toString() ) );
		}
		return( result );
	}

	@Override
	public boolean isOpen()
	{
		boolean result = false;

		if( _fileChannel != null )
			result = _fileChannel.isOpen();

		return( result );
	}

	@Override
	public void close() throws BinaryFileException
	{
		try
		{
			_fileChannel.close();
			if( _raf != null )
				_raf.close();
		}
		catch( RuntimeException rte )
		{
			throw( rte );
		}
		catch( Throwable th )
		{
			throw( new BinaryFileException( th.toString() ) );
		}
	}

	protected static void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	protected static String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}
}
