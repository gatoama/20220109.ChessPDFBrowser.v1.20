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
package com.frojasg1.general.desktop.view;

import com.frojasg1.applications.common.components.name.ComponentNameComponents;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.screen.ScreenFunctions;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.view.ViewComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Usuario
 */
public class ViewFunctions
{
	public static final Point ORIGIN = new Point( 0, 0 );

	protected static ViewFunctions _instance;

	protected static final Map<String, Insets> _mapOfBorders;

	static
	{
		_mapOfBorders = new HashMap<String, Insets>();
/*
		JFrame jframe = new JFrame();
		Insets bordersOfJFrame = getFrameBorders_static( jframe );
		_mapOfBorders.put( "JFrame", bordersOfJFrame );

		JDialog jdialog = new JDialog( jframe );
		Insets bordersOfJDialog = getFrameBorders_static( jdialog );
		_mapOfBorders.put( "JDialog", bordersOfJDialog );
*/
	}

	public static void changeInstance( ViewFunctions inst )
	{
		_instance = inst;
	}

	public static ViewFunctions instance()
	{
		if( _instance == null )
			_instance = new ViewFunctions();
		return( _instance );
	}

	protected boolean isInside( int min, int max, int value )
	{
		return( ( value >= min ) && ( value <= max ) );
	}

	protected boolean isNear( int val1, int val2, int maxDiff )
	{
		return( Math.abs( val1 - val2 ) <= maxDiff );
	}

	protected boolean isAtLeft( Rectangle bounds, Point position, int maxDiff )
	{
		return( isInside( bounds.y, bounds.y + bounds.height, position.y ) &&
				isNear( bounds.x, position.x, maxDiff ) );
	}

	protected boolean isAtRight( Rectangle bounds, Point position, int maxDiff )
	{
		return( isInside( bounds.y, bounds.y + bounds.height, position.y ) &&
				isNear( bounds.x + bounds.width, position.x, maxDiff ) );
	}

	protected boolean isAtTop( Rectangle bounds, Point position, int maxDiff )
	{
		return( isInside( bounds.x, bounds.x + bounds.width, position.x ) &&
				isNear( bounds.y, position.y, maxDiff ) );
	}

	protected boolean isAtBottom( Rectangle bounds, Point position, int maxDiff )
	{
		return( isInside( bounds.x, bounds.x + bounds.width, position.x ) &&
				isNear( bounds.y + bounds.height, position.y, maxDiff ) );
	}

	public boolean isAtBorder( Rectangle bounds, Point position, int maxDiff )
	{
		boolean result = false;
		if( ( bounds != null ) && ( position != null ) )
		{
			result = isAtLeft( bounds, position, maxDiff ) ||
					isAtRight( bounds, position, maxDiff ) ||
					isAtTop( bounds, position, maxDiff ) ||
					isAtBottom( bounds, position, maxDiff );
		}
		return( result );
	}

	public BufferedImage addImageToButtonFast( AbstractButton button, String resourceName, Insets insets )
	{
		return( addImageToButtonGeneric( button, resourceName, insets,
					(b,i,xMargin,yMargin) ->
						ImageFunctions.instance().resizeImage( i, b.getWidth()-xMargin, b.getHeight()-yMargin, null, null, null ) )
			);
	}

	public BufferedImage addImageToButtonAccurate( AbstractButton button, String resourceName, Insets insets )
	{
		return( addImageToButtonGeneric( button, resourceName, insets,
					(b,i,xMargin,yMargin) ->
						ImageFunctions.instance().resizeImageAccurately(i, b.getWidth()-xMargin, b.getHeight()-yMargin ) )
			);
	}

	public BufferedImage addImageToButtonAccurate( AbstractButton button, BufferedImage origImg, Insets insets )
	{
		return( addImageToButtonGeneric( button, origImg, insets,
				(b,i,xMargin,yMargin) ->
					ImageFunctions.instance().resizeImageAccurately(i, b.getWidth()-xMargin, b.getHeight()-yMargin ) )
			);
	}

	public BufferedImage addImageToButtonGeneric( AbstractButton button, String resourceName, Insets insets,
										CreateImageForButton imageCreatorForButton )
	{
		BufferedImage result = null;
		if( resourceName != null )
		{
			BufferedImage origImg = ExecutionFunctions.instance().safeSilentFunctionExecution( () -> ImageFunctions.instance().loadImageFromJar(resourceName) );
			result = addImageToButtonGeneric( button, origImg, insets, imageCreatorForButton );
		}
		return( result );
	}

	public BufferedImage addImageToButtonGeneric( AbstractButton button, BufferedImage origImg, Insets insets,
										CreateImageForButton imageCreatorForButton )
	{
		BufferedImage result = null;
		if( ( button.getWidth() > 0 ) &&
			( button.getHeight() > 0 ) )
		{
			try
			{
				int xMargin = 0;
				int yMargin = 0;

				button.setVerticalTextPosition( SwingConstants.CENTER );
				button.setHorizontalTextPosition( SwingConstants.CENTER );

				if( insets != null )
				{
					xMargin = insets.left + insets.right;
					yMargin = insets.top + insets.bottom;

					button.setMargin( insets );
				}

				if( origImg != null )
				{
					BufferedImage resizedImage = imageCreatorForButton.createImage(button, origImg, xMargin, yMargin );

					button.setIcon(new ImageIcon(resizedImage));
					
					result = resizedImage;
				}
			}
			catch (Throwable ex)
			{
				ex.printStackTrace();
			}
		}

		return( result );
	}

	public Component getRootAncestor( Component comp )
	{
		Component result = comp;

		while( ( result != null ) &&
				!( result instanceof JFrame ) &&
				!( result instanceof JDialog ) &&
				!( result instanceof JInternalFrame ) &&
				( result.getParent() != null )  )
		{
			result = result.getParent();
		}

		return( result );
	}

	protected void setFocusedComponent_nonEDT( Component comp )
	{
		SwingUtilities.invokeLater( new Runnable() {
					@Override
					public void run()
					{
						setFocusedComponent( comp );
					}
		});
	}

	public void setFocusedComponent( Component comp )
	{
		if( !SwingUtilities.isEventDispatchThread() )
		{
			setFocusedComponent_nonEDT( comp );
			return;
		}

		if( comp != null )
		{
			Component rootAncestror = getRootAncestor( comp );

			rootAncestror.setVisible( true );
			comp.requestFocusInWindow();
		}
	}

	public int getNumberOfChild( Container parent, Component comp )
	{
		int result = -1;

		for( int ii=0; ( result == -1 ) && ( ii<parent.getComponentCount() ); ii++ )
		{
			if( parent.getComponent( ii ) == comp )
				result = ii;
		}

		return( result );
	}

	public String getComponentTrace( Component comp )
	{
		String result = "";

		Container parent = comp.getParent();
		while( parent != null )
		{
			result = String.valueOf( getNumberOfChild( parent, comp ) ) + "-" + result;
			comp = parent;
			parent = comp.getParent();
		}

		return( result );
	}

	public String traceComponentTree( Component comp )
	{
		return( traceComponentTree( comp, (component) -> "" ) );
	}

	public String traceComponentTree( Component comp, GetComponentDetail gcd )
	{
		StringBuilder sb = new StringBuilder();

		traceComponentTree( sb, comp, "", gcd );

		return( sb.toString() );
	}

	public String traceComponentTreeSizes( Component comp )
	{
		return( traceComponentTree( comp, (component) -> "preferredSize : " + component.getPreferredSize() + "     size : " + component.getSize() ) );
	}

	protected void appendToSb( StringBuilder sb, String indentation, String str )
	{
		sb.append( indentation ).append( str ).append( String.format( "%n" ) );
	}

	protected String getComponentDetailString( Component comp, GetComponentDetail gcd )
	{
		String defaultMessage = "null";
		if( comp != null )
			defaultMessage = comp.getClass().getName() + "( name: " + comp.getName() + ")";

		String detail = ( gcd == null ? "" : gcd.getComponentDetail(comp) );
		String result = null;
		if( ( detail == null ) || ( detail.isEmpty() ) )
			result = defaultMessage;
		else
			result = String.format( "%s ----> %s", defaultMessage, detail );

		return( result );
	}

	protected void traceSingleComponent( StringBuilder sb, Component comp, String indentation, GetComponentDetail gcd )
	{
		appendToSb( sb, indentation, getComponentDetailString( comp, gcd ) );
	}

	public void traceComponentTree( StringBuilder sb, Component comp, String indentation, GetComponentDetail gcd )
	{
		String nextIndentation = indentation + "  ";

		traceSingleComponent( sb, comp, indentation, gcd );

		if( comp instanceof JTabbedPane )
		{
			JTabbedPane tp = (JTabbedPane) comp;
			
			appendToSb( sb, indentation, "{" );

			for( int ii=0; ii<tp.getTabCount(); ii++ )
			{
				traceComponentTree( sb, tp.getComponentAt(ii), nextIndentation, gcd );
			}

			appendToSb( sb, indentation, "}" );
		}
		else if( comp instanceof JComboBox )
		{
			JComboBox combo = (JComboBox) comp;
			BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);

			appendToSb( sb, indentation, "{" );

			traceComponentTree( sb,  popup, nextIndentation, gcd );

			appendToSb( sb, indentation, "}" );
		}

		if( comp instanceof Container )
		{
			Container cont = (Container) comp;

			appendToSb( sb, indentation, "{" );

			for( int ii=0; ii<cont.getComponentCount(); ii++ )
				traceComponentTree( sb, cont.getComponent(ii), nextIndentation, gcd );

			appendToSb( sb, indentation, "}" );

			if( comp instanceof JMenu )
			{
				JMenu jMenu = (JMenu) comp;

				appendToSb( sb, indentation, "{" );

				traceComponentTree( sb,  jMenu.getPopupMenu(), nextIndentation, gcd );

				appendToSb( sb, indentation, "}" );
			}
		}
	}

	public Dimension getNewDimension( Dimension dim, Insets insets, double zoomFactor )
	{
		Dimension result = null;
		
		if( dim != null )
		{
			if( insets == null )
				insets = new Insets(0,0,0,0);

			int insetsWidth = insets.left + insets.right;
			int insetsHeight = insets.top + insets.bottom;
			result = new Dimension( IntegerFunctions.zoomValueCeil( dim.width - insetsWidth, zoomFactor ) + insetsWidth,
									IntegerFunctions.zoomValueCeil( dim.height - insetsHeight, zoomFactor ) + insetsHeight );
		}

		return( result );
	}

	public Insets getInsets( Component comp )
	{
		Insets result = null;
		if( comp instanceof JComponent )
			result = ( (JComponent) comp ).getInsets();

		return( result );
	}

	public Rectangle getNewRectangle( Rectangle rect, Insets insets, double zoomFactor )
	{
		Rectangle result = null;

		if( rect != null )
		{
			if( insets == null )
				insets = new Insets(0,0,0,0);

			int insetsWidth = insets.left + insets.right;
			int insetsHeight = insets.top + insets.bottom;
			result = new Rectangle( IntegerFunctions.zoomValueCeil( rect.x + insets.left, zoomFactor )  - insets.left,
										IntegerFunctions.zoomValueCeil( rect.y + insets.top, zoomFactor)  - insets.top,
										IntegerFunctions.zoomValueCeil( rect.width - insetsWidth, zoomFactor)  + insetsWidth,
										IntegerFunctions.zoomValueCeil( rect.height - insetsHeight, zoomFactor) + insetsHeight );
		}

		return( result );
	}

	public Dimension getMaxComponentsDimension( Dimension dim1, Dimension dim2 )
	{
		Dimension result = null;

		if( ( dim1 != null ) && ( dim2 == null ) )
			result = dim1;
		else if( ( dim2 != null ) && ( dim1 == null ) )
			result = dim2;
		else if( (dim1 != null) && (dim2 != null ) )
		{
			if( ( dim1.width >= dim2.width ) &&
				( dim1.height >= dim2.height ) )
			{
				result = dim1;
			}
			else if( ( dim2.width >= dim1.width ) &&
					( dim2.height >= dim1.height ) )
			{
				result = dim2;
			}
			else
			{
				result = new Dimension( IntegerFunctions.max( dim1.width, dim2.width ),
										IntegerFunctions.max( dim1.height, dim2.height ) );
			}
		}

		return( result );
	}

	public Dimension addLineDimension( Dimension dimenToAdd, Dimension totalDimen )
	{
		Dimension result = totalDimen;

		if( totalDimen != null )
		{
			if( dimenToAdd != null )
			{
				totalDimen.width = IntegerFunctions.max(totalDimen.width, dimenToAdd.width );
				totalDimen.height = totalDimen.height + dimenToAdd.height;
			}
		}

		return( result );
	}

	public Dimension getNewDimension( Dimension dim, double zoomFactor )
	{
		Dimension result = null;

		if( dim != null )
		{
			result = new Dimension( IntegerFunctions.zoomValueCeil( dim.width, zoomFactor ),
										IntegerFunctions.zoomValueCeil( dim.height, zoomFactor) );
		}

		return( result );
	}

	public Point getNewPoint( Point inputPoint, double zoomFactor )
	{
		Point result = null;

		if( inputPoint != null )
		{
			result = new Point( IntegerFunctions.zoomValueCeil( inputPoint.x, zoomFactor ),
								IntegerFunctions.zoomValueCeil( inputPoint.y, zoomFactor) );
		}

		return( result );
	}

	public Insets getNewInsets( Insets insets, double zoomFactor )
	{
		Insets result = null;

		if( insets != null )
		{
			result = new Insets( IntegerFunctions.zoomValueFloor( insets.top, zoomFactor ),
									IntegerFunctions.zoomValueFloor( insets.left, zoomFactor ),
									IntegerFunctions.zoomValueFloor( insets.bottom, zoomFactor ),
									IntegerFunctions.zoomValueFloor( insets.right, zoomFactor ) );
		}

		return( result );
	}

	public boolean rectanglesOverlap( Rectangle rect1, Rectangle rect2 )
	{
		boolean result = true;

		if( ( rect1 == null ) || ( rect2 == null ) )
			result = false;
		else
		{
			int bottom1 = rect1.y + rect1.height;
			int bottom2 = rect2.y + rect2.height;
			int right1 = rect1.x + rect1.width;
			int right2 = rect2.x + rect2.width;
			result = !( ( rect1.y > bottom2 ) || ( rect2.y > bottom1 ) ||
						( rect1.x > right2 ) || ( rect2.x > right1 ) );
		}

		return( result );
	}

	public Point getCenter( Rectangle rect )
	{
		Point result = null;

		if( rect != null )
		{
			result = new Point( (int) ( rect.getX() + rect.getWidth() / 2 ),
								(int) ( rect.getY() + rect.getHeight() / 2 ) );
		}

		return( result );
	}

	public Point getValidPositionToPlaceComponent( Dimension size, Point position )
	{
		Point result = position;

		Rectangle boundsOfMaxWindow = ScreenFunctions.getBoundsOfMaxWindow();

		int maximumYYBound = boundsOfMaxWindow.y + boundsOfMaxWindow.height - size.height;
		int minimumYYBound = boundsOfMaxWindow.y;

		int maximumXXBound = boundsOfMaxWindow.x + boundsOfMaxWindow.width - size.width;
		int minimumXXBound = boundsOfMaxWindow.x;

		int yyCoordinate = IntegerFunctions.limit( position.y, minimumYYBound, maximumYYBound );
		int xxCoordinate = IntegerFunctions.limit( position.x, minimumXXBound, maximumXXBound );

		result.x = xxCoordinate;
		result.y = yyCoordinate;

		return( result );
	}

	public Point getValidPositionToPlaceComponent( Component comp, Point position )
	{
		return( getValidPositionToPlaceComponent( comp.getSize(), position ) );
	}

	public Rectangle calculateNewBounds( Rectangle originalBounds, Insets insets, Point center, double zoomFactor )
	{
		Rectangle result = null;

		if( originalBounds != null )
		{
			if( insets == null )
				insets = new Insets( 0, 0, 0, 0 );

			int newX = (int) originalBounds.getX();
			int newY = (int) originalBounds.getY();
			if( center != null )
			{
				newX = center.x - insets.left + IntegerFunctions.zoomValueInt(originalBounds.x - center.x + insets.left, zoomFactor );
				newY = center.y  - insets.top + IntegerFunctions.zoomValueInt( originalBounds.y - center.y + insets.top, zoomFactor );
			}

			int newWidth = IntegerFunctions.zoomValueFloor( originalBounds.width - insets.left - insets.right, zoomFactor ) + insets.left + insets.right;
			int newHeight = IntegerFunctions.zoomValueFloor( originalBounds.height - insets.top - insets.bottom, zoomFactor ) + insets.top + insets.bottom;

			result = new Rectangle( newX, newY,	newWidth, newHeight );
		}

		return( result );
	}

	public Rectangle calculateNewBoundsOnScreen( Rectangle originalBounds, Insets insets, Point center, double zoomFactor )
	{
		Rectangle result = calculateNewBounds( originalBounds, insets, center, zoomFactor );

		if( result != null )
		{
			Point newPosition = new Point( result.x, result.y );
			Dimension newSize = new Dimension( result.width, result.height );

			newPosition = getValidPositionToPlaceComponent( newSize, newPosition );

			result = new Rectangle( newPosition.x,
									newPosition.y,
									newSize.width,
									newSize.height
								);
		}

		return( result );
	}

	/**
	 * 
	 * @param cont	is a JFrame or a JDialog
	 * @return 
	 */
	public static Insets getFrameBorders_static( Container cont )
	{
		Insets result = null;
		try
		{
			cont.setVisible( true );

			int topFrameBorder = 0;
			int bottomFrameBorder = 0;
			int leftFrameBorder = 0;
			int rightFrameBorder = 0;

			Point outerLocation = cont.getLocationOnScreen();
			Point innerLocation = null;
			Dimension outerDimension = cont.getSize();
			Dimension innerDimension = null;

			Component contentPane = ( (Container) ( (Container) cont.getComponent(0) ).getComponent(1) ).getComponent(0);
			innerLocation = contentPane.getLocationOnScreen();
			innerDimension = contentPane.getSize();

			topFrameBorder = (int) (innerLocation.getY() - outerLocation.getY());
			bottomFrameBorder = (int) (outerDimension.getHeight() - innerDimension.getHeight() - topFrameBorder );
			leftFrameBorder = (int) (innerLocation.getX() - outerLocation.getX());
			rightFrameBorder = (int) (outerDimension.getWidth() - innerDimension.getWidth() - leftFrameBorder );

			result = new Insets( topFrameBorder, leftFrameBorder, bottomFrameBorder, rightFrameBorder );

			cont.setVisible( false );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( result );
	}

	protected Insets getBorders( String typeOfContainer )
	{
		return( _mapOfBorders.get( typeOfContainer ) );
	}

	public boolean haveBeenBordersCalculated( Component comp )
	{
		boolean result = ( ( comp != null ) && ( _mapOfBorders.get( comp.getClass().getName() ) != null ) );

		return( result );
	}

	public Insets getBorders( Component comp )
	{
		Insets result = null;

		if( comp != null )
		{
			result = _mapOfBorders.get( comp.getClass().getName() );
		}

		if( result == null )
		{
			result = getBorders_new( comp );
			
			if( result != null )
				_mapOfBorders.put( comp.getClass().getName(), result );
		}

		return( result );
	}

	public Insets getBorders_new( Component comp )
	{
		Insets result = null;

//		if( comp instanceof JInternalFrame )
		{
//			result = ( (JInternalFrame) comp ).getInsets();
		}
//		else
		if( comp instanceof Container )
		{
			boolean isVisible = comp.isVisible();
			
			try
			{
				if( comp instanceof JInternalFrame )
					comp.setVisible( true );

				JMenuBar jMenuBar = null;
				Container contentPane = getContentPane( comp );
				if( comp instanceof JFrame )
				{
					JFrame jframe = (JFrame) comp;
					jMenuBar = jframe.getJMenuBar();
				}

				if( contentPane != null )
				{
					Insets insets = contentPane.getInsets();

					int top = 0;
					int left = 0;
					int bottom = 0;
					int right = 0;

					if( insets != null )
					{
						top = insets.top;
						left = insets.left;
						bottom = insets.bottom;
						right = insets.right;
					}

					Container parent = null;
					Container current = contentPane;
					while( current.getParent() != null )
					{
						parent = current.getParent();
						Rectangle boundsForCurrentPosition = current.getBounds();
						Rectangle boundsForCurrentSize = boundsForCurrentPosition;
						Rectangle parentBounds = parent.getBounds();

						if( ( jMenuBar != null ) && ( current == contentPane ) )	// to avoid the menu be taken as a border
							boundsForCurrentPosition = jMenuBar.getBounds();

						top = top + ( boundsForCurrentPosition.y );
						left = left + ( boundsForCurrentPosition.x );
						bottom = bottom + ( parentBounds.height - boundsForCurrentSize.y - boundsForCurrentSize.height );
						right = right + ( parentBounds.width - boundsForCurrentSize.x - boundsForCurrentSize.width );

						current = parent;

						if( ( current instanceof JFrame ) ||
							( current instanceof JDialog ) ||
							( current instanceof JInternalFrame ) )
							break;
					}

					result = new Insets( top, left, bottom, right );
	//				result = new Insets( top, left, left, left );

				}
			}
			finally
			{
				if( comp instanceof JInternalFrame )
					comp.setVisible( isVisible );
			}
		}

		return( result );
	}

	public Component getFirstChild( Container container )
	{
		Component result = null;
		if( container != null )
		{
			if( container.getComponentCount() > 0 )
				result = container.getComponent(0);
		}
		return( result );
	}

	public Insets getBorders_old( Component comp )
	{
		String typeOfContainer = null;
		if( comp instanceof JFrame )
		{
			JFrame jframe = (JFrame) comp;
			if( ! jframe.isUndecorated() )
				typeOfContainer = "JFrame";
		}
		else if( comp instanceof JDialog )
		{
			JDialog jdialog = (JDialog) comp;
			if( ! jdialog.isUndecorated() )
				typeOfContainer = "JDialog";
		}

		Insets result = null;
		if( typeOfContainer != null )
		{
			result = getBorders( typeOfContainer );
		}

		return( result );
	}

	public boolean rectanglesCloseEnough( Rectangle rect1, Rectangle rect2, int maxError )
	{
		boolean result = false;

		if( ( rect1 != null ) && ( rect2 != null ) )
		{
			result = ( IntegerFunctions.abs( rect1.x - rect2.x ) <= maxError ) &&
					( IntegerFunctions.abs( rect1.y - rect2.y ) <= maxError ) &&
					( IntegerFunctions.abs( rect1.width - rect2.width ) <= maxError ) &&
					( IntegerFunctions.abs( rect1.height - rect2.height ) <= maxError );
		}

		return( result );
	}

	public boolean sizesCloseEnough( Dimension size1, Dimension size2, int maxError )
	{
		boolean result = false;

		if( ( size1 != null ) && ( size2 != null ) )
		{
			result = ( IntegerFunctions.abs( size1.width - size2.width ) <= maxError ) &&
					( IntegerFunctions.abs( size1.height - size2.height ) <= maxError );
		}

		return( result );
	}

	public void setNameForComponent( Component comp, String name )
	{
		if( comp != null )
		{
			ComponentNameComponents cnc = new ComponentNameComponents( comp.getName() );
			cnc.setName( name );
			comp.setName( cnc.getCompoundNameForComponentName() );
		}
	}

	public String getComponentName( Component comp )
	{
		String result = null;

		if( comp != null )
		{
			ComponentNameComponents cnc = new ComponentNameComponents( comp.getName() );
			result = cnc.getName();
		}

		return( result );
	}

	public Container getContentPane( Component comp )
	{
		Container result = null;

		if( comp instanceof JFrame )
			result = ( (JFrame ) comp ).getContentPane();
		else if( comp instanceof JDialog )
			result = ( (JDialog ) comp ).getContentPane();
		else if( comp instanceof JInternalFrame )
			result = ( (JInternalFrame ) comp ).getContentPane();

		return( result );
	}

	public Rectangle getRectangle( Component comp )
	{
		Rectangle result = null;

		if( comp != null )
		{
			Point point = comp.getLocationOnScreen();
			result = new Rectangle( point.x, point.y, comp.getWidth(), comp.getHeight() );
		}

		return( result );
	}

	public boolean componentContainsScreenPoint( Component comp, Point point )
	{
		boolean result = false;
		if( ( comp != null ) && ( point != null ) )
		{
			Rectangle rect = getRectangle( comp );
			if( rect != null )
				result = rect.contains(point);
		}

		return( result );
	}

	public boolean componentOverlapsRectangle( Component comp, Rectangle rect )
	{
		boolean result = false;

		Rectangle compRect = getRectangle( comp );
		if( ( compRect != null ) && ( rect != null ) )
		{
			result = compRect.intersects(rect);

			if( result )
			{
				System.out.println( "Image: " + rect );
				System.out.println( "Component: " + compRect );
			}
		}

		return( result );
	}

	public Dimension atLeastAsBigAsParent( Dimension newSize, Component parent )
	{
		Dimension result = newSize;
		if( ( newSize != null ) && ( parent != null ) )
		{
			Dimension parentSize = parent.getSize();
			if( ( newSize.width < parentSize.width ) ||
				( newSize.height < parentSize.height ) )
			{
				result = new Dimension( IntegerFunctions.max( newSize.width, parentSize.width ),
										IntegerFunctions.max( newSize.height, parentSize.height ) );
			}
		}
		return( result );
	}

	public String getText( Component comp )
	{
		String result = null;
		if( comp instanceof JLabel )
			result = ( (JLabel ) comp ).getText();
		else if( comp instanceof JButton )
			result = ( (JButton) comp ).getText();
		else if( comp instanceof JTextComponent )
			result = ( (JTextComponent ) comp ).getText();

		return( result );
	}

	public Dimension getSizeOfComponentWithSizeBasedOnText( Component comp )
	{
		Font font = comp.getFont();
		String text = ViewFunctions.instance().getText( comp );
		FontRenderContext frc = ImageFunctions.instance().getFontRenderContext( comp );
		Rectangle2D textBounds = ImageFunctions.instance().getImageWrappedBoundsForString(font, text, frc );

		Dimension result = null;
		if( textBounds != null )
			result = new Dimension( (int) textBounds.getWidth(),
									(int) textBounds.getHeight() );

		return( result );
	}

	public Rectangle calculateNewBounds( Rectangle oldBounds, Dimension newSize,
										double zoomFactor )
	{
		Rectangle result = null;

		if( ( oldBounds != null ) && ( newSize != null ) )
		{
			result = new Rectangle( IntegerFunctions.zoomValueInt( oldBounds.x, zoomFactor),
									IntegerFunctions.zoomValueInt( oldBounds.y, zoomFactor),
									newSize.width,
									newSize.height );
		}

		return( result );
	}

	public Rectangle getBoundsInWindow( Component comp )
	{
		Rectangle result = null;
		if( comp != null )
		{
			Component window = ComponentFunctions.instance().getAncestor(comp);
			if( window != null )
			{
				result = comp.getBounds();
				Point cpoint = comp.getLocationOnScreen();
				Point wpoint = window.getLocationOnScreen();

				result.x = cpoint.x - wpoint.x;
				result.y = cpoint.y - wpoint.y;
			}
		}

		return( result );
	}

	public Component getComponent( ViewComponent vc )
	{
		Component result = null;
		if( vc instanceof DesktopViewComponent )
		{
			result = ( (DesktopViewComponent) vc ).getComponent();
		}

		return( result );
	}

	public Point getLocationOnScreen( Component comp, Point targetPoint )
	{
		boolean withoutTaskBar = true;
		Dimension screenSize = ScreenFunctions.getScreenSize( comp, withoutTaskBar );
		
		int minXx = 0;
		int maxXx = IntegerFunctions.max( minXx, IntegerFunctions.min( targetPoint.x, screenSize.width - comp.getWidth() ) );
		int xx = IntegerFunctions.limit( targetPoint.x, minXx, maxXx );
		
		int minYy = 0;
		int maxYy = IntegerFunctions.max( minYy, IntegerFunctions.min( targetPoint.y, screenSize.height - comp.getHeight() ) );
		int yy = IntegerFunctions.limit( targetPoint.y, minYy, maxYy );

		return( new Point( xx, yy ) );
	}

	public Rectangle addArea( Rectangle origRect, Rectangle areaToAdd )
	{
		Rectangle result = origRect;
		if( result == null )
			result = areaToAdd;
		else if( areaToAdd != null )
		{
			int maxX = IntegerFunctions.max( result.x + result.width,
											areaToAdd.x + areaToAdd.width );
			int maxY = IntegerFunctions.max( result.y + result.height,
											areaToAdd.y + areaToAdd.height );
			result.x = IntegerFunctions.min( result.x, areaToAdd.x );
			result.y = IntegerFunctions.min( result.y, areaToAdd.y );
			result.width = maxX - result.x;
			result.height = maxY - result.y;
		}

		return( result );
	}

	public Rectangle applyLimits( Rectangle rect, Dimension limits )
	{
		Rectangle result = rect;
		if( ( result != null ) && ( limits != null ) )
		{
			result.x = IntegerFunctions.max( 0, result.x );
			result.y = IntegerFunctions.max( 0, result.y );
			result.width = IntegerFunctions.max( 0, IntegerFunctions.min( result.width, limits.width - result.x ) );
			result.height = IntegerFunctions.max( 0, IntegerFunctions.min( result.height, limits.height - result.y ) );
		}

		return( result );
	}

	public Rectangle widenBounds( Rectangle origRect, int width, int height,
									Dimension limits )
	{
		Rectangle result = origRect;
		if( result != null )
		{
			result.x -= width;
			result.y -= height;
			result.width += width;
			result.height += height;
			
			if( limits != null )
			{
				result = applyLimits(result, limits);
			}
		}

		return( result );
	}

	public boolean isNearestLeft( Rectangle rect, Point point )
	{
		boolean result = false;
		
		if( ( rect != null ) && ( point != null ) )
			result = ( Math.abs( point.x - rect.x ) < Math.abs( point.x - rect.x - rect.width ) );

		return( result );
	}

	public Rectangle calculateIntersection( Rectangle rect1, Rectangle rect2 )
	{
		Rectangle result = null;
		
		int x1 = Math.max( rect1.x, rect2.x );
		int x2 = Math.min( rect1.x + rect1.width, rect2.x + rect2.width );
		int y1 = Math.max( rect1.y, rect2.y );
		int y2 = Math.min( rect1.y + rect1.height, rect2.y + rect2.height );

		if( ( x1 <= x2 ) && ( y1 <= y2 ) )
			result = new Rectangle( x1, y1, x2 - x1, y2 - y1 );

		return( result );
	}

	public Rectangle calculateUnion( Rectangle rect1, Rectangle rect2 )
	{
		Rectangle result = null;

		int x1 = Math.min( rect1.x, rect2.x );
		int x2 = Math.max( rect1.x + rect1.width, rect2.x + rect2.width );
		int y1 = Math.min( rect1.y, rect2.y );
		int y2 = Math.max( rect1.y + rect1.height, rect2.y + rect2.height );

		if( ( x1 <= x2 ) && ( y1 <= y2 ) )
			result = new Rectangle( x1, y1, x2 - x1, y2 - y1 );

		return( result );
	}

	protected interface CreateImageForButton
	{
		public BufferedImage createImage( AbstractButton button, BufferedImage originalImage,
										int xMargin, int yMargin );
	}
}

