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
package com.frojasg1.chesspdfbrowser.enginewrapper.uci.action;

import com.frojasg1.chesspdfbrowser.enginewrapper.EngineWrapper;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.EngineWrapperActionBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.move.LongAlgebraicNotationMove;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionRequest;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import com.frojasg1.chesspdfbrowser.enginewrapper.utils.EngineWrapperUtils;
import com.frojasg1.chesspdfbrowser.enginewrapper.variant.EngineMoveVariant;
import com.frojasg1.general.dialogs.highlevel.DebugDialog;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.structures.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UciGoEngineWrapperAction extends EngineWrapperActionBase<ChessEngineGoAttributes, ChessEngineResult>
{
	// key: firstMove-string
	protected volatile Map< String, Map<String, String> > _infoMapOfMaps;

	protected List<EngineMoveVariant> _lastVariants;

	public UciGoEngineWrapperAction( EngineWrapperUtils utils )
	{
		super( utils );
	}

	@Override
	public void init( String name, EngineWrapper engineWrapper, EngineActionRequest<ChessEngineGoAttributes> request,
		Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>> callbackFunction )
	{
		throw( new RuntimeException( "cannot use: init( String name, EngineWrapper engineWrapper, EngineActionRequest<EngineActionArgs> request,\n" +
									"		Consumer<FullEngineActionResult> callbackFunction )" ) );
	}

	@Override
	public void init( EngineWrapper engineWrapper, EngineActionRequest<ChessEngineGoAttributes> request,
						Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>> callbackFunction,
						Class<ChessEngineGoAttributes> argsClass )
	{
		// TODO: translate
		super.init( "Uci go", engineWrapper, request, callbackFunction );

		_infoMapOfMaps = new HashMap<>();
	}

	@Override
	public void start()
	{
		super.start();

		safeMethodExecution( () -> send( getArgs().getCommandString() ) );
	}

	protected void partialCallbackInvocation( FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult> result )
	{
//		synchronized( getTemporalOutput() )
		{
			if( getCallbackFunction() != null )
				getCallbackFunction().accept( result );
		}
	}

	@Override
	public void accept_internal( String line )
	{
		List<String> temporalOutputListCopy = copyTemporalOutput();

		// when new set of MultiPV is received, we will send the new partial results to callback function.
		if( isLastMultiPV( line ) )
		{
			partialCallbackInvocation( createFullNonLastResult( createChessEngineResult( temporalOutputListCopy ) ) );

			clearTemporalOutput();
		}
		else if( line.startsWith( "bestmove " ) )
		{
			ChessEngineResult cer = createChessEngineResult( temporalOutputListCopy );
			FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult> fullResult = createFullResult( cer );
			callbackInvocation( fullResult );
			clearTemporalOutput();
		}
	}

	protected int getMultiPvNumber()
	{
		Integer result = null;
		
		ConfigurationItem ci = this.getParent().getConfiguration().getMap().get( "MultiPV" );
		if( ci != null )
			result = (Integer) ci.getValue();

		if( result == null )
			result = 1;

		return( result );
	}

	protected boolean isLastMultiPV( String line )
	{
		boolean result = false;

		int multiPvNumber = getMultiPvNumber();
		Map<String, String> infoMap = getInfoVariantMap( line );
		String pv = infoMap.get( "pv" );
		if( pv != null )
		{
			Integer multiPvIndex = IntegerFunctions.parseInt( infoMap.get( "multipv" ) );

			if( (multiPvIndex == null) || multiPvIndex.equals( multiPvNumber ) )
				result = true;
		}

		return( result );
	}

	@Override
	protected void callbackInvocation(SuccessResult result)
	{
		callbackInvocation( createFullResult( result ) );
	}

	protected ChessEngineResult createChessEngineResult( List<String> lines )
	{
		ChessEngineResult result = new ChessEngineResult();
		result.init();

		List<EngineMoveVariant> variants = getVariants( lines );

		Pair<LongAlgebraicNotationMove, LongAlgebraicNotationMove> pairOfMoves = getBestAndPonderMoves( lines );
		if( pairOfMoves != null )
		{
			LongAlgebraicNotationMove bestMove = pairOfMoves.getKey();
			LongAlgebraicNotationMove ponderMove = pairOfMoves.getValue();

			double score = getBestMoveScore( variants, bestMove );

			result.setBestMove(bestMove);
			result.setPonderMove(ponderMove);
			result.setScore(score);

			variants = _lastVariants;
		}
		else
			_lastVariants = variants;

		if( variants != null )
		{
			for( EngineMoveVariant variant: variants )
				result.addVariant( variant );
		}

		return( result );
	}

	protected double getBestMoveScore( List<EngineMoveVariant> variants,
										LongAlgebraicNotationMove bestMove )
	{
		double result = 0d;

		EngineMoveVariant variant = getVariangOfFirstMove( variants, bestMove );
		if( variant != null )
			result = variant.getScore();

		return( result );
	}

	protected EngineMoveVariant getVariangOfFirstMove( List<EngineMoveVariant> variants,
										LongAlgebraicNotationMove bestMove )
	{
		EngineMoveVariant result = null;
		for( EngineMoveVariant variant: variants )
			if( startsWith( variant, bestMove ) )
			{
				result = variant;
				break;
			}

		return( result );
	}

	protected boolean startsWith( EngineMoveVariant variant, LongAlgebraicNotationMove bestMove )
	{
		boolean result = false;
		if( variant != null )
			result = Objects.equals( getFirst( variant.getListOfMoves() ), bestMove );

		return( result );
	}

	protected <CC> CC getLast( List<CC> list )
	{
		CC result = null;
		if( ( list != null ) && !list.isEmpty() )
			result = list.get( list.size() - 1 );

		return( result );
	}

	protected <CC> CC getFirst( List<CC> list )
	{
		CC result = null;
		if( ( list != null ) && !list.isEmpty() )
			result = list.get( 0 );

		return( result );
	}

	protected Map<String, String> getMapOfPairs( String line )
	{
		Map<String, String> result = new HashMap<>();
		if( line != null )
		{
			String[] array = line.split( "\\s" );

			// TODO: translate
//			if( array.length % 2 != 0 )
//				throw( new RuntimeException( "Line with a number of words not even" ) );

			if( array.length % 2 != 0 )
				result = null;
			else
			{
				for( int ii=0; ii<array.length; ii+=2 )
				{
					String key = array[ii];
					String value = array[ii+1];

					result.put( key, value );
				}
			}
		}

		return( result );
	}

	protected Pair<LongAlgebraicNotationMove, LongAlgebraicNotationMove> getBestAndPonderMoves( List<String> lines )
	{
		Pair<LongAlgebraicNotationMove, LongAlgebraicNotationMove> result = null;

		String lastLine = getLast( lines );

		Map<String, String> map = getMapOfPairs( lastLine );

		if( map != null )
		{
			String bestMoveStr = map.get( "bestmove" );

			if( bestMoveStr != null )
			{
				LongAlgebraicNotationMove bestMove = createMove( bestMoveStr );
				LongAlgebraicNotationMove ponderMove = createMove( map.get( "ponder" ) );

				result = new Pair<>(bestMove, ponderMove);
			}
		}

		return( result );
	}

	protected LongAlgebraicNotationMove createMove( String moveStr )
	{
		LongAlgebraicNotationMove result = null;

		if( moveStr != null )
		{
			result = new LongAlgebraicNotationMove();

			result.init(moveStr);

			if( result.isEmpty() )
				result = null;
		}
		return( result );
	}

	protected List<EngineMoveVariant> getVariants( List<String> lines )
	{
		List<EngineMoveVariant> result = new ArrayList<>();

		Map< Integer, Map<String, String> > map = createInfoMapOfMaps( lines );

		for( Map.Entry<Integer, Map<String, String>> entry: map.entrySet() )
		{
			result.add( createEngineMoveVariant( entry.getValue() ) );
		}

		return( result );
	}

	protected EngineMoveVariant createEngineMoveVariant( Map<String, String> map )
	{
		EngineMoveVariant result = new EngineMoveVariant();
		result.init();

		result.setScore( getScore( map.get( "cp" ) ) );

		String moves = map.get( "pv" );
		if( moves != null )
		{
			for( String moveStr: moves.split( "\\s" ) )
				result.add( createMove( moveStr ) );
		}

		return( result );
	}

	protected double getScore( String scoreStr )
	{
		double result = 0d;

		Integer scoreInt = parseInt( scoreStr );
		if( scoreInt != null )
			result = scoreInt / 100d;

		return( result );
	}

	protected Map< Integer, Map<String, String> > createInfoMapOfMaps( List<String> lines )
	{
		Map< Integer, Map<String, String> > result = new HashMap<>();

		for( String line: lines )
		{
			Map<String, String> infoMap = getInfoVariantMap( line );

			if( infoMap != null )
			{
				String firstMoveString = getFirstMoveString( infoMap.get( "pv" ) );

				if( firstMoveString != null )
				{
					Integer index = parseInt( infoMap.get( "multipv" ) );
					if( index == null )
						index = 1;

					Map<String, String> storedMap = _infoMapOfMaps.get( firstMoveString );

					Map<String, String> best = chooseBest( storedMap, infoMap );

					result.put( index, best );
					if( firstMoveString != null )
						_infoMapOfMaps.put( firstMoveString, best );
				}
			}
		}

		return( result );
	}

	protected String getFirstMoveString( String pvMovesStr )
	{
		String result = null;
		if( pvMovesStr != null )
			result = pvMovesStr.split( "\\s" )[0];

		return( result );
	}

	protected Map<String, String> chooseBest( Map<String, String> one, Map<String, String> another )
	{
		Map<String, String> result = null;

		if( one == null )
			result = another;
		else if( another == null )
			result = one;
		else
		{
			if( compareVariants( one, another ) < 0 )
				result = one;
			else
				result = another;
		}

		return( result );
	}

	protected int compareVariants( Map<String, String> one, Map<String, String> another )
	{
		return( numPvItems( another ) - numPvItems( one ) );
	}

	protected int numPvItems( Map<String, String> map )
	{
		int result = -1;

		String moves = map.get( "pv" );
		if( moves != null )
			result = moves.split( "\\s" ).length;

		return( result );
	}

	protected Map<String, String> getInfoVariantMap( String line )
	{
		Map<String, String> result = new HashMap<>();

		String[] array = line.split( "\\s" );

		if( array.length > 1 )
		{
			String commandName = array[0];
			if( "info".equals( commandName ) && "depth".equals( array[1] ) )
			{
				for( int ii=1; ii<array.length; ii+=2 )
				{
					String key = array[ii];
					String value = null;

					if( key.equals( "pv" ) )
					{
						StringBuilder sb = new StringBuilder();
						String separator = "";
						for( int jj=ii+1; jj<array.length; jj++ )
						{
							sb.append( separator ).append( array[jj] );
							separator = " ";
						}
						value = sb.toString();
					}
					else
					{
						if( ii >= ( array.length - 1 ) )
							value = "0";
						else
						{
							value = array[ii+1];
							Integer intValue = IntegerFunctions.parseInt( value );
							if( intValue == null )
							{
								ii--;
								value = "0";
							}
						}
					}

					result.put(key, value);

					if( key.equals( "pv" ) )
						break;
				}
			}
		}

		return( result );
	}

	protected Integer parseInt( String str )
	{
		return( IntegerFunctions.parseInt( str ) );
	}
}
