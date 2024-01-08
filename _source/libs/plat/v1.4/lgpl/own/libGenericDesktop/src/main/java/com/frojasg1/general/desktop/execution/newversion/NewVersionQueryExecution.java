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
package com.frojasg1.general.desktop.execution.newversion;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.application.version.DesktopApplicationVersion;
import com.frojasg1.general.desktop.queries.newversion.NewVersionQuery;
import com.frojasg1.general.desktop.queries.newversion.NewVersionQueryFactory;
import com.frojasg1.general.desktop.queries.newversion.NewVersionQueryResult;
import com.frojasg1.general.desktop.view.newversion.NewVersionFoundJDialog;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author fjavier.rojas
 */
public class NewVersionQueryExecution implements Runnable
{
	protected BaseApplicationConfigurationInterface _applicationConfiguration;
	protected boolean _isStartQuery;
	protected boolean _onlyShowResultWhenThereIsANewDownloadableVersion = false;

	protected JFrame _parentJFrame = null;

	protected boolean _urlClicked = false;

	protected Consumer<NewVersionQueryExecution> _callback = null;

	public NewVersionQueryExecution( JFrame parentJFrame,
									BaseApplicationConfigurationInterface applicationConfiguration,
									boolean isStartQuery,
									boolean onlyShowResultWhenThereIsANewDownloadableVersion,
									Consumer<NewVersionQueryExecution> callback )
	{
		_callback = callback;
		_parentJFrame = parentJFrame;

		_applicationConfiguration = applicationConfiguration;
		_isStartQuery = isStartQuery;
		_onlyShowResultWhenThereIsANewDownloadableVersion = onlyShowResultWhenThereIsANewDownloadableVersion;
	}

	protected NewVersionQuery createNewVersionInetQueryForApplications()
	{
		NewVersionQueryFactory factory = NewVersionQueryFactory.instance();
		NewVersionQuery result = null;
		
		if( factory != null )
		{
			result = factory.createNewVersionQuery();
			result.init( getAppliConf().getUrlForNewVersionQuery() );
		}

		return( result );
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _applicationConfiguration );
	}

	protected NewVersionFoundJDialog createNewVersionFoundJDialog( NewVersionQueryResult nvqResult,
																	Consumer<InternationalizationInitializationEndCallback> windowCallback )
	{
		NewVersionFoundJDialog result = new NewVersionFoundJDialog( _parentJFrame, true, _applicationConfiguration );
		result.setApplicationVersion(DesktopApplicationVersion.instance() );
		result.setInternationalizationEndCallBack(windowCallback);
		result.init( nvqResult );

		return( result );
	}

	protected boolean isIgnored( NewVersionQueryResult nvqr )
	{
		boolean result = false;
		
		if( nvqr.getNewDownloadResource() != null )
		{
			result = Objects.equals(nvqr.getNewDownloadResource(), _applicationConfiguration.getDownloadFileToIgnore() );
		}

		return( result );
	}

	@Override
	public void run()
	{
		try
		{
			NewVersionQuery nvq = createNewVersionInetQueryForApplications();
			NewVersionQueryResult result = ( nvq != null ) ?
											nvq.queryForApplication(_isStartQuery) :
											null;

			SwingUtilities.invokeLater( () -> showNewVersionQueryResult( result ) );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public void showNewVersionQueryResult(NewVersionQueryResult result)
	{
		try
		{
			if( ( result != null ) &&
				( ! _onlyShowResultWhenThereIsANewDownloadableVersion ||
					( result.isSuccessful() ) && result.thereIsANewVersion() &&
					!isIgnored( result )
				)
			  )
			{
				NewVersionFoundJDialog dialog = createNewVersionFoundJDialog( result,
															this::processNewVersionFoundJDialog );
			}
			else
				invokeCallback();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			invokeCallback();
		}
	}

	public void processNewVersionFoundJDialog( InternationalizationInitializationEndCallback iiec )
	{
		try
		{
			NewVersionFoundJDialog dialog = (NewVersionFoundJDialog) iiec;

			dialog.setVisible(true);

			_urlClicked = dialog.wasUrlClicked();
			if( _urlClicked )
			{
				urlClicked();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		invokeCallback();
	}

	protected void invokeCallback()
	{
		if( _callback != null )
			_callback.accept(this);
	}

	public boolean wasUrlClicked()
	{
		return( _urlClicked );
	}

	protected void urlClicked()
	{
	}
}
