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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation.rawparser3;

import com.frojasg1.chesspdfbrowser.engine.configuration.TagExtractorConfiguration;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation.RawChessGameParser;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.MoveToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.Token;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ImagePositionController;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.progress.GeneralUpdatingProgress;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RawImprovedChessGameParser3 extends RawChessGameParser
{
	protected ChessViewConfiguration _chessViewConfiguration = null;

	protected static final int MAX_NUMBER_OF_PLY_DIFF_TO_BE_THE_SAME_GAME = 30;

//	protected Token _nextProperToken = null;

//	protected LinkedList<MoveTreeNode> _listOfStartedSubvariants = null;
//	protected MoveTreeNode _currentNode = null;

	protected ChessGame _chessGame = null;

	protected StartedSubvariantsList _listOfStartedSubvariants = null; 

	public RawImprovedChessGameParser3( ChessLanguageConfiguration clc,
										GeneralUpdatingProgress gup,
										ChessViewConfiguration cvc,
										TagExtractorConfiguration tec,
										TagsExtractor tagsExtractor,
										ImagePositionController controller,
										String pdfBaseFileName )
	{
		super( clc, gup, tec, tagsExtractor, controller, pdfBaseFileName );

		_chessViewConfiguration = cvc;

		_gup = gup;
	}

	protected void parseGameMoves( ChessGame cg ) throws ChessParserException, CancellationException
	{
		_chessGame = cg;
//		if( startsFromFirstMoveOfGame( _nextMoveToken ) )
			improvedParseGameMoves( cg );
//		else
//			super.parseGameMoves( cg );
	}

	@Override
	protected Token getIfNotMoveProperToken( Token token )
	{
		Token result = null;
		if( token != null )
		{
			switch( token.getTokenId() )
			{
				case OPEN_BRACKET:
				case CLOSE_BRACKET:
				case OPEN_SQUARE_BRACKET:
				case CLOSE_SQUARE_BRACKET:
				case OPEN_BRACE:
				case CLOSE_BRACE:
				case RESULT:
					result = token;
				break;
			}
		}

		return( result );
	}
/*
	@Override
	protected MoveToken nextMoveToken() throws ChessParserException, CancellationException
	{
		_nextMoveToken = null;

		do
		{
			nextProperToken();
			if( _nextProperToken instanceof MoveToken )
			{
				_nextMoveToken = (MoveToken) _nextProperToken;
			}
		}
		while( ( _nextMoveToken == null ) && hasNextProperToken() );

		return( _nextMoveToken );
	}
*/

	protected void initParsing( ChessGame cg )
	{
		_listOfStartedSubvariants = new StartedSubvariantsList();
		_listOfStartedSubvariants.init(_chessViewConfiguration, this);

		_listOfStartedSubvariants.setCurrentNode( cg.getMoveTreeGame() );
	}

	protected void improvedParseGameMoves( ChessGame cg ) throws ChessParserException, CancellationException
	{
		initParsing( cg );

//		_thereAreBrackets = false;

//		_bufferOfGameHeaderStringLines = new ArrayList<>();
		boolean success = true;
		while( hasNextProperToken() )
		{
			getCurrentPageAndUpdateProgress();

			_listOfStartedSubvariants.setNextProperToken(_nextProperToken);

			switch( _nextProperToken.getTokenId() )
			{
				case MOVE:
					success = processMove();
				break;

				case OPEN_BRACKET:
				case OPEN_SQUARE_BRACKET:
				case OPEN_BRACE:
					success = processOpenNewSubvariant();
				break;

				case CLOSE_BRACKET:
				case CLOSE_SQUARE_BRACKET:
				case CLOSE_BRACE:
					success = processCloseSubvariant();
				break;

				case RESULT:
					boolean hasToContinue = setGameResult();
					success = hasToContinue;
				break;
			}

			if( !isSubvariantClosing( _nextProperToken ) )
				_listOfStartedSubvariants.resetLastNodePreviousToPop();

			// if we are at root, we have to get next token, to avoid infinite loop.
			if( !success && !( getCurrentNode() instanceof MoveTreeGame ) )
				break;

//			addToBufferOfGameHeader();
			nextProperToken();
//			resetBufferOfGameHeader();

			// if we are at root and not success, then exit loop
			if( ! success )
				break;
		}
	}

	protected MoveTreeNode getCurrentNode()
	{
		return( _listOfStartedSubvariants.getCurrentNode() );
	}

	protected boolean isSubvariantClosing( Token nextProperToken )
	{
		boolean result = false;

		switch( _nextProperToken.getTokenId() )
		{
			case CLOSE_BRACKET:
			case CLOSE_SQUARE_BRACKET:
			case CLOSE_BRACE:
				result = true;
			break;
		}
		return( result );
	}

	protected boolean setGameResult()
	{
		boolean result = setGameResult( getCurrentNode(), _nextProperToken );

		return( result );
	}


	protected boolean processMove() throws ChessParserException, CancellationException
	{
		boolean success = true;
		ChessGameMove cgm = createChessGameMove( _chessGame, (MoveToken) _nextProperToken );

		success = _listOfStartedSubvariants.processMove( cgm );

		return( success );
	}

	protected boolean processOpenNewSubvariant()
	{
		boolean success = true;

//		if( _currentNode != null )
		{
			_listOfStartedSubvariants.newSubvariant();
//			_listOfStartedSubvariants.addLast( _currentNode );
		}

		return( success );
	}

	protected boolean processCloseSubvariant()
	{
		boolean success = _listOfStartedSubvariants.removeLast();

		return( success );
	}
}
