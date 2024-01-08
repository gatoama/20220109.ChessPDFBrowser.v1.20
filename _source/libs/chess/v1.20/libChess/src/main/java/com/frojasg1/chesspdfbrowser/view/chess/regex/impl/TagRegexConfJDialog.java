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
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.view.chess.completion.WholeCompletionManager;
import com.frojasg1.chesspdfbrowser.view.chess.regex.RegexConfJDialog;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TagRegexConfJDialog extends RegexConfJDialog
{
	protected final static String a_configurationBaseFileName = "TagRegexConfJDialog";

	protected String[] _availableTagElements = null;

	protected TagRegexNameJPanel _tagRegexNameJPanel = null;

	public TagRegexConfJDialog(JFrame parent, boolean modal,
							Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
							WholeCompletionManager wholeCompletionManager,
							String[] availableTagElements )
	{
		super( parent, modal, initializationEndCallBack, wholeCompletionManager );
		_availableTagElements = availableTagElements;
	}

	public TagRegexConfJDialog(JDialog parent, boolean modal,
							Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
							WholeCompletionManager wholeCompletionManager,
							String[] availableTagElements )
	{
		super( parent, modal, initializationEndCallBack, wholeCompletionManager );
		_availableTagElements = availableTagElements;
	}

	@Override
	public void init( String regexName, String initialRegex,
						RegexWholeFileModel regexWholeContainer,
						String blockToReplaceWith )
	{
		super.init( regexName, initialRegex, regexWholeContainer, blockToReplaceWith );
	}

	protected TagRegexNameJPanel createRegexNameJPanel()
	{
		_tagRegexNameJPanel = new TagRegexNameJPanel( _availableTagElements );
		_tagRegexNameJPanel.setRegexOrProfileName( getInitialRegexName() );

		return( _tagRegexNameJPanel );
	}

	protected String getConfigurationBaseFileName()
	{
		return( a_configurationBaseFileName );
	}

	@Override
	protected void validateFormChild() throws ValidationException
	{
		super.validateFormChild();

		_regexEditionPanel.validateBlockToExtractWith();
	}
}
