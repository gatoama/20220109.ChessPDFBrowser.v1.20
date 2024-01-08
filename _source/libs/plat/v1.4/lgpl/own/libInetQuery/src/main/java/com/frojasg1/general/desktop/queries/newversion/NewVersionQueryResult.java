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
package com.frojasg1.general.desktop.queries.newversion;

import com.frojasg1.general.desktop.queries.InetQueryResultBase;

/**
 *
 * @author fjavier.rojas
 */
public class NewVersionQueryResult extends InetQueryResultBase
{
	protected boolean _successful = false;
	protected String _errorString = null;
	protected Boolean _thereIsANewVersion = null;
	protected Boolean _isAFinalVersion = null;
	protected String _link = null;
	protected Integer _numberOfDownloadsOfLatestVersion = null;
	protected Integer _totalNumberOfDownloadsOfApplication = null;
	protected String _hintForDownload = null;
	protected String _newDownloadResource = null;

	public boolean isSuccessful()
	{
		return( _successful );
	}

	public void setSuccessful( boolean value )
	{
		_successful = value;
	}

	public String getErrorString()
	{
		return( _errorString );
	}

	public void setErrorString( String value )
	{
		_errorString = value;
	}

	public Boolean thereIsANewVersion()
	{
		return( _thereIsANewVersion );
	}

	public void setThereIsANewVersion( Boolean value )
	{
		_thereIsANewVersion = value;
	}

	public boolean isAFinalVersion()
	{
		return( _isAFinalVersion );
	}

	public void setIsAFinalVersion( Boolean value )
	{
		_isAFinalVersion = value;
	}

	public String getLink()
	{
		return( _link );
	}

	public void setLink( String value )
	{
		_link = value;
	}

	public Integer getNumberOfDownloadsOfLatestVersion()
	{
		return( _numberOfDownloadsOfLatestVersion );
	}

	public void setNumberOfDownloadsOfLatestVersion( Integer value )
	{
		_numberOfDownloadsOfLatestVersion = value;
	}

	public Integer getTotalNumberOfDownloadsOfApplication()
	{
		return( _totalNumberOfDownloadsOfApplication );
	}

	public void setTotalNumberOfDownloadsOfApplication( Integer value )
	{
		_totalNumberOfDownloadsOfApplication = value;
	}

	public String getHintForDownload()
	{
		return( _hintForDownload );
	}

	public void setHintForDownload( String value )
	{
		_hintForDownload = value;
	}

	public String getNewDownloadResource()
	{
		return( _newDownloadResource );
	}

	public void setNewDownloadResource( String value )
	{
		_newDownloadResource = value;
	}
}
