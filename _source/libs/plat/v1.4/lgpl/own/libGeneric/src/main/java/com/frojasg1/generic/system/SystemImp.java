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
package com.frojasg1.generic.system;

import com.frojasg1.general.string.StringFunctions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class SystemImp implements SystemInterface
{
	//https://stackoverflow.com/questions/24924072/website-url-validation-regex-in-java/37864510
	// modified
	protected Pattern _urlPattern = Pattern.compile( "^((https?:\\/\\/)(www\\.)?|(www\\.))([\\w\\Q$-_+!*'(),%\\E]+\\.)+[‌​\\w]{2,63}(:\\d+)?(\\/.*)?$" );

	//http://emailregex.com/
	protected Pattern _emailAddressPattern = Pattern.compile( "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])" );

	@Override
	public boolean isEmailAddress( String text )
	{
		boolean result = StringFunctions.instance().regExMatches( _emailAddressPattern, text );

		return( result );
	}

	@Override
	public boolean isUrl( String text )
	{
		boolean result = StringFunctions.instance().regExMatches( _urlPattern, text );

		return( result );
	}
}
