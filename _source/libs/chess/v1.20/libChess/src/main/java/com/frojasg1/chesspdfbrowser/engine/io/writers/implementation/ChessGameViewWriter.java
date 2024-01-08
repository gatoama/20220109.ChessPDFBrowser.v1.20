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
package com.frojasg1.chesspdfbrowser.engine.io.writers.implementation;

import com.frojasg1.chesspdfbrowser.engine.io.writers.ChessGameWriter;
import com.frojasg1.chesspdfbrowser.engine.io.writers.ChessGameViewWriterObserver;
import com.frojasg1.chesspdfbrowser.engine.io.writers.WrittenNodeInfo;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessModelException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessPieceCreationException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessWriterException;
import com.frojasg1.chesspdfbrowser.engine.io.notation.ChessMoveAlgebraicNotation;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.NAG;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class ChessGameViewWriter implements ChessGameWriter
{
	protected ChessGame _parent = null;
	protected ChessGameViewWriterObserver _observer = null;

	protected StringBuilder _stringBuilderMoveTree = null;

	protected int _currentCharPosition = 0;

	protected boolean _isToWriteInTextPane = false;

	protected int _initialPositionOfComment = -1;
	protected int _finalPositionOfComment = -1;
	
	public ChessGameViewWriter( boolean isToWriteInTextPane )
	{
		_isToWriteInTextPane = isToWriteInTextPane;
		_stringBuilderMoveTree = new StringBuilder();
	}

	public String getGameHeaderString( ChessGame chessGame )
	{
		return( null );
	}

	@Override
	public String getGameMovesString( ChessGame chessGame, ChessGameViewWriterObserver observer ) throws ChessWriterException
	{
		String result = null;
		try
		{
			_parent = chessGame;
			_observer = observer;

			ChessBoard chessBoard = null;

//			System.out.println( "Game to save: " + chessGame.getChessGameHeaderInfo().getDescriptionOfGame() );

			if( chessGame.isValid() )
			{
				chessBoard = new ChessBoard();
				chessBoard.setPosition( _parent.getInitialPosition() );
			}

			boolean isMainLine = true;

			_currentCharPosition = 0;

			_stringBuilderMoveTree = new StringBuilder();
			updateAdditionalInfo( chessBoard, _parent.getMoveTreeGame(), isMainLine );

			addStringToStringBuilderMoveTree( " " );	// for the last character not to be selected.

			result = _stringBuilderMoveTree.toString();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			throw( new ChessWriterException( th.getMessage() ) );
		}

		endOfGame();
		
		return( result );
	}

	protected void endOfGame()
	{
		
	}
	
	// this function creates the string of the move tree, and the locations of every move in the string.
	protected void updateAdditionalInfo( ChessBoard chessBoard,
											MoveTreeNode mt, boolean isMainLine )
		throws ChessMoveException, ChessPieceCreationException, ChessModelException, ChessParserException
	{
		// if the root node has not been checked, it means that is has to be checked now,
		// thus making al moves an recalculating the ChessGameMove, to check if the moves are correct.
		boolean mustMove = true;

		MoveTreeNode parentMt = mt.getParent();
		boolean newIsMainLine = isMainLine;
		boolean hasToWriteEllipsis = true;
		boolean addedBrackets = false;
		boolean moveDone = false;

		if( ( parentMt != null ) &&
			( parentMt.getNumberOfChildren() > 1 ) &&
			( parentMt.getChild(0) != mt ) )
		{
			if( isMainLine && _isToWriteInTextPane )		addStringToStringBuilderMoveTree( " \r    " );
			else											addStringToStringBuilderMoveTree( " " );

			addStringToStringBuilderMoveTree( "( " );
			addedBrackets = true;

			hasToWriteEllipsis = true;
			newIsMainLine = false;

			addMoveToStringBuilderMoveTree( chessBoard, mt, parentMt, newIsMainLine, hasToWriteEllipsis );
		}
		else if( ( parentMt != null ) &&
				( parentMt.getNumberOfChildren() > 1 ) &&
				( parentMt.getChild(0) == mt ) &&
				( mt.getNumberOfChildren() > 0 ) )
		{
			if( newIsMainLine && _isToWriteInTextPane )
			{
				addStringToStringBuilderMoveTree( " \r" );
			}
		}
		else if( ( parentMt != null ) && ( parentMt.getNumberOfChildren() == 1 ) )
		{
			addStringToStringBuilderMoveTree( " " );
			MoveTreeNode parentParent = ( ( parentMt == null ) ? null : parentMt.getParent() );
			hasToWriteEllipsis = ( parentParent == null ) || ( parentParent.getNumberOfChildren() > 1 ) && ( parentParent.getChild(0) == parentMt );

			addMoveToStringBuilderMoveTree( chessBoard, mt, parentMt, isMainLine, hasToWriteEllipsis );
		}

		if( mt.getNumberOfChildren() > 1 )
		{
			hasToWriteEllipsis = ( parentMt == null ) || ( parentMt.getNumberOfChildren() != 1 );

			if( parentMt != null )
			{
				addStringToStringBuilderMoveTree( " " );
			}

			if( mustMove )
			{
				moveDone = doMove( mt, chessBoard );
			}
			addMoveToStringBuilderMoveTree( chessBoard, mt.getChild(0), mt, newIsMainLine, hasToWriteEllipsis );
		}

		if( !moveDone && mustMove  )
		{
			moveDone = doMove( mt, chessBoard );
		}

		Iterator<MoveTreeNode> it = mt.getChildrenIterator();
		MoveTreeNode firstChild = null;
		if( it.hasNext() ) firstChild = it.next();

		while( it.hasNext() )
		{
			MoveTreeNode childMt = it.next();
			updateAdditionalInfo( chessBoard, childMt, newIsMainLine );
		}

		if( ( mt.getMove() != null ) && ( mt.getMove()._resultOfGame != null ) )
		{
			addStringToStringBuilderMoveTree( " " + mt.getMove()._resultOfGame );
		}

		if( firstChild != null )
		{
			updateAdditionalInfo( chessBoard, firstChild, newIsMainLine );
		}

/*
		if( (chessBoard != null ) && 
			(mt.getMove() != null ) && mustMove &&
			( ( mt.getAdditionalInfo() == null ) ||
				!mt.getAdditionalInfo().getIsIllegalMove() &&
				!mt.getAdditionalInfo().getIsAnyParentIllegalMove() )
		  )
*/
		if( moveDone )
		{
			chessBoard.undoMove( mt.getMove() );
		}

		if( addedBrackets )
		{
			addStringToStringBuilderMoveTree( " )" );
		}
	}

	protected void addStringToStringBuilderMoveTree( String str )
	{
		if( ( str != null ) && ( str.length() != 0 ) )
		{
			_stringBuilderMoveTree.append( str );
			_currentCharPosition += str.length();
		}
	}

	protected void addMoveAdditionalInfo( WrittenNodeInfo nodeInfo )
	{
		if( _observer != null )
		{
			_observer.newWrittenNode(nodeInfo);
		}
	}

	protected boolean doMove( MoveTreeNode mtn, ChessBoard chessBoard )
	{
		boolean moveDone = false;
		if( ( mtn.getMove() != null ) &&
			( chessBoard != null ) &&
			( ( mtn.getAdditionalInfo() == null ) ||
				!mtn.getAdditionalInfo().getIsIllegalMove() &&
				!mtn.getAdditionalInfo().getIsAnyParentIllegalMove() )
		  )
		{
			try
			{
				chessBoard.move( mtn.getMove() );
				moveDone = true;
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				if( mtn.getAdditionalInfo() != null )
					mtn.getAdditionalInfo().setIsIllegalMove(true);
			}
		}
		return( moveDone );
	}

	protected void undoMove( MoveTreeNode mtn, ChessBoard chessBoard ) throws ChessModelException
	{
		if( ( mtn.getMove() != null )  && ( chessBoard != null ) &&
			( ( mtn.getAdditionalInfo() == null ) ||
				!mtn.getAdditionalInfo().getIsIllegalMove() &&
				!mtn.getAdditionalInfo().getIsAnyParentIllegalMove() )
		  )
		{
			chessBoard.undoMove( mtn.getMove() );
		}
	}

	protected String getMoveString( String englishMoveString, ChessGameMove cgm )
	{
		ChessLanguageConfiguration clc = _parent.getChessViewConfiguration().getChessLanguageConfigurationToShow();
		String result = clc.translateMoveStringFromEnglish( englishMoveString, cgm);

		return( result );
	}
	
	protected String getMoveString( ChessBoard cb, ChessGameMove cgm ) throws ChessParserException
	{
		return( getMoveString( ChessMoveAlgebraicNotation.getInstance().getMoveString(cb, cgm ), cgm ) );
	}
	
	protected void addMoveToStringBuilderMoveTree( ChessBoard chessBoard,
											MoveTreeNode mt, MoveTreeNode parent, boolean isMainLine,
											boolean hasToWriteEllipsis ) throws ChessParserException
	{
		String numberOfMove = getNumberOfMoveString(mt.getLevel(), hasToWriteEllipsis );

		boolean commentForVariantSet = writeComment( null, mt.getCommentForVariant(), isMainLine, "\n" );

		addStringToStringBuilderMoveTree( numberOfMove );

		int initialMovePosition = _currentCharPosition;

		boolean illegalMove = false;

		String moveString = null;
		try
		{
			moveString = getMoveString(chessBoard, mt.getMove() );
		}
		catch( ChessParserException ex )
		{
			illegalMove = true;
			
			if( ( mt.getMove() != null ) &&
				( mt.getMove().getMoveToken() != null ) )
			{
				moveString = getMoveString( mt.getMove().getMoveToken().getString(), mt.getMove() );
			}
			else
			{
				moveString = "void";
			}
		}

		addStringToStringBuilderMoveTree( moveString );
//		int finalMovePosition = _currentCharPosition;

//		WrittenNodeInfo nodeInfo = new WrittenNodeInfo( mt, initialMovePosition, finalMovePosition, isMainLine );
//		addMoveAdditionalInfo( nodeInfo );

		if( ( mt.getMove() != null ) &&
			( mt.getMove().getMoveToken() != null ) )
		{
			Iterator<NAG> it = mt.getMove().getMoveToken().nagIterator();
			while( it.hasNext() )
			{
				NAG nag = it.next();
				if( !_isToWriteInTextPane )
					addStringToStringBuilderMoveTree( " " + nag.getStringForPGNfile() );
				else if( _parent.getChessViewConfiguration().getHasToShowNAGs() )
					addStringToStringBuilderMoveTree( nag.getStringToShow() );
			}
		}

		int finalMovePosition = _currentCharPosition;

		WrittenNodeInfo nodeInfo = new WrittenNodeInfo( mt, initialMovePosition, finalMovePosition, isMainLine );
		addMoveAdditionalInfo( nodeInfo );

		writeComment( nodeInfo, mt.getComment(), isMainLine, "" );

		if( commentForVariantSet )
		{
			nodeInfo.setLocationOfCommentForVariant(_initialPositionOfComment, _finalPositionOfComment);
		}
/*
		if( ( mt.getMove() != null ) && ( mt.getMove()._resultOfGame != null ) )
		{
			addStringToStringBuilderMoveTree( " " + mt.getMove()._resultOfGame );
		}
*/
		if( illegalMove && ! nodeInfo.getIsAnyParentIllegalMove() )
			nodeInfo.setIsIllegalMove(illegalMove);
	}

	protected boolean writeComment( WrittenNodeInfo nodeInfo, String comment,
									boolean isMainLine, String prefix )
	{
		boolean result = false;
		if( _parent.getChessViewConfiguration().getHasToShowComments() && ( comment != null ) )
		{
			if( !_isToWriteInTextPane )
				addStringToStringBuilderMoveTree( " {" );
			else if( _currentCharPosition > 0 )
				addStringToStringBuilderMoveTree( prefix );

			addStringToStringBuilderMoveTree( " " );

			int initialPositionOfComment = _currentCharPosition;
			addStringToStringBuilderMoveTree( comment );
			int finalPositionOfComment = _currentCharPosition-1;

			if( nodeInfo != null )
			{
				nodeInfo.setLocationOfComment(initialPositionOfComment, finalPositionOfComment);
			}
			else
			{
				_initialPositionOfComment = initialPositionOfComment;
				_finalPositionOfComment = finalPositionOfComment;
			}

			if( !_isToWriteInTextPane )
				addStringToStringBuilderMoveTree( " }" );

			addStringToStringBuilderMoveTree( prefix );

			result = true;
		}

		return( result );
	}

	protected String getNumberOfMoveString( int plyNumber, boolean hasToWriteEllipsis )
	{
		String result = "";
		if( plyNumber > 0 )
		{
			if( hasToWriteEllipsis && ( plyNumber % 2 == 0 ) ||
				( plyNumber % 2 == 1 ) )
			{
				result = String.valueOf( ( plyNumber - 1 ) / 2 + 1 );

				if( hasToWriteEllipsis && ( plyNumber % 2 == 0 ) )	result = result + "... ";
				else												result = result + ". ";
			}
		}
		return( result );
	}

	public String getGameMovesStringForLine( ChessGame cg, MoveTreeNode mtn,
											ChessViewConfiguration cvc ) throws ChessWriterException
	{
		String result = "";

		if( ( cg != null ) && (mtn != null ) )
		{
			ChessGame tmpCg = null;

			try
			{
				tmpCg = new ChessGame( null, new MoveTreeGame( cg.getInitialPosition() ), cvc );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}

			if( tmpCg != null )
			{
				LinkedList<MoveTreeNode> mtnList = new LinkedList<MoveTreeNode>();

				while( ( mtn != null ) && ( mtn.getParent() != null ) )
				{
					mtnList.addFirst( mtn );
					mtn = mtn.getParent();
				}

				Iterator<MoveTreeNode> it = mtnList.iterator();
				MoveTreeNode insertMtnTo = tmpCg.getMoveTreeGame();
				while( it.hasNext() )
				{
					MoveTreeNode mtnToInsert = it.next();
					insertMtnTo = insertMtnTo.simpleInsert( mtnToInsert.getMove(), mtnToInsert.getLevel() );
				}

				result = getGameMovesString( tmpCg, null );
			}
		}

		return( result );
	}


	public String getGameMovesStringForLine( ChessGame cg, List<ChessGameMove> listOfMoves ) throws ChessWriterException
	{
		StringBuilder sb = new StringBuilder();

		_parent = cg;
		ChessGamePosition initialPosition = cg.getInitialPosition();

		if( initialPosition != null )
		{
			try
			{
				ChessBoard chessBoard = new ChessBoard();
				chessBoard.setPosition( initialPosition );

				Iterator<ChessGameMove> it = listOfMoves.iterator();
				int firstPly = 1;
				if( ! chessBoard.getIsWhitesTurn() ) firstPly++;
				int plyMoveNumber = firstPly;

				while( it.hasNext() )
				{
					ChessGameMove cgm = it.next();

					if( chessBoard.getIsWhitesTurn() || ( plyMoveNumber == firstPly ) )
					{
						String numberOfMove = getNumberOfMoveString( plyMoveNumber, true );
						sb.append( numberOfMove );
					}

					String moveString = getMoveString(chessBoard, cgm );
					if( chessBoard != null )
						chessBoard.move( cgm );

					sb.append( moveString + " " );
					plyMoveNumber++;

	//				System.out.println( sb );
				}

			}
			catch( Throwable th )
			{
	//			th.printStackTrace();
	//			throw( new ChessWriterException( th.getMessage() ) );
				sb.append( th.getMessage() );
			}
		}
		else
		{
			sb.append( "Initial position not set for game." );
		}

		String result = sb.toString();
		return( result );
	}

}
