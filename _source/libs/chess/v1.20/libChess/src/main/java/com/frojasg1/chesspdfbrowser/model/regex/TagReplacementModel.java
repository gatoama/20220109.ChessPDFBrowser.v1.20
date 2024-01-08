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

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TagReplacementModel
{
	protected String _blockToReplaceWith = null;

	protected String _tagName = null;


	// function for DefaultConstructorInitCopier
	public TagReplacementModel()
	{
		
	}

	// function for DefaultConstructorInitCopier
	public void init( TagReplacementModel that )
	{
		_blockToReplaceWith = that._blockToReplaceWith;
		_tagName = that._tagName;
	}

	public void setTagName( String tagName )
	{
		_tagName = tagName;
	}

	public String getTagName()
	{
		return( _tagName );
	}

	public void setBlockToReplaceWith( String value )
	{
		_blockToReplaceWith = value;
	}

	public String getBlockToReplaceWith()
	{
		return( _blockToReplaceWith );
	}
}
