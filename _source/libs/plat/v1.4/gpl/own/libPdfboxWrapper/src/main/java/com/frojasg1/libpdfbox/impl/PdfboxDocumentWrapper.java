/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 */
package com.frojasg1.libpdfbox.impl;

import com.frojasg1.libpdf.api.GlyphWrapper;
import com.frojasg1.libpdf.api.ImageWrapper;
import com.frojasg1.libpdf.api.PdfDocumentWrapper;
import com.frojasg1.libpdfbox.utils.PDFboxWrapperUtils;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PdfboxDocumentWrapper implements PdfDocumentWrapper
{
	protected static final float _dpi_of_pdf_page_to_show_factor_1 = 72F;

	protected PDDocument _pdfDocument = null;
	protected PDPage _page = null;
	protected int _currentPageNumber = -1;

	protected MyPDFRenderer _pdfRendererShowText = null;
	protected MyPDFRenderer _pdfRendererDoNotShowText = null;
	protected String _fileName = null;

	protected List<ImageWrapper> _currentPageImages = null;
	protected List<GlyphWrapper> _currentPageGlyphs = null;


	@Override
	public String getFileName()
	{
		return( _fileName );
	}

	@Override
	public float getDpi( double factor )
	{
		float result = (float) (_dpi_of_pdf_page_to_show_factor_1 * factor );
		return( result );
	}

	@Override
	public void loadPdf( String fileName ) throws IOException
	{
		_fileName = fileName;
		close();

		_pdfDocument = PDDocument.load( new File(_fileName) );

		_pdfRendererShowText = createRenderer();
		_pdfRendererDoNotShowText = createRenderer();
		_pdfRendererDoNotShowText.setShowText(false);
	}

	protected MyPDFRenderer createRenderer()
	{
		return( new MyPDFRenderer( _pdfDocument ) );
	}

	@Override
	public int getNumberOfPages()
	{
		int result = -1;
		if( _pdfDocument != null )
			result = _pdfDocument.getNumberOfPages();

		return( result );
	}

	@Override
	public BufferedImage renderImageWithDPI( int pageIndex, float dpi )
	{
		BufferedImage result = null;
		try
		{
			result = _pdfRendererShowText.renderImageWithDPI(pageIndex, dpi);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	@Override
	public BufferedImage renderImageWithDPIForBackground( int pageIndex, float dpi )
	{
		return( renderImageWithDPIForBackground(_pdfRendererDoNotShowText,
												pageIndex, dpi) );
	}

	public BufferedImage renderImageWithDPIForBackground( PDFRenderer renderer,
															int pageIndex, float dpi )
	{
		BufferedImage result = null;
		try
		{
			result = renderer.renderImageWithDPI(pageIndex, dpi);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	protected void resetImagesAndGlyphs()
	{
		_currentPageImages = null;
		_currentPageGlyphs = null;
	}

	protected synchronized PDPage getPDPage( int pageIndex )
	{
		if( ( _currentPageNumber != pageIndex ) || ( _page == null ) )
		{
			_currentPageNumber = pageIndex;
			resetImagesAndGlyphs();

			_page = _pdfDocument.getPage( pageIndex );
		}

		return( _page );
	}

	@Override
	public Dimension getSizeOfPage( int pageIndex )
	{
		PDPage page = getPDPage( pageIndex );
		Dimension result = new Dimension( (int) page.getBBox().getWidth(),
											(int) page.getBBox().getHeight() );

		return( result );
	}

	@Override
	public String getTextOfPage( int pageIndex, List<Rectangle> segments ) throws IOException
	{
		StringBuilder sb = new StringBuilder();

		PDPage page = getPDPage( pageIndex );
		
		PDFTextStripperByArea stripper = new PDFTextStripperByArea();
		stripper.setSortByPosition( true );

		int index = 0;
		for( Rectangle rect: segments )
		{
			stripper.addRegion( "region-" + index, rect );
			index++;
		}

		stripper.extractRegions( page );

		for( int ii=0; ii<index; ii++ )
		{
			String regionText = stripper.getTextForRegion( "region-" + ii );
			sb.append( regionText );
		}

		return( sb.toString() );
	}
/*
	@Override
	public List<ImageWrapper> getImagesOfPage( int pageIndex ) throws IOException
	{
		return( getImagesFromPage( getPDPage( pageIndex ) ) );
	}
*/

	@Override
	public List<ImageWrapper> getImagesOfPage() throws IOException
	{
		return( getImagesOfPage( _currentPageNumber ) );
	}

	@Override
	public List<ImageWrapper> getImagesOfPage( int pageIndex ) throws IOException
	{
		List<ImageWrapper> result = null;
		if( pageIndex == _currentPageNumber )
			result = _currentPageImages;

		if( result == null )
			result = getImagesOfPageRenderer( pageIndex );

		if( pageIndex == _currentPageNumber )
			_currentPageImages = result;

//		return( getImagesOfPageStreamEngine( pageIndex ) );
		return( result );
	}

	public List<ImageWrapper> getImagesOfPageStreamEngine( int pageIndex ) throws IOException
	{
		GetImageLocationsAndSize printer = new GetImageLocationsAndSize();

		printer.processPage( getPDPage( pageIndex ) );

		return( printer.getImageList() );
	}

	public List<ImageWrapper> getImagesOfPageRenderer( int pageIndex ) throws IOException
	{
		MyPDFRenderer renderer = createRenderer();
		renderer.setGetGlyphsAndImages(true);

		float dpi = getDpi( 1.0f );
		renderer.renderImageWithDPI(pageIndex, dpi);

		return( renderer.getImageList() );
	}

	@Override
	public void close() throws IOException
	{
		if( _pdfDocument != null )
			_pdfDocument.close();
	}

	@Override
	public BufferedImage getPage(int numberOfPage, double factor) {
		float dpi = getDpi( factor );

		BufferedImage result = renderImageWithDPI( numberOfPage, dpi );

		return( result );
	}
/*
	// https://stackoverflow.com/questions/8705163/extract-images-from-pdf-using-pdfbox
	public List<RenderedImage> getImagesFromPDF(PDDocument document) throws IOException {
			List<RenderedImage> images = new ArrayList<>();
		for (PDPage page : document.getPages()) {
			images.addAll(getImagesFromResources(page.getResources()));
		}

		return images;
	}
*/

	@Override
	public List<GlyphWrapper> getGlyphsOfPage( boolean getImages,
												Float factorForImages ) throws IOException
	{
		return( getGlyphsOfPage( _currentPageNumber, getImages, factorForImages ) );
	}

	@Override
	public List<GlyphWrapper> getGlyphsOfPage( int pageIndex, boolean getImages,
												Float factorForImages ) throws IOException
	{
		List<GlyphWrapper> result = null;
		if( pageIndex == _currentPageNumber )
			result = _currentPageGlyphs;

		if( result == null )
			result = getGlyphsOfPageStripper( pageIndex, getImages, factorForImages );

		if( pageIndex == _currentPageNumber )
			_currentPageGlyphs = result;

		return( result );
//		return( getGlyphsOfPageRenderer( pageIndex ) );
	}

	public List<GlyphWrapper> getGlyphsOfPageRenderer( int pageIndex ) throws IOException
	{
		MyPDFRenderer renderer = createRenderer();
		renderer.setGetGlyphsAndImages(true);

		float factor = 4f;

		float dpi = getDpi( factor );
		BufferedImage pageImage = renderer.renderImageWithDPI(pageIndex, dpi);
		renderer.setGetGlyphsAndImages(false);

		List<GlyphWrapper> result = renderer.getGlyphList();
		adjustGlyphs( pageImage, factor, factor, result );

		return( result );
	}

	protected void adjustGlyphs( BufferedImage pageImage, float pageFactor,
								float boundsFactor, List<GlyphWrapper> result )
	{
		for( GlyphWrapper gw: result )
			PDFboxWrapperUtils.instance().adjustGlyph( pageImage,
				getPDPage(_currentPageNumber).getCropBox(),
				pageFactor, boundsFactor, gw );
	}

	public List<GlyphWrapper> getGlyphsOfPageStripper( int pageIndex, boolean getImages,
												Float factorForImages ) throws IOException
	{
		GetTextLocations stripper = new GetTextLocations(_pdfDocument);
//		stripper.setSortByPosition( true );
//		stripper.setStartPage(pageIndex);
//		stripper.setEndPage(pageIndex);

//		Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
//		stripper.writeText( _pdfDocument, dummy );
		stripper.stripPage( pageIndex );

		Map<MatrixWrapper, GlyphWrapper> glyphMap = stripper.getGlyphMap();
/*
		float factor = 4f;
		BufferedImage pageImage = this.getPage(pageIndex, factor);
//		ImageIO.write(pageImage, "png", new File( "J:\\page.png" ) );
		adjustGlyphs( pageImage, factor, 1.0f, result );
*/
		if( getImages )
		{
			float factor = ( factorForImages != null ) ? factorForImages : 4f;
			MyPDFRendererWithDrawer renderer = createMyPDFRendererWithDrawer(glyphMap, factor);
			renderer.setGetGlyphsAndImages(false);
			renderer.setShowText(true);

			renderImageWithDPIForBackground( renderer, pageIndex, getDpi(factor) );
		}

		List<GlyphWrapper> result = new ArrayList<>(glyphMap.values());

		return( result );
	}

	protected MyPDFRendererWithDrawer createMyPDFRendererWithDrawer( Map<MatrixWrapper, GlyphWrapper> glyphMap,
																	float pageFactor )
	{
		return( new MyPDFRendererWithDrawer( _pdfDocument, glyphMap, pageFactor ) );
	}
	
	/*
	public List<ImageWrapper> getImagesFromPage(PDPage page) throws IOException {
		GetImageLocationsAndSize printer = new GetImageLocationsAndSize();
	    printer.processPage(page);  

		return( printer.getImages() );
	}

	public List<RenderedImage> getImagesFromPage(PDPage page) throws IOException {
		return getImagesFromResources(page.getResources());
	}

	private List<RenderedImage> getImagesFromResources(PDResources resources) throws IOException {
		List<RenderedImage> images = new ArrayList<>();

		for (COSName xObjectName : resources.getXObjectNames()) {
			PDXObject xObject = resources.getXObject(xObjectName);

			if (xObject instanceof PDFormXObject) {
				images.addAll(getImagesFromResources(((PDFormXObject) xObject).getResources()));
			} else if (xObject instanceof PDImageXObject) {
				PDImageXObject pdImage = (PDImageXObject) xObject;
				images.add(pdImage.getImage());
			}
		}

		return images;
	}
*/
}
