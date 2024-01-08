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
package com.frojasg1.general.desktop.threads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class Generic_getResultEDT<AA> implements GetResultEDT<AA>
{
	protected ReentrantLock _lock = new ReentrantLock();
	protected Condition _informationCaught = _lock.newCondition();
	protected boolean _isFinished = false;

	protected GetResultEDT<AA> _functionToEvaluate = null;
	protected AA _result = null;

	// if you invoke this constructor, you should override function: public AA overridableFunctionGetResultEDT()
	public Generic_getResultEDT()
	{
	}

	public Generic_getResultEDT( GetResultEDT<AA> functionToEvaluate )
	{
		_functionToEvaluate = functionToEvaluate;
	}

	public AA overridableFunctionGetResultEDT()
	{
		return( _functionToEvaluate.getResultEDT() );
	}

	@Override
	public AA getResultEDT()
	{
		if( SwingUtilities.isEventDispatchThread() )
		{
			_result = overridableFunctionGetResultEDT();
		}
		else
		{
			try
			{
				_lock.lock();
				_isFinished = false;

				SwingUtilities.invokeLater(new Runnable(){
						@Override
						public void run()
						{
							try
							{
								_lock.lock();

								_result = overridableFunctionGetResultEDT();

								_isFinished = true;

								while( !_lock.hasWaiters(_informationCaught))
								{
									try
									{
										Thread.sleep(40);
									}
									catch( InterruptedException ie )
									{}
								}

								_informationCaught.signal();
							}
							finally
							{
								_lock.unlock();
							}
						}
				} );

				while( !_isFinished )
				{
					try
					{
						_informationCaught.await();
					}
					catch( InterruptedException ie )
					{}
				}
			}
			finally
			{
				_lock.unlock();
			}
		}

		return( _result );
	}
}
