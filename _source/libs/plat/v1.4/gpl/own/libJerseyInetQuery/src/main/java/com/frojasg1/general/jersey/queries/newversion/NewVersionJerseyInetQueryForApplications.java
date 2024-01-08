/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 */
package com.frojasg1.general.jersey.queries.newversion;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.queries.InetQueryException;
import com.frojasg1.general.desktop.queries.newversion.NewVersionQueryResult;
import com.frojasg1.generic.GenericFunctions;

/**
 *
 * @author fjavier.rojas
 */
public class NewVersionJerseyInetQueryForApplications extends NewVersionJerseyInetQuery
{
	@Override
	public void init( String url )
	{
		super.init( url );
	}

	@Override
	public NewVersionQueryResult queryForApplication( boolean isApplicationStart ) throws InetQueryException
	{
		NewVersionQueryResult result = null;

		String downloadFile = getDownloadFile();
		String applicationLanguage = getApplicationLanguage();
		String webLanguage = getWebLanguage(applicationLanguage);
		boolean isDarkModeActivated = isDarkModeActivated();

		System.out.println( "New version query ..." );
		if( downloadFile != null )
		{
			result = query( downloadFile, isApplicationStart, applicationLanguage,
							webLanguage, isDarkModeActivated );
			if( result != null )
			{
				System.out.println( "isSuccessful: " + result.isSuccessful() );
				if( ! result.isSuccessful() )
					System.out.println( "Error: " + result.getErrorString() );
			}
		}
		else
		{
			System.out.println( "downloadFile was null" );
		}

		return( result );
	}

	protected String getDownloadFile()
	{
		return( GenericFunctions.instance().getApplicationFacilities().getApplicationVersion());
	}

	protected String getWebLanguage( String applicationLanguage )
	{
		return( GenericFunctions.instance().getObtainAvailableLanguages().
										getWebLanguageName(applicationLanguage) );
	}

	protected String getApplicationLanguage()
	{
		return( getAppliConf().getLanguage() );
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( GenericFunctions.instance().getAppliConf() );
	}

	protected boolean isDarkModeActivated()
	{
		return( getAppliConf().isDarkModeActivated() );
	}
}
