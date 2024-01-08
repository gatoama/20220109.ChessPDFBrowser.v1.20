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
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.TokenId;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.Token;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.io.notation.ChessMoveAlgebraicNotation;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.AttributeToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.ImageToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.MoveToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.PreToken;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.NAG;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.SegmentKey;
import com.frojasg1.general.number.IntegerReference;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.string.StringFunctions;
import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Usuario
 */
public class PGNLexicalAnalyser
{
	protected static Pattern _newLinePattern;
//	protected static Pattern _newPreTokenPattern;

	protected Matcher _newLineMatcher = null;
//	protected Matcher _newPreTokenMatcher = null;

	protected BufferedReader _readerToParse = null;
	protected String _stringToParse = null;

//	ChessMoveMatcher _chessMoveMatcher = null;
	protected ChessLanguageConfiguration _chessLanguageConfiguration = null;

	protected String _currentLine = null;
	protected int _currentPosInLine = 0;
	protected int _currentLineNumber = 0;

	protected LinkedList<Token> _bufferOfOutputTokens = null;
	protected LinkedList<PreToken> _bufferOfInputPreTokens = null;
	
	protected boolean _isEOF = false;

	protected Boolean _hasToBeStrict = null;
	protected Boolean _hasToNotifyReturns = null;

	protected PreToken _lastCreatedPreToken = null;

//	boolean _hasToNotifyBlankLines = true;

	protected BlankLineNotficationEnum _blankLineNotification = BlankLineNotficationEnum.TO_OUTPUT;

	static
	{
		try
		{
			_newLinePattern = Pattern.compile( "([^\n\r]*)(\r\n|\r|\n|$)" );
//			_newPreTokenPattern = Pattern.compile( "([^\\s]*)([\\s]+|\\s*$)" );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public PGNLexicalAnalyser( String stringToParse, ChessLanguageConfiguration chessLanguageConfiguration )
	{
		this( chessLanguageConfiguration );
		setStringToParse( stringToParse );
		setBlankLineNotification( BlankLineNotficationEnum.TO_OUTPUT );
	}

	public void setStringToParse( String stp )
	{
//		System.out.println( "setStringToParse  ----> " + stp );
		_stringToParse = stp;
		_newLineMatcher = _newLinePattern.matcher( _stringToParse );
	}

	public void setBlankLineNotification( BlankLineNotficationEnum value )
	{
		_blankLineNotification = value;
	}

	public PGNLexicalAnalyser( BufferedReader reader, ChessLanguageConfiguration chessLanguageConfiguration )
	{
		this( chessLanguageConfiguration );
		_readerToParse = reader;
	}

	/**
	 * If this constructor is invoked, then the string to parse must be set before starting the lexical analyser.
	 * @param cmm 
	 */
	public PGNLexicalAnalyser( ChessLanguageConfiguration chessLanguageConfiguration )
	{
		_chessLanguageConfiguration = chessLanguageConfiguration;

		_bufferOfOutputTokens = new LinkedList<Token>();
		_bufferOfInputPreTokens = new LinkedList<PreToken>();
	}

	public void setHasToBeStrict( boolean hasToBeStrict )
	{
		_hasToBeStrict = hasToBeStrict;
	}
	
	public void setHasToNotifyReturns( boolean hasToNotifyReturns )
	{
		_hasToNotifyReturns = hasToNotifyReturns;
	}
	
	protected String nextLine()
	{
		String result = null;
		try
		{
			_currentPosInLine = 0;

			if( _readerToParse != null )	result = _readerToParse.readLine();
			else if( _stringToParse != null )
			{
				if( _newLineMatcher.find() )
				{
					if( _newLineMatcher.groupCount() == 2 )
					{
						result = _newLineMatcher.group( 1 );
					}
				}
			}
			_currentLineNumber++;
		}
		catch( Throwable th )
		{}

		return( result );
	}

	protected boolean isBlank( char currentChar )
	{
		return( ( currentChar == ' ' ) || ( currentChar == '\t' ) );
	}

	protected boolean isEndOfPreToken( char currentChar )
	{
		boolean result = isBlank( currentChar );

		if( ! result )
		{
			switch( currentChar )
			{
				case ' ':
				case '{':
				case '}':
				case '.':
				case '(':
				case ')':
				case '\t':
				case ';':
					result = true;
			}
		}

		return( result );
	}

	protected boolean isANumber( String text )
	{
		boolean result = false;
		try
		{
			Integer.parseInt( text );
			result = true;
		}
		catch( Throwable th )
		{
		}
		return( result );
	}
	
	protected boolean isAMove( String text )
	{
		boolean result = false;

		if( text != null )
		{
			if( text.endsWith( "TN" ) )		// TN novelty
				text = text.substring( 0, text.length() - 2 );
			else if( text.endsWith( "N" ) )	// N novelty
				text = text.substring( 0, text.length() - 1 );
		}

		String translatedText = _chessLanguageConfiguration.translateMoveStringToEnglish(text, null);
		result = ChessMoveAlgebraicNotation.getInstance().isItAChessMoveString(translatedText);

		return( result );
	}

	protected boolean isAResult( String text )
	{
		boolean result = ( text.equals( "*" ) ||
							text.equals( "1-0" ) ||
							text.equals( "0-1" ) ||
							text.equals( "1/2-1/2" ) ||
							text.equals( "½-½" ) ||

		// with minus UTF
							text.equals( "1–0" ) ||
							text.equals( "0–1" ) ||
							text.equals( "1/2–1/2" ) ||
							text.equals( "½–½" )
			);

		return( result );
	}

	protected boolean isANag( String text )
	{
		return( ( text.length() > 0 ) && ( text.charAt(0) == '$' ) );
	}

	protected TokenId getTokenId_simple( String preTokenStr )
	{
		TokenId result = null;

		if( preTokenStr.equals( "." ) )			result = TokenId.DOT;
		else if( preTokenStr.equals( "(" ) )	result = TokenId.OPEN_BRACKET;
		else if( preTokenStr.equals( ")" ) )	result = TokenId.CLOSE_BRACKET;
		else if( preTokenStr.equals( "{" ) )	result = TokenId.OPEN_BRACE;
		else if( preTokenStr.equals( "}" ) )	result = TokenId.CLOSE_BRACE;
		else if( isANumber( preTokenStr ) )		result = TokenId.NUMBER;
		else if( isAMove( preTokenStr ) )		result = TokenId.MOVE;
		else if( isAResult( preTokenStr ) )		result = TokenId.RESULT;
		else if( isANag( preTokenStr ) )		result = TokenId.NAG;

		return( result );
	}

	protected boolean hasToNotifyReturns()
	{
		boolean result = false;
		if( _hasToNotifyReturns != null ) result = _hasToNotifyReturns;

		return( result );
	}

	protected boolean hasToBeStrict()
	{
		boolean result = true;
		if( _hasToBeStrict != null ) result = _hasToBeStrict;

		return( result );
	}

	protected TokenId getTokenId( String preTokenStr ) throws ChessParserException
	{
		TokenId result = getTokenId_simple( preTokenStr );

		if( result == null )
		{
			if( hasToBeStrict() ) 
			{
				throw( new ChessParserException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_TOKEN_NOT_IDENTIFIED ),
												preTokenStr,
												_currentLineNumber,
												(_currentPosInLine - preTokenStr.length())  )          ) );
			}
			else
			{
				result = TokenId.STRING;
			}
		}

		return( result );
	}

	protected PreToken readCommentBetweenBraces() throws ChessParserException
	{
		PreToken result = null;
		
		if( _currentLine.charAt( _currentPosInLine ) != '{' )
		{
			throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_NOT_AN_OPEN_SQUARE_BRACE ) ) );
		}

		boolean success = false;
		_currentPosInLine++;
		int lineNumber = _currentLineNumber;
		int initialPosition = _currentPosInLine;
		StringBuilder sb = new StringBuilder();
		do
		{
			int initialPositionInLine = _currentPosInLine;
			int posClose = _currentLine.indexOf( "}", _currentPosInLine );
			int posOpen = _currentLine.indexOf( "{", _currentPosInLine );
			if( ( posClose >= 0 ) && ( ( posOpen < 0 ) || ( posOpen > posClose ) ) ) // if found close brace and not open brace, or open brace is after close brace, then we get the comment
			{
				_currentPosInLine = posClose+1;
				sb.append( _currentLine.substring( initialPositionInLine, posClose ) );
//				_bufferOfInputPreTokens.add( createPreToken( TokenId.COMMENT, sb.toString(), lineNumber, initialPosition ) );  // adding comment excluding the braces
				result = createPreToken( TokenId.COMMENT, sb.toString(), lineNumber, initialPosition,
										null );  // adding comment excluding the braces
				success = true;
				break;
			}
			else if( posOpen < 0 )	// the case in which no open brace was found.
			{
				sb.append( _currentLine.substring( initialPositionInLine ) + " " );
				_currentLine = null;	// we prepare for the getNewLine function
			}
			else
			{
				throw( new ChessParserException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_ANOTHER_OPEN_SQUARE_BRACE_FOUND ),
												lineNumber,
												initialPosition  ) ) );
			}
		}
		while( getNewLineAndThereWasANewLine() );

		if( ! success )
			throw( new ChessParserException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_CLOSING_OF_COMMNENT_NOT_FOUND ),
												lineNumber,
												initialPosition  ) ) );

		return( result );
	}

	protected boolean getNewLineAndThereWasANewLine() throws ChessParserException
	{
		getNewLine();

		return( _currentLine != null );
	}

	protected boolean isCastleKingSide( String moveStr )
	{
		return( ChessMoveAlgebraicNotation.getInstance().isCastleKingSide(moveStr) );
	}

	protected boolean isCastleQueenSide( String moveStr )
	{
		return( ChessMoveAlgebraicNotation.getInstance().isCastleQueenSide(moveStr) );
	}

	protected String translateMoveStrToStandard( String moveStr )
	{
		String result = moveStr;
		if( isCastleKingSide(moveStr) )
			result = "O-O";
		else if( isCastleQueenSide(moveStr) )
			result = "O-O-O";

		return( result );
	}

	protected PreToken createPreToken( TokenId tokenId, String text, int lineNumber, int initialPosition,
										SegmentKey segmentId )
	{
		String standardText = translateMoveStrToStandard(text);

		_lastCreatedPreToken = new PreToken( tokenId, standardText, lineNumber, initialPosition, segmentId );

		return( _lastCreatedPreToken );
	}

	protected String getNextPreTokenStringInCurrentLine() throws ChessParserException
	{
		String result = null;

		if( ( _currentLine != null ) && ( _currentPosInLine < _currentLine.length() ) )
		{
			while( ( _currentPosInLine < _currentLine.length() ) && isBlank( _currentLine.charAt( _currentPosInLine ) ) )
			{
				_currentPosInLine++;
			}

			if( _currentPosInLine < _currentLine.length() )	// if we have not reached the end of line, we continue
			{
				int initialPosition = _currentPosInLine;
				boolean isInitialCharEndOfPreToken = isEndOfPreToken( _currentLine.charAt(initialPosition) );

				while(	!isInitialCharEndOfPreToken &&
						( _currentPosInLine < _currentLine.length() - 1 ) &&
						! isEndOfPreToken( _currentLine.charAt(_currentPosInLine + 1) )
					)
				{
					_currentPosInLine++;
				}

				result = _currentLine.substring( initialPosition, _currentPosInLine + 1 );
			}
		}

		return( result );
	}

	protected PreToken getPreTokenFromPreTokenString( String preTokenStr ) throws ChessParserException
	{
		PreToken result = null;

		if( preTokenStr != null )
		{
			int iniPosOfLine = ( _newLineMatcher != null ? _newLineMatcher.start() : 0 );

			if( preTokenStr.equals( ";" ) ) // comment to the end of line. We ignore the rest of the line
			{
				preTokenStr = _currentLine.substring( _currentPosInLine + 1 );
				_currentPosInLine = _currentLine.length();
			}
			else
			{
				if( preTokenStr.equals( "{" ) )
				{
					result = readCommentBetweenBraces();
					// unless an exception is thrown by the previous call, the comment between braces will have been read.
				}

				if( result == null )
				{
					TokenId tokenId = getTokenId( preTokenStr );

//				_bufferOfInputPreTokens.add( new PreToken( tokenId, preTokenStr, _currentLineNumber, iniPosOfLine + _currentPosInLine - preTokenStr.length() ) );
					result = createPreToken( tokenId, preTokenStr, _currentLineNumber,
											iniPosOfLine + _currentPosInLine - preTokenStr.length() + 1,
											null );
					_currentPosInLine++;
				}
			}
		}

		return( result );
	}

	protected PreToken getNextPreTokenInCurrentLine() throws ChessParserException
	{
		PreToken result = null;

		String nextPreTokenStr = getNextPreTokenStringInCurrentLine();
		result = getPreTokenFromPreTokenString( nextPreTokenStr );

		return( result );
	}

	protected boolean checkIsCommentLine( )
	{
		boolean result = false;
		if( ( _currentLine != null ) &&
			( _currentLine.length() > 0 ) &&
			( _currentLine.charAt(0) == '#' ) )
		{
			_currentLine = null;		// we ignore the whole line
			result = true;
		}
		return( result );
	}

	protected boolean checkIsAttribute( ) throws ChessParserException
	{
		boolean result = false;
		if( ( _currentLine != null ) &&
			( _currentLine.length() > 2 ) &&
			( _currentLine.charAt(0) == '[' ) &&
			( _currentLine.charAt(_currentLine.length()-1) == ']' ) )
		{
			String str = _currentLine.substring( 1, _currentLine.length() - 1 ).trim();

			String[] array = str.split( "\"" );
			if( ( array.length > 0 ) &&
				( array[0].charAt( array[0].length() - 1 ) == ' ' )
			  )
			{
				emptyBufferOfInputStrings();

				AttributeToken token = new AttributeToken();
				token.setString( array[0] );
				token.setAttributeName(array[0].trim() );
				if( array.length>1 )
					token.setValue( array[1] );
				else
					token.setValue( "" );

				token.setLineNumber( _currentLineNumber );

				_bufferOfOutputTokens.add( token );
				_currentLine = null;
				result = true;
			}
		}
		return( result );
	}

	protected boolean hasToNotifyBlankLines()
	{
		return( ( _blankLineNotification != null ) &&
			!_blankLineNotification.equals( BlankLineNotficationEnum.NONE ) );
	}

	protected boolean checkIsBlackLine( ) throws ChessParserException
	{
		boolean result = false;
		if( hasToNotifyBlankLines() &&
			( _currentLine != null ) &&
			( StringFunctions.instance().removeAllCharacters(_currentLine, " \t\n\r").length() == 0 )  )
		{
			if( _blankLineNotification.equals( BlankLineNotficationEnum.TO_OUTPUT ) )
			{
				emptyBufferOfInputStrings();

				Token token = new Token();
				token.setTokenId(TokenId.BLANK_LINE);
				token.setString( _currentLine );

				token.setLineNumber( _currentLineNumber );

				_bufferOfOutputTokens.add( token );
			}
			else if( _blankLineNotification.equals( BlankLineNotficationEnum.TO_INPUT ) )
			{
				PreToken preToken = createPreToken( TokenId.BLANK_LINE, _currentLine, _currentLineNumber, 0, null );

				_bufferOfInputPreTokens.add( preToken );
			}

			_currentLine = null;
			result = true;
		}
		return( result );
	}

	// this functions sets _currentLine class attribute.
	// it returns a PreToken, in case it is met before the new line (as for example ImagePretoken, in case of Pdf parser).
	protected PreToken getNewLine() throws ChessParserException
	{
		while( (_currentLine == null) && !_isEOF )
		{
			_currentLine = nextLine();

			if( _currentLine != null )
			{
				boolean found = false;
				if( !found )
					found = checkIsAttribute( );
				if( !found )
					found = checkIsCommentLine( );
				if( !found )
					found = checkIsBlackLine( );
			}
			else
			{
				_isEOF = true;
			}
		}

		if( _currentLine != null )
		{
			_currentPosInLine = 0;
		}

		return( null );
	}

	protected void getNewPreToken() throws ChessParserException, CancellationException
	{
		PreToken pt = null;
		while( !_isEOF && (pt==null) )
		{
			if( _currentLine == null )
			{
				pt = getNewLine();
			}

			if( !_isEOF )
			{
				pt = getNextPreTokenInCurrentLine();

				if( pt != null )
				{
					_bufferOfInputPreTokens.add( pt );
				}

				if( _currentPosInLine >= _currentLine.length() )
				{
					_currentLine = null;
					if( hasToNotifyReturns() )
					{
						pt = createPreToken( TokenId.RETURN, "\r", _currentLineNumber, _currentPosInLine, null );
						_bufferOfInputPreTokens.add( pt );
					}
				}

			}
		}
	}

	public Token next() throws ChessParserException, CancellationException
	{
		Token result = null;

		while(  ( result == null ) &&
			   !( _isEOF &&
					( _bufferOfOutputTokens.size() == 0 ) &&
					( _bufferOfInputPreTokens.size() == 0 )
				)
			)
		{
			if( _bufferOfOutputTokens.size() == 0 )
			{
				getNewPreToken();

				processNewPreToken();
			}

			if( _bufferOfOutputTokens.size() > 0 )
			{
				result = _bufferOfOutputTokens.removeFirst();
			}

			if( ( result == null ) && _isEOF &&
				!( (_bufferOfOutputTokens.size()>0 ) &&
					_bufferOfOutputTokens.getLast().getTokenId().equals( TokenId.EOF )
				 )
			  )
			{
				emptyBufferOfInputStrings();

				Token eof = new Token( TokenId.EOF );
				_bufferOfOutputTokens.add( eof );
			}
		}

		return( result );
	}

	protected void processNewPreToken( ) throws ChessParserException
	{
		boolean hasToEmptyBuffer = false;
		processBufferOfInputPreTokens( hasToEmptyBuffer );
	}

	protected void emptyBufferOfInputStrings() throws ChessParserException
	{
		boolean hasToEmptyBuffer = true;
		processBufferOfInputPreTokens( hasToEmptyBuffer );
		_bufferOfInputPreTokens.clear();
	}

	protected void processBufferOfInputPreTokens( boolean hasToEmptyBuffer ) throws ChessParserException
	{
		boolean hasToProcess = ( _bufferOfInputPreTokens.size() > 0 );

		while( hasToProcess )
		{
			PreToken preToken = _bufferOfInputPreTokens.getFirst();
			Token token = null;

			if( preToken.getTokenId().equals( TokenId.NUMBER ) )
			{
				token = createTokensForMove( preToken, hasToEmptyBuffer );
			}
			else
			{
				token = processOtherPreTokenDifferentFromAMove( preToken );
				_bufferOfOutputTokens.add(token);

				_bufferOfInputPreTokens.removeFirst();
			}

			hasToProcess = ( ( token != null ) && ( _bufferOfInputPreTokens.size() > 0 ) );
		}
	}

	public int getLineNumberOfLastReadPretoken()
	{
		int result = -1;
		if( _lastCreatedPreToken != null )
			result = _lastCreatedPreToken.getLineNumber();

		return( result );
	}

	public int getNumberOfPendingPreTokensOrTokensWithLineNumberLessThanLastCreatedPretoken()
	{
		int lineNumber = getLineNumberOfLastReadPretoken();
		int result = 0;
		for( Token token: _bufferOfOutputTokens )
			if( token.getLineNumber() < lineNumber )
				result++;

		for( PreToken preToken: _bufferOfInputPreTokens )
			if( preToken.getLineNumber() < lineNumber )
				result++;

		return( result );
	}

	protected Token processOtherPreTokenDifferentFromAMove( PreToken preToken )
	{
		Token result = new Token( preToken );

		return( result );
	}

	protected Token createTokensForMove( PreToken preToken, boolean hasToEmptyBuffer ) throws ChessParserException
	{
		Token result = null;

		int moveNumber = Integer.parseInt( preToken.getString() );

		boolean[] optional1 = { false, true, false, true, true, false, true, true };
		boolean[] optional3 = { false, false, false, false, true, false, true, true };
		TokenId[] case1 = { TokenId.NUMBER, TokenId.DOT, TokenId.MOVE, TokenId.NAG, TokenId.COMMENT, TokenId.MOVE, TokenId.NAG, TokenId.COMMENT };
		TokenId[] case2 = { TokenId.NUMBER, TokenId.DOT, TokenId.MOVE, TokenId.NAG, TokenId.COMMENT };
		TokenId[] case3 = { TokenId.NUMBER, TokenId.DOT, TokenId.DOT, TokenId.DOT, TokenId.DOT, TokenId.MOVE, TokenId.NAG, TokenId.COMMENT };

		ResultOfCheckMoveTokenSequence resultTS = new ResultOfCheckMoveTokenSequence();

		if( matchTokenIdsForMove( case1, optional1, hasToEmptyBuffer, resultTS ).getResultListOfPreTokens() != null )
		{
			result = createCase1Move( moveNumber, resultTS, hasToEmptyBuffer );
		}
		else if( (!resultTS.getHasNotFailed() || hasToEmptyBuffer ) &&
				( matchTokenIdsForMove( case2, optional1, hasToEmptyBuffer, resultTS ).getResultListOfPreTokens() != null ) )
		{
			result = createCase2Move( moveNumber, resultTS, hasToEmptyBuffer );
		}
		else if( (!resultTS.getHasNotFailed() || hasToEmptyBuffer ) &&
					( matchTokenIdsForMove( case3, optional3, hasToEmptyBuffer, resultTS ).getResultListOfPreTokens() != null ) )
		{
			result = createCase3Move( moveNumber, resultTS, hasToEmptyBuffer );
		}
		else if( hasToBeStrict() && !resultTS.getHasNotFailed() )
		{
			throw( new ChessParserException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_NUMBER_NOT_FOLLOWED_BY_VALID_MOVE ),
												preToken.getLineNumber(),
												preToken.getString()  )   ) );
		}

		if( result != null )
		{
			for( int ii=0; ii<resultTS.getNumberOfInputItemsToErase()._value; ii++ )
				_bufferOfInputPreTokens.removeFirst();
		}

		return( result );
	}

	// 		TokenId[] case1 = { TokenId.NUMBER, TokenId.DOT, TokenId.MOVE, TokenId.COMMENT, TokenId.MOVE };
	protected Token createCase1Move( int moveNumber, ResultOfCheckMoveTokenSequence rocmts, boolean hasToEmptyBuffer ) throws ChessParserException
	{
		Token result = null;
		
		LinkedList<PreToken> listOfPreTokens = rocmts.getResultListOfPreTokens();

		boolean isPossiblyMainLine = isFirstLineBlank( listOfPreTokens );
		int offset = isPossiblyMainLine ? 1 : 0;

		IntegerReference index = new IntegerReference(1 + offset);

		boolean whiteToPlay = true;
		MoveToken firstMove = getMoveTokenFromList( index, listOfPreTokens, moveNumber,
													whiteToPlay, hasToEmptyBuffer, isPossiblyMainLine );

		whiteToPlay = false;
		MoveToken secondMove = getMoveTokenFromList( index, listOfPreTokens, moveNumber,
													whiteToPlay, hasToEmptyBuffer, isPossiblyMainLine );

		if( ( firstMove != null ) && ( secondMove != null ) )
		{
			_bufferOfOutputTokens.add( firstMove );
			_bufferOfOutputTokens.add( secondMove );
			rocmts.setNumberOfInputItemsToErase(index);

			result = firstMove;
		}

		return( result );
	}

	//		TokenId[] case2 = { TokenId.NUMBER, TokenId.DOT, TokenId.MOVE };
	protected Token createCase2Move( int moveNumber, ResultOfCheckMoveTokenSequence rocmts, boolean hasToEmptyBuffer ) throws ChessParserException
	{
		Token result = null;

		LinkedList<PreToken> listOfPreTokens = rocmts.getResultListOfPreTokens();
		boolean isPossiblyMainLine = isFirstLineBlank( listOfPreTokens );
		int offset = isPossiblyMainLine ? 1 : 0;
	
		IntegerReference index = new IntegerReference(1 + offset);

		boolean whiteToPlay = true;
		MoveToken firstMove = getMoveTokenFromList( index, listOfPreTokens, moveNumber,
													whiteToPlay, hasToEmptyBuffer, isPossiblyMainLine );

		if( firstMove != null )
		{
			_bufferOfOutputTokens.add( firstMove );
			rocmts.setNumberOfInputItemsToErase(index);

			result = firstMove;
		}

		return( result );
	}

	protected boolean isFirstLineBlank( List<PreToken> pretokenList )
	{
		boolean result = false;

		if( !pretokenList.isEmpty() )
			result = pretokenList.get(0).getTokenId().equals( TokenId.BLANK_LINE );

		return( result );
	}

	//		TokenId[] case3 = { TokenId.NUMBER, TokenId.DOT, TokenId.DOT, TokenId.DOT, TokenId.DOT, TokenId.MOVE };
	protected Token createCase3Move( int moveNumber, ResultOfCheckMoveTokenSequence rocmts, boolean hasToEmptyBuffer ) throws ChessParserException
	{
		Token result = null;

		LinkedList<PreToken> listOfPreTokens = rocmts.getResultListOfPreTokens();

		boolean isPossiblyMainLine = isFirstLineBlank( listOfPreTokens );
		int offset = isPossiblyMainLine ? 1 : 0;

		IntegerReference index = new IntegerReference(1 + offset);

		PreToken preToken = null;
		
		while( ( ( preToken = getPreTokenAndIncrementIndex( listOfPreTokens, index ) ) != null ) &&
				( preToken.getTokenId() == TokenId.DOT ) );

		index._value--;
		boolean whiteToPlay = false;
		MoveToken move = getMoveTokenFromList( index, listOfPreTokens, moveNumber,
												whiteToPlay, hasToEmptyBuffer, isPossiblyMainLine );

		if( move != null )
		{
			_bufferOfOutputTokens.add( move );
			rocmts.setNumberOfInputItemsToErase(index);

			result = move;
		}

		return( result );
	}

	protected PreToken getPreTokenAndIncrementIndex( LinkedList<PreToken> list, IntegerReference index )
	{
		PreToken result = null;
		if( index._value < list.size() )
		{
			result = list.get( index._value );
			index._value++;
		}
		return( result );
	}

	protected MoveToken getMoveTokenFromList( IntegerReference index, LinkedList<PreToken> listOfPreTokens,
												int moveNumber, boolean whiteToPlay, boolean hasToEmptyBuffer,
												boolean isPossiblyMainLine )  throws ChessParserException
	{
		PreToken movePreToken = getPreTokenAndIncrementIndex( listOfPreTokens, index );
		if( ( movePreToken != null ) && ( movePreToken.getTokenId() != TokenId.MOVE ) )
		{
			movePreToken = getPreTokenAndIncrementIndex( listOfPreTokens, index );
		}
		PreToken nextPreToken = getPreTokenAndIncrementIndex( listOfPreTokens, index );

		MoveToken result = null;
		if( ( movePreToken != null ) && ( hasToEmptyBuffer || ( nextPreToken != null ) ) )
		{
			result = new MoveToken( movePreToken );
			result.setString( _chessLanguageConfiguration.translateMoveStringToEnglish( result.getString(), null ) );

			while( ( nextPreToken!= null ) && nextPreToken.getTokenId().equals( TokenId.NAG ) )
			{
				NAG nag = new NAG( nextPreToken.getString() );
				result.addNag(nag);
				nextPreToken = getPreTokenAndIncrementIndex( listOfPreTokens, index );
			}

			result.setMoveNumber(moveNumber);
			result.setIsWhiteToMove(whiteToPlay);

			if( nextPreToken!= null )
			{
				if( nextPreToken.getTokenId() == TokenId.COMMENT ) 
				{
					result.setComment( nextPreToken.getString() );
				}
				else
				{
					index._value--;
				}
			}
			else if( !hasToEmptyBuffer ) // if we do not have to empty the buffer, we need an extra token to be sure that no NAG or COMMENT follows for the next move.
			{
				result = null;
			}
		}

		return( result );
	}

	// this function returns a list of PreTokens if the top of the bufferOfInputPreTokens matches with the list of tokenIds
	// otherwise it returns null.
	protected ResultOfCheckMoveTokenSequence matchTokenIdsForMove( TokenId[] tokenIds, boolean[] optionals,
																	boolean hasToEmptyBuffer,
																	ResultOfCheckMoveTokenSequence result )
	{
		LinkedList<PreToken> listOfPreTokens = new LinkedList<PreToken>();

		int indexForInputPreTokens = 0;
		int indexForTokenIds = 0;
		boolean matches = true;

		while( matches && (indexForTokenIds < tokenIds.length) && ( indexForInputPreTokens < _bufferOfInputPreTokens.size() ) )
		{
			PreToken preToken = _bufferOfInputPreTokens.get( indexForInputPreTokens );

			if( !preToken.getTokenId().equals(tokenIds[ indexForTokenIds ] ) )
			{
				if( ( indexForTokenIds < optionals.length ) && optionals[ indexForTokenIds ] )
				{
					indexForTokenIds++;
					continue;
				}
				matches = false;
			}
			else
			{
				if( tokenIds[ indexForTokenIds ].equals( TokenId.NAG ) )  // if nag, we read all the correlative nags.
				{
					while( ( indexForInputPreTokens < _bufferOfInputPreTokens.size() ) &&
							( preToken = _bufferOfInputPreTokens.get( indexForInputPreTokens ) ).getTokenId().equals( TokenId.NAG ) )
					{
						listOfPreTokens.add( preToken );
						indexForInputPreTokens++;
					}
				}
				else
				{
					listOfPreTokens.add( preToken );
					indexForInputPreTokens++;
				}
				indexForTokenIds++;
			}
		}

		if( matches )
			result.setHasNotFailed();

		if( matches && ( hasToEmptyBuffer || ( listOfPreTokens.size() < _bufferOfInputPreTokens.size() ) ) )
		{
			for( int ii=indexForTokenIds; (listOfPreTokens != null) && (ii<tokenIds.length); ii++ )
				if( !optionals[ii] )
					listOfPreTokens = null;
		}
		else
		{
			listOfPreTokens = null;
		}

		if( ( listOfPreTokens != null ) && ( ( indexForInputPreTokens < _bufferOfInputPreTokens.size() ) ) )
		{
			listOfPreTokens.add( _bufferOfInputPreTokens.get( indexForInputPreTokens ) );
		}

		if( listOfPreTokens != null )
			result.setResultListOfPreTokens(listOfPreTokens);

		return( result );
	}

	protected static class ResultOfCheckMoveTokenSequence
	{
		LinkedList<PreToken> _resultListOfPreTokens = null;
		boolean _hasNotFailed = false;
		IntegerReference _numberOfInputItemsToErase = null;

		public void setResultListOfPreTokens( LinkedList<PreToken> list )
		{
			_resultListOfPreTokens = list;
		}
		
		public void setHasNotFailed()
		{
			_hasNotFailed = true;
		}

		public LinkedList<PreToken> getResultListOfPreTokens()
		{
			return( _resultListOfPreTokens );
		}

		public boolean getHasNotFailed()
		{
			return( _hasNotFailed );
		}
		
		public void setNumberOfInputItemsToErase( IntegerReference ri )
		{
			_numberOfInputItemsToErase = ri;
		}
		
		public IntegerReference getNumberOfInputItemsToErase()
		{
			return( _numberOfInputItemsToErase );
		}
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}

	protected static enum BlankLineNotficationEnum
	{
		TO_INPUT,
		TO_OUTPUT,
		NONE
	}
}
