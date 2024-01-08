/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.combobox.chained.impl;

import com.frojasg1.applications.common.configuration.ParameterListConfiguration;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import com.frojasg1.general.combohistory.impl.TextComboBoxHistoryWithProperties;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentForChildComboContentServer;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChainedParentChildComboBoxManagerAutomaticSave extends ChainedParentChildComboBoxManagerBase {
	

	public ChainedParentChildComboBoxManagerAutomaticSave( String key,
						BaseApplicationConfigurationInterface appliConf,
						String confBaseFileName,
						int maxItems,
						ChainedParentForChildComboContentServer contentServer )
	{
		super( key, null, contentServer, null );

		String language = null;
		ParameterListConfiguration conf = new ParameterListConfiguration( appliConf.getConfigurationMainFolder(),
															appliConf.getApplicationNameFolder(),
															appliConf.getApplicationGroup(), language,
															confBaseFileName + ".properties" );
		ExecutionFunctions.instance().safeMethodExecution( () -> conf.M_openConfiguration() );
		TextComboBoxHistoryWithProperties content = new TextComboBoxHistoryWithProperties( maxItems, conf );
		content.init( (List<String>)null);
		content.loadItems();

		setComboBoxContent( content );
	}

	public ChainedParentChildComboBoxManagerAutomaticSave( String key, TextComboBoxHistoryWithProperties contents,
						ChainedParentForChildComboContentServer contentServer )
	{
		this( key, contents, contentServer, null );
	}

	public ChainedParentChildComboBoxManagerAutomaticSave( String key, TextComboBoxHistoryWithProperties contents,
						ChainedParentForChildComboContentServer contentServer,
						ChainedParentComboBoxGroupManager parent )
	{
		super( key, contents, contentServer, parent );
	}

	@Override
	public void setComboBoxContent(TextComboBoxContent comboBoxContents)
	{
		if( !( comboBoxContents instanceof TextComboBoxHistoryWithProperties ) )
			throw( new RuntimeException( "comboBoxContents is not instanceof TextComboBoxHistoryWithProperties" ) );

		super.setComboBoxContent(comboBoxContents);
	}

	@Override
	public TextComboBoxHistoryWithProperties getComboBoxContent()
	{
		return( (TextComboBoxHistoryWithProperties) super.getComboBoxContent() );
	}

	@Override
	public void newItemSelected( String newItem )
	{
		super.newItemSelected( newItem );
		saveComboboxContents();
	}

	protected void saveComboboxContents()
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> getComboBoxContent().save() );
	}
}
