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
package com.frojasg1.generic.languages;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface ObtainAvailableLanguages_int
{
	public void setBasicLanguages( String[] basicLanguages );
	public void setAvailableLanguagesInJar( String[] availableLanguagesInJar );
	public void setAvailableWebLanguageNames( String[] availableWebLanguagesNames );

	public void setRootLanguageConfigurationPathInDisk( String rootLanguageConfigurationPathInDisk );
	public String getRootLanguageConfigurationPathInDisk();
	public ListOfLanguagesResult_int getTotalListOfAvailableLanguages();
//	public ListOfLanguagesResult_int getTotalListOfAvailableLanguages(String rootLanguagePackage,
//													String rootLanguageConfigurationPathInDisk );
//	public List<String> getListOfAvailableLanguagesInPackage();
//	public List<String> getListOfAvailableLanguagesConfiguredInDisk();
//	public List<String> getListOfAvailableLanguagesConfiguredInDisk( String rootLanguageConfigurationPathInDisk );

	public String[] getTotalArrayOfAvailableLanguages();
	public List<String> getListOfAvailableWebLanguageNames();

	public void reloadLanguages();

	public Locale getLocaleOfLanguage( String language );
	public Locale getLocaleOfLanguageFromJar( String language );

	public void setLocaleLanguageOfLanguage( String language, String javaLocaleLanguage,
												String webLanguageName );

	public void newLanguageSetToConfiguration( String language, String javaLocaleLanguage,
												String webLanguageName );

	public void updateLocaleLanguagesToDisk();

	public String getLanguageOfLocale( Locale locale );

	public String getDefaultLanguage();
	public String getDefaultWebLanguage();

	public String getWebLanguageName( String language );
}
