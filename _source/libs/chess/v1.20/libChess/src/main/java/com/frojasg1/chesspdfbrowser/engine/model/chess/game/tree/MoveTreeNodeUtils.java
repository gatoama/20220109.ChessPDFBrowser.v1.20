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
package com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree;

import com.frojasg1.chesspdfbrowser.analysis.SubvariantAnalysisResult;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessGamePositionException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessModelException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessPieceCreationException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessWriterException;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.move.LongAlgebraicNotationMove;
import com.frojasg1.chesspdfbrowser.enginewrapper.variant.EngineMoveVariant;
import com.frojasg1.general.ExecutionFunctions;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class MoveTreeNodeUtils
{
	protected static MoveTreeNodeUtils _instance;

	protected ChessBoard _chessBoard = new ChessBoard();
	protected ChessViewConfiguration _chessViewConfigurationEMP = new ChessViewConfigurationDummy();

	public static void changeInstance( MoveTreeNodeUtils inst )
	{
		_instance = inst;
	}

	public static MoveTreeNodeUtils instance()
	{
		if( _instance == null )
			_instance = new MoveTreeNodeUtils();
		return( _instance );
	}

	public boolean canHaveCommentsForVariant( MoveTreeNode node )
	{
		boolean result = false;

		if( node != null )
		{
			if( node.getParent() instanceof MoveTreeGame )
				result = true;
			else if( node.getParent() != null )
				result = !node.isFirstChild() && ( getNumberOfSibilings( node ) > 0 );
		}

		return( result );
	}

	public int getNumberOfSibilings( MoveTreeNode node )
	{
		int result = 0;
		if( ( node != null ) && ( node.getParent() != null ) )
			result = node.getParent().getNumberOfChildren() - 1;

		return( result );
	}

	public MoveTreeNode getLastMoveOfMainVariation( MoveTreeGame mtg )
	{
		MoveTreeNode result = mtg;
		while( ( result != null ) && ( result.getNumberOfChildren() > 0 ) )
			result = result.getChild(0);

		return( result );
	}

	protected ChessGame getChessGame( ChessGamePosition cgp, List<ChessGameMove> listOfMoves )
	{
		MoveTreeGame mtg = new MoveTreeGame( cgp );
		ChessGame result = null;
		try
		{
			result = new ChessGame( null, mtg, _chessViewConfigurationEMP );
			mtg.insertMoves(listOfMoves);
			result.start();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	public synchronized String getFen( MoveTreeNode mtn )
	{
		String result = null;

		if( mtn != null )
		{
			try
			{
				ChessGame cg = getChessGame( mtn.getChessGame().getInitialPosition(), mtn.getGameMoveList() );
				cg.getChessGameTreeAdditionalInfo().updateAdditionalInfo();

				ChessGamePosition position = cg.getInitialPosition();
				if( position != null )
				{
					_chessBoard.doMovesFromInitialPosition( position,
										cg.getMoveTreeGame().getEndOfMainLine().getGameMoveList() );
					result = _chessBoard.getCurrentPosition().getFenString();
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}

		return( result );
	}

	public boolean startsFromInitialPosition( MoveTreeNode mtn )
	{
		boolean result = false;

		if( mtn != null )
		{
			try
			{
				MoveTreeGame ancestor = (MoveTreeGame) mtn.getAncestor();
				result = ( ancestor.getLevel() == 0 ) &&
						ancestor.getInitialPosition().getFenString().equals( ChessGamePosition.INITIAL_POSITION_FEN_STRING );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}

		return( result );
	}

	public void setInitialBaseBoardFen( String baseBoardFen, ChessGame cg )
	{
		if( ( cg != null ) && ( baseBoardFen != null ) )
		{
			MoveTreeGame mtg = cg.getMoveTreeGame();
			ChessGamePosition position = new ChessGamePosition();
			position.parseBoardFenPositionBase(baseBoardFen);
			int moveNumber = mtg.getMoveNumber();
			if( mtg.getLevel() % 2 == 0 )
			{
				position.setIsWhitesTurn();
				moveNumber++;
			}
			else
				position.setIsBlacksTurn();
			position.setMoveNumber( moveNumber );

			String fenStr = ExecutionFunctions.instance().safeFunctionExecution( () -> position.getFenString() );
			if( fenStr != null )
				cg.setInitialPosition( position );
		}
	}

	public SubvariantAnalysisResult createSubvariantAnalysisResult( String fenString,
																	ChessViewConfiguration chessViewConf,
																	EngineMoveVariant emv )
	{
		SubvariantAnalysisResult result = null;

		if( emv != null )
		{
			try
			{
				ChessGame cg = ExecutionFunctions.instance().safeFunctionExecution( () -> new ChessGame( chessViewConf ) );
				ChessGamePosition cgp = ExecutionFunctions.instance().safeFunctionExecution( () -> new ChessGamePosition( fenString ) );
				cg.setInitialPosition(cgp);

//				ChessBoard cb = cg.getChessBoard();

				for( LongAlgebraicNotationMove move: emv.getListOfMoves() )
				{
					ChessGameMove cgm = new ChessGameMove( move.getFromColumn(), move.getFromRow(),
															move.getToColumn(), move.getToRow() );
					if( isMoveAPromotion( move ) )
						cgm.setPromotionPiece( move.getPromotionPieceCode().toString() );

					if( cg.getChessBoard().isLegalThisMove( cgm, false ) )
						makeANewMove( cg, cgm );
					else
					{
						int ii=0;
					}
				}

				result = new SubvariantAnalysisResult( emv.getScore(), cg );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
		else
			result = new SubvariantAnalysisResult( Double.NaN, null );

		return( result );
	}

	protected boolean isMoveAPromotion( LongAlgebraicNotationMove move )
	{
		boolean result = false;
		
		if( move != null )
			result = ( move.getPromotionPieceCode() != null );

		return( result );
	}

	protected void makeANewMove( ChessGame cg, ChessGameMove cgm ) throws ChessPieceCreationException, ChessMoveException, ChessModelException, ChessGamePositionException, ChessWriterException
	{
		List<ChessGameMove> list = cg.getChessBoard().getListOfMoves();
		list.add(cgm);
		cg.insertMoves(list);
		cg.setCurrentListOfMoves(list);
	}

	protected ChessGameHeaderInfo clone( ChessGameHeaderInfo input )
	{
		ChessGameHeaderInfo result = null;
		if( input != null )
			result = new ChessGameHeaderInfo( input );

		return( result );
	}

	public ChessGamePosition clone( ChessGamePosition input )
	{
		ChessGamePosition result = null;
		if( input != null )
			result = ExecutionFunctions.instance().safeFunctionExecution( () -> new ChessGamePosition( input.getFenString() ) );

		return( result );
	}

	protected MoveTreeGame cloneEmptyWithInitialPosition( MoveTreeGame input )
	{
		MoveTreeGame result = null;
		if( input != null )
		{
			ChessGamePosition position = clone( input.getInitialPosition() );

			result = new MoveTreeGame( position );
		}

		return( result );
	}

	public ChessGame cloneEmptyWithInitialPosition(ChessGame inputGame)
	{
		ChessGame result = null;
		if( inputGame != null )
		{
			ChessGameHeaderInfo headerInfo = clone( inputGame.getChessGameHeaderInfo() );

			MoveTreeGame inputMtg = inputGame.getMoveTreeGame();
			MoveTreeGame resultMtg = cloneEmptyWithInitialPosition( inputMtg );

			result = ExecutionFunctions.instance().safeFunctionExecution(
				() -> new ChessGame(headerInfo, resultMtg, inputGame.getChessViewConfiguration() )
			);
		}

		return( result );
	}

	public ChessGame copyMainLineGame( ChessGame inputGame )
	{
		ChessGame result = cloneEmptyWithInitialPosition(inputGame);
		if( result != null )
			copyMainLine( inputGame.getMoveTreeGame(), result.getMoveTreeGame() );

		return( result );
	}

	protected ChessGameMove clone( ChessGameMove input )
	{
		return( new ChessGameMove( input ) );
	}

	public void copyMainLine( MoveTreeGame input, MoveTreeGame output )
	{
		if( ( input != null ) && ( output != null ) )
		{
			MoveTreeNode currentInput = input.getFirstChild();
			MoveTreeNode currentOuptut = output;

			while( currentInput != null )
			{
				currentOuptut = currentOuptut.insertFirst( clone( currentInput.getMove() ) );
				currentInput = currentInput.getFirstChild();
			}
		}
	}

	public int getNumberOfPliesOfMainLine( ChessGame cg )
	{
		int result = 0;
		if( cg != null )
		{
			MoveTreeNode mtn = cg.getMoveTreeGame().getLastNodeOfMainVariant();
			if( mtn != null )
				result = mtn.getLevel() - cg.getMoveTreeGame().getLevel();
		}
		return( result );
	}

	public ChessGamePosition copy( ChessGamePosition that )
	{
		ChessGamePosition result = null;
		if( that != null )
			result = ExecutionFunctions.instance().safeFunctionExecution( () -> new ChessGamePosition( that.getFenString() ) );
		return( result );
	}

	public String getMostCompleteFenString( ChessGamePositionBase positionDetected )
	{
		String result = null;
		if( positionDetected != null )
		{
			if( positionDetected instanceof ChessGamePosition )
			{
				ChessGamePosition cgp = (ChessGamePosition) positionDetected;
				if( ! cgp.canBeBaseFenString() )
					result = ExecutionFunctions.instance().safeFunctionExecution( () -> cgp.getFenString() );
			}

			if( result == null )
				result = positionDetected.getFenBoardStringBase();
		}
		return( result );
	}
}
