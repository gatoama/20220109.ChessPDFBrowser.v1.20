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
package com.frojasg1.general.dialogs.highlevel;

import com.frojasg1.general.clipboard.SystemClipboard;
import com.frojasg1.general.view.ViewComponent;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DebugDialog {

	protected static DebugDialog _instance = null;

	protected boolean _debug = false;

	protected StringBuilder _sb = new StringBuilder();

	protected ReentrantLock _lock = new ReentrantLock( true );

	public static DebugDialog instance()
	{
		if( _instance == null )
			_instance = new DebugDialog();

		return( _instance );
	}

	protected DebugDialog()
	{
	}

	public Integer yesNoCancelDialogAlways( ViewComponent parent, Object message, String title,
									Integer initialValue )
	{
		_sb.setLength(0);
		return( yesNoCancelDialogAlways_internal( parent, message, title, initialValue ) );
	}

	public Integer yesNoCancelDialogAlways_internal( ViewComponent parent, Object message, String title,
									Integer initialValue )
	{
		log( message );
		SystemClipboard.instance().setClipboardContents( _sb.toString() );

		Integer result = HighLevelDialogs.instance().yesNoCancelDialog(null, message, title, initialValue );

		if( Objects.equals(result, HighLevelDialogs.YES) )
			_debug = true;
		else
			_debug = false;

		return( result );
	}

	protected String toString( Object obj )
	{
		String result = "null";
		if( obj != null )
			result = obj.toString();

		return( result );
	}

	public void log( Object obj )
	{
		try
		{
			_lock.lock();

			_sb.append( toString( obj ) ).append( "\n" );
		}
		finally
		{
			_lock.unlock();
		}
	}

	public Integer yesNoCancelDialog( ViewComponent parent, Object message, String title,
									Integer initialValue )
	{
		Integer result = null;

		if( _debug )
			result = yesNoCancelDialogAlways_internal(null, message, title, initialValue );

		return( result );
	}
}
