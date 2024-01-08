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
package com.frojasg1.applications.common.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ParameterListConfiguration extends ConfigurationParent
{
	protected String _baseLabelForList = null;

	// function for DefaultConstructorInitCopier
	public ParameterListConfiguration( ) {
		super();
	}

	public ParameterListConfiguration( String mainFolder, String applicationName, String group,
								String language, String configurationFileName )
	{
		this( mainFolder, applicationName, group, language, configurationFileName, "ITEM" );
	}

	public ParameterListConfiguration( String mainFolder, String applicationName, String group,
								String language, String configurationFileName,
								String baseLabelForList )
	{
		super( mainFolder, applicationName, group, language, configurationFileName );

		_baseLabelForList = baseLabelForList;
	}

	// function for DefaultConstructorInitCopier
	public void init( ParameterListConfiguration that )
	{
		super.init( (ConfigurationParent) that );

		_baseLabelForList = that._baseLabelForList;
	}

	@Override
	protected Properties M_getDefaultProperties(String language)
	{
		return( new Properties() );
	}

	public String getBaseLabelForList()
	{
		return( _baseLabelForList );
	}

	public void setBaseLabelForList( String value )
	{
		_baseLabelForList = value;
	}

	public List<String> getList()
	{
		return( M_getListParamConfiguration( getBaseLabelForList() ) );
	}

	public void setList( List<String> list )
	{
		M_setListParamConfiguration( getBaseLabelForList(), new ArrayList<>(list) );
	}
}
