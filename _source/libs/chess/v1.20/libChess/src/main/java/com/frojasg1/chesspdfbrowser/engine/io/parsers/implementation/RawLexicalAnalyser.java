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
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.io.notation.ChessMoveAlgebraicNotation;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.InputElement;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.InputElementResult;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputTextLine;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.ImageToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.MoveToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.PreToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.Token;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.TokenId;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.NAG;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.PDFPageSegmentatorInterface;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.SegmentKey;
import com.frojasg1.general.BooleanReference;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.number.IntegerReference;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.progress.UpdatingProgress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 *
 * @author Usuario
 */
public class RawLexicalAnalyser extends PGNLexicalAnalyser
{
//	protected static final int MINIMUM_NUMBER_OF_TIMES_A_TITLE_MUST_BE_REPEATED = 7;

	protected boolean _isLastString = false;
	protected UpdatingProgress _updatingProgress = null;

//	protected int[] _positionsOfPageStarts = null;

	protected PDFPageSegmentatorInterface _source = null;

	protected int _currentPageNumber = -1;

//	protected String _lineStringToSkip = null;

	protected Integer _initialPageToScanForGames = null;
	protected Integer _finalPageToScanForGames = null;

	protected List<String> _bufferOfGameHeaderStringLines = null;

	protected InputElementResult _inputText = null;
	protected Iterator<InputElement> _inputElementIterator = null;

	protected InputImage _pendingInputImage = null;

	protected SegmentKey _lastTextSegmentKey = null;

	public RawLexicalAnalyser( ChessLanguageConfiguration clc,
								PDFPageSegmentatorInterface source,
								UpdatingProgress up,
								Integer initialPageToScanForGames,
								Integer finalPageToScanForGames )
		throws ChessParserException, CancellationException
	{
		super( clc );

		_initialPageToScanForGames = initialPageToScanForGames;
		_finalPageToScanForGames = finalPageToScanForGames;

		_source = source;
		_updatingProgress = up;

		_inputText = getTextToParse( source );
		_inputElementIterator = _inputText.getInputElementListIterator();
//		setStringToParse( getTextToParse( source ) );

		setHasToBeStrict( false );
		setHasToNotifyReturns( false );
		setBlankLineNotification( BlankLineNotficationEnum.TO_INPUT );
	}

	public RawLexicalAnalyser( ChessLanguageConfiguration clc, String text )
		throws ChessParserException, CancellationException
	{
		super( clc );

		_inputText = getTextToParse( text );
		_inputElementIterator = _inputText.getInputElementListIterator();
//		setStringToParse( getTextToParse( source ) );

		setHasToBeStrict( false );
		setHasToNotifyReturns( false );
		setBlankLineNotification( BlankLineNotficationEnum.TO_INPUT );
	}

	public int getPageOfPosition( int lineNumber )
	{
		return( _inputText.getPageIndexOfPosition(lineNumber ) );
	}

	public InputImage getInputImage()
	{
		InputImage result = _pendingInputImage;
		_pendingInputImage = null;

		return( result );
	}

	public SegmentKey getLastTextSegmentKey()
	{
		return( _lastTextSegmentKey );
	}

/*
	public int getPageIndexOfPosition( int position )
	{
		int ini = 0;
		int end = _positionsOfPageStarts.length - 1;
		int result = -1;
		while( result == -1 )
		{
			int mid = ( ini + end ) / 2;
			
			if( matchesPageFromPosition( ini, position ) )
			{
				result = ini;
			}
			else if( matchesPageFromPosition( end, position ) )
			{
				result = end;
			}
			else if( matchesPageFromPosition( mid, position ) )
			{
				result = mid;
			}
			else if( ( end - ini ) < 2 )
			{
				result = mid;
				System.out.println( "Error, page not found. Position: " + position + ". We break the loop to avoid infinite looping" );
			}
			else if( _positionsOfPageStarts[mid] >= position )
			{
				end = mid;
			}
			else
			{
				ini = mid;
			}
		}
		return( result + getFromPageIndex() );
	}
*/
	@Override
	protected String nextLine()
	{
		throw( new RuntimeException( "Do not use this function. Use newInputElement() instead") );
	}

	protected InputElement nextInputElement()
	{
		InputElement result = null;

		while( ( result == null ) && _inputElementIterator.hasNext() )
		{
			InputElement elem = _inputElementIterator.next();

			if( elem instanceof InputImage )
			{
				result = elem;
				_pendingInputImage = (InputImage) result;
			}
			else if( elem instanceof InputTextLine )
			{
				String line = ( (InputTextLine) elem ).getLine();
				result = elem;
				_lastTextSegmentKey = elem.getSegmentKey();
//			}

//			if( result == null )
//				continue;

				_currentPageNumber = _inputText.getCurrentPageIndex() + 1;

				if( hasToSkip( line ) )
					result = null;

				_currentLineNumber++;
			}
		}

		return( result );
	}

/*
	@Override
	protected String nextLine()
	{
		String result = null;

		while( result == null )
		{
			result = super.nextLine();
			if( result == null )
				break;

			if( ( _currentPageNumber < ( _positionsOfPageStarts.length - 1 ) ) &&
				(_newLineMatcher.start() == _positionsOfPageStarts[_currentPageNumber + 1] ) )
			{
				_currentPageNumber++;

				if( hasToSkip( result ) )
					result = null;
			}
		}

		return( result );
	}
*/
	protected boolean hasToSkip( String line )
	{
		return( _inputText.isDiscardable( line ) );
	}
/*
	protected boolean matchesPageFromPosition( int arrayIndex, int position )
	{
		boolean result = ( _positionsOfPageStarts[ arrayIndex ] <= position );
		if( arrayIndex < (_positionsOfPageStarts.length-1) )
			result = result && ( _positionsOfPageStarts[arrayIndex+1] > position );
		
		return( result );
	}
*/
	protected InputElementResult getTextToParse(String text) throws ChessParserException, CancellationException
	{
		InputElementResult result = new InputElementResult();
		boolean hasToAddPageNumber = false;
		int startPageIndex = -1;
		int endPageIndex = -1;
		result.init( hasToAddPageNumber, startPageIndex, endPageIndex);

		SegmentKey sk = null;
		result.getInputElementList().add( new InputTextLine( text, sk ) );

		return( result );
	}

	protected void addStringToCandidateToSkip( Map<String, IntegerReference> map, String pageString )
	{
		Matcher newLineMatcher = _newLinePattern.matcher( pageString );
		String line = null;
		if( newLineMatcher.find() )
		{
			if( newLineMatcher.groupCount() == 2 )
			{
				line = newLineMatcher.group( 1 );
			}
		}
		
		if( line != null )
		{
			IntegerReference value = map.get( line );
			if( value == null )
				map.put( line, new IntegerReference( 1 ) );
			else
			{
				value._value++;
			}
		}
	}
/*
	protected String getStringToSkip( Map<String, IntegerReference> hashCandidateToSkip )
	{
		String result = null;
		int max = 0;
		
		Iterator<Map.Entry<String, IntegerReference> > it = hashCandidateToSkip.entrySet().iterator();
		while( it.hasNext() )
		{
			Map.Entry<String, IntegerReference> mapElem = it.next();

			if( ( mapElem.getValue()._value > max ) &&
				( mapElem.getValue()._value >= MINIMUM_NUMBER_OF_TIMES_A_TITLE_MUST_BE_REPEATED )
			  )
			{
				max = mapElem.getValue()._value;
				result = mapElem.getKey();
			}
		}

		return( result );
	}
*/
	protected int getIntValue( Integer value, int defaultValue )
	{
		return( ( value == null ) ? defaultValue : value );
	}

	protected int getFromPageIndex()
	{
		return( getIntValue( _initialPageToScanForGames, 1 ) - 1 );
	}

	protected int getToPageIndex()
	{
		return( getIntValue( _finalPageToScanForGames, _source.getNumberOfPages() ) - 1 );
	}

	protected int getTotalAmountOfPagesToParse()
	{
		return( getToPageIndex() + 1 - getFromPageIndex() );
	}

	public double getProgress( int currentPage )
	{
		return( ( double ) ( currentPage - getFromPageIndex() - 1 ) / getTotalAmountOfPagesToParse() );
	}

	protected InputElementResult getTextToParse(PDFPageSegmentatorInterface source) throws ChessParserException, CancellationException
	{
		InputElementResult result = new InputElementResult();
		boolean hasToAddPageNumber = true;
		int initialPageToScanForGames = getFromPageIndex() + 1;
		int finalPageToScanForGames = getToPageIndex() + 1;
		result.init( hasToAddPageNumber, initialPageToScanForGames, finalPageToScanForGames );

		if( _updatingProgress != null )
			_updatingProgress.up_childStarts();

		int fromIndex = getFromPageIndex();
		int toIndexPlusOne = getToPageIndex() + 1;
		for( int ii=fromIndex; ii<toIndexPlusOne; ii++ )
		{
			try
			{
				if( _updatingProgress != null )
					_updatingProgress.up_updateProgressFromChild( getProgress( ii + 1 ) );

				InputElementResult pageInputResult = source.getInputResultOfPage( ii );
				result.addPartialResult( pageInputResult );

//				String pageString = source.getTextOfPage( ii );
//				result.append( pageString );
//				addStringToCandidateToSkip( hashCandidateToSkip, pageString );
			}
			catch( CancellationException ce )
			{
				throw( ce );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				throw( new ChessParserException( th.toString() ) );
			}
		}

		result.endOfCapture();
//		beginningOfPages.add( result.length() );

		if( _updatingProgress != null )
			_updatingProgress.up_childEnds();

		return( result );
	}
/*
	protected String getTextToParse(PDFPageSegmentatorInterface source) throws ChessParserException, CancellationException
	{
		StringBuilder result = new StringBuilder();

		// this map counts the times a string appear as the first line of the page.
		// we want to skip that line if it is repeated many times (then it is a title, and it is not part of the games)
		Map<String, IntegerReference> hashCandidateToSkip = new HashMap<String, IntegerReference>();

		List<Integer> beginningOfPages = new ArrayList<Integer>();

		if( _updatingProgress != null )
			_updatingProgress.up_childStarts();

		beginningOfPages.add(0);

		int fromIndex = getFromPageIndex();
		int toIndexPlusOne = getToPageIndex() + 1;
		for( int ii=fromIndex; ii<toIndexPlusOne; ii++ )
		{
			try
			{
				if( _updatingProgress != null )
					_updatingProgress.up_updateProgressFromChild( getProgress( ii + 1 ) );

				if( ii>fromIndex )
				{
					result.append( "\n" );
					beginningOfPages.add( result.length() );
				}

				String pageString = source.getTextOfPage( ii );
				result.append( pageString );
				addStringToCandidateToSkip( hashCandidateToSkip, pageString );
			}
			catch( CancellationException ce )
			{
				throw( ce );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				throw( new ChessParserException( th.toString() ) );
			}
		}

		beginningOfPages.add( result.length() );

		if( _updatingProgress != null )
			_updatingProgress.up_childEnds();

		_positionsOfPageStarts = new int[beginningOfPages.size()];
		Iterator<Integer> it = beginningOfPages.iterator();
		int index = 0;
		while( it.hasNext() )
		{
			_positionsOfPageStarts[index] = it.next();
			index++;
		}
		
		_lineStringToSkip = getStringToSkip( hashCandidateToSkip );
		
		return( result.toString() );
	}
*/
	@Override
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
				case '[':
				case ']':
				case '\t':
				case ';':
				case ':':
				case ',':
				case '…':
					result = true;
			}
		}

		return( result );
	}

	@Override
	protected boolean isANag( String text )
	{
		return( NAG.getNAGfromStringToShow( text ) != -1 );
	}

	@Override
	protected TokenId getTokenId_simple( String preTokenStr )
	{
		TokenId result = null;

		if( preTokenStr.equals( "." ) )			result = TokenId.DOT;
		else if( preTokenStr.equals( "[" ) )	result = TokenId.OPEN_SQUARE_BRACKET;
		else if( preTokenStr.equals( "]" ) )	result = TokenId.CLOSE_SQUARE_BRACKET;
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

	// this functions sets _currentLine class attribute.
	// it returns a PreToken, in case it is met before the new line (as for example ImagePretoken, in case of Pdf parser).
	@Override
	protected PreToken getNewLine() throws ChessParserException
	{
		PreToken exceptionalPreTokenBeforeImage = null;
		while( (_currentLine == null) && !_isEOF )
		{
			InputElement inputElement = nextInputElement();

			if( inputElement instanceof InputImage )
			{
				exceptionalPreTokenBeforeImage = new PreToken( ( InputImage ) inputElement,
																_currentLineNumber );
				break;
			}
			else if( inputElement instanceof InputTextLine )
				_currentLine = ( (InputTextLine) inputElement).getLine();

			_isEOF = ( _currentLine == null );

			if( !_isEOF )
			{
				if( getBufferOfGameHeaderStringLines() != null ) 
				{
					getBufferOfGameHeaderStringLines().add(_currentLine);
				}

				checkIsBlackLine();	// adds a blank line token if line is blank

			}
		}

		if( _currentLine != null )
		{
			_currentPosInLine = 0;
		}

		return( exceptionalPreTokenBeforeImage );
	}

	/**
	 * We override this method to allow joining the two parts of a string splitted by a new line
	 * As for example can happen in case of a O-O which can be splitted in two parts 
	 * ( O- and O, each one in a different line).
	 * @throws ChessParserException 
	 */
	@Override
	protected void getNewPreToken() throws ChessParserException
	{
		PreToken pt = null;
		PreToken stringWrappedToEndOfLinePt = null;
		PreToken endOfLinePt = null;

		while( !_isEOF && (pt==null) )
		{
			if( _currentLine == null )
			{
				pt = getNewLine();
			}

			if( pt != null )
				addPreToken( pt );
			else if( !_isEOF )
			{
				pt = getNextPreTokenInCurrentLine();

				if( pt != null )
				{
					if( isStringWrappedToEndOfLinePt( pt ) )
					{
						stringWrappedToEndOfLinePt = pt;
						pt = null;
					}
					else if( stringWrappedToEndOfLinePt!=null )
					{
						if( isStringWrappedToStartOfLinePt( pt ) )
						{
							String ptStr = stringWrappedToEndOfLinePt.getString() + pt.getString();
							PreToken tmpPt = getPreTokenFromPreTokenString( ptStr );
							_currentPosInLine--;	//because it had been incremented inside the previous function.

							if( tmpPt != null )
							{
								if( tmpPt.getTokenId().equals( TokenId.MOVE ) ||
									tmpPt.getTokenId().equals( TokenId.RESULT ) ) 
								{
									stringWrappedToEndOfLinePt = null;
									endOfLinePt = null;
									pt = tmpPt;
								}
							}
						}
					}
				}

				if( pt != null )
				{
					if( stringWrappedToEndOfLinePt != null )
					{
						addPreToken( stringWrappedToEndOfLinePt );
						stringWrappedToEndOfLinePt = null;
					}

					if( endOfLinePt != null )
					{
						addPreToken( endOfLinePt );
						endOfLinePt = null;
					}

					addPreToken( pt );
				}

				if( _currentPosInLine >= _currentLine.length() )
				{
					_currentLine = null;
					if( hasToNotifyReturns() )
					{
						endOfLinePt = createPreToken( TokenId.RETURN, "\r",
													_currentLineNumber, _currentPosInLine,
													getLastTextSegmentKey() );
						if( pt != null )
							addPreToken( endOfLinePt );
					}
				}
			}
		}
	}

	protected void addPreToken( PreToken pt )
	{
		if( pt != null )
		{
			if( pt.getTokenId().equals( TokenId.STRING ) )
			{
				List<PreToken> list = splitStringToken( pt );

				for( PreToken pt2: list )
					addPreTokenDiscardingCommentedMove( pt2 );
			}
			else if( TokenId.IMAGE.equals( pt.getTokenId() ) )
				onlyAddPreToken( pt );
			else
				addPreTokenDiscardingCommentedMove( pt );
		}
	}

	protected List<PreToken> splitStringToken( PreToken pt )
	{
		List<PreToken> result = new ArrayList<>();

		int lineNumber = pt.getLineNumber();
		int initialPosition = pt.getInitialPosition();

		String str = pt.getString();
		if( str.equals( "…" ) )
		{
			PreToken pt2 = createPreToken( TokenId.DOT, ".", lineNumber, initialPosition,
											getLastTextSegmentKey() );
			result.add( pt2 );
			result.add( pt2 );
			result.add( pt2 );
		}
		else // check if move+NAG
		{
			int pos = IntegerFunctions.max( 0, str.length() - 4 );
			String nagStr = null;
			for( int ii=pos; ( nagStr == null ) && ( ii < str.length() ); ii++ )
			{
				nagStr = str.substring( ii );

				if( !isANag( nagStr ) )
					nagStr = null;
			}

			if( nagStr != null )
			{
				String possibleMove = str.substring( str.length() - nagStr.length() );

				if( isAMove( possibleMove ) )
				{
					PreToken pt2 = createPreToken( TokenId.MOVE, possibleMove,
													lineNumber, initialPosition,
													getLastTextSegmentKey() );
					result.add( pt2 );

					pt2 = createPreToken( TokenId.NAG, nagStr, lineNumber,
											initialPosition + possibleMove.length(),
											getLastTextSegmentKey() );
					result.add( pt2 );
				}
			}
		}

		if( result.isEmpty() )
			result.add( pt );

		return( result );
	}

	protected void onlyAddPreToken( PreToken pt )
	{
		_bufferOfInputPreTokens.add( pt );
	}

	protected void addPreTokenDiscardingCommentedMove( PreToken pt )
	{
		boolean hasToAdd = true;
		if( pt.getTokenId().equals( TokenId.MOVE ) )
		{
			hasToAdd = hasToAddNewMove();
		}

		if( hasToAdd )
			onlyAddPreToken( pt );
	}

	protected boolean hasToAddNewMove()
	{
		boolean result = ( ! _bufferOfInputPreTokens.isEmpty() );

		if( result )
		{
			TokenId lastTid = _bufferOfInputPreTokens.getLast().getTokenId();

			result = ( lastTid.equals( TokenId.MOVE ) ||
						lastTid.equals( TokenId.DOT ) ||
						lastTid.equals( TokenId.NUMBER ) ||
						lastTid.equals( TokenId.NAG )
				);
		}

		if( result )
		{
			Iterator<PreToken> it = _bufferOfInputPreTokens.descendingIterator();
			int numOfPreviousMoves = 0;
			int numOfConsecutiveDots = 0;
			loop: while( result && it.hasNext() )
			{
				PreToken item = it.next();

				switch( item.getTokenId() )
				{
					case MOVE:
						numOfPreviousMoves++;
					break;

					case DOT:
						numOfConsecutiveDots++;
					break;

					case NUMBER:
						break loop;
						// result = true;
				}

				if( !item.getTokenId().equals( TokenId.DOT ) )
				{
					numOfConsecutiveDots = 0;
				}

				if( ( numOfConsecutiveDots > 2 ) && ( numOfPreviousMoves > 0 ) )
					result = false;

				if( numOfPreviousMoves > 1 )
					result = false;
			}
		}

		return( result );
	}

	protected boolean isStringWrappedToEndOfLinePt( PreToken pt )
	{
		boolean result = ( pt.getTokenId().equals( TokenId.STRING ) ||
							isCastleKingSide(pt.getString()) ||
								pt.getTokenId().equals( TokenId.MOVE ) ) &&
							(	(pt.getInitialPosition() + pt.getString().length() ) ==
								( ( _newLineMatcher != null ? _newLineMatcher.start() : 0 ) + _currentLine.length() ) );

		return( result );
	}

	protected boolean isStringWrappedToStartOfLinePt( PreToken pt )
	{
		boolean result = ( pt.getTokenId().equals( TokenId.STRING ) ||
							pt.getTokenId().equals( TokenId.NUMBER ) ||
							( pt.getString().length()>=3 ) &&
							isCastleKingSide( pt.getString().substring(0,3)) ) &&

							( pt.getInitialPosition() == ( _newLineMatcher != null ? _newLineMatcher.start() : 0 ) );

		return( result );
	}

	protected boolean isSimplePreToken( PreToken pt )
	{
		boolean result = false;
		if( pt != null )
		{
			switch( pt.getTokenId() )
			{
				case OPEN_BRACE:
				case OPEN_BRACKET:
				case OPEN_SQUARE_BRACKET:
				case CLOSE_BRACE:
				case CLOSE_BRACKET:
				case CLOSE_SQUARE_BRACKET:
				case RESULT:
				case IMAGE:
					result = true;
			}
		}

		return( result );
	}

	protected Token createToken( PreToken preToken )
	{
		Token result = null;
		if( preToken.getTokenId().equals( TokenId.IMAGE ) )
			result = new ImageToken( preToken );
		else
			result = new Token( preToken );

		return( result );
	}

	protected void addSimplePreTokenToOutputTokens( PreToken pt )
	{
		if( pt != null )
		{
			_bufferOfOutputTokens.add( createToken( pt ) );
			_bufferOfInputPreTokens.removeFirst();
		}
	}

	@Override
	protected void processBufferOfInputPreTokens( boolean hasToEmptyBuffer ) throws ChessParserException
	{
		boolean hasToProcess = ( _bufferOfInputPreTokens.size() > 0 );

		BooleanReference mustLoop = new BooleanReference( false );
		while( hasToProcess )
		{
			Token token = null;

			mustLoop._value = true;
			do
			{
				PreToken preToken = _bufferOfInputPreTokens.getFirst();

				if( isSimplePreToken( preToken ) )
				{
					addSimplePreTokenToOutputTokens( preToken );
					return;
				}

				if( preToken.getTokenId().equals( TokenId.NUMBER ) ||
					preToken.getTokenId().equals( TokenId.MOVE ) ||
					preToken.getTokenId().equals( TokenId.BLANK_LINE )
					)
				{
					token = createTokensForMove( preToken, hasToEmptyBuffer, mustLoop );	// this function must not return token != null and mustLoop==true
				}

				if( mustLoop._value )	// if mustLoop, we must discard the preToken.
				{
					_bufferOfInputPreTokens.removeFirst();
					mustLoop._value = ( _bufferOfInputPreTokens.size() > 0 );
				}
			}
			while( mustLoop._value ); // if we must not loop is because no token has been found, or there are not more preTokens in the bufferOfInputPreTokens.

			hasToProcess = ( ( token != null ) && ( _bufferOfInputPreTokens.size() > 0 ) );
		}
	}

	/**
	 * 
	 * @param preToken
	 * @param hasToEmptyBuffer
	 * @param mustDiscard. is a reference to boolean. At the exit of the function, it must contain if the numberPreToken must be discarded or not
	 * @return
	 * @throws ChessParserException 
	 */
	protected Token createTokensForMove( PreToken preToken, boolean hasToEmptyBuffer, BooleanReference mustDiscard ) throws ChessParserException
	{
		Token result = null;

//		int moveNumber = -1;
		int moveNumber = getMoveNumber( _bufferOfInputPreTokens );

//		if( preToken.getTokenId().equals( TokenId.NUMBER) )
//			moveNumber = Integer.parseInt( preToken.getString() );

		boolean[] optional4 = { true, false, true, false, true, false, true, false, true };
		boolean[] optional1 = { true, false, true, false, true, false, true };
		boolean[] optional3 = { true, false, false, false, false, true, false, true };
		TokenId[] case4 = { TokenId.BLANK_LINE, TokenId.MOVE, TokenId.NAG, TokenId.NUMBER, TokenId.DOT, TokenId.MOVE, TokenId.NAG, TokenId.MOVE, TokenId.NAG };
		TokenId[] case5 = { TokenId.BLANK_LINE, TokenId.MOVE, TokenId.NAG, TokenId.NUMBER, TokenId.DOT, TokenId.MOVE, TokenId.NAG };
		TokenId[] case1 = { TokenId.BLANK_LINE, TokenId.NUMBER, TokenId.DOT, TokenId.MOVE, TokenId.NAG, TokenId.MOVE, TokenId.NAG };
		TokenId[] case2 = { TokenId.BLANK_LINE, TokenId.NUMBER, TokenId.DOT, TokenId.MOVE, TokenId.NAG };
		TokenId[] case3 = { TokenId.BLANK_LINE, TokenId.NUMBER, TokenId.DOT, TokenId.DOT, TokenId.DOT, TokenId.DOT, TokenId.MOVE, TokenId.NAG };

		ResultOfCheckMoveTokenSequence resultTS = new ResultOfCheckMoveTokenSequence();

		if( matchTokenIdsForMove( case4, optional4, hasToEmptyBuffer, resultTS ).getResultListOfPreTokens() != null )
		{
			result = createCase4Move( resultTS, hasToEmptyBuffer );
		}
		else if( (!resultTS.getHasNotFailed() || hasToEmptyBuffer ) &&
					( matchTokenIdsForMove( case5, optional4, hasToEmptyBuffer, resultTS ).getResultListOfPreTokens() != null ) )
		{
			result = createCase5Move( resultTS, hasToEmptyBuffer );
		}
		else if( matchTokenIdsForMove( case1, optional1, hasToEmptyBuffer, resultTS ).getResultListOfPreTokens() != null )
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
			throw( new ChessParserException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_CLOSING_OF_COMMNENT_NOT_FOUND ),
												preToken.getLineNumber(),
												preToken.getString()  ) ) );
		}

		mustDiscard._value = !resultTS.getHasNotFailed();

		if( result != null )
		{
			for( int ii=0; ii<resultTS.getNumberOfInputItemsToErase()._value; ii++ )
				_bufferOfInputPreTokens.removeFirst();
		}

		return( result );
	}

	@Override
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
			result.setPageNumber( _currentPageNumber );
			result.setString( _chessLanguageConfiguration.translateMoveStringToEnglish( result.getString(), null ) );

			while( ( nextPreToken!= null ) && nextPreToken.getTokenId().equals( TokenId.NAG ) )
			{
				NAG nag = new NAG( nextPreToken.getString() );
				result.addNag(nag);
				nextPreToken = getPreTokenAndIncrementIndex( listOfPreTokens, index );
			}

			result.setMoveNumber(moveNumber);
			result.setIsWhiteToMove(whiteToPlay);
			result.setIsPossiblyMainLine( isPossiblyMainLine );

			if( nextPreToken!= null )
			{
				if( nextPreToken.getTokenId() != TokenId.NAG ) 
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

	@Override
	protected PreToken getPreTokenFromPreTokenString( String preTokenStr ) throws ChessParserException
	{
		PreToken result = null;

		if( preTokenStr != null )
		{
			int iniPosOfLine = ( _newLineMatcher != null ? _newLineMatcher.start() : 0 );

			TokenId tokenId = getTokenId( preTokenStr );

			result = createPreToken( tokenId, preTokenStr, _currentLineNumber,
									iniPosOfLine + _currentPosInLine - preTokenStr.length() + 1,
									_lastTextSegmentKey );

//			if( hasToIncrement( tokenId ) )
				_currentPosInLine++;
		}

		return( result );
	}

	protected boolean hasToIncrement( TokenId tokenId )
	{
		boolean result = false;

		if( tokenId != null )
		{
			if( _currentPosInLine < _currentLine.length() )
			{
				TokenId ti2 = getTokenId_simple( _currentLine.substring( _currentPosInLine, _currentPosInLine + 1 ));
				if( ( ti2 == null ) || tokenId.equals( ti2 ) )
				{
					result = true;
				}
			}
		}
		else
		{
			result = true;
		}

		return( result );
	}

	protected int getMoveNumber( LinkedList<PreToken> listOfPreTokens )
	{
		int result = -1;

		Iterator<PreToken> it = listOfPreTokens.iterator();
		while( (result==-1) && it.hasNext() )
		{
			PreToken preToken = it.next();
			if( preToken.getTokenId().equals( TokenId.NUMBER ) )
				result = Integer.parseInt( preToken.getString() );
		}
		return( result );
	}
	
	// 	TokenId[] case4 = { TokenId.MOVE, TokenId.NAG, TokenId.NUMBER, TokenId.DOT, TokenId.MOVE, TokenId.NAG, TokenId.MOVE, TokenId.NAG };
	protected Token createCase4Move( ResultOfCheckMoveTokenSequence rocmts, boolean hasToEmptyBuffer ) throws ChessParserException
	{
		Token result = null;

		LinkedList<PreToken> listOfPreTokens = rocmts.getResultListOfPreTokens();

		boolean isPossiblyMainLine = isFirstLineBlank( listOfPreTokens );
		int offset = isPossiblyMainLine ? 1 : 0;

		int moveNumber = getMoveNumber( listOfPreTokens );
		
		IntegerReference index = new IntegerReference(0 + offset);

		boolean whiteToPlay = false;
		MoveToken previousMove = getMoveTokenFromList( index, listOfPreTokens, moveNumber-1,
														whiteToPlay, hasToEmptyBuffer, isPossiblyMainLine );

		index._value++;
		whiteToPlay = true;
		MoveToken firstMove = getMoveTokenFromList( index, listOfPreTokens, moveNumber,
													whiteToPlay, hasToEmptyBuffer, isPossiblyMainLine );

		whiteToPlay = false;
		MoveToken secondMove = getMoveTokenFromList( index, listOfPreTokens, moveNumber,
													whiteToPlay, hasToEmptyBuffer, isPossiblyMainLine );

		if( ( previousMove != null ) && ( firstMove != null ) && ( secondMove != null ) )
		{
			_bufferOfOutputTokens.add( previousMove );
			_bufferOfOutputTokens.add( firstMove );
			_bufferOfOutputTokens.add( secondMove );
			rocmts.setNumberOfInputItemsToErase(index);

			result = firstMove;
		}

		return( result );
	}

	protected Token createCase5Move( ResultOfCheckMoveTokenSequence rocmts, boolean hasToEmptyBuffer ) throws ChessParserException
	{
		Token result = null;

		LinkedList<PreToken> listOfPreTokens = rocmts.getResultListOfPreTokens();

		boolean isPossiblyMainLine = isFirstLineBlank( listOfPreTokens );
		int offset = isPossiblyMainLine ? 1 : 0;

		int moveNumber = getMoveNumber( listOfPreTokens );
		
		IntegerReference index = new IntegerReference(0 + offset);

		boolean whiteToPlay = false;
		MoveToken previousMove = getMoveTokenFromList( index, listOfPreTokens, moveNumber-1,
														whiteToPlay, hasToEmptyBuffer, isPossiblyMainLine );

		index._value++;
		whiteToPlay = true;
		MoveToken firstMove = getMoveTokenFromList( index, listOfPreTokens, moveNumber,
													whiteToPlay, hasToEmptyBuffer, isPossiblyMainLine );

		if( ( previousMove != null ) && ( firstMove != null )  )
		{
			_bufferOfOutputTokens.add( previousMove );
			_bufferOfOutputTokens.add( firstMove );
			rocmts.setNumberOfInputItemsToErase(index);

			result = firstMove;
		}

		return( result );
	}

	protected void setBufferOfGameHeaderStringLines( List<String> bufferOfGameHeaderStringLines )
	{
		_bufferOfGameHeaderStringLines = bufferOfGameHeaderStringLines;
	}

	protected List<String> getBufferOfGameHeaderStringLines()
	{
		return( _bufferOfGameHeaderStringLines );
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}
}
