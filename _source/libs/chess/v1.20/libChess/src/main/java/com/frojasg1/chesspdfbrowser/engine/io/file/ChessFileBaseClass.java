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
package com.frojasg1.chesspdfbrowser.engine.io.file;

import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessWriterException;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.general.desktop.files.TextFileWrapper;
import com.frojasg1.general.exceptions.GeneralFileException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Usuario
 */
public abstract class ChessFileBaseClass implements ChessFileInterface
{
	protected TextFileWrapper _textFileWrapper = null;
	
	public List<ChessGame> loadFromFile( String fileName ) throws IOException, GeneralFileException, ChessParserException
	{
		List<ChessGame> result = null;
		_textFileWrapper = new TextFileWrapper( fileName );

		try
		{
			BufferedReader br = _textFileWrapper.openReadStream();

			result = loadFromFile_child( br );
		}
		finally
		{
			try
			{
				_textFileWrapper.closeReadStream();
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
		return( result );
	}

	public abstract List<ChessGame> loadFromFile_child( BufferedReader reader ) throws IOException, ChessParserException;
	public abstract void saveToFile_child( List<ChessGame> list, BufferedWriter writer ) throws IOException, ChessWriterException;

	public void saveToFile( List<ChessGame> list, String fileName, String charsetName ) throws IOException, GeneralFileException, ChessWriterException
	{
		try
		{
			if( _textFileWrapper == null )
				_textFileWrapper = new TextFileWrapper( fileName );

			BufferedWriter bw = _textFileWrapper.openSecureReplaceStream(charsetName, fileName);

			saveToFile_child( list, bw );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			_textFileWrapper.closeSecureReplaceStream_with_ERROR();
			th.printStackTrace();
			throw( th );
		}

		_textFileWrapper.closeSecureReplaceStream_without_ERROR();
	}

	public String getFileName()
	{
		String result = null;
		if( _textFileWrapper != null )
			result = _textFileWrapper.getFileName();

		return( result );
	}
}
