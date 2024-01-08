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
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.libpdf.api.ImageWrapper;
import com.frojasg1.libpdf.api.impl.ImageImpl;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

/**
 *	adapted from org.apache.pdfbox.examples.util.PrintImageLocations
 * @author Ben Litchfield
 */

public class GetImageLocationsAndSize  extends PDFStreamEngine
{
	protected List<ImageWrapper> _result = new ArrayList<>();

	public void GetImageLocationsAndSize() throws IOException  
    {
		// preparing PDFStreamEngine  
        addOperator(new Concatenate());  
        addOperator(new DrawObject());  
        addOperator(new SetGraphicsStateParameters());  
        addOperator(new Save());  
        addOperator(new Restore());  
        addOperator(new SetMatrix());  
    }     

	public List<ImageWrapper> getImageList()
	{
		return( _result );
	}

	protected ImageWrapper createImageWrapper( PDImageXObject pdImage )
	{
		BufferedImage image = ExecutionFunctions.instance().safeFunctionExecution( () -> pdImage.getImage() );
		ImageWrapper result = null;
		
		if( image != null )
			result = new ImageImpl( image, getBounds( pdImage ) );

		return( result );
	}

	protected Rectangle getBounds( PDImageXObject pdImage )
	{
		int imageWidth = pdImage.getWidth();
		int imageHeight = pdImage.getHeight();
//		System.out.println("*******************************************************************");
//		System.out.println("Found image [" + objectName.getName() + "]");

		Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
		float imageXScale = ctmNew.getScalingFactorX();
		float imageYScale = ctmNew.getScalingFactorY();
/*
		// position in user space units. 1 unit = 1/72 inch at 72 dpi
		System.out.println("position in PDF = " + ctmNew.getTranslateX() + ", " + ctmNew.getTranslateY() + " in user space units");
		// raw size in pixels
		System.out.println("raw image size  = " + imageWidth + ", " + imageHeight + " in pixels");
		// displayed size in user space units
		System.out.println("displayed size  = " + imageXScale + ", " + imageYScale + " in user space units");
		// displayed size in inches at 72 dpi rendering
		imageXScale /= 72;
		imageYScale /= 72;
		System.out.println("displayed size  = " + imageXScale + ", " + imageYScale + " in inches at 72 dpi rendering");
		// displayed size in millimeters at 72 dpi rendering
		imageXScale *= 25.4;
		imageYScale *= 25.4;
		System.out.println("displayed size  = " + imageXScale + ", " + imageYScale + " in millimeters at 72 dpi rendering");
		System.out.println();
*/
		Rectangle result = new Rectangle( (int) ctmNew.getTranslateX(), (int) ctmNew.getTranslateY(),
											IntegerFunctions.zoomValueRound(imageWidth, imageXScale),
											IntegerFunctions.zoomValueRound(imageHeight, imageYScale));

		return( result );
	}

	protected int getPageHeight()
	{
		return( (int) getCurrentPage().getBBox().getHeight() );
	}

	/**
     * This is used to handle an operation.
     *
     * @param operator The operation to perform.
     * @param operands The list of arguments.
     *
     * @throws IOException If there is an error processing the operation.
     */
    @Override
    protected void processOperator( Operator operator, List<COSBase> operands) throws IOException
    {
        String operation = operator.getName();
        if (OperatorName.DRAW_OBJECT.equals(operation))
        {
            COSName objectName = (COSName) operands.get( 0 );
            PDXObject xobject = getResources().getXObject( objectName );
            if( xobject instanceof PDImageXObject)
            {
                PDImageXObject pdImage = (PDImageXObject)xobject;

				ImageWrapper image = createImageWrapper( pdImage );
				if( image != null )
					_result.add( image );
            }
            else if(xobject instanceof PDFormXObject)
            {
                PDFormXObject form = (PDFormXObject)xobject;
                showForm(form);
            }
        }
        else
        {
            super.processOperator( operator, operands);
        }
    }
}  
