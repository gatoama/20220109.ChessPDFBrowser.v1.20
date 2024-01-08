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
package com.frojasg1.chesspdfbrowser.recognizer.chessboard.impl;

import com.frojasg1.applications.common.components.resizecomp.ComponentOriginalDimensions;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.ChessBoardGridDetector;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.image.processing.CannyEdgeDetector;
import com.frojasg1.general.desktop.image.processing.DiagonalIntegrator;
import com.frojasg1.general.desktop.image.processing.Normalizer;
import com.frojasg1.general.desktop.image.processing.RadonTransform;
import com.frojasg1.general.desktop.image.processing.SobelEdgeDetector;
import com.frojasg1.general.desktop.image.processing.StraightLineDetector;
import com.frojasg1.general.desktop.image.processing.StraightLineIntegrator;
import com.frojasg1.general.desktop.image.processing.ThresholdByStraightLinesChooser;
import com.frojasg1.general.desktop.view.ViewFunctions;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardGridDetectorImpl implements ChessBoardGridDetector
{
	protected static final int MIN_NUM_OF_LINES_IN_ONE_DIRECTION = 12;
	protected static final int MAX_NUM_OF_LINES_IN_ONE_DIRECTION = 33;

	protected InputImage _inputImage = null;

//	protected int[] _pixels = null;

//	protected Map<Point, VertexSurroundingResult> _points = null;

	protected BufferedImage _edgesImage = null;

	protected List<ChessBoardGridResult> _chessBoardGrids = null;

	protected Map<Point, Long> _thresholdPoints = null;

	protected int _numHorizontalLines = 0;
	protected int _numVerticalLines = 0;


	public BufferedImage getEdgesImage()
	{
		return( _edgesImage );
	}

	@Override
	public List<ChessBoardGridResult> getGridOfBoardsDetected()
	{
		return( _chessBoardGrids );
	}

	@Override
	public boolean process(InputImage inputImage)
	{
		_inputImage = inputImage;
/*
		BufferedImage image = inputImage.getImage().getImage();
		_pixels = turnIntoGray( ImageFunctions.instance().getRGB(0, 0,
			image.getWidth(), image.getHeight(), image) );

		_points = getPossiblePointsOfGrid( _pixels );
*/
		_edgesImage = calculateEdgeImage( _inputImage.getImage().getImage() );

		_chessBoardGrids = detectChessBoards( _thresholdPoints );
//		boolean result = fillInVertexCoordinates( points );

		return( true );
	}

	protected boolean numLinesOk( int numLines )
	{
		return( (numLines >= MIN_NUM_OF_LINES_IN_ONE_DIRECTION ) &&
			( numLines <= MAX_NUM_OF_LINES_IN_ONE_DIRECTION ) );
	}

	protected boolean linesDetectedOk()
	{
		return( numLinesOk( _numHorizontalLines ) && numLinesOk( _numVerticalLines ) );
	}

	protected BufferedImage calculateEdgeImage( BufferedImage image )
	{
		BufferedImage result = image;
//		result = calculateSobelEdgeImage( result );

		float gaussianKernelRadius = 0.7f;
		int count = 0;
		do
		{
			result = calculateCannyEdgeImage( image, gaussianKernelRadius );
	//		result = calculateRadonTransform( result );

			result = calculateStraightLineIntegrator( result );
	//		result = calculateStraightLineIntegrator( result );
	//		result = calculateStraightLineIntegrator( result );

	//		result = calculateSobelEdgeImage( result );

			result = calculateStraightLineDetector( result );
			gaussianKernelRadius *= 1.5f;
			count++;
		}
		while( !linesDetectedOk() && ( count < 5 ) );

		ThresholdByStraightLinesChooser thresholdStraight = new ThresholdByStraightLinesChooser();
//		Map<Point, Long> thresholdPoints = thresholdStraight.processStraightLineBinaryThreshold(result, 0.60D, 0.95D );
//		_thresholdPoints = thresholdPoints.keySet();

		DiagonalIntegrator diagonalIntegrator = new DiagonalIntegrator();
		int xxTolerance = 0;
		_thresholdPoints = diagonalIntegrator.process( _thresholdPoints,
			 xxTolerance );

		_thresholdPoints = thresholdStraight.process( _thresholdPoints,
			ChessBoardVertexSetsDetector.MINIMUM_NUMBER_OF_POINTS_ALLIGNED_IN_DIAGONAL );

//		_thresholdPoints = thresholdPoints.keySet();

		return( result );
	}

	protected List<ChessBoardGridResult> detectChessBoards( Map<Point, Long> vertex )
	{
		ChessBoardVertexSetsDetector detector = new ChessBoardVertexSetsDetector();
		
		return( detector.process( _edgesImage, vertex ) );
	}

	protected BufferedImage calculateStraightLineDetector( BufferedImage image )
	{
		StraightLineDetector detector = new StraightLineDetector();

		double thresholdToCheckPeak = 0.33D;
		double thresholdIncrementToDetectPeak = 0.40D;
		double minimumThresholdIncrementToDetectPeak = 0.20D;
		double toleranceToMaintainPeak = 0.05D;
		BufferedImage result = image;
		result = detector.process( result, thresholdToCheckPeak,
									thresholdIncrementToDetectPeak,
									minimumThresholdIncrementToDetectPeak,
									toleranceToMaintainPeak );

		_numHorizontalLines = detector.getyCoorOfHorizontalLines().size();
		_numVerticalLines = detector.getxCoorOfVerticalLines().size();
		_thresholdPoints = detector.getVertexCandidates();

		return( result );
	}

	protected BufferedImage calculateStraightLineIntegrator( BufferedImage image )
	{
		StraightLineIntegrator detector = new StraightLineIntegrator();
		Normalizer normalizer = new Normalizer();

		BufferedImage result = image;
		result = detector.process( result );
		result = normalizer.process( result );

		return( result );
	}

	protected BufferedImage calculateRadonTransform( BufferedImage image )
	{
		RadonTransform detector = new RadonTransform();
		return( detector.process( image ) );
	}

	protected BufferedImage calculateSobelEdgeImage( BufferedImage image )
	{
		SobelEdgeDetector detector = new SobelEdgeDetector();
		return( detector.process( image ) );
	}

	protected BufferedImage calculateCannyEdgeImage( BufferedImage image,
														float gaussianKernelRadius )
	{
		CannyEdgeDetector detector = new CannyEdgeDetector();

//		detector.setGaussianKernelRadius( 0.7f );
		detector.setGaussianKernelRadius( gaussianKernelRadius );

		detector.setLowThreshold(0.5f);
		detector.setHighThreshold(1f);

		detector.setSourceImage(image);
		detector.process();

		return( detector.getEdgesImage() );
	}

	public InputImage getInputImage()
	{
		return( _inputImage );
	}

	protected int[] turnIntoGray( int[] rgbPixels )
	{
		for( int ii=0; ii<rgbPixels.length; ii++ )
			rgbPixels[ii] = ImageFunctions.instance().getGrayScale( rgbPixels[ii] );

		return( rgbPixels );
	}

	protected int getWidth()
	{
		return( _inputImage.getImage().getImage().getWidth() );
	}

	protected int getHeight()
	{
		return( _inputImage.getImage().getImage().getHeight() );
	}

	public Collection<Point> getPointsThresholdAndDiagonalIntegrator()
	{
		return( _thresholdPoints.keySet() );
	}

/*
	public Set<Point> getPointsSurroundingInspection()
	{
		return( _points.keySet() );
	}

	protected Map<Point, VertexSurroundingResult> getPossiblePointsOfGrid( int[] pixels )
	{
		Map<Point, VertexSurroundingResult> result = new HashMap<>();

		int width = getWidth();
		int height = getHeight();
		for( int yy = 0; yy < height; yy++ )
			for( int xx = 0; xx < width; xx++ )
			{
				VertexSurroundingResult vertexResult = getPossibleVertex( xx, yy, pixels );
				if( vertexResult != null )
					result.put( new Point( xx, yy ), vertexResult );
			}

		return( result );
	}

	protected VertexSurroundingResult getPossibleVertex( int xx, int yy, int[] pixels )
	{
		int edgeLength = 3;
		int gap = 0;
		return( getPossibleVertexBySurroundingInspection( xx, yy, pixels,
			edgeLength, gap ) );
	}

	protected VertexSurroundingResult getPossibleVertexBySurroundingInspection( int xx, int yy,
		int[] pixels, int edgeLength, int gap )
	{
		VertexSurroundingResult result = null;

		int maxDelta = edgeLength + gap;
		if( ( xx >= maxDelta ) && ( xx < ( getWidth() - maxDelta ) ) &&
			( yy >= maxDelta ) && ( yy < ( getHeight() - maxDelta ) ) )
		{
			int northWest = calculateAddition( xx - maxDelta, yy - maxDelta, pixels, edgeLength );
			int northEast = calculateAddition( xx + gap, yy - maxDelta, pixels, edgeLength );
			int southWest = calculateAddition( xx - maxDelta, yy + gap, pixels, edgeLength );
			int southEast = calculateAddition( xx + gap, yy + gap, pixels, edgeLength );

			double tolerance = 0.20D;
			if( additionMatches( northWest, southEast, tolerance ) &&
				additionMatches( northEast, southWest, tolerance ) &&
				!additionMatches( northEast, southEast, tolerance ) &&
				!additionMatches( northEast, northWest, tolerance ) &&
				!additionMatches( southWest, southEast, tolerance ) &&
				!additionMatches( southWest, northWest, tolerance )
				)
			{
				result = new VertexSurroundingResult( northWest, northEast,
					southWest, southEast );
			}
		}

		return( result );
	}

	protected int calculateAddition( int xx, int yy, int[] pixels, int edgeLength )
	{
		int width = getWidth();
		int result = 0;
		for( int ii=0; ii<edgeLength; ii++ )
			for( int jj=0; jj<edgeLength; jj++ )
				result += getPixel( xx + ii, yy + jj, width, pixels );

		return( result );
	}

	protected int getPixel( int xx, int yy, int width, int[] pixels )
	{
		return( ImageFunctions.instance().getColorOfPixel(xx, yy, width, pixels) );
	}

	protected boolean additionMatches( int addition1, int addition2, double tolerance )
	{
		int diff = IntegerFunctions.abs( addition1 - addition2 );
		int max = IntegerFunctions.max( addition1, addition2 );

		return( diff <= max * tolerance );
	}
*/
	@Override
	public double getZoomFactor()
	{
		return( (double) _inputImage.getImage().getBounds().width /
			(double) _inputImage.getImage().getImage().getWidth() );
	}

	protected Rectangle normalizedGlobalBounds( Rectangle bounds )
	{
		double factor = getZoomFactor();
		
		Rectangle result = ViewFunctions.instance().calculateNewBounds(bounds, null,
			ComponentOriginalDimensions.ORIGIN, factor);

		result.x += _inputImage.getImage().getBounds().x;
		result.y += _inputImage.getImage().getBounds().y;

		return( result );
	}

	@Override
	public Rectangle getBoardBoundsInsideImage(ChessBoardGridResult grid)
	{
		return( grid.getBoardBoundsInsideImage() );
	}

	@Override
	public Rectangle getBoxBoundsInsideImage(ChessBoardGridResult grid,
									int colNum, int rowNum)
	{
		return( grid.getBoxBoundsInsideImage(colNum, rowNum) );
	}

	@Override
	public Rectangle getNormalizedGlobalBoardBounds(ChessBoardGridResult grid)
	{
		return( normalizedGlobalBounds( getBoardBoundsInsideImage(grid) ) );
	}

	@Override
	public Rectangle getNormalizedGlobalBoxBounds(ChessBoardGridResult grid,
									int colNum, int rowNum) {
		return( normalizedGlobalBounds( getBoxBoundsInsideImage( grid, colNum, rowNum ) ) );
	}
/*
	public static class VertexSurroundingResult
	{
		int _northWestAddition = 0;
		int _northEastAddition = 0;
		int _southWestAddition = 0;
		int _southEastAddition = 0;

		public VertexSurroundingResult( int northWest, int northEast,
										int southWest, int southEast )
		{
			_northWestAddition = northWest;
			_northEastAddition = northEast;
			_southWestAddition = southWest;
			_southEastAddition = southEast;
		}

		public int getNorthWestAddition() {
			return _northWestAddition;
		}

		public int getNorthEastAddition() {
			return _northEastAddition;
		}

		public int getSouthWestAddition() {
			return _southWestAddition;
		}

		public int getSouthEastAddition() {
			return _southEastAddition;
		}
	}
*/
}
