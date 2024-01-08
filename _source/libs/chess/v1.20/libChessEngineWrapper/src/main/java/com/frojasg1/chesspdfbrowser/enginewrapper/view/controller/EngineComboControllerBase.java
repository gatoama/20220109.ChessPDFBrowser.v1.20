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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.controller;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.items.ChessEngineConfigurationMap;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.EngineInstanceConfiguration_JDialog;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import com.frojasg1.general.combohistory.impl.TextComboBoxHistory;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.desktop.view.combobox.AddRemoveModifyItemNewSelectionController;
import com.frojasg1.general.desktop.view.combobox.AddRemoveModifyItemResult;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentForChildComboContentServer;
import com.frojasg1.general.desktop.view.combobox.chained.ComboBoxGroupManager;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.view.ViewComponent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class EngineComboControllerBase
	implements AddRemoveModifyItemNewSelectionController,
				ChainedParentForChildComboContentServer, InternationalizedStringConf
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	public static final String GLOBAL_CONF_FILE_NAME = "EngineComboControllerBase.properties";
	protected static final String CONF_ARE_YOU_SURE_YOU_WANT_TO_ERASE_THAT_ENGINE = "ARE_YOU_SURE_YOU_WANT_TO_ERASE_THAT_ENGINE";

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	protected BaseApplicationConfigurationInterface _appliConf = null;

	protected ViewComponent _parentWindow = null;

	protected ChessEngineConfigurationMap _chessEngineConfigurationMap = null;

//	protected RegexWholeFileModel _regexWholeConf = null;

	protected ChainedParentComboBoxGroupManager _enginesCbgMan = null;

	protected EngineMasterComboChangeListener _listener = null;

	protected boolean _alreadyMapped = false;

	public EngineComboControllerBase()
	{
	}

	public void init( BaseApplicationConfigurationInterface appliConf,
						ChessEngineConfigurationMap chessEngineConfigurationMap,
						ChainedParentComboBoxGroupManager enginesCbgMan,
						ViewComponent parentWindow )
	{
		_appliConf = appliConf;
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();

		_parentWindow = parentWindow;

		_chessEngineConfigurationMap = chessEngineConfigurationMap;

		_enginesCbgMan = enginesCbgMan;

		setThisAsController(_enginesCbgMan );
	}

	public void addListener( EngineMasterComboChangeListener listener )
	{
		_listener = listener;
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
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

	public ChessEngineConfigurationMap getChessEngineConfigurationMap()
	{
		return( _chessEngineConfigurationMap );
	}

	public String getSelectedEngineName()
	{
		return( _enginesCbgMan.getSelectedItem() );
	}

	public EngineInstanceConfiguration getEngineInstanceConfiguration()
	{
		return( getEngineInstanceConfiguration( getSelectedEngine() ) );
	}

	protected EngineInstanceConfiguration_JDialog createEngineInstanceConfiguration_JDialog( EngineInstanceConfiguration eiConf,
		Consumer<InternationalizationInitializationEndCallback> callbackWindow )
	{
		EngineInstanceConfiguration_JDialog result = null;

		boolean modal = true;
		if( (_parentWindow == null) || ( _parentWindow instanceof JFrame ) )
		{
			result = new EngineInstanceConfiguration_JDialog( (JFrame) _parentWindow, modal,
												getAppliConf() );
		}
		else if( _parentWindow instanceof JDialog )
		{
			result = new EngineInstanceConfiguration_JDialog( (JDialog) _parentWindow, modal,
												getAppliConf() );
		}

		result.setInternationalizationEndCallBack(callbackWindow);
		result.init( getChessEngineConfigurationMap(), eiConf );

		return( result );
	}

	protected String getSelectedEngine()
	{
		return( _enginesCbgMan.getSelectedItem() );
	}

	protected void createEngineConfiguration( Consumer<String> callback )
	{
		EngineInstanceConfiguration_JDialog dial = createEngineInstanceConfiguration_JDialog( null,
			(iiec) -> processCreateEngineConfiguration( iiec, callback ) );
	}

	public void processCreateEngineConfiguration( InternationalizationInitializationEndCallback iiec,
									Consumer<String> callback)
	{
		String result = null;
		EngineInstanceConfiguration_JDialog dial = (EngineInstanceConfiguration_JDialog) iiec;
		dial.setTitle( dial.getInternationalString(EngineInstanceConfiguration_JDialog.CONF_NEW_ENGINE ) );
		dial.setVisibleWithLock(true);
		if( dial.wasSuccessful() )
		{
			EngineInstanceConfiguration eiConf = dial.getResult();
			result = eiConf.getName();

			getChessEngineConfigurationMap().add(eiConf);
		}

		invokeCallback( callback, result );
	}
/*
	protected String createEngineConfiguration()
	{
		String result = null;

		EngineInstanceConfiguration_JDialog dial = createEngineInstanceConfiguration_JDialog( null );

		dial.setVisible(true);
		if( dial.wasSuccessful() )
		{
			EngineInstanceConfiguration eiConf = dial.getResult();
			result = eiConf.getName();

			getChessEngineConfigurationMap().add(eiConf);
		}

		return( result );
	}
*/
	public abstract String modifyEngineConfiguration( String engineName, Consumer<String> callback );


	protected String removeEngineConfiguration( String engineName )
	{
		String result = null;

		if( ( _chessEngineConfigurationMap != null ) &&
			( _chessEngineConfigurationMap.remove(engineName) != null ) )
		{
			result = engineName;
		}

		return( result );
	}

	@Override
	public void added(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData,
		Consumer<String> callback )
	{
		if( sender == _enginesCbgMan )
			createEngineConfiguration(
					(item) -> {
						if( item != null )
						{
							updateCombos();

							if( _listener != null )
								_listener.addedElement(this, item);
						}

						invokeCallback( callback, item );
					});
	}

	@Override
	public void removed(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData,
		Consumer<String> callback)
	{
		String result = null;
		if( sender == _enginesCbgMan )
			result = removeEngineConfiguration( eventData.getItem() );

		if( result != null )
		{
			updateCombos();

			if( _listener != null )
				_listener.addedElement(this, result);
		}

		invokeCallback( callback, result );
	}

	protected void invokeCallback( Consumer<String> callback, String item )
	{
		if( callback != null )
			callback.accept(item);
	}

	@Override
	public void modify(ComboBoxGroupManager sender, AddRemoveModifyItemResult eventData)
	{
		boolean validateAtOnce = false;

		if( sender == _enginesCbgMan )
			modifyEngineConfiguration( eventData.getItem(),
				(item) -> {

					if( item != null )
					{
						updateCombos();
					
						if( _listener != null )
							_listener.removedElement(this, item);
					}
				});

	}

	@Override
	public void comboBoxSelectionChanged(ComboBoxGroupManager sender, String previousSelectedItem, String newSelection) {
		System.out.println( "RegexComboControllerBase - comboBoxSelectionChanged" );
	}

	protected void updateCombosKeepingSelection( ComboBoxGroupManager cbMan )
	{
		if( cbMan != null )
			cbMan.updateCombosKeepingSelection();
	}

	protected void updateCombos( ComboBoxGroupManager cbMan )
	{
		if( cbMan != null )
			cbMan.updateCombos();
	}

	public void updateCombosKeepingSelection()
	{
		SwingUtilities.invokeLater(() -> {
			_enginesCbgMan.setComboBoxContent(_chessEngineConfigurationMap.getComboBoxContent() ); // just in case it was reverted
			updateCombosKeepingSelection( _enginesCbgMan );
		});
	}

	public void updateCombos()
	{
		SwingUtilities.invokeLater(() -> {
			_enginesCbgMan.setComboBoxContent(_chessEngineConfigurationMap.getComboBoxContent() ); // just in case it was reverted
			updateCombos(_enginesCbgMan );
		});
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper) {
		_enginesCbgMan.setComponentMapper(mapper);

		_alreadyMapped = true;
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		return( _alreadyMapped );
	}

	public ChainedParentComboBoxGroupManager getFilesComboBoxGroupManager()
	{
		return( _enginesCbgMan );
	}

	protected EngineInstanceConfiguration getEngineInstanceConfiguration( String engineName )
	{
		EngineInstanceConfiguration result = null;
		if( engineName != null )
			result = _chessEngineConfigurationMap.get(engineName);

		return( result );
	}
/*
	protected TextComboBoxContent getContentForCombosOfBlock( String fileName )
	{
		TextComboBoxContent result = null;
		RegexWholeFileModel rwfm = getRegexWholeFileModel(fileName);
		if( rwfm != null )
			result = rwfm.getContentForBlocks();

		return( result );
	}
*/
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
/*
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
*/
		}

		if( result == null )
			result = getEmptyTextComboBox();

		return( result );
	}

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_ARE_YOU_SURE_YOU_WANT_TO_ERASE_THAT_ENGINE, "Are you sure you want to erase that engine" );
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
