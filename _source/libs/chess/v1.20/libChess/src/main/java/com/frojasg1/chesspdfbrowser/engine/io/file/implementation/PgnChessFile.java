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
package com.frojasg1.chesspdfbrowser.engine.io.file.implementation;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessWriterException;
import com.frojasg1.chesspdfbrowser.engine.io.file.ChessFileBaseClass;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation.PGNChessGameParser;
import com.frojasg1.chesspdfbrowser.engine.io.writers.ChessGamePGNWriterObserver;
import com.frojasg1.chesspdfbrowser.engine.io.writers.implementation.ChessGamePGNwriter;
import com.frojasg1.chesspdfbrowser.engine.io.writers.implementation.ChessGameViewWriter;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class PgnChessFile extends ChessFileBaseClass implements ChessGamePGNWriterObserver
{
//	protected ChessLanguageConfiguration _languageConfiguration = null;
	protected BufferedWriter _writer = null;

	protected static final String EXTENSION = "pgn";

	protected String _lastError = null;

	protected StringBuilder _tmpSb = null;
/*	
	public PgnChessFile( ChessLanguageConfiguration languageConfiguration )
	{
		_languageConfiguration = languageConfiguration;
	}
*/
	public PgnChessFile()
	{
	}
	
	public static String getExtension()
	{
		return( EXTENSION );
	}

	@Override
	public List<ChessGame> loadFromFile_child( BufferedReader reader ) throws IOException, ChessParserException
	{
//		PGNChessGameParser pgnParser = new PGNChessGameParser( _languageConfiguration );
		PGNChessGameParser pgnParser = new PGNChessGameParser( );
		List<ChessGame> result = pgnParser.parseChessGameText(reader);

		return( result );
	}

	@Override
	public void saveToFile_child( List<ChessGame> list, BufferedWriter writer ) throws IOException, ChessWriterException
	{
		_writer = writer;

		Iterator<ChessGame> it = list.iterator();

		ChessGame cg = null;
		while( it.hasNext() )
		{
			if( cg != null )
				_writer.newLine();

			cg = it.next();
			saveGameToFile(cg);

			if( _lastError != null )
				throw( new ChessWriterException( _lastError ) );
		}
	}

	public String saveGameToFile( ChessGame game ) throws IOException, ChessWriterException
	{
		_tmpSb = new StringBuilder();

		ChessGameViewWriter pgnWriter = new ChessGamePGNwriter( this );

		String textString = pgnWriter.getGameHeaderString(game);
		if( textString != null ) _tmpSb.append( textString );

		if( _writer != null )
			_writer.newLine();

		try
		{
			game.setChessViewConfiguration( PGNchessViewConfiguration.instance() );
			game.updateAdditionalInfo();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			throw( new ChessWriterException( ChessStringsConf.instance().getProperty( ChessStringsConf.CONF_ERROR_UPDATING_ADD_INFO ) + th ) );
		}

		textString = pgnWriter.getGameMovesString(game, null);
//		if( textString != null ) sb.append( textString );

		return( _tmpSb.toString() );
	}

	@Override
	public void writeLine_pgnWriter( String line )
	{
		if( _writer != null )
		{
			try
			{
				_writer.write( line );
				_writer.newLine();
			}
			catch( IOException ex )
			{
				ex.printStackTrace();
				_lastError = ex.getMessage();
			}
		}
		else if( _tmpSb != null )
		{
			_tmpSb.append( line );
			_tmpSb.append( System.getProperty( "line.separator" ) );
		}
	}

	protected static class PGNchessViewConfiguration implements ChessViewConfiguration
	{
		protected static PGNchessViewConfiguration _instance = null;

		public static PGNchessViewConfiguration instance()
		{
			if( _instance == null )
				_instance = new PGNchessViewConfiguration();

			return( _instance );
		}

		@Override
		public boolean getHasToShowComments()
		{
			return( true );
		}

		@Override
		public void setHasToShowComments(boolean value)
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean getHasToShowNAGs()
		{
			return( true );
		}

		@Override
		public void setHasToShowNAGs(boolean value)
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public ChessLanguageConfiguration getChessLanguageConfigurationToShow()
		{
			return( ChessLanguageConfiguration.getConfiguration( ChessLanguageConfiguration.ENGLISH ) );
		}

		@Override
		public ChessLanguageConfiguration getChessLanguageConfigurationToParseTextFrom()
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public String getConfigurationOfChessLanguageToShow()
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public String getConfigurationOfChessLanguageToParseTextFrom()
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setConfigurationOfChessLanguageToShow(String value)
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setConfigurationOfChessLanguageToParseTextFrom(String value)
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean getDetachedGameWindowsAlwaysOnTop()
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setDetachedGameWindowsAlwaysOnTop(boolean value)
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean getHasToShowSegments()
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean isAutocompletionForRegexActivated() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setIsAutocompletionForRegexActivated(boolean value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean getShowPdfGameWhenNewGameSelected() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setShowPdfGameWhenNewGameSelected(boolean value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	}

}
