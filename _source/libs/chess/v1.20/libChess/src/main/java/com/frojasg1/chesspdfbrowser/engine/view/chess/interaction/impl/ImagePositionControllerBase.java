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
package com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.impl;

import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ImagePositionController;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ImagePositionControllerBase implements ImagePositionController
{
	protected ApplicationInitContext _applicationContext = null;
	protected Map< InputImage, ChessGame > _pendingFenMap = null;
	protected Consumer<ChessGame> _updatedChessGameConsumer = null;

	public ImagePositionControllerBase( ApplicationInitContext applicationContext )
	{
		this( applicationContext, null );
	}

	public ImagePositionControllerBase( ApplicationInitContext applicationContext,
										Consumer<ChessGame> updatedChessGameConsumer )
	{
		_updatedChessGameConsumer = updatedChessGameConsumer;
		_applicationContext = applicationContext;
		_pendingFenMap = new ConcurrentHashMap<>();
	}

	protected ApplicationInitContext getApplicationContext()
	{
		return( _applicationContext );
	}

	protected void addNewPendingImagePositionTask( InputImage image )
	{
		getApplicationContext().getChessBoardRecognizerWhole().recognizePosition( image, (fen, im) -> newImagePositionDetected( fen, im ) );
//		.getRecognizerThreads().addPendingExecutor( createPendingImagePositionExecutor( image ) );
	}

	@Override
	public void putNewPendingImagePosition(InputImage image, ChessGame game)
	{
		_pendingFenMap.put( image, game );

		addNewPendingImagePositionTask( image );
	}

	@Override
	public void newImagePositionDetected(String baseBoardFen, InputImage image)
	{
		ChessGame cg = _pendingFenMap.remove( image );
		if( cg != null )
		{
			MoveTreeNodeUtils.instance().setInitialBaseBoardFen( baseBoardFen, cg );
			if( _updatedChessGameConsumer != null )
				_updatedChessGameConsumer.accept(cg);
		}
	}

	@Override
	public void newImagePositionForTraining(String baseBoardFen, InputImage image)
	{
//		getApplicationContext().getChessBoardRecognizerWhole().getOcrTrainingThread().addTrainigPair( new Pair( baseBoardFen, image ) );
		getApplicationContext().getChessBoardRecognizerWhole().addTrainigPair( baseBoardFen, image );
	}
}
