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
package com.frojasg1.applications.common.configuration.application;

import com.frojasg1.applications.common.VersionsRunListString;
import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterObserved;
import com.frojasg1.general.exceptions.ConfigurationException;

/**
 *
 * @author Usuario
 */
public interface BaseApplicationConfigurationInterface
	extends ChangeLanguageClientInterface, ChangeLanguageServerInterface,
			ChangeZoomFactorServerInterface, ConfigurationForFileChooserInterface,
			ConfigurationParameterObserved
{
	public boolean hasToEnableUndoRedoForTextComponents();
	public boolean hasToEnableTextCompPopupMenus();

	public String getHomePageUrl();
	public String getAuthorEmailAddress();

	public String getConfigurationMainFolder();
	public String getDefaultLanguageBaseConfigurationFolder();
	public String getDefaultLanguageConfigurationFolder( String language );
	public String getApplicationNameFolder();
	public String getApplicationName();
	public String getApplicationVersion();
	public String getApplicationGroup();
	public String getInternationalPropertiesPathInJar();

	public String getUrlForNewVersionQuery();
//	public void setUrlForNewVersionQuery( String value );

	public String getUrlForResourceCounter();
//	public void setUrlForResourceCounter( String value );

	public String getAdditionalLanguage();
	public void setAdditionalLanguage( String language );

	public String getDefaultLanguage();

	public String getResourceNameForApplicationIcon();

	public boolean getLicensesHaveBeenAccepted();
	public void setLicensesHaveBeenAccepted( boolean value );

	public String getLastExecutionDownloadFileName();
	public boolean hasBeenShownWhatIsNewOfDownloadFile(String downloadFile);
	public void addDownloadFileWhatIsNewShown( String downloadFile );
	public String getWhatIsNewShownDownladFileListString();
	public void setWhatIsNewShownDownladFileListString( String value );

	public String getYoungestVersionFileNameEverRun();
	public VersionsRunListString getVersionsRunListString();

	public String getDownloadFileToIgnore();
	public void setDownloadFileToIgnore( String value );

	public void toggleDarkMode();
	public boolean isDarkModeActivated();
	public void setDarkModeActivated( boolean value );

	public boolean isDefaultModeDark();
	
	public void M_saveConfiguration() throws ConfigurationException;
}
