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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Usuario
 */
public class RawImprovedChessGameParser extends RawChessGameParser
{
	ChessViewConfiguration _chessViewConfiguration = null;

	public RawImprovedChessGameParser( ChessLanguageConfiguration clc,
										GeneralUpdatingProgress gup,
										ChessViewConfiguration cvc,
										TagExtractorConfiguration tagExtractorConfiguration,
										TagsExtractor tagsExtractor,
										ImagePositionController controller,
										String pdfBaseFileName )
	{
		super( clc, gup, tagExtractorConfiguration, tagsExtractor, controller, pdfBaseFileName );

		_chessViewConfiguration = cvc;

		_gup = gup;
	}

	@Override
	protected void parseGameMoves( ChessGame cg ) throws ChessParserException, CancellationException
	{
		if( startsFromFirstMoveOfGame( _nextMoveToken ) )
			improvedParseGameMoves( cg );
		else
			super.parseGameMoves( cg );
	}

	protected StatusOfImprovedParserParsedGame createStatusOfImprovedParserParsedGame()
	{
		return( new StatusOfImprovedParserParsedGame(_chessViewConfiguration) );
	}

	protected void improvedParseGameMoves( ChessGame cg ) throws ChessParserException, CancellationException
	{
		StatusOfImprovedParserParsedGame status = createStatusOfImprovedParserParsedGame();

		while( hasNextMoveToken() && status.addMove( createChessGameMove( cg, _nextMoveToken ) ) )
			nextMoveToken();

		if( _nextMoveToken != null )
			giveBack( _nextMoveToken );

		MoveTreeGame outputMtg = cg.getMoveTreeGame();

		status.addMovesToMoveTree( outputMtg );
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
					( ( getNumberOfChildren() + getNumberOfPossibleChildren() +
						getNumberOfTotalElements() + getNumberOfPossibleParents() ) == 0 )
					);
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

		public ExperimentalMoveTreeNode getNodeAt( int index )
		{
			return( _listOfNodes.get( index ) );
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

	protected class StatusOfImprovedParserParsedGame
	{
		protected static final int MAX_NUM_OF_OPTIONS = 3;

		protected ChessBoard _chessBoard = new ChessBoard();

		protected ExperimentalMoveTreeNode _gameMoveTreeNode = null;
		protected ExperimentalMoveTreeNode _lastMove = null;

		protected ChessViewConfiguration _chessViewConfigurationEMP = null;

		public StatusOfImprovedParserParsedGame( ChessViewConfiguration cvc )
		{
			_chessViewConfigurationEMP = cvc;
			_gameMoveTreeNode = new ExperimentalMoveTreeNode( null, 0 );
			_lastMove = _gameMoveTreeNode;
		}

		public void setLastMove( ExperimentalMoveTreeNode lastMove )
		{
			_lastMove = lastMove;
		}

		public ExperimentalMoveTreeNode getLastMove()
		{
			return( _lastMove );
		}

		public boolean addMove( ChessGameMove cgm ) throws CancellationException
		{
			boolean result = false;

			if( ( cgm.getMoveToken().getNumberOfPly() == 1 ) &&
				! _gameMoveTreeNode.isEmpty() )
			{
				return( result );
			}

			getCurrentPageAndUpdateProgress( );

			ExperimentalMoveTreeNode newNode = createExperimentalMoveTreeNode( cgm );
			List<ExperimentalMoveTreeNode> listOfPossibleParents = findPossibleParents( newNode );

			getCurrentPageAndUpdateProgress( );

			if( listOfPossibleParents.size() > 0 )
			{
				if( listOfPossibleParents.size() == 1 )
					listOfPossibleParents.get(0).addChild(newNode);
				else
					addPossibleParentsToNode( newNode, listOfPossibleParents );

				getCurrentPageAndUpdateProgress( );

				result = checkAndAddNewNodeAndRemoveUnsuitableOldParents( newNode, listOfPossibleParents );
			}

			getCurrentPageAndUpdateProgress( );

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

		protected SequenceOfMoves createSequenceOfMoves()
		{
			return( new SequenceOfMoves() );
		}

		protected SequenceOfMoves createSequenceOfMoves( SequenceOfMoves other )
		{
			return( new SequenceOfMoves( other ) );
		}

		protected boolean checkAndAddNewNodeAndRemoveUnsuitableOldParents( ExperimentalMoveTreeNode newNode,
																			List<ExperimentalMoveTreeNode> listOfPossibleParents )
		{
			List<SequenceOfMoves> somList = createSequenceOfMoves( newNode, listOfPossibleParents );

			checkListOfSequencesOfMoves( somList );

			boolean result = isLegal( somList );
			if( result )
			{
				result = isTotallyLegal( somList );
				if( result )
				{
					removeUnsuitableOldParents( somList );
					_lastMove = newNode;
				}
			}

			return( result );
		}

		protected void removeUnsuitableOldParents( List<SequenceOfMoves> somList )
		{
			if( somList != null )
			{
				List< ExperimentalMoveTreeNode > listOfElemsWithPossibleChildrenToRemove = new ArrayList<>();

				Iterator<SequenceOfMoves> it = somList.iterator();
				Map<ExperimentalMoveTreeNode, Boolean> tmpMap = new HashMap<>();
				while( it.hasNext() )
				{
					SequenceOfMoves item = it.next();
					if( !item.getIsLegal() )
					{
						List<ExperimentalMoveTreeNode> nodeList = item.getListOfNodes();

						Iterator<ExperimentalMoveTreeNode> it2 = nodeList.iterator();
						while( it2.hasNext() )
						{
							ExperimentalMoveTreeNode item2 = it2.next();

							if( tmpMap.get( item2 ) == null )
							{
								boolean isLegal = isLegal( somList, item2 );
								tmpMap.put(item2, isLegal);

								if( ! isLegal )
									listOfElemsWithPossibleChildrenToRemove.add( item2 );
//									item2.removeAllPossibleChildren();
							}
						}
					}
				}

				removePossibleChildren( listOfElemsWithPossibleChildrenToRemove, somList );
			}
		}

		protected void removePossibleChildren( List< ExperimentalMoveTreeNode > listOfElemsWithPossibleChildrenToRemove,
												List<SequenceOfMoves> somList )
		{
			for( ExperimentalMoveTreeNode node: listOfElemsWithPossibleChildrenToRemove )
			{
				for( SequenceOfMoves som: somList )
				{
					ExperimentalMoveTreeNode node2 = som.getNodeAt( node.getLevel() - 1 );

					if( node == node2 )
						node.removePossibleChild( som.getNodeAt( node.getLevel() ) );
				}
			}
		}

		protected boolean isTotallyLegal( List<SequenceOfMoves> somList )
		{
			boolean result = true;
			if( somList != null )
			{
				Iterator<SequenceOfMoves> it = somList.iterator();
				Map<ExperimentalMoveTreeNode, Boolean> tmpMap = new HashMap<>();
				while( result && it.hasNext() )
				{
					SequenceOfMoves item = it.next();
					if( !item.getIsLegal() )
					{
						List<ExperimentalMoveTreeNode> nodeList = item.getListOfNodes();

						Iterator<ExperimentalMoveTreeNode> it2 = nodeList.iterator();
						while( result && it2.hasNext() )
						{
							ExperimentalMoveTreeNode item2 = it2.next();

							if( tmpMap.get( item2 ) == null )
							{
								boolean isLegal = isLegal( somList, item2 );
								tmpMap.put(item2, isLegal);

								if( ! isLegal )
								{
									Iterator<ExperimentalMoveTreeNode> it3 = item2.getPossibleChildrenIterator();
									while( result && it3.hasNext() )
									{
										ExperimentalMoveTreeNode item3 = it3.next();
										result = !( (item3.getParent() == null ) &&
													( item3.getNumberOfPossibleParents() == 1 ) );
									}
								}
							}
						}
					}
				}
			}

			return( result );
		}

		protected boolean isLegal( List<SequenceOfMoves> somList, ExperimentalMoveTreeNode node )
		{
			boolean result = false;
			
			Iterator<SequenceOfMoves> it = somList.iterator();

			while( !result && it.hasNext() )
			{
				SequenceOfMoves item = it.next();
				result = item.getListOfNodes().get( node.getLevel() - 1 ) == node;
				if( result )
					result = item.getIsLegal();
			}

			return( result );
		}

		protected void checkListOfSequencesOfMoves( List<SequenceOfMoves> somList )
		{
			if( somList != null )
			{
				Iterator<SequenceOfMoves> it = somList.iterator();
				while( it.hasNext() )
				{
					SequenceOfMoves item = it.next();
					boolean isLegal = isLegal( item );
					item.setIsLegal(isLegal);
				}
			}
		}

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

		protected boolean isLegal( List<SequenceOfMoves> somList )
		{
			boolean result = false;
			if( ( somList != null ) && ( somList.size() > 0 ) )
			{
				Iterator<SequenceOfMoves> it = somList.iterator();
				while( !result && it.hasNext() )
					result = it.next().getIsLegal();
			}

			return( result );
		}

		protected List<SequenceOfMoves> createSequenceOfMoves( ExperimentalMoveTreeNode newNode,
																List<ExperimentalMoveTreeNode> listOfPossibleParents )
		{
			List<SequenceOfMoves> result = new ArrayList<>();

			SequenceOfMoves som = createSequenceOfMoves();
			som.addNode( newNode );

			if( newNode.getLevel() == 1 )
				result.add(som);
			else
			{
				Iterator< ExperimentalMoveTreeNode > it = listOfPossibleParents.iterator();
				while( it.hasNext() )
				{
					createSequenceOfMoves( it.next(), result, som );
				}
			}

			return( result );
		}

		protected void createSequenceOfMoves( ExperimentalMoveTreeNode node,
											List<SequenceOfMoves> result,
											SequenceOfMoves somModel )
		{
			if( node != null )
			{
				SequenceOfMoves som = createSequenceOfMoves( somModel );
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

		protected List<ExperimentalMoveTreeNode> findPossibleParents( ExperimentalMoveTreeNode newNode )
		{
			List<ExperimentalMoveTreeNode> result = new ArrayList<>();

			findPossibleParents_internal( newNode, result, _lastMove );//, null );

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

		protected void addPossibleParentsOfBranch( List<ExperimentalMoveTreeNode> result,
													ExperimentalMoveTreeNode existingNode,
//													ExperimentalMoveTreeNode previousExistingNode,
													int level )
		{
			if( existingNode.getLevel() == level )
			{
				if( !result.contains( existingNode ) )
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
//													previousExistingNode,
													level );
				}
			}
		}

		protected void findPossibleParents_internal( ExperimentalMoveTreeNode newNode,
													List<ExperimentalMoveTreeNode> result,
													ExperimentalMoveTreeNode existingNode )
//													ExperimentalMoveTreeNode previousExistingNode )
		{
			addPossibleParentsOfBranch( result, existingNode,
//										previousExistingNode,
										newNode.getLevel() - 1 );

//			if( result.size() < MAX_NUM_OF_OPTIONS )
			{
				if( existingNode.getParent() != null )
				{
					findPossibleParents_internal( newNode, result, existingNode.getParent() );//, existingNode );
				}
				else
				{
					Iterator<ExperimentalMoveTreeNode> it = existingNode.getPossibleParentsIterator();
					while( ( result.size() < 4 ) && it.hasNext() )
						findPossibleParents_internal( newNode, result, it.next() );//, existingNode );
				}
			}

//			while( result.size() > MAX_NUM_OF_OPTIONS )
//				result.remove( MAX_NUM_OF_OPTIONS );
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
