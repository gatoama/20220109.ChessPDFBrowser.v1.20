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
package com.frojasg1.applications.pdf2pgn.commandline.args;

import com.frojasg1.applications.pdf2pgn.commandline.helpers.ScanGamesForCommandLineFunctions;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.args.ArgsExtractorBase;
import com.frojasg1.general.streams.InOutErrStreamFunctions;
import java.io.PrintStream;

/**
 *
 * @author fjavier.rojas
 */
public class Pdf2pgnArgsExtractor extends ArgsExtractorBase
{
    protected static final String INPUT_PDF_FILENAME = "-inputFilename";
    protected static final String NUM_THREADS_FOR_BOARD_RECOGNITION = "-numThreadsForBoardRecognition";
    protected static final String CHESS_INPUT_LANGUAGE = "-chessInputLanguage";
    protected static final String LETTERS_FOR_PIECES = "-lettersForPieces";
    protected static final String OUTPUT_PGN_FILENAME = "-outputFilename";
    protected static final String INITIAL_PAGE_NUMBER = "-initialPageNumber";
    protected static final String FINAL_PAGE_NUMBER = "-finalPageNumber";
    protected static final String HELP = "-help";

    protected String _inputFileName = null;
    protected String _outputFileName = null;
    protected String _lettersForPieces = null;
    protected String _chessInputLanguage = null;
	protected Integer _numThreadsForBoardRecognition = 1;
	protected Integer _initialPageNumber = null;
	protected Integer _finalPageNumber = null;

	protected boolean _help = false;
	protected boolean _debug = false;


	public Pdf2pgnArgsExtractor( )
    {
        super( );
    }

	public String getHelpString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( String.format( "Input parameters: \n \"%s=%s\"  \"%s=%s\" %s=%s [ %s=%s | %s=%s ] " +
									"[ %s=%s ] [ %s=%s ] [ %s ] [ %s ]",
									INPUT_PDF_FILENAME, "pdfFilename",
									OUTPUT_PGN_FILENAME, "pgnFilename",
									NUM_THREADS_FOR_BOARD_RECOGNITION, "number",
									CHESS_INPUT_LANGUAGE, "chessGamesLanguage",
									LETTERS_FOR_PIECES, "lettersForPieces",
									INITIAL_PAGE_NUMBER, "number",
									FINAL_PAGE_NUMBER, "number",
											DEBUG, HELP ) );

		sb.append( "\n" ).append( "chessGamesLanguages allowed:\n" );
		for( ChessLanguageConfiguration.LanguageConfigurationData lc: ScanGamesForCommandLineFunctions.instance().getListOfLanguagesToParseFrom() )
			if( isValidLanguageConfiguration( lc ) )
				sb.append( "    - " ).append( lc._languageName ).append( "    ( equivalent lettersForPieces=" )
					.append( lc._stringOfPieceCodes ).append( " )\n" );

		return sb.toString();
	}

	public void help()
	{
		System.out.println( getHelpString() );
	}

	protected boolean isValidLanguageConfiguration( ChessLanguageConfiguration.LanguageConfigurationData lc )
	{
		return( ScanGamesForCommandLineFunctions.instance().isValidLanguageConfiguration( lc ) );
	}

	@Override
    public void process( String[] args )
    {
        try
        {
            super.process( args );

            _inputFileName = processString(INPUT_PDF_FILENAME, null );
            _outputFileName = processString(OUTPUT_PGN_FILENAME, "./output.pgn" );
			_chessInputLanguage = processString( CHESS_INPUT_LANGUAGE, null );
			_lettersForPieces = processString( LETTERS_FOR_PIECES, null );
			_numThreadsForBoardRecognition = processInteger( NUM_THREADS_FOR_BOARD_RECOGNITION, 1 );
			_initialPageNumber = processInteger(INITIAL_PAGE_NUMBER, null );
			_finalPageNumber = processInteger(FINAL_PAGE_NUMBER, null );

			_help = isPresentWithoutValue( HELP );
			_debug = isPresentWithoutValue( DEBUG );
		}
        catch( Exception ex )
        {
			ex.printStackTrace();
			throw( new RuntimeException( "Problems parsing input parameters. " + ex.getMessage() ) );
        }
    }

	protected PrintStream out()
	{
		return( InOutErrStreamFunctions.instance().getOriginalOutStream() );
	}

	public void validate()
	{
		checkValues();
		limitValues();
	}

	public void checkValues()
	{
		if( ( _inputFileName == null ) || !FileFunctions.instance().isFile(_inputFileName) )
			throw( new RuntimeException( "inputFilename: " + _inputFileName + " does not seem to be a valid filename" ) );

		if( ( _chessInputLanguage != null ) && !isValidLanguageConfigurationName( _chessInputLanguage ) )
			throw( new RuntimeException( "LanguageConfigurationName ( " + _chessInputLanguage + " ) not recognized" ) );
	}

	protected void limitValues()
	{
		if( ( _chessInputLanguage == null ) && ( _lettersForPieces == null ) )
			_chessInputLanguage = getDefaultChessInputLanguage();

		_numThreadsForBoardRecognition = Math.min( _numThreadsForBoardRecognition, 5 );
	}

	public boolean isValidLanguageConfigurationName( String chessGameInputLanguageName )
	{
		return( ScanGamesForCommandLineFunctions.instance().isValidLanguageConfigurationName( chessGameInputLanguageName ) );
	}

	public String getDefaultChessInputLanguage()
	{
		return( ScanGamesForCommandLineFunctions.instance().getDefaultChessInputLanguage() );
	}

	public boolean helpPresent()
	{
		return( _help );
	}

	public String getInputFileName() {
		return _inputFileName;
	}

	public String getOutputFileName() {
		return _outputFileName;
	}

	public String getLettersForPieces() {
		return _lettersForPieces;
	}

	public String getChessInputLanguage() {
		return _chessInputLanguage;
	}

	public Integer getNumThreadsForBoardRecognition() {
		return _numThreadsForBoardRecognition;
	}

	public Integer getInitialPageNumber() {
		return _initialPageNumber;
	}

	public Integer getFinalPageNumber() {
		return _finalPageNumber;
	}

	public boolean isHelp() {
		return _help;
	}

	public boolean isDebug() {
		return _debug;
	}
}
