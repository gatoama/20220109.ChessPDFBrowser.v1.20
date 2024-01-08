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
package com.frojasg1.general.xml.persistency.loader.impl;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.persistency.loader.XmlToModel;
import java.util.Objects;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class XmlToModelBase<CC> implements XmlToModel<CC>, InternationalizedStringConf
{
	protected static final String CONF_CHILD_NOT_PRESENT_ERROR_MESSAGE = "CHILD_NOT_PRESENT_ERROR_MESSAGE";
	protected static final String CONF_DID_NOT_HAVE_A_VALID_LONG = "DID_NOT_HAVE_A_VALID_LONG";
	protected static final String CONF_XML_ELEMENT_HAD_NOT_EXPECTED_NAME = "XML_ELEMENT_HAD_NOT_EXPECTED_NAME";

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	protected void init( String languageGlobalConfFileName,
							String propertiesPathInJar )
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( languageGlobalConfFileName,
								propertiesPathInJar );

		registerInternationalizedStrings();
	}

	protected void assertXmlElementName( XmlElement xe, String expectedName )
	{
		String name = ( xe == null ) ? null : xe.getName();
		if( !Objects.equals( name, expectedName ) )
			throw( new RuntimeException( this.createCustomInternationalString( CONF_XML_ELEMENT_HAD_NOT_EXPECTED_NAME, name, expectedName )) );
	}

	protected XmlElement getMandatoryChild( XmlElement parent, String childName, String errorMessage )
	{
		XmlElement result = parent.getChild( childName );
		if( result == null )
			throw( new RuntimeException( errorMessage ) );

		return( result );
	}

	protected String getMandatoryChildString( XmlElement parent, String childName, String errorMessage )
	{
		String result = null;
		XmlElement resultXe = getMandatoryChild( parent, childName, errorMessage );
		if( resultXe != null )
			result = resultXe.getText();

		return( result );
	}

	protected String getOptionalChildString( XmlElement parent, String childName )
	{
		String result = null;
		if( parent != null )
		{
			XmlElement resultXe = parent.getChild( childName );
			if( resultXe != null )
				result = resultXe.getText();
		}

		return( result );
	}

	protected String childNotPresentErrorMessage( String parentName, String childName )
	{
		return( createCustomInternationalString( CONF_CHILD_NOT_PRESENT_ERROR_MESSAGE, parentName, childName ) );
	}

	protected int getInt( XmlElement xmlElement, String childName, String errorHeader )
	{
		return( (int) getLong( xmlElement, childName, errorHeader ) );
	}

	protected long getLong( XmlElement xmlElement, String childName, String parentName )
	{
		XmlElement xe = getMandatoryChild(xmlElement, childName, childNotPresentErrorMessage(parentName, childName ) );
//		Long result = IntegerFunctions.parseLong( xe.getText() );

		Double result = ExecutionFunctions.instance().safeFunctionExecution( () -> Double.parseDouble( xe.getText() ) );
		if( result == null )
			throw( new RuntimeException( createCustomInternationalString( CONF_DID_NOT_HAVE_A_VALID_LONG, parentName, childName ) ) );

		return( (long) ( (double) result ) );
	}

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_CHILD_NOT_PRESENT_ERROR_MESSAGE, "$1 without $2 in xml" );
		this.registerInternationalString(CONF_DID_NOT_HAVE_A_VALID_LONG, "$1 -> $2 did not have a valid Long." );
		this.registerInternationalString(CONF_XML_ELEMENT_HAD_NOT_EXPECTED_NAME, "Name of xml element: <$1> but was expected <$2>" );
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
}
