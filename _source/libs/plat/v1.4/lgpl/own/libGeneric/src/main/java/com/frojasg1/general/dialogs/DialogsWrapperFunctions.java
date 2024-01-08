/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.dialogs;

import com.frojasg1.general.dialogs.filefilter.FilterForFile;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DialogsWrapperFunctions
{
	protected static class LazyHolder
	{
		public static final DialogsWrapperFunctions INSTANCE = new DialogsWrapperFunctions();
	}

	public static DialogsWrapperFunctions instance()
	{
		return LazyHolder.INSTANCE;
	}

	public FilterForFile createExtensionFilter( String description, String extension )
	{
		return( new FilterForFile( description, extension ) );
	}

	public List<FilterForFile> createListOfFilters( String ... texts )
	{
		if( (texts.length) % 2 != 0 )
			throw( new IllegalArgumentException( "Error, texts length was not an even number" ) );

		List<FilterForFile> result = new ArrayList<FilterForFile>();
		for( int ii=0; ii<texts.length; ii += 2)
		{
			String description = texts[ii];
			String extension = texts[ii+1];
			result.add( createExtensionFilter(description, extension) );
		}

		return( result );
	}
}
