/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.libpdfbox.impl;

import com.frojasg1.libpdf.api.GlyphWrapper;
import com.frojasg1.libpdfbox.utils.PDFboxWrapperUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MyPageDrawer extends PageDrawer {

	protected static Color TRANSPARENT_WHITE = new Color( 0xffffff );

	protected BufferedImage _image;
	protected Graphics2D _graphics;
	protected float _factor = 1.0f;

	protected Map<MatrixWrapper, GlyphWrapper> _glyphMap = null;

	protected AtomicInteger _count;

	public MyPageDrawer( PageDrawerParameters parameters,
						Map<MatrixWrapper, GlyphWrapper> glyphMap,
						float factor,
						AtomicInteger count) throws IOException
	{
		super(parameters);
		_factor = factor;
		_glyphMap = glyphMap;
		_count = count;
	}

	public float getFactor()
	{
		return( _factor );
	}

	protected PDRectangle findCropBox()
	{
		return( getPage().getCropBox() );
	}

	protected float findRotation()
	{
		return( getPage().getRotation() );
	}

	protected BufferedImage createImage()
	{
		int imageType = BufferedImage.TYPE_4BYTE_ABGR;
		PDRectangle cropBox = findCropBox();
        float widthPt = cropBox.getWidth();
        float heightPt = cropBox.getHeight();
        int widthPx = (int)Math.max(Math.floor((double)(widthPt * _factor)), 1.0D);
        int heightPx = (int)Math.max(Math.floor((double)(heightPt * _factor)), 1.0D);

		int rotationAngle = getPage().getRotation();

		BufferedImage result;
		if (rotationAngle != 90 && rotationAngle != 270) {
			result = new BufferedImage(widthPx, heightPx, imageType);
		} else {
			result = new BufferedImage(heightPx, widthPx, imageType);
		}

		Graphics2D graphics = (Graphics2D) result.getGraphics();
		graphics.setBackground(TRANSPARENT_WHITE);

		graphics.clearRect(0, 0, result.getWidth(), result.getHeight());

		this.transform(graphics, getPage(), _factor, _factor);
		
		_graphics = (Graphics2D) graphics;

		return( result );
	}

	public void drawPage(Graphics g, PDRectangle pageSize) throws IOException {
		_image = createImage();

		super.drawPage( _graphics, pageSize );
	}

	protected MatrixWrapper createMatrixWrapper( Matrix matrix )
	{
		return( PDFboxWrapperUtils.instance().createMatrixWrapper(matrix) );
	}

	protected GlyphWrapper getGlyphWrapper(Matrix matrix)
	{
		return( _glyphMap.get( createMatrixWrapper( matrix ) ) );
	}

	protected GlyphWrapper adjustNextGlyph(Matrix textRenderingMatrix, PDRectangle cropBox )
	{
		GlyphWrapper gw = getGlyphWrapper(textRenderingMatrix);
		
		PDFboxWrapperUtils.instance().adjustGlyph( _image, cropBox,
												_factor, 1.0f, gw );
		return( gw );
	}

	protected Rectangle getDescendingYBounds( int pageHeight, Rectangle ascendingYBounds )
	{
		ascendingYBounds.y = pageHeight - ascendingYBounds.y - ascendingYBounds.height;
		return( ascendingYBounds );
	}

	protected void doTranslation( Rectangle bounds, PDRectangle cropBox )
	{
		bounds.x += cropBox.getLowerLeftX();
		bounds.y += cropBox.getLowerLeftY();
	}

	protected Rectangle undoTransform( GlyphWrapper gw )
	{
		Rectangle bounds = getDescendingYBounds( (int) getPage().getBBox().getHeight(),
												applyFactor( gw.getBounds(), 1.0f ) );
		doTranslation( bounds, getPage().getCropBox() );

//		Rectangle result = applyFactor(bounds, _factor);
		Rectangle result = bounds;
		return( result );
	}

	protected Rectangle applyFactor( Rectangle bounds, float factor )
	{
		return( PDFboxWrapperUtils.instance().applyFactor(bounds, factor) );
	}

	protected void clearGlyph( Matrix textRenderingMatrix )
	{
		GlyphWrapper gw = getGlyphWrapper(textRenderingMatrix);
		Rectangle rect = undoTransform( gw );

		_graphics.setColor( TRANSPARENT_WHITE );
		_graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
	}

	protected void incCount()
	{
		if( _count != null )
			_count.incrementAndGet();
	}

	@Override
	protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code,
							String unicode, Vector displacement) throws IOException
	{
		incCount();

		clearGlyph( textRenderingMatrix );

		super.showGlyph( textRenderingMatrix, font, code, unicode, displacement );

		adjustNextGlyph(textRenderingMatrix, getPage().getCropBox());
	}

	@Override
	public void drawImage(PDImage pdImage) throws IOException {
	}

    private void transform(Graphics2D graphics, PDPage page, float scaleX, float scaleY) {
        graphics.scale((double)scaleX, (double)scaleY);
        int rotationAngle = page.getRotation();
        PDRectangle cropBox = page.getCropBox();
        if (rotationAngle != 0) {
            float translateX = 0.0F;
            float translateY = 0.0F;
            switch(rotationAngle) {
            case 90:
                translateX = cropBox.getHeight();
                break;
            case 180:
                translateX = cropBox.getWidth();
                translateY = cropBox.getHeight();
                break;
            case 270:
                translateY = cropBox.getWidth();
            }

            graphics.translate((double)translateX, (double)translateY);
            graphics.rotate(Math.toRadians((double)rotationAngle));
        }

    }
}
