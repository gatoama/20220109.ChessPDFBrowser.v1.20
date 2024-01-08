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

import com.frojasg1.general.dialogs.highlevel.DebugDialog;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.analysis.AnalysisWindowView;
import com.frojasg1.chesspdfbrowser.analysis.AnalysisWindowViewController;
import com.frojasg1.chesspdfbrowser.analysis.EngineAnalysisProcessData;
import com.frojasg1.chesspdfbrowser.analysis.SubvariantAnalysisResult;
import com.frojasg1.chesspdfbrowser.analysis.engine.EngineWrapperInstanceWrapper;
import com.frojasg1.chesspdfbrowser.analysis.engine.UciInstanceWrapper;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.UciInstance;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import com.frojasg1.chesspdfbrowser.enginewrapper.variant.EngineMoveVariant;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.transform.TransformerException;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AnalysisWindowViewControllerImpl_old implements AnalysisWindowViewController,
														InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "AnalysisWindowViewControllerImpl.properties";

	public static final String CONF_ANALYZED_SUBVARIANT = "ANALYZED_SUBVARIANT";
	public static final String CONF_UNKNOWN = "UNKNOWN";


	protected Map<Integer, EngineWrapperInstanceWrapper> _map;

	protected InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																												ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );
	protected ChessEngineConfigurationPersistency _chessEngineConfPersistency;
	protected AnalysisWindowView _view;

	protected MoveTreeNode _currentMoveTreeNode;
	protected String _currentFenString;

	protected ChessGameControllerInterface _chessGameController = null;

	protected StartEngineAnalysis _startEngineAnalysis;

	protected ChessViewConfiguration _chessViewConfiguration;

	public void init( ChessEngineConfigurationPersistency chessEngineConfPersistency,
						ChessGameControllerInterface chessGameController,
						ChessViewConfiguration chessViewConfiguration )
	{
		registerInternationalizedStrings();

		_map = createMap();
		_chessEngineConfPersistency = chessEngineConfPersistency;
		_chessGameController = chessGameController;
		_startEngineAnalysis = createStartEngineAnalysis();
		_chessViewConfiguration = chessViewConfiguration;
	}

	protected <KK, VV> Map<KK, VV> createMap()
	{
		return( new ConcurrentHashMap<>() );
	}

	protected StartEngineAnalysis createStartEngineAnalysis()
	{
		return( new StartEngineAnalysis(null, (id, res) -> processGoResult( id, res )) );
	}

	@Override
	public void setView( AnalysisWindowView view )
	{
		_view = view;
		_map.clear();
	}

	protected EngineInstanceConfiguration getEngineInstanceConfiguration( String engineName )
	{
		return( _chessEngineConfPersistency.getModelContainer().get(engineName) );
	}

	protected EngineWrapperInstanceWrapper createEngineInstance( Integer id, EngineAnalysisProcessData engineData )
	{
		UciInstanceWrapper result = new UciInstanceWrapper( );

		UciInstance ui = new UciInstance();
		ui.init();

		result.setNumberOfMilliSecondsToSpendInAnalysis( engineData.getNumberOfMilliSecondsToSpendInAnalysis() );
		result.setNumberOfVariantsToAnalyse( engineData.getNumberOfVariants() );

		result.init( id, ui );

		return( result );
	}

	protected void setNumberOfPV( Integer id,
									EngineWrapperInstanceWrapper engineWrapperInstanceWrapper,
									EngineInstanceConfiguration conf )
	{
		ChessEngineConfiguration conf2 = createMPVconf( conf, engineWrapperInstanceWrapper.getNumberOfVariantsToAnalyse() );
		applyEngineConfiguration(engineWrapperInstanceWrapper, conf2);
	}

	protected ChessEngineConfiguration createMPVconf( EngineInstanceConfiguration conf, int numberOfVariants )
	{
		ChessEngineConfiguration result = conf.getChessEngineConfiguration().createMultiPVConf( numberOfVariants );

		return( result );
	}

	protected boolean hasChanged(Integer id, EngineAnalysisProcessData engineData)
	{
		boolean result = ( engineData != null );
		EngineWrapperInstanceWrapper existing = _map.get(id);

		if( result && ( existing != null ) )
		{
			EngineInstanceConfiguration engineConf = ExecutionFunctions.instance().safeFunctionExecution( () -> existing.getEngineInstance().getUciInstanceConfiguration() );

			if( engineConf != null )
			{
				result = ! Objects.equals( engineData.getEngineName(), engineConf.getName() ) ||
						! Objects.equals( engineData.getNumberOfMilliSecondsToSpendInAnalysis(), existing.getNumberOfSecondsToSpendInAnalysis() ) ||
						! Objects.equals( engineData.getNumberOfVariants(), existing.getNumberOfVariantsToAnalyse() );
			}
		}

		return( result );
	}

	protected ChessEngineConfiguration getEngineConfiguration( String engineName )
	{
		return( ExecutionFunctions.instance().safeSilentFunctionExecution( () -> _chessEngineConfPersistency.getModelContainer().get( engineName ).getChessEngineConfiguration() ) );
	}

	@Override
	public void setAnalysisProcess(Integer id, EngineAnalysisProcessData engineData)
	{
		if( hasChanged( id, engineData ) )
		{
			_view.updateEngineConfiguration( id, getEngineConfiguration( engineData.getEngineName() ) );

			closeAnalysisProcess(id);

			EngineWrapperInstanceWrapper engineWrapper = createEngineInstance( id, engineData );
			_map.put( id, engineWrapper );

			startEngine( id, engineWrapper, engineData );
		}
	}

	protected void startEngine( Integer id, EngineWrapperInstanceWrapper engineWrapper,
								EngineAnalysisProcessData engineData )
	{
		EngineInstanceConfiguration conf = getEngineInstanceConfiguration( engineData.getEngineName() );

		if( conf != null )
			_startEngineAnalysis.initEngine(id, engineWrapper, conf, _currentFenString);
	}

	@Override
	public void closeAnalysisProcess(Integer id)
	{
		EngineWrapperInstanceWrapper ui = _map.remove(id);

		if( ui != null )
			ui.closeEngine();
	}

	@Override
	public void addSubvariantAnalysisToGame(Integer id, double score, MoveTreeNode mtn)
	{
		if( _currentMoveTreeNode != null )
		{
			MoveTreeNode copiedMtn = pasteSubvariant_internal( _currentMoveTreeNode, mtn );
			copiedMtn.setComment( getCommentForAnalysisSubvariant( id, score ) );

			_chessGameController.newChessGameChosen( _currentMoveTreeNode.getChessGame(), true );
		}
	}

	protected String getCommentForAnalysisSubvariant( Integer id, double score )
	{
		String engineName = getEngineName( id );
		String scoreStr = String.format( "%.2f", score );

		return( this.createCustomInternationalString(CONF_ANALYZED_SUBVARIANT, engineName, scoreStr ) );
	}

	protected String getEngineName( Integer id )
	{
		EngineWrapperInstanceWrapper engineWrapper = _map.get(id);

		String result = null;
		if( engineWrapper != null )
			result = engineWrapper.getEngineInstance().getUciInstanceConfiguration().getName();
		else
			result = this.getInternationalString( CONF_UNKNOWN );

		return( result );
	}

	protected MoveTreeNode pasteSubvariant_internal( MoveTreeNode parent, MoveTreeNode variantToCopy )
	{
		MoveTreeNode mtn = parent.simpleInsert( variantToCopy );

		for( int ii=0; ii<variantToCopy.getNumberOfChildren(); ii++ )
		{
			pasteSubvariant_internal( mtn, variantToCopy.getChild( ii ) );
		}

		return( mtn );
	}

	protected void applyEngineConfiguration(EngineWrapperInstanceWrapper engineWrapperInstanceWrapper,
											ChessEngineConfiguration chessEngineConfiguration)
	{
		if( ( engineWrapperInstanceWrapper != null ) && ( chessEngineConfiguration != null ) )
			engineWrapperInstanceWrapper.applyEngineConfiguration(chessEngineConfiguration, null);
	}

	@Override
	public void applyEngineConfiguration(Integer id, ChessEngineConfiguration chessEngineConfiguration)
		throws IOException, TransformerException
	{
		EngineWrapperInstanceWrapper ewiw = _map.get(id);

		applyEngineConfiguration( ewiw, chessEngineConfiguration );

		saveConfiguration( ewiw.getEngineInstance().getUciInstanceConfiguration() );
	}

	protected void saveConfiguration( EngineInstanceConfiguration engineIConf ) throws IOException, TransformerException
	{
		_chessEngineConfPersistency.saveXmlConfigurationFile(engineIConf);
	}

	@Override
	public void setNewPosition(MoveTreeNode mtn)
	{
		_currentMoveTreeNode = mtn;

		String fenString = calculateFenString( _currentMoveTreeNode );

		if( ! Objects.equals( fenString, _currentFenString ) )
		{
			_currentFenString = fenString;
			updatePositionEverywhere( _currentFenString );
		}
	}

	protected String calculateFenString( MoveTreeNode mtn )
	{
		return( MoveTreeNodeUtils.instance().getFen( mtn ) );
	}

	protected synchronized void updatePositionEverywhere( String fenString )
	{
		for( EngineWrapperInstanceWrapper ew: _map.values() )
			ew.setCurrentPosition( fenString, null );
	}

	protected boolean hasToDebug( FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult> fear )
	{
		boolean result = false;
		if( fear != null )
		{
			ChessEngineResult cer = fear.getActionResult();
			if( cer != null )
				result = ( cer.getBestMove() != null );
		}

		return( result );
	}

	public void processGoResult( Integer id, FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult> result )
	{
		EngineWrapperInstanceWrapper engineWrapper = _map.get(id);

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

	protected SubvariantAnalysisResult createSubvariantAnalysisResult( EngineMoveVariant emv )
	{
		return( MoveTreeNodeUtils.instance().createSubvariantAnalysisResult( _currentFenString,
																				_chessViewConfiguration,
																				emv ) );
	}

	@Override
	public void startThinking( Integer id )
	{
		EngineWrapperInstanceWrapper engineInstance = _map.get(id);
		if( engineInstance != null )
			engineInstance.setCurrentPosition( _currentFenString, null );
	}

	@Override
	public void stop(Integer id) {
		EngineWrapperInstanceWrapper engineInstance = _map.get(id);
		if( engineInstance != null )
			engineInstance.stopThinking( null );
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}

	protected void registerInternationalizedStrings()
	{
		// TODO. translate
		registerInternationalString( CONF_ANALYZED_SUBVARIANT, "Analyzed subvariant score: $2 (by $1)" );
		registerInternationalString( CONF_UNKNOWN, "Unknown" );
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
