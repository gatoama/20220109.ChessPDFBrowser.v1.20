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
package com.frojasg1.general.desktop.view.pdf;

import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.desktop.keyboard.IsKeyPressed;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ImageInversor;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.number.DoubleReference;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.metal.MetalScrollButton;

/**
 *
 * @author Usuario
 */
public class ImageJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase implements MouseMotionListener, MouseListener, MouseWheelListener,
													ChangeListener, ComponentListener, InternallyMappedComponent
{
	protected static final double DEFAULT_MIN_FACTOR = 0.25d;
	protected static final double DEFAULT_MAX_FACTOR = 8.0d;

	protected static final int MAX_TOP_WHEEL = 1;

	protected int _numTopWheelUp = 0;
	protected int _numTopWheelDown = 0;
	
	protected static final long serialVersionUID = 1L;

	protected JScrollPane _parent = null;

	protected Point _mouseLocationOnScreen = new Point(0,0);
	protected Point _mouseLocation = new Point(0,0);
	protected Point _initialDragMouseLocationOnScreen = new Point(0,0);
	protected Point _initialDragMouseLocationOnImage_Zoom1 = new Point(0,0);

	protected Point _pointInImage_zoom1_WhenStartingToZoom = null;
	
	protected DoubleReference _zoomFactor = null;

	protected double _imageZoom = 1.0D;

	protected BufferedImage _image = null;
	protected BufferedImage _originalImage = null;

	protected BufferedImage _defaultOriginalImage = null;
	
	protected ImageInversor _defaultImageInvesorFunction = null;

	protected boolean _hasToFit = false;

	protected ImageJPanelControllerInterface _controller = null;

	protected LinkedList<DoubleReference> _stepsForFactor = null;

	protected static final Cursor _sa_handCursor = new Cursor( Cursor.HAND_CURSOR );
	protected static final Cursor _sa_defaultCursor = new Cursor( Cursor.DEFAULT_CURSOR );

	protected boolean _scrollByProgram = false;

	protected Font _originalFontForTextLines = new Font("Tahoma", Font.BOLD, 17);
	protected Font _zoomedFontForTextLines = new Font("Tahoma", Font.BOLD, 17);

	protected List< TextLine > _originalListOfTextLines = new ArrayList<TextLine>();
	protected List< TextLine > _zoomedListOfTextLines = new ArrayList<TextLine>();
	protected Color _foregroundColorForTextLines = Color.BLACK;

	protected int _frameWidth = 0;
	protected Color _frameColor = Color.BLACK;

	protected boolean _canInvertImageColors = true;


	public static enum InitialVerticalPosition
	{
		TOP,
		BOTTOM,
		NOTHING
	}

	public ImageJPanel( JScrollPane parent, ImageJPanelControllerInterface controller )
	{
		super.init();

		_parent = parent;
		if( _parent != null )
		{
			_parent.getHorizontalScrollBar().addComponentListener(this);
			_parent.getVerticalScrollBar().addComponentListener(this);
			_parent.getVerticalScrollBar().getComponent(0).addMouseListener(this);
			_parent.getVerticalScrollBar().getComponent(1).addMouseListener(this);
			_parent.setWheelScrollingEnabled( false );		// we will program manually the wheel scrolling.
			_parent.getVerticalScrollBar().setUnitIncrement(0);
		}

		IsKeyPressed.activateKeyEventListening();
		_controller = controller;

		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);

		parent.getViewport().addChangeListener( this );
	}

	public ImageJPanel( BufferedImage image )
	{
		init();

		_hasToFit = true;
		_originalImage = image;
		_image = image;
	}

	public void setDefaultOriginalImage( BufferedImage defaultImage )
	{
		_image = null;
		_defaultOriginalImage = defaultImage;

		repaint();
	}

	protected BufferedImage getDefaultOriginalImage()
	{
		return( _defaultOriginalImage );
	}

	protected ImageInversor getDefaultImageInvesorFunction() {
		return _defaultImageInvesorFunction;
	}

	public void setDefaultImageInvesorFunction(ImageInversor _defaultImageInvesorFunction) {
		this._defaultImageInvesorFunction = _defaultImageInvesorFunction;
	}

	public BufferedImage getOriginalImage()
	{
		return( _originalImage );
	}

	public void zoomImageWithSize( double zoomFactor )
	{
		double equivalentZoomFactor = zoomFactor * getSize().width / _originalImage.getWidth();

		_imageZoom = zoomFactor;

		zoomImage( equivalentZoomFactor );

		setOriginalFontForTextLines( _originalFontForTextLines );
		zoomTexts();
	}

	protected double getFittingFactor( double availableSize, double imageSize )
	{
		return( availableSize / imageSize );
	}

	public Dimension getRealSize()
	{
		return( _hasToFit ? getSize() : getPreferredSize() );
	}

	public Dimension getSizeToFit()
	{
		Dimension size = getRealSize();
		int frameWidth = getZoomedFrameWidth();
		size.width -= 2*frameWidth;
		size.height -= 2*frameWidth;

		return( size );
	}

	protected double getFittingFactor( BufferedImage image )
	{
		double result = 1.0D;

		if( image != null )
		{
			Dimension size = getSizeToFit();

			double f1 = getFittingFactor( size.width, image.getWidth() );
			double f2 = getFittingFactor( size.height, image.getHeight() );

			result = Math.min( f1, f2 );
		}

		return( result );
	}

	public void setImageFittingIt( BufferedImage image )
	{
		boolean hasToFit = true;
		setImage( image, null, null, hasToFit );
	}

	public void zoomImage( double zoomFactor )
	{
		int newWidth = (int) ( new Double( _originalImage.getWidth() * zoomFactor ) ).intValue();
		int newHeight = (int) ( new Double( _originalImage.getHeight() * zoomFactor ) ).intValue();

		_image = ImageFunctions.instance().resizeImageAccurately(_originalImage, newWidth, newHeight );

		setPreferredSize();

		SwingUtilities.invokeLater( () -> repaint() );
	}

	protected double getZoomFactorDouble()
	{
		double result = 1.0D;
		
		if( _zoomFactor != null )
			result = _zoomFactor._value;

		return( result );
	}

	protected void zoomTexts()
	{
		_zoomedListOfTextLines = zoomTexts( _originalListOfTextLines, _imageZoom );
	}

	protected List< TextLine > zoomTexts( List< TextLine > originalList, double zoomFactor )
	{
		List< TextLine > result = new ArrayList<>();

		for( TextLine tl: originalList )
			result.add( zoomTextLine( tl, zoomFactor ) );

		return( result );
	}

	protected TextLine zoomTextLine( TextLine tl, double zoomFactor )
	{
		TextLine result = null;
		
		if( tl != null )
		{
			result = new TextLine( tl.getText(),
						ViewFunctions.instance().getNewPoint(tl.getCenter(), zoomFactor) );

			result.setFont( zoomFont( tl.getFont() ) );
		}

		return( result );
	}

	public void setFrameThick( int value )
	{
		if( value >= 0 )
		{
			_frameWidth = value;
		}
	}
	
	public void setOriginalFontForTextLines( Font font )
	{
		if( font != null )
		{
			_originalFontForTextLines = font;
			_zoomedFontForTextLines = zoomFont( font );
		}
	}

	protected Font zoomFont( Font font )
	{
		Font result = null;

		if( font != null )
		{
			result = FontFunctions.instance().getZoomedFont(font, _imageZoom );
		}

		return( result );
	}

	public void setForegroundColorForTextLines( Color color )
	{
		if( color != null )
			_foregroundColorForTextLines = color;
	}
	
	public void addTextLine( TextLine textLine )
	{
		addTextLine( _originalListOfTextLines, textLine );
		addTextLine( _zoomedListOfTextLines, zoomTextLine(textLine, _imageZoom ) );
	}

	protected void addTextLine( List< TextLine > list, TextLine textLine )
	{
		if( list != null )
			list.add( textLine );
	}

	public void clearTextLines()
	{
		_originalListOfTextLines.clear();

		if( _zoomedListOfTextLines != null )
			_zoomedListOfTextLines.clear();
	}

	@Override
	public void componentResized(ComponentEvent ce)
	{
		if( ce.getComponent() instanceof JScrollBar )
		{
			JScrollBar jsb = (JScrollBar) ce.getComponent();
			initializeUnitIncrementOfScrollBar( jsb );
		}
	}

	@Override
	public void componentMoved(ComponentEvent ce)
	{
	}

	@Override
	public void componentShown(ComponentEvent ce )
	{
	}

	@Override
	public void componentHidden(ComponentEvent ce)
	{
	}

	protected void initializeUnitIncrementOfScrollBar( JScrollBar jsb )
	{
		jsb.setUnitIncrement( jsb.getVisibleAmount() / 24 );
	}
	
	protected void changeImagePosition( BufferedImage image, InitialVerticalPosition ivp )
	{
		double factorX = (int) ( ( (double) image.getWidth() ) / image.getWidth() );
		double factorY = (int) ( ( (double) image.getHeight() ) / image.getHeight() );
		int xx = (int) ( _initialDragMouseLocationOnImage_Zoom1.getX() * factorX );
		int yy = (int) ( _initialDragMouseLocationOnImage_Zoom1.getY() * factorY );

		if( ivp.equals( InitialVerticalPosition.TOP ) )
		{
			_initialDragMouseLocationOnImage_Zoom1 = new Point( xx, 0 );
		}
		else if( ivp.equals( InitialVerticalPosition.BOTTOM ) )
		{
			_initialDragMouseLocationOnImage_Zoom1 = new Point( xx, (int) ( factorY * image.getHeight() ) );
		}
		else
		{
			_initialDragMouseLocationOnImage_Zoom1 = new Point( xx, yy );
		}
	}

	public void setImage( BufferedImage image, DoubleReference newFactor, InitialVerticalPosition ivp )
	{
		boolean hasToFit = false;
		setImage( image, newFactor, ivp, hasToFit );
	}

	public void setImage( BufferedImage image, DoubleReference newFactor, InitialVerticalPosition ivp,
							boolean hasToFit )
	{
		if( hasToFit )
		{
			_hasToFit = true;
			_originalImage = image;
			_image = _originalImage;

			repaint();
			return;
		}

		_numTopWheelUp = 0;
		_numTopWheelDown = 0;

		changeImagePosition( image, ivp );

		_image = image;

		repaint();
		_scrollByProgram = true;
		_parent.setViewportView(this);

		_parent.getHorizontalScrollBar().setValue( _parent.getHorizontalScrollBar().getMinimum() );
		_parent.getVerticalScrollBar().setValue( _parent.getVerticalScrollBar().getMinimum() );

		initializeUnitIncrementOfScrollBar( _parent.getHorizontalScrollBar() );
		initializeUnitIncrementOfScrollBar( _parent.getVerticalScrollBar() );
		_scrollByProgram = false;

		setStepsForFactor( newFactor );

		if( _zoomFactor == null )
		{
			newFactor = new DoubleReferencePercentage( ( (double) _parent.getWidth()) / (double) _image.getWidth() );
		}

		_zoomFactor = getMostSuitableFactor( newFactor );

//		setPreferredSize(getSizeForPreferredSize( getZoomFactorDouble() ) );
		setPreferredSize();

		updateScrolls( _zoomFactor );
	}

	protected void setPreferredSize()
	{
		setPreferredSize(getSizeForPreferredSize( 1.0D ) );
	}

	public Double getMostSuitableFactor( double factor )
	{
		DoubleReference factorDr = new DoubleReference( factor );
		DoubleReference resultDr = getMostSuitableFactor( factorDr );
		Double result = null;
		if( resultDr != null )
			result = resultDr._value;

		return( result );
	}

	public DoubleReference getMostSuitableFactor( DoubleReference factor )
	{
		DoubleReference result = null;

		DoubleReference next = null;
		DoubleReference previous = null;
		Iterator<DoubleReference> it = _stepsForFactor.iterator();
		while( ( result == null ) && ( it.hasNext() ) )
		{
			next = it.next();

			if( ( ( previous == null ) || (previous._value <= factor._value) ) && ( next._value > factor._value ) )
			{
				if( previous == null )  result = next;
				else
				{
					double delta1 = factor._value - previous._value;
					double delta2 = next._value - factor._value;
					if( delta1<=delta2 )	result = previous;
					else					result = next;
				}
			}
			previous = next;
		}
		if( result == null )
			result = next;

		return( result );
	}
	
	public LinkedList<DoubleReference> getStepsForFactorList()
	{
		return( _stepsForFactor );
	}

	protected double getMinFactor()
	{
		return( DEFAULT_MIN_FACTOR );
	}

	protected double getMaxFactor()
	{
		return( DEFAULT_MAX_FACTOR );
	}

	protected void setStepsForFactor( DoubleReference newFactorReference )
	{
		if( _image != null )
		{
			_stepsForFactor = new LinkedList<DoubleReference>();

			double newFactor = 1.0D;
			if( newFactorReference != null )
				newFactor = newFactorReference._value;
			
			double minFactor = getMinFactor();
			double maxFactor = getMaxFactor();

			int maxIncrementInPixels = 250;
			double step = 0.33d;

			double factor = minFactor;
			while( factor <= maxFactor )
			{
				_stepsForFactor.add( new DoubleReferencePercentage( factor ) );

				if( factor == maxFactor )
					break;

				int incrementInPixels = IntegerFunctions.min( maxIncrementInPixels,
																(int) (_image.getWidth() * factor * step / newFactor ) );

				factor = ( _image.getWidth() * factor + incrementInPixels * newFactor ) / _image.getWidth();

				if( factor > maxFactor ) factor = maxFactor;
			}
		}
	}

	protected void decreaseZoomFactor( int steps )
	{
		DoubleReference factor = getFactorDecreaseFactor( steps );
		setZoomFactor( factor );
	}

	protected DoubleReference getFactorDecreaseFactor( int steps )
	{
		DoubleReference result = new DoubleReferencePercentage( 1.0d );
		if( steps > 0 )
		{
			ListIterator<DoubleReference> it = _stepsForFactor.listIterator( _stepsForFactor.size() );
			while( it.hasPrevious() )
			{
				result = it.previous();
				if( result._value < getZoomFactorDouble() )
				{
					for( int ii=0; (ii<(steps-1)) && it.hasPrevious(); ii++ )
						result = it.previous();
					break;
				}
			}
		}
		else if( steps < 0 )
		{
			ListIterator<DoubleReference> it = _stepsForFactor.listIterator();
			while( it.hasNext() )
			{
				result = it.next();
				if( result._value > getZoomFactorDouble() )
				{
					for( int ii=0; (ii<(-steps-1)) && it.hasNext(); ii++ )
						result = it.next();
					break;
				}
			}
		}
		return( result );
	}
	
	protected Dimension getSizeForPreferredSize( double factor )
	{
		int width = 1;
		int height = 1;

		if( _image != null )
		{
			width = (int) ( _image.getWidth() * factor );
			height = (int) ( _image.getHeight() * factor );
		}

		Dimension result = new Dimension( width, height );
		return( result );
	}

	public DoubleReference getZoomFactor()
	{
		return( _zoomFactor );
	}
/*
	public void setZoomFactor( DoubleReference factor )
	{
		if( ( _parent != null ) && (_image != null ) )
		{
			Dimension newSize = getSizeForPreferredSize( factor._value );
//			Point newZoomedImageLocation = getNewZoomedImageLocation( newSize, _initialDragMouseLocationOnImage );

			Point newScrollValues = getNewScrollValues( _initialDragMouseLocationOnImage_Zoom1, _initialDragMouseLocationOnScreen, factor._value );

			setPreferredSize( newSize );
			repaint();

			_parent.setViewportView(this);
			setNewScrollValues( newScrollValues );
			_zoomFactor = factor;

			if( _controller != null )
				_controller.setNewPdfZoomFactor( _zoomFactor );
		}
	}
*/
	public void setZoomFactor( DoubleReference factor )
	{
		if( ( _parent != null ) && (_image != null ) )
		{
			if( _controller != null )
				_controller.setNewPdfZoomFactor( factor );
		}
	}

	protected void updateScrolls( DoubleReference factor )
	{
		if( ( _parent != null ) && (_image != null ) )
		{
			_scrollByProgram = true;
			_parent.setViewportView(this);
			_scrollByProgram = false;

			Point mouseLocationOnImage_Zoom1 = ( _pointInImage_zoom1_WhenStartingToZoom != null ?
												_pointInImage_zoom1_WhenStartingToZoom :
												_initialDragMouseLocationOnImage_Zoom1 );
			Point newScrollValues = getNewScrollValues( mouseLocationOnImage_Zoom1, _initialDragMouseLocationOnScreen, factor._value );

			repaint();

			setNewScrollValues( newScrollValues );
			_zoomFactor = factor;
		}
	}

	protected Point getNewScrollValues( Point newZoomedImageLocation, Point mouseLocation, double factor )
	{
		Point result = new Point( 0, 0 );

		if( isJFrameVisible() )
		{
			Point leftUpperCorner = _parent.getLocationOnScreen();
			Point relativeMouseLocation = new Point( (int) (mouseLocation.getX() - leftUpperCorner.getX()),
													 (int) (mouseLocation.getY() - leftUpperCorner.getY()) );

			JScrollBar verticalScrollBar = _parent.getVerticalScrollBar();
			int maxY = verticalScrollBar.getMaximum();
			int minY = verticalScrollBar.getMinimum();
			int visibleY = verticalScrollBar.getVisibleAmount();
			int visibleImageY = (int) _parent.getViewport().getExtentSize().getHeight();

			JScrollBar horizontalScrollBar = _parent.getHorizontalScrollBar();
			int maxX = horizontalScrollBar.getMaximum();
			int minX = horizontalScrollBar.getMinimum();
			int visibleX = horizontalScrollBar.getVisibleAmount();
			int visibleImageX = (int) _parent.getViewport().getExtentSize().getWidth();

			int coorY = calculateScrollValueCoordinate( minY, maxY, visibleY,
														(int) relativeMouseLocation.getY(),
														(int) getPreferredSize().getHeight(),
														(int) newZoomedImageLocation.getY(),
														visibleImageY, factor );

			int coorX = calculateScrollValueCoordinate( minX, maxX, visibleX,
														(int) relativeMouseLocation.getX(),
														(int) getPreferredSize().getWidth(),
														(int) newZoomedImageLocation.getX(),
														visibleImageX, factor );

			result = new Point( coorX, coorY );
		}

		return( result );
	}

	protected int calculateScrollValueCoordinate( int min, int max,
													int visible,
													int mouseLocation,
													int totalImageJPanel,
													int imageLocation,
													int visibleImage,
													double zoomFactor )
	{
		int result = 0;

		if( visibleImage <= totalImageJPanel )
		{
			int totalScrollPane = max - min;

			double factor1 = ( (double)totalScrollPane ) / totalImageJPanel;
			double factor2 = ( (double)visible ) / visibleImage;

			result = (int) ( imageLocation * factor1 * zoomFactor - mouseLocation * factor2 );
		}

		return( result );
	}

	protected int getZoomedValue( double value )
	{
		int result = IntegerFunctions.roundToInt( value * _imageZoom );

		return( result );
	}

	protected int getZoomedFrameWidth()
	{
		return( getZoomedValue( _frameWidth ) );
	}

	protected Dimension getFittingSize( BufferedImage image )
	{
		double factor = getFittingFactor( image );
		return( new Dimension( IntegerFunctions.zoomValueRound(image.getWidth(), factor),
								IntegerFunctions.zoomValueRound(image.getHeight(), factor) ) );
	}

	protected Dimension getSize( BufferedImage image )
	{
		return( new Dimension( image.getWidth(), image.getHeight() ) );
	}

	protected boolean doesItFit( BufferedImage image )
	{
		boolean result = ( image != null );
		if( result )
		{
			result = doesItFit( getSize(image), getSizeToFit() );
		}

		return( result );
	}

	protected boolean doesItFit( int i1, int i2 )
	{
		return( IntegerFunctions.abs( i1 - i2 ) <= 1 );
	}

	protected boolean doesItFit( Dimension dimen1, Dimension dimen2 )
	{
		return( doesItFit( dimen1.width, dimen2.width ) &&
				doesItFit( dimen1.height, dimen2.height ) );
	}

	protected BufferedImage getFittingImage( BufferedImage originalImage )
	{
		return( resizeImageAccurately( originalImage, getFittingSize( originalImage ) ) );
	}

	protected BufferedImage resizeImageAccurately( BufferedImage originalImage, Dimension size )
	{
		return( resizeImageAccurately( originalImage, size.width, size.height ) );
	}

	protected BufferedImage resizeImageAccurately( BufferedImage originalImage, int width, int height )
	{
		return( ImageFunctions.instance().resizeImageAccurately(originalImage, width, height) );
	}

	protected int getPositionToPaint( int sizeToFit, int imageSize )
	{
		return( IntegerFunctions.max( 0, ( sizeToFit - imageSize ) / 2 ) );
	}

	protected Point getPositionToPaint( BufferedImage image )
	{
		Point result = null;
		if( image != null )
		{
			result = new Point();

			int frameWidth = getZoomedFrameWidth();
			Dimension sizeToFit = getSizeToFit();

			result.x = getPositionToPaint( sizeToFit.width, image.getWidth() ) + frameWidth;
			result.y = getPositionToPaint( sizeToFit.height, image.getHeight() ) + frameWidth;
		}

		return( result );
	}

	protected <CC> CC getFirst( CC ... options )
	{
		CC result = null;
		for( CC elem: options )
			if( ( result = elem ) != null )
				break;

		return( result );
	}

	@Override
	protected void paintComponent(Graphics gc) {
		super.paintComponent(gc);

		paintFrame( gc );

		BufferedImage image;
		if( _hasToFit )
		{
			image = getFirst( _originalImage, _defaultOriginalImage );
			if( image != null )
			{
				if( ! doesItFit( _image ) )
					_image = getFittingImage( image );

				Point position = getPositionToPaint( _image );
				gc.drawImage( _image,
								position.x, position.y,
								position.x + _image.getWidth(),
								position.y + _image.getHeight(),
								0, 0, _image.getWidth(), _image.getHeight(),
								null );
			}
		}
		else if( ( image = getFirst( _image, _defaultOriginalImage ) ) != null )
		{
			int frameWidth = getZoomedFrameWidth();
			gc.drawImage( image,
							frameWidth, frameWidth,
							(int) getPreferredSize().getWidth() - 2 * frameWidth,
							(int) getPreferredSize().getHeight() - 2 * frameWidth,
							0, 0, image.getWidth(), image.getHeight(),
							null );
		}

		writeTextLines( gc );
	}

	protected void writeTextLines( Graphics gc )
	{
		Iterator<TextLine> it = _zoomedListOfTextLines.iterator();
		
		while( it.hasNext() )
		{
			writeTextLine( gc, it.next() );
		}
	}

	protected Font getFont( TextLine textLine )
	{
		Font font = textLine.getFont();
		if( font == null )
			font = _zoomedFontForTextLines;

		return( font );
	}

	protected Color getColor( TextLine textLine )
	{
		Color color = textLine.getColor();
		if( color == null )
			color = _foregroundColorForTextLines;

		return( color );
	}

	protected void writeTextLine( Graphics gc, TextLine textLine )
	{
		if( textLine != null )
		{
			ImageFunctions.instance().paintStringCentered( gc, getFont( textLine ), textLine.getText(),
												getColor( textLine ), textLine.getCenter() );
		}
	}

	protected void paintFrame( Graphics gc )
	{
		int frameWidth = getZoomedFrameWidth();

		int xx = 0;
		int yy = 0;
		int width = 0;
		int height = 0;

		if( _hasToFit )
		{
			Dimension sizeToFit = getSizeToFit();

			xx = frameWidth-1;
			yy = frameWidth-1;
			width = sizeToFit.width + 1;
			height = sizeToFit.height + 1;
		}
		else
		{
			Dimension size = getRealSize();

			xx = frameWidth - 1;
			yy = frameWidth - 1;
			width = size.width - 1;
			height = size.height - 1;
		}

		ImageFunctions.instance().drawRect(gc, xx, yy, width, height, _frameColor, frameWidth);
/*
		ImageFunctions.instance().drawRect( gc, xx, yy,
								width,
								height,
								Color.BLACK, frameWidth );
*/
	}

	@Override
	public void mouseClicked(MouseEvent evt)
	{
		if( evt.getComponent() == this )
			System.out.println( "Location of point: " + getPointFactored( evt.getPoint(), 1/getZoomFactorDouble() ) );
	}

	/**
	 * 
	 * @param component		It will be a MetalScrollButton of the Vertical Scroll Bar.
	 * @return				1 - Up button clicked
	 *						-1 - Down button clicked
	 *						0 - Otherwise
	 */
	protected int getUnitsFromComponentClicked( Component component )
	{
		int result = 0;

		if( _parent != null )
		{
			if( component == _parent.getVerticalScrollBar().getComponent( 0 ) )		// up button
				result = 1;
			else if( component == _parent.getVerticalScrollBar().getComponent( 1 ) )		// down button
				result = -1;
		}

		return( result );
	}
	
	@Override
	public void mouseEntered(MouseEvent evt)
	{
//		updateInitialLocation( evt );
		setCursor( _sa_defaultCursor );
	}
	
	@Override
	public void mouseExited(MouseEvent evt)
	{
		pickupLocation( evt );
		_pointInImage_zoom1_WhenStartingToZoom = null;
		setCursor( _sa_defaultCursor );
	}

	@Override
	public void mousePressed(MouseEvent evt)
	{
		// for vertical scroll
		if( (evt.getButton() == MouseEvent.BUTTON1 ) && ( evt.getComponent() instanceof MetalScrollButton ) && ( _parent != null ) )
		{
			int units = getUnitsFromComponentClicked( evt.getComponent() );
			boolean pageHasBeenChanged = changePageIfNecessary( units );

			if( !pageHasBeenChanged )
			{
				JScrollBar scrollBar = _parent.getVerticalScrollBar();
				incrementScrollBarValue( scrollBar, ( units * scrollBar.getVisibleAmount() ) / 12 );
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent evt)
	{
		pickupLocation( evt );
		setCursor( _sa_defaultCursor );
	}

	@Override
	public void mouseDragged(MouseEvent evt)
	{
		if( getCursor().getName().equals( _sa_defaultCursor.getName() ) )
			setCursor( _sa_handCursor );

		Point mouseLocation = evt.getLocationOnScreen();
		_pointInImage_zoom1_WhenStartingToZoom = null;

		Point newScrollValues = getNewScrollValues(_initialDragMouseLocationOnImage_Zoom1, mouseLocation, getZoomFactorDouble() );
		setNewScrollValues( newScrollValues );
	}

	@Override
	public void mouseMoved(MouseEvent evt)
	{
		pickupLocation( evt );
		_pointInImage_zoom1_WhenStartingToZoom = null;
	}

	@Override
	public void stateChanged(ChangeEvent e){
		_parent.revalidate();
		repaint();

//		_pointInImage_zoom1_WhenStartingToZoom = null;
		if( ! _scrollByProgram )
			updateInitialLocation( _mouseLocationOnScreen, _mouseLocation );
    }

	protected void pickupLocation( MouseEvent evt )
	{
		_mouseLocationOnScreen = evt.getLocationOnScreen();
		_mouseLocation = evt.getPoint();
		updateInitialLocation( _mouseLocationOnScreen, _mouseLocation );
	}
	
	protected void updateInitialLocation( Point mouseLocationOnScreen, Point mouseLocation )
	{
		if( ( _zoomFactor != null ) && ( getZoomFactorDouble() > 0 ) )
		{
			_initialDragMouseLocationOnScreen = mouseLocationOnScreen;
			_initialDragMouseLocationOnImage_Zoom1 = getPointFactored( mouseLocation, 1/getZoomFactorDouble() );
		}
	}

	protected Point getInitialLocationForZoom( Point mouseLocation )
	{
		Point result = null;
		if( ( _zoomFactor != null ) && ( getZoomFactorDouble() > 0 ) )
		{
			result = getPointFactored( mouseLocation, 1/getZoomFactorDouble() );
		}
		return( result );
	}

	protected Point getPointFactored( Point point, double factor )
	{
		Point result = new Point( (int) (point.getX() * factor), (int) (point.getY() * factor) );
		return( result );
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent evt)
	{
		boolean pageHasBeenChanged = false;
		int units = evt.getUnitsToScroll();
		
		if( IsKeyPressed.isKeyPressed( KeyEvent.VK_CONTROL ) )
		{
			_numTopWheelUp = 0;
			_numTopWheelDown = 0;

			if( _pointInImage_zoom1_WhenStartingToZoom == null )
				_pointInImage_zoom1_WhenStartingToZoom = getInitialLocationForZoom( evt.getPoint() );

			decreaseZoomFactor( IntegerFunctions.sgn( evt.getWheelRotation() ) );
		}
		else
		{
			if( _parent != null )
			{
				_pointInImage_zoom1_WhenStartingToZoom = null;

				JScrollBar scrollBar = null;

				if( IsKeyPressed.isKeyPressed( KeyEvent.VK_SHIFT ) )
				{
					_numTopWheelUp = 0;
					_numTopWheelDown = 0;

					scrollBar = _parent.getHorizontalScrollBar();
				}
				else
				{
					scrollBar = _parent.getVerticalScrollBar();

					pageHasBeenChanged = changePageIfNecessary( units );
				}

				if( !pageHasBeenChanged )
				{
					if (evt.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
					{
	//					incrementScrollBarValue( scrollBar, evt.getUnitsToScroll() * scrollBar.getUnitIncrement() );
						incrementScrollBarValue( scrollBar, ( units * scrollBar.getVisibleAmount() ) / 12 );
					}
					else
					{ //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
	//					incrementScrollBarValue( scrollBar, scrollBar.getBlockIncrement(1) );
						incrementScrollBarValue( scrollBar, scrollBar.getVisibleAmount() / 12 );
					}
				}
			}
		}
	}

	protected boolean changePageIfNecessary( int units )
	{
		boolean pageHasBeenChanged = false;

		if( units > 0 )
			_numTopWheelUp = 0;
		else if( units < 0 )
			_numTopWheelDown = 0;
		
		if(  ( _controller != null ) && ( _parent != null ) )
		{
			JScrollBar scrollBar = _parent.getVerticalScrollBar();

			if( ( units < 0 ) && ( scrollBar.getMinimum() == scrollBar.getValue() )  )
			{
				if( _numTopWheelUp >= MAX_TOP_WHEEL )
				{
					pageHasBeenChanged = true;
					_controller.previousPage();
				}
				else
					_numTopWheelUp++;
			}
			else if( ( units > 0 ) && ( (scrollBar.getMaximum() - scrollBar.getVisibleAmount() ) == scrollBar.getValue() ) )
			{
				if( _numTopWheelDown >= MAX_TOP_WHEEL )
				{
					pageHasBeenChanged = true;
					_controller.nextPage();
				}
				else
					_numTopWheelDown++;
			}
		}
		
		return( pageHasBeenChanged );
	}
	
	protected void incrementScrollBarValue( JScrollBar scrollBar, int increment )
	{
		setScrollBarValue( scrollBar, scrollBar.getValue() + increment );
	}

	protected void setScrollBarValue( JScrollBar scrollBar, int value )
	{
		int valueToSet = IntegerFunctions.min( scrollBar.getMaximum(),
												IntegerFunctions.max( scrollBar.getMinimum(), value) );

		_scrollByProgram = true;
		scrollBar.setValue(valueToSet);
		_scrollByProgram = false;
	}

	protected void setNewScrollValues( Point newScrollValues )
	{
		if( _parent != null )
		{
			setScrollBarValue( _parent.getHorizontalScrollBar(), (int) newScrollValues.getX() );
			setScrollBarValue( _parent.getVerticalScrollBar(), (int) newScrollValues.getY() );
		}
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper) {
		_parent = compMapper.mapComponent(_parent);

		super.setComponentMapper(compMapper);
	}

	public static class DoubleReferencePercentage extends DoubleReference
	{
		public DoubleReferencePercentage( double value )
		{
			super( value );
		}

		@Override
		public String toString()
		{
			String result = null;

			double percentage = _value * 100;
			
			double absolute = Math.abs( percentage );

			if( absolute == 0.0d )
				result = "0 %";
			else if( absolute < 10 )
				result = String.format( "%.2f %%", percentage );
			else if( absolute < 100 )
				result = String.format( "%.1f %%", percentage );
			else
				result = String.format( "%.0f %%", percentage );

			return( result );
		}
	}

	public static void main( String[] args )
	{
		DoubleReference number = new DoubleReferencePercentage( 9.5 );
		System.out.println( number );
	}

	protected boolean isJFrameVisible()
	{
		Boolean result = null;
		
		Component comp = this;
		while( ( result == null ) && (comp != null ) )
		{
			if( ( comp instanceof JFrame ) || ( comp.getParent() == null ) )
				result = comp.isVisible();
			
			comp = comp.getParent();
		}

		return( result );
	}

	@Override
	protected void invertColorsChild(ColorInversor colorInversor)
	{
		BasicScrollBarUI ui;
		invertTextLineListColors( _originalListOfTextLines, colorInversor );
		invertTextLineListColors( _zoomedListOfTextLines, colorInversor );

		_foregroundColorForTextLines = colorInversor.invertColor(_foregroundColorForTextLines);
		_frameColor = colorInversor.invertColor(_frameColor);

		if( canInvertImageColors() )
			invertImageColors(colorInversor);

		if( this.getDefaultImageInvesorFunction() != null )
			invertDefaultImageColors(colorInversor);
	}

	public boolean canInvertImageColors()
	{
		return( _canInvertImageColors );
	}

	public void setCanInvertImageColors( boolean value )
	{
		_canInvertImageColors = value;
	}

	protected void invertImageColors(ColorInversor colorInversor)
	{
		_image = null;
//		_originalImage = ImageFunctions.instance().invertImage(_originalImage);
		_originalImage = colorInversor.invertImage(_originalImage);
	}

	protected void invertDefaultImageColors(ColorInversor colorInversor)
	{

		if( getDefaultImageInvesorFunction() != null )
		{
			_image = null;
			_defaultOriginalImage = getDefaultImageInvesorFunction().apply(colorInversor, _defaultOriginalImage);
		}
	}

	protected void invertTextLineListColors( List< TextLine > list, ColorInversor colorInversor )
	{
		for( TextLine tl: list )
			invertTextLineColors(tl, colorInversor);
	}

	protected void invertTextLineColors( TextLine textLine, ColorInversor colorInversor )
	{
		if( textLine != null )
			textLine.setColor( colorInversor.invertColor(textLine.getColor()) );
	}

	public void setFrameColor( Color color )
	{
		_frameColor = color;
	}

	public static class TextLine
	{
		protected String _text = null;
		protected Point _center = null;
		protected Font _font = null;
		protected Color _color = null;

		public TextLine( String text, Point center )
		{
			_text = text;
			_center = center;
		}

		public void setFont( Font font )
		{
			_font = font;
		}

		public void setColor( Color color )
		{
			_color = color;
		}
		
		public String getText()
		{
			return( _text );
		}

		public Point getCenter()
		{
			return( _center );
		}

		public Font getFont()
		{
			return( _font );
		}
		
		public Color getColor()
		{
			return( _color );
		}
	}

}
