/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.editorpane;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.color.components.ColorInversorJEditorPane;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableForCustomComponent;
import com.frojasg1.general.desktop.view.text.utils.TextViewFunctions;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FastColorInversorStaticDocumentJEditorPane extends ColorInversorJEditorPane
	{
		protected Integer _previousSelectionStart = null;
		protected Integer _previousSelectionEnd = null;

		protected int _newSelectionStart = 0;
		protected int _newSelectionEnd = 0;

		protected BufferedImage _previousImage = null;

		protected Boolean _wasLatestModeDark = null;

		public FastColorInversorStaticDocumentJEditorPane() {
			super();
		}

		public FastColorInversorStaticDocumentJEditorPane(String type, String text) {
			super(type, text);
		}

		protected boolean isDarkMode()
		{
			return( createColorThemeChangeableStatus().isDarkMode() );
		}

		protected BufferedImage paintNewImage(Rectangle clip)
		{
			BufferedImage result = new BufferedImage( getWidth(),
													getHeight(),
													BufferedImage.TYPE_INT_ARGB );
			Graphics grp2 = result.createGraphics();
			grp2.setClip( clip );
			superPaint( grp2 );

			return( result );
		}

		protected boolean hasToInvertColors()
		{
			return( createColorThemeChangeableStatus().wasOriginallyDark() != isDarkMode() );
		}

		protected void updateNewSelection()
		{
			_newSelectionStart = getSelectionStart();
			_newSelectionEnd = getSelectionEnd();
		}

		protected void updatePreviousSelection()
		{
			_previousSelectionStart = _newSelectionStart;
			_previousSelectionEnd = _newSelectionEnd;
		}

		protected int getNewSelectionStart()
		{
			return( _newSelectionStart );
		}

		protected int getNewSelectionEnd()
		{
			return( _newSelectionEnd );
		}

		protected void paintChild( Graphics grp )
		{
			Rectangle clip = grp.getClipBounds();
			updateNewSelection();
			if( ( _previousImage == null ) ||
				!Objects.equals( _wasLatestModeDark, isDarkMode() ) )
			{
				_wasLatestModeDark = isDarkMode();
				_previousImage = paintNewImage( new Rectangle( 0, 0, getWidth(), getHeight() ) );

				if( hasToInvertColors() )
					_previousImage = ImageFunctions.instance().invertImage( _previousImage );
			}
			else if( hasSelectionChanged() )
			{
				try
				{
					List<Rectangle> rects = calculateOptimizedBoundsOfChangingAreas();
					for( Rectangle rect: rects )
					{
						clip = addArea( clip, rect );
						BufferedImage newImage = paintNewImage(rect);
//						ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(newImage, "png", new File( "J:\\diff.png" ) ) );
						modifyPreviousImage( _previousImage, newImage, rect );
//						ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(_previousImage, "png", new File( "J:\\prevImage.png" ) ) );
					}
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
//					if( hasToInvertColors() )
//						_previousImage = ImageFunctions.instance().invertImage( newImage );
				}
			}

			grp.setClip(clip);
			paintImage(grp, _previousImage);
			updatePreviousSelection();
		}

		protected List<Rectangle> createListWithOneRectangleWithAllChanges()
		{
			List<Rectangle> result = new ArrayList<>();

			Rectangle rect = calculateBoundsOfChangingArea();
			if( rect != null )
				result.add( rect );

			return( result );
		}

		protected List<Rectangle> calculateOptimizedBoundsOfChangingAreas()
		{
//			List<Rectangle> result1 = calculateBoundsOfChangingAreas();
			List<Rectangle> result2 = createListWithOneRectangleWithAllChanges();

//			return( getMinimumAreaList( result1, result2 ) );
			return( result2 );
		}

		protected List<Rectangle> getMinimumAreaList( List<Rectangle> list1, List<Rectangle> list2 )
		{
			List<Rectangle> result = null;
			if( list1.isEmpty() )
				result = list2;
			else if( list2.isEmpty() )
				result = list1;
			else if( areas(list1) < areas(list2) )
				result = list1;
			else
				result = list2;

			return( result );
		}

		protected long area( Rectangle rect )
		{
			return( ( rect == null ) ? 0 : rect.width * rect.height );
		}

		protected long areas( List<Rectangle> list )
		{
			long result = 0;
			for( Rectangle rect: list )
				result += area( rect );

			return( result );
		}

		protected boolean hasSelectionStartChanged()
		{
			return( _previousSelectionStart != getNewSelectionStart() );
		}

		protected boolean hasSelectionEndChanged()
		{
			return( _previousSelectionEnd != getNewSelectionEnd() );
		}

		protected List<Rectangle> calculateBoundsOfChangingAreas()
		{
			List<Rectangle> result = new ArrayList<>();

			if( ( _previousSelectionStart != null ) &&
				( _previousSelectionEnd != null ) )
			{
				List<Integer> list = new ArrayList();
				// if either start or end have not changed, selection difference will be between the changed ones.
				if( !hasSelectionStartChanged() || !hasSelectionEndChanged() )
				{
					if( hasSelectionStartChanged() )
					{
						list.add( _previousSelectionStart );
						list.add( getNewSelectionStart() );
					}
					else if( hasSelectionEndChanged() )
					{
						list.add( _previousSelectionEnd );
						list.add( getNewSelectionEnd() );
					}
				}
				else // if all have changed, we sort them and take them in pairs
				{
					list.add( _previousSelectionStart );
					list.add( getNewSelectionStart() );
					list.add( _previousSelectionEnd );
					list.add( getNewSelectionEnd() );
				}
	
				if( ! list.isEmpty() )
				{
					Collections.sort(list);
					for( int ii=0; ii<list.size(); ii+=2 )
					{
						Rectangle rect = calculateBoundsOfSelection( null, list.get(ii), list.get(ii+1) );
						if( rect != null )	// could be null if those two elements of the list were equal
							result.add( rect );
					}
				}
			}

			return( result );
		}


		protected void paintImage( Graphics grp, BufferedImage image )
		{
			grp.drawImage(image,
							0, 0,
							image.getWidth(), image.getHeight(),
							null );
		}

		protected void modifyPreviousImage( BufferedImage previousImage,
											BufferedImage newImage,
											Rectangle rect)
		{
			if( area( rect ) > 0 )
			{
				BufferedImage changingImage = ImageFunctions.instance().getSubImage( newImage, rect );
				if( hasToInvertColors() )
					changingImage = ImageFunctions.instance().invertImage(changingImage);

				_previousImage = ImageFunctions.instance().addSpriteToImage(_previousImage,
					changingImage, new Coordinate2D( rect.x, rect.y ) );
			}
		}

		protected Rectangle calculateBoundsOfChangingArea()
		{
			Rectangle result = null;
			result = calculateBoundsOfSelection( result, _previousSelectionStart, _previousSelectionEnd );
			result = calculateBoundsOfSelection( result, getNewSelectionStart(), getNewSelectionEnd() );
			return( result );
		}

		protected Rectangle calculateBoundsOfSelection( Rectangle origRect, int start, int end )
		{
			Rectangle charBounds = null;
			Rectangle result = origRect;
			for( int ii=start; ii<end; ii++ )
				result = addArea( result, (charBounds = getCharacterBounds(ii)) );

			if( charBounds != null )
				result = widenBounds( result, charBounds.width, charBounds.height );

			return( result );
		}

		protected Rectangle addArea( Rectangle origRect, Rectangle areaToAdd )
		{
			return( ViewFunctions.instance().addArea(origRect, areaToAdd) );
		}

		protected Rectangle widenBounds( Rectangle origRect, int width, int height )
		{
			return( ViewFunctions.instance().widenBounds(origRect, width, height,
														getSize() ) );
		}

		public Rectangle getCharacterBounds(int index)
		{
			return( TextViewFunctions.instance().getCharacterBounds(this, index) );
		}

		protected boolean hasSelectionChanged()
		{
			return( !Objects.equals( _previousSelectionStart, getNewSelectionStart() ) ||
					!Objects.equals( _previousSelectionEnd, getNewSelectionEnd() ) );
		}

		@Override
		public ColorThemeChangeableForCustomComponent createColorThemeChangeableStatus()
		{
			if( _colorThemeStatus == null )
				_colorThemeStatus = new ColorThemeChangeableForCustomComponent( this, grp -> superPaint(grp) , false) {
					@Override
					public void paint( Graphics grp )
					{
						paintChild( grp );
					}
				};
			return( _colorThemeStatus );
		}
	}
