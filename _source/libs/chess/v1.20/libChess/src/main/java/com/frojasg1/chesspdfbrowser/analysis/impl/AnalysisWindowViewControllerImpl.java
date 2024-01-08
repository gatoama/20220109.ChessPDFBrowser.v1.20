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
package com.frojasg1.chesspdfbrowser.analysis.impl;

import com.frojasg1.chesspdfbrowser.analysis.AnalysisControllerBase;
import com.frojasg1.chesspdfbrowser.analysis.AnalysisWindowView;
import com.frojasg1.chesspdfbrowser.analysis.AnalysisWindowViewController;
import com.frojasg1.chesspdfbrowser.analysis.EngineAnalysisProcessData;
import com.frojasg1.chesspdfbrowser.analysis.engine.EngineWrapperInstanceWrapper;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import com.frojasg1.chesspdfbrowser.enginewrapper.variant.EngineMoveVariant;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.xml.transform.TransformerException;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AnalysisWindowViewControllerImpl extends AnalysisControllerBase
												implements AnalysisWindowViewController
{
	protected Map<Integer, EngineWrapperInstanceContext> _map;

	protected AnalysisWindowView _view;

	protected ChessGameControllerInterface _chessGameController = null;

	public AnalysisWindowViewControllerImpl( ChessEngineConfigurationPersistency chessEngineConfPersistency,
												ChessViewConfiguration chessViewConfiguration )
	{
		super( chessEngineConfPersistency, chessViewConfiguration );
	}

	public void init( ChessGameControllerInterface chessGameController )
	{
		_map = createMap();

		_chessGameController = chessGameController;
	}

	protected <KK, VV> Map<KK, VV> createMap()
	{
		return( new ConcurrentHashMap<>() );
	}

	@Override
	protected Consumer<SuccessResult> getProcessAfterInitResultFunction()
	{
		return( null );
	}

	@Override
	protected BiConsumer<Integer,
				FullEngineActionResult<ChessEngineGoAttributes,ChessEngineResult>> getProcessGoResultFunction()
	{
		return( this::processGoResult );
	}

	@Override
	public void setView( AnalysisWindowView view )
	{
		_view = view;
		_map.clear();
	}

	protected boolean hasChanged(Integer id, EngineAnalysisProcessData engineData)
	{
		return( hasChanged( getEngineWrapperInstanceWrapper(id), engineData ) );
	}

	@Override
	public void setAnalysisProcess(Integer id, EngineAnalysisProcessData engineData)
	{
		if( hasChanged( id, engineData ) )
		{
			_view.updateEngineConfiguration( id, getEngineConfiguration( engineData.getEngineName() ) );

			closeAnalysisProcess(id);

			EngineWrapperInstanceContext engineContext = createEngineContext( id, engineData );
			_map.put( id, engineContext );

			startEngine( id, engineContext.getEngineWrapperInstanceWrapper(), engineData );
		}
	}

	protected EngineWrapperInstanceWrapper getEngineWrapperInstanceWrapper( Integer id )
	{
		return( getEngineWrapperInstanceWrapper( _map.get(id) ) );
	}

	@Override
	public void closeAnalysisProcess(Integer id)
	{
		EngineWrapperInstanceContext engineContext = _map.remove(id);
		closeAnalysisProcess( getEngineWrapperInstanceWrapper(engineContext) );
	}

	@Override
	public void addSubvariantAnalysisToGame(Integer id, double score, MoveTreeNode mtn)
	{
		if( addSubvariantAnalysisToGame_internal( getEngineName( id ),
												score, mtn) )
		{
			_chessGameController.newChessGameChosen( _currentMoveTreeNode.getChessGame(), true );
		}
	}

	protected String getEngineName( Integer id )
	{
		return( getEngineName( getEngineWrapperInstanceWrapper(id) ) );
	}

	@Override
	public void applyEngineConfiguration(Integer id, ChessEngineConfiguration chessEngineConfiguration)
		throws IOException, TransformerException
	{
		EngineWrapperInstanceWrapper ewiw = getEngineWrapperInstanceWrapper(id);

		applyEngineConfiguration( ewiw, chessEngineConfiguration );

		saveConfiguration( ewiw.getEngineInstance().getUciInstanceConfiguration() );
	}

	@Override
	protected synchronized void updatePositionEverywhere( String fenString )
	{
		for( Map.Entry<Integer, EngineWrapperInstanceContext> entry: _map.entrySet())
			if( !entry.getValue().isStopped() )
				updatePosition( getEngineWrapperInstanceWrapper(entry.getValue()), fenString );
			else
				_view.resetSubvariants( entry.getKey() );				
	}

	public void processGoResult( Integer id, FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult> result )
	{
		EngineWrapperInstanceWrapper engineWrapper = getEngineWrapperInstanceWrapper(id);

		if( ( engineWrapper != null ) && ( result != null ) && result.getSuccessResult().isAsuccess() )
		{
			ChessEngineResult cer = result.getActionResult();
			if( cer != null )
			{
				int index = 0;

				for( EngineMoveVariant emv: cer.getBestVariants() )
					_view.updateSubvariant(id, index++, createSubvariantAnalysisResult( emv ) );
			}
		}
	}

	@Override
	public void startThinking( Integer id )
	{
		EngineWrapperInstanceContext ctx = _map.get(id);
		if( ctx != null )
		{
			ctx.setIsStopped(false);
			startThinking( getEngineWrapperInstanceWrapper(ctx) );
		}
	}

	@Override
	public void stop(Integer id) {
		EngineWrapperInstanceContext ctx = _map.get(id);
		if( ctx != null )
		{
			ctx.setIsStopped(true);
			stop( getEngineWrapperInstanceWrapper(ctx) );
		}
	}

	@Override
	public void setNewPosition( MoveTreeNode mtn )
	{
		super.setNewPosition( mtn );
	}
}
