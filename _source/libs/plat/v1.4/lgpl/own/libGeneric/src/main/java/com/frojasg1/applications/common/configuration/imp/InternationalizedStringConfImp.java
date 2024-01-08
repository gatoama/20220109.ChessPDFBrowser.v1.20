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
package com.frojasg1.applications.common.configuration.imp;

import com.frojasg1.general.string.CreateCustomString;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageClientInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageServerInterface;
import com.frojasg1.generic.GenericFunctions;
import java.util.Properties;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.general.ExecutionFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class InternationalizedStringConfImp implements ChangeLanguageClientInterface,
												InternationalizedStringConf
{
	protected FormLanguageConfiguration a_formLanguageConfiguration = null;

	protected BaseApplicationConfigurationInterface _appliConf = null;

	public InternationalizedStringConfImp( String languageConfigurationFileName,
									String languagePackageName )
	{
		this( GenericFunctions.instance().getAppliConf(),
				languageConfigurationFileName,
				languagePackageName);
	}

	public InternationalizedStringConfImp( BaseApplicationConfigurationInterface appliConf,
									String languageConfigurationFileName,
									String languagePackageName )
	{
		try
		{
			_appliConf = appliConf;

			if( _appliConf != null )
				a_formLanguageConfiguration = new FormLanguageConfiguration(
										_appliConf.getConfigurationMainFolder(),
										_appliConf.getApplicationNameFolder(),
										_appliConf.getApplicationGroup(),
										languageConfigurationFileName,
										languagePackageName,
										new Properties() );
			else
				a_formLanguageConfiguration = new FormLanguageConfiguration(
										null,
										null,
										null,
										languageConfigurationFileName,
										languagePackageName,
										new Properties() );

			if( _appliConf != null )
				_appliConf.registerChangeLanguageObserver(this);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		

		ExecutionFunctions.instance().safeMethodExecution( () -> a_formLanguageConfiguration.changeLanguage( getLanguage() ) );
	}

	@Override
	public String getLanguage()
	{
		String result = null;
		if( _appliConf != null )
			result = _appliConf.getLanguage();

		return( result );
	}

	@Override
	public void changeLanguage(String newLanguage) throws Exception
	{
		a_formLanguageConfiguration.changeLanguage(newLanguage);
	}

	@Override
	public void unregisterFromChangeLanguageAsObserver()
	{
		if( _appliConf != null )
			_appliConf.unregisterChangeLanguageObserver(this);
	}

	@Override
	public void registerToChangeLanguageAsObserver(ChangeLanguageServerInterface conf)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		a_formLanguageConfiguration.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		String result = a_formLanguageConfiguration.getInternationalString(label);
		if( result == null )
			result = label;
		return( result );
	}

	@Override
	public String createCustomInternationalString( String label, Object ... args )
	{
		return( CreateCustomString.instance().createCustomString( getInternationalString( label ), args) );
	}
}
