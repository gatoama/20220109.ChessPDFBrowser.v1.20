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
package com.frojasg1.libpdf.view.listeners;

import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.pdf.ImageJPanel;
import com.frojasg1.libpdf.api.GlyphWrapper;
import com.frojasg1.libpdf.api.ImageWrapper;
import com.frojasg1.libpdf.view.PdfViewerContext;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.RenderedImage;
import java.util.List;
import java.util.Optional;
import com.frojasg1.libpdf.view.controller.PdfObjectsSelectorObserverGen;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PdfObjectsControllerListenerBase extends MouseAdapter
{
	protected static final Point ORIGIN = new Point( 0, 0 );

	protected PdfViewerContext _pdfViewerContext;

//	protected List<GlyphWrapper> _glyphsOfPage = null;
//	protected List<ImageWrapper> _imagesOfPage = null;

	protected ImageJPanel _imageJPanel = null;

	protected PdfObjectsSelectorObserverGen _objectSelectorObserver = null;

//	protected GlyphWrapper _selectedGlyph = null;
//	protected ImageWrapper _selectedImage = null;

//	protected Map<RenderedImage, Rectangle> _imagesNormalizedBounds = new HashMap<>();

	public void init( ImageJPanel imageJPanel,
						PdfObjectsSelectorObserverGen objectSelectorObserver,
						PdfViewerContext pdfViewerContext )
	{
		_pdfViewerContext = pdfViewerContext;
		_imageJPanel = imageJPanel;

		addListeners();

		_objectSelectorObserver = objectSelectorObserver;
	}

	protected PdfViewerContext getPdfViewerContext()
	{
		return( _pdfViewerContext );
	}

	protected PdfObjectsSelectorObserverGen getPdfObjectsSelectorObserved()
	{
		return( _objectSelectorObserver );
	}

	protected void addListeners()
	{
		_imageJPanel.addMouseListener(this);
		_imageJPanel.addMouseMotionListener(this);
		_imageJPanel.addMouseWheelListener(this);
	}

	@Override
	public void mouseMoved( MouseEvent evt )
	{
		mouseMoved( evt.getPoint() );
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent evt )
	{
		resetSelection();
		mouseMoved( evt.getPoint() );
	}

	@Override
	public void mousePressed( MouseEvent evt )
	{
		setSelectedGlyph( getGlyph( getNormalizedLocation( evt.getPoint() ) ) );
	}

	@Override
	public void mouseDragged( MouseEvent evt )
	{
//		newLineSelected( evt.getY() );
	}

	@Override
	public void mouseReleased( MouseEvent evt )
	{
//		newLineSelected( -1 );
	}

	@Override
    public void mouseExited(MouseEvent e)
	{
		setSelectedItemsToNull();
	}

	protected void setSelectedItemsToNull()
	{
		setSelectedGlyph(null);
		setSelectedImage(null);
	}

	public void resetSelection()
	{
		setSelectedItemsToNull();

		_objectSelectorObserver.newGlyph(null, null );
		_objectSelectorObserver.newImage(null, null );
	}

	protected void mouseMoved( Point point )
	{
		Point normalizedPoint = getNormalizedLocation( point );
		GlyphWrapper glyph = getGlyph( normalizedPoint );
		ImageWrapper image = getImage( normalizedPoint );

		if( getSelectedGlyph() != glyph )
		{
			_objectSelectorObserver.newGlyph(glyph,
				( glyph == null ) ? null : PdfObjectsControllerListenerBase.this.getBoundsOnImageJPanel( glyph.getBounds() ) );
			setSelectedGlyph( glyph );
		}

		if( getSelectedImage() != image )
		{
			_objectSelectorObserver.newImage(image,
				( image == null ) ? null : getBoundsOnImageJPanel( image.getBounds() ) );
			setSelectedImage( image );
		}
	}


	protected GlyphWrapper getGlyph( Point point )
	{
		GlyphWrapper result = null;
		if( getGlyphsOfPage() != null )
		{
			if( ( getSelectedGlyph() != null ) && getSelectedGlyph().getBounds().contains( point ) )
				result = getSelectedGlyph();
			else
			{
				Optional<GlyphWrapper> opt = getGlyphsOfPage().stream().filter( (gl) -> gl.getBounds().contains( point ) ).findAny();
				if( opt.isPresent() )
					result = opt.get();
			}
		}

		return( result );
	}

	protected ImageWrapper getImage( Point point )
	{
		ImageWrapper result = null;
		if( getImagesOfPage() != null )
		{
//			if( ( _selectedImage != null ) && getNormalizedBounds( _selectedImage ).contains( point ) )
//				result = _selectedImage;
//			else
			{
				Optional<ImageWrapper> opt = getImagesOfPage().stream()
					.filter( (im) -> getNormalizedBounds(im).contains( point ) )
					.sorted( (im1, im2) -> area(im1.getImage()) - area(im2.getImage()) )
					.findFirst();
				if( opt.isPresent() )
					result = opt.get();
			}
		}

		return( result );
	}

	protected int area( RenderedImage image )
	{
		int result = 0;
		if( image != null )
			result = image.getWidth() * image.getHeight();

		return( result );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC, RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}

	protected double getZoomFactor()
	{
		Double result = getIfNotNull(
							getIfNotNull( _imageJPanel, ImageJPanel::getZoomFactor ),
							zf -> zf._value );
		if( result == null )
			result = 1.0d;

		return( result );
	}

	protected Point getNormalizedLocation( Point pointOnComponent )
	{
		return( ViewFunctions.instance().getNewPoint(pointOnComponent, 1 / getZoomFactor() ) );
	}

	public Rectangle getBoundsOnImageJPanel( Rectangle normalizedBounds )
	{
		Rectangle result = null;
		if( normalizedBounds != null )
		{
//			Point locationOnScreen = _imageJPanel.getLocationOnScreen();

			result = ViewFunctions.instance().calculateNewBounds(normalizedBounds, null,
				ORIGIN, getZoomFactor() );

//			result.x += locationOnScreen.x;
//			result.y += locationOnScreen.y;
		}

		return( result );
	}

	protected Rectangle getNormalizedBounds( ImageWrapper image )
	{
		Rectangle result = null;
		if( image != null )
		{
			result = image.getBounds();
/*
			result = _imagesNormalizedBounds.get(image);
			if( result == null )
			{
				result = new Rectangle( image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight() );
				_imagesNormalizedBounds.put( image, result );
			}
*/
		}

		return( result );
	}
/*
	protected Rectangle getBoundsOnImageJPanel( ImageWrapper image )
	{
		Rectangle result = PdfObjectsControllerListenerBase.this.getBoundsOnImageJPanel( getNormalizedBounds( image ) );

		return( result );
	}
*/
	protected void removeListeners()
	{
		_imageJPanel.removeMouseListener(this);
		_imageJPanel.removeMouseMotionListener(this);
		_imageJPanel.removeMouseWheelListener(this);
	}

	public void releaseResources()
	{
		removeListeners();
		_imageJPanel = null;
	}

	public List<GlyphWrapper> getGlyphsOfPage() {
		return getPdfViewerContext().getGlyphsOfPage();
	}

	public void setGlyphsOfPage(List<GlyphWrapper> _glyphsOfPage) {
		getPdfViewerContext().setGlyphsOfPage(_glyphsOfPage );
		setSelectedGlyph(null);
	}

	public List<ImageWrapper> getImagesOfPage() {
		return getPdfViewerContext().getImagesOfPage();
	}

	public void setImagesOfPage(List<ImageWrapper> _imagesOfPage) {
		getPdfViewerContext().setImagesOfPage( _imagesOfPage );
		setSelectedImage(null);
	}

	public GlyphWrapper getSelectedGlyph() {
		return getPdfViewerContext().getSelectedGlyph();
	}

	public void setSelectedGlyph(GlyphWrapper _selectedGlyph) {
		getPdfViewerContext().setSelectedGlyph(_selectedGlyph );
	}

	public ImageWrapper getSelectedImage() {
		return getPdfViewerContext().getSelectedImage();
	}

	public void setSelectedImage(ImageWrapper _selectedImage) {
		getPdfViewerContext().setSelectedImage(_selectedImage);
	}
}
