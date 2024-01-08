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

import com.frojasg1.chesspdfbrowser.analysis.engine.EngineWrapperInstanceWrapper;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.args.EnginePositionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.impl.FullEngineActionResultImpl;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class StartEngineAnalysis
{
//	protected AnalysisWindowViewControllerImpl _analysisController;
	protected BiConsumer<Integer, FullEngineActionResult<ChessEngineGoAttributes,ChessEngineResult>> _callback;
	protected Consumer<SuccessResult> _callbackForStartAndConfiguration;
/*
	public StartEngineAnalysis( AnalysisWindowViewControllerImpl analysisController )
	{
		_analysisController = analysisController;
	}
*/

	public StartEngineAnalysis( Consumer<SuccessResult> callbackForStartAndConfiguration,
		BiConsumer<Integer,
				FullEngineActionResult<ChessEngineGoAttributes,ChessEngineResult>> callback )
	{
		_callback = callback;
		_callbackForStartAndConfiguration = callbackForStartAndConfiguration;
	}

	public void initEngine( Integer id, EngineWrapperInstanceWrapper engineWrapper,
								EngineInstanceConfiguration conf,
								String fenString )
	{
		engineWrapper.initEngine( conf, res -> processInitEngineResult( res, id, engineWrapper, fenString ) );
	}

	protected void processInitEngineResult( FullEngineActionResult<EngineInstanceConfiguration, EngineActionResult> result,
											Integer id,
											EngineWrapperInstanceWrapper engineWrapperInstanceWrapper,
											String fenString )
	{
		if( ( result != null ) && result.getSuccessResult().isAsuccess() )
		{
			EngineInstanceConfiguration conf = result.getRequest().getArgs();
			ChessEngineConfiguration cec = conf.getChessEngineConfiguration();
			if( cec != null )
				engineWrapperInstanceWrapper.applyEngineConfiguration(cec,
							res -> processEngineConfigurationApplicationResult( conf, res, id,
																				engineWrapperInstanceWrapper,
																				fenString ) );
			else
			{
				processEngineConfigurationApplicationResult( conf, createEmptyApplyConfigurationResult(), id,
															engineWrapperInstanceWrapper,
															fenString );
			}
		}
		else
		{
			invokeCallbackForStartAndConfiguration( getSuccessResult( result ) );
		}
	}

	protected void invokeCallbackForStartAndConfiguration( SuccessResult successResult )
	{
		if( _callbackForStartAndConfiguration != null )
			_callbackForStartAndConfiguration.accept(successResult);
	}

	protected SuccessResult getSuccessResult( FullEngineActionResult fullResult )
	{
		return( fullResult == null ? null : fullResult.getSuccessResult() );
	}

	protected FullEngineActionResult<ChessEngineConfiguration, EngineActionResult> createEmptyApplyConfigurationResult()
	{
		boolean success = true;
		FullEngineActionResultImpl<ChessEngineConfiguration, EngineActionResult> result = 
			new FullEngineActionResultImpl<>(success);

		return( result );
	}

	protected void processEngineConfigurationApplicationResult( EngineInstanceConfiguration conf,
											FullEngineActionResult<ChessEngineConfiguration, EngineActionResult> result,
											Integer id,
											EngineWrapperInstanceWrapper engineWrapperInstanceWrapper,
											String fenString )
	{
		if( ( result != null ) && result.getSuccessResult().isAsuccess() )
		{
			setPosition( id, engineWrapperInstanceWrapper, conf, fenString );
		}
		else
		{
			invokeCallbackForStartAndConfiguration( getSuccessResult( result ) );
		}
	}

	protected void setPosition( Integer id,
									EngineWrapperInstanceWrapper engineWrapperInstanceWrapper,
									EngineInstanceConfiguration conf,
									String fenString )
	{
		engineWrapperInstanceWrapper.setCurrentPosition(fenString,
						res -> processSetInitialPositionResult( res, id, engineWrapperInstanceWrapper, conf ) );
	}

	protected void processSetInitialPositionResult( FullEngineActionResult<EnginePositionArgs, EngineActionResult> result,
													Integer id,
													EngineWrapperInstanceWrapper engineWrapperInstanceWrapper,
													EngineInstanceConfiguration conf )
	{
		if( ( result != null ) && result.getSuccessResult().isAsuccess() )
			setNumberOfPV( id, engineWrapperInstanceWrapper, conf );
		else
			invokeCallbackForStartAndConfiguration( getSuccessResult( result ) );
	}

	protected void setNumberOfPV( Integer id,
									EngineWrapperInstanceWrapper engineWrapperInstanceWrapper,
									EngineInstanceConfiguration conf )
	{
		ChessEngineConfiguration conf2 = createMPVconf( conf, engineWrapperInstanceWrapper.getNumberOfVariantsToAnalyse() );
		applyMPVEngineConfiguration(id, engineWrapperInstanceWrapper, conf2);
	}

	protected ChessEngineConfiguration createMPVconf( EngineInstanceConfiguration conf, int numberOfVariants )
	{
		ChessEngineConfiguration result = conf.getChessEngineConfiguration().createMultiPVConf( numberOfVariants );

		return( result );
	}

	protected void applyMPVEngineConfiguration(Integer id,
											EngineWrapperInstanceWrapper engineWrapperInstanceWrapper,
											ChessEngineConfiguration chessEngineConfiguration)
	{
		if( engineWrapperInstanceWrapper != null )
		{
			if( chessEngineConfiguration != null ) // if MultiPv configuration is not null (that is: multiPv is allowed), we apply it
			{
				engineWrapperInstanceWrapper.applyEngineConfiguration(chessEngineConfiguration,
							res -> processMPVEngineConfigurationApplicationResult( id, engineWrapperInstanceWrapper,
																				res ) );
			}
			else // if multiPv is not allowed, we start analysis directly
			{
				// here is the last operation after start, so we must always invoke callback
				invokeCallbackForStartAndConfiguration( SuccessResult.SUCCESS_NON_LAST_RESULT );

				launchGo( id, engineWrapperInstanceWrapper );
			}
		}
		else
			invokeCallbackForStartAndConfiguration( SuccessResult.ERROR_SUCCESS_RESULT );
	}

	protected void processMPVEngineConfigurationApplicationResult( Integer id,
											EngineWrapperInstanceWrapper engineWrapperInstanceWrapper,
											FullEngineActionResult<ChessEngineConfiguration, EngineActionResult> result
																)
	{
		// here is the last operation after start, so we must always invoke callback
		invokeCallbackForStartAndConfiguration( getSuccessResult( result ) );

		if( ( result != null ) && result.getSuccessResult().isAsuccess() )
			launchGo( id, engineWrapperInstanceWrapper );
	}

	protected void launchGo( Integer id, EngineWrapperInstanceWrapper engineWrapperInstanceWrapper )
	{
//		engineWrapperInstanceWrapper.go( res -> _analysisController.processGoResult( id, res ) );
		Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>> newCallback = null;
		if( _callback != null )
			newCallback = res -> _callback.accept( id, res );

		engineWrapperInstanceWrapper.go( newCallback );
	}
}
