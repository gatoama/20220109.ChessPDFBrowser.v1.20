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

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.desktop.queries.InetQueryException;
import com.frojasg1.general.desktop.queries.newversion.NewVersionQuery;
import com.frojasg1.general.desktop.queries.newversion.NewVersionQueryResult;
import com.frojasg1.general.jersey.queries.InetQueryBase;
import org.json.JSONObject;

/**
 *
 * @author fjavier.rojas
 */
public abstract class NewVersionJerseyInetQuery extends InetQueryBase<NewVersionQueryResult>
								implements NewVersionQuery
{
//	protected static final String URL = "https://frojasg1.com:8080/downloads_web";
	protected static final String PATH = "/restful/versionQueryQueryInput";

	protected static final String VERSION_OF_SERVICE_PARAM = "versionOfService";
	protected static final String VERSION_OF_SERVICE_VALUE = "1.0";

	protected static final String DOWNLOAD_FILE_PARAM = "downloadFile";
	protected static final String IS_APPLICATION_START_PARAM = "isApplicationStart";
	protected static final String APPLICATION_LANGUAGE_PARAM = "applicationLanguage";
	protected static final String WEB_LANGUAGE_PARAM = "webLanguage";
	protected static final String IS_DARK_MODE_ACTIVATED_PARAM = "isDarkModeActivated";

	@Override
	public void init( String url )
	{
		super.init( url, PATH );
	}

	@Override
	protected NewVersionQueryResult createInetQueryResult()
	{
		return( new NewVersionQueryResult() );
	}

	@Override
	public abstract NewVersionQueryResult queryForApplication( boolean isApplicationStart ) throws InetQueryException;

	protected <CC> CC safeFunction( ExecutionFunctions.UnsafeFunction<CC> run )
	{
		return( ExecutionFunctions.instance().safeFunctionExecution(run) );
	}

	protected String getBaseFileName( String longFileName )
	{
		return( FileFunctions.instance().getBaseName(longFileName) );
	}

	protected boolean getBooleanWithDefault( JSONObject jsonObject, String fieldName, boolean defaultValue )
	{
		Boolean result = safeFunction( () -> jsonObject.getBoolean( fieldName ) );
		if( result == null )
			result = defaultValue;

		return( result );
	}

	protected Integer getInt( JSONObject jsonObject, String fieldName )
	{
		return( safeFunction( () -> jsonObject.getInt( fieldName ) ) );
	}

	protected String getString( JSONObject jsonObject, String fieldName )
	{
		return( safeFunction( () -> jsonObject.getString( fieldName ) ) );
	}

	protected boolean isSuccessful( JSONObject jsonObject )
	{
		return( getBooleanWithDefault( jsonObject, "successful", false ) );
	}

	protected NewVersionQueryResult createNewVersionQueryResult( JSONObject jsonObject )
	{
		NewVersionQueryResult result = createInetQueryResult();

		boolean successful = isSuccessful( jsonObject );
		result.setSuccessful( successful );
		if( successful )
		{
			boolean thereIsANewVersion = getBooleanWithDefault( jsonObject, "thereIsANewVersion", false );
			result.setThereIsANewVersion( thereIsANewVersion );

			if( thereIsANewVersion )
			{
				result.setIsAFinalVersion(getBooleanWithDefault( jsonObject, "isAFinalVersion", false ) );
				result.setLink( getString( jsonObject, "link" ) );
				result.setHintForDownload( getString( jsonObject, "hintForDownload" ) );
				result.setNewDownloadResource( getBaseFileName( getString( jsonObject, "newDownloadResource" ) ) );

				result.setNumberOfDownloadsOfLatestVersion( getInt( jsonObject, "numberOfDownloadsOfLatestVersion" ) );
				result.setTotalNumberOfDownloadsOfApplication( getInt( jsonObject, "totalNumberOfDownloadsOfApplication" ) );
			}
		}
		else
		{
			result.setErrorString( getString( jsonObject, "errorString" ) );
		}

		return( result );
	}

	@Override
	protected NewVersionQueryResult convert( String jsonStr )
	{
		NewVersionQueryResult result = null;
		
		try
		{
			result = createNewVersionQueryResult( getJsonObject(jsonStr) );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	@Override
	public NewVersionQueryResult query( String downloadFile, boolean isApplicationStart,
										String applicationLanguage,
										String webLanguage,
										boolean isDarkModeActivated) throws InetQueryException
	{
		return( queryGen( VERSION_OF_SERVICE_PARAM, VERSION_OF_SERVICE_VALUE,
						DOWNLOAD_FILE_PARAM, downloadFile,
						IS_APPLICATION_START_PARAM, booleanToString(isApplicationStart),
						APPLICATION_LANGUAGE_PARAM, applicationLanguage,
						WEB_LANGUAGE_PARAM, webLanguage,
						IS_DARK_MODE_ACTIVATED_PARAM, booleanToString(isDarkModeActivated)) );
	}
}
