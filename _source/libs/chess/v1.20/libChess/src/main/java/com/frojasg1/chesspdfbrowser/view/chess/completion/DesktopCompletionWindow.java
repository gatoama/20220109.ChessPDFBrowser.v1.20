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

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.completion.base.CompletionDocumentFormatterBase;
import com.frojasg1.general.desktop.completion.base.DesktopCompletionWindowBase;
import com.frojasg1.general.completion.PrototypeForCompletionBase;
import com.frojasg1.general.desktop.completion.base.CurrentParamDocumentFormatterBase;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopCompletionWindow extends DesktopCompletionWindowBase<PrototypeForCompletionBase>
{
	public DesktopCompletionWindow( JFrame frame,
									BaseApplicationConfigurationInterface applicationConfiguration,
									InternationalizedStringConf translatorOfType )
//									ApplicationContextImp appCtx ) {
	{
		super( frame, applicationConfiguration, translatorOfType );
	}

	public DesktopCompletionWindow( JDialog dialog,
									BaseApplicationConfigurationInterface applicationConfiguration,
									InternationalizedStringConf translatorOfType )
//									ApplicationContextImp appCtx ) {
	{
		super( dialog, applicationConfiguration, translatorOfType );
	}

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	protected CompletionDocumentFormatterBase<PrototypeForCompletionBase> createCompletionDocumentFormatter( JTextPane textPane )
	{
		return( new CompletionDocumentFormatter( textPane, getAppliConf(),
													_translatorOfType ) );//, getApplicationContext().getBigMathHelp() ) );
	}

	@Override
	protected CurrentParamDocumentFormatterBase createCurrentParamDocumentFormatter(JTextPane textPane) {
		return( null );
	}
}
