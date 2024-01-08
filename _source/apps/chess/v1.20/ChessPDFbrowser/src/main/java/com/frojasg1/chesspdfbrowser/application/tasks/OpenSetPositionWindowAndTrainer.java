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
package com.frojasg1.chesspdfbrowser.application.tasks;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.chesspdfbrowser.engine.position.impl.ChessGamePositionImpl;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.result.RecognitionResult;
import com.frojasg1.chesspdfbrowser.recognizer.store.whole.ChessBoardRecognizerWhole;
import com.frojasg1.chesspdfbrowser.view.chess.initialposition.InitialPositionDialog;
import com.frojasg1.general.clipboard.SystemClipboard;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.view.ViewComponent;
import java.awt.Component;
import java.awt.Frame;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.JDialog;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class OpenSetPositionWindowAndTrainer implements InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "OpenSetPositionWindowAndTrainer.properties";

	protected static final String CONF_COULD_NOT_RECOGNIZE_BOARD_POSITION = "COULD_NOT_RECOGNIZE_BOARD_POSITION";
	protected static final String CONF_RUN_TRAINER = "RUN_TRAINER";
	protected static final String CONF_POSITION_DETECTED = "POSITION_DETECTED";
	protected static final String CONF_COULD_NOT_RECOGNIZE_BOARD_POSITION_DO_YOU_WANT_TO_TRAIN_THE_RECOGNIZER = "COULD_NOT_RECOGNIZE_BOARD_POSITION_DO_YOU_WANT_TO_TRAIN_THE_RECOGNIZER";
	protected static final String CONF_BOARD_POSITION_COPIED_TO_CLIPBOARD = "BOARD_POSITION_COPIED_TO_CLIPBOARD";
	protected static final String CONF_BOARD_POSITION_RECOGNIZED_DO_YOU_WANT_TO_OPEN_IN_A_CHESS_BOARD = "BOARD_POSITION_RECOGNIZED_DO_YOU_WANT_TO_OPEN_IN_A_CHESS_BOARD";
	protected static final String CONF_DO_YOU_WANT_TO_SET_THAT_POSITION_IN_CURRENT_CHESS_GAME = "DO_YOU_WANT_TO_SET_THAT_POSITION_IN_CURRENT_CHESS_GAME";


	protected InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																												ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );


	public void init()
	{
		registerInternationalizedStrings();		
	}

	protected String getMostCompleteFenString( ChessGamePositionBase positionDetected )
	{
		return( MoveTreeNodeUtils.instance().getMostCompleteFenString(positionDetected) );
	}

	public void newImagePositionDetected( ViewComponent parent, ApplicationConfiguration appliConf,
											RecognitionResult result, InputImage image,
											ChessBoardRecognizerWhole chessBoardRecognizer,
											ChessGameControllerInterface chessGameController,
											ActionOnFenDetection action )
	{
		ChessGamePositionBase positionDetected = result.getDetectedPosition();
		String fenString = getMostCompleteFenString( positionDetected );

		String message = null;
		if( ( fenString == null ) || ( positionDetected != null ) && !positionDetected.isComplete() )
		{
			if( ( ( positionDetected == null ) || ( positionDetected instanceof ChessGamePositionImpl ) ) &&
				( result.getGrid() != null ) )
			{
				message = getInternationalString(CONF_COULD_NOT_RECOGNIZE_BOARD_POSITION_DO_YOU_WANT_TO_TRAIN_THE_RECOGNIZER);
				String title = getInternationalString(CONF_RUN_TRAINER);
				int yesNoAnswer = HighLevelDialogs.instance().yesNoCancelDialog(parent,
															message, title, HighLevelDialogs.YES );

				if( yesNoAnswer == HighLevelDialogs.YES )
				{
					trainRecognizer( parent, appliConf,
									(pos, im) -> newImagePositionDetected( parent,
																appliConf, pos, im,
																chessBoardRecognizer,
																chessGameController,
																ActionOnFenDetection.ASK_TO_SET_INITIAL_POSITION_TO_CURRENT_GAME),
									positionDetected, image, chessBoardRecognizer );
				}
			}
			else
			{
				message = this.getInternationalString(CONF_COULD_NOT_RECOGNIZE_BOARD_POSITION);
				HighLevelDialogs.instance().informationMessageDialog(parent, message);
			}
		}
		else
		{
			SystemClipboard.instance().setClipboardContents(fenString );

			if( action == null )
			{
				
			}
			else if( action.equals( ActionOnFenDetection.ASK_TO_OPEN_EDIT_POSITION_WINDOW ) )
			{
				message = this.createCustomInternationalString(CONF_BOARD_POSITION_RECOGNIZED_DO_YOU_WANT_TO_OPEN_IN_A_CHESS_BOARD, fenString);

				String title = getInternationalString(CONF_POSITION_DETECTED);
				int yesNoAnswer = HighLevelDialogs.instance().yesNoCancelDialog(parent, message, title, HighLevelDialogs.YES );

				if( yesNoAnswer == HighLevelDialogs.YES )
				{
					openSetPositionWindow( parent, appliConf, positionDetected, image,
											(pos, im) -> newImagePositionDetected( parent,
																appliConf, pos, im,
																chessBoardRecognizer,
																chessGameController,
																ActionOnFenDetection.ASK_TO_SET_INITIAL_POSITION_TO_CURRENT_GAME) );
				}
			}
			else if( action.equals( ActionOnFenDetection.ASK_TO_SET_INITIAL_POSITION_TO_CURRENT_GAME ) )
			{
				ChessGame chessGame = chessGameController.getCurrentChessGame();

				if( canSetInitialPosition( chessGame ) )
				{
					message = this.createCustomInternationalString(CONF_DO_YOU_WANT_TO_SET_THAT_POSITION_IN_CURRENT_CHESS_GAME, fenString);

					String title = getInternationalString(CONF_POSITION_DETECTED);
					int yesNoAnswer = HighLevelDialogs.instance().yesNoCancelDialog(parent, message, title, HighLevelDialogs.YES );

					ChessGamePositionBase position = result.getDetectedPosition();
					if( ( yesNoAnswer == HighLevelDialogs.YES ) &&
						( position instanceof ChessGamePosition ) )
					{
						chessGame.setInitialPosition( (ChessGamePosition) position );
						boolean hasBeenModified = true;
						chessGameController.newChessGameChosen(chessGame, hasBeenModified);
					}
				}
				else
				{
					message = createCustomInternationalString(CONF_BOARD_POSITION_COPIED_TO_CLIPBOARD, fenString );
					HighLevelDialogs.instance().informationMessageDialog(parent, message);
				}
			}
		}
	}

	protected boolean canSetInitialPosition( ChessGame chessGame )
	{
		boolean result = false;

		if( chessGame != null )
		{
			try
			{
				result = ( chessGame.getInitialPosition() == null ) ||
						( !chessGame.getInitialPosition().getFenString().equals( ChessGamePosition.INITIAL_POSITION_FEN_STRING ) ) ||
						( chessGame.getMoveTreeGame().getNumberOfChildren() == 0 );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}

		return( result );
	}

	public void openSetPositionWindow( ViewComponent parent, ApplicationConfiguration appliConf,
									ChessGamePositionBase positionDetected, InputImage image,
									BiConsumer<RecognitionResult, InputImage> callbackFunction )
	{
		InitialPositionDialog dial = createInitialPositionDialog( parent, appliConf,
			(win) -> initialInitialPositionDialogJustCreatedToTrainRecognizer( win, callbackFunction,
																				positionDetected,
																				image ) );
	}

	protected void initialInitialPositionDialogJustCreatedToTrainRecognizer( InternationalizationInitializationEndCallback window,
																			BiConsumer<RecognitionResult, InputImage> callbackFunction,
																			ChessGamePositionBase positionDetected, InputImage image )
	{
		InitialPositionDialog dial = (InitialPositionDialog) window;

		dial.setInitialPosition(positionDetected);
		dial.setVisibleWithLock( true );

		if( dial.getPositionResult() != null )
		{
			callbackFunction.accept( createRecognitionResult( dial.getPositionResult() ), image);
		}
	}

	public void trainRecognizer( ViewComponent parent, ApplicationConfiguration appliConf,
									BiConsumer<RecognitionResult, InputImage> callbackFunction,
									ChessGamePositionBase positionDetected, InputImage image,
									ChessBoardRecognizerWhole chessBoardRecognizer )
	{
		InitialPositionDialog dial = createInitialPositionDialog( parent, appliConf,
					(win) -> initialInitialPositionDialogJustCreatedToTrainRecognizer( win,
										callbackFunction, positionDetected, image,
										chessBoardRecognizer ) );
	}

	protected void initialInitialPositionDialogJustCreatedToTrainRecognizer( InternationalizationInitializationEndCallback window,
																			BiConsumer<RecognitionResult, InputImage> callbackFunction,
																			ChessGamePositionBase positionDetected, InputImage image,
																			ChessBoardRecognizerWhole chessBoardRecognizer )
	{
		InitialPositionDialog dial = (InitialPositionDialog) window;

		dial.setInitialPosition(positionDetected);
		dial.setVisible( true );

		if( dial.getPositionResult() != null )
		{
			String fenBoardStringBase = dial.getPositionResult().getFenBoardStringBase();
			chessBoardRecognizer.addTrainigPair(fenBoardStringBase, image);

			RecognitionResult result = createRecognitionResult( dial.getPositionResult() );
			callbackFunction.accept( result, image );
		}
	}

	protected RecognitionResult createRecognitionResult( ChessGamePositionBase position )
	{
		return( new RecognitionResult( position, null ) );
	}

	protected InitialPositionDialog createInitialPositionDialog( ViewComponent parent,
										ApplicationConfiguration appliConf,
										Consumer<InternationalizationInitializationEndCallback> callbackFun )
	{
		InitialPositionDialog result = null;
		boolean modal = true;

		Component parentComp = ( parent != null ) ? ( ( DesktopViewComponent ) parent ).getComponent() : null;

		if( ( parentComp == null ) || ( parentComp instanceof Frame ) )
		{
			Frame frame = (Frame) parentComp;
			result = new InitialPositionDialog( frame, modal, appliConf, callbackFun );
		}
		else if( parentComp instanceof JDialog )
		{
			JDialog dial = (JDialog) parentComp;
			result = new InitialPositionDialog( dial, modal, appliConf, callbackFun );
		}

		return( result );
	}


	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
		registerInternationalString( CONF_COULD_NOT_RECOGNIZE_BOARD_POSITION, "Could not recognize board position" );
		registerInternationalString( CONF_RUN_TRAINER, "Run trainer ?" );
		registerInternationalString( CONF_POSITION_DETECTED, "Position detected" );
		registerInternationalString( CONF_BOARD_POSITION_COPIED_TO_CLIPBOARD, "Board position copied to clipboard \n[ $1 ]" );
		registerInternationalString( CONF_COULD_NOT_RECOGNIZE_BOARD_POSITION_DO_YOU_WANT_TO_TRAIN_THE_RECOGNIZER, "Could not recognize board position.\n Do you want to train the recognizer ?" );
		registerInternationalString( CONF_BOARD_POSITION_RECOGNIZED_DO_YOU_WANT_TO_OPEN_IN_A_CHESS_BOARD, "Board position recognized and copied to clipboard \n[ $1 ]\nDo you want to open it in a chess board?" );
		registerInternationalString( CONF_DO_YOU_WANT_TO_SET_THAT_POSITION_IN_CURRENT_CHESS_GAME, "Board position copied to clipboard \nDo you want to set that position as initial position of current game?" );
	}

	public static enum ActionOnFenDetection
	{
		ASK_TO_OPEN_EDIT_POSITION_WINDOW,
		ASK_TO_SET_INITIAL_POSITION_TO_CURRENT_GAME
	}

}
