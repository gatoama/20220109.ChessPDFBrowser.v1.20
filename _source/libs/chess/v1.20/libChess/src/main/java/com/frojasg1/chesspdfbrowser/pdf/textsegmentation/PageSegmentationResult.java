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
package com.frojasg1.chesspdfbrowser.pdf.textsegmentation;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class PageSegmentationResult
{
	protected List<Rectangle> _segmentedRegions;
	protected boolean _coudBeValidated = false;

	public PageSegmentationResult()
	{
		_segmentedRegions = new ArrayList<Rectangle>();
	}

	public void setCouldBeValidated( boolean value )
	{
		_coudBeValidated = value;
	}

	public boolean getCouldBeValidated()
	{
		return( _coudBeValidated );
	}

	public List<Rectangle> geListOfSegmentedRegions()
	{
		return( _segmentedRegions );
	}

	public void addSegmentedRegion( Rectangle region )
	{
		_segmentedRegions.add( region );
	}
	
	public void setListOfSegmentedRegions( List<Rectangle> list )
	{
		_segmentedRegions = list;
	}
}
