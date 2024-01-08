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
import com.frojasg1.chesspdfbrowser.model.regex.RegexOfBlockModel;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.items.ListOfRegexWholeFiles;
import com.frojasg1.chesspdfbrowser.view.chess.completion.WholeCompletionManager;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.chesspdfbrowser.view.chess.regex.RegexConfJDialog;
import com.frojasg1.chesspdfbrowser.view.chess.regex.profile.TagsProfileConfJDialog;
import com.frojasg1.chesspdfbrowser.view.chess.regex.impl.BlockRegexConfJDialog;
import com.frojasg1.chesspdfbrowser.view.chess.regex.managers.BlockComboManager;
import com.frojasg1.chesspdfbrowser.view.chess.regex.managers.ProfileComboManager;
import com.frojasg1.chesspdfbrowser.view.chess.regex.xmlfilevalidation.XmlFileNameValidationJDialog;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import com.frojasg1.general.combohistory.impl.TextComboBoxHistory;
import com.frojasg1.general.desktop.view.combobox.AddRemoveModifyItemNewSelectionController;
import com.frojasg1.general.desktop.view.combobox.AddRemoveModifyItemResult;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedChildComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentForChildComboContentServer;
import com.frojasg1.general.desktop.view.combobox.chained.ComboBoxGroupManager;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.view.ViewComponent;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexComboControllerBase
	implements AddRemoveModifyItemNewSelectionController,
				ChainedParentForChildComboContentServer, InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "RegexComboControllerBase.properties";
	protected static final String CONF_ARE_YOU_SURE_YOU_WANT_TO_ERASE_THAT_FILE = "ARE_YOU_SURE_YOU_WANT_TO_ERASE_THAT_FILE";
	protected static final String CONF_YOU_HAVE_TO_CHOOSE_ONE_FILE = "YOU_HAVE_TO_CHOOSE_ONE_FILE";
	protected static final String CONF_THERE_ARE_NO_FILES_CREATE_ONE = "THERE_ARE_NO_FILES_CREATE_ONE";

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	protected ViewComponent _parentWindow = null;

	protected ListOfRegexWholeFiles _listOfRegexFiles = null;

//	protected RegexWholeFileModel _regexWholeConf = null;

	protected ChainedParentComboBoxGroupManager _filesCbgMan = null;
	protected ChainedChildComboBoxGroupManager _blockCbgMan = null;
	protected ChainedChildComboBoxGroupManager _profileCbgMan = null;
//	protected ComboBoxGroupManager _tagCbgMan = null;

	protected WholeCompletionManager _wholeCompletionManager = null;

	protected boolean _alreadyMapped = false;

	public RegexComboControllerBase()
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );

		registerInternationalizedStrings();
	}

	public void init( //RegexWholeFileModel regexWholeConf,
						ListOfRegexWholeFiles listOfRegexFiles,
						ChainedParentComboBoxGroupManager filesCbgMan,
						ChainedChildComboBoxGroupManager blockCbgMan,
						ChainedChildComboBoxGroupManager profileCbgMan,
//						ComboBoxGroupManager tagCbgMan,
						ViewComponent parentWindow,
							WholeCompletionManager wholeCompletionManager )
	{
		_parentWindow = parentWindow;
		_wholeCompletionManager = wholeCompletionManager;

//		_regexWholeConf = regexWholeConf;
		_listOfRegexFiles = listOfRegexFiles;

		_filesCbgMan = filesCbgMan;
		_blockCbgMan = blockCbgMan;
		_profileCbgMan = profileCbgMan;

//		_tagCbgMan = tagCbgMan;

		setThisAsController( _blockCbgMan );
		setThisAsController( _profileCbgMan );
		setThisAsController( _filesCbgMan );
	}
/*
	public void revert()
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> _regexWholeConf.init( _regexWholeConf.getAppliConf() ) );

		_blockCbgMan.setComboBoxContent( _regexWholeConf.getContentForBlocks() );
	}
*/
	protected void setThisAsController( ComboBoxGroupManager cbgm )
	{
		if( cbgm != null )
			cbgm.setController( this );
	}

	public ListOfRegexWholeFiles getListOfRegexWholeFiles()
	{
		return( _listOfRegexFiles );
	}

	public String getSelectedFileName()
	{
		return( _filesCbgMan.getSelectedItem() );
	}

	public RegexWholeFileModel getRegexWholeFileModel()
	{
		return( getRegexWholeFileModel( getSelectedFileName() ) );
	}

	protected String getRegex( RegexOfBlockModel rbm )
	{
		String result = null;
		if( rbm != null )
			result = rbm.getExpression();

		return( result );
	}

	protected RegexConfJDialog createBlockRegexDialog( String blockName, String regex,
		Consumer<InternationalizationInitializationEndCallback> callbackForWindow )
	{
		RegexConfJDialog result = null;

		boolean modal = true;
		if( ( _parentWindow == null ) || ( _parentWindow instanceof JFrame ) )
		{
			result = new BlockRegexConfJDialog( (JFrame) _parentWindow, modal,
												callbackForWindow,
												_wholeCompletionManager );
		}
		else if( _parentWindow instanceof JDialog )
		{
			result = new BlockRegexConfJDialog( (JDialog) _parentWindow, modal,
												callbackForWindow,
												_wholeCompletionManager );
		}

		String initialBlockToReplaceWith = null;
//		result.setInternationalizationEndCallBack(callbackForWindow);
		result.init( blockName, regex, getRegexWholeFileModel(),
					initialBlockToReplaceWith );

		return( result );
	}

	protected String getSelectedProfile()
	{
		return( _profileCbgMan.getSelectedItem() );
	}

	protected String[] getAvailableTags()
	{
		String[] result = null;
/*
		ProfileModel tagRegexConfCont = getSelectedProfileConfCont();
		TextComboBoxContent content = null;
		if( tagRegexConfCont != null )
			content = tagRegexConfCont.getComboBoxContent();

		if( content instanceof TagRegexTextComboBoxHistory )
		{
			List<String> list = ( (TagRegexTextComboBoxHistory) content ).getWholeSortedListOfElementsForCombo();
			list = CollectionFunctions.instance().removeItemsThatExistInTheSecondList(list, content.getListOfItems());
			result = list.toArray( new String[list.size()] );
		}
*/
		return( result );
	}

	protected XmlFileNameValidationJDialog createXmlFileNameValidationJDialog( String singleFileName,
		Consumer<InternationalizationInitializationEndCallback> callbackForWindow )
	{
		XmlFileNameValidationJDialog result = null;

		boolean modal = true;
		if( ( _parentWindow == null ) || ( _parentWindow instanceof JFrame ) )
		{
			result = new XmlFileNameValidationJDialog( (JFrame) _parentWindow, modal );
		}
		else if( _parentWindow instanceof JDialog )
		{
			result = new XmlFileNameValidationJDialog( (JDialog) _parentWindow, modal );
		}

		result.setInternationalizationEndCallBack(callbackForWindow);
		result.init( singleFileName, _listOfRegexFiles );

		return( result );
	}

	protected TagsProfileConfJDialog createTagsProfileConfJDialog( String profileName, ProfileModel profConf,
		Consumer<InternationalizationInitializationEndCallback> callbackForWindow )
	{
		TagsProfileConfJDialog result = null;

		if( profConf != null )
		{
			boolean modal = true;
			if( (_parentWindow == null) || ( _parentWindow instanceof JFrame ) )
			{
				result = new TagsProfileConfJDialog( (JFrame) _parentWindow, modal,
												_wholeCompletionManager );
			}
			else if( _parentWindow instanceof JDialog )
			{
				result = new TagsProfileConfJDialog( (JDialog) _parentWindow, modal,
												_wholeCompletionManager );
			}

			result.setInternationalizationEndCallBack(callbackForWindow);
			result.init( profileName, profConf.getParent().getBlockRegexBuilder(), profConf );
		}

		return( result );
	}
/*
	protected TagRegexConfJDialog createTagRegexDialog( String tagName, String regex,
														Integer offset, String[] availableElements )
	{
		TagRegexConfJDialog result = null;

		if( availableElements != null )
		{
			if( availableElements.length == 0 )
				throw( new RuntimeException( "Already all tags are defined. You cannot add more tags." ) );

			boolean modal = true;
			if( _parentWindow instanceof JFrame )
			{
				result = new TagRegexConfJDialog( (JFrame) _parentWindow, modal, availableElements );
			}
			else if( _parentWindow instanceof JDialog )
			{
				result = new TagRegexConfJDialog( (JDialog) _parentWindow, modal, availableElements );
			}
			else
			{
				result = new TagRegexConfJDialog( (JFrame) null, modal, availableElements );
			}

			result.init( tagName, regex, offset, _regexWholeConf.getBlockRegexBuilder(),
						_regexWholeConf.getBlockConfigurationContainer() );
		}

		return( result );
	}

	protected RegexOfTagModel_old getRegexModelOfTagOfSelectedProfile( String tagName )
	{
		RegexOfTagModel_old result = null;

		RegexOfBlockModel resultParent = null;

		ProfileModel profileConfCont = getSelectedProfileConfCont();

//		if( profileConfCont != null )
//			resultParent = profileConfCont.get(tagName);

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

	protected Integer getTagOffset( RegexOfBlockModel robm )
	{
		Integer result = null;
		if( robm instanceof RegexOfTagModel_old )
		{
			result = ( (RegexOfTagModel_old) robm).getOffset();
		}

		return( result );
	}
*/
	protected void createProfile( String profileNameToStartWith,
		Consumer<String> callback )
	{
		String result = null;

		RegexWholeFileModel rwfm = getRegexWholeFileModel();
		if( rwfm != null )
		{
			ProfileModel profConf = rwfm.getProfile( profileNameToStartWith );

			ProfileModel newProfConf = rwfm.createProfile( result, profConf );

			TagsProfileConfJDialog dial = createTagsProfileConfJDialog( result,
				newProfConf, (iiec) -> processCreateProfile( iiec, rwfm, callback ) );
		}
		else
			invokeCallback( callback, result );
	}

	protected void processCreateProfile( InternationalizationInitializationEndCallback iiec,
		RegexWholeFileModel rwfm, Consumer<String> callback )
	{
		String result = null;

		TagsProfileConfJDialog dial = (TagsProfileConfJDialog) iiec;

		if( dial != null )
		{
			dial.setVisibleWithLock(true);
			if( dial.wasSuccessful() )
			{
				ProfileModel resultProfile = dial.getRegexConfContainer();
				rwfm.addProfile( resultProfile );

				result = resultProfile.getProfileName();
			}
		}

		invokeCallback( callback, result );
	}
/*
	protected String createTag( String tagNameToStartWith )
	{
		String result = null;

		String initialRegex = null;
		Integer initialOffset = null;

		RegexOfTagModel_old rotm = getRegexModelOfTagOfSelectedProfile( tagNameToStartWith );
		if( rotm != null )
		{
			initialRegex = rotm.getExpression();
//			initialOffset = rotm.getOffset();
		}

		TagRegexConfJDialog dial = createTagRegexDialog( result, initialRegex, initialOffset, getAvailableTags() );

		dial.setVisibleWithLock(true);
		if( dial.wasSuccessful() )
		{
			result = dial.getRegexName();

			ProfileModel profileConfCont = getSelectedProfileConfCont();
			if( profileConfCont != null )
			{
				RegexOfTagModel_old rtm = profileConfCont.addBlockRegex(result, null);
				if( rtm != null )
				{
					rtm.setExpression( dial.getExpression() );
//					rtm.setOffset( dial.getOffset() );
				}
			}
		}

		return( result );
	}
*/
	public void modifyProfile( String profileName, boolean validateAtOnce,
		Consumer<String> callback)
	{
		String result = null;

		RegexWholeFileModel rwfm = getRegexWholeFileModel();
		if( rwfm != null )
		{
			ProfileModel profConf = rwfm.getProfile( profileName );
			
			modifyProfile( profConf, validateAtOnce, callback );
		}
		else
			invokeCallback( callback, result );
	}

	protected void setNewSelectedFileName( String newFileName )
	{
		if( ! Objects.equals( _filesCbgMan.getSelectedItem(), newFileName ) )
			_filesCbgMan.getComboBoxContent().newItemSelected( newFileName );
	}

	public void modifyProfile( ProfileModel profConf, boolean validateAtOnce,
		Consumer<String> callback )
	{
		String result = null;

		if( profConf != null )
		{
			setNewSelectedFileName( profConf.getParent().getFileName() );

			String profileName = profConf.getProfileName();
			TagsProfileConfJDialog dial = createTagsProfileConfJDialog( profileName, profConf,
				(iiec) -> processModifyProfile(iiec, profileName, callback) );

			if( dial != null )
				dial.setValidateAtOnce( validateAtOnce );
		}
		else
			invokeCallback( callback, result );
	}

	public void processModifyProfile( InternationalizationInitializationEndCallback iiec,
									String profileName, Consumer<String> callback )
	{
		String result = null;

		TagsProfileConfJDialog dial = (TagsProfileConfJDialog) iiec;

		dial.setVisibleWithLock(true);
		if( dial.wasSuccessful() )
		{
			ProfileModel resultProfile = dial.getRegexConfContainer();

			result = profileName;
		}

		invokeCallback( callback, result );
	}

	public String removeFile( String singleFileName )
	{
		String result = null;
		int answer = HighLevelDialogs.instance().yesNoCancelDialog(_parentWindow,
			getInternationalString(CONF_ARE_YOU_SURE_YOU_WANT_TO_ERASE_THAT_FILE), null, HighLevelDialogs.NO );

		if( answer == HighLevelDialogs.YES )
		{
			_listOfRegexFiles.remove( singleFileName );
			result = singleFileName;
		}

		return( result );
	}

	public String createNewFile( Consumer<String> callback )
	{
		return( newFileWindowOperation( null, (fn) -> {
					_listOfRegexFiles.createAndAddEmptyFileModel( fn );
					invokeCallback(callback, fn);
			}) );
	}

	protected void renameFileName( String singleFileName, String newFileName )
	{
		_listOfRegexFiles.rename( singleFileName, newFileName );
		_filesCbgMan.getComboBoxContent().removeItem( singleFileName );
		_filesCbgMan.getComboBoxContent().newItemSelected( newFileName );
	}

	public void modifyFileName( String singleFileName, Consumer<String> callback )
	{

		if( ( singleFileName == null ) && checkIfFileSelection() )
			singleFileName = getSelectedFileName();

		String singleFileNameFinal = singleFileName;
		if( singleFileName != null )
			newFileWindowOperation( singleFileNameFinal,
				(fn) -> { renameFileName( singleFileNameFinal, fn );
						invokeCallback(callback, fn);
				});
	}

	public String newFileWindowOperation( String singleFileName, Consumer<String> operationOnSuccess )
	{
		String result = null;

		XmlFileNameValidationJDialog dial = createXmlFileNameValidationJDialog( singleFileName,
			(iiec) -> processNewFileWindowOperation( iiec, operationOnSuccess ) );

		return( result );
	}

	public String processNewFileWindowOperation( InternationalizationInitializationEndCallback iiec,
		Consumer<String> operationOnSuccess )
	{
		String result = null;
		XmlFileNameValidationJDialog dial = (XmlFileNameValidationJDialog)iiec;

		if( dial != null )
		{
			dial.setVisibleWithLock(true);
			if( dial.wasSuccessful() )
			{
				result = dial.getResultFileName();

				operationOnSuccess.accept( result );
			}
		}

		return( result );
	}

/*
	protected String modifyTag( String tagName )
	{
		String result = null;

		String initialRegex = null;
		Integer initialOffset = null;
		
		RegexOfTagModel_old rotm = getRegexModelOfTagOfSelectedProfile( tagName );
		if( rotm != null )
		{
			initialRegex = rotm.getExpression();
//			initialOffset = rotm.getOffset();
		}

		TagRegexConfJDialog dial = createTagRegexDialog( result, initialRegex, initialOffset, new String[]{ tagName } );

		dial.setVisibleWithLock(true);
		if( dial.wasSuccessful() )
		{
			result = dial.getRegexName();

//			RegexOfBlockModel rbm = _regexWholeConf.getBlockConfigurationContainer().get(result);
			ProfileModel profileConfCont = getSelectedProfileConfCont();
			if( profileConfCont != null )
			{
				RegexOfTagModel_old rtm = profileConfCont.get(result);
				if( rtm != null )
				{
					rtm.setExpression( dial.getExpression() );
//					rtm.setOffset( dial.getOffset() );
				}
			}
		}

		return( result );
	}
*/
	protected String createBlock( String blockNameToStartWith, Consumer<String> callback )
	{
		String result = null;

		RegexWholeFileModel rwfm = getRegexWholeFileModel();
		if( rwfm != null )
		{
			String initialRegex = getRegex( rwfm.getBlockRegexConf(blockNameToStartWith) );
			RegexConfJDialog dial = createBlockRegexDialog( result, initialRegex,
							(iiec) -> processCreateBlock( iiec, rwfm, callback ) );
		}
		else
			invokeCallback( callback, result );

		return( result );
	}

	protected String processCreateBlock( InternationalizationInitializationEndCallback iiec,
										RegexWholeFileModel rwfm,
										Consumer<String> callback )
	{
		String result = null;

		RegexConfJDialog dial = (RegexConfJDialog) iiec;

		dial.setVisibleWithLock(true);
		if( dial.wasSuccessful() )
		{
			result = dial.getRegexName();

			RegexOfBlockModel rbm = rwfm.getBlockConfigurationContainer().addBlockRegex(result, null);
			if( rbm != null )
				rbm.setExpression( dial.getExpression() );
		}

		invokeCallback( callback, result );

		return( result );
	}

	public void modifyBlock( String blockName, boolean validateAtOnce,
								Consumer<String> callback)
	{
		modifyBlock( getSelectedFileName(), blockName, validateAtOnce, callback );
	}

	public void modifyBlock( String fileName, String blockName, boolean validateAtOnce,
								Consumer<String> callback)
	{
		String result = null;

		RegexWholeFileModel rwfm = getRegexWholeFileModel( fileName );
		if( rwfm != null )
		{
			String initialRegex = getRegex( rwfm.getBlockRegexConf(blockName) );
			RegexConfJDialog dial = createBlockRegexDialog( blockName, initialRegex,
				(iiec) -> processModifyBlock( iiec, rwfm, callback ) );

			dial.setValidateAtOnce(validateAtOnce);
		}
		else
			invokeCallback( callback, result );
	}

	public void processModifyBlock( InternationalizationInitializationEndCallback iiec,
									RegexWholeFileModel rwfm,
									Consumer<String> callback)
	{
		String result = null;

		RegexConfJDialog dial = (RegexConfJDialog) iiec;

		dial.setVisibleWithLock(true);
		if( dial.wasSuccessful() )
		{
			result = dial.getRegexName();

			RegexOfBlockModel rbm = rwfm.getBlockConfigurationContainer().get(result);
			if( rbm != null )
				rbm.setExpression( dial.getExpression() );
		}

		invokeCallback( callback, result );
	}

	protected ProfileModel getSelectedProfileConfCont()
	{
		ProfileModel result = null;
		RegexWholeFileModel rwfm = getRegexWholeFileModel();
		if( rwfm != null )
			result = rwfm.getProfile( getSelectedProfile() );

		return( result );
	}

	protected String removeTag( String tagName )
	{
		String result = null;
//		_regexWholeConf.removeProfile(blockName);
		ProfileModel profileConfCont = getSelectedProfileConfCont();
		if( profileConfCont != null )
		{
//			if( profileConfCont.remove(tagName) )
//				result = tagName;
		}

		return( result );
	}

	protected String removeProfile( String profileName )
	{
		String result = null;
//		_regexWholeConf.removeProfile(blockName);
		RegexWholeFileModel rwfm = getRegexWholeFileModel();
		ProfileModel profileConfCont = rwfm.getProfile(profileName);
		if( profileConfCont != null )
		{
			rwfm.removeProfile(profileName);
			result = profileName;
		}

		return( result );
	}

	protected String removeBlock( String blockName )
	{
		String result = null;
//		_regexWholeConf.removeProfile(blockName);
		RegexWholeFileModel rwfm = getRegexWholeFileModel();
		if( ( rwfm != null ) && rwfm.getBlockConfigurationContainer().remove(blockName) )
			result = blockName;

		return( blockName );
	}

	@Override
	public void added(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData,
		Consumer<String> callback)
	{
		if( sender == _blockCbgMan )
		{
			if( checkIfFileSelection() )
				createBlock( eventData.getItem(), callback );
		}
		else if( sender == _profileCbgMan )
		{
			if( checkIfFileSelection() )
				createProfile( eventData.getItem(), callback );
		}
		else if( sender == _filesCbgMan )
			createNewFile(callback);
	}

	protected void afterAdding( String item, Consumer<String> callback )
	{
		if( item != null )
			updateCombos();

		invokeCallback( callback, item );
	}

	protected void invokeCallback( Consumer<String> callback, String item )
	{
		if( callback != null )
			callback.accept(item);
	}

	@Override
	public void removed(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData,
		Consumer<String> callback )
	{
		String result = null;
		if( sender == _blockCbgMan )
			result = removeBlock( eventData.getItem() );
		else if( sender == _profileCbgMan )
			result = removeProfile( eventData.getItem() );
		else if( sender == _filesCbgMan )
			result = removeFile( eventData.getItem() );

		if( result != null )
			updateCombos();

		invokeCallback(callback, result);
	}

	protected void updateCombos(String item)
	{
		if( item != null )
			updateCombos();
	}

	protected boolean isThereASelectedFile()
	{
		return( getSelectedFileName() != null );
	}

	protected boolean thereAreXmlFiles()
	{
		return( ! _listOfRegexFiles.getComboBoxContent().getListOfItems().isEmpty() );
	}

	protected boolean checkIfFileSelection()
	{
		boolean result = isThereASelectedFile();

		if( ! result )
		{
			if( thereAreXmlFiles() )
				HighLevelDialogs.instance().errorMessageDialog( _parentWindow,
					getInternationalString(CONF_YOU_HAVE_TO_CHOOSE_ONE_FILE) );
			else
				HighLevelDialogs.instance().errorMessageDialog( _parentWindow,
					getInternationalString(CONF_THERE_ARE_NO_FILES_CREATE_ONE) );
		}

		return( result );
	}

	@Override
	public void modify(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData)
	{
		boolean validateAtOnce = false;
		String result = null;
		if( sender == _blockCbgMan )
		{
			if( checkIfFileSelection() )
				modifyBlock( eventData.getItem(), validateAtOnce,
					(item) -> { if( item != null ) updateCombos(); } );
		}
//		else if( sender == _tagCbgMan )
//			result = modifyTag( eventData.getItem() );
		else if( sender == _profileCbgMan )
		{
			if( checkIfFileSelection() )
				modifyProfile( eventData.getItem(), validateAtOnce,
					(item) -> { if( item != null ) updateCombos(); } );
		}
		else if( sender == _filesCbgMan )
			modifyFileName( eventData.getItem(),
					(item) -> { if( item != null ) updateCombos(); } );
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
		SwingUtilities.invokeLater( () -> {
			_filesCbgMan.setComboBoxContent( _listOfRegexFiles.getComboBoxContent() );
			updateCombos( _blockCbgMan );
			updateCombos( _profileCbgMan );
			updateCombos( _filesCbgMan );
		});
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper) {
		_blockCbgMan.setComponentMapper(mapper);
		_profileCbgMan.setComponentMapper(mapper);
		_filesCbgMan.setComponentMapper(mapper);

		_alreadyMapped = true;
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		return( _alreadyMapped );
	}

	public ChainedParentComboBoxGroupManager getFilesComboBoxGroupManager()
	{
		return( _filesCbgMan );
	}

	public ChainedChildComboBoxGroupManager getBlockComboBoxGroupManager()
	{
		return( _blockCbgMan );
	}

	public ChainedChildComboBoxGroupManager getProfileComboBoxGroupManager()
	{
		return( _profileCbgMan );
	}

	protected RegexWholeFileModel getRegexWholeFileModel( String fileName )
	{
		RegexWholeFileModel result = null;
		if( fileName != null )
			result = _listOfRegexFiles.get(fileName);

		return( result );
		
	}

	protected TextComboBoxContent getContentForCombosOfBlock( String fileName )
	{
		TextComboBoxContent result = null;
		RegexWholeFileModel rwfm = getRegexWholeFileModel(fileName);
		if( rwfm != null )
			result = rwfm.getContentForBlocks();

		return( result );
	}

	protected TextComboBoxContent getContentForCombosOfProfile( String fileName )
	{
		TextComboBoxContent result = null;
		RegexWholeFileModel rwfm = getRegexWholeFileModel(fileName);
		if( rwfm != null )
			result = rwfm.getComboBoxContentForProfiles();

		return( result );
	}

	protected TextComboBoxContent getEmptyTextComboBox()
	{
		TextComboBoxHistory result = new TextComboBoxHistory();
		result.init( (List<String>) null );

		return( result );
	}

	@Override
	public TextComboBoxContent getContentForChildCombos(String key, List<String> chainedSelectedItem) {
		TextComboBoxContent result = null;
		if( ( chainedSelectedItem != null ) && ! chainedSelectedItem.isEmpty() )
		{
			String singleXmlFileName = chainedSelectedItem.get(0);
			switch( key )
			{
				case ProfileComboManager.CHILD_KEY_FOR_PARENT:
					result = getContentForCombosOfProfile( singleXmlFileName );
				break;
				case BlockComboManager.CHILD_KEY_FOR_PARENT:
					result = getContentForCombosOfBlock( singleXmlFileName );
				break;
			}
		}

		if( result == null )
			result = getEmptyTextComboBox();

		return( result );
	}

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_ARE_YOU_SURE_YOU_WANT_TO_ERASE_THAT_FILE, "Are you sure you want to erase that file" );
		this.registerInternationalString(CONF_YOU_HAVE_TO_CHOOSE_ONE_FILE, "You have to choose a file before adding elements to it" );
		this.registerInternationalString(CONF_THERE_ARE_NO_FILES_CREATE_ONE, "There are no regex xml files. Create one before adding elements to it." );
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
