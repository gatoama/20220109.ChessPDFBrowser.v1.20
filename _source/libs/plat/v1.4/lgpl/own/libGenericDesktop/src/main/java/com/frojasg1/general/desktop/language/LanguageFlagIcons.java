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
package com.frojasg1.general.desktop.language;

import com.frojasg1.applications.common.configuration.ConfigurationParent;
import com.frojasg1.general.ResourceFunctions;
import com.frojasg1.general.desktop.GenericDesktopConstants;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.icons.ZoomIconBuilder;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomIconImp;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LanguageFlagIcons
{
	protected static final String FLAG_FILE_NAME = "flag.png";

	protected static LanguageFlagIcons _instance;

	protected Map<String, Map<Double, ZoomIconImp> > _mapOfFlags = new HashMap<>();

	

	public static void changeInstance( LanguageFlagIcons inst )
	{
		_instance = inst;
	}

	public static LanguageFlagIcons instance()
	{
		if( _instance == null )
			_instance = new LanguageFlagIcons();
		return( _instance );
	}
/*
	public void copyLanguageConfigurationFilesFromJar( String newFolder )
	{
		M_copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "CopyPastePopupMenu.properties" );
		M_copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "searchAndReplaceJFrame_LAN.properties" );
	}
*/
	protected void M_copyLanguageConfigurationFileFromJarToFolder( 
							String destinationFolder,
							String originLanguage,
							String fileName )
	{
		String longFileNameInJar = GenericDesktopConstants.sa_PROPERTIES_PATH_IN_JAR + "/" + originLanguage + "/" + fileName;
		String longFileNameInDisk = destinationFolder + ConfigurationParent.sa_dirSeparator + fileName;

		ResourceFunctions.instance().copyBinaryResourceToFile(longFileNameInJar, longFileNameInDisk );
	}

	protected Map<Double, ZoomIconImp> getLanguageMapOfFlags( String language )
	{
		Map<Double, ZoomIconImp> result = _mapOfFlags.get( language );

		return( result );
	}

	protected ZoomIconImp getReferenceZoomIcon( Map<Double, ZoomIconImp> map )
	{
		ZoomIconImp result = null;

		if( map != null )
		{
			if( map.size() > 0 )
			{
				Iterator<ZoomIconImp> it = map.values().iterator();
				
				while( ( result == null ) && it.hasNext() )
					result = it.next();
			}
		}

		return( result );
	}

	protected Double calculateAdditionalFactor( Map<Double, ZoomIconImp> languageMap,
												Dimension dimen )
	{
		Double result = null;

		ZoomIconImp zi = getReferenceZoomIcon( languageMap );

		if( zi != null )
		{
			Icon icon = zi.getOriginalIcon();
			result = calculateAdditionalFactor( icon, dimen );
		}

		return( result );
	}

	protected Double calculateAdditionalFactor( Icon icon, Dimension dimen )
	{
		return( ZoomIconBuilder.instance().calculateAdditionalFactor( icon, dimen ) );
	}

	protected ZoomIconImp lookForFlagIcon( String language, Dimension dimen )
	{
		ZoomIconImp result = null;
		Map<Double, ZoomIconImp> languageMap = getLanguageMapOfFlags( language );

		if( languageMap != null )
		{
			Double factor = calculateAdditionalFactor( languageMap, dimen );

			if( factor != null )
				result = languageMap.get( factor );
		}

		return( result );
	}

	protected Icon createOriginalIcon( BufferedImage image )
	{
		return ZoomIconBuilder.instance().createOriginalIcon(image);
	}

	protected Icon createOriginalIcon( String language )
	{
		Icon result = null;

		try
		{
			String resourceFileName = GenericDesktopConstants.sa_PROPERTIES_PATH_IN_JAR + "/" + language + "/" + FLAG_FILE_NAME;
			BufferedImage bi = ImageFunctions.instance().loadImageFromJar( resourceFileName );

			result = createOriginalIcon( bi );
		}
		catch( Exception ex )
		{}

		return( result );
	}

	protected ZoomIconImp createZoomIcon( Icon originalIcon, double factor )
	{
		ZoomIconImp result = new ZoomIconImp( originalIcon );
		result.setAdditionalFactor(factor);

		return( result );
	}

	protected ZoomIconImp createFlagIcon( String language, Dimension dimen )
	{
		ZoomIconImp result = null;
		Map<Double, ZoomIconImp> languageMap = getLanguageMapOfFlags( language );

		// if there is an empty map, means that no icon exists for that language, so in that case, we return null
		if( ( languageMap == null ) || ( languageMap.size() != 0 ) )
		{
			Icon originalIcon = null;

			if( languageMap == null )
			{
				languageMap = new HashMap<>();
				_mapOfFlags.put( language, languageMap );

				originalIcon = createOriginalIcon( language );
			}
			else
			{
				ZoomIconImp zi = getReferenceZoomIcon( languageMap );
				if( zi != null )
					originalIcon = zi.getOriginalIcon();
			}

			Double factor = calculateAdditionalFactor( originalIcon, dimen );
			if( factor != null )
			{
				result = ZoomIconBuilder.instance().createZoomIcon( originalIcon, factor );
//				result.setCanInvertColors(false);

				languageMap.put(factor, result);
			}
		}

		return( result );
	}

	public ZoomIconImp getFlagOfLanguage( String language, Dimension dimen )
	{
		ZoomIconImp result = null;

		result = lookForFlagIcon( language, dimen );
		if( result == null )
		{
			result = createFlagIcon( language, dimen );
		}

		return( result );
	}
}
