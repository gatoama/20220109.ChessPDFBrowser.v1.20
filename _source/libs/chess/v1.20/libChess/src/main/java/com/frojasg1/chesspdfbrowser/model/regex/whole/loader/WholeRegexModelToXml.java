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

import com.frojasg1.chesspdfbrowser.model.regex.BlockRegexConfigurationContainer;
import com.frojasg1.chesspdfbrowser.model.regex.LineModel;
import com.frojasg1.chesspdfbrowser.model.regex.RegexOfBlockModel;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.model.regex.TagReplacementModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.XmlFunctions;
import com.frojasg1.general.xml.persistency.loader.ModelToXml;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class WholeRegexModelToXml implements ModelToXml<RegexWholeFileModel>
{
	@Override
	public XmlElement build( RegexWholeFileModel model )
	{
		XmlElement result = createElement( "regexwhole" );

		XmlElement blockregexes = createElement( "blockregexes" );
		XmlElement profiles = createElement( "profiles" );

		result.addChild( blockregexes );
		result.addChild( profiles );

		translateBlocks( model.getBlockConfigurationContainer(), model.getContentForBlocks().getListOfItems(), blockregexes );
		translateProfiles( model, model.getComboBoxContentForProfiles().getListOfItems(), profiles );

		return( result );
	}

	protected <CC> List<CC> reverse( List<CC> input )
	{
		return( CollectionFunctions.instance().reverseList(input) );
	}

	protected XmlElement createElement( String name )
	{
		return( XmlFunctions.instance().createXmlElement( name ) );
	}

	protected void translateBlocks( BlockRegexConfigurationContainer brecc,
									List<String> items, XmlElement blockregexesXe )
	{
		for( String blockName: reverse(items) )
			blockregexesXe.addChild( createBlockXe( blockName, brecc.get(blockName) ) );
	}

	protected XmlElement createBlockXe( String blockName, RegexOfBlockModel model )
	{
		return( createRegexXe( "block", blockName, model ) );
	}

	protected XmlElement createTagXe( String tagName, TagReplacementModel model )
	{
		XmlElement result = createElement( "tag" );
		XmlElement nameXe = createElement( "name" );
		XmlElement blockToReplaceWithXe = createElement( "blocktoreplacewith" );

		String name = model.getTagName();
		String blockToReplaceWith = model.getBlockToReplaceWith();

		nameXe.setText(name);
		blockToReplaceWithXe.setText( blockToReplaceWith );

		result.addChild( nameXe );
		result.addChild( blockToReplaceWithXe );

		return( result );
	}

	protected XmlElement createRegexXe( String tagName, String name, RegexOfBlockModel model )
	{
		XmlElement result = createElement( tagName );
		XmlElement nameXe = createElement( "name" );
		XmlElement regex = createElement( "regex" );
		
		nameXe.setText(name);
		regex.setText( model.getExpression() );

		result.addChild( nameXe );
		result.addChild( regex );

		return( result );
	}

	protected void translateProfiles( RegexWholeFileModel model,
					List<String> profileList, XmlElement profilesXe )
	{
		for( String profileName: profileList )
			profilesXe.addChild( createProfileXe( model.getProfile(profileName), profileName ) );
	}

	protected XmlElement createProfileXe( ProfileModel profile, String profileName )
	{
		XmlElement result = createElement( "profile" );

		XmlElement nameXe = createElement( "name" );
		nameXe.setText( profileName );

		XmlElement activeXe = createElement( "isactive" );
		activeXe.setText( String.valueOf( profile.isActive() ) );

		XmlElement linesXe = createElement( "lines" );

		for( LineModel line: profile.getListOfLines() )
			linesXe.addChild( createLineXe( line ) );

		result.addChild( nameXe );
		result.addChild( activeXe );
		result.addChild( linesXe );

		return( result );
	}

	protected XmlElement createLineXe( LineModel line )
	{
		XmlElement result = createElement( "line" );

		XmlElement optionalXe = createElement( "optional" );
		optionalXe.setText( String.valueOf( line.isOptional() ) );

		XmlElement syncRegexXe = createElement( "synchronizationregex" );
		syncRegexXe.setText( line.getSynchronizationRegexModel().getExpression() );

		XmlElement tagsXe = createElement( "tags" );
		for( String tagName: reverse( line.getComboBoxContent().getListOfItems() ) )
			tagsXe.addChild( createTagXe( tagName, line.get(tagName) ) );

		result.addChild( optionalXe );
		result.addChild( syncRegexXe );
		result.addChild( tagsXe );

		return( result );
	}
}
