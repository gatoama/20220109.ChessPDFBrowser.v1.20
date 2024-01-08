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

import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.implementation.Range;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author Usuario
 */
public class ImageSegmentator
{
	public static final short TOLERANCE_TO_MATCH_COMPONENT_OF_PIXEL_FOR_BACKGROUND_GAP = 4;
	public static final short TOLERANCE_TO_MATCH_COMPONENT_OF_PIXEL_FOR_HORIZONTAL_LINE_BOUNDARY = 20;
	protected static final double PERCENTAGE_OF_CENTERED_HORIZONTAL_LINE = 66D;
	protected static final double PERCENTAGE_OF_MATCHED_POINTS_TO_MATCH_HORIZONTAL_LINE_BOUNDARY = 95D;

	protected static final double MAXIMUM_GAP_PERCENTAGE_OF_TOTAL = 5D;
	protected static final double MINIMUM_PERCENTAGE_OF_MATCHED_POINTS_TO_MATCH_VERTICAL_BOUNDARY = 99.5D;
	protected static final int MAX_VALUE_OF_COLOR_COMPONENT = 225;
//	protected static final int INTERVAL_OF_BACKGROUND_GAP_TO_CHECK_IN_PIXELS_OF_WIDTH = 25;
//	protected static final int MINIMUM_NUMBER_OF_MATCHES_IN_INTERVAL_BACKGROUND_GAP_TO_BE_VERTICAL_BOUNDARY = 20;
	protected static final int INTERVAL_OF_BACKGROUND_GAP_TO_CHECK_IN_PIXELS_OF_WIDTH = 10;
	protected static final int MINIMUM_NUMBER_OF_MATCHES_IN_INTERVAL_BACKGROUND_GAP_TO_BE_VERTICAL_BOUNDARY = 6;
	protected static final double PERCENTAGE_OF_CENTERED_VERTICAL_LINE = 80D;

	protected static final double MINIMUM_PERCENTAGE_OF_MATCHED_POINTS_TO_MATCH_HORIZONTAL_BOUNDARY = 98D;
	protected static final int INTERVAL_OF_BACKGROUND_GAP_TO_CHECK_IN_PIXELS_OF_HEIGHT = 35;
	protected static final int MINIMUM_NUMBER_OF_MATCHES_IN_INTERVAL_BACKGROUND_GAP_TO_BE_HORIZONTAL_BOUNDARY = 35;


	protected SummarizedPage _summarizedPage = null;
	protected BufferedImage _image = null;

	protected int[] _pixels = null;
//	protected Integer[] _preprocessedPixels = null;

	protected List<Range> _yAxisSegments = null;

	public ImageSegmentator( SummarizedPage sp, BufferedImage bi, int[] pixels )
	{
		_summarizedPage = sp;
		_image = bi;

		// TODO: comment
/*
		ExecutionFunctions.instance().safeMethodExecution( () -> 
			ImageIO.write( _image, "JPEG", new File( "J:\\Image.Segmentator.jpg" ) ) );
*/

		if( pixels != null )
			_pixels = pixels;
		else
			_pixels = ImageFunctions.instance().getRGB( 0, 0, (int) bi.getWidth(), (int) bi.getHeight(), bi );
	}

	public void init( BufferedImage bi, Dimension size )
	{
		_image = bi;

		_pixels = ImageFunctions.instance().getRGB( 0, 0, (int) bi.getWidth(), (int) bi.getHeight(), bi, size );
	}

	public int[] getPixels()
	{
		return( _pixels );
	}

	public List<Rectangle> getAreaSegments()
	{
		List<Rectangle> result = new ArrayList<Rectangle>();

		if( _summarizedPage != null )
		{
			int discardedPixels = (int) ( (100-PERCENTAGE_OF_CENTERED_VERTICAL_LINE) / 100 * _image.getWidth() );
			int initialY_toCheckforLine = discardedPixels/2;
			int totalPixelsToInspect = (int) ( _image.getHeight() * PERCENTAGE_OF_CENTERED_VERTICAL_LINE / 100 );
			int finalY_toCheckforLine = initialY_toCheckforLine + totalPixelsToInspect;

			List<Range> yRanges = getYaxisSegments();
			PixelComponents[][] summarizedPixels = _summarizedPage.getSummarizedPixels();

			PixelComponents summarizedPixel = null;
			if( summarizedPixels == null )
				summarizedPixel = leftTopCornerColorSummarizedPixel();

			Iterator<Range> it = yRanges.iterator();
			while( it.hasNext() )
			{
				Range rangeY = it.next();

				List<CandidateToBoundary> candidatesToVerticalBoundary = new ArrayList<CandidateToBoundary>();

				for( int xx=0; xx<(int) _image.getWidth(); xx++ )
				{
					int countOfMatches = 0;
					int countOfValidTests = 0;

					int offset = xx + (int) ( _image.getWidth() * rangeY.getInitial() );
					for( int yy=rangeY.getInitial(); yy<rangeY.getFinal(); yy++ )
					{
						if( ( yy>= initialY_toCheckforLine ) &&
							( yy <= finalY_toCheckforLine ) )
						{
							if( summarizedPixels != null )
								summarizedPixel = summarizedPixels[xx][yy];

							if( summarizedPixel != null )
							{
								short[] comp = getPixelComponents( _pixels[offset] );

								if( summarizedPixel.nearlyEquals(comp, TOLERANCE_TO_MATCH_COMPONENT_OF_PIXEL_FOR_BACKGROUND_GAP) )
								{
									countOfMatches++;
								}
								countOfValidTests++;
							}
						}
						offset += (int) _image.getWidth();
					}

					double percentageOfMatched = ( countOfValidTests > 0 ?
													( (double) countOfMatches * 100 ) / countOfValidTests :
													0 );
					if( percentageOfMatched >= MINIMUM_PERCENTAGE_OF_MATCHED_POINTS_TO_MATCH_VERTICAL_BOUNDARY )
					{
						candidatesToVerticalBoundary.add( new CandidateToBoundary( xx ) );
					}
				}

				candidatesToVerticalBoundary.add( new CandidateToBoundary( (int) _image.getWidth(), true ) );

				List<Range> xAxisRanges = addCandidatesForBoundary_toRanges( candidatesToVerticalBoundary,
																			INTERVAL_OF_BACKGROUND_GAP_TO_CHECK_IN_PIXELS_OF_WIDTH,
																			MINIMUM_NUMBER_OF_MATCHES_IN_INTERVAL_BACKGROUND_GAP_TO_BE_VERTICAL_BOUNDARY );

				xAxisRanges = discardShortRanges( xAxisRanges, (int) _image.getWidth() );

				Iterator<Range> itX = xAxisRanges.iterator();

				Rectangle prevAreaSegment = null;
				while( itX.hasNext() )
				{
					Range rangeX = itX.next();

					Rectangle areaSegment = new Rectangle( rangeX.getInitial(), rangeY.getInitial(),
															rangeX.getFinal() - rangeX.getInitial() + 30,
															rangeY.getFinal() - rangeY.getInitial() + 1 );

					correctRightSide( prevAreaSegment, areaSegment );
					prevAreaSegment = areaSegment;

					result.add( areaSegment );
				}
				prevAreaSegment = null;
			}
		}

		return( result );
	}

	protected void correctRightSide( Rectangle prevAreaSegment, Rectangle areaSegment )
	{
		if( prevAreaSegment != null )
			prevAreaSegment.width = IntegerFunctions.min( prevAreaSegment.width, areaSegment.x - prevAreaSegment.x );
	}


	protected PixelComponents leftTopCornerColorSummarizedPixel()
	{
		short[] comp = getPixelComponents( _pixels[0] );

		boolean signedComponents = false;
		return( new PixelComponents( (short ) 0xFF, comp[0], comp[1], comp[2], signedComponents ) );
	}

	public List<Range> getYaxisSegments()
	{
		if( _yAxisSegments == null )
		{
			int discardedPixels = (int) ( (100-PERCENTAGE_OF_CENTERED_HORIZONTAL_LINE) / 100 * _image.getWidth() );
			int initialX_toCheckforLine = discardedPixels/2;
			int totalPixelsToInspectForLine = (int) ( _image.getWidth() * PERCENTAGE_OF_CENTERED_HORIZONTAL_LINE / 100 );
			int finalX_toCheckforLine = initialX_toCheckforLine + totalPixelsToInspectForLine;

			PixelComponents[][] summarizedPixels = _summarizedPage.getSummarizedPixels();

			PixelComponents summarizedPixel = null;

			if( summarizedPixels == null )
				summarizedPixel = leftTopCornerColorSummarizedPixel();
			
			List<CandidateToBoundary> candidatesToHorizontalBoundary = new ArrayList<CandidateToBoundary>();

			int offset=0;
			for( int yy=0; yy<(int) _image.getHeight(); yy++ )
			{
				int countOfMatches_forBackgroundGap = 0;
				int countOfValidTests_forBackgroundGap = 0;

				short[][] pixelsOfLineToBeProcessed = new short[ totalPixelsToInspectForLine ][];

				for( int xx=0; xx<(int) _image.getWidth(); xx++ )
				{
					if( (xx>=initialX_toCheckforLine ) && (xx<finalX_toCheckforLine) )
						pixelsOfLineToBeProcessed[xx-initialX_toCheckforLine] = getPixelComponents( _pixels[offset] );

					if( summarizedPixels != null )
						summarizedPixel = summarizedPixels[xx][yy];

					if( summarizedPixel != null )
					{
						short[] comp = getPixelComponents( _pixels[offset] );

						if( summarizedPixel.nearlyEquals(comp, TOLERANCE_TO_MATCH_COMPONENT_OF_PIXEL_FOR_BACKGROUND_GAP) )
						{
							countOfMatches_forBackgroundGap++;
						}
					}
					countOfValidTests_forBackgroundGap++;
					offset++;
				}

				double percentageOfMatched = ( countOfValidTests_forBackgroundGap > 0 ?
												( (double) countOfMatches_forBackgroundGap * 100 ) / countOfValidTests_forBackgroundGap :
												0 );
				if( percentageOfMatched >= MINIMUM_PERCENTAGE_OF_MATCHED_POINTS_TO_MATCH_HORIZONTAL_BOUNDARY )
				{
					candidatesToHorizontalBoundary.add( new CandidateToBoundary( yy ) );
				}
				else
				{
					PixelComponents summarized = getSummararizedHomogeneousPoint(pixelsOfLineToBeProcessed,
																							PERCENTAGE_OF_MATCHED_POINTS_TO_MATCH_HORIZONTAL_LINE_BOUNDARY,
																							TOLERANCE_TO_MATCH_COMPONENT_OF_PIXEL_FOR_HORIZONTAL_LINE_BOUNDARY );

					if( canBeHorizontalLineBoundary( summarized ) )
						candidatesToHorizontalBoundary.add( new CandidateToBoundary( yy, true ) );
				}
			}
			candidatesToHorizontalBoundary.add( new CandidateToBoundary( (int) _image.getHeight(), true ) );

			_yAxisSegments = addCandidatesForBoundary_toRanges( candidatesToHorizontalBoundary,
																	INTERVAL_OF_BACKGROUND_GAP_TO_CHECK_IN_PIXELS_OF_HEIGHT,
																	MINIMUM_NUMBER_OF_MATCHES_IN_INTERVAL_BACKGROUND_GAP_TO_BE_HORIZONTAL_BOUNDARY );

			_yAxisSegments = discardShortRanges( _yAxisSegments, (int) _image.getHeight() );
		}

		return( _yAxisSegments );
	}

	protected List<Range> addCandidatesForBoundary_toRanges( List<CandidateToBoundary> candidatesForBoundary,
																int minIntervalToCheck,
																int minimumNumberOfMatchesInIntervalToCommitCandidate )
	{
		final int margin = minIntervalToCheck - minimumNumberOfMatchesInIntervalToCommitCandidate;

		List<Range> result = new ArrayList<Range>();

		LinkedList<Integer> tmpIntervalList = new LinkedList<Integer>();

		for( int ii=-minimumNumberOfMatchesInIntervalToCommitCandidate; ii<0; ii++ )
			tmpIntervalList.add( ii );

		Integer startOfGapInterval = null;
		Integer startOfVisibleBlock = null;		// set start of visible block at the beginning
		Iterator<CandidateToBoundary> it = candidatesForBoundary.iterator();

		int previousValueToCheck = -1;
		while( it.hasNext() )
		{
			CandidateToBoundary ctb = it.next();
			int newValueToCheck = ctb._coordinate;

			if( ctb._fixed )
			{
				tmpIntervalList.clear();
				for( int ii=-minimumNumberOfMatchesInIntervalToCommitCandidate; ii<0; ii++ )
					tmpIntervalList.add( ii + newValueToCheck );
			}

			while( ( tmpIntervalList.size() > 0 ) && ( tmpIntervalList.getFirst() < newValueToCheck-minIntervalToCheck ))
				tmpIntervalList.removeFirst();

			tmpIntervalList.add( newValueToCheck );

			if( ( tmpIntervalList.size() >= minimumNumberOfMatchesInIntervalToCommitCandidate ) || ctb._fixed )
			{
				if( ( startOfGapInterval == null ) || ctb._fixed )
				{
					startOfGapInterval = tmpIntervalList.getFirst();

					if( startOfVisibleBlock != null )
					{
						int upperBound = ( ctb._fixed ? newValueToCheck : startOfGapInterval );
						int lowerBound = IntegerFunctions.max( 0, startOfVisibleBlock - margin );
						Range range = new Range( lowerBound, upperBound );
						result.add( range );

						startOfVisibleBlock = null;
					}
				}
			}
			else
			{
				if( newValueToCheck > minIntervalToCheck ) // if we are not at the beginning, then we have to set a visible segment
				{
					startOfGapInterval =  null;

					if( startOfVisibleBlock == null )
					{
						startOfVisibleBlock = previousValueToCheck;
					}
				}
			}
			previousValueToCheck = newValueToCheck;
		}

		return( result );
	}
/*
	protected List<Range> addCandidatesForBoundary_toRanges( List<CandidateToBoundary> candidatesForBoundary,
																int minIntervalToCheck,
																int minimumNumberOfMatchesInIntervalToCommitCandidate )
	{
		final int margin = minIntervalToCheck - minimumNumberOfMatchesInIntervalToCommitCandidate;

		List<Range> result = new ArrayList<Range>();

		LinkedList<Integer> tmpIntervalList = new LinkedList<Integer>();

		for( int ii=-minimumNumberOfMatchesInIntervalToCommitCandidate; ii<0; ii++ )
			tmpIntervalList.add( ii );

		Integer startOfGapInterval = null;
		Integer startOfVisibleBlock = null;		// set start of visible block at the beginning
		Iterator<CandidateToBoundary> it = candidatesForBoundary.iterator();

		int previousValueToCheck = -1;
		while( it.hasNext() )
		{
			CandidateToBoundary ctb = it.next();
			int newValueToCheck = ctb._coordinate;

			if( ctb._fixed )
			{
				tmpIntervalList.clear();
				for( int ii=-minimumNumberOfMatchesInIntervalToCommitCandidate; ii<0; ii++ )
					tmpIntervalList.add( ii + newValueToCheck );
			}
			
			while( ( tmpIntervalList.size() > 0 ) && ( tmpIntervalList.getFirst() < newValueToCheck-minIntervalToCheck ))
				tmpIntervalList.removeFirst();

			tmpIntervalList.add( newValueToCheck );

			if( ( tmpIntervalList.size() >= minimumNumberOfMatchesInIntervalToCommitCandidate ) || ctb._fixed )
			{
				if( ( startOfGapInterval == null ) || ctb._fixed )
				{
					startOfGapInterval = tmpIntervalList.getFirst();

					if( startOfVisibleBlock != null )
					{
						int upperBound = ( ctb._fixed ? newValueToCheck : startOfGapInterval );
						int lowerBound = IntegerFunctions.max( 0, startOfVisibleBlock - margin );
						Range range = new Range( lowerBound, upperBound );
						result.add( range );

						startOfVisibleBlock = null;
					}
				}
			}
			else
			{
				if( newValueToCheck > minIntervalToCheck ) // if we are not at the beginning, then we have to set a visible segment
				{
					startOfGapInterval =  null;

					if( startOfVisibleBlock == null )
					{
						startOfVisibleBlock = previousValueToCheck;
					}
				}
			}
			previousValueToCheck = newValueToCheck;
		}

		return( result );
	}
*/
	protected List<Range> discardShortRanges( List<Range> initialRanges, int total )
	{
		List<Range> result = new ArrayList<Range>();

		int minimumSegmentSize = (int) ( MAXIMUM_GAP_PERCENTAGE_OF_TOTAL * total / 100 );

		Iterator<Range> it = initialRanges.iterator();
		while( it.hasNext() )
		{
			Range range = it.next();

			if( ( range.getFinal()-range.getInitial() ) >= minimumSegmentSize )
			{
				result.add( range );
			}
		}

		return( result );
	}

	public boolean canBeHorizontalLineBoundary( PixelComponents pc )
	{
		boolean result = false;

		if( pc != null )
		{
			if( ( pc.getRed() <= MAX_VALUE_OF_COLOR_COMPONENT ) ||
				( pc.getGreen() <= MAX_VALUE_OF_COLOR_COMPONENT ) ||
				( pc.getBlue() <= MAX_VALUE_OF_COLOR_COMPONENT ) )
			{
				result = true;
			}
		}

		return( result );
	}

	public static short[] getPixelComponents( Integer rgbPixel )
	{
		short[] result = new short[3];
		
		short blue = (short) ( rgbPixel != null ? ( rgbPixel & 0xFF ) : -1 );
		short green = (short) ( rgbPixel != null ? ((rgbPixel >> 8) & 0xFF ) : -1 );
		short red = (short) ( rgbPixel != null ? ( (rgbPixel >> 16) & 0xFF ) : -1 );

		result[0] = red;
		result[1] = green;
		result[2] = blue;

		return( result );
	}
	
	/**
	 * 
	 * @param arrayOfValuesOfPixels		This param is an array of pixels. Each pixel being defined by an array short[3] (representing red, green and blue).
	 *									if the components are below 0, the pixel does not count.
	 *									If the pixel is null, it does neither count.
	 * @param numberOfMatchesToSummarize
	 * @return 
	 */
	public static PixelComponents getSummararizedHomogeneousPoint( short[][] arrayOfValuesOfPixels, double percentageToMatch, int toleranceForPixelComparison )
	{
		PixelComponents result = null;

		int totalValidElements = 0;

		int intervalForPixelComparison = 2 * toleranceForPixelComparison;

		int maxMatches = 0;
		PixelComponents tmpShp = null;
		for( int ii=0; ii<arrayOfValuesOfPixels.length; ii++ )
		{
			int numberOfMatches = 0;

			short[] pixelComponentsII = arrayOfValuesOfPixels[ii];
			if( pixelComponentsII != null )
			{
				int redToMatch = IntegerFunctions.max(0, pixelComponentsII[0] - toleranceForPixelComparison );
				int greenToMatch = IntegerFunctions.max(0, pixelComponentsII[1] - toleranceForPixelComparison );
				int blueToMatch = IntegerFunctions.max(0, pixelComponentsII[2] - toleranceForPixelComparison );

				int tolerance = TOLERANCE_TO_MATCH_COMPONENT_OF_PIXEL_FOR_BACKGROUND_GAP;
				if( ( pixelComponentsII[0] > tolerance ) ||
					( pixelComponentsII[1] > tolerance ) ||
					( pixelComponentsII[2] > tolerance ) )
				{
					for( int jj=0; jj<arrayOfValuesOfPixels.length; jj++ )
					{
						short[] pixelComponentsJJ = arrayOfValuesOfPixels[jj];
						if( pixelComponentsJJ != null )
						{
							int redDiff = IntegerFunctions.abs( pixelComponentsJJ[0] - redToMatch );
							int greenDiff = IntegerFunctions.abs( pixelComponentsJJ[1] - greenToMatch );
							int blueDiff = IntegerFunctions.abs( pixelComponentsJJ[2] - blueToMatch );
							if( (redDiff >= 0) && (redDiff <= intervalForPixelComparison) &&
								(greenDiff >= 0) && (greenDiff <= intervalForPixelComparison) &&
								(blueDiff >= 0) && (blueDiff <= intervalForPixelComparison) )
							{
								numberOfMatches++;
							}
						}
					}
				}

				if( numberOfMatches > maxMatches )
				{
					maxMatches = numberOfMatches;
					boolean signedComponents = false;
					tmpShp = new PixelComponents( (short) 0xFF,
															(short) (redToMatch + toleranceForPixelComparison),
															(short) (greenToMatch + toleranceForPixelComparison),
															(short) (blueToMatch + toleranceForPixelComparison),
															signedComponents );
				}

				totalValidElements++;
			}

		}

		int numberOfMatchesToSummarize = (int) Math.max( 1, Math.ceil( percentageToMatch * totalValidElements / 100 ) );

		if( maxMatches >= numberOfMatchesToSummarize )
		{
			result = tmpShp;
		}

		return( result );
	}

	protected static class CandidateToBoundary
	{
		public int _coordinate = -1;
		public boolean _fixed = false;

		public CandidateToBoundary( int coordinate, boolean fixed )
		{
			_coordinate = coordinate;
			_fixed = fixed;
		}

		public CandidateToBoundary( int coordinate )
		{
			this( coordinate, false );
		}
	}
}
