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
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.ButtonConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.CheckConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.ComboConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.SpinConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.StringConfigurationItem;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.persistency.loader.impl.ModelToXmlBase;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineInstanceConfigurationToXml extends ModelToXmlBase<EngineInstanceConfiguration>
{
	@Override
	public XmlElement build( EngineInstanceConfiguration model )
	{
		XmlElement result = createElement( "chessengine" );

		XmlElement nameXe = createLeafElement( result, "name", model.getName() );
		XmlElement commandXe = createLeafElement( result, "startcommand", model.getEngineCommandForLaunching() );
		XmlElement enginetypeXe = createLeafElement( result, "enginetype", engineTypeToString( model.getEngineType() ) );

		XmlElement configurationXe = createElement( result, "configuration" );
		XmlElement itemsXe = createElement( configurationXe, "items" );
		fillInConfiguration( model.getChessEngineConfiguration(), itemsXe );

		return( result );
	}

	protected void fillInConfiguration( ChessEngineConfiguration conf,
									XmlElement configurationXe )
	{
		for( ConfigurationItem ci: conf.getMap().values() )
			configurationXe.addChild( createConfItemXe( ci ) );
	}

	protected XmlElement createConfItemXe( ConfigurationItem ci )
	{
		XmlElement result = null;
		if( ci instanceof ButtonConfigurationItem )
			result = createButtonConfItemXe( ( ButtonConfigurationItem ) ci );
		if( ci instanceof CheckConfigurationItem )
			result = createCheckConfItemXe( ( CheckConfigurationItem ) ci );
		if( ci instanceof ComboConfigurationItem )
			result = createComboConfItemXe( ( ComboConfigurationItem ) ci );
		if( ci instanceof SpinConfigurationItem )
			result = createSpinConfItemXe( ( SpinConfigurationItem ) ci );
		if( ci instanceof StringConfigurationItem )
			result = createStringConfItemXe( ( StringConfigurationItem ) ci );

		return( result );
	}

	protected <CC> void createConfItemXe_common( ConfigurationItem<CC> ci,
														XmlElement parent )
	{
		XmlElement nameXe = createLeafElement( parent, "name", ci.getName() );
		XmlElement defaultXe = createLeafElement( parent, "default", toString( ci.getDefaultValue() ) );
		XmlElement valueXe = createLeafElement( parent, "value", toString( ci.getValue() ) );
	}

	protected XmlElement createButtonConfItemXe( ButtonConfigurationItem ci )
	{
		XmlElement result = createElement( "button" );
		createConfItemXe_common( ci, result );

		return( result );
	}

	protected XmlElement createCheckConfItemXe( CheckConfigurationItem ci )
	{
		XmlElement result = createElement( "check" );
		createConfItemXe_common( ci, result );

		return( result );
	}

	protected XmlElement createComboConfItemXe( ComboConfigurationItem ci )
	{
		XmlElement result = createElement( "combo" );
		createConfItemXe_common( ci, result );
		result.addChild( createComboOptions( ci ) );

		return( result );
	}

	protected XmlElement createComboOptions( ComboConfigurationItem ci )
	{
		XmlElement result = this.createElement( "combooptions" );
		for( String item: ci.getAllowedValues() )
			createLeafElement( result, "item", item );

		return( result );
	}

	protected XmlElement createSpinConfItemXe( SpinConfigurationItem ci )
	{
		XmlElement result = createElement( "spin" );
		createConfItemXe_common( ci, result );

		XmlElement maxXe = createLeafElement( result, "max", toString( ci.getMax() ) );
		XmlElement minXe = createLeafElement( result, "min", toString( ci.getMin() ) );

		return( result );
	}

	protected XmlElement createStringConfItemXe( StringConfigurationItem ci )
	{
		XmlElement result = createElement( "string" );
		createConfItemXe_common( ci, result );

		return( result );
	}

	protected String engineTypeToString( int engineType )
	{
		String result = null;
		switch( engineType )
		{
			case EngineInstanceConfiguration.UCI: result = "UCI"; break;
			case EngineInstanceConfiguration.XBOARD: result = "XBOARD"; break;
			default: result = "UNKNOWN"; break;
		}

		return( result );
	}
}
