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
package com.frojasg1.general.dialogstoshow.imp;

import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.general.dialogstoshow.GenericDialogToShowInt;
import com.frojasg1.general.zoom.ZoomInterface;
import com.frojasg1.generic.GenericFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class OptionDialogToShow extends GenericDialogToShowBase
{
	protected Object _message = null;
	protected String _title = null;
	protected int _optionType = -1;
	protected int _messageType = -1;
	protected Object[] _options = null;
	protected Object _initialValue = null;
	protected ZoomInterface _conf = null;

	/**
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 * @param optionType		an example for this parameter: DialogsWrapper.YES_NO_CANCEL_OPTION
	 * @param messageType		an example for this parameter: DialogsWrapper.QUESTION_MESSAGE
	 * @param options
	 * @param initialValue
	 * @return 
	 */
	public OptionDialogToShow( ViewComponent parent, Object message, String title, int optionType,
								int messageType, Object[] options, Object initialValue,
								ZoomInterface conf )
	{
		super( parent );

		_message = message;
		_title = title;
		_optionType = optionType;
		_messageType = messageType;
		_options = options;
		_initialValue = initialValue;
		_conf = conf;
	}

	public void setMessage( Object message )
	{
		_message = message;
	}

	public void setTitle( String title )
	{
		_title = title;
	}

	public void setOptionType( int optionType )
	{
		_optionType = optionType;
	}

	public void setMessageType( int messageType )
	{
		_messageType = messageType;
	}

	public void setOptions( Object[] options )
	{
		_options = options;
	}

	public void setInitialValue( Object initialValue )
	{
		_initialValue = initialValue;
	}

	@Override
	public Result showDialog()
	{
		int chosenIndex = GenericFunctions.instance().getDialogsWrapper().showOptionDialog( _parent, _message,
																							_title, _optionType, _messageType,
																							_options, _initialValue, _conf );

		Result result = new Result( chosenIndex );

		return( result );
	}

	public static class Result implements GenericDialogToShowInt.Result
	{
		protected int _chosenIndex = -1;

		public Result( int chosenIndex )
		{
			_chosenIndex = chosenIndex;
		}

		public int getChosenIndex()
		{
			return( _chosenIndex );
		}
	}
}
