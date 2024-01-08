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

import com.frojasg1.chesspdfbrowser.engine.configuration.TagExtractorConfiguration;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ImagePositionController;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.progress.GeneralUpdatingProgress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RawImprovedChessGameParser2 extends RawChessGameParser
{
	ChessViewConfiguration _chessViewConfiguration = null;

	public RawImprovedChessGameParser2( ChessLanguageConfiguration clc,
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
		if( startsFromFirstMoveOfGame( _nextMoveToken ) )
			improvedParseGameMoves( cg );
		else
			super.parseGameMoves( cg );
	}

	protected ExperimentalMovePlacer createExperimentalMovePlacer()
	{
		return( new ExperimentalMovePlacer(_chessViewConfiguration) );
	}

	protected void improvedParseGameMoves( ChessGame cg ) throws ChessParserException, CancellationException
	{
		ExperimentalMovePlacer emp = createExperimentalMovePlacer();

		while( hasNextMoveToken() && emp.addMove( createChessGameMove( cg, _nextMoveToken ) ) )
		{
			getCurrentPageAndUpdateProgress();
			nextMoveToken();
		}

		MoveTreeGame outputMtg = cg.getMoveTreeGame();

		emp.addMovesToMoveTree( outputMtg );
	}

	protected static class ExperimentalMoveTreeNode extends MoveTreeNode
	{
		List< ExperimentalMoveTreeNode > _possibleParents = new ArrayList<>();
		List< ExperimentalMoveTreeNode > _possibleChildren = new ArrayList<>();

		public ExperimentalMoveTreeNode( ChessGameMove cgm, int level )
		{
			super( null, cgm, level );
		}

		public boolean isEmpty()
		{
			return( ( getParent() == null ) &&
					( this.getNumberOfChildren() == 0 ) &&
					( this.getNumberOfPossibleChildren() == 0 ) &&
					( this.getNumberOfPossibleParents() == 0 ) );
		}

		public void addChildFirst( ExperimentalMoveTreeNode node )
		{
			if( node != null )
				_children.add(0, node);
		}

		@Override
		public ExperimentalMoveTreeNode getParent()
		{
			return( (ExperimentalMoveTreeNode) super.getParent() );
		}

		public int getNumberOfPossibleParents()
		{
			return( _possibleParents.size() );
		}

		public void addPossibleParent( ExperimentalMoveTreeNode possibleParent )
		{
			if( canAddPossibleParent( possibleParent ) )
			{
				_possibleParents.add( possibleParent );
				possibleParent.addPossibleChild( this );
			}
		}

		public void addPossibleChild( ExperimentalMoveTreeNode child )
		{
			if( canAddPossibleChild( child ) )
			{
				_possibleChildren.add( child );
//				child.addPossibleParent( this );
			}
		}

		public int getNumberOfPossibleChildren()
		{
			return( _possibleChildren.size() );
		}

		public Iterator<ExperimentalMoveTreeNode> getPossibleParentsIterator()
		{
			return( _possibleParents.iterator() );
		}

		public Iterator<ExperimentalMoveTreeNode> getPossibleChildrenIterator()
		{
			return( _possibleChildren.iterator() );
		}

		public ExperimentalMoveTreeNode getPossibleChild( int index )
		{
			ExperimentalMoveTreeNode result = null;
			if( ( getNumberOfPossibleChildren() > index ) &&
				( index >= 0 ) )
			{
				result = _possibleChildren.get(index);
			}

			return( result );
		}

		protected ExperimentalMoveTreeNode getChild( ChessGameMove cgm )
		{
			return( (ExperimentalMoveTreeNode) findChild(cgm) );
		}

		protected boolean stringsEqual( String str1, String str2 )
		{
			return( ( str1==null ) && ( str2==null ) ||
					( str1!=null ) && ( str2 != null ) &&
					str1.equals( str2 ) );
		}

		protected String getMoveString( )
		{
			String result = null;
			if( ( getMove() != null ) &&
				( getMove().getMoveToken() != null ) )
			{
				result = getMove().getMoveToken().getString();
			}

			return( result );
		}

		public void removePossibleParent( ExperimentalMoveTreeNode discardedParent )
		{
			if( _possibleParents.contains( discardedParent ) )
			{
				_possibleParents.remove( discardedParent );
				discardedParent.removePossibleChild( this );
			}
		}

		protected void promoteParent()
		{
			if( _possibleParents.size() > 0 )
				promotePossibleParent( _possibleParents.get(0) );

			Iterator<MoveTreeNode> it = getChildrenIterator();
			while( it.hasNext() )
			{
				ExperimentalMoveTreeNode item = (ExperimentalMoveTreeNode) it.next();
				item.promoteParent();
			}

			int possibleChildrenSize = getNumberOfPossibleChildren();
			int ii=0;
			while( ii<possibleChildrenSize )
			{
				getPossibleChild(ii).promoteParent();
				if( getNumberOfPossibleChildren() == possibleChildrenSize )
					ii++;
				else
					possibleChildrenSize = getNumberOfPossibleChildren();
			}
		}

		protected void promotePossibleParent( ExperimentalMoveTreeNode parent )
		{
			if( _possibleParents.contains( parent ) )
			{
				Iterator<ExperimentalMoveTreeNode> it = _possibleParents.iterator();
				while( it.hasNext() )
				{
					ExperimentalMoveTreeNode item = it.next();
					if( item != parent )
					{
						removePossibleParent( item );
						it = _possibleParents.iterator();	// let's start again.
					}
				}

				parent.promotePossibleChild(this);

				_possibleParents.remove( parent );
			}
		}

		public void removeAllPossibleChildren()
		{
			Iterator<ExperimentalMoveTreeNode> it = _possibleChildren.iterator();

			while( it.hasNext() )
			{
				removePossibleChild( it.next() );
			}
		}

		public void setParent( ExperimentalMoveTreeNode parent )
		{
			_parent = parent;
		}

		public void addChild( ExperimentalMoveTreeNode child )
		{
			if( child != null )
			{
				child.setParent( this );
				_children.add( child );
			}
		}

		protected void promotePossibleChild( ExperimentalMoveTreeNode child )
		{
			if( _possibleChildren.contains( child ) )
			{
				removePossibleChild( child );
				addOrCopyChild( child );
			}
		}

		public void addOrCopyChild( ExperimentalMoveTreeNode child )
		{
			if( child != null )
			{
				ExperimentalMoveTreeNode childNodeWhereToCopy = getChild( child.getMove() );
				if( childNodeWhereToCopy == null )
					addChild( child );
				else
				{
					Iterator<ExperimentalMoveTreeNode> it = child.getPossibleChildrenIterator();
					while( it.hasNext() )
					{
						ExperimentalMoveTreeNode item = it.next();
						this.addPossibleChild( item );
					}

					Iterator<MoveTreeNode> it2 = _children.iterator();
					while( it.hasNext() )
					{
						ExperimentalMoveTreeNode item = (ExperimentalMoveTreeNode) it2.next();
						childNodeWhereToCopy.addOrCopyChild( item );
					}
				}
			}
		}

		public void removePossibleChild( ExperimentalMoveTreeNode child )
		{
			_possibleChildren.remove( child );
		}

		protected boolean canAddPossibleChild( ExperimentalMoveTreeNode child )
		{
			return( ( child.getLevel() == ( getLevel() + 1 ) ) &&
//					( getChild( child.getMove() ) == null ) &&
					!_possibleChildren.contains( child ) );
		}

		public boolean canAddPossibleParent( ExperimentalMoveTreeNode possibleParent )
		{
			return( ( possibleParent != null ) &&
					( possibleParent.getLevel() == ( getLevel() - 1 ) ) &&
					( getParent() == null ) &&
					!_possibleParents.contains( possibleParent ) &&
					possibleParent.canAddPossibleChild( this ) );
		}
	}

	protected static class SequenceOfMoves
	{
		protected boolean _isLegal = false;
		protected LinkedList< ExperimentalMoveTreeNode > _listOfNodes = new LinkedList<>();
		protected LinkedList< ChessGameMove > _listOfMoves = new LinkedList<>();

		public SequenceOfMoves()
		{}

		public SequenceOfMoves( SequenceOfMoves other )
		{
			_isLegal = other.getIsLegal();

			Iterator< ExperimentalMoveTreeNode > it = other._listOfNodes.descendingIterator();
			while( it.hasNext() )
				addNode( it.next() );
		}

		public List< ChessGameMove > getListOfMoves()
		{
			return( _listOfMoves );
		}

		public List< ExperimentalMoveTreeNode > getListOfNodes()
		{
			return( _listOfNodes );
		}

		protected ChessGameMove copy( ChessGameMove other )
		{
			ChessGameMove result = null;
			if( other != null )
				result = new ChessGameMove(other);
			return( result );
		}

		public void addNode( ExperimentalMoveTreeNode node )
		{
			_listOfNodes.addFirst( node );
			_listOfMoves.addFirst( copy( node.getMove() ) );
		}

		public void setIsLegal( boolean value )
		{
			_isLegal = value;
		}

		public boolean getIsLegal()
		{
			return( _isLegal );
		}
	}

	protected class ExperimentalMovePlacer
	{
		protected static final int MAX_NUM_OF_OPTIONS = 16;

		protected ChessBoard _chessBoard = new ChessBoard();

		protected ExperimentalMoveTreeNode _gameMoveTreeNode = null;
		protected ExperimentalMoveTreeNode _lastMove = null;

		protected ChessViewConfiguration _chessViewConfigurationEMP = null;

//		protected Map< ExperimentalMoveTreeNode, PreRequirementForMove > _preRequirementMap = new HashMap<>();

		public ExperimentalMovePlacer( ChessViewConfiguration cvc )
		{
			_chessViewConfigurationEMP = cvc;
			_gameMoveTreeNode = new ExperimentalMoveTreeNode( null, 0 );
			_lastMove = _gameMoveTreeNode;
		}

		public boolean addMove( ChessGameMove cgm ) throws CancellationException
		{
			boolean result = false;

			if( ( cgm.getMoveToken().getNumberOfPly() == 1 ) &&
				!_gameMoveTreeNode.isEmpty() )
			{
				return( result );
			}

			ExperimentalMoveTreeNode newNode = createExperimentalMoveTreeNode( cgm );
/*
			if( _lastMove.getLevel() == newNode.getLevel() &&
				_lastMove.isFirstChild() )
			{
				_lastMove.getParent().addChildFirst( newNode );
			}
*/
			List<ExperimentalMoveTreeNode> listOfPossibleParents = findPossibleParents( newNode );

			if( listOfPossibleParents.size() > 0 )
			{
				result = addMoveToTree( newNode, listOfPossibleParents, _gameMoveTreeNode );

				if( result )
					_lastMove = newNode;
			}

			return( result );
		}

		protected boolean addMoveToTree( ExperimentalMoveTreeNode newNode,
											List<ExperimentalMoveTreeNode> listOfPossibleParents,
											ExperimentalMoveTreeNode gameTreeNode )
		{
			boolean result = true;

			if( listOfPossibleParents.size() == 1 )
				listOfPossibleParents.get(0).addChild(newNode);
			else
			{
				result = false;

				ExperimentalMoveTreeNode suitableParent = findSuitableParent( newNode, listOfPossibleParents );
				if( suitableParent != null )
					suitableParent.addChild( newNode );
//				addPossibleParentsToNode( newNode, listOfPossibleParents );

				result = ( suitableParent != null );
			}


			return( result );
		}

		protected SequenceOfMoves createSequenceOfMoves()
		{
			return( new SequenceOfMoves() );
		}

		protected ExperimentalMoveTreeNode findSuitableParent( ExperimentalMoveTreeNode newNode,
											List<ExperimentalMoveTreeNode> listOfPossibleParents )
		{
			ExperimentalMoveTreeNode result = null;

			Iterator<ExperimentalMoveTreeNode> it = listOfPossibleParents.iterator();
			while( ( result == null ) && it.hasNext() )
			{
				ExperimentalMoveTreeNode item = it.next();

				SequenceOfMoves seq = createSequenceOfMoves();
				seq.addNode( newNode );
				addMovesToSequenceOfMoves( seq, item );

				if( isLegal( seq ) )
					result = item;
				else
					result = null;
			}

			return( result );
		}

		protected void addMovesToSequenceOfMoves( SequenceOfMoves seq, ExperimentalMoveTreeNode item )
		{
			ExperimentalMoveTreeNode node = item;
			while( ( node != null ) && ( node.getMove() != null ) )
			{
				seq.addNode(node);
				node = node.getParent();
			}
		}

		protected boolean findSuitableTree( ExperimentalMoveTreeNode newNode,
											List<ExperimentalMoveTreeNode> listOfPossibleParents,
											ExperimentalMoveTreeNode gameTreeNode )
		{
			boolean result = true;

			return( result );
		}

		protected ChessGame getChessGame( List<ChessGameMove> listOfMoves )
		{
			MoveTreeGame mtg = new MoveTreeGame( ChessGamePosition.getInitialPosition() );
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
/*
		protected void createSequenceOfMoves( ExperimentalMoveTreeNode node,
											List<SequenceOfMoves> result,
											SequenceOfMoves somModel )
		{
			if( node != null )
			{
//				SequenceOfMoves som = createSequenceOfMoves( somModel );
				som.addNode( node );

				if( node.getLevel() == 1 )
					result.add( som );
				else
				{
					createSequenceOfMoves( node.getParent(), result, som );

					Iterator< ExperimentalMoveTreeNode > it = node.getPossibleParentsIterator();
					while( it.hasNext() )
						createSequenceOfMoves( it.next(), result, som );
				}
			}
		}
*/
		protected boolean isLegal( SequenceOfMoves som )
		{
			boolean result = false;
			if( som != null )
			{
				try
				{
					ChessGame cg = getChessGame( som.getListOfMoves() );
					cg.getChessGameTreeAdditionalInfo().updateAdditionalInfo();

					_chessBoard.doMovesFromInitialPosition( ChessGamePosition.getInitialPosition(),
															cg.getMoveTreeGame().getEndOfMainLine().getGameMoveList() );
					result = true;
				}
				catch( Exception ex )
				{
					result = false;
				}
			}

			return( result );
		}

		protected void addPossibleParentsToNode( ExperimentalMoveTreeNode newNode,
												List<ExperimentalMoveTreeNode> listOfPossibleParents )
		{
			if( ( listOfPossibleParents != null ) && ( newNode != null ) )
			{
				Iterator<ExperimentalMoveTreeNode> it = listOfPossibleParents.iterator();
				while( it.hasNext() )
				{
					ExperimentalMoveTreeNode item = it.next();
					newNode.addPossibleParent(item);
				}
			}
		}

		protected ExperimentalMoveTreeNode createExperimentalMoveTreeNode( ChessGameMove cgm )
		{
			return( new ExperimentalMoveTreeNode( cgm, cgm.getMoveToken().getNumberOfPly() ) );
		}

		public void addMovesToMoveTree( MoveTreeGame outputMtg )
		{
			_gameMoveTreeNode.promoteParent();
			addMovesToMoveTree_internal( outputMtg, _gameMoveTreeNode, 0 );
		}

		protected List<ExperimentalMoveTreeNode> findPossibleParents( ExperimentalMoveTreeNode newNode )
		{
			List<ExperimentalMoveTreeNode> result = new ArrayList<>();

			findPossibleParents_internal( newNode, result, _lastMove, null );

			int indexToErase = MAX_NUM_OF_OPTIONS;
			if( result.size() > 0 )
			{
				MoveTreeNode node = result.get( result.size() - 1 );
				if( node.isMainLine() )
					indexToErase--;
			}

			while( result.size() > MAX_NUM_OF_OPTIONS )
				result.remove( indexToErase );

			return( result );
		}

		protected void findPossibleParents_internal( ExperimentalMoveTreeNode newNode,
													List<ExperimentalMoveTreeNode> result,
													ExperimentalMoveTreeNode existingNode,
													ExperimentalMoveTreeNode previousExistingNode )
		{
			addPossibleParentsOfBranch( result, existingNode, previousExistingNode,	newNode.getLevel() - 1 );

//			if( result.size() < MAX_NUM_OF_OPTIONS )
			{
				if( existingNode.getParent() != null )
				{
					findPossibleParents_internal( newNode, result, existingNode.getParent(), existingNode );
				}
				else
				{
					Iterator<ExperimentalMoveTreeNode> it = existingNode.getPossibleParentsIterator();
					while( ( result.size() < 4 ) && it.hasNext() )
						findPossibleParents_internal( newNode, result, it.next(), existingNode );
				}
			}

//			while( result.size() > MAX_NUM_OF_OPTIONS )
//				result.remove( MAX_NUM_OF_OPTIONS );
		}

		protected void addPossibleParentsOfBranch( List<ExperimentalMoveTreeNode> result,
													ExperimentalMoveTreeNode existingNode,
													ExperimentalMoveTreeNode previousExistingNode,
													int level )
		{
			if( existingNode.getLevel() == level )
			{
				if( existingNode != previousExistingNode )
				{
					if( _lastMove.getLevel() == level - 1 )
					{
						if( ( existingNode.getNumberOfPossibleChildren() == 0 ) &&
							( existingNode.getNumberOfChildren() == 0 ) )
						{
							result.add( existingNode );
						}
					}
					else
						result.add( existingNode );
				}
			}
			else
			{
				if( existingNode.getNumberOfChildren() > 0 )
				{
					addPossibleParentsOfBranch( result,
													(ExperimentalMoveTreeNode) existingNode.getChild(0),
													existingNode,
													level );
				}
			}
		}

		public void addMovesToMoveTree_internal( MoveTreeNode outputMtg,
													ExperimentalMoveTreeNode inputEmtg,
													int level )
		{
			for( int ii=0; ii<inputEmtg.getNumberOfChildren(); ii++ )
			{
				addMovesToMoveTree_internal( simpleInsert( outputMtg, inputEmtg.getChild(ii).getMove(),
																		level + 1 ),
											(ExperimentalMoveTreeNode) inputEmtg.getChild(ii),
											level + 1
											);
			}
		}
	}
}
