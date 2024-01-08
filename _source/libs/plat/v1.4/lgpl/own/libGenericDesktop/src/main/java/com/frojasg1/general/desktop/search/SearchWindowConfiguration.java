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
package com.frojasg1.general.desktop.search;

import com.frojasg1.applications.common.configuration.ConfigurationParent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SearchWindowConfiguration extends ConfigurationParent
{
    public static final String CONF_IS_REGEX = "IS_REGEX";
    public static final String CONF_MATCH_CASE = "MATCH_CASE";
    public static final String CONF_WHOLE_WORDS = "WHOLE_WORDS";
    public static final String CONF_REPLACE = "REPLACE";
    public static final String CONF_MAX_ITEMS_IN_HISTORY = "MAX_ITEMS_IN_HISTORY";
    public static final String CONF_ALWAYS_ON_TOP = "ALWAYS_ON_TOP";
    public static final String CONF_SEARCH_HISTORY_PREFIX = "SEARCH_HISTORY_";
    public static final String CONF_REPLACE_HISTORY_PREFIX = "REPLACE_HISTORY_";

	public SearchWindowConfiguration(	String mainFolder,
										String applicationName, String group,
										String configurationFileName )
	{
		super( mainFolder, applicationName, group, null, configurationFileName );
	}

	protected Properties M_getDefaultProperties( String language )
	{
		Properties result = new Properties();

		result.setProperty( CONF_IS_REGEX, "0" );
		result.setProperty( CONF_MATCH_CASE, "0" );
		result.setProperty( CONF_WHOLE_WORDS, "0" );
		result.setProperty( CONF_REPLACE, "0" );
		result.setProperty( CONF_ALWAYS_ON_TOP, "1" );
		result.setProperty( CONF_MAX_ITEMS_IN_HISTORY, "10" );

//		a_defaultPropertiesReadFromComponents = M_makePropertiesAddingDefaults(a_defaultPropertiesReadFromComponents, a_defaultPropertiesReadFromComponents );

		return( result );
	}

	public Collection<String> getCollectionOfSearchItems()
	{
		return( getCollectionOfItemsFromPrefixLabel( CONF_SEARCH_HISTORY_PREFIX ) );
	}

	public Collection<String> getCollectionOfReplaceItems()
	{
		return( getCollectionOfItemsFromPrefixLabel( CONF_REPLACE_HISTORY_PREFIX ) );
	}

	public void setCollectionOfSearchItems( Collection<String> col )
	{
		setCollectiontOfPrefixLabel( CONF_SEARCH_HISTORY_PREFIX, col );
	}
	
	public void setCollectionOfReplaceItems( Collection<String> col )
	{
		setCollectiontOfPrefixLabel( CONF_REPLACE_HISTORY_PREFIX, col );
	}
	
	protected Collection<String> getCollectionOfItemsFromPrefixLabel( String labelPrefix )
	{
		ArrayList<String> result = new ArrayList<String>();

		int max = this.M_getIntParamConfiguration( CONF_MAX_ITEMS_IN_HISTORY );

		String item = "";
		for( int ii=1; (ii<= max) && (item != null); ii++ )
		{
			String label = getLabelFromPrefixAndIndex( labelPrefix, ii );
			item = this.M_getStrParamConfiguration( label );

			if( item != null )
				result.add( item );
		}
		return( result );
	}

	protected void setCollectiontOfPrefixLabel( String labelPrefix, Collection<String> col )
	{
		int max = this.M_getIntParamConfiguration( CONF_MAX_ITEMS_IN_HISTORY );

		int ii=0;
		String label;
		Iterator<String> it = col.iterator();
		while( it.hasNext() && ( ii<max ) )
		{
			label = getLabelFromPrefixAndIndex( labelPrefix, ii+1 );
			M_setStrParamConfiguration(label, it.next() );
			ii++;
		}

		label = getLabelFromPrefixAndIndex( labelPrefix, ii+1 );
		removeLabel(label);
	}

	protected String getLabelFromPrefixAndIndex( String labelPrefix, int index )
	{
		String result = String.format("%s%02d", labelPrefix, index );
		return( result );
	}
}
