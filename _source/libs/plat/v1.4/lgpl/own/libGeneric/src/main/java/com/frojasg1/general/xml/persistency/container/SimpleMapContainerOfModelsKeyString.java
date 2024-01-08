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
package com.frojasg1.general.xml.persistency.container;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.xml.model.KeyModel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class SimpleMapContainerOfModelsKeyString<MM extends KeyModel<String> >
			extends SimpleMapContainerOfModels<String, MM>
			implements InternationalizedStringConf
{
	@Override
	public void init( SimpleMapContainerOfModels<String, MM> that )
	{
		throw( new RuntimeException( "Non usable init function" ) );
	}

	public void init( BaseApplicationConfigurationInterface appliConf,
						String languageGlobalConfFileName,
						String languagePropertiesFolderInJar,
						Function<String, String> fileNameCreatorFunction )
	{
		super.init( appliConf, languageGlobalConfFileName,
					languagePropertiesFolderInJar,
					fileNameCreatorFunction );
	}

	public void init( SimpleMapContainerOfModelsKeyString<MM> that )
	{
		super.init( that );
	}

	protected <KKK, VVV> Map<KKK, VVV> createMap()
	{
		return( new HashMap<>() );
	}

	@Override
	public String getRelativeFileNameFromItemList( String item )
	{
		return( createRelativeFileName(item) );
	}
/*
	@Override
	public void add( MM rwc )
	{
		super.add(rwc);
		
		if( rwc != null )
			this.getComboBoxContent().addItem( rwc.getKey() );
	}

	public MM remove( String key )
	{
		MM result = super.remove(key);
		this.getComboBoxContent().removeItem(key);

		return( result );
	}
*/
}
