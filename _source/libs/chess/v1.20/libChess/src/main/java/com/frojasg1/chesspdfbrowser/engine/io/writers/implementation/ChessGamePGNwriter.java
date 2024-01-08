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

import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.io.writers.ChessGamePGNWriterObserver;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.NAG;
import com.frojasg1.general.string.StringFunctions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class ChessGamePGNwriter extends ChessGameViewWriter
{
	protected static int ADVISED_MAX_LINE_LENGTH = 80;

	protected ChessGamePGNWriterObserver _observer = null;
//	protected ChessMoveNotation _chessMoveNotation = null;

	protected StringBuilder _currentLine = null;

	public ChessGamePGNwriter( ChessGamePGNWriterObserver observer )
	{
		super( false );	// isToWriteInTextPane == false

		_observer = observer;
		_currentLine = new StringBuilder();
	}

	protected void addTagToStringBuilder( String tagKey, String tagValue )
	{
		addStringToStringBuilderMoveTree( "[" + tagKey + " \"" );
		addStringToStringBuilderMoveTree( tagValue );
		addStringToStringBuilderMoveTree( "\"]" );
		processCurrentLine( true );
	}

	@Override
	public String getGameHeaderString( ChessGame chessGame )
	{
		_currentLine = new StringBuilder();

		ChessGameHeaderInfo headerInfo = chessGame.getChessGameHeaderInfo();

		boolean onlyAddIfNotIsEmpty = false;
		for( int ii=0; ii<ChessGameHeaderInfo._mandatoryTagArray.length; ii++ )
		{
			String tagKey = ChessGameHeaderInfo._mandatoryTagArray[ii];
			addTagToHeaderString( headerInfo, tagKey, onlyAddIfNotIsEmpty );
		}

		onlyAddIfNotIsEmpty = true;
		for( int jj=0; jj<ChessGameHeaderInfo._optionalTagArrays.length; jj++ )
		{
			String[] groupOfTags = ChessGameHeaderInfo._optionalTagArrays[jj];
			for( int ii=0; ii<groupOfTags.length; ii++ )
			{
				String tagKey = groupOfTags[ii];
				addTagToHeaderString( headerInfo, tagKey, onlyAddIfNotIsEmpty );
			}
		}

//		addTagToHeaderString( headerInfo, ChessGameHeaderInfo.FEN_TAG, onlyAddIfNotIsEmpty );
		addTagToHeaderString( headerInfo, ChessGameHeaderInfo.CONTROL_NAME_TAG, onlyAddIfNotIsEmpty );
		addTagToHeaderString( headerInfo, ChessGameHeaderInfo.FILE_NAME_TAG, onlyAddIfNotIsEmpty );

		return( null );
	}

	protected void addTagToHeaderString( ChessGameHeaderInfo headerInfo, String tagName,
										boolean onlyAddIfNotIsEmpty )
	{
		String tagValue = headerInfo.get( tagName );
		if( !( onlyAddIfNotIsEmpty &&
				( ( tagValue == null ) || tagValue.isEmpty() )
			  )
			)
		{
			addTagToStringBuilder( tagName, tagValue );
		}
	}

	protected boolean canHaveCommentsForVariant( MoveTreeNode node )
	{
		return( MoveTreeNodeUtils.instance().canHaveCommentsForVariant( node ) );
	}

	@Override
	protected void addMoveToStringBuilderMoveTree( ChessBoard chessBoard,
											MoveTreeNode mt, MoveTreeNode parent, boolean isMainLine,
											boolean hasToWriteEllipsis ) throws ChessParserException
	{
		String numberOfMove = getNumberOfMoveString(mt.getLevel(), hasToWriteEllipsis );

		if( canHaveCommentsForVariant( mt ) )
			addCommentWithBrackets( "", mt.getCommentForVariant(), " " );

		addStringToStringBuilderMoveTree( numberOfMove );

		String moveString = "void";
/*
		if( ( mt.getAdditionalInfo() == null ) ||
			( !mt.getAdditionalInfo().getIsIllegalMove() &&
				!mt.getAdditionalInfo().getIsAnyParentIllegalMove() )
		  )
		{
			moveString = ChessMoveAlgebraicNotation.getInstance().getMoveString( chessBoard, mt.getMove() );
		}
		else
*/
		if( mt.getMove().getMoveToken() != null )
		{
			moveString = mt.getMove().getMoveToken().getString();
		}

		addStringToStringBuilderMoveTree( moveString );

		if( ( mt.getMove() != null ) &&
			( mt.getMove().getMoveToken() != null ) )
		{
			// for a comment starting a subvariant, previous to any move of that variant
			Iterator<NAG> it = mt.getMove().getMoveToken().nagIterator();
			while( it.hasNext() )
			{
				NAG nag = it.next();
				if( !_isToWriteInTextPane )
					addStringToStringBuilderMoveTree( " " + nag.getStringForPGNfile() );
				else
					addStringToStringBuilderMoveTree( nag.getStringToShow() );
			}
		}

		addCommentWithBrackets( " ", mt.getComment(), "" );
/*
		if( ( mt.getMove() != null ) && ( mt.getMove()._resultOfGame != null ) )
		{
			addStringToStringBuilderMoveTree( " " + mt.getMove()._resultOfGame );
		}
*/
	}

	protected void addCommentWithBrackets( String prefix, String comment, String suffix )
	{
		if( comment != null )
		{
			addStringToStringBuilderMoveTree( prefix );
			addStringToStringBuilderMoveTree( "{" );
			addStringToStringBuilderMoveTree( comment );
			addStringToStringBuilderMoveTree( "}" );
			addStringToStringBuilderMoveTree( suffix );
		}
	}

	@Override
	protected void addStringToStringBuilderMoveTree( String str )
	{
		if( ( str != null ) && ( str.length() != 0 ) )
		{
			_currentLine.append( str );
			processCurrentLine( false );
		}
	}

	@Override
	protected void endOfGame()
	{
		processCurrentLine( true );
	}

	protected boolean isTag(StringBuilder sb)
	{
		Character firstChar = StringFunctions.instance().getCharAt(sb, 0);
		return( ( firstChar != null ) && ( firstChar == '[' ) );
	}

	protected void processCurrentLine( boolean forced )
	{
		boolean isTag = isTag(_currentLine);
		if( forced ||
			!isTag && ( _currentLine.length() > ADVISED_MAX_LINE_LENGTH ) )
		{
			if( !isTag && ( _currentLine.length() > ADVISED_MAX_LINE_LENGTH ) )
			{
				Object[] splitted = splitCurrentLine( _currentLine );
				for( int ii=0; ii<(splitted.length-1); ii++ )
				{
					addLineToFile( (String) splitted[ii] );
				}
				_currentLine.append( splitted[ splitted.length-1 ] );
			}
			else
			{
				addLineToFile( _currentLine.toString() );
			}
		}
	}

	protected void addLineToFile( String line )
	{
		_stringBuilderMoveTree.append( line + "\n" );
		if( _observer != null )
			_observer.writeLine_pgnWriter( line.toString() );

		_currentLine = new StringBuilder();
	}
	
	protected Object[] splitCurrentLine( StringBuilder sb )
	{
		List<String> splittedList = new ArrayList<String>();
		int pos = 0;
		boolean continueProcessing = true;
		while( continueProcessing )
		{
			if( sb.length() - pos > ADVISED_MAX_LINE_LENGTH )
			{
				int pos1 = pos + ADVISED_MAX_LINE_LENGTH - 1;
				pos1 = StringFunctions.instance().lastIndexOfAnyChar(sb, " {}", pos1 );
				if( pos1 == -1 )
				{
					pos1 = pos + ADVISED_MAX_LINE_LENGTH;
					pos1 = StringFunctions.instance().indexOfAnyChar(sb, " {}", pos1 );
					if( pos1 == -1 )
					{
						pos1 = sb.length() - 1;
					}
				}
				splittedList.add( sb.substring( pos, pos1 + 1 ) );
				pos = pos1 + 1;
				continueProcessing = ( pos < sb.length() );
			}
			else
			{
				splittedList.add( sb.substring( pos, sb.length() ) );
				continueProcessing = false;
			}
		}

		return( splittedList.toArray() );
	}
}
