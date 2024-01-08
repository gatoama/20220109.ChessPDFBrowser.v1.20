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

import com.frojasg1.general.executor.ExecutorInterface;
import com.frojasg1.general.executor.imp.ExecutorBase;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ExecuteEDT extends ExecutorBase
{
	protected ExecutorInterface _executor = null;

	// if you use this constructor, then you have to override function: public void overridableExecute()
	public ExecuteEDT( )
	{
	}

	public ExecuteEDT( ExecutorInterface ei )
	{
		_executor = ei;
	}

	public void overridableExecute()
	{
		_executor.execute();
	}

	@Override
	public void execute()
	{
		if( SwingUtilities.isEventDispatchThread() )
		{
			overridableExecute();
		}
		else
		{
			SwingUtilities.invokeLater( new Runnable() {
					@Override
					public void run()
					{
						overridableExecute();
					}
			});
		}
	}
}
