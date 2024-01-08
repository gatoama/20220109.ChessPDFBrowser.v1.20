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
package com.frojasg1.chesspdfbrowser.view.chess.completion;

import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.completion.InputTextComponentListener;
import com.frojasg1.general.desktop.completion.base.WholeCompletionManagerBase;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class WholeCompletionManager extends WholeCompletionManagerBase
{
	protected RegexWholeFileModel _regexWholeContainer = null;

	public void init( ApplicationConfiguration appliConf )
	{
		super.init( appliConf );
	}

	@Override
	protected DesktopInputTextCompletionManagerImp createDesktopInputTextCompletionManager()
	{
		DesktopInputTextCompletionManagerImp result = new DesktopInputTextCompletionManagerImp( getAppliConf(), _regexWholeContainer.getBlockConfigurationContainer() );

		result.setPrototypeManager(_prototypeManager);
		_inputTextListener.setCompletionManager(result);

		return( result );
	}

	@Override
	protected InputTextComponentListener createInputTextComponentListener()
	{
		return( super.createInputTextComponentListener() );
	}

	@Override
	protected TranslatorOfTypeForCompletion createTranslatorOfType()
	{
		TranslatorOfTypeForCompletion result = new TranslatorOfTypeForCompletion( getAppliConf() );
		ExecutionFunctions.instance().safeMethodExecution( () -> result.M_openConfiguration() );

		return( result );
	}

	@Override
	protected DesktopCompletionWindow createCompletionWindow( Component compForNotHiding )
	{
		DesktopCompletionWindow result = null;

		Component mainWindow = ComponentFunctions.instance().getAncestor( compForNotHiding );
		if( mainWindow instanceof JFrame )
			result = new DesktopCompletionWindow( (JFrame) mainWindow, getAppliConf(),
													_translatorOfType );
		else
		{
			JDialog dialog = null;
			if( mainWindow instanceof JDialog )
				dialog = (JDialog) mainWindow;

			result = new DesktopCompletionWindow( dialog, getAppliConf(),
													_translatorOfType );
		}

//		result = new DesktopCompletionWindow( getAppliConf(), _translatorOfType );

		result.init();

		result.setComponentForNotHiding( compForNotHiding );

		return( result );
	}

	@Override
	protected ApplicationConfiguration getAppliConf()
	{
		return( (ApplicationConfiguration) super.getAppliConf() );
	}

	@Override
	protected PrototypeManager createPrototypeManager()
	{
		return( new PrototypeManager() );
	}

	@Override
	protected PrototypeManager getPrototypeManager()
	{
		return( (PrototypeManager) super.getPrototypeManager() );
	}

	public void setInputTextCompletionManager( RegexWholeFileModel regexWholeContainer,
													JTextComponent textcomp,
													Component compForNotHiding )
	{
		if( _regexWholeContainer != regexWholeContainer )
			setRegexConfigurations( regexWholeContainer );

		_regexWholeContainer = regexWholeContainer;

		setInputTextCompletionManager( textcomp, compForNotHiding );
	}

	public void setRegexConfigurations( RegexWholeFileModel regexWholeContainer )
	{
		getPrototypeManager().initialize( regexWholeContainer );
	}
}
