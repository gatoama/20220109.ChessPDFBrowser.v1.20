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
package com.frojasg1.libpdf.utils;

import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.libpdf.api.GlyphWrapper;
import com.frojasg1.libpdf.api.ImageWrapper;
import com.frojasg1.libpdf.api.PdfElementWrapper;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PdfUtils
{
	protected static PdfUtils _instance = null;

	public static PdfUtils instance()
	{
		if( _instance == null )
			_instance = new PdfUtils();
		
		return( _instance );
	}


	protected Point center( Rectangle rect )
	{
		return( ViewFunctions.instance().getCenter(rect) );
	}

	protected boolean lookForLine( String line, List<GlyphWrapper> glyphs,
									Rectangle scannerRect, int topYY )
	{
		boolean found = false;
		while( !found && ( scannerRect.y < topYY ) )
		{
			String lineForYY = getLine( glyphs, scannerRect );
			found = line.equals( lineForYY );
			if( !found )
				scannerRect.y++;
		}

		return( found );
	}

	public Stream<GlyphWrapper> getSortedGlyphsStream( List<GlyphWrapper> glyphs,
		Rectangle rect )
	{
		return( glyphs.stream().filter( (gw) -> overlap( rect, gw.getBounds() ) )
			.sorted( (gw1, gw2) -> ( gw1.getBounds().x - gw2.getBounds().x ) ) );
	}

	public List<GlyphWrapper> getSortedGlyphs( List<GlyphWrapper> glyphs,
		Rectangle rect )
	{
		return( getSortedGlyphsStream( glyphs, rect ).collect( Collectors.toList() ) );
	}

	protected boolean overlap( Rectangle rect1, Rectangle rect2 )
	{
		return( ViewFunctions.instance().rectanglesOverlap(rect1, rect2) );
	}

	public String getLine( List<GlyphWrapper> glyphs, Rectangle rect )
	{
		StringBuilder sb = new StringBuilder();

		getSortedGlyphsStream( glyphs, rect )
			.forEach( (gw) -> sb.append( gw.getUnicodeString() ) );

		return( sb.toString() );
	}

	protected int applyFactor( int value, float factor )
	{
		return( (int) Math.round( value * factor ) );
	}

	public Rectangle applyFactor( Rectangle bounds, float factor )
	{
		Rectangle result = null;
		if( bounds != null )
			result = applyFactorNoCopy( new Rectangle( bounds ), factor );

		return( result );
	}

	protected Rectangle applyFactorNoCopy( Rectangle bounds, float factor )
	{
		if( bounds != null )
		{
			bounds.x = applyFactor( bounds.x, factor );
			bounds.y = applyFactor( bounds.y, factor );
			bounds.width = applyFactor( bounds.width, factor );
			bounds.height = applyFactor( bounds.height, factor );
		}

		return( bounds );
	}

	protected BufferedImage getGlyphImage( BufferedImage pageImage, Rectangle bounds )
	{
		return getSubImage( pageImage, bounds ); 
//		return getSubImage( pageImage, new Rectangle( 0, 0, pageImage.getWidth(),
//													pageImage.getHeight() ) ); 
	}

	public BufferedImage getSubImage( BufferedImage image, Rectangle bounds )
	{
		BufferedImage result = new BufferedImage( bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB );
		Graphics2D grp = result.createGraphics();
		grp.drawImage(image, 0, 0, bounds.width, bounds.height,
							bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height,
							null, null);
		grp.dispose();

		return( result );
	}
/*
	public void setBackgroundImages( List<ImageWrapper> listOfImages, GlyphWrapper glyphWrapper )
	{
		if( listOfImages != null )
		{
			for( ImageWrapper image: listOfImages )
			{
				if( isGlyphValid( glyphWrapper ) &&
					( image.getBounds().contains( glyphWrapper.getBounds() ) ||
					image.getBounds().intersects(glyphWrapper.getBounds() ) ) )
				{
					image.setIsBackground(true);
				}
			}
		}
	}
*/

	public boolean imageOverlapsGlyph( ImageWrapper imageWrapper, GlyphWrapper glyphWrapper )
	{
		return( isGlyphValid( glyphWrapper ) &&
					( imageWrapper.getBounds().contains( glyphWrapper.getBounds() ) ||
					imageWrapper.getBounds().intersects(glyphWrapper.getBounds() ) ) );
	}

	public int countGlyphsOverlapping( ImageWrapper imageWrapper, List<GlyphWrapper> glyphs )
	{
		int result = 0;
		if( glyphs != null )
		{
			for( GlyphWrapper glyphWrapper: glyphs )
				if( imageOverlapsGlyph( imageWrapper, glyphWrapper ) )
					result++;
		}

		return( result );
	}

	protected long calculateArea( PdfElementWrapper elem )
	{
		long result = 0;
		if( elem != null )
			result = ((long)elem.getBounds().width) * elem.getBounds().height;

		return( result );
	}

	protected boolean isGlyphValid( GlyphWrapper glyph )
	{
		return( calculateArea(glyph) > 9 );
	}

	protected boolean canBeBackgroundImage( ImageWrapper imageWrapper )
	{
		// 26 * 26
		return( calculateArea(imageWrapper) > 676 );
	}

	public void setBackgroundImages( List<ImageWrapper> listOfImages, List<GlyphWrapper> glyphs )
	{
/*
		if( glyphs != null )
			for( GlyphWrapper glyph: glyphs )
				setBackgroundImages( listOfImages, glyph );
*/
		if( listOfImages != null )
			for( ImageWrapper imageWrapper: listOfImages )
			{
				int number = 0;
				if( canBeBackgroundImage(imageWrapper) )
					number = countGlyphsOverlapping(imageWrapper, glyphs);

				imageWrapper.setNumberOfOverlappingGlyphs(number);
				if( number > 16 )
					imageWrapper.setIsBackground(true);
			}
	}
}
