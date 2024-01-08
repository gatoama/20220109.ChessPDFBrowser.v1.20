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
package com.frojasg1.general.dialogs.filefilter.impl;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.general.dialogs.filefilter.GenericFileFilter;
import java.io.File;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericFileFilterForExecutable implements GenericFileFilter
{
	protected static final String CONF_DESCRIPTION = GenericFileFilterChooserImpl.CONF_GENERIC_FILE_FILTER_FOR_EXECUTABLE_BY_ATTRIBUTES_DESCRIPTION;
	protected InternationalizedStringConf _internationalStrings = null;

	public GenericFileFilterForExecutable( InternationalizedStringConf internationalStrings )
	{
		_internationalStrings = internationalStrings;
	}

	@Override
	public boolean accept(File file)
	{
		return( file.canRead() && file.canExecute() );
	}

	@Override
	public String getDescription()
	{
		return( _internationalStrings.getInternationalString(CONF_DESCRIPTION) );
	}
}
