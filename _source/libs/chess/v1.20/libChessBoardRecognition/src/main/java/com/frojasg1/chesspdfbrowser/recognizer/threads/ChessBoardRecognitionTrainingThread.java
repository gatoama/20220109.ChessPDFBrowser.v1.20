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
package com.frojasg1.chesspdfbrowser.recognizer.threads;

import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.recognizer.configuration.ChessRecognizerApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.result.RecognitionResult;
import com.frojasg1.chesspdfbrowser.recognizer.store.ChessBoardRecognitionStore;
import com.frojasg1.chesspdfbrowser.recognizer.store.whole.ChessBoardRecognizerWhole;
import com.frojasg1.chesspdfbrowser.recognizer.trainer.impl.ChessBoardPositionTrainerImpl;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.listeners.GenericListOfListeners;
import com.frojasg1.general.listeners.GenericListOfListenersImp;
import com.frojasg1.general.listeners.GenericNotifier;
import com.frojasg1.general.listeners.GenericObserved;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import com.frojasg1.chesspdfbrowser.recognizer.threads.notifier.PendingItemsListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardRecognitionTrainingThread extends Thread implements GenericObserved<PendingItemsListener>
{
	protected GenericNotifier< PendingItemsListener > _genericNotifier = null;
	protected GenericListOfListeners<PendingItemsListener> _listOfListeners = null;

	protected ChessBoardRecognitionStore _store = null;

	protected LinkedList<ChessBoardRecognitionTrainingTaskData> _trainingItems = null;

	protected ReentrantLock _lock = new ReentrantLock(true);
	protected Condition _moreItemsAvailable = _lock.newCondition();

	protected volatile boolean _hasToStop = false;

	protected ChessBoardPositionTrainerImpl _trainer = null;

	protected volatile boolean _isActivated = false;

	protected ChessBoardRecognizerWhole _parent = null;

	public ChessBoardRecognitionTrainingThread()
	{
	}

	public void init( ChessBoardRecognizerWhole parent )
	{
		_listOfListeners = createListOfListeners();
		_genericNotifier = createGenericNotifier();

		_parent = parent;
		_store = parent.getStore();

		_trainingItems = new LinkedList<>();
		_trainer = createTrainer();
	}

	protected GenericListOfListeners<PendingItemsListener> createListOfListeners()
	{
		return( new GenericListOfListenersImp<>() );
	}

	protected GenericNotifier< PendingItemsListener > createGenericNotifier()
	{
		return( (lis) -> notify( lis ) );
	}

	public int getNumPendingItems()
	{
		return( _trainingItems.size() );
	}

	protected void notify( PendingItemsListener listener )
	{
		listener.pendingItemsChanged( this, getNumPendingItems() );
	}

	protected void notifyListeners()
	{
		_listOfListeners.notifyListeners( _genericNotifier );
	}

	@Override
	public void addListenerGen(PendingItemsListener listener)
	{
		_listOfListeners.add(listener);
	}

	@Override
	public void removeListenerGen(PendingItemsListener listener)
	{
		_listOfListeners.remove(listener);
	}

	public boolean isActivated()
	{
		return( _isActivated );
	}

	public void setIsActivated( boolean value )
	{
		_isActivated = value;

		if( !_isActivated )
			clearListOfPendingTasks();

		activateDetectionIfNecessary();
	}

	protected ChessBoardRecognizerWhole getParent()
	{
		return( _parent );
	}

	protected ChessRecognizerApplicationConfiguration getAppliConf()
	{
		return( getParent().getAppliConf() );
	}

	protected ChessBoardPositionTrainerImpl createTrainer()
	{
		return( new ChessBoardPositionTrainerImpl(getAppliConf()) );
	}

	public void clearListOfPendingTasks()
	{
		try
		{
			_lock.lock();
			_trainingItems.clear();
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void addTrainigTaskData( ChessBoardRecognitionTrainingTaskData taskData )
	{
		if( !isActivated() )
			return;

		try
		{
			_lock.lock();
			_trainingItems.add( taskData );
			notifyListeners();

			if( _lock.hasWaiters( _moreItemsAvailable ) )
				_moreItemsAvailable.signal();
		}
		finally
		{
			_lock.unlock();
		}
	}

	public boolean isEmpty()
	{
		try
		{
			_lock.lock();

			return( _trainingItems.isEmpty() );
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected void activateDetectionIfNecessary()
	{
		if( isActivated() && isEmpty() )
			_parent.getRecognizerThreads().setIsActivated(true);
	}

	protected ChessBoardRecognitionTrainingTaskData removeFirst()
	{
		try
		{
			ChessBoardRecognitionTrainingTaskData result = null;

			_lock.lock();

			boolean hasToShow = ( _trainingItems.size() > 0 );

			if( ! _trainingItems.isEmpty() )
			{
				result = _trainingItems.removeFirst();
				notifyListeners();
			}

			if( hasToShow )
				System.out.println( "Remaining Chess Board Recognition training items: " + _trainingItems.size() );

			return( result );
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected ChessBoardRecognitionTrainingTaskData getTrainingItem()
	{
		try
		{
			ChessBoardRecognitionTrainingTaskData result = null;

			_lock.lock();

			if( _trainingItems.isEmpty() && ! _hasToStop )
			{
				activateDetectionIfNecessary();

				ExecutionFunctions.instance().safeMethodExecution( () -> _moreItemsAvailable.awaitNanos( 1000000000L ) );
			}

			if( !_hasToStop )
				result = removeFirst();

			return( result );
		}
		finally
		{
			_lock.unlock();
		}
	}

	@Override
	public void run()
	{
		while( ! _hasToStop )
		{
			try
			{
				ChessBoardRecognitionTrainingTaskData trainingItem = getTrainingItem();

				if( trainingItem != null )
				{
					trainingItem.decNumberOfAttempts();

					String baseBoardFen = trainingItem.getBaseBoardFen();
					InputImage image = trainingItem.getImage();
					if( _trainer.train( baseBoardFen, image, _store) )
					{
						BiConsumer<RecognitionResult, InputImage> cons = trainingItem.getCallbackFunction();
						if( cons != null )
							cons.accept( _trainer.getDetector().getRecognitionResult(), image );
					}
					else if( trainingItem.getNumberOfRemainingAttempts() > 0 ) // one retrial
						addTrainigTaskData( trainingItem );
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	public void hasToStop( boolean value )
	{
		_hasToStop = value;
	}
}
