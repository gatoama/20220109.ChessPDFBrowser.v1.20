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
package com.frojasg1.applications.common.components.resizecomp;

import com.frojasg1.applications.common.components.internationalization.ExtendedZoomSemaphore;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.zoom.ZoomInterface;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.zoom.ZoomParam;
import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.name.ComponentNameComponents;
import com.frojasg1.general.CallStackFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.edt.EventDispatchThreadInvokeLaterPurge;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.mouse.CursorFunctions;
import com.frojasg1.general.desktop.mouse.MouseFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.IconFunctions;
import com.frojasg1.general.desktop.view.buttons.ResizableImageJButton;
import com.frojasg1.general.desktop.view.buttons.ResizableImageJToggleButton;
import com.frojasg1.general.desktop.view.document.formatter.SizeChangedListener;
import com.frojasg1.general.desktop.view.document.formatter.SizeChangedObserved;
import com.frojasg1.general.desktop.view.zoom.ResizeSizeComponent;
import com.frojasg1.general.desktop.view.zoom.ZoomComponentInterface;
import com.frojasg1.general.desktop.view.zoom.ZoomIcon;
import com.frojasg1.general.desktop.view.zoom.components.ComponentWithIconForZoomInterface;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomMetalComboBoxIcon;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import com.frojasg1.general.executor.ParamExecutorInterface;
import com.frojasg1.general.listeners.GenericObserved;
import com.frojasg1.general.listeners.ListOfListenersImp;
import com.frojasg1.general.threads.ThreadFunctions;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.metal.MetalComboBoxButton;
import javax.swing.plaf.metal.MetalComboBoxIcon;
import javax.swing.text.JTextComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Usuario
 */
public class ResizeRelocateItem implements ParamExecutorInterface< ZoomParam >,
											SizeChangedListener
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ResizeRelocateItem.class);

	protected static final Insets ZERO_INSETS = new Insets(0,0,0,0);

	public static final long MS_TO_CONSIDER_USER_RESIZURE = 200;

	// Flags:
	public static final int RESIZE_TO_RIGHT = 1;
	public static final int MOVE_TO_RIGHT = 2;
	public static final int RESIZE_TO_BOTTOM = 4;
	public static final int MOVE_TO_BOTTOM = 8;
	public static final int MOVE_LEFT_SIDE_PROPORTIONAL = 16;
	public static final int MOVE_RIGHT_SIDE_PROPORTIONAL = 32;
	public static final int MOVE_RIGHT_SIDE_TO_RIGHT = 64;
	public static final int LEAVE_WIDTH_UNALTERED = 128;
	public static final int MOVE_TOP_SIDE_PROPORTIONAL = 256;
	public static final int MOVE_BOTTOM_SIDE_PROPORTIONAL = 512;
	public static final int MOVE_BOTTOM_SIDE_TO_BOTTOM = 1024;
	public static final int RESIZE_SCROLLABLE_HORIZONTAL_FREE = 2048;
	public static final int RESIZE_SCROLLABLE_VERTICAL_FREE = 4096;
	public static final int NOT_RELOCATE_HORIZONTAL_BAR_SCROLL_IN_ZOOM = 8192;
	public static final int NOT_RELOCATE_VERTICAL_BAR_SCROLL_IN_ZOOM = 16384;
	public static final int LEAVE_HEIGHT_UNALTERED = 32768;
	public static final int FILL_WHOLE_PARENT = 65536;
	public static final int FILL_WHOLE_WIDTH = 131072;
	public static final int FILL_WHOLE_HEIGHT = 262144;
	public static final int SKIP_CHANGE_BOUNDS_AND_REPOSITION = 524288;
	public static final int MOVE_MID_HORIZONTAL_SIDE_PROPORTIONAL = 1048576;
	public static final int MOVE_MID_VERTICAL_SIDE_PROPORTIONAL = 2097152;
	public static final int DO_NOT_ZOOM_FONT = 4194304;
	public static final int RESIZE_PROPORTIONAL = 8388608;
	public static final int ONLY_ZOOM_FONT = 16777216;

	public static final int MOVE_HORIZONTAL_SIDES_PROPORTIONAL = MOVE_LEFT_SIDE_PROPORTIONAL +
																MOVE_RIGHT_SIDE_PROPORTIONAL;
	public static final int MOVE_VERTICAL_SIDES_PROPORTIONAL = MOVE_TOP_SIDE_PROPORTIONAL +
																MOVE_BOTTOM_SIDE_PROPORTIONAL;

	public static final int MOVE_ALL_SIDES_PROPORTIONAL = MOVE_HORIZONTAL_SIDES_PROPORTIONAL +
															MOVE_VERTICAL_SIDES_PROPORTIONAL;

	public static final int MOVE_MID_PROPORTIONAL = MOVE_MID_HORIZONTAL_SIDE_PROPORTIONAL +
													MOVE_MID_VERTICAL_SIDE_PROPORTIONAL;

	public static final int UP_TO_RIGHT = 33554432;
	public static final int UP_TO_BOTTOM = 67108864;

	public static final int MASK = 2 * UP_TO_BOTTOM - 1;

	protected static final int HORIZONTAL_MODIFIER_FLAGS = RESIZE_TO_RIGHT | MOVE_TO_RIGHT |
		MOVE_LEFT_SIDE_PROPORTIONAL | MOVE_RIGHT_SIDE_PROPORTIONAL | MOVE_RIGHT_SIDE_TO_RIGHT |
		LEAVE_WIDTH_UNALTERED | FILL_WHOLE_WIDTH | MOVE_MID_HORIZONTAL_SIDE_PROPORTIONAL |
		UP_TO_RIGHT;

	protected static final int VERTICAL_MODIFIER_FLAGS = RESIZE_TO_BOTTOM | MOVE_TO_BOTTOM |
		MOVE_TOP_SIDE_PROPORTIONAL | MOVE_BOTTOM_SIDE_PROPORTIONAL | MOVE_BOTTOM_SIDE_TO_BOTTOM |
		LEAVE_HEIGHT_UNALTERED | FILL_WHOLE_HEIGHT | MOVE_MID_VERTICAL_SIDE_PROPORTIONAL |
		UP_TO_BOTTOM;

	// For the calculations of the bounds of JSplitPane in calculateBounds_JSplitPane. options for parameter: int position
	public static final int RIGHT = 1;
	public static final int LEFT = 2;
	public static final int TOP = 3;
	public static final int BOTTOM = 4;

	protected ResizeRelocateItem_parent _parent = null;
	protected Component _component;
	protected int		_flags;

	protected BasicSplitPaneUI uiman;

	protected int _originalWidthOfParent = -1;
	protected int _originalHeightOfParent = -1;

	protected int		_pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent = -1;		// it is needed if RESIZE_TO_RIGHT flag is active
	protected int		_pixelsFromTheComponentLeftBoundaryToTheRightOfTheParent = -1;			// it is needed if MOVE_TO_RIGHT flag is active
	protected int		_pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent = -1;	// it is needed if RESIZE_TO_BOTTOM flag is active
	protected int		_pixelsFromTheComponentTopBoundaryToTheBottomOfTheParent = -1;			// it is needed if MOVE_TO_BOTTOM flag is active

	protected int _originalWidth = -1;
	protected int _originalHeight = -1;

	protected boolean _initialized = false;

	protected ComponentOriginalDimensions _compOrigDimen = null;

	protected double _previousZoomFactor = 1.0D;

	protected int _originalPreferredSizeComponentForScrollBar = -1;

	protected boolean _isAlreadyZoomed = false;
	protected Boolean _initialIsAlreadyZoomed = null;

	protected Integer _previousFontSize = -1;
//	protected Integer _previousHorizontalBarPosition = -1;
//	protected Integer _previousVerticalBarPosition = -1;
	protected Double _previousHorizontalBarPositionPercent = 0.0D;
	protected Double _previousVerticalBarPositionPercent = 0.0D;

	protected Dimension _minimumPreferredSize = new Dimension( 0, 0 );

	protected double _previousZoomFactorWhenPickingData = 1.0D;
	protected boolean _pickedWaitingForUpdate = false;

	protected SizeChangedObserved _sizeChangedObserved = null;

//	protected ZoomParam _lastZoomParam = new ZoomParam( 1.0D );

	protected Rectangle _originalParentBounds;
	protected Rectangle _previousParentBounds = null;

	protected ChangeListener _parentChangeListener = null;
	protected ComponentListener _parentComponentListener = null;
	protected ComponentListener _compComponentListener = null;
	protected MouseAdapter _mouseAdapter = null;

	protected ComponentResizedListenerList _listeners = new ComponentResizedListenerList();

	protected boolean _servingResizureFromListener = false;

	protected ZoomParam _newExpectedZoomParam = new ZoomParam( 1.0D );

	protected long _previousResizureServedTimestamp = 0;

	protected Dimension _scrollableFreeComponentSize = null;

	protected boolean _forceExecution = false;

	protected boolean _triedToRegisterListeners = false;

	protected ExtendedZoomSemaphore _extendedZoomSemaphore = null;

	protected boolean _boundsWereChanged = false;

	protected Dimension _previousViewportSize = null;

	protected double _horizontalMoveFactor = 1d;
	protected double _horizontalResizeFactor = 1d;
	protected double _verticalMoveFactor = 1d;
	protected double _verticalResizeFactor = 1d;

	protected EventDispatchThreadInvokeLaterPurge _invokeLaterPurge = new EventDispatchThreadInvokeLaterPurge( 10 );

//	protected long _calculateOnlyNewBounds_parentCount;

	public ResizeRelocateItem( Component comp, int flags, ResizeRelocateItem_parent parent,
								boolean postpone_initialization ) throws InternException
	{
		this( comp, flags, parent, postpone_initialization, null );
	}

	public ResizeRelocateItem( Component comp, int flags, ResizeRelocateItem_parent parent,
								boolean postpone_initialization, Boolean isAlreadyZoomed ) throws InternException
	{
		_initialIsAlreadyZoomed = isAlreadyZoomed;

		_parent = parent;
		setFlags( flags );

		_component = comp;
		if( _component.getClass().getName().equals( "com.frojasg1.chesspdfbrowser.view.chess.ChessTreeGameTextPane" ) )
		{
			int kk=1;
		}
		else if( _component.getClass().getName().equals( "com.frojasg1.general.desktop.view.panels.NavigatorJPanel" ) )
		{
			int aa = 1;
		}
		else if( _component instanceof JTextPane )
		{
			int kk=1;
		}

		_compOrigDimen = new ComponentOriginalDimensions( _component );
		if( ! postpone_initialization )
			initializeWithComponent( _component );

		_initialized = !postpone_initialization;
	}
/*
	protected boolean isServingResizureFromListener()
	{
		return( _servingResizureFromListener );
	}

	protected boolean isServingResizureFromListenerOfUser()
	{
		return( isServingResizureFromListener() &&
				( _previousResizureServedTimestamp < MS_TO_CONSIDER_USER_RESIZURE ) );
	}
*/

	public void newExpectedZoomParam( ZoomParam newExpectedZoomParam )
	{
		if( matches() )
		{
			int aa = 1;
		}

		_newExpectedZoomParam = newExpectedZoomParam;
	}

	public ResizeRelocateItem setHorizontalMoveFactor( double value )
	{
		_horizontalMoveFactor = value;
		return( this );
	}

	public ResizeRelocateItem setHorizontalResizeFactor( double value )
	{
		_horizontalResizeFactor = value;
		return( this );
	}

	public ResizeRelocateItem setVerticalMoveFactor( double value )
	{
		_verticalMoveFactor = value;
		return( this );
	}

	public ResizeRelocateItem setVerticalResizeFactor( double value )
	{
		_verticalResizeFactor = value;
		return( this );
	}

	protected boolean isServingResizureFromListenerOfUser()
	{
//		return( !_parent.isResizeRelocateItemsResizeListenersBlocked() );
		Boolean result = getIfNotNull( _parent, ResizeRelocateItem_parent::isResizeDragging );
		if( result == null )
			result = false;

		return( result );
	}

	public SizeChangedObserved getSizeChangedObserved()
	{
		return( _sizeChangedObserved );
	}

	public void setExtendedZoomSemaphore( ExtendedZoomSemaphore semaphore )
	{
		_extendedZoomSemaphore = semaphore;
	}

	protected ExtendedZoomSemaphore getExtendedZoomSemaphore()
	{
		return( _extendedZoomSemaphore );
	}

	public void addResizeRelocateItemComponentResizedListener( ResizeRelocateItemComponentResizedListener listener )
	{
		_listeners.add(listener);
	}

	public void removeComponentResizedListener( ResizeRelocateItemComponentResizedListener listener )
	{
		_listeners.remove(listener);
	}

	public void setSizeChangedObserved( SizeChangedObserved sizeChangedObserved )
	{
		if( _sizeChangedObserved != null )
			_sizeChangedObserved.removeListener(this);

		_sizeChangedObserved = sizeChangedObserved;
		_sizeChangedObserved.addListener(this);
	}

	public boolean getForceExecution()
	{
		return( _forceExecution );
	}

	public void setForceExecution( boolean value )
	{
		_forceExecution = value;
	}

	public Component getComponent()
	{
		return( _component );
	}

	public boolean isAlreadyZoomed()
	{
		return( _isAlreadyZoomed );
	}

	protected int getFlags()
	{
		return( _flags );
	}

	protected void setIsAlreadyZoomed( boolean value )
	{
		_isAlreadyZoomed = value;
	}

	public ComponentOriginalDimensions getComponentOriginalDimensions()
	{
		return( _compOrigDimen );
	}
	
	public boolean isInitialized()
	{
		return( _initialized );
	}

	public void resetValues()
	{
		_initialized = false;

		_originalWidthOfParent = -1;
		_originalHeightOfParent = -1;

		_pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent = -1;		// it is needed if RESIZE_TO_RIGHT flag is active
		_pixelsFromTheComponentLeftBoundaryToTheRightOfTheParent = -1;			// it is needed if MOVE_TO_RIGHT flag is active
		_pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent = -1;	// it is needed if RESIZE_TO_BOTTOM flag is active
		_pixelsFromTheComponentTopBoundaryToTheBottomOfTheParent = -1;			// it is needed if MOVE_TO_BOTTOM flag is active

		_originalWidth = -1;
		_originalHeight = -1;

		_initialized = false;

//		_compOrigDimen = null;

		_previousZoomFactor = 1.0D;

		_originalPreferredSizeComponentForScrollBar = -1;

		if( _initialIsAlreadyZoomed != null )
			_isAlreadyZoomed = _initialIsAlreadyZoomed;
		else
			_isAlreadyZoomed = false;

		_previousFontSize = -1;

		_previousHorizontalBarPositionPercent = 0.0D;
		_previousVerticalBarPositionPercent = 0.0D;

		_minimumPreferredSize = new Dimension( 0, 0 );

		_previousZoomFactorWhenPickingData = 1.0D;
		_pickedWaitingForUpdate = false;

		_sizeChangedObserved = null;

		 _previousParentBounds = null;

//		_parentComponentListener = null;
//		_mouseAdapter = null;

//		_listeners = new ComponentResizedListenerList();

//		_servingResizureFromListener = false;

//		_newExpectedZoomParam = new ZoomParam( 1.0D );

//		_previousResizureServedTimestamp = 0;
	}


	public void initialize() throws InternException
	{
		initialize( 1.0D );
	}

	public void initialize( double currentZoomFactor ) throws InternException
	{
		resetValues();
		initializeWithComponent( _component, currentZoomFactor );
		_initialized = true;
	}

	protected int count( Boolean ... bools )
	{
		int result = 0;
		for( int ii=0; ii<bools.length; ii++ )
			if( bools[ii] )
				result++;

		return( result );
	}

	public void setFlags( int flags ) throws InternException
	{
		if( ( flags - ( flags & MASK ) ) != 0 )
			throw( new InternException( "Flags out of bounds" ) );

//		if( isFlagActive( RESIZE_TO_RIGHT ) && isFlagActive( MOVE_TO_RIGHT ) )
//			throw( new InternException( "Invalid flags: RESIZE_TO_RIGHT and MOVE_TO_RIGHT flags are both active" ) );

//		if( isFlagActive( RESIZE_TO_BOTTOM ) && isFlagActive( MOVE_TO_BOTTOM ) )
//			throw( new InternException( "Invalid flags: RESIZE_TO_BOTTOM and MOVE_TO_BOTTOM flags are both active" ) );

		if( ( isFlagActive( RESIZE_TO_RIGHT + MOVE_TO_RIGHT + UP_TO_RIGHT ) ) &&
			isFlagActive( MOVE_LEFT_SIDE_PROPORTIONAL ) )
			throw( new InternException( "Invalid flags: MOVE_LEFT_SIDE_PROPORTIONAL is incompatible with " +
										"RESIZE_TO_RIGHT and MOVE_TO_RIGHT flags" ) );

		if( ( isFlagActive( RESIZE_TO_BOTTOM + MOVE_TO_BOTTOM + UP_TO_BOTTOM ) ) &&
			isFlagActive( MOVE_TOP_SIDE_PROPORTIONAL ) )
			throw( new InternException( "Invalid flags: MOVE_TOP_SIDE_PROPORTIONAL is incompatible with " +
										"RESIZE_TO_BOTTOM and MOVE_TO_BOTTOM flags" ) );

		if( isFlagActive( MOVE_LEFT_SIDE_PROPORTIONAL ) &&
			!( isFlagActive( MOVE_RIGHT_SIDE_PROPORTIONAL ) ||
				isFlagActive( MOVE_RIGHT_SIDE_TO_RIGHT ) ||
				isFlagActive( LEAVE_WIDTH_UNALTERED ) ) )
			throw( new InternException( "Invalid flags: MOVE_LEFT_SIDE_PROPORTIONAL must be combined with " +
										"MOVE_RIGHT_SIDE_PROPORTIONAL or MOVE_RIGHT_SIDE_TO_RIGHT or LEAVE_WIDTH_UNALTERED flags" ) );

		if( isFlagActive( MOVE_TOP_SIDE_PROPORTIONAL ) &&
			!( isFlagActive( MOVE_BOTTOM_SIDE_PROPORTIONAL ) ||
				isFlagActive( MOVE_BOTTOM_SIDE_TO_BOTTOM ) ||
				isFlagActive( LEAVE_HEIGHT_UNALTERED ) ) )
			throw( new InternException( "Invalid flags: MOVE_TOP_SIDE_PROPORTIONAL must be combined with " +
										"MOVE_BOTTOM_SIDE_PROPORTIONAL or MOVE_BOTTOM_SIDE_TO_BOTTOM or LEAVE_HEIGHT_UNALTERED flags" ) );

/*	This two posibilities are now possible.
		if( !isFlagActive( MOVE_LEFT_SIDE_PROPORTIONAL ) &&
			( isFlagActive( MOVE_RIGHT_SIDE_PROPORTIONAL ) ||
				isFlagActive( MOVE_RIGHT_SIDE_TO_RIGHT ) ||
				isFlagActive( LEAVE_WIDTH_UNALTERED ) ) )
			throw( new InternException( "Invalid flags: MOVE_RIGHT_SIDE_PROPORTIONAL, MOVE_RIGHT_SIDE_TO_RIGHT and " +
										"LEAVE_WIDTH_UNALTERED flags can only be active in combination with" +
										"MOVE_LEFT_SIDE_PROPORTIONAL flag." ) );

		if( !isFlagActive( MOVE_TOP_SIDE_PROPORTIONAL ) &&
			( isFlagActive( MOVE_BOTTOM_SIDE_PROPORTIONAL ) ||
				isFlagActive( MOVE_BOTTOM_SIDE_TO_BOTTOM ) ||
				isFlagActive( LEAVE_HEIGHT_UNALTERED ) ) )
			throw( new InternException( "Invalid flags: MOVE_BOTTOM_SIDE_PROPORTIONAL, MOVE_BOTTOM_SIDE_TO_BOTTOM and " +
										"LEAVE_HEIGHT_UNALTERED flags can only be active in combination with" +
										"MOVE_TOP_SIDE_PROPORTIONAL flag." ) );
*/
		if( count( isFlagActive( MOVE_RIGHT_SIDE_PROPORTIONAL ),
					isFlagActive( MOVE_RIGHT_SIDE_TO_RIGHT ),
					isFlagActive( LEAVE_WIDTH_UNALTERED ) ) > 1 )
			throw( new InternException( "Invalid flags: only one of these flags can be active at the same time: " +
										"MOVE_RIGHT_SIDE_PROPORTIONAL, MOVE_RIGHT_SIDE_TO_RIGHT, LEAVE_WIDTH_UNALTERED" ) );

		if( count( isFlagActive( MOVE_BOTTOM_SIDE_PROPORTIONAL ),
					isFlagActive( MOVE_BOTTOM_SIDE_TO_BOTTOM ),
					isFlagActive( LEAVE_HEIGHT_UNALTERED ) ) > 1 )
			throw( new InternException( "Invalid flags: only one of these flags can be active at the same time: " +
										"MOVE_BOTTOM_SIDE_PROPORTIONAL, MOVE_BOTTOM_SIDE_TO_BOTTOM, LEAVE_HEIGHT_UNALTERED" ) );

		if( count( isFlagActive(RESIZE_TO_RIGHT + MOVE_TO_RIGHT),
					isFlagActive(UP_TO_RIGHT),
					isFlagActive(MOVE_LEFT_SIDE_PROPORTIONAL +
									MOVE_RIGHT_SIDE_PROPORTIONAL +
									MOVE_RIGHT_SIDE_TO_RIGHT +
									LEAVE_WIDTH_UNALTERED ),
					isFlagActive(RESIZE_SCROLLABLE_HORIZONTAL_FREE +
                                                    RESIZE_SCROLLABLE_VERTICAL_FREE )
					) > 1 )
		{
			throw( new InternException( "Invalid flags with horizontal resizing of moving" ) );
		}

		if( count( isFlagActive(RESIZE_TO_BOTTOM + MOVE_TO_BOTTOM),
					isFlagActive(UP_TO_BOTTOM),
					isFlagActive(MOVE_TOP_SIDE_PROPORTIONAL +
									MOVE_BOTTOM_SIDE_PROPORTIONAL +
									MOVE_BOTTOM_SIDE_TO_BOTTOM +
									LEAVE_HEIGHT_UNALTERED ),
					isFlagActive(RESIZE_SCROLLABLE_HORIZONTAL_FREE +
                                                    RESIZE_SCROLLABLE_VERTICAL_FREE )
					) > 1 )
		{
			throw( new InternException( "Invalid flags with vertical resizing of moving" ) );
		}
		_flags = flags;
	}

	public void setComponent( Component component )
	{
		if( _component != component )
		{
			unregisterListeners(_component);

			_component = component;

			registerListeners();
		}
	}

	public void setParent( ResizeRelocateItem_parent parent )
	{
		_parent = parent;
	}

	public void updateJScrollBar( double zoomFactor )
	{
		if( _component instanceof JScrollBar )
		{
			JScrollBar sb = (JScrollBar) _component;
			zoomScrollBar( sb, zoomFactor );
		}
	}

	public final void catchPreferredSizeOfJScrollBar( Component comp )
	{
		if( comp instanceof JScrollBar )
		{
			JScrollBar sb = (JScrollBar) comp;
			zoomScrollBar( sb, 1.0D );
		}
	}

	protected void zoomScrollBar( JScrollBar sb, double zoomFactor )
	{
		if( sb != null )
		{
			if( sb.getOrientation() == JScrollBar.HORIZONTAL )
				zoomHorizontalScrollBar( sb, zoomFactor );
			else if( sb.getOrientation() == JScrollBar.VERTICAL )
				zoomVerticalScrollBar( sb, zoomFactor );
		}
	}

	protected void zoomHorizontalScrollBar( JScrollBar hsb, double zoomFactor )
	{
		if( hsb != null )
		{
			if( _originalPreferredSizeComponentForScrollBar < 0 )
			{
				_originalPreferredSizeComponentForScrollBar = hsb.getPreferredSize().height;
			}

//			if( hsb.isPreferredSizeSet() )
			{
				Dimension size = hsb.getPreferredSize();
				hsb.setPreferredSize( new Dimension( size.width,
										ceil(_originalPreferredSizeComponentForScrollBar, zoomFactor)
														)
										);
			}
		}
	}
	
	protected void zoomVerticalScrollBar( JScrollBar vsb, double zoomFactor )
	{
		if( vsb != null )
		{
			if( _originalPreferredSizeComponentForScrollBar < 0 )
			{
				_originalPreferredSizeComponentForScrollBar = vsb.getPreferredSize().width;
			}

//			if( vsb.isPreferredSizeSet() )
			{
				Dimension size = vsb.getPreferredSize();
				vsb.setPreferredSize( new Dimension(
											ceil(_originalPreferredSizeComponentForScrollBar, zoomFactor),
											size.width )
										);
			}
		}
	}

	protected Icon changeToZoomIcon( Icon icon )
	{
		Icon result = null;

		if( icon instanceof ZoomIcon )
		{
			result = icon;
		}
		else if( ( icon instanceof MetalComboBoxIcon ) && ( _component instanceof MetalComboBoxButton ) )
		{
			MetalComboBoxButton btn = (MetalComboBoxButton) _component;
			result = new ZoomMetalComboBoxIcon();
			( (ZoomMetalComboBoxIcon) result).setParentButton(btn);
		}
		else
		{
			result = IconFunctions.instance().createZoomIcon(icon);
		}

		return( result );
	}
/*
	protected void changeToZoomInsets( JComponent jcomp )
	{
		if( ( _compOrigDimen.getOriginalInsets() != null ) &&
			( jcomp.getBorder() instanceof CompoundBorder ) )
		{
			jcomp.setBorder( BorderFunctions.instance().createZoomCompoundBorder( _compOrigDimen.getOriginalInsets(),
																					(CompoundBorder) jcomp.getBorder() ) );
		}
	}
*/
	public final void initializeWithComponent( Component comp ) throws InternException
	{
		initializeWithComponent( comp, 1.0D );
	}

	protected Rectangle getRealParentBounds()
	{
		return( getIfNotNull( getParent(), Component::getBounds ) );
	}

	public final void initializeWithComponent( Component comp,
									double currentZoomFactor ) throws InternException
	{
		_component = comp;
		if( matches() )
		{
			int kk=1;
		}

//		if( _component instanceof JViewport )
//			_previousViewportSize = getViewportSize();

		_compOrigDimen = new ComponentOriginalDimensions( _component, currentZoomFactor );
		_previousParentBounds = null;

		if( ! _isAlreadyZoomed )
			currentZoomFactor = 1.0D;

		if( _component instanceof JTextPane )
		{
			int kk=1;
		}

		if( _component.getClass().getName().equals( "javax.swing.plaf.metal.MetalComboBoxButton" ) )
		{
			MetalComboBoxButton mcbb = (MetalComboBoxButton) _component;

//			changeToZoomInsets( mcbb );
			mcbb.setComboIcon( changeToZoomIcon( mcbb.getComboIcon() ) );
		}

		if( _component == null )
			throw( new InternException( "Component null" ) );

		catchPreferredSizeOfJScrollBar( comp );

		Component parent = _component.getParent();
		Rectangle componentBounds = getComponentBounds( comp );
		Rectangle parentBounds = getParentBounds( comp, 1.0D );

		_originalWidth = unzoom( _component.getWidth(), currentZoomFactor );
		_originalHeight = unzoom( _component.getHeight(), currentZoomFactor );

		if( parent == null )
		{
			if( _flags != 0 )
				throw( new InternException( "Parent component null" ) );

			return;
		}

		_originalWidthOfParent = unzoom( (int) parentBounds.getWidth(), currentZoomFactor );
		_originalHeightOfParent = unzoom( (int) parentBounds.getHeight(), currentZoomFactor );

		if( ! ( getComponent() instanceof JFrame ) && ! ( getComponent() instanceof JDialog ) )
			_originalParentBounds = getRealParentBounds();

//		if( isFlagActive( RESIZE_TO_RIGHT ) )
		{
/*			_pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent = (int) ( parentBounds.getWidth() -
																					( componentBounds.getX() - parentBounds.getX() ) -
																					componentBounds.getWidth()
																					);
*/
			_pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent = unzoom( (int) ( parentBounds.getWidth() -
																					componentBounds.getX() -
																					componentBounds.getWidth()
																					), 
																					currentZoomFactor );
		}
//		else if( isFlagActive( MOVE_TO_RIGHT ) )
		{
			_pixelsFromTheComponentLeftBoundaryToTheRightOfTheParent =	unzoom( (int) ( parentBounds.getWidth() - parentBounds.getX() -
																				componentBounds.getX() ), 
																					currentZoomFactor );

		}

//		if( isFlagActive( RESIZE_TO_BOTTOM ) )
		{
/*			_pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent =	(int) ( parentBounds.getHeight() -
																						(componentBounds.getY() - parentBounds.getY()) -
																						componentBounds.getHeight()
																						);
*/
			_pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent =	unzoom( (int) ( parentBounds.getHeight() -
																						componentBounds.getY() -
																						componentBounds.getHeight()
																						), 
																					currentZoomFactor );

		}
//		else if( isFlagActive( MOVE_TO_BOTTOM ) )
		{
			_pixelsFromTheComponentTopBoundaryToTheBottomOfTheParent =	unzoom( (int) ( parentBounds.getHeight() -
																					(componentBounds.getY() - parentBounds.getY())
																					), 
																					currentZoomFactor );

		}

		if( _triedToRegisterListeners )
			registerListeners();
	}

	protected int getOriginalWitdh()
	{
		return( getLength( _pixelsFromTheComponentLeftBoundaryToTheRightOfTheParent,
							_pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent )
			);
	}

	protected int getOriginalHeight()
	{
		return( getLength( _pixelsFromTheComponentTopBoundaryToTheBottomOfTheParent,
							_pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent )
			);
	}

	protected int getLength( int minorBoundaryToMax, int majorBoundaryToMax )
	{
		return( minorBoundaryToMax - majorBoundaryToMax );
	}

	protected double getCenterPercentajeOfParent( int minorBoundaryToMax, int majorBoundaryToMax, int max )
	{
		double result = 0d;
		if( max > 0 )
			result = ( ( ( max - minorBoundaryToMax ) + ( (double) getLength( minorBoundaryToMax, majorBoundaryToMax  ) ) / 2 ) / max );

		return( result );
	}

	protected double getOriginalHorizontalCenterPercentageOfMax()
	{
		return( getCenterPercentajeOfParent( _pixelsFromTheComponentLeftBoundaryToTheRightOfTheParent,
												_pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent,
												_originalWidthOfParent )
			);
	}

	protected double getOriginalVerticalCenterPercentageOfMax()
	{
		return( getCenterPercentajeOfParent( _pixelsFromTheComponentTopBoundaryToTheBottomOfTheParent,
											_pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent,
											_originalHeightOfParent )
			);
	}

	public boolean isParentComponentListenerAdded()
	{
		return( _parentComponentListener != null );
	}

	protected void addParentListeners()
	{
		Component parent = _component.getParent();
		
		if( parent instanceof JViewport )
		{
			JViewport jvp = (JViewport) parent;
			if( _parentChangeListener == null )
			{
				_parentChangeListener = (evt) ->
					parentResized();
				jvp.addChangeListener(_parentChangeListener);
			}
		}
		else if( _parentComponentListener == null )
		{
			_parentComponentListener = createComponentListener( () -> parentResized() );
			if( ! addComponentListenerToParent( _parentComponentListener ) )
				_parentComponentListener = null;
		}
	}

	public void registerListeners()
	{
		_triedToRegisterListeners = true;

		if( matches() )
		{
			int kk = 1;
		}

		addParentListeners();

		if( _compComponentListener == null )
		{
			_compComponentListener = createComponentListener( () -> compResized() );
			_component.addComponentListener(_compComponentListener);
		}

		if( _mouseAdapter == null )
		{
			_mouseAdapter = this.createMouseAdapter();
			_component.addMouseListener(_mouseAdapter );
			_component.addMouseMotionListener(_mouseAdapter );
		}
	}

	protected ExtendedZoomSemaphore isExtendedZoomSemaphoreActivated()
	{
		ExtendedZoomSemaphore result = getExtendedZoomSemaphore();
		if( ( result == null ) || !result.isActivated() )
			result = null;

		return( result );
	}

	protected void compResized()
	{
		if( matches() )
		{
			int ii=1;
		}

		ExtendedZoomSemaphore ezs = isExtendedZoomSemaphoreActivated();
		if( ezs != null )
			SwingUtilities.invokeLater( () -> SwingUtilities.invokeLater( () -> ezs.getSemaphore().release() ) );
		setExtendedZoomSemaphore(null);
	}

	protected boolean addComponentListenerToParent( ComponentListener listener )
	{
		boolean result = false;

		Component parent = getParent(_component);
		result = ( parent != null );
		if( result )
			parent.addComponentListener( listener );

		return( result );
	}

	protected void storeCurrentCursor()
	{
		CursorFunctions.instance().storeCurrentCursor( _component.getCursor() );
	}

	protected void storeMouseEvent( MouseEvent event )
	{
		MouseFunctions.storeLastMouseEvent(event);
	}

	public void updateZoom()
	{
		execute( _newExpectedZoomParam );
	}

	protected void parentResized()
	{
		if( _component instanceof JTextPane )
		{
			int aa = 1;
		}
		if( "jTF_regex".equals( _component.getName() ) )
		{
			boolean stop = true;
		}
		if( matches() )
		{
			int kk = 1;
		}
//				javax.swing.JScrollPane cp;
//				javax.swing.plaf.metal.MetalScrollButton l;
//				JComboBox m;
//				javax.swing.plaf.metal.MetalComboBoxUI k;
//				javax.swing.plaf.metal.MetalComboBoxButton j;
//				if( ( _parent == null ) ||
//					! _parent.isResizeRelocateItemsResizeListenersBlocked() );
		{
			try
			{
				_servingResizureFromListener = true;

				parentResizedInvokeUpdateZoom();

				if( isActiveAnyFlagToResizeScrollableComp() &&
					( _component.getParent() instanceof JViewport ) )
				{
					resizeScrollableComponent();
				}
//				else if( parentIsContentPane() )
//				{
//					SwingUtilities.invokeLater( () -> execute( _newExpectedZoomParam ) );
//				}
			}
			finally
			{
				_servingResizureFromListener = false;
				_previousResizureServedTimestamp = System.currentTimeMillis();
			}
		}
	}

	protected void parentResizedInvokeUpdateZoom()
	{
		if( parentIsContentPane() )
			ExecutionFunctions.instance().nestedInvokeLater( 3,
				() -> updateZoomInvokeLaterPurge( this::updateZoom ) );
//		else if( isInsideSplitPane() )
//		{
//			ExecutionFunctions.instance().nestedInvokeLater( 1,
//				() -> updateZoomInvokeLaterPurge( this::updateZoom ) );
//			updateZoom();
//		}
		else
			updateZoom();
	}

	protected boolean isInsideSplitPane()
	{
		return( ContainerFunctions.instance().isInsideSplitPane( getComponent() ) );
	}

	protected ComponentListener createComponentListener( Runnable runnable )
	{
		return( new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				runnable.run();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}

		} );
	}

	protected MouseAdapter createMouseAdapter()
	{
		return( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				storeMouseEvent(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				storeMouseEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				storeMouseEvent(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				storeMouseEvent(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				storeMouseEvent(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				storeMouseEvent(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				storeMouseEvent(e);
			}
		} );
	}

	protected Dimension resizeScrollableComponent()
	{
		Dimension newSize = null;

		newSize = calculateSizeOfScrollableComponent();

//		Dimension previousFreeDimension = _scrollableFreeComponentSize;

		if( isFlagActive(RESIZE_SCROLLABLE_HORIZONTAL_FREE) ||
			( isFlagActive(RESIZE_SCROLLABLE_VERTICAL_FREE) ) )
		{
//			_scrollableFreeComponentSize = null;
//			sizeChanged( null, previousFreeDimension );
			sizeChanged( null, _scrollableFreeComponentSize );
		}
		else if( ! newSize.equals( _component.getPreferredSize() ) )
		{
			_component.setPreferredSize( newSize );
			JScrollPane sp = getScrollPaneOfViewportView();
			if( sp != null )
				sp.setViewportView( _component);
		}

		return( newSize );
	}

	protected Dimension calculateSizeOfScrollableComponent()
	{
		Dimension newSize = null;
		if( isFlagActive( RESIZE_PROPORTIONAL ) )
			newSize = ViewFunctions.instance().getNewDimension(
									getComponentOriginalDimensions().getOriginalPreferredSize(),
									_newExpectedZoomParam.getZoomFactor() );
		else
			newSize = _component.getParent().getSize();

		if( _minimumPreferredSize != null )
		{
			newSize.width = IntegerFunctions.max( newSize.width, _minimumPreferredSize.width );
			newSize.height = IntegerFunctions.max( newSize.height, _minimumPreferredSize.height );
		}

		return( newSize );
	}

	public boolean isFlagActive( int flag )
	{
		return( (_flags & flag) != 0 );
	}

	protected Font getZoomedFont( Font originalFont, Double originalFontSize, double zoomFactor )
	{
		Font result = null;

		if( originalFontSize != null )
		{
			float newFontSize = FontFunctions.instance().getZoomedFontSize( originalFontSize.intValue(), zoomFactor );
			result = FontFunctions.instance().getResizedFont(originalFont, newFontSize);
		}

		return( result );
	}

	protected void zoomFonts( double zoomFactor )
	{
		if( _component.getClass().getName().equals( "com.frojasg1.chesspdfbrowser.view.chess.ChessTreeGameTextPane" ) )
		{
			int kk=1;
		}

		if( ! isFlagActive( DO_NOT_ZOOM_FONT ) )
		{
			Font newFont = getZoomedFont( _component.getFont(), _compOrigDimen.getOriginalFontSize(), zoomFactor );
			if( matches() )
			{
				int kk=1;
			}

			if( newFont != null )
				_component.setFont( newFont );

			if( _component instanceof JComponent )
			{
				JComponent jcomp = (JComponent) _component;
				if( jcomp.getBorder() instanceof TitledBorder )
				{
					TitledBorder tb = (TitledBorder) jcomp.getBorder();
					Font newFontOfTitledBorder = getZoomedFont( tb.getTitleFont(),
																_compOrigDimen.getOriginalFontSizeOfTitledBorder(),
																zoomFactor );
					if( newFontOfTitledBorder != null )
						tb.setTitleFont( newFont );
				}
			}
		}
	}

	protected Icon getIcon()
	{
		Icon result = null;
		if( _component instanceof JLabel )
		{
			JLabel jl = (JLabel) _component;

			result = jl.getIcon();
		}
		else if( _component instanceof MetalComboBoxButton )
		{
			MetalComboBoxButton mcbb = (MetalComboBoxButton) _component;
			result = mcbb.getComboIcon();
		}
		else if( _component instanceof AbstractButton )
		{
			AbstractButton ab = (AbstractButton) _component;

			result = ab.getIcon();
		}

		return( result );
	}

	protected void zoomIcon( double zoomFactor )
	{
		Icon icon = getIcon();

		if( icon instanceof ZoomIcon )
		{
			ZoomIcon zi = (ZoomIcon) icon;

			zi.setZoomFactor(zoomFactor);
		}
	}

	protected void executePopupMenu( ZoomParam zp )
	{
		if( ( _previousZoomFactor != zp.getZoomFactor() ) &&
			( _component instanceof JTextComponent ) )
		{
			JTextComponent jtc = (JTextComponent) _component;
			JPopupMenu jppm = _parent.getNonInheritedPopupMenu( jtc );
			if( jppm != null )
			{
				_parent.executeResizeRelocateItemRecursive( jppm );
/*
				ResizeRelocateItem rri = _parent.getResizeRelocateComponentItem( jppm );
				if( rri != null )
					rri.execute( zp );
*/
			}
		}
	}
/*
	protected void zoomInsets( double zoomFactor )
	{
		if( _component instanceof JComponent )
		{
			JComponent jcomp = (JComponent) _component;
			
			if( jcomp.getBorder() instanceof ZoomInterface )
			{
				ZoomInterface zi = (ZoomInterface) jcomp.getBorder();
				zi.setZoomFactor( zoomFactor );
			}
		}
	}
*/
	protected void setZoomFactorZoomInterface( double zoomFactor )
	{
		if( _component instanceof ZoomInterface )
		{
			if( matches() )
			{
				int kk=0;
			}
			ZoomInterface zi = (ZoomInterface) _component;
			zi.setZoomFactor( zoomFactor );

			_component.repaint();
		}
	}

	protected Dimension execute_basic( double zoomFactor )
	{
		if( this.parentIsContentPane() )
		{
			int kk = 1;
		}

		if( matches() )
		{
			int kk=1;
		}

		if( _component instanceof JTextPane )
		{
			int kk=1;
		}
		
		zoomFonts( zoomFactor );

//		zoomInsets( zoomFactor );

		zoomIcon( zoomFactor );

		updateJScrollBar( zoomFactor );

		setZoomFactorZoomInterface( zoomFactor );

		if( !( _component instanceof JInternalFrame ) && 
			( 
				( _component instanceof JPopupMenu ) ||	// if JPopupMenu, we change only the font size.
				( _component.getParent() == null )
			)
			)
			return( null );

		Dimension newSize = null;
		boolean hasToChangeBoundsAtElse = hasToChangeBoundsAtElse();
		if( !( _component instanceof JInternalFrame ) && 
			( _component.getParent().getLayout() != null )&&
			!( _component.getParent() instanceof JSplitPane ) &&
			!( _component.getParent() instanceof JViewport ) )
		{
			if( _component instanceof ResizeSizeComponent )
				newSize = changeOnlySize( zoomFactor );
		}
		else if( hasToChangeBoundsAtElse )// normal
			newSize = changeBoundsAndRepositionScrolls( zoomFactor );

		if( ( newSize == null ) && !hasToChangeBoundsAtElse )
//		else
			newSize = changeBoundsAndRepositionScrolls( zoomFactor );

		return( newSize );
	}

	protected void invokeLaterPurge( String functionName, Runnable runnable )
	{
		_invokeLaterPurge.invokeLater( functionName, runnable);
	}

	protected void updateZoomInvokeLaterPurge( Runnable runnable )
	{
		invokeLaterPurge( "updateZoom", runnable);
	}

	protected boolean hasToChangeBoundsAtElse()
	{
		return( isFlagActive( SKIP_CHANGE_BOUNDS_AND_REPOSITION ) );//||
//				( _component instanceof JPopupMenu ) );
	}

	protected void resizeRelocate( Component comp, ZoomParam zp )
	{
		ResizeRelocateItem rri = getResizeRelocateItem(comp);

		if( rri != null )
			rri.execute( zp );
	}

	protected boolean matches()
	{
		boolean result = false;
		
		result = ( _component instanceof JRadioButtonMenuItem ) &&
				( ( JRadioButtonMenuItem ) _component ).getText().equals( "CAT" );
//		result = ( _component instanceof JTabbedPane );
//		result = ( _component.getClass().getName().contains( "LineOfTagsJPanel" ) );

//		result = _component.getParent() instanceof JViewport;
/*
		if( _component != null )
			result = Objects.equals( _component.getName(), "OptionsJPanel" );

		if( _component instanceof Container )
		{
			Container cont = (Container) _component;
//			if( cont.getComponentCount() > 0 )
//				result = "InternalChessEngineAnalysisJPanel".equals( cont.getComponent(0).getName() );
			if( cont.getComponentCount() > 0 )
				result = "SimpleVariantAnalysisJPanel_internal".equals( cont.getComponent(0).getName() );
		}
*/
//		boolean result = "jP_GlyphData".equals( _component.getName() );
//		boolean result = "name=jB_modify,icon=com/frojasg1/generic/resources/addremovemodify/modify.png".equals( _component.getName() );
/*
		if( _component instanceof ZoomMetalScrollButton_forScrollBar )
		{
			JComboBox combo = null;
			
			Component parent = _component.getParent();
			while( ( parent != null ) && ( combo == null ) )
			{
				if( parent instanceof BasicComboPopup )
					combo = ReflectionFunctions.instance().getAttribute( "comboBox",
																		JComboBox.class, parent,
																		BasicComboPopup.class );
				parent = parent.getParent();
			}

			if( combo != null )
				result = ( ( combo.getName() != null ) &&
							( combo.getName().equals( "jCb_locale1" ) ) );
		}
*/
		return( result );
	}

	protected Dimension changeOnlySize( double zoomFactor )
	{
		Dimension result = null;

		if( _component instanceof ComponentWithIconForZoomInterface )
		{
			( (ComponentWithIconForZoomInterface) _component ).setZoomFactor( zoomFactor );
			result = _component.getPreferredSize();
		}
		else
		{
			Rectangle newBounds = getOriginalComponentBounds( zoomFactor );

			Dimension newPreferredSize = calculateNewPreferredSize( zoomFactor );
			if( _component.isMinimumSizeSet() )
			{
				Dimension newMinimumSize = calculateNewMinimumSize( zoomFactor );
				_component.setMinimumSize( newMinimumSize );
			}

			if( _component.isMaximumSizeSet() )
			{
				Dimension newMaximumSize = calculateNewMaximumSize( zoomFactor );
				_component.setMaximumSize( newMaximumSize );
			}

			if( _component.isPreferredSizeSet() )
			{
				_component.setPreferredSize( newPreferredSize );
			}

	//		Dimension result = new Dimension( newBounds.width, newBounds.height );
			result = newPreferredSize;
			_component.setSize(result);
		}

		return( result );
	}

	protected boolean hasToChangeBoundsScrollable()
	{
		return( _sizeChangedObserved == null );
	}

	protected Dimension changeBoundsAndRepositionScrolls( double zoomFactor )
	{
		Dimension result = null;

//		if( isActiveAnyFlagToResizeScrollableComp() )
		if( getScrollPaneOfViewportView() != null )
		{


			if( _sizeChangedObserved == null ) // if we are not a size changed listener, we have to update the scrolls
			{
				if( isFlagActive( RESIZE_PROPORTIONAL ) )
					resizeScrollableComponent();

				if ( _pickedWaitingForUpdate &&
						( zoomFactor != _previousZoomFactorWhenPickingData )
					)
				{
					_pickedWaitingForUpdate = false;

					result = changeBoundsNormal( zoomFactor );

					if( hasToRelocateScrolls( zoomFactor, _previousZoomFactorWhenPickingData ) )
					{
						repositionScrolls(zoomFactor);
					}

					_previousZoomFactorWhenPickingData = zoomFactor;
				}
			}
/*
			if( _pickedWaitingForUpdate &&
				( zoomFactor != _previousZoomFactorWhenPickingData ) )
			{
				if( hasToChangeBoundsScrollable() )
				{
					_pickedWaitingForUpdate = false;

					result = changeBoundsScrollable( zoomFactor );

					if( hasToRelocateScrolls( zoomFactor, _previousZoomFactorWhenPickingData ) )
					{
						repositionScrolls(zoomFactor);
					}

					_previousZoomFactorWhenPickingData = zoomFactor;
				}
			}
*/
                }
		else
		{
			result = changeBoundsNormal( zoomFactor );
		}

		return( result );
	}

	protected Insets getInsets( Component comp )
	{
		Insets result = ZERO_INSETS;

		if( comp instanceof JComponent )
			result = ( (JComponent) comp ).getInsets();

		return( result );
	}

	protected Insets getInsets()
	{
		return( getInsets( _component ) );
	}
/*
	protected Dimension changeBoundsScrollable( double zoomFactor )
	{
		Component parent = _component.getParent();
		
		if( parent != null )
		{
			Insets insets = getInsets( _component.getParent() );

			int horizInsets = insets.left + insets.right;
			int vertInsets = insets.top + insets.bottom;
			int width = _minimumPreferredSize.width - horizInsets;
			int height = _minimumPreferredSize.height - vertInsets;

			if( isFlagActive( RESIZE_HORIZONTALLY_BY_ZOOM_FACTOR ) ||
				isFlagActive( RESIZE_HORIZONTALLY_BY_FONT_SIZE ) )
			{
				double factorOfFactors = getZoomFactorForScrollableResizing(
							isFlagActive( RESIZE_HORIZONTALLY_BY_FONT_SIZE ),
							_previousZoomFactorWhenPickingData,
							zoomFactor );

				width = IntegerFunctions.zoomValueCeil( width, factorOfFactors ) + horizInsets + 2;
			}

			if( isFlagActive( RESIZE_VERTICALLY_BY_ZOOM_FACTOR ) ||
				isFlagActive( RESIZE_VERTICALLY_BY_FONT_SIZE ) )
			{
				double factorOfFactors = getZoomFactorForScrollableResizing(
						isFlagActive( RESIZE_VERTICALLY_BY_FONT_SIZE ),
						_previousZoomFactorWhenPickingData,
						zoomFactor );

				height = IntegerFunctions.zoomValueCeil(width, factorOfFactors ) + vertInsets;
			}

			_minimumPreferredSize.width = width;
			_minimumPreferredSize.height = height;
		}

		return( resizeScrollableComponent() );
	}
*/
	protected boolean isViewportView()
	{
		return( isViewportView( _component ) );
	}

	protected boolean isViewportView( Component component )
	{
		return( ComponentFunctions.instance().isViewportView(component) );
	}

	protected boolean hasToChangeBounds( double zoomFactor, Rectangle parentBounds )
	{
		return( !isViewportView() &&
				( ( _previousParentBounds == null ) || !_previousParentBounds.equals( parentBounds ) )
				||
				( _previousZoomFactor != zoomFactor ) ||
				getForceExecution() );
	}

	protected Rectangle calculateNewBoundsViewportView( double zoomFactor )
	{
		Dimension newDimen = ViewFunctions.instance().getNewDimension(_compOrigDimen.getOriginalPreferredSize(), zoomFactor );

		int newWidth = newDimen.width;
		JScrollBar hsv = getScrollPaneOfViewportView().getHorizontalScrollBar();

		if( hsv != null )
			newWidth = hsv.getVisibleAmount();

		Rectangle result = new Rectangle( 0, 0, newWidth, newDimen.height );

		return( result );
	}

	public Rectangle calculateNewBounds( double zoomFactor, Rectangle parentBounds )
	{
		Rectangle result = null;
		if( isViewportView() )
			result = calculateNewBoundsViewportView(zoomFactor);
		else
			result = calculateOnlyNewBounds_parent( zoomFactor, parentBounds );

		return( result );
	}

	protected Dimension changeBoundsNormal( double zoomFactor )
	{
		Dimension result = null;

		if( matches() )
		{
			int kk=1;
		}
		Rectangle parentBounds = getParentBounds( _component, zoomFactor );

		if( hasToChangeBounds( zoomFactor, parentBounds ) )
		{
			Rectangle newBounds = calculateNewBounds( zoomFactor, parentBounds );

			Dimension newMinimumSize = calculateNewMinimumSize( zoomFactor );
			Dimension newMaximumSize = calculateNewMaximumSize( zoomFactor );
			if( _component.isMinimumSizeSet() )
				_component.setMinimumSize( newMinimumSize );
			if( _component.isMaximumSizeSet() )
				_component.setMaximumSize( newMaximumSize );

			if( matches() )
			{
				int kk=1;
			}

			Dimension newSize = new Dimension( newBounds.width, newBounds.height );

			if( _component.isPreferredSizeSet() )
				_component.setPreferredSize( newSize );

			if( _component instanceof ZoomComponentInterface )
			{
				ZoomComponentInterface zci = (ZoomComponentInterface) _component;
				zci.setZoomFactor(zoomFactor);
			}

			setBounds( newBounds, zoomFactor );
			result = new Dimension( newBounds.width, newBounds.height );

			_previousParentBounds = parentBounds;
		}

		return( result );
	}

	protected boolean configuredToRelocateAnyScrollPosition()
	{
		return( ! isFlagActive( NOT_RELOCATE_HORIZONTAL_BAR_SCROLL_IN_ZOOM ) ||
				! isFlagActive( NOT_RELOCATE_VERTICAL_BAR_SCROLL_IN_ZOOM ) );
	}

	protected boolean hasToRelocateScrolls( double zoomFactor, double previousZoomFactor )
	{
		return( ( zoomFactor != previousZoomFactor ) &&
					configuredToRelocateAnyScrollPosition() );
	}

	public boolean isActiveAnyFlagToResizeHorizontallyScrollableComp()
	{
		return( isFlagActive( RESIZE_SCROLLABLE_HORIZONTAL_FREE ) );
	}

	public boolean isActiveAnyFlagToResizeVerticallyScrollableComp()
	{
		return( isFlagActive( RESIZE_SCROLLABLE_VERTICAL_FREE ) );
	}

	public boolean isActiveAnyFlagToResizeScrollableComp()
	{
		return( isActiveAnyFlagToResizeHorizontallyScrollableComp() ||
				isActiveAnyFlagToResizeVerticallyScrollableComp() );
	}

	protected void repositionScroll( JScrollBar sb, Double previousPositionPercent, int totalSize )
	{
		if( sb != null )
		{
			int position = 0;

			if( previousPositionPercent != null )
			{
				position = IntegerFunctions.zoomValueInt( totalSize, previousPositionPercent );
				Integer posInt = position;

				Runnable il = () -> {
								sb.setValue( posInt );
								};

				ThreadFunctions.instance().delayedSafeInvoke( () -> {
							SwingUtilities.invokeLater( il );
				},
															50 );
			}
		}
	}

	protected void repositionScrolls( double zoomFactor )
	{
		JScrollPane sp = getScrollPaneOfViewportView();
		if( sp != null )
		{
			Dimension size = _component.getSize();
			
			if( size == null )
				size = _component.getPreferredSize();

			double factorOfFactors = 1.0D;

			if( !isFlagActive( NOT_RELOCATE_HORIZONTAL_BAR_SCROLL_IN_ZOOM ) )
			{
/*				// for vertical resizing, horizontal scrollbar must be adjusted
				factorOfFactors = getZoomFactorForScrollableResizing(
										isFlagActive( RESIZE_VERTICALLY_BY_FONT_SIZE ),
										_previousZoomFactorWhenPickingData,
										zoomFactor );
*/
				repositionScroll( sp.getHorizontalScrollBar(), _previousHorizontalBarPositionPercent, size.width );
			}

			if( !isFlagActive( NOT_RELOCATE_VERTICAL_BAR_SCROLL_IN_ZOOM ) )
			{
				// for horizontal resizing, vertical scrollbar must be adjusted
/*				factorOfFactors = getZoomFactorForScrollableResizing(
								isFlagActive( RESIZE_HORIZONTALLY_BY_FONT_SIZE ),
								_previousZoomFactorWhenPickingData,
								zoomFactor );
*/
				repositionScroll( sp.getVerticalScrollBar(), _previousVerticalBarPositionPercent, size.height );
			}
		}
	}

	protected boolean hasToSetBounds()
	{
		return( ! isActiveAnyFlagToResizeScrollableComp() );
	}

	protected void setBounds( Rectangle newBounds, double zoomFactor )
	{
		if( _component.getParent() instanceof JSplitPane )
		{
			_component.setPreferredSize( new Dimension( newBounds.width, newBounds.height ) );
		}
		else
		{
			_component.setBounds( newBounds );
			_boundsWereChanged = true;
		}
	}

	@Override
	public void execute( ZoomParam zp )
	{
		if( matches() )
		{
			int ii=1;
		}

		if( ( _component instanceof JScrollPane ) &&
			( _component.getParent() instanceof BasicComboPopup ) )
		{
			return;
		}

//		if( isViewportView( _component.getParent() ) )
//			SwingUtilities.invokeLater( () -> execute_internal( zp ) );
//		else
//			execute_internal( zp );

//		if( parentIsContentPane() )
//			SwingUtilities.invokeLater( () -> execute_internal( zp ) );
//		else
			execute_internal( zp );
	}

	protected boolean parentIsContentPane()
	{
		return( ContainerFunctions.instance().isContentPane( getParent() ) );
	}

	protected double getNewExpectedZoomParam()
	{
		double result = 1.0D;
		if( _newExpectedZoomParam != null )
			result = _newExpectedZoomParam.getZoomFactor();

		return( result );
	}

	protected boolean isExpectedZoomFactor( ZoomParam zp )
	{
		boolean result = ( zp != null ) && ( zp.getZoomFactor() == getNewExpectedZoomParam() );

		return( result );
	}

	public void execute_internal( ZoomParam zp )
	{
		_boundsWereChanged = false;
		boolean hasToReleaseSemaphore = isExpectedZoomFactor( zp );

		if( matches() )
		{
			int kk=0;
		}

		if( _component instanceof JPopupMenu )
		{
			int ii=0;
		}

		if( _component.getClass().getName().equals( "javax.swing.plaf.metal.MetalComboBoxButton" ) )
		{
			int kk=1;
		}
//		_lastZoomParam = zp;
		
		double zoomFactor = zp.getZoomFactor();

		setZoomFactorZoomInterface( zoomFactor );

		if( isFlagActive(ONLY_ZOOM_FONT) )
			zoomFonts( zp.getZoomFactor() );
		else if( ! ( _component instanceof JFrame ) &&
			! ( _component instanceof JDialog ) )
		{
			executePopupMenu( zp );

			Dimension newDimen = execute_basic( zoomFactor );

			if( _component instanceof ResizableComponentInterface )
			{
				ResizableComponentInterface rci = (ResizableComponentInterface) _component;
				rci.doTasksAfterResizingComponent(zoomFactor);
			}
			else if( ( _component instanceof AbstractButton ) &&
					!( _component instanceof ZoomComponentInterface )  &&
					!( _component instanceof ResizableImageJButton )  &&
					!( _component instanceof ResizableImageJToggleButton ) )
			{
				String iconResourceName = getIconResourceName( _component );
				if( iconResourceName != null )
				{
					ViewFunctions.instance().addImageToButtonAccurate( (AbstractButton) _component,
													getImage( iconResourceName ), new Insets(2,2,2,2) );
				}
			}

			if( ( newDimen != null ) ||
				( _previousZoomFactor != zoomFactor ) )
			{
				invokeListeners( _component, zoomFactor );
			}

//			if( _component instanceof JScrollPane )
//				resizeRelocate( ( (JScrollPane)_component).getViewport().getView(), zp );
		}

		setForceExecution( false );
		_previousZoomFactor = zoomFactor;
		_newExpectedZoomParam = zp;

		if( hasToReleaseSemaphore )
			releaseSemaphore();
	}

	protected BufferedImage getImage( String iconResourceName )
	{
		BufferedImage result = ExecutionFunctions.instance().safeFunctionExecution( () ->
			ImageFunctions.instance().loadImageFromJar(iconResourceName) );

		if( ( result != null ) && ( isDarkMode() ) )
			result = ImageFunctions.instance().invertImage(result);

		return( result );
	}

	protected boolean isDarkMode()
	{
		return( FrameworkComponentFunctions.instance().isDarkMode(_component) );
	}

	protected void releaseSemaphore()
	{
		ExtendedZoomSemaphore ezs = isExtendedZoomSemaphoreActivated();
		if( ezs != null )
		{
			if( _boundsWereChanged )
				ezs.getSemaphore().release();
			else
				ezs.skipRelease( getNumberOfChildrenWithExtendedZoomSemaphore( ezs ) );
		}
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC, RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}

	protected ResizeRelocateItem getResizeRelocateItem( Component comp )
	{
		return( getIfNotNull( _parent,
						rrip -> rrip.getResizeRelocateComponentItem(comp) ) );
	}

	protected int getNumberOfChildrenWithExtendedZoomSemaphore( ExtendedZoomSemaphore ezs )
	{
		AtomicInteger result = new AtomicInteger(0);
		if( ezs != null )
		{
			ComponentFunctions.instance().browseComponentHierarchy(_component,
				(comp) -> {
					ResizeRelocateItem rri = getResizeRelocateItem(comp);
					if( ( rri != null ) && ( rri.getExtendedZoomSemaphore() == ezs ) )
						result.incrementAndGet();
					return( null );
				});
		}

		return( result.get() );
	}

	protected String getIconResourceName( Component component )
	{
		ComponentNameComponents cnc = new ComponentNameComponents( );
		cnc.init( component.getName() );
		
		return( cnc.getComponent( "icon" ) );
	}

	protected void invokeListeners( Component comp, double zoomFactor )
	{
		_listeners.resizeRelocateItemComponentResized( comp, zoomFactor );
	}

	protected double getZoomFactorForScrollableResizing( boolean isByFontSize,
														double previousZoomFactor,
														double zoomFactor )
	{
		double factor = 1.0D;

		factor = zoomFactor / previousZoomFactor;
		if( isByFontSize &&	( _previousFontSize != null ) &&
			factor != 1.0D )
		{
			Integer newFontSize = getFontSize();
			if( newFontSize != null )
				factor = ( (double) newFontSize ) /
							( (double) this._previousFontSize );
		}

		return( factor );
	}

	public Rectangle calculateOnlyNewBounds( double zoomFactor, Rectangle parentBounds )
	{
		return( calculateOnlyNewBounds_parent( zoomFactor, parentBounds ) );
	}

	public final Rectangle calculateOnlyNewBounds_parent( double zoomFactor, Rectangle parentBounds )
	{
//		Rectangle defaultNewBounds = getDefaultZoomedBounds( zoomFactor );

		Rectangle newBounds = getOriginalComponentBounds( zoomFactor );
//		Rectangle newBounds = _component.getBounds();
		Dimension minimumSize = calculateNewMinimumSize( zoomFactor );

		Component parent = getParent();
		
		Rectangle realParentBounds = getRealParentBounds();
		Rectangle originalBounds100 = _compOrigDimen.getOriginalBounds();

		if( parent != null )
		{
			if( parent instanceof JSplitPane )
			{
				_component.setLocation( parentBounds.x, parentBounds.y );
			}
/*
			int newX = (int) _component.getX();
			int newY = (int) _component.getY();
			int width = (int) _component.getWidth();
			int height = (int) _component.getHeight();
*/
			int newX = (int) newBounds.getX();
			int newY = (int) newBounds.getY();
			int width = (int) newBounds.getWidth();
			int height = (int) newBounds.getHeight();

			if( isFlagActive( FILL_WHOLE_PARENT ) )
			{
				if( parentIsContentPane() )
				{
//					LOGGER.info( "parentIsContentPane, calculateOnlyNewBounds_parent invoked for {} times", _calculateOnlyNewBounds_parentCount++ );
				}

				if( realParentBounds != null )
				{
					width = realParentBounds.width;
					height = realParentBounds.height;
				}
				else
				{
					width = parentBounds.width;
					height = parentBounds.height;
				}
			}
			else
			{
				if( ! isAnyHorizontalModifierFlagActive() )
				{
//					newX = defaultNewBounds.x;
//					width = defaultNewBounds.width;
					newX = newBounds.x;
					width = newBounds.width;
				}
				else if( isFlagActive( FILL_WHOLE_WIDTH ) )
				{
					newX = 0;
					width = (int) parentBounds.getWidth();
				}
				else if( isFlagActive( UP_TO_RIGHT ) )
				{
					int delta = realParentBounds.width -
								newBounds.x -
								newBounds.width;// the least width

					if( delta != 0 )
						width = IntegerFunctions.limit( newBounds.width + delta,
														minimumSize.width, _component.getMaximumSize().width );
				}
				else if( isFlagActive( RESIZE_TO_RIGHT + MOVE_TO_RIGHT ) )
				{
					if( isFlagActive( RESIZE_TO_RIGHT ) )
					{
		/*				int delta = (int)(	parentBounds.getWidth() -
											( newBounds.getX() - parentBounds.getX() ) -
											newBounds.getWidth()  -
											_pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent * zoomFactor );
		*/
						Integer delta = null;
						if( ( _originalParentBounds != null ) && ( realParentBounds != null ) )
							delta = zoom( unzoom(realParentBounds.width, zoomFactor) - _originalParentBounds.width -
										( unzoom( unzoom( newBounds.width, zoomFactor ) - originalBounds100.width, _horizontalResizeFactor ) ),
										zoomFactor );

						if( delta == null )
							delta = (int)(	parentBounds.getWidth() -
											newBounds.getX() -
											newBounds.getWidth()  -
											ceil( _pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent, zoomFactor ) );	// the least width

						if( delta != 0 )
						{
//							width = (int) Math.max( 
//												Math.min( newBounds.getWidth() + getHorizontalResizeDelta( delta ), _component.getMaximumSize().getWidth() ),
//												minimumSize.getWidth() );
							width = IntegerFunctions.limit( newBounds.width + getHorizontalResizeDelta( delta ),
															minimumSize.width, _component.getMaximumSize().width );
		//					_component.setSize( width, (int) _component.getHeight() );
						}
					}

					if( isFlagActive( MOVE_TO_RIGHT ) )
					{
						Integer delta = null;
						if( ( _originalParentBounds != null ) && ( realParentBounds != null ) )
							delta = zoom( unzoom(realParentBounds.width, zoomFactor) - _originalParentBounds.width -
										( unzoom( unzoom( newBounds.x, zoomFactor ) - originalBounds100.x, _horizontalMoveFactor )  ),
										zoomFactor );

						if( delta == null )
							delta = parentBounds.width -
									( newBounds.x - parentBounds.x ) -
									(int) floor( _pixelsFromTheComponentLeftBoundaryToTheRightOfTheParent, zoomFactor );	// the hiest x

						if( delta != 0 )
						{
							newX = newBounds.x + getHorizontalMoveDelta( delta );
		//					_component.setLocation( newX, (int) _component.getY() );
						}
					}
				}
				else
				{
					if( _originalWidthOfParent != 0 )
					{
						double factor = (double) parentBounds.getWidth() / _originalWidthOfParent;

						int unalteredWidth = floor( _originalWidth, zoomFactor );
						int rightBound = 0;
						if( isFlagActive( MOVE_MID_HORIZONTAL_SIDE_PROPORTIONAL ) )
						{
							newX = ceil( parentBounds.width, this.getOriginalHorizontalCenterPercentageOfMax() ) - unalteredWidth / 2;  // the hiest x
							rightBound = newX + unalteredWidth;
						}
						else
						{
							if( isFlagActive( MOVE_LEFT_SIDE_PROPORTIONAL ) )
								newX = parentBounds.width - ceil( _pixelsFromTheComponentLeftBoundaryToTheRightOfTheParent, factor );  // the hiest x

		//					int rightBound = newX + _component.getWidth();
							rightBound = newX + width;

							if( isFlagActive( MOVE_RIGHT_SIDE_PROPORTIONAL ) )
							{
								rightBound = parentBounds.width - floor( _pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent, factor );   // the lowest x
							}
							else if( isFlagActive( MOVE_RIGHT_SIDE_TO_RIGHT ) )
							{
								rightBound = parentBounds.width - floor( _pixelsLeftFromTheComponentRightBoundaryToTheRightOfTheParent, zoomFactor );   // the lowest x
							}
							else if( isFlagActive( LEAVE_WIDTH_UNALTERED ) )
							{
		//						rightBound = newX + _originalWidth;
								rightBound = newX + unalteredWidth;	// the lowest x
							}
						}

						width = rightBound - newX;
					}
				}

				if( ! isAnyVerticalModifierFlagActive() )
				{
//					newY = defaultNewBounds.y;
//					height = defaultNewBounds.height;
					newY = newBounds.y;
					height = newBounds.height;
				}
				else if( isFlagActive( FILL_WHOLE_HEIGHT ) )
				{
					newY = 0;
					height = (int) parentBounds.getHeight();
				}
				else if( isFlagActive( UP_TO_BOTTOM ) )
				{
					if( parentIsContentPane() )
					{
						int ii=0;
					}
					int delta = realParentBounds.height -
								newBounds.y -
								newBounds.height; // the least width

					if( delta != 0 )
						height = IntegerFunctions.limit( newBounds.height + delta,
														minimumSize.height, _component.getMaximumSize().height );
				}
				else if( isFlagActive( RESIZE_TO_BOTTOM + MOVE_TO_BOTTOM ) )
				{
					if( isFlagActive( RESIZE_TO_BOTTOM ) )
					{
		/*				int delta = (int) ( parentBounds.getHeight() -
											( newBounds.getY()  - parentBounds.getY()) -
											newBounds.getHeight()  -
											_pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent * zoomFactor );
		*/
						Integer delta = null;
						if( ( _originalParentBounds != null ) && ( realParentBounds != null ) )
							delta = zoom( unzoom(realParentBounds.height, zoomFactor) - _originalParentBounds.height -
										( unzoom( unzoom( newBounds.height, zoomFactor ) - originalBounds100.height, _verticalResizeFactor ) ),
										zoomFactor );

						if( delta == null )
							delta = (int) ( parentBounds.getHeight() -
											newBounds.getY() -
											newBounds.getHeight()  -
											ceil( _pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent, zoomFactor ) );	// the least height
						if( delta != 0 )
						{
//							height = (int) Math.max(
//											Math.min( newBounds.getHeight() + getVerticalResizeDelta( delta ), _component.getMaximumSize().getHeight() ),
//											minimumSize.getHeight() );
							height = IntegerFunctions.limit( newBounds.height + getVerticalResizeDelta( delta ),
															minimumSize.height, _component.getMaximumSize().height );
		//					_component.setSize( new Double( _component.getWidth() ).intValue(), new Double( _component.getHeight() ).intValue() + delta );
						}
					}

					if( isFlagActive( MOVE_TO_BOTTOM ) )
					{
						Integer delta = null;
						if( ( _originalParentBounds != null ) && ( realParentBounds != null ) )
							delta = zoom( unzoom(realParentBounds.height, zoomFactor) - _originalParentBounds.height -
										( unzoom( unzoom( newBounds.y, zoomFactor ) - originalBounds100.y, _verticalMoveFactor )  ),
										zoomFactor );

						if( delta == null )
							delta = (int) ( parentBounds.getHeight() -
											( newBounds.getY()  - parentBounds.getY() )  -
											ceil(_pixelsFromTheComponentTopBoundaryToTheBottomOfTheParent, zoomFactor ) );	// the hiest y
						if( delta != 0 )
						{
							newY = (int)newBounds.getY() + getVerticalMoveDelta( delta );
		//					_component.setLocation( (int) _component.getX(), newY );
						}
					}
				}
				else
				{
					if( _originalHeightOfParent != 0 )
					{
						double factor = (double) parentBounds.getHeight() / _originalHeightOfParent;

						int unalteredHeight = floor( _originalHeight, zoomFactor );
						int bottomBound = 0;
						if( isFlagActive( MOVE_MID_VERTICAL_SIDE_PROPORTIONAL ) )
						{
							newY = ceil( parentBounds.width, this.getOriginalVerticalCenterPercentageOfMax() ) - unalteredHeight / 2;  // the hiest x
							bottomBound = newX + unalteredHeight;
						}
						else
						{
							if( isFlagActive( MOVE_TOP_SIDE_PROPORTIONAL ) )
								newY = (int)( parentBounds.getHeight() - _pixelsFromTheComponentTopBoundaryToTheBottomOfTheParent * factor );

							bottomBound = newY + height;

							if( isFlagActive( MOVE_BOTTOM_SIDE_PROPORTIONAL ) )
							{
								bottomBound = (int)( parentBounds.getHeight() - _pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent * factor );
							}
							else if( isFlagActive( MOVE_BOTTOM_SIDE_TO_BOTTOM ) )
							{
								bottomBound = (int)( parentBounds.getHeight() - _pixelsLeftFromTheComponentBottomBoundaryToTheBottomOfTheParent );
							}
							else if( isFlagActive( LEAVE_HEIGHT_UNALTERED ) )
							{
//								bottomBound = newX + _originalHeight;
								bottomBound = newY + unalteredHeight;
							}
						}
						height = bottomBound - newY;
					}
				}
			}

//			Dimension minSize = _component.getMinimumSize();
//			Dimension maxSize = _component.getMaximumSize();
			Dimension minSize = minimumSize;
			Dimension maxSize = calculateNewMaximumSize( zoomFactor );

			width = (int) Math.min( maxSize.getWidth(), Math.max( minSize.getWidth(), width ) );
			height = (int) Math.min( maxSize.getHeight(), Math.max( minSize.getHeight(), height ) );

			newBounds = new Rectangle( newX, newY, width, height );
		}

//		if( _component.getClass().getName().equals( "com.frojasg1.general.desktop.view.panels.NavigatorJPanel" ) )
		if( ( _component.getName() != null ) && ( _component.getName().equals( "jPanel3_test" ) ) )
		{
			int aa = 1;
		}

		return( newBounds );
	}
	
	protected int weightDelta( int delta, double factor )
	{
		return( (int) Math.round( delta * factor ) );
	}

	protected int getHorizontalMoveDelta( int delta )
	{
		return( weightDelta( delta, _horizontalMoveFactor ) );
	}

	protected int getHorizontalResizeDelta( int delta )
	{
		return( weightDelta( delta, _horizontalResizeFactor ) );
	}

	protected int getVerticalMoveDelta( int delta )
	{
		return( weightDelta( delta, _verticalMoveFactor ) );
	}

	protected int getVerticalResizeDelta( int delta )
	{
		return( weightDelta( delta, _verticalResizeFactor ) );
	}
/*
	public int getDividerLocation( double zoomFactor )
	{
		return( -1 );
	}

	protected int getDividerLocation( JSplitPane jsp, double zoomFactor )
	{
		int result = jsp.getDividerLocation();

		ResizeRelocateItem rri = null;

		if( ( rri = _parent.getResizeRelocateComponentItem(jsp) ) != null )
		{
			result = rri.getDividerLocation( zoomFactor );
		}

		return( result );
	}
*/
	protected Rectangle getParentBounds( Component comp, double zoomFactor ) throws IllegalArgumentException
	{
		Component parent = comp.getParent();
/*
		if( ( parent instanceof JPanel ) &&
			( parent.getName() != null ) &&
			( parent.getName().length() >= 5 ) &&
			parent.getName().substring( 0, 5 ).equals( "null." ) )
		{
			JPanel panel = (JPanel) parent;
			panel.revalidate();
			panel.repaint();
		}
*/

		// if the parent is a kind of root pane, we skip all of them until we reach the root component.
		while( ( parent != null ) && (parent.getParent() != null ) &&
				( ( parent instanceof JLayeredPane ) || ( parent instanceof JRootPane ) ||
					( parent instanceof JPanel ) && ( parent.getName() != null ) &&
					( parent.getName().length() >= 5 ) &&
					parent.getName().substring( 0, 5 ).equals( "null." )
				)
			)
		{
			parent = parent.getParent();
		}

		Rectangle result = null;
		if( parent != null )
		{
			int left = -1;
			int top = -1;
			int width = -1;
			int height = -1;

			if( parent instanceof JSplitPane )
			{
				JSplitPane jsp = (JSplitPane) parent;

//				int dividerLocation = getDividerLocation( jsp, zoomFactor );
				int dividerLocation = jsp.getDividerLocation( );
//				Rectangle bounds = jsp.getBounds();
				Rectangle bounds = getComponentBounds( jsp );

				if( jsp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
				{
					if( jsp.getLeftComponent() == comp )		result = calculateBounds_JSplitPane( jsp, bounds, dividerLocation, LEFT );
					else if( jsp.getRightComponent() == comp )	result = calculateBounds_JSplitPane( jsp, bounds, dividerLocation, RIGHT );
					else
					{
						throw( new IllegalArgumentException( "The split pane parent was HorizontalSplit, but the child was not found" ) );
					}
				}
				else if( jsp.getOrientation() == JSplitPane.VERTICAL_SPLIT )
				{
					if( jsp.getTopComponent() == comp )		result = calculateBounds_JSplitPane( jsp, bounds, dividerLocation, TOP );
					else if( jsp.getBottomComponent() == comp )	result = calculateBounds_JSplitPane( jsp, bounds, dividerLocation, BOTTOM );
					else
					{
						throw( new IllegalArgumentException( "The split pane parent was VerticalSplit, but the child was not found" ) );
					}
				}
				else
				{
					throw( new IllegalArgumentException( "The split pane parent was neither HorizontalSplit neither VerticalSplit" ) );
				}
			}
			else
			{
//				Dimension dim = parent.getSize();
				Dimension dim = getComponentSize( parent );
				left = 0;
				top = 0;
				width = dim.width;
				height = dim.height;

				Insets insets = ViewFunctions.instance().getBorders( parent );
				if( insets != null )
				{
					width = width - insets.left - insets.right;
					height = height - insets.top - insets.bottom;
				}

				result = new Rectangle( left, top, width, height );
			}
		}
		return( result );
	}

	protected Dimension getComponentSize( Component comp )
	{
		Dimension dim = null;
		
		if( !( comp instanceof JPanel ) &&
			( comp.getParent() instanceof JSplitPane ) )
		{
			dim = comp.getPreferredSize();
		}
		else
		{
			dim = comp.getSize();
		}

		int width = dim.width;
		int height = dim.height;

//		if( ( width == 0 ) || ( comp instanceof JFrame ) ||
//				(comp instanceof JDialog ) )
		if( width == 0 )
		{
			width = (int) comp.getPreferredSize().getWidth();
		}

//		if( ( height == 0 ) || ( comp instanceof JFrame ) ||
//				(comp instanceof JDialog ) )
		if( height == 0 )
		{
			height = (int) comp.getPreferredSize().getHeight();
		}

		Dimension result = new Dimension( width, height );
		
		return( result );
	}

	protected Rectangle getOriginalComponentBounds( double zoomFactor )
	{
/*
		Rectangle result = _component.getBounds();
		
		Dimension size = getComponentSize( comp );
		
		result.width = size.width;
		result.height = size.height;
*/
		Rectangle originalBounds100 = _compOrigDimen.getOriginalBounds();
//		Rectangle currentBounds = _component.getBounds();
		Rectangle currentBounds = getComponentBounds( _component );

		Rectangle result = getCurrentOrOriginalBounds( originalBounds100, currentBounds, zoomFactor );

		return( result );
	}

	protected Rectangle getDefaultZoomedBounds( double zoomFactor )
	{
		Rectangle originalBounds100 = _compOrigDimen.getOriginalBounds();
		Insets insets = null;
		Rectangle originalBoundsZoomed = ViewFunctions.instance().getNewRectangle( originalBounds100, insets, zoomFactor );

		return( originalBoundsZoomed );
	}

	protected int calculateMaxError( double newZoomFactor, double previousZoomFactor )
	{
		int result = 0;
		
		if( previousZoomFactor > 0 )
			result = (int) Math.ceil( newZoomFactor/previousZoomFactor + 2 );

		return( result );
	}

	protected Rectangle getCurrentOrOriginalBounds( Rectangle originalBounds100, Rectangle currentBounds,
													double zoomFactor )
	{
		Rectangle result = null;

		Insets insets = null;

		Rectangle originalBoundsZoomed = ViewFunctions.instance().getNewRectangle( originalBounds100, insets, zoomFactor );
		Rectangle currentBoundsZoomed = currentBounds;

		if( !isAlreadyZoomed() )
			currentBoundsZoomed = ViewFunctions.instance().getNewRectangle( currentBounds, insets, zoomFactor/_previousZoomFactor );
		else
			setIsAlreadyZoomed( false );

		int maxError = calculateMaxError( zoomFactor, _previousZoomFactor );
/*
		if( ViewFunctions.instance().rectanglesCloseEnough( originalBoundsZoomed, currentBoundsZoomed, maxError ) )
			result = originalBoundsZoomed;
		else
			result = currentBoundsZoomed;
*/
		int xx = selectNumberWithError( originalBoundsZoomed.x, currentBoundsZoomed.x, maxError );
		int yy = selectNumberWithError( originalBoundsZoomed.y, currentBoundsZoomed.y, maxError );
		int width = selectNumberWithError( originalBoundsZoomed.width, currentBoundsZoomed.width, maxError );
		int height = selectNumberWithError( originalBoundsZoomed.height, currentBoundsZoomed.height, maxError );

		result = new Rectangle( xx, yy, width, height );

		return( result );
	}

	protected Rectangle getComponentBounds( Component comp )
	{
		Rectangle result = _component.getBounds();
		
		Dimension size = getComponentSize( comp );
		
		result.width = size.width;
		result.height = size.height;

		return( result );
	}

	protected Rectangle calculateBounds_JSplitPane( JSplitPane jsp, Rectangle newBounds, int dividerLocation, int position )
	{
		Insets insets = jsp.getInsets();

		int left = -1;
		int top = -1;
		int width = -1;
		int height = -1;

		if( position == LEFT )
		{
			top = insets.top;
			height = (int) newBounds.getHeight() - top - insets.bottom;
			left = insets.left;
			width = dividerLocation - left;
		}
		else if( position == RIGHT )
		{
			top = insets.top;
			height = (int) newBounds.getHeight() - top - insets.bottom;
			left = dividerLocation + jsp.getDividerSize();
			width = (int) newBounds.getWidth() - left - insets.right;
		}
		else if( position == TOP )
		{
			left = insets.left;
			width = (int)newBounds.getWidth() - left - insets.right;
			top = insets.top;
			height = dividerLocation - top;
		}
		else if( position == BOTTOM )
		{
			left = insets.left;
			width = (int)newBounds.getWidth() - left - insets.right;
			top = dividerLocation + jsp.getDividerSize();
			height = (int) newBounds.getHeight() - top - insets.bottom;
		}

		Rectangle result = new Rectangle( left, top, width, height );
		return( result );
	}

	protected Dimension getCurrentOrOriginalSize( Dimension originalSize100, Dimension currentSize,
													double zoomFactor )
	{
		Dimension result = null;

		Insets insets = null;

		Dimension originalSizeZoomed = ViewFunctions.instance().getNewDimension( originalSize100, insets, zoomFactor );
		Dimension currentSizeZoomed = ViewFunctions.instance().getNewDimension( currentSize, insets, zoomFactor/_previousZoomFactor );

		int maxError = calculateMaxError( zoomFactor, _previousZoomFactor );

		if( ( _component instanceof JLabel ) ||
			( _component instanceof JButton ) ||
			( _component instanceof JRadioButton ) ||
			( _component instanceof JCheckBox ) ||
			( _component instanceof JComboBox ) )
		{
			result = originalSizeZoomed;
		}
		else
		{
			int width = selectNumberWithError( originalSizeZoomed.width, currentSizeZoomed.width, maxError );
			int height = selectNumberWithError( originalSizeZoomed.height, currentSizeZoomed.height, maxError );

			result = new Dimension( width, height );
		}

		return( result );
	}

	protected int selectNumberWithError( int originalNumber, int newNumber, int error )
	{
		return( ( IntegerFunctions.abs( originalNumber - newNumber ) > error ) ? newNumber : originalNumber );
	}

	protected Dimension calculateNewMinimumSize( double zoomFactor )
	{
		Dimension result = null;
		if( _component.isMinimumSizeSet() )
		{
			Dimension originalSize100 = _compOrigDimen.getOriginalMinimumSize();
			Dimension currentSize = _component.getMinimumSize();

			result = getCurrentOrOriginalSize( originalSize100, currentSize, zoomFactor );
		}
		else
			result = new Dimension( 0, 0 );

		return( result );
	}

	protected Dimension calculateNewMaximumSize( double zoomFactor )
	{
		Dimension result = null;

		if( _component.isMaximumSizeSet() )
		{
			Dimension originalSize100 = _compOrigDimen.getOriginalMaximumSize();
			Dimension currentSize = _component.getMaximumSize();

			result = getCurrentOrOriginalSize( originalSize100, currentSize, zoomFactor );
		}
		else
			result = new Dimension( 20000, 20000 );

		return( result );
	}

	protected Dimension calculateNewPreferredSize( double zoomFactor )
	{
		Dimension originalSize100 = _compOrigDimen.getOriginalPreferredSize();
		Dimension currentSize = _component.getPreferredSize();

		Dimension result = getCurrentOrOriginalSize( originalSize100, currentSize, zoomFactor );

		return( result );
	}

	protected Integer getFontSize( Font font )
	{
		Integer result = -1;
		if( font != null )
			result = font.getSize();
		return( result );
	}

	protected Integer getFontSize()
	{
		return( getFontSize( _component.getFont() ) );
	}

	public void pickPreviousData( ZoomParam zp )
	{
		if( !_pickedWaitingForUpdate &&
			( _previousZoomFactorWhenPickingData != zp.getZoomFactor() ) )
		{
			_pickedWaitingForUpdate = true;
			_previousFontSize = getFontSize();

			_previousHorizontalBarPositionPercent = null;
			_previousVerticalBarPositionPercent = null;
			JScrollPane sp = getScrollPaneOfViewportView();
			if( sp != null )
			{
				Dimension preferredSize = _component.getPreferredSize();
				_previousHorizontalBarPositionPercent = getPercentage( getBarPosition( sp.getHorizontalScrollBar() ),
																preferredSize.width );
				_previousVerticalBarPositionPercent = getPercentage( getBarPosition( sp.getVerticalScrollBar() ),
																preferredSize.height );
			}

			// TODO: I have changed that, that can damage current way to treat free resizable custom JTextPanes
			_previousZoomFactorWhenPickingData = zp.getZoomFactor();
		}
	}

	protected Double getPercentage( Integer value, Integer total )
	{
		Double result = null;
		
		if( ( value != null ) && ( total != null ) && ( total != 0 ) )
		{
			result = ( (double) value ) / ( (double) total );
		}

		return( result );
	}

	protected Integer getBarPosition( JScrollBar sb )
	{
		Integer result = null;
		if( sb != null )
			result = sb.getValue();

		return( result );
	}

	protected JScrollPane getScrollPaneOfViewportView()
	{
		return( ComponentFunctions.instance().getScrollPaneOfViewportView(_component) );
	}

	public Dimension getMinimumPreferredSize()
	{
		return( _minimumPreferredSize );
	}

	public void setMinimumPreferredSize( Dimension value )
	{
		if( ( value != null ) &&
			!_minimumPreferredSize.equals( value ) )
		{
/*			Dimension minimumSize = _component.getMinimumSize();
			if( this.isActiveAnyFlagToResizeHorizontallyScrollableComp())
				minimumSize.width = IntegerFunctions.max( minimumSize.width, value.width );

			if( this.isActiveAnyFlagToResizeVerticallyScrollableComp() )
				minimumSize.height = IntegerFunctions.max( minimumSize.height, value.height );
*/
			_minimumPreferredSize = value;

			SwingUtilities.invokeLater(() -> {
				_component.setMinimumSize(value);
				_component.setPreferredSize(value);
				if( isActiveAnyFlagToResizeScrollableComp() &&
					( _component.getParent() instanceof JViewport ) )
				{
					resizeScrollableComponent();
				}

//				modifyScrollsToRight();

				JScrollPane sp = getScrollPaneOfViewportView();
				boolean hasFocus = _component.hasFocus();
				if( sp != null )
					sp.setViewportView(_component);
				if( hasFocus )
					_component.requestFocus();

				_component.repaint();
			} );

		}
	}

	@Override
	public void sizeChanged(GenericObserved observed, Dimension newSize)
	{
		SwingUtilities.invokeLater( () -> sizeChangedInternal( observed, newSize ) );
	}

	public void sizeChangedInternal(GenericObserved observed, Dimension newSize)
	{
		_pickedWaitingForUpdate = false;

		Dimension previousPreferredSize = _component.getPreferredSize();
		Dimension newPreferredSize = calculateNewMinimumPreferredSizeForScrollableFreeComponent( newSize );
//		System.out.println( "previousPreferredSize: " + previousPreferredSize + "      newPreferredSize: " + newPreferredSize );

		if( ( newPreferredSize != null ) && !previousPreferredSize.equals( newPreferredSize ) )
		{
			setMinimumPreferredSize( newPreferredSize );
//			System.out.println( "minimumSize set: " + newPreferredSize );

			if( hasToRelocateScrolls( _previousZoomFactor, _previousZoomFactorWhenPickingData ) )
			{
				ThreadFunctions.instance().delayedSafeInvoke( () -> {
						SwingUtilities.invokeLater( ()->{
							repositionScrolls(_previousZoomFactor );
							_previousZoomFactorWhenPickingData = _previousZoomFactor;
						} );
							}, 50 );
			}
/*
			SwingUtilities.invokeLater( () -> { _component.revalidate();
												_component.repaint(); } );
*/
		}
	}

	protected Dimension getViewportSize()
	{
		Dimension parentSize = null;
		
		if( _component.getParent() instanceof JViewport )
		{
			parentSize = _component.getParent().getSize();
			if( ( parentSize.height == 0 ) ||
				( parentSize.width == 0 ) )
			{
				Rectangle visibleRect = ( (JViewport) _component.getParent() ).getVisibleRect();

				parentSize.width = visibleRect.width;
				parentSize.height = visibleRect.height;
			}

			if( ( parentSize.height == 0 ) ||
				( parentSize.width == 0 ) )
			{
				JScrollPane sp = getScrollPaneOfViewportView();
				if( sp != null )
					parentSize = sp.getSize();
			}

//			System.out.println( "getViewportSize() : " + parentSize );
		}

		return( parentSize );
	}

	protected Dimension calculateNewMinimumPreferredSizeForScrollableFreeComponent( Dimension newSize )
	{
		Dimension result = null;
		Dimension parentSize = getViewportSize();
		if( ! Objects.equals( _scrollableFreeComponentSize, newSize ) ||
			! Objects.equals( _previousViewportSize, parentSize ) )
		{
			if( newSize == null )
				newSize = parentSize;

			_scrollableFreeComponentSize = newSize;

			_previousViewportSize = parentSize;
			if( _scrollableFreeComponentSize != null )
			{
				result = new Dimension( newSize.width, newSize.height );
				if( _component.getParent() instanceof JViewport )
				{
					if( isFlagActive(RESIZE_SCROLLABLE_HORIZONTAL_FREE) )
						result.width = IntegerFunctions.max( result.width, parentSize.width );

					if( isFlagActive(RESIZE_SCROLLABLE_VERTICAL_FREE) )
						result.height = IntegerFunctions.max( result.height, parentSize.height );
				}
			}
		}

		return( result );
	}

	protected int zoom( int value, double zoomFactor )
	{
		return( IntegerFunctions.zoomValueRound(value, zoomFactor) );
	}

	protected int unzoom( int value, double zoomFactor )
	{
		return( IntegerFunctions.zoomValueRound(value, 1 / zoomFactor) );
	}

	protected int ceil( int value, double zoomFactor )
	{
		return( IntegerFunctions.zoomValueCeil(value, zoomFactor) );
	}

	protected int floor( int value, double zoomFactor )
	{
		return( IntegerFunctions.zoomValueFloor(value, zoomFactor) );
	}

	protected Component getParent()
	{
		return( getParent( getComponent() ) );
	}

	protected Component getParent( Component comp )
	{
		Component result = null;

		if( comp != null )
			result = comp.getParent();

		if( result instanceof JViewport )
			result = result.getParent();

		return( result );
	}

	protected void unregisterListenersFromParent( Component comp )
	{
//		Component parent = getParent( _component );
		Component parent2 = getParent( comp );
//		if( ( _parentComponentListener != null ) && ( parent != null ) && ( parent2 != parent ) )
		if( ( _parentComponentListener != null ) && ( parent2 != null ) )
		{
			parent2.removeComponentListener(_parentComponentListener);

//			if( !isComponentListenerAdded( parent2, _parentComponentListener ) )
//				_parentComponentListener = null;
		}

		if( ( _parentChangeListener != null ) && ( parent2 instanceof JViewport ) )
			( (JViewport) parent2 ).removeChangeListener(_parentChangeListener);
	}

	protected void unregisterListenersFromComp( Component comp )
	{
		if( _mouseAdapter != null )
		{
			_component.removeMouseListener(_mouseAdapter );
			_component.removeMouseMotionListener(_mouseAdapter );

			if( !isMouseListenerAdded( comp, _mouseAdapter ) )
				_mouseAdapter = null;
		}
		
		if( _compComponentListener != null )
			_component.removeComponentListener(_compComponentListener);
	}

	protected boolean isComponentListenerAdded( Component comp, ComponentListener listener )
	{
		return( ComponentFunctions.instance().isComponentListenerAdded( comp, listener ) );
	}

	protected boolean isMouseListenerAdded( Component comp, MouseListener listener )
	{
		return( ComponentFunctions.instance().isMouseListenerAdded( comp, listener ) );
	}


	protected void unregisterListeners( Component comp )
	{
		unregisterListenersFromParent( comp );
		unregisterListenersFromComp( comp );
	}

	protected void unregisterListeners()
	{
		if( matches() )
		{
			int kk = 1;
		}
		unregisterListeners(null);
	}

	public void dispose()
	{
		_listeners.clear();

		unregisterListeners();

		if( _sizeChangedObserved != null )
			_sizeChangedObserved.removeListener(this);

		_sizeChangedObserved = null;
		_parent = null;
	}

	protected static class ComponentResizedListenerList
		extends ListOfListenersImp<ResizeRelocateItemComponentResizedListener>
		implements ResizeRelocateItemComponentResizedListener
	{
		@Override
		public void resizeRelocateItemComponentResized( Component comp, double newZoomFactor )
		{
			for( ResizeRelocateItemComponentResizedListener crl: _list )
			{
				try
				{
					crl.resizeRelocateItemComponentResized( comp, newZoomFactor );
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static ResizeRelocateItem buildResizeRelocateItem( Component comp, int flags,
																ResizeRelocateItem_parent parent,
																boolean postpone_initialization,
																Boolean isAlreadyZoomed ) throws InternException
	{
		ResizeRelocateItem result = null;

		if( comp instanceof JSplitPane )
		{
			result = new ResizeRelocateItem_JSplitPane( (JSplitPane) comp, flags,
														parent, postpone_initialization,
														isAlreadyZoomed );
		}
		else if( comp instanceof JTable )
		{
			result = new ResizeRelocateItem_JTable( (JTable) comp, flags, parent,
													postpone_initialization,
													isAlreadyZoomed );
		}
		else if( comp instanceof JComboBox )
		{
			result = new ResizeRelocateItem_JComboBox( (JComboBox) comp, flags, parent,
													postpone_initialization,
													isAlreadyZoomed );
		}
		else
		{
			result = new ResizeRelocateItem( comp, flags, parent, postpone_initialization,
													isAlreadyZoomed );
		}

		return( result );
	}

	protected boolean isAnyHorizontalModifierFlagActive()
	{
		return( isFlagActive( HORIZONTAL_MODIFIER_FLAGS ) );
	}

	protected boolean isAnyVerticalModifierFlagActive()
	{
		return( isFlagActive( VERTICAL_MODIFIER_FLAGS ) );
	}
}
