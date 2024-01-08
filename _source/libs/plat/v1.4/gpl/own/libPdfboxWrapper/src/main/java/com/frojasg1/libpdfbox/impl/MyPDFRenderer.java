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

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.libpdf.api.GlyphWrapper;
import com.frojasg1.libpdf.api.ImageWrapper;
import com.frojasg1.libpdf.api.impl.GlyphImpl;
import com.frojasg1.libpdf.api.impl.ImageImpl;
import com.frojasg1.libpdfbox.utils.PDFboxWrapperUtils;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

/**
 *
 * @author Usuario
 */
public class MyPDFRenderer extends PDFRenderer
{
	protected boolean _showText = true;
	protected boolean _getGlyphsAndImages = false;

	protected List<GlyphWrapper> _listOfGlyphs = new ArrayList<>();
	protected List<ImageWrapper> _listOfImages = new ArrayList<>();

    private AffineTransform flipAT;
    private AffineTransform rotateAT;
    private AffineTransform transAT;

	public MyPDFRenderer(PDDocument document)
	{
		super(document);
	}

	public List<ImageWrapper> getImageList()
	{
		return( _listOfImages );
	}

	public void setShowText( boolean showText )
	{
		_showText = showText;
	}

	public void setGetGlyphsAndImages( boolean getGlyphsAndImages )
	{
		_getGlyphsAndImages = getGlyphsAndImages;
	}

	public boolean isShowText() {
		return _showText;
	}

	public boolean isGetGlyphsAndImages() {
		return _getGlyphsAndImages;
	}

	protected void addGlyph( GlyphWrapper glyphWrapper )
	{
		_listOfGlyphs.add( glyphWrapper );
	}

	public List<GlyphWrapper> getGlyphList()
	{
		return( _listOfGlyphs );
	}

	@Override
	protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
	{
		return new MyPageDrawer(parameters);
	}

    /**
     * Example PageDrawer subclass with custom rendering.
     */
    private class MyPageDrawer extends PageDrawer
    {
        MyPageDrawer(PageDrawerParameters parameters) throws IOException
        {
            super(parameters);
        }

        /**
         * Color replacement.
         */
        @Override
        protected Paint getPaint(PDColor color) throws IOException
        {
            // if this is the non-stroking color
            if (getGraphicsState().getNonStrokingColor() == color)
            {
                // find red, ignoring alpha channel
                if (color.toRGB() == (Color.RED.getRGB() & 0x00FFFFFF))
                {
                    // replace it with blue
                    return Color.BLUE;
                }
            }
            return super.getPaint(color);
        }

		protected GlyphWrapper createGlyphWrapper( Matrix textRenderingMatrix, PDFont font,
								int code, String unicode, Vector displacement) throws IOException
		{
            Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
            AffineTransform at = textRenderingMatrix.createAffineTransform();
	        bbox = at.createTransformedShape(bbox);

			Rectangle bounds = bbox.getBounds();
			bounds.y = getPageHeight() - bounds.y - bounds.height;
			GlyphWrapper result = new GlyphImpl( new int[] { code }, unicode, bounds );

			return( result );
		}

		protected int getPageHeight()
		{
			return( (int) getCurrentPage().getBBox().getHeight() );
		}

		protected void createTransformations()
		{
		    PDPage pdPage = getCurrentPage();
	        PDRectangle cropBox = pdPage.getCropBox();

			flipAT = new AffineTransform();
			flipAT.translate(0, pdPage.getBBox().getHeight());
			flipAT.scale(1, -1);

			// pageIndex may be rotated
			rotateAT = new AffineTransform();
			int rotation = pdPage.getRotation();
			if (rotation != 0)
			{
				PDRectangle mediaBox = pdPage.getMediaBox();
				switch (rotation)
				{
					case 90:
						rotateAT.translate(mediaBox.getHeight(), 0);
						break;
					case 270:
						rotateAT.translate(0, mediaBox.getWidth());
						break;
					case 180:
						rotateAT.translate(mediaBox.getWidth(), mediaBox.getHeight());
						break;
					default:
						break;
				}
				rotateAT.rotate(Math.toRadians(rotation));
			}

			// cropbox
			transAT = AffineTransform.getTranslateInstance(-cropBox.getLowerLeftX(), cropBox.getLowerLeftY());
		}

		/**
         * Glyph bounding boxes.
         */
        @Override
        protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                                 Vector displacement) throws IOException
        {
			if( isGetGlyphsAndImages() )
			{
				GlyphWrapper glyphWrapper = createGlyphWrapper( textRenderingMatrix, font, code, unicode, displacement);
				addGlyph( glyphWrapper );
			}

			if( isShowText() )
			{
	            // draw glyph
		        super.showGlyph(textRenderingMatrix, font, code, unicode, displacement);
			}
/*
            // bbox in EM -> user units
            Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
            AffineTransform at = textRenderingMatrix.createAffineTransform();
            bbox = at.createTransformedShape(bbox);
            
            // save
            Graphics2D graphics = getGraphics();
            Color color = graphics.getColor();
            Stroke stroke = graphics.getStroke();
            Shape clip = graphics.getClip();

            // draw
            graphics.setClip(graphics.getDeviceConfiguration().getBounds());
            graphics.setColor(Color.RED);
            graphics.setStroke(new BasicStroke(.5f));
            graphics.draw(bbox);

            // restore
            graphics.setStroke(stroke);
            graphics.setColor(color);
            graphics.setClip(clip);
*/
        }
		
		/**
         * Filled path bounding boxes.
         */
        @Override
        public void fillPath(int windingRule) throws IOException
        {
            // bbox in user units
//            Shape bbox = getLinePath().getBounds2D();
            
            // draw path (note that getLinePath() is now reset)
            super.fillPath(windingRule);

/*
            // save
            Graphics2D graphics = getGraphics();
            Color color = graphics.getColor();
            Stroke stroke = graphics.getStroke();
            Shape clip = graphics.getClip();

            // draw
            graphics.setClip(graphics.getDeviceConfiguration().getBounds());
            graphics.setColor(Color.GREEN);
            graphics.setStroke(new BasicStroke(.5f));
            graphics.draw(bbox);

            // restore
            graphics.setStroke(stroke);
            graphics.setColor(color);
            graphics.setClip(clip);
*/
		}

        /**
         * Custom annotation rendering.
         */
        @Override
        public void showAnnotation(PDAnnotation annotation) throws IOException
        {
            // save
            saveGraphicsState();
            
            // 35% alpha
            getGraphicsState().setNonStrokeAlphaConstants(0.35);
            super.showAnnotation(annotation);
            
            // restore
            restoreGraphicsState();
        }

		@Override
		public void drawImage(PDImage pdImage) throws IOException {
			super.drawImage(pdImage);

			if( isGetGlyphsAndImages() )
			{
				ImageWrapper image = createImageWrapper( pdImage );
				if( image != null )
					_listOfImages.add( image );
			}
		}

		// ami3
		private Rectangle getBoundingRect() {

			Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
			AffineTransform at = ctm.createAffineTransform();
            Shape bbox = new Rectangle2D.Float(0, 0, 1, 1);
	        bbox = at.createTransformedShape(bbox);

            bbox = flipAT.createTransformedShape(bbox);
            bbox = rotateAT.createTransformedShape(bbox);
            bbox = transAT.createTransformedShape(bbox);

			Rectangle result = bbox.getBounds();

			return( result );
		}

		protected ImageWrapper createImageWrapper( PDImage pdImage )
		{
			ImageWrapper result = null;
			BufferedImage image = ExecutionFunctions.instance().safeFunctionExecution( () -> pdImage.getImage() );
			if( image != null )
			{
				createTransformations();
				Rectangle rect = getBoundingRect();
				result = new ImageImpl( image, rect );
			}

			return( result );
		}
	}
}
