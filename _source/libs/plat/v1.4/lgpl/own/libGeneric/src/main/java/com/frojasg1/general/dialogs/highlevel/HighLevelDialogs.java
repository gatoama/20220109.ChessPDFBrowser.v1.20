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

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.GenericConstants;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.generic.GenericFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class HighLevelDialogs implements InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "HighLevelDialogs.properties";

	public static final Integer YES = 0;
	public static final Integer NO = 1;
	public static final Integer CANCEL = 2;

	public static final String CONF_WARNING = "WARNING";
	public static final String CONF_ERROR = "ERROR";
	public static final String CONF_INFORMATION = "INFORMATION";
	public static final String CONF_YES = "YES";
	public static final String CONF_NO = "NO";
	public static final String CONF_CANCEL = "CANCEL";

	protected static HighLevelDialogs _instance = null;

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	public static HighLevelDialogs instance()
	{
		if( _instance == null )
			_instance = new HighLevelDialogs();

		return( _instance );
	}

	protected HighLevelDialogs()
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								GenericConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	public int yesNoCancelDialog( ViewComponent parent, Object message, String title,
									Integer initialValue )
	{
		Object[] options = new Object[] {
			getInternationalString( CONF_YES ),
			getInternationalString( CONF_NO ),
			getInternationalString( CONF_CANCEL )
		};

		Object initialValueObj = null;
		if( YES.equals( initialValue ) )
			initialValueObj = options[YES];
		else if( NO.equals( initialValue ) )
			initialValueObj = options[NO];
		else if( CANCEL.equals( initialValue ) )
			initialValueObj = options[CANCEL];

		if( title == null )
			title = getInternationalString( CONF_WARNING );

		int answer = GenericFunctions.instance().getDialogsWrapper().showOptionDialog( parent,
													message,
													title,
													DialogsWrapper.YES_NO_CANCEL_OPTION,
													DialogsWrapper.WARNING_MESSAGE,
													options,
													initialValueObj );

		return( answer );
	}

	public void errorMessageDialog( ViewComponent parent, Object message )
	{
		GenericFunctions.instance().getDialogsWrapper().showMessageDialog( parent, message,
							getInternationalString( CONF_ERROR ),
							DialogsWrapper.ERROR_MESSAGE );
	}

	public void informationMessageDialog( ViewComponent parent, Object message )
	{
		GenericFunctions.instance().getDialogsWrapper().showMessageDialog( parent, message,
							getInternationalString( CONF_INFORMATION ),
							DialogsWrapper.INFORMATION_MESSAGE );
	}

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_WARNING, "Warning" );
		this.registerInternationalString(CONF_ERROR, "Error" );
		this.registerInternationalString(CONF_INFORMATION, "Information" );
		this.registerInternationalString(CONF_YES, "Yes" );
		this.registerInternationalString(CONF_NO, "No" );
		this.registerInternationalString(CONF_CANCEL, "Cancel" );
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

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
