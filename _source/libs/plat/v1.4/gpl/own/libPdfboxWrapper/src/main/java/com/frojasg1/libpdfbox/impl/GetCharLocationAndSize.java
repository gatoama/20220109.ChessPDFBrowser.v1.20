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
import com.frojasg1.libpdf.api.impl.GlyphImpl;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

// https://github.com/kanishk-mehta/PDFBox-get-Coordinates-of-text/blob/master/source/BoomPdf.java
public class GetCharLocationAndSize extends PDFTextStripper
{
	protected List<GlyphWrapper> _listOfGlyphs = new ArrayList<>();

    public GetCharLocationAndSize() throws IOException {
    }

    /**
     * @throws IOException If there is an error parsing the document.
     */
    public static void main( String[] args ) throws IOException    {
        PDDocument document = null;
       // String fileName;
        String fileName= new String(args[0]);
//        String fileName= new String("C:\\Users\\barna.cherian\\Desktop\\test.pdf");
       // File inFile = new File(args[0]);
      // fileName = "C:\\Users\\barna.cherian\\Desktop\\apache.pdf";
        int x=0,y=0;
        String a= (args[1]);
//        String a= "0";
         x=Integer.parseInt(a);
        
        String b=(args[2]);
//        String b= "3";
         y=Integer.parseInt(b);
        
        try {
            document = PDDocument.load( new File(fileName) );
            PDFTextStripper stripper = new GetCharLocationAndSize();
            stripper.setSortByPosition( true );
            stripper.setStartPage(x);
            stripper.setEndPage(y);

            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            stripper.writeText(document, dummy);
        }
        finally {
            if( document != null ) {
                document.close();
            }
        }
    }

	protected void addGlyph( GlyphWrapper glyphWrapper )
	{
		_listOfGlyphs.add( glyphWrapper );
	}

	public List<GlyphWrapper> getGlyphList()
	{
		return( _listOfGlyphs );
	}

	protected int getWidth( int[] codes, PDFont font ) throws IOException
	{
		int result = 0;
		for( Integer code: codes )
			result += font.getWidth(code);
		return( result );
	}

	protected GlyphWrapper createGlyphWrapper( Matrix textRenderingMatrix, PDFont font,
							int[] codes, String unicode, Vector displacement) throws IOException
	{
		Shape bbox = new Rectangle2D.Float(0, 0, getWidth(codes, font) / 1000, 1);
		AffineTransform at = textRenderingMatrix.createAffineTransform();
        bbox = at.createTransformedShape(bbox);

		Rectangle bounds = bbox.getBounds();
		bounds.y = getPageHeight() - bounds.y - bounds.height;
		GlyphWrapper result = new GlyphImpl( codes, unicode, bounds );

		return( result );
	}

	protected GlyphWrapper createGlyphWrapper( TextPosition text ) throws IOException
	{
//		GlyphWrapper result = new GlyphImpl( text.getCharacterCodes(), text.getUnicode(), getBounds( text ) );

//		return( result );
		return( createGlyphWrapper( text.getTextMatrix(), text.getFont(), text.getCharacterCodes(),
									text.getUnicode(), null ) );
	}

	protected int getPageHeight()
	{
		return( (int) getCurrentPage().getBBox().getHeight() );
	}

	protected Rectangle getBounds( TextPosition text )
	{
		return( new Rectangle( (int) text.getXDirAdj(), (int) text.getYDirAdj(),
			(int) text.getWidthDirAdj(), (int) text.getHeightDir() ) );
	}

	/**
     * Override the default functionality of PDFTextStripper.writeString()
     */
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for (TextPosition text : textPositions) {
/*
			System.out.println(text.getUnicode()+ " [(X=" + text.getXDirAdj() + ",Y=" +
                    text.getYDirAdj() + ") height=" + text.getHeightDir() + " width=" +
                    text.getWidthDirAdj() + "]");
*/
			addGlyph( createGlyphWrapper( text ) );
		}
    }
}