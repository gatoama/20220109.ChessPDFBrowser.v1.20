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
package com.frojasg1.chesspdfbrowser.enginewrapper.uci.interf;

import com.frojasg1.chesspdfbrowser.enginewrapper.EngineWrapper;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.args.EngineCustomCommandArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.args.EnginePositionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.args.EngineButtonNameArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface UciChessEngineWrapper extends EngineWrapper
{
	public final int INIT = 0;
	public final int APPLY_CONFIGURATION = 1;
	public final int APPLY_BUTTON_OPTION_ITEM = 2;
	public final int UCI_NEW_GAME = 3;
	public final int START_POSITION = 4;
	public final int POSITION = 5;
	public final int GO = 6;
	public final int CUSTOM_COMMAND = 7;
	public final int STOP_THINKING = 8;


	public void init( EngineInstanceConfiguration configuration,
		Consumer<FullEngineActionResult<EngineInstanceConfiguration, EngineActionResult>> callbackFun );

	public void applyConfiguration( ChessEngineConfiguration configuration,
		Consumer<FullEngineActionResult<ChessEngineConfiguration, EngineActionResult>> callbackFun );
	public void applyButtonOptionItem( EngineButtonNameArgs buttonNameArgs,
		Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun );

	public void uciNewGame(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun);

	public void startPosition(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun);
	public void position( EnginePositionArgs fenString,
		Consumer<FullEngineActionResult<EnginePositionArgs, EngineActionResult>> callbackFun );

	public void go( ChessEngineGoAttributes goAttr,
		Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>> callBackFunction );

//	public void executeWhenIsReady( Runnable runnable );

	public void sendCustomCommand( EngineCustomCommandArgs commandArgs,
		Consumer<FullEngineActionResult<EngineCustomCommandArgs, EngineActionResult>> callbackFun );

	public void stopThinking(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun);
}
