/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.libpdfbox.utils;

import com.frojasg1.libpdf.api.GlyphWrapper;
import com.frojasg1.libpdf.api.impl.GlyphImpl;
import com.frojasg1.libpdf.utils.PdfUtils;
import com.frojasg1.libpdfbox.impl.MatrixWrapper;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PDFboxWrapperUtils extends PdfUtils
{
	protected static PDFboxWrapperUtils _instance;

	public static PDFboxWrapperUtils instance()
	{
		if( _instance == null )
			_instance = new PDFboxWrapperUtils();
		
		return( _instance );
	}

	public void adjustGlyph( BufferedImage pageImage, PDRectangle cropBox,
							float pageFactor, float boundsFactor,
							GlyphWrapper result )
	{
		GlyphImpl gi = (GlyphImpl) result;
		float multiplierFactor = pageFactor / boundsFactor;
//		float multiplierFactor = 1.0f;
//		Rectangle multipliedBounds = applyFactor( bounds, multiplierFactor );
		Rectangle multipliedBounds = applyFactor( result.getBounds(), multiplierFactor );

		gi.setImage( getGlyphImage( pageImage, multipliedBounds ) );
//		gi.setImage( getGlyphImage( pageImage, result.getBounds() ) );
		applyFactorNoCopy( result.getBounds(), 1 / boundsFactor );
	}

	public MatrixWrapper createMatrixWrapper( Matrix matrix )
	{
		return( new MatrixWrapper(matrix) );
	}
}
