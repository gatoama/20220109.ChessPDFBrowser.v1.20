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
package com.frojasg1.chesspdfbrowser.analysis.engine;

import com.frojasg1.chesspdfbrowser.enginewrapper.EngineWrapperInstance;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.args.EnginePositionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class EngineWrapperInstanceWrapper {

	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected Integer _id;
	protected EngineWrapperInstance _engineWrapperInstance;

	protected int _numberOfSecondsToSpendInAnalysis = 10;
	protected int _numberOfVariantsToAnalyse = 1;

	protected void init( Integer id, EngineWrapperInstance engineWrapperInstance )
	{
		_id = id;
		_engineWrapperInstance = engineWrapperInstance;
	}

	public int getNumberOfSecondsToSpendInAnalysis() {
		return _numberOfSecondsToSpendInAnalysis;
	}

	public void setNumberOfMilliSecondsToSpendInAnalysis(int _numberOfSecondsToSpendInAnalysis) {
		this._numberOfSecondsToSpendInAnalysis = _numberOfSecondsToSpendInAnalysis;
	}

	public int getNumberOfVariantsToAnalyse() {
		return _numberOfVariantsToAnalyse;
	}

	public void setNumberOfVariantsToAnalyse(int _numberOfVariantsToAnalyse) {
		this._numberOfVariantsToAnalyse = _numberOfVariantsToAnalyse;
	}

	public Integer getId()
	{
		return( _id );
	}

	public EngineWrapperInstance getEngineInstance()
	{
		return( _engineWrapperInstance );
	}

	public abstract void initEngine( EngineInstanceConfiguration configuration,
		Consumer<FullEngineActionResult<EngineInstanceConfiguration, EngineActionResult>> callbackFun );

	public abstract void go(Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>> callbackFun);

	public abstract void go(ChessEngineGoAttributes goAttr,
					Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>> callbackFun);

	public abstract void stopThinking(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun);


	public abstract void setCurrentPosition( String fenString, Consumer<FullEngineActionResult<EnginePositionArgs, EngineActionResult>> callbackFun );

	public void applyEngineConfiguration( ChessEngineConfiguration configuration,
										Consumer<FullEngineActionResult<ChessEngineConfiguration, EngineActionResult>> callbackFun )
	{
		if( callbackFun == null )
			callbackFun = res -> processApplyConfigurationResult(res);

		_engineWrapperInstance.applyConfigurationBase(configuration, callbackFun );
	}

	protected void processApplyConfigurationResult( FullEngineActionResult<ChessEngineConfiguration, EngineActionResult> result )
	{
		if( ( result != null ) && result.getSuccessResult().isAsuccess() )
			updateEverything();
	}

	protected abstract void updateEverything();

	public void closeEngine()
	{
		if( _engineWrapperInstance != null )
		{
			_engineWrapperInstance.stopAndClear();

			_engineWrapperInstance = null;
			_id = null;
		}
	}
}
