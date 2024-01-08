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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessModelException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.io.notation.ChessMoveAlgebraicNotation;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.ChessGameParser;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.AttributeToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.MoveToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.Token;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.TokenId;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.PDFPageSegmentatorInterface;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.progress.CancellationException;
import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class PGNChessGameParser implements ChessGameParser
{
	protected boolean _isParsingString = false;
	protected boolean _isParsingReader = false;

	protected PGNLexicalAnalyser _lexicalAnalyser = null;

//	protected ChessMoveAlgebraicNotation _moveMatcherAndMoveNotation = null;
	
	protected ChessLanguageConfiguration _chessLanguageConfiguration = null;

	protected LinkedList<Token> _aheadTokenList = null;

	protected Token _nextToken = null;

	public PGNChessGameParser( ChessLanguageConfiguration clc )
	{
		_chessLanguageConfiguration = clc;
//		_moveMatcherAndMoveNotation = new ChessMoveAlgebraicNotation( clc );
	}

	public PGNChessGameParser()
	{
		this( ChessLanguageConfiguration.getConfiguration(ChessLanguageConfiguration.ENGLISH) );
	}

	protected void checkAlreadyParsing() throws ChessParserException
	{
		if( _isParsingString || _isParsingReader )
			throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_ALREADY_PARSING ) ) );
	}

	@Override
	public List<ChessGame> parseChessGameText( String text, PDFPageSegmentatorInterface source,
												Integer initialPageToScanForGames,
												Integer finalPageToScanForGames ) throws ChessParserException, CancellationException
	{
		List<ChessGame> result = new LinkedList<ChessGame>();

		checkAlreadyParsing();
		_isParsingString = true;

		if( source == null )
		{
			_lexicalAnalyser = new PGNLexicalAnalyser( text, _chessLanguageConfiguration );
			result = processInput();
		}
		else
		{
			throw (new ChessParserException("PGNChessGameParser.parseChessGameText( String text, PDFPageSegmentatorInterface source ) " +
											getChessStrConf().getProperty( ChessStringsConf.CONF_NOT_IMPLEMENTED ) ));
		}

		return( result );
	}

	protected ChessGameMove createChessGameMove( ChessGame cg, MoveToken moveToken )
	{
		return( ExecutionFunctions.instance().safeFunctionExecution( () -> getChessGameMove( cg, moveToken ) ) );
	}

	@Override
	public List<ChessGame> parseChessGameText( BufferedReader reader) throws ChessParserException
	{
		List<ChessGame> result = null;

		checkAlreadyParsing();
		_isParsingReader = true;

		_lexicalAnalyser = new PGNLexicalAnalyser( reader, _chessLanguageConfiguration );

		result = processInput();

		return( result );
	}

	public void stopParsing()
	{
		_isParsingString = false;
		_isParsingReader = false;
	}

	public List<ChessGame> processInput() throws ChessParserException
	{
		List<ChessGame> result = new LinkedList<ChessGame>();
		_aheadTokenList = new LinkedList<Token> ();
		_nextToken = null;

		try
		{
			while( hasNext() && _nextToken.getTokenId().equals( TokenId.BLANK_LINE ) )		// we skip the first blank lines.
				next();

			while( hasNext() )
			{
				while( hasNext() && _nextToken.getTokenId().equals( TokenId.BLANK_LINE ) )
				{
					next();
				}

				if( hasNext() )
				{
					ChessGame game = readChessGame();
					result.add( game );
				}
			}
		}
		catch (Throwable th)
		{
			th.printStackTrace();
			throw( new ChessParserException( th.toString() ) );
		}

		return( result );
	}

	public boolean hasNext() throws ChessParserException, CancellationException
	{
		boolean result = false;

		if( _nextToken == null )
			next();

		result = ( ( _nextToken != null ) && ( ! _nextToken.getTokenId().equals( TokenId.EOF ) ) );

		return( result );
	}

	public Token next() throws ChessParserException, CancellationException
	{
		_nextToken = null;
		if( _aheadTokenList.size() > 0 )
		{
			_nextToken = _aheadTokenList.removeFirst();
		}
		else
		{
			_nextToken = _lexicalAnalyser.next();
		}

		return( _nextToken );
	}

	public void giveBack( Token token )
	{
		_aheadTokenList.addFirst( token );
		_nextToken = null;
	}

	public ChessGame readChessGame() throws ChessParserException, ChessModelException, CancellationException
	{
		ChessGameHeaderInfo cghi = readChessGameHeader();

		ChessGamePosition cgp = null;
		String fenString = cghi.get( ChessGameHeaderInfo.FEN_TAG );

		String isSetUp = cghi.get( ChessGameHeaderInfo.SETUP_TAG );

		if( ( fenString != null ) && ( isSetUp != null ) && ( isSetUp.equals( "1" ) ) )
			cgp = new ChessGamePosition( fenString );

		MoveTreeGame mtg = new MoveTreeGame( cgp );

		ChessGame result = new ChessGame( cghi, mtg, null );

		try
		{
			parseVariation( mtg, result, true );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		if( ( ( mtg.getNumberOfChildren() == 0 ) || ( mtg.getChild(0).getLevel() == 1 ) ) &&
			( result.getInitialPosition() == null ) )
		{
			result.setInitialPosition( ChessGamePosition.getInitialPosition() );
		}

		result.start();

		return( result );
	}

	protected void skipUntilNextGame() throws ChessParserException, CancellationException
	{
		while( hasNext() )
		{
			if( _nextToken.getTokenId().equals( TokenId.ATTRIBUTE ) )
				break;

			next();
		}
	}

	protected ChessGameHeaderInfo readChessGameHeader() throws ChessParserException, CancellationException
	{
		ChessGameHeaderInfo result = new ChessGameHeaderInfo();

		skipUntilNextGame();
		while( _nextToken.getTokenId().equals( TokenId.ATTRIBUTE ) )
		{
			if( _nextToken instanceof AttributeToken )
			{
				AttributeToken at = (AttributeToken) _nextToken;
				result.put( at.getAttributeName(), at.getValue() );
				next();
			}
			else
				throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_TOKEN_DIFFERENT_FROM_ATTRIBUTE_TOKEN ) ) );
		}
		result.setPdfBaseFileName( result.get( ChessGameHeaderInfo.FILE_NAME_TAG ) );
		result.setControlName(result.get( ChessGameHeaderInfo.CONTROL_NAME_TAG ) );

		return( result );
	}

	protected String getParticularCommentContent( String prefix, String comment )
	{
		String result = null;

		if( comment.startsWith(prefix) )
			result = comment.substring( prefix.length() );

		return( result );
	}

	protected void insertMoves( ChessGame cg, List<ChessGameMove> listOfMoves ) throws ChessParserException
	{
		try
		{
			cg.insertMoves(listOfMoves);
		}
		catch( Throwable th )
		{
			throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_ERROR_INSERTING_MOVES ) ) );
		}
	}

	protected void doMoves( ChessGame cg, List<ChessGameMove> listOfMoves ) throws ChessParserException
	{
		if( cg.isValid() )
		{
			try
			{
				cg.doMovesFromInitialPosition(listOfMoves);
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				throwChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_ERROR_DOING_MOVES ), cg, listOfMoves );
			}
		}
	}

	protected void parseVariation( MoveTreeNode mtn, ChessGame cg, boolean isMainLine ) throws ChessParserException, CancellationException
	{
		LinkedList<ChessGameMove> listOfMoves = mtn.getGameMoveList();
		doMoves( cg, listOfMoves );

		int currentPly = mtn.getLevel();  // level is identified with the ply number.

		String commentForVariant = null;

		MoveTreeNode currentLineNode = mtn;
		while( hasNext() )
		{
			if( _nextToken.getTokenId().equals( TokenId.BLANK_LINE ) )
			{
				next();
			}
			else if( _nextToken.getTokenId().equals( TokenId.MOVE ) )
			{
				if( _nextToken instanceof MoveToken )
				{
					MoveToken moveToken = (MoveToken) _nextToken;

					if( //(currentPly!=0) &&
						!( mtn instanceof MoveTreeGame ) &&
						( moveToken.getNumberOfPly() != (currentPly + 1) ) )
					{
/*
						if( ( currentPly + 2 ) == moveToken.getNumberOfPly() )
						{
							System.out.println( "Discarding move." );
							next();
							continue;
						}
						else  */
							throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_NUMBER_OF_PLY_NOT_VALID ) +
															moveToken.getNumberOfPly() ) );
					}
					currentPly = moveToken.getNumberOfPly();

					ChessGameMove cgm = getChessGameMove( cg, moveToken );
//					listOfMoves.add( cgm );
//					insertMoves( cg, listOfMoves );
//					doMoves( cg, listOfMoves );

//					currentLineNode = cg.getCurrentMove();

					currentLineNode = simpleInsert( currentLineNode, cgm, currentPly );

					// if the move is not legal, we will mark it in red background.
//					doMove( cg, cgm, moveToken );

					if( moveToken.getComment() != null )
					{
						currentLineNode.setComment( moveToken.getComment() );
					}

					if( commentForVariant != null )
					{
						currentLineNode.setCommentForVariant( commentForVariant );
					}

//					currentLineNode.getMove().addAllNAGs( moveToken.nagIterator() );

					next();
				}
				else
				{
					throwChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_INTERNAL_ERROR ), cg, null );
				}
			}
			else if( _nextToken.getTokenId().equals( TokenId.OPEN_BRACKET ) )
			{
				if( currentLineNode == null )
					throwChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_NO_MAIN_MOVE ), cg, null );

				parseVariationBetweenBrackets( currentLineNode.getParent(), cg );

//				logMoveList( cg, currentLineNode.getGameMoveList() );

				doMoves( cg, currentLineNode.getGameMoveList() );
			}
			else if( _nextToken.getTokenId().equals( TokenId.CLOSE_BRACKET ) )
			{
				break;
			}
			else if( _nextToken.getTokenId().equals( TokenId.ATTRIBUTE ) )
			{
				break;
			}
			else if( _nextToken.getTokenId().equals( TokenId.RESULT ) )
			{
				ChessGameMove cgm = currentLineNode.getMove();

				if( cgm != null )
					cgm.setResultOfGame( _nextToken.getString() );

				next();
				if( isMainLine )	break;
			}
			else if( _nextToken.getTokenId().equals( TokenId.COMMENT ) )
			{
				commentForVariant = _nextToken.getString();
				next();
				continue;
			}
			else
			{
				throwChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_TOKEN_NOT_EXPECTED )+
											Token.tokenIdToString( _nextToken.getTokenId() ),
											cg, null );
			}

			commentForVariant = null;
		}
	}

	public MoveTreeNode simpleInsert(MoveTreeNode currentLineNode, ChessGameMove cgm,
										int numberOfPly )
	{
		return( currentLineNode.simpleInsert( cgm, numberOfPly ) );
	}

	public MoveTreeNode insertFirst(MoveTreeNode currentLineNode, ChessGameMove cgm,
										int numberOfPly )
	{
		return( currentLineNode.insertFirst( cgm, numberOfPly ) );
	}

	protected void parseVariationBetweenBrackets( MoveTreeNode mtn, ChessGame cg ) throws ChessParserException, CancellationException
	{
		checkTokenExpected( TokenId.OPEN_BRACKET );
		next();

		parseVariation( mtn, cg, false );

		checkTokenExpected( TokenId.CLOSE_BRACKET );
		next();
	}

	protected void checkTokenExpected( TokenId tokenId ) throws ChessParserException, CancellationException
	{
		if( ! hasNext() || !_nextToken.getTokenId().equals( tokenId ) )
		{
			throw( new ChessParserException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_TOKEN_EXPECTED_TEXT ),
											Token.tokenIdToString(tokenId),
											_nextToken.getLineNumber(),
											_nextToken.getInitialPosition() ) ) );
		}
	}

	protected ChessGameMove getChessGameMove( ChessGame cg, MoveToken moveToken ) throws ChessParserException
	{
//		ChessBoard cb = cg.getChessBoard();
		ChessBoard cb = null;

		return( ChessMoveAlgebraicNotation.getInstance().getChessGameMove( cb, moveToken ) );
	}

	protected void logMoveList( ChessGame cg, List<ChessGameMove> listOfMoves )
	{
		try
		{
			System.out.println( cg.getMoveListString( listOfMoves ) );
		}
		catch( Throwable th1 )
		{
			System.out.println( "Error logging move list" );
			th1.printStackTrace();
		}
	}
	
	protected void throwChessParserException( String message, ChessGame cg, List<ChessGameMove> listOfMoves ) throws ChessParserException
	{
		System.out.print( "Error doing moves: " );
		logMoveList( cg, listOfMoves );

		throw( new ChessParserException( message ) );
	}
	
	protected void doMove( ChessGame cg, ChessGameMove cgm, MoveToken moveToken ) throws ChessParserException
	{
		if( cg.isValid() )
		{
			try
			{
				cg.getChessBoard().move(cgm);
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				throwChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_ERROR_DOING_MOVE ) +
											" " + moveToken.getString(), cg, null );
			}
		}
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}
}
