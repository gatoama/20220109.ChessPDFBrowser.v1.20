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
package com.frojasg1.chesspdfbrowser.engine.view.chess.interaction;

import com.frojasg1.chesspdfbrowser.analysis.AnalysisWindowViewController;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.game.ChessGamePlayContext;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.general.view.ViewComponent;

/**
 *
 * @author Usuario
 */
public interface ChessGameControllerInterface
{
	/**
	 * Interface used by view to communicate to Controller if the user has
	 * changed to another move in the moves tree.
	 * 
	 * @param lcgm 
	 */
//	public void newPositionInTheMovesTree( List<ChessGameMove> lcgm );
	public void newPositionInTheMovesTree( MoveTreeNode currentMtn, ChessGameMove cgm,
											ChessMoveGenerator source );

	public ChessGame getCurrentChessGame();

	public void newChessGameChosen( ChessGame chessGame, boolean hasBeenModified );

	public void editInitialPosition( ChessGame chessGame );
	
	public void editComment( ChessGame cg, MoveTreeNode mtn, Boolean isTypeOfCommentOfMove );

	public void setHasBeenModified( boolean value );

	public boolean checkToSaveCurrentPGN();

	public void openConfiguration( boolean openTagRegexConfiguration, ProfileModel profileModel,
									ViewComponent parentWindow );

	public AnalysisWindowViewController getAnalysisController();

	public void gameIsOver();
	public void clearGame();

	public void setRemainingTime( Integer msLeftWhite, Integer msLeftBlack );

	public void setNewPlayedGame( ChessGamePlayContext gamePlayContext );

	public void updateMoveNavigator();

	public ApplicationConfiguration getAppliConf();

	public void analyzeGame( ChessGame cg);
}
