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
package com.frojasg1.chesspdfbrowser.model.regex.whole;

import com.frojasg1.chesspdfbrowser.engine.tags.regex.BlockRegexBuilder;
import com.frojasg1.chesspdfbrowser.model.regex.BlockRegexConfigurationContainer;
import com.frojasg1.chesspdfbrowser.model.regex.RegexOfBlockModel;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import com.frojasg1.general.combohistory.impl.TextComboBoxHistory;
import com.frojasg1.general.xml.model.KeyModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexWholeFileModel implements KeyModel<String>
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected Map< String, ProfileModel > _map = null;

//	protected BaseApplicationConfigurationInterface _appliConf = null;
//	protected ParameterListConfiguration _listOfProfilesConf = null;

	protected TextComboBoxContent _cbContentForProfiles = null;

	protected BlockRegexConfigurationContainer _blockConfigurationContainer = null;

	protected BlockRegexBuilder _regexBuilder = null;

	protected String _singleFileName = null;

	// function for DefaultConstructorInitCopier
	public RegexWholeFileModel()
	{
		
	}

	// function for DefaultConstructorInitCopier
	public void init( RegexWholeFileModel other )
	{
		_singleFileName = other._singleFileName;
		_map = _copier.copyMap( other._map );

		// do not change, as block regex configuration must be accessible at the same memory address
		// even after the invocation to this function
		// for its memory address not to change
		if( _blockConfigurationContainer == null )
			_blockConfigurationContainer = createEmptyBlockContainer();
		_copier.copy( _blockConfigurationContainer, other._blockConfigurationContainer );

		// do not change, as block regex configuration must be accessible at the same memory address
		// even after the invocation to this function
		// for its memory address not to change
		if( _cbContentForProfiles == null )
			_cbContentForProfiles = createComboBoxHistory();
		 _copier.copy( _cbContentForProfiles, other._cbContentForProfiles );

		_regexBuilder = other._regexBuilder;
	}

	public void init()
	{
		_map = new HashMap<>();

		_cbContentForProfiles = createComboBoxHistory();

		_blockConfigurationContainer = createEmptyBlockContainer();

		_regexBuilder = createRegexBuilder();
	}

	@Override
	public void setKey( String key )
	{
		setFileName( key );
	}

	@Override
	public String getKey()
	{
		return( getFileName() );
	}

	public void setFileName( String singleFileName )
	{
		_singleFileName = singleFileName;
	}

	public String getFileName()
	{
		return( _singleFileName );
	}

	public BlockRegexConfigurationContainer getBlockConfigurationContainer()
	{
		return( _blockConfigurationContainer );
	}

	protected BlockRegexBuilder createRegexBuilder()
	{
		return( new BlockRegexBuilder( _blockConfigurationContainer ) );
	}

	public TextComboBoxHistory createComboBoxHistory()
	{
		TextComboBoxHistory result = new TextComboBoxHistory( null );
		result.init( (List<String>) null );

		return( result );
	}

	public BlockRegexBuilder getBlockRegexBuilder()
	{
		return( _regexBuilder );
	}

	protected BlockRegexConfigurationContainer createEmptyBlockContainer()
	{
		BlockRegexConfigurationContainer result = new BlockRegexConfigurationContainer();
		result.init( this );

		return( result );
	}

	protected void loadBlockContainer()
	{
		_blockConfigurationContainer = createEmptyBlockContainer();
	}
/*
	protected void loadProfiles()
	{
		try
		{
			_map.clear();

			_listOfProfilesConf.M_openConfiguration();

			List<String> items = _listOfProfilesConf.getList();

			if( items != null )
				for( String profileName: items )
					addProfile(profileName, null);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
*/
	public ProfileModel createProfile( String profileName, ProfileModel other )
	{
		ProfileModel result = new ProfileModel();

		if( other != null )
			result.init( other );
		else
		{
			result.init( profileName, this );
		}

		return( result );
	}

	public boolean addProfile( ProfileModel profConf )
	{
		boolean result = false;
		if( profConf != null )
		{
			String profileName = profConf.getProfileName();
			if( profileName != null )
			{
				_cbContentForProfiles.newItemSelected(profileName);
				_map.put( profileName, profConf );
				result = true;
			}
		}

		return( result );
	}
/*
	protected void addProfile( String profileName, ProfileModel other )
	{
		ProfileModel profConf = createProfile( profileName, other );
		addProfile( profConf );
	}

	protected ListOfProfilesConf_old createListOfProfilesConf()
	{
		ListOfProfilesConf_old result = new ListOfProfilesConf_old( getAppliConf() );
		ExecutionFunctions.instance().safeMethodExecution( () -> result.M_openConfiguration() );

		return( result );
	}

	public ParameterListConfiguration getListOfProfilesConf()
	{
		return( _listOfProfilesConf );
	}
*/
	public void invalidateCaches()
	{
		_blockConfigurationContainer.invalidateCaches();
//		for( ProfileModel cont: _map.values() )
//			cont.invalidateCaches();
	}

	public void put( String profileName, ProfileModel value )
	{
		_map.put( profileName, value );
		_cbContentForProfiles.addItem(profileName);
	}

	public ProfileModel get( String profileName )
	{
		return( _map.get( profileName ) );
	}

	public boolean removeProfile( String profileName )
	{
		_cbContentForProfiles.removeItem(profileName);
		return( _map.remove( profileName ) != null );
	}

	public TextComboBoxContent getComboBoxContentForProfiles()
	{
		return( _cbContentForProfiles );
	}

	public TextComboBoxContent getContentForBlocks()
	{
		return( _blockConfigurationContainer.getComboBoxContent() );
	}

	public ProfileModel getProfile( String profileName )
	{
		return( _map.get(profileName) );
	}

	public RegexOfBlockModel getBlockRegexConf( String blockName )
	{
		return( getRegexModel( _blockConfigurationContainer, blockName ) );
	}

	public Collection<ProfileModel> getSetOfProfiles()
	{
		return( _map.values() );
	}

	protected RegexOfBlockModel getRegexModel( BlockRegexConfigurationContainer blockOrTagConfigContainer,
										String blockOrTagName )
	{
		RegexOfBlockModel result = null;

		if( blockOrTagConfigContainer != null )
			result = blockOrTagConfigContainer.get(blockOrTagName);

		return( result );
	}
/*
	public Exception save()
	{
		Exception result = save( getBlockConfigurationContainer() );

		Exception ex2 = saveProfileList();
		if( result == null )
			result = ex2;

		for( ProfileModel tagConfCont: _map.values() )
		{
			ex2 = save( tagConfCont );
			if( result == null )
				result = ex2;
		}

		return( result );
	}
*/
	public boolean profileExists( String profileName )
	{
		return( _cbContentForProfiles.contains(profileName) );
	}
}
