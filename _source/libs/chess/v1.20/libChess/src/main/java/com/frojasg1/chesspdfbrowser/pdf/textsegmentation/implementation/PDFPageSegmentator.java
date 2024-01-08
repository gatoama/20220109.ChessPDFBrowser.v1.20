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
package com.frojasg1.chesspdfbrowser.pdf.textsegmentation.implementation;

import com.frojasg1.chesspdfbrowser.engine.io.notation.ChessMoveAlgebraicNotation;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.InputElementResult;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputTextLine;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.libpdf.api.PdfDocumentWrapper;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.PDFPageSegmentatorInterface;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.PageSegmentationResult;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.SegmentKey;
import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import static com.frojasg1.chesspdfbrowser.pdf.textsegmentation.implementation.ImageSegmentator.TOLERANCE_TO_MATCH_COMPONENT_OF_PIXEL_FOR_BACKGROUND_GAP;
import static com.frojasg1.chesspdfbrowser.pdf.textsegmentation.implementation.ImageSegmentator.getPixelComponents;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.progress.UpdatingProgress;
import com.frojasg1.general.string.NewLineSplitter;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.libpdf.api.GlyphWrapper;
import com.frojasg1.libpdf.api.ImageWrapper;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Usuario
 */
public class PDFPageSegmentator implements PDFPageSegmentatorInterface
{
	protected Map<SumPageKey, SummarizedPage> _map = null;

	protected ChessViewConfiguration _configuration = null;

	protected PdfDocumentWrapper _pdfDocument = null;

	public PDFPageSegmentator( ChessViewConfiguration configuration )
	{
		_configuration = configuration;
		_map = new HashMap<>();
	}

	public SummarizedPage getSummarizedPage( BufferedImage image, int pageIndex )
	{
		Dimension size = new Dimension( (int) image.getWidth(),
										(int) image.getHeight() );

		SummarizedPage result = chooseBestSummarizedPage(image, getSummarizedPage(size, pageIndex ),
															getSummarizedPage(size, pageIndex + 1 ) );
/*
		if( result == null )
			result = createSummarizedPageOfSinglePage( image );
*/
		return( result );
	}

	protected SummarizedPage chooseBestSummarizedPage( BufferedImage image,
														SummarizedPage one,
														SummarizedPage two )
	{
		double distance1 = calculateDistance( image, one );
		double distance2 = calculateDistance( image, two );
		
		SummarizedPage result = ( distance1 > distance2 ) ? two : one;

		return( result );
	}

	protected double calculateDistance( BufferedImage image, SummarizedPage sp )
	{
		int[] imagePixels = ImageFunctions.instance().getRGB(0, 0, image.getWidth(),
															image.getHeight(), image);

		double result = 0;
		if( sp != null )
			result = calculateDistance( imagePixels, sp.getSummarizedPixels() );

		return( result );
	}

	protected int abs( int value )
	{
		return( IntegerFunctions.abs( value ) );
	}

	protected SummarizedPage createSummarizedPageOfSinglePage( BufferedImage image )
	{
		return( new SummarizedPage( image ) );
	}

	protected double calculateDistance( int[] imagePixels, PixelComponents[][] pcArr )
	{
		double result = 1000D;

		if( pcArr != null )
		{
			int tolerance = ImageSegmentator.TOLERANCE_TO_MATCH_COMPONENT_OF_PIXEL_FOR_BACKGROUND_GAP;

			int width = pcArr.length;
			int height = pcArr[0].length;
			int offset = 0;
			int count = 0;
			int count2 = 0;
			int countMatches = 0;
			for( int yy=0; yy<height; yy++ )
				for( int xx=0; xx<width; xx++, offset++ )
				{
					PixelComponents summarizedPixel = pcArr[xx][yy];

					if( summarizedPixel != null )
					{
						short[] comp = getPixelComponents( imagePixels[offset] );

						if( ( comp[0] > tolerance ) || ( comp[1] > tolerance ) || ( comp[2] > tolerance ) )
						{
							if( summarizedPixel.nearlyEquals(comp, TOLERANCE_TO_MATCH_COMPONENT_OF_PIXEL_FOR_BACKGROUND_GAP) )
							{
								countMatches++;
							}
							count2++;
						}
	//				}
	/*
					if( pc != null )
					{
	//					int pixel = imagePixels[offset];
						int pixel = ImageFunctions.instance().getColorOfPixel(xx, yy, width, imagePixels);

						int rr = ( pixel & 0xFF0000 ) >>> 16;
						int gg = ( pixel & 0xFF00 ) >>> 8;
						int bb = ( pixel & 0xFF );

						if( ( rr > 25 ) || ( gg > 25 ) || ( bb > 25 ) )
						{
							int dr = abs( pc.getRed() - rr );
							int dg = abs( pc.getGreen() - gg );
							int db = abs( pc.getRed() - bb );

							result = result + dr + dg + db;

							if( ( dr < tolerance ) && ( dg < tolerance ) && ( db < tolerance ) )
								countMatches++;
							count2++;
						}
	*/
						count++;
					}
				}
			result = ( 1 - ( (double) countMatches ) / count2 );
		}

		return( result );
	}

	public SummarizedPage getSummarizedPage( Dimension dim, int pageIndex )
	{
//		int pageIndex = pageIndex - 1;
//		SumPageKey key = new SumPageKey( (pageIndex%2==0), dim );
		SumPageKey key = new SumPageKey( (pageIndex%2==0), dim );

		return( get( key ) );
	}

	protected SummarizedPage get( SumPageKey key )
	{
		SummarizedPage result = null;

		for( Map.Entry< SumPageKey, SummarizedPage > entry: _map.entrySet() )
		{
			if( matches( key, entry.getKey() ) )
			{
				result = entry.getValue();
				break;
			}
		}

		return( result );
	}

	protected boolean matches( SumPageKey key1, SumPageKey key2 )
	{
		boolean result = ( key1.isEvenPage() == key2.isEvenPage() ) && matches( key1.getDimension(), key2.getDimension() );

		return( result );
	}

	protected boolean matches( int dim1, int dim2 )
	{
		return( IntegerFunctions.abs( dim1 - dim2 ) <= SummarizedPage.ALLOWED_GAP_FOR_DIMENSION );
	}

	protected boolean matches( Dimension dim1, Dimension dim2 )
	{
		boolean result = false;

		if( ( dim1 != null ) && ( dim2 != null ) )
		{
			result = matches( dim1.width, dim2.width ) && matches( dim1.height, dim2.height );
		}

		return( result );
	}

	public void setChessViewConfiguration( ChessViewConfiguration configuration )
	{
		_configuration = configuration;
	}
	
	public PdfDocumentWrapper getPDDocument()
	{
		return( _pdfDocument );
	}
	
	public int getNumberOfPages()
	{
		int result = -1;
		
		if( _pdfDocument != null )
			result = _pdfDocument.getNumberOfPages();

		return( result );
	}


	public void initialize(PdfDocumentWrapper document,
							int minimumNumberOfPagesToCheck,
							int maximumNumberOfPagesToCheck,
							UpdatingProgress up )
					throws IOException, CancellationException
	{
		_pdfDocument = document;

		if( up != null )
			up.up_childStarts();

		int startingPage = getStartingPage( document.getNumberOfPages() );

		for( int ii=startingPage; ii<document.getNumberOfPages(); ii++ )
		{
			if( isChessGamePage( ii ) )
			{
				Dimension size = _pdfDocument.getSizeOfPage(ii);

				SumPageKey key = new SumPageKey( (ii%2==0), size );
				SummarizedPage sp = get( key );

				if( sp == null )
				{
					sp = new SummarizedPage();
					sp.setNumberOfPagesToSummarize( minimumNumberOfPagesToCheck, maximumNumberOfPagesToCheck );
					_map.put( key, sp );
				}

				if( ( sp != null ) && !sp.isComplete() )
				{
					BufferedImage image = _pdfDocument.renderImageWithDPIForBackground( ii, 72F );

					sp.addPage(image);
				}
			}

			if( up != null )
				up.up_updateProgressFromChild( (double) ii / document.getNumberOfPages() );
		}

		for( SumPageKey key: _map.keySet() )
		{
			SummarizedPage sp = get( key );
			sp.summarize();
		}

		if( up != null )
			up.up_childEnds();
	}

	protected int getStartingPage( int totalNumberOfPages )
	{
		int result = 0;
/*
		if( totalNumberOfPages > 100 )
			result = 15;
		else if( totalNumberOfPages > 40 )
			result = 5;
		else if( totalNumberOfPages > 20 )
			result = 3;
		else if( totalNumberOfPages > 3 )
			result = 1;
*/
		return( result );
	}

	@Override
	public PageSegmentationResult getSegments( int pageNumber ) throws IOException
	{
		PageSegmentationResult result = new PageSegmentationResult();

//		Dimension size = new Dimension( (int) page.getBBox().getWidth(),
//										(int) page.getBBox().getHeight() );

		BufferedImage image = _pdfDocument.renderImageWithDPI( pageNumber, 72F );

		SummarizedPage sp = getSummarizedPage( image, pageNumber );

		if( sp != null )
		{
			ImageSegmentator is = new ImageSegmentator( sp, image, null );
			List<Rectangle> listOfSegments = is.getAreaSegments();

			result.setListOfSegmentedRegions( listOfSegments );
			result.setCouldBeValidated(true);
		}
		else
		{
			List<Rectangle> listOfSegments = Arrays.asList( new Rectangle( 0, 0, image.getWidth(), image.getHeight() ) );
			result.setListOfSegmentedRegions( listOfSegments );
			result.setCouldBeValidated(true);
		}

		return( result );
	}

	@Override
	public String getTextOfPage(int pageIndex) throws IOException
	{
		PageSegmentationResult psr = getSegments(pageIndex );
		
		List<Rectangle> segments = psr.geListOfSegmentedRegions();

		String result = _pdfDocument.getTextOfPage(pageIndex, segments );
		return( result );
	}

	protected boolean isChessGamePage( int pageIndex ) throws IOException
	{
		boolean result = false;

		_pdfDocument.getSizeOfPage(pageIndex);
		Dimension size = _pdfDocument.getSizeOfPage(pageIndex);
		Rectangle rect = new Rectangle( 0, 0,
										size.width, size.height );
		List<Rectangle> segments = new ArrayList<>();
		segments.add( rect );

		String text = _pdfDocument.getTextOfPage( pageIndex, segments );

		result = isChessGamePage( text );

		return( result );
	}

	protected boolean isChessGamePage( String text )
	{
		LiteParserValidator lpv = new LiteParserValidator( text, _configuration );
		
		return( lpv.isChessGameText() );
	}

	@Override
	public InputElementResult getInputResultOfPage(int pageIndex) throws IOException
	{
		InputElementResult result = new InputElementResult();

		boolean hasToAddPageNumber = false;
		int startingPageIndex = -1;
		int endPageIndex = -1;
		result.init( hasToAddPageNumber, startingPageIndex, endPageIndex );

		PageSegmentationResult psr = getSegments(pageIndex );
		List<Rectangle> segments = psr.geListOfSegmentedRegions();

		boolean getCharacterImages = false;
		List<GlyphWrapper> glyphs = _pdfDocument.getGlyphsOfPage(pageIndex, getCharacterImages, null);
		List<ImageWrapper> images = _pdfDocument.getImagesOfPage(pageIndex);

		for( Rectangle segment: segments )
		{
			SegmentKey sk = new SegmentKey( pageIndex, segment );
			addInputElementsOfSegment( sk, glyphs, images, result );
		}

/*
		String text = getTextOfPage(pageIndex);


		result.getInputElementList().addAll(
			NewLineSplitter.instance().split( text ).stream()
				.map( line -> new InputTextLine( line ) )
				.collect( Collectors.toList() ) );
*/
		return( result );
	}

	protected void addInputElementsOfSegment( SegmentKey sk,
			List<GlyphWrapper> glyphs, List<ImageWrapper> images,
			InputElementResult result ) throws IOException
	{
		int pageIndex = sk.getPageIndex();
		Rectangle segment = sk.getSegment();

		String text = _pdfDocument.getTextOfPage(pageIndex, Arrays.asList( segment ) );

		Rectangle verticalScannerRect = new Rectangle( segment.x, segment.y, segment.width, 7 );
		List<String> lines = NewLineSplitter.instance().split( text );
		Rectangle imageScannerRect = new Rectangle( segment.x, segment.y, segment.width, 1 );
		int lastYY = segment.y;
		int topYY = segment.y + segment.height;
		for( String line: lines )
		{
			lookForLine( line, glyphs, verticalScannerRect, topYY );
			imageScannerRect.y = lastYY;
			imageScannerRect.height = verticalScannerRect.y - lastYY;
			lastYY = verticalScannerRect.y;

			addImageIfNecessary( images, glyphs, verticalScannerRect, imageScannerRect, result, sk );

			result.getInputElementList().add( new InputTextLine( line, sk ) );
		}

		int lastHeight = topYY - lastYY;
		if( lastHeight > 0 )
		{
			imageScannerRect.y = lastYY;
			imageScannerRect.height = lastHeight;

			addImageIfNecessary( images, glyphs, verticalScannerRect, imageScannerRect, result, sk );
		}
	}

	protected void addImageIfNecessary( List<ImageWrapper> images, List<GlyphWrapper> glyphs,
		Rectangle glyphRect, Rectangle imageRect, InputElementResult result, SegmentKey sk )
	{
		ImageWrapper image = lookForImage( images, glyphs, glyphRect,
											imageRect );

		if( image != null )
			result.getInputElementList().add( new InputImage( image,
				center( imageRect ), sk ) );
	}

	protected Point center( Rectangle rect )
	{
		return( ViewFunctions.instance().getCenter(rect) );
	}

	protected boolean lookForLine( String line, List<GlyphWrapper> glyphs,
									Rectangle scannerRect, int topYY )
	{
		boolean found = false;
		String lineWithoutSpace = line.replaceAll( "\\s", "" );
		while( !found && ( scannerRect.y < topYY ) )
		{
			String lineForYY = getLine( glyphs, scannerRect );
			found = line.equals( lineForYY ) || lineWithoutSpace.equals( lineForYY );
			if( !found )
				scannerRect.y++;
		}

		return( found );
	}

	protected Stream<GlyphWrapper> getSortedGlyphs( List<GlyphWrapper> glyphs,
		Rectangle rect )
	{
		return( glyphs.stream().filter( (gw) -> overlap( rect, gw.getBounds() ) )
			.sorted( (gw1, gw2) -> ( gw1.getBounds().x - gw2.getBounds().x ) ) );
	}

	protected boolean overlap( Rectangle rect1, Rectangle rect2 )
	{
		return( ViewFunctions.instance().rectanglesOverlap(rect1, rect2) );
	}

	protected String getLine( List<GlyphWrapper> glyphs, Rectangle rect )
	{
		StringBuilder sb = new StringBuilder();

		getSortedGlyphs( glyphs, rect )
			.forEach( (gw) -> sb.append( gw.getUnicodeString() ) );

		return( sb.toString() );
	}

	protected ImageWrapper lookForImage( List<ImageWrapper> images, List<GlyphWrapper> glyphs,
		Rectangle glyphRect, Rectangle imageRect )
	{
		ImageWrapper result = null;
		List<GlyphWrapper> sortedGlyphs = getSortedGlyphs( glyphs, glyphRect ).collect( Collectors.toList() );

		Optional<ImageWrapper> opt = images.stream().filter( (im) -> overlap( im.getBounds(), imageRect ) )
			.filter( (im) -> !sortedGlyphs.stream().anyMatch( (gl) -> overlap( im.getBounds(), gl.getBounds() ) ) )
			.sorted( (im1,im2) -> im2.getBounds().y - im1.getBounds().y )
			.findFirst();

		if( opt.isPresent() )
			result = opt.get();

		return( result );
	}


	protected static class LiteParserValidator
	{
		protected static final int NUMBER = 1;
		protected static final int DOT = 2;
		protected static final int MOVE = 3;

		protected ChessViewConfiguration _configuration = null;

		enum ResultOfCheck
		{
			MATCHES,
			NOT_COMPLETE,
			FAILS
		}

		protected String _text = null;
		protected int _position = -1;
		List<String> _tokensToCheck = null;

		public LiteParserValidator( String text, ChessViewConfiguration configuration )
		{
			_configuration = configuration;
			_text = text;
			_tokensToCheck = new ArrayList<String>();
		}

		public boolean isChessGameText()
		{
			boolean result = false;

			while( !result && hasNext() )
			{
				String token = next();
				_tokensToCheck.add( token );
				result = checkTokens();
			}

			return( result );
		}
		
		public boolean hasNext()
		{
			return( _position < _text.length() );
		}

		public String next()
		{
			String result = "";

			while( ( result.length() == 0 ) && hasNext() )
			{
				if( _position >= 0 )
				{
					String separator =  _text.substring( _position, _position + 1 );
					if( separator.equals( "." ) )
						_tokensToCheck.add( separator );
				}

				int pos1 = StringFunctions.instance().indexOfAnyChar( _text, " \t.", _position + 1 );
				if( pos1 == -1 )		pos1 = _text.length();

				result = _text.substring( _position + 1, pos1 );
				_position = pos1;
			}

			return( result );
		}
		
		protected boolean checkTokens()
		{
			boolean result = false;

			final int[] sequence1 = new int[] { NUMBER, DOT, MOVE, MOVE };
			final int[] sequence2 = new int[] { NUMBER, DOT, DOT, DOT, MOVE, NUMBER, DOT, MOVE };

			ResultOfCheck result1 = check( sequence1 );
			ResultOfCheck result2 = null;
			if( ! result1.equals( ResultOfCheck.MATCHES ) )
			{
				result2 = check( sequence2 );
			}

			if( result1.equals( ResultOfCheck.MATCHES ) || result2.equals( ResultOfCheck.MATCHES ) )
				result = true;
			else if( !result1.equals( ResultOfCheck.NOT_COMPLETE ) && !result2.equals( ResultOfCheck.NOT_COMPLETE ) )
			{
				_tokensToCheck.clear();
			}

			return( result );
		}

		protected boolean isItAChessMoveString( String moveString )
		{
			boolean result = false;
			String englishMoveString = _configuration.getChessLanguageConfigurationToParseTextFrom().translateMoveStringToEnglish( moveString, null );
			result = ChessMoveAlgebraicNotation.getInstance().isItAChessMoveString( englishMoveString );
			return( result );
		}

		protected ResultOfCheck check( int[] sequence )
		{
			ResultOfCheck result = ResultOfCheck.NOT_COMPLETE;

			int lastNumber = -1;
			Iterator<String> it = _tokensToCheck.iterator();
			for( int ii=0; it.hasNext() && (ii<sequence.length) && result.equals( ResultOfCheck.NOT_COMPLETE ); ii++ )
			{
				String token = it.next();
				switch( sequence[ii] )
				{
					case NUMBER:
					{
						Integer number = IntegerFunctions.parseInt(token);
						if( ( number == null ) ||
							( lastNumber != -1 ) && ( ( lastNumber + 1 ) != number ) )
						{
							result = ResultOfCheck.FAILS;
						}
						else
						{
							lastNumber = number;
						}
					}
					break;

					case DOT:
						if( ! token.equals( "." ) )
							result = ResultOfCheck.FAILS;
					break;

					case MOVE:
						if( ! isItAChessMoveString( token ) )
							result = ResultOfCheck.FAILS;
					break;
				}
				if( (ii==(sequence.length-1)) && result.equals( ResultOfCheck.NOT_COMPLETE ) )
					result = ResultOfCheck.MATCHES;
			}
			
			return( result );
		}
	}
}
