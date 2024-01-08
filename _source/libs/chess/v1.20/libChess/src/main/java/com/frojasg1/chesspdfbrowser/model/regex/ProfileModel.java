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
package com.frojasg1.chesspdfbrowser.model.regex;

import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ProfileModel
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected RegexWholeFileModel _parent = null;

	protected List<LineModel> _listOfLines = null;

	protected String _profileName = null;

	protected boolean _isActive = true;

	// function for DefaultConstructorInitCopier
	public ProfileModel( )
	{
		
	}

	// function for DefaultConstructorInitCopier
	public void init( ProfileModel that )
	{
		_listOfLines = _copier.copy( that._listOfLines );

		_parent = that.getParent();

		_profileName = that._profileName;
		_isActive = that._isActive;

		for( LineModel line: _listOfLines )
			line.setParent( this );
	}

	public void init( String profileName,
						RegexWholeFileModel parent )
	{
		_profileName = profileName;

		_parent = parent;
		_listOfLines = new ArrayList<>();
	}

	public void setProfileName( String profileName )
	{
		_profileName = profileName;
	}

	public String getProfileName()
	{
		return( _profileName );
	}

	public RegexWholeFileModel getParent()
	{
		return( _parent );
	}

	public List<LineModel> getListOfLines( )
	{
		return( _listOfLines );
	}

	protected RegexOfBlockModel createRegexOfBlockModel( String tagName, RegexOfBlockModel other )
	{
		RegexOfBlockModel result = new RegexOfBlockModel( tagName, _parent.getBlockConfigurationContainer() );

		if( other != null )
			result.init((RegexOfBlockModel) other );
//		else
//			result.init( regexConf );

		return( result );
	}

	protected LineModel createEmptyLineOfTagRegexes()
	{
		LineModel result = new LineModel();
		result.init( this );

		return( result );
	}

	public void addEmptyLineOfTagRegexes( int index, LineModel lotr )
	{
		_listOfLines.add( index, lotr );
	}

	public boolean eraseLineModel( LineModel lineModel )
	{
		return( _listOfLines.remove(lineModel) );
	}

	public LineModel addEmptyLineOfTagRegexes( int index )
	{
		LineModel result = createEmptyLineOfTagRegexes();

		addEmptyLineOfTagRegexes( index, result );

		return( result );
	}
/*
	public void invalidateCaches()
	{
		for( LineModel lotr: _listOfLines )
			lotr.invalidateCaches();
	}
*/
	public boolean contains( String regexName )
	{
		boolean result = false;
		for( LineModel lotr: _listOfLines )
			if( lotr.contains( regexName ) )
			{
				result = true;
				break;
			}

		return( result );
	}

	public boolean isActive()
	{
		return( _isActive );
	}

	public void setActive( boolean value )
	{
		_isActive = value;
	}
}
