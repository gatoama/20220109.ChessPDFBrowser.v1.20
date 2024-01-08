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
package com.frojasg1.general.desktop.completion.base;

import com.frojasg1.general.completion.PrototypeManagerBase;
import com.frojasg1.applications.common.configuration.InternationalStringsConfiguration;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.completion.InputTextComponentListener;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class WholeCompletionManagerBase
{
	protected InputTextComponentListener _inputTextListener = null;

	protected PrototypeManagerBase _prototypeManager = null;

	protected DesktopCompletionWindowBase _completionWindow = null;

	protected InputTextCompletionManagerBase< Rectangle > _inputTextCompletionManager = null;

	protected BaseApplicationConfigurationInterface _appliConf = null;

	protected InternationalizedStringConf _translatorOfType = null;

	public void init( BaseApplicationConfigurationInterface appliConf )
	{
		_appliConf = appliConf;

		_prototypeManager = createPrototypeManager();
//		_completionWindow = createCompletionWindow();
		_inputTextListener = createInputTextComponentListener();
//		_inputTextCompletionManager = createAndInitDesktopInputTextCompletionManager();

//		_inputTextCompletionManager.setPrototypeManager(_prototypeManager);

		_translatorOfType = createTranslatorOfType();

//		_inputTextListener.setCompletionManager(_inputTextCompletionManager);
	}

	protected InputTextCompletionManagerBase< Rectangle > getInputTextCompletionManagerBase()
	{
		return( _inputTextCompletionManager );
	}

	protected PrototypeManagerBase getPrototypeManager()
	{
		return( _prototypeManager );
	}

	protected abstract InputTextCompletionManagerBase< Rectangle > createDesktopInputTextCompletionManager();

	protected InputTextCompletionManagerBase< Rectangle > createAndInitDesktopInputTextCompletionManager()
	{
		InputTextCompletionManagerBase< Rectangle > result = createDesktopInputTextCompletionManager();

		result.setPrototypeManager(_prototypeManager);
		_inputTextListener.setCompletionManager(result);

		return( result );
	}

	protected InputTextComponentListener createInputTextComponentListener()
	{
		return( new InputTextComponentListener() );
	}

	protected abstract InternationalStringsConfiguration createTranslatorOfType();
/*
	{
		return( new TranslatorOfTypeForCompletion( getAppliConf() ) );
	}
*/
	protected abstract DesktopCompletionWindowBase createCompletionWindow( Component compForNotHiding );
/*
	{
		DesktopCompletionWindowBase result = null;

		Component mainWindow = ComponentFunctions.instance().getAncestor( compForNotHiding );
		if( mainWindow instanceof JFrame )
			result = new DesktopCompletionWindowBase( (JFrame) mainWindow, getAppliConf(),
													_translatorOfType );
		else
		{
			JDialog dialog = null;
			if( mainWindow instanceof JDialog )
				dialog = (JDialog) mainWindow;

			result = new DesktopCompletionWindowBase( dialog, getAppliConf(),
													_translatorOfType );
		}

		result.setComponentForNotHiding( compForNotHiding );

		return( result );
	}
*/
	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	protected abstract PrototypeManagerBase createPrototypeManager();

	public void setInputTextCompletionManager( JTextComponent textcomp,
													Component compForNotHiding )
	{
/*		if( _regexWholeContainer != regexWholeContainer )
			setRegexConfigurations( regexWholeContainer );

		_regexWholeContainer = regexWholeContainer;
*/
		if( _completionWindow != null )
			_completionWindow.formWindowClosing(true);

		_inputTextCompletionManager = createAndInitDesktopInputTextCompletionManager();

		_completionWindow = createCompletionWindow( compForNotHiding );
		_completionWindow.setCompletionManager(_inputTextCompletionManager);
		_inputTextCompletionManager.setCompletionWindow(_completionWindow);

		_inputTextListener.setNewJTextComponent(textcomp);
		_inputTextCompletionManager.setInputTextComponent(_inputTextListener.getViewTextComponent());

	}

	public JTextComponent getTextComponent()
	{
		JTextComponent result = null;
		if( ( _inputTextListener != null ) && ( _inputTextListener.getViewTextComponent() != null ) )
			result = _inputTextListener.getViewTextComponent().getComponent();

		return( result );
	}

	public DesktopCompletionWindowBase getCompletionWindow()
	{
		return( _completionWindow );
	}
}
