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
package com.frojasg1.general.timers;

import java.util.TimerTask;

/**
 *
 * @author fjavier.rojas
 */
public class TimerFunctions
{
	protected static TimerFunctions _instance;

	public static void changeInstance( TimerFunctions inst )
	{
		_instance = inst;
	}

	public static TimerFunctions instance()
	{
		if( _instance == null )
			_instance = new TimerFunctions();
		return( _instance );
	}

	public TimerTask createTimerTask( Runnable runnable )
	{
		TimerTask result = new TimerTask() {
			@Override
			public void run()
			{
				runnable.run();
			}
		};

		return( result );
	}
}
