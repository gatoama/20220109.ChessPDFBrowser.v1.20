/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.image.builders.helpers;

import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ImageBuilderHelper {
	
	protected static class LazyHolder
	{
		public static final ImageBuilderHelper INSTANCE = new ImageBuilderHelper();
	}

	public static ImageBuilderHelper instance()
	{
		return( LazyHolder.INSTANCE );
	}

	public void drawRing( BufferedImage image, int cx, int cy, int extRadius, int internalRadius, Color color )
	{
		if( internalRadius < 0 )
			internalRadius = 0;

		int rgb = color.getRGB();

		int yyLimit = (int) ( extRadius * Math.sin( Math.PI / 4 ) );

		for( int yy=0; yy<=yyLimit; yy++ )
		{
			int finalXX = (int) ( extRadius * Math.cos( Math.asin( yy/ (double) extRadius ) ) );
			int initialXX = 0;
			if( yy <= internalRadius )
				initialXX = IntegerFunctions.max( yy,
										IntegerFunctions.round( internalRadius * Math.cos( Math.asin( yy / (double) internalRadius ) ) )
												);
			else
				initialXX = yy;

			for( int xx=initialXX; xx<=finalXX; xx++ )
				setSimmetricalPixelColor( image, cx, cy, xx, yy, rgb );
		}
	}

	protected void setSimmetricalPixelColor( BufferedImage image, int cx, int cy,
											int xx, int yy, int rgb )
	{
		image.setRGB( cx + xx, cy + yy, rgb );
		image.setRGB( cx + xx, cy - yy, rgb );
		image.setRGB( cx - xx, cy + yy, rgb );
		image.setRGB( cx - xx, cy - yy, rgb );
		image.setRGB( cx + yy, cy + xx, rgb );
		image.setRGB( cx + yy, cy - xx, rgb );
		image.setRGB( cx - yy, cy + xx, rgb );
		image.setRGB( cx - yy, cy - xx, rgb );
	}
}
