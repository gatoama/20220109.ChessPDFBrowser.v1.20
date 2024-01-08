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
package com.frojasg1.libpdf.api;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface PdfDocumentWrapper extends PDFownerInterface
{
	public String getFileName();

	public void loadPdf( String fileName ) throws IOException;

	public int getNumberOfPages();

	public BufferedImage renderImageWithDPI( int pageIndex, float dpi );

	public BufferedImage renderImageWithDPIForBackground( int pageIndex, float dpi );

	public String getTextOfPage( int pageIndex, List<Rectangle> segments ) throws IOException;

	public void close() throws IOException;

	public Dimension getSizeOfPage( int pageIndex );

	public float getDpi( double factor );

	public List<ImageWrapper> getImagesOfPage( int pageIndex ) throws IOException;
	public List<ImageWrapper> getImagesOfPage() throws IOException;

	public List<GlyphWrapper> getGlyphsOfPage( int pageIndex, boolean getImages, Float factorForImages) throws IOException;
	public List<GlyphWrapper> getGlyphsOfPage( boolean getImages, Float factorForImages) throws IOException;
}
