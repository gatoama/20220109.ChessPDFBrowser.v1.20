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
package com.frojasg1.applications.common.components.hints;

import com.frojasg1.applications.common.components.internationalization.window.ComponentWithOverlappedImage;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author Usuario
 */
public class HintForComponent implements FocusListener, MouseListener,
											MouseMotionListener
{
	protected Component _component = null;
	protected String _hintToShow = null;

	protected Timer _timerToShowHint = null;

	protected boolean _hasSetTimeToHideHint = false;
	protected Timer _timerToHideHint = null;

	protected boolean _showingHint = false;
	protected boolean _canShowHint = true;

	protected Point _currentMousePosition = null;

	protected BufferedImage _image = null;

	protected ComponentWithOverlappedImage _parentComponentWithOverlappedImage = null;

	protected HintConfiguration _configuration = null;

	public HintForComponent( Component comp, String hint, HintConfiguration config )
	{
		_component = comp;

		if( config != null )
			_configuration = config;
		else
			_configuration = HintConfiguration.getDefault();

		setHintConfiguration( _configuration );

		_timerToShowHint = new Timer( 1000, new TimerForShowingAction() );
		_timerToHideHint = new Timer( 5000, new TimerForHidingAction() );

		setHint( hint );

		_canShowHint = initializeCanShowHint();
		
		comp.addFocusListener(this);
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
	}

	public HintForComponent( Component comp, String hint )
	{
		this( comp, hint, null );
	}

	public HintConfiguration getHintConfiguration()
	{
		return( _configuration );
	}

	public synchronized void setHintConfiguration( HintConfiguration conf )
	{
		if( conf == null )
		{
			// unexpected.
		}
//		else if( ( conf != _configuration ) &&
//				!( ( conf == null ) && _configuration == HintConfiguration.getDefault() ) )
		else
		{
			if( _configuration != null )
				_configuration.remove( this );

			if( conf == null )
				conf = HintConfiguration.getDefault();
			
			_configuration = conf;
			_configuration.add( this );
			
			_image = null;
		}
	}

	protected boolean initializeCanShowHint()
	{
		return( ( _hintToShow != null ) && ( _hintToShow.length() > 0 ) &&
				(  _configuration.getShowHintIfcomponentDisabled() || _component.isEnabled() ) &&
				( !_configuration.getHintHidesWhenFocusGained() || !_component.hasFocus() ) &&
				( _configuration.getHintsActivated() ) );
	}

	public Component getComponent()
	{
		return( _component );
	}

	public String getHint()
	{
		return( _hintToShow );
	}

	public synchronized void setHint( String hint )
	{
		if( (hint != _hintToShow) &&
			( ( _hintToShow == null ) ||
				!( _hintToShow.equals( hint ) ) ) )
		{
			_hintToShow = hint;

			if( ! _hasSetTimeToHideHint )
			{
				int milliseconds = 400 * hint.length();
				milliseconds = IntegerFunctions.max( 5000, milliseconds );
				_timerToHideHint.setDelay( milliseconds );
			}

			_image = null;
		}
	}

	public int getTimerToShowHint()
	{
		return( _timerToShowHint.getDelay() );
	}

	public synchronized void updateTimerToShowHint( int milliseconds )
	{
		_timerToShowHint.setDelay(milliseconds);
	}

	public int getTimerToHideHint()
	{
		return( _timerToHideHint.getDelay() );
	}

	public void setTimerToHideHint( int milliseconds )
	{
		_timerToHideHint.setDelay(milliseconds);
		_hasSetTimeToHideHint = true;
	}

	protected synchronized BufferedImage getImage()
	{
		if( _image == null )
			_image = createImage();

		return( _image );
	}

	protected synchronized BufferedImage createImage()
	{
		String stringToShow = "  " + _hintToShow + "  ";

		Rectangle2D wrappedBounds = ImageFunctions.instance().getImageWrappedBoundsForString( _configuration.getFont(), stringToShow );

		int margin = (int) Math.max( 3, wrappedBounds.getHeight() * 0.1 );

		BufferedImage result = new BufferedImage( (int)wrappedBounds.getWidth(),
													(int) wrappedBounds.getHeight() + 2 * margin,
													BufferedImage.TYPE_INT_RGB );
		Graphics gc1 = result.getGraphics();

		gc1.setColor( HintConfiguration.DEFAULT_FOREGROUND_COLOR );
		gc1.drawRect( 0, 0, (int)wrappedBounds.getWidth(),
							(int) wrappedBounds.getHeight() + 2 * margin);

		gc1.setColor( _configuration.getBackgroundColor() );
		gc1.fillRect( 1, 1, (int)wrappedBounds.getWidth() - 2,
							(int) wrappedBounds.getHeight() + 2 * margin - 2);

		gc1.setColor( _configuration.getForegroundColor() );
		gc1.setFont( _configuration.getFont() );
		gc1.drawString( stringToShow, 0, (int) ( result.getHeight() - margin - 3 ) );
		
		return( result );
	}

	@Override
	public void focusGained(FocusEvent fe)
	{
		if( _showingHint && _configuration.getHintHidesWhenFocusGained() &&
			_configuration.getHintsActivated() )
			hideHint_withTimers();
	}

	@Override
	public void focusLost(FocusEvent fe)
	{
	}

	@Override
	public void mouseClicked(MouseEvent me)
	{
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
	}

	@Override
	public void mouseReleased(MouseEvent me)
	{
	}
	
	@Override
	public void mouseEntered(MouseEvent me)
	{
		_canShowHint = initializeCanShowHint();
		_currentMousePosition = me.getLocationOnScreen();
	}

	@Override
	public void mouseExited(MouseEvent me)
	{
		if( ( _configuration == null ) ||
			( _configuration.getHintsActivated() ) )
		{
			if( _timerToShowHint.isRunning() )
				_timerToShowHint.stop();

			if( _showingHint && _configuration.getHintHidesWhenMouseExit() &&
				_configuration.getHintsActivated() )
				hideHint_withTimers();
		}
	}

	@Override
	public void mouseDragged(MouseEvent me)
	{
		if( _timerToShowHint.isRunning() )
			_timerToShowHint.stop();

		if( _showingHint )
		{
			if( _configuration.getHintHidesWhenMouseMoved() &&
				_configuration.getHintsActivated() )
				hideHint_withTimers();

			_canShowHint = false;
		}
		else if( _canShowHint )
		{
			_currentMousePosition = me.getLocationOnScreen();
			_timerToShowHint.restart();
		}
	}

	@Override
	public void mouseMoved(MouseEvent me)
	{
		if( _timerToShowHint.isRunning() )
			_timerToShowHint.stop();

		if( _showingHint )
		{
			if( _configuration.getHintHidesWhenMouseMoved() &&
				_configuration.getHintsActivated() )
				hideHint_withTimers();

			_canShowHint = false;
		}
		else if( _canShowHint )
		{
			_currentMousePosition = me.getLocationOnScreen();
			_timerToShowHint.restart();
		}
	}

	public synchronized void hideHint()
	{
		if( _showingHint )
		{
			ComponentWithOverlappedImage cwoi = getParentComponentWithOverlappedImage();

			if( cwoi != null )
			{
				cwoi.setOverlappedImage( null, null );
			}
			_showingHint = false;
		}
	}

	public synchronized void hideHint_withTimers()
	{
		if( _showingHint )
		{
			hideHint();

			if( _timerToHideHint.isRunning() )
				_timerToHideHint.stop();
			if( _timerToShowHint.isRunning() )
				_timerToShowHint.stop();

			_canShowHint = false;
		}
	}

	public synchronized void showHint()
	{
		if( !_showingHint )
		{
			ComponentWithOverlappedImage cwoi = getParentComponentWithOverlappedImage();

			Point point = getOptimalPositionForHint( cwoi, getImage(), _currentMousePosition );

			if( cwoi != null )
			{
				cwoi.setOverlappedImage( getImage(), point );
			}

			_showingHint = true;
		}
	}

	public synchronized void showHint_withTimers()
	{
		if( !_showingHint )
		{
			showHint();

			if( _timerToHideHint.isRunning() )
				_timerToHideHint.stop();
			if( _timerToShowHint.isRunning() )
				_timerToShowHint.stop();

			_timerToHideHint.restart();
		}
	}

	public ComponentWithOverlappedImage calculateParentComponentWithOverlappedImage()
	{
		ComponentWithOverlappedImage result = null;
		
		Component comp = _component.getParent();
		while( ( result == null ) && ( comp != null ) )
		{
			if( comp instanceof ComponentWithOverlappedImage )
				result = ( ComponentWithOverlappedImage ) comp;
			
			comp = comp.getParent();
		}

		return( result );
	}

	public ComponentWithOverlappedImage getParentComponentWithOverlappedImage()
	{
		if( _parentComponentWithOverlappedImage == null )
			_parentComponentWithOverlappedImage = calculateParentComponentWithOverlappedImage();
		
		return( _parentComponentWithOverlappedImage );
	}

	public void setParentComponentWithOverlappedImage( ComponentWithOverlappedImage parentComponentWithOverlappedImage )
	{
		_parentComponentWithOverlappedImage = parentComponentWithOverlappedImage;
	}

	protected Point getOptimalPositionForHint( ComponentWithOverlappedImage cwoi,
												BufferedImage image,
												Point mouseLocation )
	{
		int yyToSubstract = (int) ( image.getHeight() + 5 );
		int xxToAdd = 5;

		Point locationOfParent = cwoi.getLocationOnScreen_forOverlappingImage();
		
		int xx = (int) ( mouseLocation.getX() - locationOfParent.getX() + xxToAdd );
		int yy = (int) ( mouseLocation.getY() - locationOfParent.getY() - yyToSubstract );

		int minimumYY = 0;
		if( cwoi instanceof JFrame )
			minimumYY = 33;

		Insets insets = null;
		if( cwoi instanceof Component )
		{
			insets = ViewFunctions.instance().getBorders( (Component) cwoi );

			minimumYY = insets.top;
		}

		xx = (int) Math.max( 0, Math.min( xx, cwoi.getWidth_forOverlappingImage() - image.getWidth() - 1 ) );
		yy = (int) Math.max( minimumYY, Math.min( yy, cwoi.getHeight_forOverlappingImage() - image.getHeight() - 1 ) );

		Point result = new Point( xx,yy );

		return( result );
	}

	protected synchronized void refreshConfiguration()
	{
		_image = null;
	}

	protected class TimerForShowingAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent ae)
		{
			showHint_withTimers();
		}
	}

	protected class TimerForHidingAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent ae)
		{
			hideHint_withTimers();
		}
	}
}
