/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.libpdf.color;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.libpdf.api.GlyphWrapper;
import com.frojasg1.libpdf.api.ImageWrapper;
import com.frojasg1.libpdf.utils.PdfUtils;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PdfPageColorInversor
{
	public BufferedImage invertPdfPageColors( ColorInversor ci, BufferedImage originalImage,
					double zoomFactor, List<ImageWrapper> pageImages,
					List<GlyphWrapper> glyphs )
	{
//		BufferedImage result = ci.invertAndPutOutImage(originalImage, 0.5);
		BufferedImage result = ci.invertImage(originalImage);
		putOutImagesColor( ci, result, zoomFactor, pageImages, glyphs );
		result = ci.putOutImageColor( result, 0.5 );

		return( result );
	}

	protected void putOutImagesColor( ColorInversor ci, BufferedImage result,
									double zoomFactor, List<ImageWrapper> pageImages,
									List<GlyphWrapper> glyphs )
	{
		if( pageImages != null )
			for( ImageWrapper iw: pageImages )
				putOutImageColor( ci, result, zoomFactor, iw, glyphs );
	}

	protected void putOutImageColor( ColorInversor ci, BufferedImage result,
									double zoomFactor, ImageWrapper iw,
									List<GlyphWrapper> glyphs )
	{
		if( hasToPutOut( iw ) )
		{
			copyImage( result, zoomFactor, iw );

			if( iw.getNumberOfOverlappingGlyphs() > 0 )
				for( GlyphWrapper gw: glyphs )
					if( PdfUtils.instance().imageOverlapsGlyph(iw, gw) )
						copyImage( result, zoomFactor, gw );
		}
	}

	protected void copyImage( BufferedImage result, double zoomFactor,
								ImageWrapper iw )
	{
		Rectangle bounds = iw.getBounds();
		BufferedImage image = iw.getImage();
		double imageZoomFactor = getImageZoomFactor( zoomFactor, image, bounds );
//			BufferedImage putOutColorImage = ci.putOutImageColor(iw.getImage(), 0.5);
//			BufferedImage zoomedPutOutImage = ImageFunctions.instance().resizeImage(putOutColorImage, imageZoomFactor);
		BufferedImage zoomedImage = ImageFunctions.instance().resizeImage(image, imageZoomFactor);
		Point zoomedLocation = ViewFunctions.instance().getNewPoint( bounds.getLocation(), zoomFactor );

		copyImage( result, zoomedImage, zoomedLocation );
	}

	protected void copyImage( BufferedImage result, double zoomFactor,
								GlyphWrapper gw )
	{
		Rectangle bounds = gw.getBounds();
		BufferedImage image = gw.getImage();
		Rectangle zoomedBounds = ViewFunctions.instance().calculateNewBounds(bounds,
			null, ViewFunctions.ORIGIN, zoomFactor);

		int fromColor = getFromColor( image );
		int toColor = 0x0;
		int alpha = 255;

		BufferedImage zoomedImage = ImageFunctions.instance().resizeImageAccurately(image,
			zoomedBounds.width, zoomedBounds.height, fromColor, toColor, alpha );

		copyImage( result, zoomedImage, zoomedBounds.getLocation() );
	}

	protected int getFromColor( BufferedImage image )
	{
		Integer[] colorOptions = new Integer[] {
			image.getRGB(0, 0),
			image.getRGB(0, image.getHeight()-1),
			image.getRGB(image.getWidth()-1, 0),
			image.getRGB(image.getWidth()-1, image.getHeight() -1)
		};

		return( ArrayFunctions.instance().getMostRepeated( colorOptions ) );
	}

	protected double getImageZoomFactor( double zoomFactor, BufferedImage image,
										Rectangle bounds )
	{
		return( zoomFactor * ( (double) bounds.getWidth() ) / image.getWidth() );
//		return( zoomFactor * ( (double) bounds.width ) / bounds.getWidth() );
	}

	protected void copyImage( BufferedImage result, BufferedImage zoomedImage,
								Point zoomedLocation )
	{
//		Point zoomedLocation = ViewFunctions.instance().getNewPoint( bounds.getLocation(), zoomFactor );
		Coordinate2D zoomedCoor = new Coordinate2D( zoomedLocation.x, zoomedLocation.y );

//			ImageFunctions.instance().addSpriteToImage(result, zoomedPutOutImage, zoomedCoor);
		ImageFunctions.instance().addSpriteToImage(result, zoomedImage, zoomedCoor);
	}

	protected boolean hasToPutOut( ImageWrapper iw )
	{
		return( !iw.isBackground() );
	}
}
