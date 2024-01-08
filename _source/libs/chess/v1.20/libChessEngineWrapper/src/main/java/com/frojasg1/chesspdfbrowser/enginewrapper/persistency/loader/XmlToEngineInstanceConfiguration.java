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
package com.frojasg1.chesspdfbrowser.enginewrapper.persistency.loader;

import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.builder.ChessEngineConfigurationBuilder;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.constants.LibConstants;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.XmlElementList;
import com.frojasg1.general.xml.persistency.loader.impl.XmlToModelBase;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class XmlToEngineInstanceConfiguration extends XmlToModelBase<EngineInstanceConfiguration>
{
	public static final String GLOBAL_CONF_FILE_NAME = "XmlToEngineInstanceConfiguration.properties";

	protected static final String CONF_ENGINE_TYPE_NOT_RECOGNIZED = "ENGINE_TYPE_NOT_RECOGNIZED";
	protected static final String CONF_ENGINE_TYPE_NOT_FOUND = "ENGINE_TYPE_NOT_FOUND";

	protected ChessEngineConfigurationBuilder  _configurationItemBuilder = null;

	public XmlToEngineInstanceConfiguration()
	{
	}

	protected void init( String languageGlobalConfFileName,
							String propertiesPathInJar )
	{
		throw( new RuntimeException( "Non usable init function" ) );
	}

	public void init()
	{
		// TODO: Translate
		super.init( GLOBAL_CONF_FILE_NAME, LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		_configurationItemBuilder = createConfigurationItemBuilder();
	}

	protected ChessEngineConfigurationBuilder createConfigurationItemBuilder()
	{
		ChessEngineConfigurationBuilder result = new ChessEngineConfigurationBuilder();
		result.init();

		return( result );
	}

	protected EngineInstanceConfiguration createEmptyModelObject()
	{
		return( new EngineInstanceConfiguration() );
	}

	@Override
	public EngineInstanceConfiguration build( XmlElement xmlElement )
	{
		EngineInstanceConfiguration result = null;

		if( xmlElement != null )
		{
			result = createEmptyModelObject();

			XmlElement rootXe = xmlElement;
			assertXmlElementName( rootXe, "chessengine" );

			loadChessEngineConfiguration( result, rootXe );
		}

		return( result );
	}

	protected void loadChessEngineConfiguration( EngineInstanceConfiguration result, XmlElement xmlElement )
	{
		String name = getMandatoryChildString( xmlElement, "name", childNotPresentErrorMessage( "chessengine", "name" ) );
		result.setName( name );

		String command = getMandatoryChildString( xmlElement, "startcommand", childNotPresentErrorMessage( "chessengine", "startcommand" ) );
		result.setEngineCommandForLaunching(command);

		int engineType = stringToEngineType( getMandatoryChildString( xmlElement, "enginetype", childNotPresentErrorMessage( "chessengine", "enginetype" ) ) );
		result.setEngineType(engineType);

		XmlElement configurationXe = getMandatoryChild( xmlElement, "configuration", childNotPresentErrorMessage( "chessengine", "configuration" ) );
		XmlElement itemsXe = getMandatoryChild( configurationXe, "items", childNotPresentErrorMessage( "configuration", "items" ) );

		ChessEngineConfiguration cec = createChessEngineConfiguration();
		for( XmlElement ciXe: itemsXe.getAllChildren() )
		{
			ConfigurationItem ci = loadConfigurationItem( ciXe );
			cec.add( ci );
		}

		result.setChessEngineConfiguration(cec);
	}

	protected ChessEngineConfiguration createChessEngineConfiguration()
	{
		ChessEngineConfiguration result = new ChessEngineConfiguration();
		result.init();

		return( result );
	}

	protected int stringToEngineType( String str )
	{
		int result = -1;
		if( str == null )
			throw( new RuntimeException( this.getInternationalString( CONF_ENGINE_TYPE_NOT_FOUND ) ) );

		switch( str )
		{
			case "UCI": result = EngineInstanceConfiguration.UCI; break;
			case "XBOARD": result = EngineInstanceConfiguration.XBOARD; break;
			default:
				throw( new RuntimeException( createCustomInternationalString(CONF_ENGINE_TYPE_NOT_RECOGNIZED,
											str ) ) );
		}

		return( result );
	}

	protected ConfigurationItem loadConfigurationItem( XmlElement ciXe )
	{
		ConfigurationItem result = null;

		String type = ciXe.getName();
		String name = this.getMandatoryChildString(ciXe, "name", childNotPresentErrorMessage( type, "name" ) );
		Map<String, String> pairMap  = createPairMap( ciXe );
		
		result = _configurationItemBuilder.createConfigurationItem(name, type, pairMap);

		return( result );
	}

	protected Map<String, String> createPairMap( XmlElement ciXe )
	{
		Map<String, String> result = new HashMap<>();

		for( XmlElement attribXe: ciXe.getAllChildren() )
		{
			String attribName = attribXe.getName();

			switch( attribName )
			{
				case "name": break;
				case "combooptions": result.put( "var", getOptions( attribXe ) ); break;
				default:
					result.put( attribName, attribXe.getText() );
			}
		}

		return( result );
	}

	protected String getOptions( XmlElement comboOptionsXe )
	{
		StringBuilder sb = new StringBuilder();

		XmlElementList list = comboOptionsXe.getChildrenByName( "item" );
		if( list != null )
		{
			String separator = "";
			for( XmlElement itemXe: list.getCollection() )
			{
				sb.append( separator ).append( itemXe.getText() );
				separator = " ";
			}
		}

		return( sb.toString() );
	}

	@Override
	protected void registerInternationalizedStrings()
	{
		super.registerInternationalizedStrings();

		// TODO: translate
		registerInternationalString(CONF_ENGINE_TYPE_NOT_RECOGNIZED, "Engine type not recognized [$1]" );
		registerInternationalString(CONF_ENGINE_TYPE_NOT_FOUND, "Engine type not found." );
	}

}
