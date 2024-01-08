/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.ui;

import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.structures.Pair;
import com.frojasg1.general.view.Blockable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import javax.swing.JComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BlockablePainter implements Blockable
{
	protected BiConsumer<Graphics, JComponent> _defaultPainter;

	protected Pair<BufferedImage, Graphics> _previousPaintedImage;

	protected boolean _isBlocked = false;

	public BlockablePainter( BiConsumer<Graphics, JComponent> defaultPainter )
	{
		_defaultPainter = defaultPainter;
	}

	public void paint( Graphics grp, JComponent jComponent )
	{
		Rectangle clip = grp.getClipBounds();
		if( isBlocked() && _previousPaintedImage != null )
		{
			grp.drawImage(_previousPaintedImage.getKey(), 0, 0, null);
		}
		else
		{
			Pair<BufferedImage, Graphics> pair = getCachedImage(grp, jComponent);
			BufferedImage img = pair.getKey();
			Graphics grp2 = pair.getValue();

			defaultPaint( grp2, jComponent );

			if( clip == null )
				clip = getClip( jComponent );
//				grp.drawImage(img, 0, 0, null);
//			else
				grp.drawImage(img,
							clip.x, clip.y, clip.x + clip.width, clip.y + clip.height,
							clip.x, clip.y, clip.x + clip.width, clip.y + clip.height,
							null);

//			ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(img, "png", new File( "J:\\img.png" ) ) );
//			grp2.dispose();

			_previousPaintedImage = pair;
		}
	}

	public void defaultPaint( Graphics grp, JComponent jComponent )
	{
		_defaultPainter.accept( grp, jComponent );
	}

	protected Rectangle getClip(JComponent jComponent)
	{
		return( ComponentFunctions.instance().getClip( jComponent ) );
	}

	@Override
	public void block()
	{
		setBlocked(true);
	}

	@Override
	public void unblock()
	{
		setBlocked(false);
	}

	@Override
	public void setBlocked(boolean value)
	{
		_isBlocked = value;
	}

	@Override
	public boolean isBlocked()
	{
		return( _isBlocked );
	}

	protected boolean isSuitable( Pair<BufferedImage, Graphics> pair, Dimension size )
	{
		BufferedImage image = (pair != null) ? pair.getKey() : null;
		boolean result = (image != null) &&
						(image.getWidth() == size.width ) &&
						(image.getHeight() == size.height );

		return( result );
	}

	protected Pair<BufferedImage, Graphics> getCachedImage(Graphics grp, JComponent jComponent)
	{
		Pair<BufferedImage, Graphics> result = _previousPaintedImage;
		if( !isSuitable( result, jComponent.getSize() ) )
			result = createEmptyImage( grp, jComponent );

		result.getValue().setClip( grp.getClip() );

		return( result );
	}

	protected Pair<BufferedImage, Graphics> createEmptyImage(Graphics grp, JComponent jComponent)
	{
		Dimension size = jComponent.getSize();
		BufferedImage img = ImageFunctions.instance().createImage(size.width, size.height);
		Graphics grp2 = img.createGraphics();
		grp2.setColor( getBackground(jComponent) );
		grp2.fillRect(0, 0, size.width, size.height);

		return( new Pair<>( img, grp2 ) );
	}

	protected Color getBackground( JComponent jComponent )
	{
		return( jComponent.getBackground() );
	}
}
