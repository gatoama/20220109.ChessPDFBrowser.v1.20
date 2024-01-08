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
package com.frojasg1.chesspdfbrowser.view.chess.regex.impl;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.chesspdfbrowser.view.chess.completion.WholeCompletionManager;
import com.frojasg1.chesspdfbrowser.view.chess.regex.RegexConfJDialog;
import com.frojasg1.chesspdfbrowser.view.chess.regex.RegexNameView;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BlockRegexConfJDialog extends RegexConfJDialog
{
	protected final static String a_configurationBaseFileName = "BlockRegexConfJDialog";

	public BlockRegexConfJDialog(JFrame parent, boolean modal,
							Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
							WholeCompletionManager wholeCompletionManager )
	{
		super( parent, modal, initializationEndCallBack, wholeCompletionManager );
	}

	public BlockRegexConfJDialog(JDialog parent, boolean modal,
							Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
							WholeCompletionManager wholeCompletionManager )
	{
		super( parent, modal, initializationEndCallBack, wholeCompletionManager );
	}

	@Override
	protected RegexNameView createRegexNameJPanel()
	{
		return( new BlockRegexOrProfileNameJPanel( this.getRegexConfContainer(),
													getInitialRegexName(),
													null ) );
	}

	@Override
	protected String getConfigurationBaseFileName()
	{
		return( a_configurationBaseFileName );
	}
}
