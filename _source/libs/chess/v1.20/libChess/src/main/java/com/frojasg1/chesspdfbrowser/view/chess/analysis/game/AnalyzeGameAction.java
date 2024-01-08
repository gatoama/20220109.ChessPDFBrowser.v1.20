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
package com.frojasg1.chesspdfbrowser.view.chess.analysis.game;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.general.ClassFunctions;
import com.frojasg1.general.view.ViewComponent;
import java.awt.Frame;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AnalyzeGameAction implements Runnable
{
	protected ViewComponent _parentWindow;
	protected ChessGame _chessGame;
	protected Consumer<ChessGame> _callback;
	protected ApplicationInitContext _applicationContext;

	public void init( ViewComponent parentWindow, ApplicationInitContext applicationContext,
						ChessGame chessGame, Consumer<ChessGame> callback )
	{
		_parentWindow = parentWindow;
		_chessGame = chessGame;
		_callback = callback;
		_applicationContext = applicationContext;
	}

	@Override
	public void run()
	{
		AnalyzeGame_JDial dial = createDialog();
	}

	protected AnalyzeGame_JDial createDialog()
	{
		boolean modal = true;
		return( new AnalyzeGame_JDial( ClassFunctions.instance().cast( _parentWindow, Frame.class ),
										modal, _chessGame, _applicationContext,
										this::processDialog) );
	}

	protected void processDialog( InternationalizationInitializationEndCallback iiec )
	{
		AnalyzeGame_JDial dialog = (AnalyzeGame_JDial) iiec;

		dialog.setVisibleWithLock(true);

//		if( dialog.wasSuccessful() )
		if( !dialog.wasCancelled() && isSuitable( dialog.getResult() ) )
			_callback.accept( dialog.getResult() );
	}

	// analized game will be suitable if any anotation has been done
	protected boolean isSuitable( ChessGame commentedGame )
	{
		int numberOfPliesMainLine = MoveTreeNodeUtils.instance().getNumberOfPliesOfMainLine(commentedGame );
		return( ( commentedGame != null ) &&
				( commentedGame.getTotalNumberOfMoves() > numberOfPliesMainLine ) );
	}
}
