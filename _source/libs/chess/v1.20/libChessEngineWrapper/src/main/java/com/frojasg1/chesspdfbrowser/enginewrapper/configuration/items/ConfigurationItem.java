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
package com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.enginewrapper.constants.LibConstants;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ConfigurationItem<CC> implements ValueItem<CC>, InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "ConfigurationItem.properties";
	protected static final String CONF_LOW_BOUND_NOT_FULFILLLED = "LOW_BOUND_NOT_FULFILLLED";
	protected static final String CONF_HIGH_BOUND_NOT_FULFILLLED = "HIGH_BOUND_NOT_FULFILLLED";
	protected static final String CONF_COMBO_VALUE_IS_NOT_AMONG_ALLOWED_VALUES = "COMBO_VALUE_IS_NOT_AMONG_ALLOWED_VALUES";
	protected static final String CONF_BUTTON_CONFIGURATION_ITEM_NOT_NULL = "BUTTON_CONFIGURATION_ITEM_NOT_NULL";

	protected static InternationalizedStringConfImp _internationalizedStringConf = null;


	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	protected String _name = null;
	protected CC _value = null;
	protected CC _defaultValue = null;

	// for Copier
	public ConfigurationItem()
	{
		
	}

	public ConfigurationItem( CC defaultValue )
	{
		_defaultValue = defaultValue;
	}

	protected void init( ConfigurationItem<CC> that )
	{
		_name = _copier.copy( that._name );
		_value = _copier.copy( that._value );
		_defaultValue = _copier.copy( that._defaultValue );
	}

	public void init( String name, CC value )
	{
		_name = name;
		setValue( value );
	}

	public String getName()
	{
		return( _name );
	}

	public CC getDefaultValue()
	{
		return( _defaultValue );
	}

	@Override
	public CC getValue()
	{
		return( _value );
	}

	public CC getValueWithDefaultValue()
	{
		return( ( _value != null ) ? _value : _defaultValue );
	}

	public void setValue( CC value )
	{
		check( value );
		_value = value;
	}

	protected void check( CC value )
	{
	}

	public abstract Class<CC> getValueClass();

	public String getCommand()
	{
		return( String.format( "setoption name %s value %s", getName(), getValueWithDefaultValue() ) );
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

	protected static void registerInternationalizedStrings()
	{
		registerInternationalStringStatic(CONF_LOW_BOUND_NOT_FULFILLLED, "Low bound not fulfilled ( $1 < $2 )." );
		registerInternationalStringStatic(CONF_HIGH_BOUND_NOT_FULFILLLED, "High bound not fulfilled ( $1 < $2 )." );
		registerInternationalStringStatic(CONF_COMBO_VALUE_IS_NOT_AMONG_ALLOWED_VALUES, "Combo value [$1] not among allowed values [$2]" );
		registerInternationalStringStatic(CONF_BUTTON_CONFIGURATION_ITEM_NOT_NULL, "Button configuration item value not null" );
	}

	protected static void registerInternationalStringStatic(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}
}
