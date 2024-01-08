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
package com.frojasg1.general.desktop.image;

import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import com.frojasg1.desktop.liblens.graphics.filters.blur.BlurFilterForImage;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.desktop.files.DesktopResourceFunctions;
import com.frojasg1.general.desktop.view.paint.PaintTextContext;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 *
 * @author Usuario
 */
public class ImageFunctions
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageFunctions.class);

	protected static ImageFunctions _instance = null;

	protected static BufferedImage _internalStaticImage = null;
	protected static FontRenderContext _internalStaticFontRenderContext = null;
	
	static
	{
		_internalStaticImage = new BufferedImage( 10, 10, BufferedImage.TYPE_INT_RGB );

		Graphics gc = _internalStaticImage.getGraphics();
		_internalStaticFontRenderContext = ((Graphics2D)gc).getFontRenderContext();
	}

	public static void changeInstance( ImageFunctions inst )
	{
		_instance = inst;
	}

	public static ImageFunctions instance()
	{
		if( _instance == null )
			_instance = new ImageFunctions();

		return( _instance );
	}

	public BufferedImage changeAlpha( BufferedImage original, int alpha )
	{
		BufferedImage result = null;
		
		if( original != null )
		{
			Integer switchColorFrom = null;
			Integer switchColorTo = null;
			result = resizeImage( original, original.getWidth(), original.getHeight(),
									switchColorFrom,
									switchColorTo, alpha );
		}

		return( result );
	}

	public BufferedImage resizeSquaredImage( BufferedImage original, int newWidth, Integer switchColorFrom,
													Integer switchColorTo, Integer alphaForPixelsDifferentFromColorFrom ) throws IllegalArgumentException
	{
		BufferedImage result = resizeImage( original, newWidth, newWidth,
											switchColorFrom, switchColorTo, alphaForPixelsDifferentFromColorFrom );

		return( result );
	}

	public BufferedImage resizeImage( BufferedImage original, int newWidth, int newHeight, Integer switchColorFrom,
											Integer switchColorTo, Integer alphaForPixelsDifferentFromColorFrom ) throws IllegalArgumentException
	{
		return( ResizeImageFast.instance().resizeImage(original, newWidth, newHeight,
														switchColorFrom, switchColorTo,
														alphaForPixelsDifferentFromColorFrom ) );
	}

	public BufferedImage resizeImage( BufferedImage original, int newWidth, int newHeight ) throws IllegalArgumentException
	{
		return( ResizeImageFast.instance().resizeImage( original, newWidth, newHeight ) );
	}

	public BufferedImage resizeImageAccurately( BufferedImage original, int newWidth, int newHeight ) throws IllegalArgumentException
	{
		return( ResizeImageAccurate.instance().resizeImage( original, newWidth, newHeight ) );
	}

	public BufferedImage cropImage( BufferedImage original, Insets insets )
	{
		BufferedImage result = original;
		if( ( original != null ) && ( insets != null ) )
		{
			int width = Math.max( 0, original.getWidth() - insets.left - insets.right );
			int height = Math.max( 0, original.getHeight() - insets.top - insets.bottom );

			result = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

			for( int ii=0; ii<height; ii++ )
				for( int jj=0; jj<width; jj++ )
					result.setRGB(jj, ii, original.getRGB(jj+insets.left, ii+insets.top ) );
		}

		return( result );
	}

	public BufferedImage translateImageColors( BufferedImage original,
		Function<Integer, Integer> colorTranslator) throws IllegalArgumentException
	{
		BufferedImage result = null;
		if( original != null )
			result = ResizeImageFast.instance().resizeImage( original, original.getWidth(),
															original.getHeight(),
															colorTranslator );
		return( result );
	}

	public BufferedImage invertImage( BufferedImage original ) throws IllegalArgumentException
	{
		return( translateImageColors( original, ImageUtilFunctions.instance()::invertColor ) );
	}

	public BufferedImage resizeImage( BufferedImage original, double zoomFactor ) throws IllegalArgumentException
	{
		return( resizeImageGen( ResizeImageFast.instance(), original, zoomFactor ) );
	}

	public BufferedImage resizeImageAccurately( BufferedImage original, double zoomFactor ) throws IllegalArgumentException
	{
		return( resizeImageGen( ResizeImageAccurate.instance(), original, zoomFactor ) );
	}

	protected BufferedImage resizeImageGen( ResizeImageInterface rii, BufferedImage original, double zoomFactor ) throws IllegalArgumentException
	{
		BufferedImage result = null;
		if( original != null )
		{
			int newWidth = IntegerFunctions.zoomValueCeil(original.getWidth(), zoomFactor);
			int newHeight = IntegerFunctions.zoomValueCeil(original.getHeight(), zoomFactor);

			result = rii.resizeImage( original, newWidth, newHeight );
		}

		return( result );
	}

	public BufferedImage resizeImageAccurately( BufferedImage original, int newWidth, int newHeight, Integer switchColorFrom,
											Integer switchColorTo, Integer alphaForPixelsDifferentFromColorFrom ) throws IllegalArgumentException
	{
		return( resizeImageAccurately(original, newWidth, newHeight,
									col -> ImageUtilFunctions.instance().getPixelValue( col, switchColorFrom,
										switchColorTo,
										alphaForPixelsDifferentFromColorFrom ) ) );
	}

	public BufferedImage resizeImageAccurately( BufferedImage original, int newWidth,
												int newHeight,
												Function<Integer, Integer> colorTranslator )
		throws IllegalArgumentException
	{
		return( ResizeImageAccurate.instance().resizeImage(original, newWidth, newHeight,
														colorTranslator ) );
	}

	/**
	* Returns an array of integer pixels in the default RGB color model
	* (TYPE_INT_ARGB) and default sRGB color space,
	* from a portion of the image data.  Color conversion takes
	* place if the default model does not match the image
	* <code>ColorModel</code>.  There are only 8-bits of precision for
	* each color component in the returned data when
	* using this method.  With a specified coordinate (x,&nbsp;y) in the
	* image, the ARGB pixel can be accessed in this way:
	* </p>
	*
	* <pre>
	*    pixel   = rgbArray[offset + (y-startY)*scansize + (x-startX)]; </pre>
	*
	* <p>
	*
	* An <code>ArrayOutOfBoundsException</code> may be thrown
	* if the region is not in bounds.
	* However, explicit bounds checking is not guaranteed.
	*
	* @param startX      the starting X coordinate
	* @param startY      the starting Y coordinate
	* @param w           width of region
	* @param h           height of region
	* @param rgbArray    if not <code>null</code>, the rgb pixels are
	*          written here
	* @param offset      offset into the <code>rgbArray</code>
	* @param scansize    scanline stride for the <code>rgbArray</code>
	* @return            array of RGB pixels.
	* @see #setRGB(int, int, int)
	* @see #setRGB(int, int, int, int, int[], int, int)
	*/
	public int[] getRGB(int startX, int startY, int w, int h,
								BufferedImage bi )
	{
		ColorModel colorModel = bi.getColorModel();

		Raster raster = bi.getRaster();
//		WritableRaster raster = colorModel.createCompatibleWritableRaster( bi.getWidth(), bi.getHeight() );

		int scansize = w;
		int offset =0;
		
		int yoff  = offset;
		int off;
		Object data;
		int nbands = raster.getNumBands();
		int dataType = raster.getDataBuffer().getDataType();
		switch (dataType)
		{
			case DataBuffer.TYPE_BYTE:
			data = new byte[nbands];
				break;
			case DataBuffer.TYPE_USHORT:
				data = new short[nbands];
				break;
			case DataBuffer.TYPE_INT:
				data = new int[nbands];
				break;
			case DataBuffer.TYPE_FLOAT:
				data = new float[nbands];
				break;
			case DataBuffer.TYPE_DOUBLE:
				data = new double[nbands];
				break;
			default:
				throw new IllegalArgumentException("Unknown data buffer type: "+
													dataType);
		}

		int[] rgbArray = new int[offset+h*scansize];

		for (int y = startY; y < startY+h; y++, yoff+=scansize)
		{
			off = yoff;
			for (int x = startX; x < startX+w; x++)
			{
				if( (x>=0) && (x<bi.getWidth()) && (y>=0) && (y<bi.getHeight() ) )
				{
					rgbArray[off++] = colorModel.getRGB(raster.getDataElements(	x,
																				y,
																				data));
				}
				else
				{
					rgbArray[off++] = 0;
				}
		   }
	   }

		return rgbArray;
	}

	public int[] getRGB(int startX, int startY, int w, int h,
								BufferedImage bi, Dimension size )
	{
		BufferedImage image = getImageWithSize( bi, size );

		return( getRGB( startX, startY, w, h, image ) );
	}

	public BufferedImage getImageWithSize( BufferedImage bi, Dimension size )
	{
		BufferedImage result = bi;
		if( ( bi != null ) && ( size != null ) )
		{
			if( ( bi.getWidth() != size.getWidth() ) ||
				( bi.getHeight() != size.getHeight() ) )
			{
				result = createImage( size.width, size.height );
				addSpriteToImage( result, bi, new Coordinate2D(0,0) );
			}
		}

		return( result );
	}

	public BufferedImage createImage( int width, int height )
	{
		return( new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB ) );
	}

	public int getColorOfPixel( int xx, int yy, int width, int[] pixelColors )
	{
		int offsetPixelColors = xx + yy * width;

		return( pixelColors[offsetPixelColors] );
	}

	public long getDistance( BufferedImage image1, BufferedImage image2 )
	{
		int xx = 0;
		int width = IntegerFunctions.min( image1.getWidth(), image2.getWidth() );
		int yy = 0;
		int height = IntegerFunctions.min( image1.getHeight(), image2.getHeight() );

		int[] array1 = this.getRGB(xx, yy, width, height, image1 );
		int[] array2 = this.getRGB(xx, yy, width, height, image2 );

		long result = 0;
		for( int ii=0; ii<array1.length; ii++ )
			result = result + this.getDistanceAddingDistances( array1[ii], array2[ii] );

		return( result );
	}

	public int getDistanceAddingDistances( int color1, int color2 )
	{
		int da = abs( (color1 & 0xFF000000) >>> 24 - (color2 & 0xFF000000) >>> 24 );
		int dr = abs( (color1 & 0xFF0000) >>> 16 - (color2 & 0xFF0000) >>> 16 );
		int dg = abs( (color1 & 0xFF00) >>> 8 - (color2 & 0xFF00) >>> 8 );
		int db = abs( (color1 & 0xFF) - (color2 & 0xFF) );

		return( da + dr + dg + db );
	}

	public int abs( int value )
	{
		return( IntegerFunctions.abs( value ) );
	}

	public Rectangle2D getImageWrappedBoundsForString( Font font, String str,
														FontRenderContext frc )
	{
		Rectangle2D result = null;

		if( ( str != null ) && ( frc != null ) && ( font != null ) )
		{
			result = font.getStringBounds( str, frc);
		}
		else
		{
			result = new Rectangle(0,0);
		}

		return( result );
	}

	public Rectangle2D getImageWrappedBoundsForString( Font font, String str )
	{
		return( getImageWrappedBoundsForString( font, str, _internalStaticFontRenderContext) );
	}

	public void paintStringCentered( Graphics gc, Component comp,
									PaintTextContext textContext, Rectangle bounds )
	{
		String text = ( textContext != null ) ? textContext.getText() : null;
		if( text != null )
		{
			textContext.update(comp);

			paintStringCentered( gc, textContext.getFont(), text,
								textContext.getForegroundColor(), bounds,
								textContext.getBackgroundColor() );
		}
	}

	public void paintStringCentered( Graphics gc, Font font, String str, Color textColor, Rectangle bounds, Color backgroundColor )
	{
		if( backgroundColor != null )
		{
			gc.setColor( backgroundColor );
			gc.fillRect( (int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight() );
		}

		Point centralPoint = new Point( (int) bounds.getCenterX(), (int) bounds.getCenterY() );

		paintStringCentered( gc, font, str, textColor, centralPoint );
	}

	public void paintStringCentered( Graphics gc, Font font, String str, Color textColor,
											Point centralPoint )
	{
		if( textColor != null ) gc.setColor( textColor );

		if( str != null )
		{
			FontRenderContext frc = ((Graphics2D)gc).getFontRenderContext();
			Rectangle2D wrappedBounds = font.getStringBounds(str, frc);
			int x1 = (int) Math.floor( centralPoint.getX() - wrappedBounds.getWidth() / 2 );
			int y1 = (int) Math.floor( centralPoint.getY() + wrappedBounds.getHeight() / 2 ) - 3;

			paintStringLeftTopJustified( gc, font, str, textColor, x1, y1 );
		}
	}

	public void paintStringCenteredInsideBounds( Graphics gc, Font font, String str, Color textColor,
													Point centralPoint, int width, int height )
	{
		if( textColor != null ) gc.setColor( textColor );

		if( str != null )
		{
			FontRenderContext frc = ((Graphics2D)gc).getFontRenderContext();
			Rectangle2D wrappedBounds = font.getStringBounds(str, frc);
			int x1 = (int) Math.floor( centralPoint.getX() - wrappedBounds.getWidth() / 2 );
			int y1 = (int) Math.floor( centralPoint.getY() + wrappedBounds.getHeight() / 2 ) - 3;

			x1 = IntegerFunctions.limit( x1, 0, width - (int) wrappedBounds.getWidth() );
			y1 = IntegerFunctions.limit( y1, 0, height - (int) wrappedBounds.getHeight() );

			paintStringLeftTopJustified( gc, font, str, textColor, x1, y1 );
		}
	}

	public void paintStringLeftTopJustified( Graphics gc, Font font, String str, Color textColor,
											Point topLeftPoint )
	{
		paintStringLeftTopJustified( gc, font, str, textColor, topLeftPoint.x, topLeftPoint.y );
	}

	public void paintStringLeftTopJustified( Graphics gc, Font font, String str, Color textColor,
											int xx, int yy )
	{
		if( textColor != null ) gc.setColor( textColor );

		if( str != null )
		{
			gc.setFont( font );
			gc.drawString( str, xx, yy);
		}
	}

	public void paintClippedImage( Component comp, Graphics gc, BufferedImage bi, Point leftUpperCorner )
	{
		if( bi != null )
		{
			// if some part of the piece is inside the visible zone (the screen)
			if( ( leftUpperCorner.getX() < comp.getWidth() ) && ( leftUpperCorner.getX() + bi.getWidth() >= 0 ) &&
				( leftUpperCorner.getY() < comp.getHeight() ) && ( leftUpperCorner.getY() + bi.getHeight() >= 0 ) )
			{
				int dx1 = IntegerFunctions.max( 0, (int) leftUpperCorner.getX() );
				int dy1 = IntegerFunctions.max( 0, (int) leftUpperCorner.getY() );
				int dx2 = IntegerFunctions.min( comp.getWidth(), (int) ( leftUpperCorner.getX() + bi.getWidth() ) );
				int dy2 = IntegerFunctions.min( comp.getHeight(), (int) ( leftUpperCorner.getY() + bi.getHeight() ) );

				int sx1 = dx1 - (int) leftUpperCorner.getX();
				int sy1 = dy1 - (int) leftUpperCorner.getY();
				int sx2 = sx1 + dx2 - dx1;
				int sy2 = sy1 + dy2 - dy1;

				gc.drawImage( bi, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null );
			}
		}
	}

	public void drawRect( Graphics gc, int xx, int yy, int width, int height, Color color, int thick )
	{
		gc.setColor( color );

		if( thick > 0 )
		{
			for( int ii=0; ii<thick; ii++ )
			{
				gc.drawRect( xx - ii, yy - ii,	width + 2 * ii, height + 2 * ii );
			}
		}
		else if( thick < 0 )
		{
			for( int ii=thick; ii<0; ii++ )
			{
				gc.drawRect( xx - ii, yy - ii,	width + 2 * ii, height + 2 * ii );
			}
		}
	}

	public void drawBoldStraightLine( Graphics gc, int x1, int y1, int x2, int y2, int thick )
	{
		int increment = IntegerFunctions.sgn(thick);
		int xDelta = 0;
		int yDelta = 0;
		
		if( x1 == x2 )
			xDelta = increment;
		else if( y1 == y2 )
			yDelta = increment;

		int to = IntegerFunctions.abs(thick);
		for( int ii=0; ii<to; ii++, x1+=xDelta, x2+=xDelta, y1+=yDelta, y2+=yDelta )
			gc.drawLine( x1, y1, x2, y2 );
	}

	public BufferedImage loadImageFromJar( String resourceFileName ) throws IOException
	{
		return(  ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream(resourceFileName) ) );
	}

	public void addARGB( double[] resultArgbArray, int argb, double factor )
	{
		resultArgbArray[3] += ( ( argb >> 24 ) & 0xFF ) * factor;
		resultArgbArray[2] += ( ( argb >> 16 ) & 0xFF ) * factor;
		resultArgbArray[1] += ( ( argb >> 8 ) & 0xFF ) * factor;
		resultArgbArray[0] += ( argb & 0xFF ) * factor;
	}

	public int limitComponent( double value )
	{
		return( IntegerFunctions.min( 255, IntegerFunctions.round( value ) ) );
	}

	public int getARGB( int alpha, int red, int green, int blue )
	{
		alpha = limitComponent( alpha );
		red = limitComponent( red );
		green = limitComponent( green );
		blue = limitComponent( blue );

		int result = ( alpha << 24 ) |
					( ( red & 0xff ) << 16 ) |
					( ( green & 0xff ) << 8 ) |
					( blue & 0xff );

		return( result );
	}

	public int getARGB( double[] components )
	{
		int alpha = limitComponent( components[3] );
		int red = limitComponent( components[2] );
		int green = limitComponent( components[1] );
		int blue = limitComponent( components[0] );

		return( getARGB( alpha, red, green, blue ) );
	}

	public PixelComponents[][] getPixelComponents( BufferedImage image, boolean signedComponents )
	{
		PixelComponents[][] result = new PixelComponents[image.getWidth()][image.getHeight()];

		for( int xx=0; xx<image.getWidth(); xx++ )
			for( int yy=0; yy<image.getHeight(); yy++ )
				result[xx][yy] = new PixelComponents( image.getRGB(xx, yy), signedComponents );

		return( result );
	}

	public PixelComponents[][] subtract( PixelComponents[][] pixels, PixelComponents[][] other )
	{
		PixelComponents[][] result = new PixelComponents[pixels.length][pixels[0].length];

		for( int xx=0; xx<pixels.length; xx++ )
			for( int yy=0; yy<pixels[0].length; yy++ )
				result[xx][yy] = pixels[xx][yy].subtract( other[xx][yy] );

		return( result );
	}

	public double[] createEmptyComponents()
	{
		double[] components = new double[4];
		resetComponents( components );

		return( components );
	}

	public void resetComponents( double[] components )
	{
		components[0] = 0.0D;
		components[1] = 0.0D;
		components[2] = 0.0D;
		components[3] = 0.0D;
	}

	public FontRenderContext getFontRenderContext( Component comp )
	{
		FontRenderContext result = null;
		if( comp != null )
		{
			Graphics grp = comp.getGraphics();
			if( grp instanceof Graphics2D )
			{
				result = ( ( Graphics2D ) grp ).getFontRenderContext();
			}
		}

		return( result );
	}

	public BufferedImage addSpriteToImage( BufferedImage bigImage, BufferedImage sprite,
											Coordinate2D position )
	{
		BufferedImage result = bigImage;
		
		int dx1 = IntegerFunctions.max( position.M_getX(), 0 );
		int dy1 = IntegerFunctions.max( position.M_getY(), 0 );
		int dx2 = IntegerFunctions.min( position.M_getX() + sprite.getWidth(), bigImage.getWidth() );
		int dy2 = IntegerFunctions.min( position.M_getY() + sprite.getHeight(), bigImage.getHeight() );

		int sx1 = IntegerFunctions.min( dx1 - position.M_getX(), sprite.getWidth() );
		int sy1 = IntegerFunctions.min( dy1 - position.M_getY(), sprite.getHeight() );
		int sx2 = IntegerFunctions.min( sx1 + dx2 - dx1, sprite.getWidth() );
		int sy2 = IntegerFunctions.min( sy1 + dy2 - dy1, sprite.getHeight() );

		if( ( dx2 > dx1 ) && ( dy2 > dy1 ) &&
			( sx2 > sx1 ) && ( sy2 > sy1 ) )
		{
			Graphics2D graphics = result.createGraphics();

			graphics.drawImage( sprite, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null );

			graphics.dispose();
		}

		return( result );
	}

	protected String getDirName( String fileName )
	{
		String result = FileFunctions.instance().getDirName(fileName);
		if( ( result != null ) && result.equals( "." ) )
			result = "";

		return( result );
	}

	protected String createRegexForReadingIconFileName( String baseFileName )
	{
		return( createRegexForIconGen( baseFileName, "([0-9]+)x[0-9]+" ) );
	}

	protected String createRegexForIconGen( String baseFileName, String suffix )
	{
		String result = null;
		if( baseFileName != null )
		{
			String baseName = FileFunctions.instance().getBaseName(baseFileName);
			result = "^" + FileFunctions.instance().appendSuffixToFileNameBeforeLastExtension(baseName, suffix ) + "$";
		}

		return( result );
	}

	protected int getWidth( Pattern pattern, String iconFileName )
	{
		Integer result = IntegerFunctions.parseInt( pattern.matcher(iconFileName).replaceFirst( "$1" ) );
		if( result == null )
			result = Integer.MAX_VALUE;

		return( result );
	}

	protected void orderByResolution( Pattern pattern, List<String> list )
	{
		Collections.sort( list, ( s1, s2 ) -> getWidth(pattern, s1) - getWidth(pattern, s2) );
	}

	protected boolean iconFileNameMatches( Pattern pattern, String fileName )
	{
		return( pattern.matcher( FileFunctions.instance().getBaseName(fileName) ).matches() );
	}

	// https://stackoverflow.com/questions/40601279/java-get-all-files-in-a-package
	public List<String> getListOfIconFileNames( Pattern pattern, String baseName )
	{
		String dirName = getDirName( baseName );
		List<String> result = null;

		try
		{
			result = Files.walk(Paths.get(dirName))
								.map(Path::getFileName)
								.map(Path::toString)
								.filter(
									n -> iconFileNameMatches( pattern, n)
								)
								.collect(Collectors.toList());

			if( !result.isEmpty() )
			{
				orderByResolution( pattern, result );

				result = addPrefixToAllElements( result, addSeparatorToDirName( dirName )  );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		if( result == null )
			result = new ArrayList<>();

		if( result.isEmpty() )
			result.add( baseName );

		return( result );
	}

	protected String addSeparatorToDirName( String dirName )
	{
		return( FileFunctions.instance().addSeparatorToDirName(dirName) );
	}

	protected List<String> addPrefixToAllElements( List<String> inputList, String prefix )
	{
		return( CollectionFunctions.instance().transformList(inputList, (s) -> prefix + s ) );
	}

	public BufferedImage safeLoadImage( String fileName )
	{
		BufferedImage result = null;
		if( fileName != null )
		{
			InputStream in = FileFunctions.instance().getFileInputStream(fileName);
			result = loadImage(in);
			if( result == null )
			{
				in = DesktopResourceFunctions.instance().getInputStreamOfResource(fileName);
				result = loadImage(in);
			}
		}

		return( result );
	}

	public BufferedImage loadImage( String longFileName ) throws IOException
	{
		BufferedImage result = null;
		File file =  new File(longFileName);
		if( FileFunctions.instance().isFile(longFileName) )
			result = ImageIO.read(file);

		return( result );
	}

	protected List<BufferedImage> loadIcons( List<String> fileNames )
	{
		List<BufferedImage> result = new ArrayList<>();
		for( String fileName: fileNames )
		{
			BufferedImage image = safeLoadImage( fileName );
			if( image != null )
				result.add( image );
		}

		return( result );
	}

	public List<String> getListOfIconResources( Pattern pattern, String baseName )
	{
		List<String> result = new ArrayList<>();

		try
		{
			String dirName = getDirName( baseName );
			String baseBaseName = FileFunctions.instance().getBaseName( baseName );
			Resource[] array = DesktopResourceFunctions.instance().getResources(dirName);

			boolean baseNameFound = false;
			for( Resource resource: array )
			{
				String fileName = FileFunctions.instance().getBaseName( resource.getFilename() );
				if( fileName.equals(baseBaseName) )
					baseNameFound = true;

				if( iconFileNameMatches( pattern, fileName) )
					result.add( fileName );
			}

			if( result.isEmpty() && baseNameFound )
			{
				result.add( baseBaseName );
			}

			result = addPrefixToAllElements( result, FileFunctions.instance().normalizeFolderSeparator( addSeparatorToDirName( dirName ) ) );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		if( result.isEmpty() )
			result = null;

		return( result );
	}

	public List<BufferedImage> getListOfIcons( String baseName )
	{
		String regex = createRegexForReadingIconFileName( baseName );
		Pattern pattern = Pattern.compile(regex);

		List<String> fileNames = getListOfIconResources( pattern, baseName );
		if( fileNames == null )
			fileNames = getListOfIconFileNames( pattern, baseName );

		List<BufferedImage> result = loadIcons( fileNames );

		return( result );
	}


	public BufferedImage loadImage( InputStream in )
	{
		BufferedImage result = null;

		if( in != null )
		{
			try( InputStream inputStream = in; )
			{
				result = ImageIO.read(inputStream);
			}
			catch( Exception ex )
			{
				LOGGER.warn( "error decoding image from InputStream: {}", in, ex );
			}
		}

		return( result );
	}

	public void saveImage( BufferedImage image, String fileName, String type ) throws IOException
	{
		File outputfile = new File(fileName);
		ImageIO.write( image, type, outputfile );
	}

	public void saveImage( BufferedImage image, String fileName ) throws IOException
	{
		String type = FileFunctions.instance().getExtension( fileName ).toLowerCase();
		saveImage( image, fileName, type );
	}

	// https://examples.javacodegeeks.com/desktop-java/imageio/list-read-write-supported-image-formats/
	public String[] getReaderImageFormatNames()
	{
		return( ImageIO.getReaderFormatNames() );
	}

	// https://examples.javacodegeeks.com/desktop-java/imageio/list-read-write-supported-image-formats/
	public String[] getWriterImageFormatNames()
	{
		return( ImageIO.getWriterFormatNames() );
	}

	// https://stackoverflow.com/questions/7044521/renderedimage-to-bufferedimage-for-multipage-tiff-reading
	public BufferedImage renderedImageToBufferedImage( RenderedImage img )
	{
		BufferedImage result = null;
		if( img != null )
		{
			if (img instanceof BufferedImage) {
				result = (BufferedImage)img;  
			}
			else
			{
				ColorModel cm = img.getColorModel();
				int width = img.getWidth();
				int height = img.getHeight();
				WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
				boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
				Hashtable properties = new Hashtable();
				String[] keys = img.getPropertyNames();
				if (keys!=null) {
					for (int i = 0; i < keys.length; i++) {
						properties.put(keys[i], img.getProperty(keys[i]));
					}
				}
				result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
				img.copyData(raster);
			}
		}

		return result;
	}

	// https://stackoverflow.com/questions/41468661/sobel-edge-detecting-program-in-java
    public short  getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        //from https://en.wikipedia.org/wiki/Grayscale, calculating luminance
        short gray = (short)(0.2126 * r + 0.7152 * g + 0.0722 * b);
        //int gray = (r + g + b) / 3;

        return gray;
    }

	public BufferedImage getBlurredImage( BufferedImage image )
	{
		BlurFilterForImage filter = new BlurFilterForImage();
		return( filter.applyFilter(image) );
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

	public void saveTiffImage( BufferedImage image, String longFileName ) throws IOException
	{
		saveImage(image, "TIFF", longFileName);
	}

	public BufferedImage loadImage( byte[] imageFileBytes )
	{
		BufferedImage result = null;
		if( imageFileBytes != null )
			result = loadImage( new ByteArrayInputStream( imageFileBytes ) );

		return( result );
	}

	public static BufferedImage getResourceImage( String resourceImageName ) throws IOException
	{
		BufferedImage result = ImageIO.read( ClassLoader.getSystemClassLoader().getResourceAsStream( resourceImageName ) );
		
		return( result );
	}
}
