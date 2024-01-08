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
package com.frojasg1.chesspdfbrowser.view.chess.regex.controller;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.model.regex.LineModel;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.model.regex.TagReplacementModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.view.chess.completion.WholeCompletionManager;
import com.frojasg1.chesspdfbrowser.view.chess.regex.profile.LineOfTagsJPanel;
import com.frojasg1.chesspdfbrowser.view.chess.regex.impl.TagRegexConfJDialog;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.combobox.AddRemoveModifyItemNewSelectionController;
import com.frojasg1.general.desktop.view.combobox.AddRemoveModifyItemResult;
import com.frojasg1.general.desktop.view.combobox.chained.ComboBoxGroupManager;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexComboControllerForLineOfProfile implements AddRemoveModifyItemNewSelectionController,
															InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "RegexComboControllerForLineOfProfile.properties";
	protected static final String CONF_ALREADY_ALL_TAGS_DEFINED = "ALREADY_ALL_TAGS_DEFINED";

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

//	protected Component _parentWindow = null;

	LineOfTagsJPanel _lineOfTagsJPanel = null;
//	protected ProfileModel _tagRegexConfCont = null;

	protected ComboBoxGroupManager _tagCbgMan = null;

	protected int _index = -1;

	protected WholeCompletionManager _wholeCompletionManager = null;

	protected boolean _alreadyMapped = false;

	public RegexComboControllerForLineOfProfile()
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );

		registerInternationalizedStrings();
	}

	public void init( LineOfTagsJPanel lineOfTagsJPanel,
						ProfileModel tagRegexConfCont,
						ComboBoxGroupManager tagCbgMan,
							WholeCompletionManager wholeCompletionManager )
//						Component parentWindow )
	{
//		_parentWindow = parentWindow;
		_lineOfTagsJPanel = lineOfTagsJPanel;
		_tagCbgMan = tagCbgMan;
		_wholeCompletionManager = wholeCompletionManager;

		setThisAsController( _tagCbgMan );
//		super.init( null, null, null, tagCbgMan, parentWindow );
	}

	protected void setThisAsController( ComboBoxGroupManager cbgm )
	{
		if( cbgm != null )
			cbgm.setController( this );
	}

/*
	@Override
	public void init( RegexWholeFileModel regexWholeConf,
						ComboBoxGroupManager blockCbgMan,
						ComboBoxGroupManager profileCbgMan,
						ComboBoxGroupManager tagCbgMan,
						Component parentWindow )
	{
		throw( new RuntimeException( "This initializer cannot be used. Use the overloaded one instead." ) );
	}

	@Override
	public void revert()
	{
		_tagRegexConfCont.revert();
	}

	@Override
	protected String getSelectedProfile()
	{
		return( null );
	}

	protected ProfileModel getSelectedProfileConfCont()
	{
		return( _tagRegexConfCont );
	}

	public void setIndex( int index )
	{
		_index = index;
	}
*/
	public int getIndex()
	{
		return( _index );
	}

	protected String[] getAvailableTags()
	{
		String[] result = null;
		List<String> list = new ArrayList<>();

		String[] mandatoryTagList = ChessGameHeaderInfo.getTAGgroup( ChessGameHeaderInfo.TAGS_TO_EXTRACT_FROM_PDF_GAMES );
		for( String tag: mandatoryTagList )
			if( !_lineOfTagsJPanel.getLineModel().contains(tag) )
				list.add( tag );

		result = list.toArray( new String[list.size()] );

		return( result );
	}

	protected TagRegexConfJDialog createTagRegexDialog( String tagName, String regex,
														String[] availableElements,
														String blockToReplaceWith,
		Consumer<InternationalizationInitializationEndCallback> callbackForWindow )
	{
		TagRegexConfJDialog result = null;

		if( availableElements != null )
		{
			if( availableElements.length == 0 )
				throw( new RuntimeException( getInternationalString(CONF_ALREADY_ALL_TAGS_DEFINED) ) );

			boolean modal = true;
			if( ( getParentWindow() == null ) ||
				( getParentWindow() instanceof JFrame ) )
			{
				result = new TagRegexConfJDialog( (JFrame) getParentWindow(), modal,
												callbackForWindow,
												_wholeCompletionManager,
												availableElements );
			}
			else if( getParentWindow() instanceof JDialog )
			{
				result = new TagRegexConfJDialog( (JDialog) getParentWindow(), modal,
												callbackForWindow,
												_wholeCompletionManager,
												availableElements );
			}

//			result.setInternationalizationEndCallBack(callbackForWindow);
			result.init( tagName, regex, getRegexWholeContainer(), blockToReplaceWith );

			result.setTitle( _lineOfTagsJPanel.calculateTitle() );
		}

		return( result );
	}

	public LineModel getLineOfTagRegexes()
	{
		return( _lineOfTagsJPanel.getLineModel() );
	}

	protected RegexWholeFileModel getRegexWholeContainer()
	{
		return( getLineOfTagRegexes().getParent().getParent() );
	}
/*
	protected RegexOfTagModel_old getRegexModelOfTagOfSelectedProfile( String tagName )
	{
		RegexOfTagModel_old result = null;

		RegexOfBlockModel resultParent = null;

		ProfileModel profileConfCont = getSelectedProfileConfCont();
		
////		if( profileConfCont != null )
////			resultParent = profileConfCont.get(tagName);

		if( resultParent instanceof RegexOfTagModel_old )
			result = (RegexOfTagModel_old) resultParent;

		return( result );
	}

	protected String getRegexOfTagOfSelectedProfile( String tagName )
	{
		String result = null;

		RegexOfTagModel_old robm = getRegexModelOfTagOfSelectedProfile( tagName );

		if( robm != null )
			result = getRegex( robm );

		return( result );
	}
*/
	protected void createTag( String tagNameToStartWith, Consumer<String> callback )
	{
		String result = null;

		String blockToReplaceWith = null;

		String initialRegex = _lineOfTagsJPanel.getExpression();

		TagRegexConfJDialog dial = createTagRegexDialog( result, initialRegex,
														getAvailableTags(),
														blockToReplaceWith,
														(iiec) -> processCreateTag(iiec, callback) );
	}

	protected String processCreateTag( InternationalizationInitializationEndCallback iiec,
									Consumer<String> callback )
	{
		String result = null;

		TagRegexConfJDialog dial = (TagRegexConfJDialog) iiec;

		dial.setVisibleWithLock(true);
		if( dial.wasSuccessful() )
		{
			result = dial.getRegexName();

			TagReplacementModel trm = getLineOfTagRegexes().addTagRegexReplacement(result, null);
			if( trm != null )
			{
				_lineOfTagsJPanel.setExpression( dial.getExpression() );
				trm.setBlockToReplaceWith( dial.getBlockToReplaceWith() );
//				rtm.setOffset( dial.getOffset() );
			}
		}

		invokeCallback( callback, result );

		return( result );
	}

	protected TagReplacementModel getRegexModelOfTag( String tagName )
	{
		return( getLineOfTagRegexes().get( tagName ) );
	}

	protected void modifyTag( String tagName, Consumer<String> callback )
	{
		String result = null;

		String initialRegex = null;
//		Integer initialOffset = null;

		TagReplacementModel trm = getRegexModelOfTag( tagName );
		if( trm != null )
		{
			initialRegex = _lineOfTagsJPanel.getExpression();
//			initialRegex = getLineOfTagRegexes().getSynchronizationRegexModel().getExpression();
//			initialOffset = rotm.getOffset();
			String blockToReplaceWith = trm.getBlockToReplaceWith();

			TagRegexConfJDialog dial = createTagRegexDialog( initialRegex, initialRegex,
															new String[]{ tagName },
															blockToReplaceWith,
															(iiec) -> processModifyTag( iiec,
																trm, callback ) );
		}
		else
			invokeCallback( callback, result );
	}

	public void processModifyTag( InternationalizationInitializationEndCallback iiec,
									TagReplacementModel trm,
									Consumer<String> callback)
	{
		String result = null;
		TagRegexConfJDialog dial = (TagRegexConfJDialog) iiec;

		dial.setVisibleWithLock(true);
		if( dial.wasSuccessful() )
		{
			_lineOfTagsJPanel.setExpression( dial.getExpression() );

			result = dial.getRegexName();

			trm.setBlockToReplaceWith( dial.getBlockToReplaceWith() );
		}

		invokeCallback( callback, result );
	}

	protected String removeTag( String tagName )
	{
		String result = null;

		if( getLineOfTagRegexes().remove(tagName) )
			result = tagName;

		return( result );
	}

	protected void invokeCallback( Consumer<String> callback, String item )
	{
		if( callback != null )
			callback.accept(item);
	}

	@Override
	public void added(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData,
						Consumer<String> callback )
	{
		String result = null;

		if( sender == _tagCbgMan )
			createTag( eventData.getItem(),
				(item) -> {
						if( item != null )
							updateCombos();
						invokeCallback( callback, item );
				});
	}

	@Override
	public void removed(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData,
						Consumer<String> callback )
	{
		String result = null;

		if( sender == _tagCbgMan )
			result = removeTag( eventData.getItem() );

		if( result != null )
			updateCombos();

		invokeCallback( callback, result );
	}

	@Override
	public void modify(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData)
	{
		String result = null;

		if( sender == _tagCbgMan )
			modifyTag( eventData.getItem(),
				(item) -> {
					if( item != null )
						updateCombos();
				});
	}

	@Override
	public void comboBoxSelectionChanged(ComboBoxGroupManager sender, String previousSelectedItem, String newSelection) {
		System.out.println( "RegexComboControllerBase - comboBoxSelectionChanged" );
	}

	protected void updateCombos( ComboBoxGroupManager cbMan )
	{
		if( cbMan != null )
			cbMan.updateCombos();
	}

	public void updateCombos()
	{
		updateCombos( _tagCbgMan );
	}

	public void dispose()
	{
//		_parentWindow = null;

		_lineOfTagsJPanel = null;
		_tagCbgMan = null;
	}

	protected Component getParentWindow()
	{
		return( ComponentFunctions.instance().getAncestor(_lineOfTagsJPanel) );
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper) {
		_tagCbgMan.setComponentMapper(mapper);

		_alreadyMapped = true;
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		return( _alreadyMapped );
	}

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_ALREADY_ALL_TAGS_DEFINED, "Already all tags are defined. You cannot add more tags." );
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
