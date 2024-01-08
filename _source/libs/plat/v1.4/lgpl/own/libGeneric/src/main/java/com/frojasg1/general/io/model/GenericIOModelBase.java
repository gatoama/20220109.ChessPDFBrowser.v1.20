/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io.model;

import com.frojasg1.general.collection.ModifiedStatus;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericIOModelBase implements ModifiedStatus
{
	protected boolean _hasBeenModified = false;

	@Override
	public void setHasBeenModified( boolean value )
	{
		_hasBeenModified = value;
	}

	@Override
	public boolean hasBeenModified()
	{
		return( _hasBeenModified );
	}
}
