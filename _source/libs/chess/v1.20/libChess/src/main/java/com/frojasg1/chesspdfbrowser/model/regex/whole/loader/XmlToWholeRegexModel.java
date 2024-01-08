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
package com.frojasg1.chesspdfbrowser.model.regex.whole.loader;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.model.regex.BlockRegexConfigurationContainer;
import com.frojasg1.chesspdfbrowser.model.regex.LineModel;
import com.frojasg1.chesspdfbrowser.model.regex.RegexOfBlockModel;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.model.regex.TagReplacementModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.XmlElementList;
import com.frojasg1.general.xml.persistency.loader.XmlToModel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class XmlToWholeRegexModel implements XmlToModel<RegexWholeFileModel>,
												InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "XmlToWholeRegexModel.properties";

	protected static final String CONF_XML_WITHOUT_REGEXES = "XML_WITHOUT_REGEXES";
	protected static final String CONF_LACK_OF_FIELDNAME = "LACK_OF_FIELDNAME";
	protected static final String CONF_XML_WITHOUT_PROFILES = "XML_WITHOUT_PROFILES";
	protected static final String CONF_PROFILE_WITHOUT_NAME = "PROFILE_WITHOUT_NAME";
	protected static final String CONF_LINE_WITHOUT_OPTIONAL_REGEX = "LINE_WITHOUT_OPTIONAL_REGEX";
	protected static final String CONF_LINE_WITHOUT_SYNCHRONIZATION_REGEX = "LINE_WITHOUT_SYNCHRONIZATION_REGEX";
	protected static final String CONF_LINE_WITHOUT_TAGS = "LINE_WITHOUT_TAGS";
	protected static final String CONF_LACK_OF_FIELD_TAGNAME = "LACK_OF_FIELD_TAGNAME";

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	protected RegexWholeFileModel createEmptyRegexWholeContainer()
	{
		RegexWholeFileModel result = new RegexWholeFileModel();
		result.init();

		return( result );
	}

	public XmlToWholeRegexModel()
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );

		registerInternationalizedStrings();
	}

	@Override
	public RegexWholeFileModel build( XmlElement xmlElement )
	{
		RegexWholeFileModel result = createEmptyRegexWholeContainer();

		if( xmlElement != null )
		{
			loadBlockRegexes( result.getBlockConfigurationContainer(), xmlElement.getChild("blockregexes" ) );
			loadProfiles( result, xmlElement.getChild( "profiles" ) );
		}

		return( result );
	}

	protected void loadBlockRegexes( BlockRegexConfigurationContainer blockCont, XmlElement xmlElement )
	{
		if( xmlElement == null )
			throw( new RuntimeException( "Xml without blockregexes" ) );

		XmlElementList xel = xmlElement.getChildrenByName( "block" );
		if( xel != null )
		{
			for( XmlElement blockXe: xel.getCollection() )
				loadBlock( blockCont, blockXe );
		}
	}

	protected void loadBlock( BlockRegexConfigurationContainer blockCont, XmlElement regexModelXe )
	{
		XmlElement nameXe = regexModelXe.getChild( "name" );
		XmlElement regexXe = regexModelXe.getChild( "regex" );

		if( ( nameXe == null ) || ( regexXe == null ) )
		{
			throw( new RuntimeException( "Lack of field name or regex in block regex." ) );
		}

		String name = nameXe.getText();
		String regex = regexXe.getText();

		RegexOfBlockModel robm = blockCont.addBlockRegex(name, null);
		robm.setExpression(regex);
	}

	protected void loadProfiles( RegexWholeFileModel wholeCont, XmlElement xmlElement )
	{
		if( xmlElement == null )
			throw( new RuntimeException( "Xml without profiles" ) );

		XmlElementList xel = xmlElement.getChildrenByName( "profile" );
		if( xel != null )
		{
			for( XmlElement profileXmlElement: xel.getCollection() )
				loadProfile( wholeCont, profileXmlElement );
		}
	}

	protected void loadProfile( RegexWholeFileModel wholeCont, XmlElement profileXmlElement )
	{
		XmlElement nameXe = profileXmlElement.getChild( "name" );
		if( nameXe == null )
			throw( new RuntimeException( "Profile without name in xml" ) );
		String name = nameXe.getText();

		ProfileModel tagRegexCont = wholeCont.createProfile( name, null );
		wholeCont.addProfile( tagRegexCont );

		XmlElement activeXe = profileXmlElement.getChild( "isactive" );
		if( activeXe != null )
		{
			boolean isActive = stringToBoolean( activeXe.getText() );
			tagRegexCont.setActive( isActive );
		}

		XmlElement linesXe = profileXmlElement.getChild( "lines" );

		XmlElementList xel = linesXe.getChildrenByName( "line" );
		if( xel != null )
		{
			int index = 0;
			for( XmlElement lineXe: xel.getCollection() )
			{
				LineModel lotr = tagRegexCont.addEmptyLineOfTagRegexes( index );

				loadLineOfProfile( lotr, lineXe );
				index++;
			}
		}
	}

	protected boolean stringToBoolean( String str )
	{
		Boolean result = ExecutionFunctions.instance().safeFunctionExecution( () -> Boolean.parseBoolean(str) );
		if( result == null )
			result = false;

		return( result );
	}

	protected void loadLineOfProfile( LineModel lotr, XmlElement lineXe )
	{
		XmlElement optionanlElem = lineXe.getChild( "optional" );
		if( optionanlElem == null )
			throw( new RuntimeException( "Line without optional regex, in profile: " + lotr.getParent().getProfileName() ) );
		boolean isOptional = stringToBoolean( optionanlElem.getText() );
		lotr.setOptional(isOptional);

		XmlElement syncRegexElem = lineXe.getChild( "synchronizationregex" );
		if( syncRegexElem == null )
			throw( new RuntimeException( "Line without synchronization regex, in profile: " + lotr.getParent().getProfileName() ) );

		String synchRegex = syncRegexElem.getText();
		lotr.getSynchronizationRegexModel().setExpression( synchRegex );

		XmlElement tagsXe = lineXe.getChild( "tags" );
		if( tagsXe == null )
			throw( new RuntimeException( "Line without tags, in profile: " + lotr.getParent().getProfileName() ) );

		XmlElementList xel = tagsXe.getChildrenByName( "tag" );
		if( xel != null )
		{
			int index = 0;
			for( XmlElement regexModelXe: xel.getCollection() )
			{
				loadRegexModel( lotr, regexModelXe );
				index++;
			}
		}
	}

	protected void loadRegexModel( LineModel lotr, XmlElement regexModelXe )
	{
		XmlElement nameXe = regexModelXe.getChild( "name" );
		XmlElement blockToReplaceWithXe = regexModelXe.getChild( "blocktoreplacewith" );

		if( nameXe == null )
		{
			throw( new RuntimeException( "Lack of field tagName line of profile: " + lotr.getParent().getProfileName() ) );
		}

		String name = nameXe.getText();
		String blockToReplaceWith = null;
		if( blockToReplaceWithXe != null )
			blockToReplaceWith = blockToReplaceWithXe.getText();

		TagReplacementModel trm = lotr.addTagRegexReplacement(name, null);
		trm.setBlockToReplaceWith(blockToReplaceWith);
	}

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_XML_WITHOUT_REGEXES, "Error loading images" );
		this.registerInternationalString(CONF_LACK_OF_FIELDNAME, "Lack of field name or regex in block regex." );
		this.registerInternationalString(CONF_XML_WITHOUT_PROFILES, "Xml without profiles" );
		this.registerInternationalString(CONF_PROFILE_WITHOUT_NAME, "Profile without name in xml" );
		this.registerInternationalString(CONF_LINE_WITHOUT_OPTIONAL_REGEX, "Line without optional regex, in profile: $1" );
		this.registerInternationalString(CONF_LINE_WITHOUT_SYNCHRONIZATION_REGEX, "Line without synchronization regex, in profile: $1" );
		this.registerInternationalString(CONF_LINE_WITHOUT_TAGS, "Line without tags, in profile: $1" );
		this.registerInternationalString(CONF_LACK_OF_FIELD_TAGNAME, "Lack of field tagName line of profile: $1" );
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
