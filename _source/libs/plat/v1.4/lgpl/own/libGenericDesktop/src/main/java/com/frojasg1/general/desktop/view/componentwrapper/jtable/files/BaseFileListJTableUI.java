/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.files;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import com.frojasg1.general.desktop.view.colors.Colors;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableFileDragAndDropEventContext;
import com.frojasg1.general.desktop.view.paint.PaintTextContext;
import com.frojasg1.general.desktop.view.transfer.component.impl.FileDragAndDropComponentContext;
import com.frojasg1.general.desktop.view.transfer.file.FileDragAndDropEvent;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.structures.Pair;
import com.frojasg1.general.view.ReleaseResourcesable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BaseFileListJTableUI extends BasicTableUI
	implements ColorThemeInvertible, ReleaseResourcesable
{
	protected static final Color NORMAL_COLOR_FOR_DRAG_AND_DROP = Colors.BUTAN.darker();
	protected static final Color INVERTED_COLOR_FOR_DRAG_AND_DROP = Colors.BUTAN.brighter();

	protected static final int HUNDRED_PERCENT_ARROW_WIDTH = 45;

	protected Color _dragAndDropDestinationColor = NORMAL_COLOR_FOR_DRAG_AND_DROP;

	protected PaintTextContext _paintTextContext;

	protected FileDragAndDropComponentContext _fileDragAndDropContext;

	protected Integer _rowIndexWhereToDrop;

	protected AtomicReference<BufferedImage> _upArrow = new AtomicReference<>();
	protected AtomicReference<BufferedImage> _downArrow = new AtomicReference<>();

	protected BufferedImage _upArrowOriginalImage;
	protected BufferedImage _downArrowOriginalImage;

	protected Integer _latestYYPositionForDragAndDrop;
	protected Integer _currentYYPositionForDragAndDrop;

	protected int _printAllVerticalTop = 0;
	protected int _printAllVerticalBottom = 10000;

	protected boolean _blockRepaint = false;

	protected Pair<BufferedImage, Graphics> _previousPaintedImage;

	@Override
    public void installUI(JComponent c)
	{
		super.installUI(c);

		if( FrameworkComponentFunctions.instance().isDarkMode(c) )
			invertColors(null);

		releaseResources();
		_fileDragAndDropContext = createDragAndDropContext(getTable());

		loadImages();
	}

	protected BufferedImage loadImage( String resourceName )
	{
		BufferedImage result = ExecutionFunctions.instance().safeFunctionExecution(
			() -> ImageFunctions.instance().loadImageFromJar(resourceName) );
		return( result );
	}

	protected void loadImages()
	{
		_upArrowOriginalImage = loadImage( "com/frojasg1/generic/resources/upanddown/up.png" );
		_downArrowOriginalImage = loadImage( "com/frojasg1/generic/resources/upanddown/down.png" );
	}

	protected JTable getTable()
	{
		return( table );
	}

	protected String getStringOverTable() {
		String result = null;
		if( _paintTextContext != null )
			result = _paintTextContext.getText();

		return( result);
	}

	public Runnable getStartDraggingListener() {
		return( getDadCompContextListener( FileDragAndDropComponentContext::getStartDraggingListener ) );
	}

	public void setStartDraggingListener(Runnable listener) {
		setDadCompContextListener( listener, FileDragAndDropComponentContext::setStartDraggingListener);
	}

	public Runnable getStopDraggingListener() {
		return( getDadCompContextListener( FileDragAndDropComponentContext::getStopDraggingListener ) );
	}

	public void setStopDraggingListener(Runnable listener) {
		setDadCompContextListener( listener, FileDragAndDropComponentContext::setStopDraggingListener);
	}

	public Runnable getDropListener() {
		return( getDadCompContextListener( FileDragAndDropComponentContext::getDropListener ) );
	}

	protected void setDadCompContextListener(Runnable listener,
										BiConsumer<FileDragAndDropComponentContext, Runnable> listenerSetter) {
		FileDragAndDropComponentContext fileDragAndDropContext = getFileDragAndDropContext();
		if( fileDragAndDropContext != null )
			listenerSetter.accept(fileDragAndDropContext, listener);
	}

	public void setDropListener(Runnable dropListener) {
		setDadCompContextListener( dropListener, FileDragAndDropComponentContext::setDropListener);
	}

	protected Runnable getDadCompContextListener(Function<FileDragAndDropComponentContext, Runnable> listenerGetter ) {
		FileDragAndDropComponentContext fileDragAndDropContext = getFileDragAndDropContext();
		Runnable result = null;
		if( fileDragAndDropContext != null )
			result = listenerGetter.apply(fileDragAndDropContext);

		return( result );
	}

	public Runnable getDeletePressedListener() {
		return( getDadCompContextListener( FileDragAndDropComponentContext::getDeleteListener ) );
	}

	public void setDeleteListener(Runnable deleteListener) {
		setDadCompContextListener( deleteListener, FileDragAndDropComponentContext::setDeleteListener);
	}

	protected FileDragAndDropComponentContext createDragAndDropContext( JComponent comp )
	{
		return( new FileDragAndDropComponentContext( comp ).init() );
	}

	public void processDragAndDropEvent( FileDragAndDropEvent<JTableFileDragAndDropEventContext> evt )
	{
		getFileDragAndDropContext().processDragAndDropEvent(evt);
	}

	protected Rectangle getClip(JComponent jComponent)
	{
		return( ComponentFunctions.instance().getClip( jComponent ) );
	}

	protected Rectangle getClipForPrintAll(JComponent jComponent)
	{
		return( new Rectangle( 0, _printAllVerticalTop, jComponent.getWidth(),
								_printAllVerticalBottom -_printAllVerticalTop ) );
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
		grp2.setColor( getTable().getBackground() );
		grp2.fillRect(0, 0, size.width, size.height);

		return( new Pair<>( img, grp2 ) );
	}

	@Override
	public void paint(Graphics grp, JComponent jComponent)
	{
		Rectangle clip = grp.getClipBounds();
		if( isBlockRepaint() && _previousPaintedImage != null )
		{
			grp.drawImage(_previousPaintedImage.getKey(), 0, 0, null);
		}
		else
		{
			Point point = getDraggingPoint();

			Pair<BufferedImage, Graphics> pair = getCachedImage(grp, jComponent);
			BufferedImage img = pair.getKey();
			Graphics grp2 = pair.getValue();

			if( point != null )
			{
	//			grp2.setPaintMode();

				if( !isPrintAll() )
				{
					clip = getClip( jComponent );
	//			grp2.setClip( new Rectangle(0, 0, size.width, size.height) );
					grp2.setClip( clip );
				}

	//			boolean db = jComponent.isDoubleBuffered();
	//			jComponent.setDoubleBuffered(false);
	//			boolean db2 = getTable().getDefaultRenderer(String.class);
				super.paint( grp2, jComponent );
	//			jComponent.setDoubleBuffered(db);

	//			paintString( grp2, jComponent, clip );

				showDraggingPosition(grp2, point);

				printAllIfNecessary(jComponent);
				_latestYYPositionForDragAndDrop = _currentYYPositionForDragAndDrop;
			}
			else
			{
				super.paint( grp2, jComponent );

				paintString( grp2, jComponent, getClip( jComponent ) );

				_currentYYPositionForDragAndDrop = null;
				printAllIfNecessary(jComponent);
				_latestYYPositionForDragAndDrop = null;
			}

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

	

	public PaintTextContext getPaintTextContext() {
		return _paintTextContext;
	}

	public void setPaintTextContext(PaintTextContext _stringOverTableContext) {
		this._paintTextContext = _stringOverTableContext;
	}

	protected void paintString( Graphics grp, JComponent jComponent, Rectangle bounds )
	{
		ImageFunctions.instance().paintStringCentered(grp, jComponent,
											getPaintTextContext(), bounds);
	}

	protected boolean isPrintAll()
	{
		return( ! Objects.equals( _latestYYPositionForDragAndDrop,
								_currentYYPositionForDragAndDrop) );
	}

	protected void printAllIfNecessary(JComponent jComponent)
	{
		if( isPrintAll() )
			SwingUtilities.invokeLater( () -> printAll(jComponent) );
	}

	protected void printAll(JComponent jComponent)
	{
//		Graphics grp = getTable().getGraphics();
		Rectangle clip = getClipForPrintAll( jComponent );
//		grp.setClip( this.getClipForPrintAll( jComponent ) );

//		getTable().printAll(grp);

		getTable().repaint(25, clip.x, clip.y, clip.width, clip.height);
	}

	protected Point getDraggingPoint()
	{
		FileDragAndDropComponentContext fdadcc = getFileDragAndDropContext();
		Point result = null;
		if( ( fdadcc.isDraggingFiles() || fdadcc.isDragging() ) )
			result = fdadcc.getDragPoint();

		return( result );
	}

	protected void showDraggingPosition(Graphics grp, Point point)
	{
		if( tableHasElements() )
		{
			Rectangle currentCell = getCellBounds(point);

			int yy = _currentYYPositionForDragAndDrop = getVerticalPositionToShowDragging( currentCell, point );

			Rectangle clipBounds = ComponentFunctions.instance().getClip( getTable() );

			if( isHalfTop( clipBounds, yy ) )
				showUpArrow( grp, clipBounds, yy );
			else
				showDownArrow( grp, clipBounds, yy );
		}
	}

	protected void showUpArrow( Graphics grp, Rectangle clipBounds, int yy )
	{
		grp.setColor(_dragAndDropDestinationColor);
		showLine( grp, clipBounds, yy );
		showArrow( grp, yy, 0, _upArrowOriginalImage, _upArrow );
	}

	protected void showDownArrow( Graphics grp, Rectangle tableBounds, int yy )
	{
		grp.setColor(_dragAndDropDestinationColor);
		showLine( grp, tableBounds, yy );
		showArrow( grp, yy, -1, _downArrowOriginalImage, _downArrow );
	}

	protected void showLine( Graphics grp, Rectangle tableBounds, int yy )
	{
		int thick = getZoomedValue( 2 );
		
		int left = thick * 3;
		int right = tableBounds.width - left;

		ImageFunctions.instance().drawBoldStraightLine(grp, left, yy, right, yy, thick );
	}

	protected void showArrow( Graphics grp, int yy, int heightDirection,
							BufferedImage originalArrow,
							AtomicReference<BufferedImage> current )
	{
		BufferedImage image = zoomImageIfNecessary( originalArrow, current );

//		Rectangle bounds = getTable().getBounds();
		Rectangle bounds = grp.getClipBounds();
		if( bounds == null )
			bounds = getTable().getBounds();

		int xx = bounds.x + ( bounds.width - image.getWidth() ) / 2;
		
		int imageHeight = image.getHeight();
		int imageY = yy + imageHeight * heightDirection;

		grp.drawImage( image, xx, imageY, null);
		
		_printAllVerticalTop = yy - imageHeight * 2 - 10;
		_printAllVerticalBottom = yy + imageHeight * 2 + 10;
	}

	protected BufferedImage zoomImageIfNecessary( BufferedImage originalArrow,
							AtomicReference<BufferedImage> current )
	{
		BufferedImage result = current.get();
		if( hasToZoom(originalArrow, result) )
			current.set( resizeArrow(originalArrow) );

		return( current.get() );
	}

	protected BufferedImage resizeArrow( BufferedImage originalArrow )
	{
		return( ImageFunctions.instance().resizeImage(originalArrow, HUNDRED_PERCENT_ARROW_WIDTH,
				HUNDRED_PERCENT_ARROW_WIDTH, 0xFF424242, _dragAndDropDestinationColor.getRGB(), null) );
	}

	protected boolean hasToZoom( BufferedImage originalArrow, BufferedImage current )
	{
		boolean result = (current == null);
		if( ! result )
		{
			int zoomedWidth = getZoomedValue( HUNDRED_PERCENT_ARROW_WIDTH );
			if( IntegerFunctions.abs( zoomedWidth - current.getWidth() ) > 2 )
				result = true;
		}

		return( result );
	}

	protected int getZoomedValue( int value )
	{
		return( IntegerFunctions.zoomValueCeil(value, getZoomFactor() ) );
	}

	protected double getZoomFactor()
	{
		return( FrameworkComponentFunctions.instance().getZoomFactor(getTable() ) );
	}

	protected boolean isHalfTop( Rectangle rect, int yy )
	{
		int delta = yy - rect.y;
		boolean result = ( delta <= rect.height / 2 );

		return( result );
	}

	protected Point getDefaultCoordinate()
	{
		Point result = null;

		Rectangle rect = NullFunctions.instance().getIfNotNull(getTable().getTableHeader(),
			JTableHeader::getBounds );
		if( rect != null )
			result = new Point( rect.x + 5, rect.y + rect.height + 5 );

		if( result == null )
			result = new Point( 5, 5 );

		return( result );
	}

	// https://www.gubatron.com/blog/2013/06/19/how-to-obtain-the-coordinates-of-the-jtable-cell-a-mouse-is-hovering-over-useful-for-tooltips-within-cell-renderers/
	protected Rectangle getCellBounds( Point point )
	{
		int row = getTable().rowAtPoint(point);
		int col = getTable().columnAtPoint(point);
		return( getTable().getCellRect(row, col, false) );
	}

	protected int getVerticalPositionToShowDragging( Rectangle currentCell, Point point )
	{
		int result = 0;

		if( currentCell == null )
			currentCell = getCellBounds(getDefaultCoordinate());

		if( currentCell != null )
		{
			_rowIndexWhereToDrop = getTable().rowAtPoint( currentCell.getLocation() );

			result = currentCell.y;
			if( ! isHalfTop( currentCell, point.y ) ) // if is from half to bottom of the cell, then will be inserted after the cell row
			{
				result += currentCell.height;
				_rowIndexWhereToDrop++;
			}
		}
		else
		{
			Rectangle rect = getTable().getBounds();
			result = rect.y + rect.width;

			_rowIndexWhereToDrop = getNumRows();
		}

		return( result );
	}

	protected int getNumRows()
	{
		return( getTable().getModel().getRowCount() );
	}

	protected boolean tableHasElements()
	{
		return( getNumRows() > 0 );
	}

	@Override
	public void invertColors(ColorInversor colorInversor)
	{
		Color col = NORMAL_COLOR_FOR_DRAG_AND_DROP;
		if( _dragAndDropDestinationColor == NORMAL_COLOR_FOR_DRAG_AND_DROP )
			col = INVERTED_COLOR_FOR_DRAG_AND_DROP;

		_dragAndDropDestinationColor = col;

		_upArrow.set(null);
		_downArrow.set(null);
	}

	public FileDragAndDropComponentContext getFileDragAndDropContext() {
		return _fileDragAndDropContext;
	}

	public void setFileDragAndDropContext(FileDragAndDropComponentContext fileDragAndDropContext) {
		this._fileDragAndDropContext = fileDragAndDropContext;
	}

	protected void removeDragAndDropContext( FileDragAndDropComponentContext context )
	{
		if( context != null )
			context.releaseResources();
	}

	@Override
	public void releaseResources()
	{
		removeDragAndDropContext( getFileDragAndDropContext() );
		setFileDragAndDropContext( null );
	}

	public Integer getRowIndexWhereToDrop()
	{
		return( _rowIndexWhereToDrop );
	}

	public void setCanStartDraggingFunction(Predicate<Point> _canDragFunction) {
		getFileDragAndDropContext().setCanStartDraggingFunction(_canDragFunction);
	}

	public boolean isBlockRepaint() {
		return _blockRepaint;
	}

	public void setBlockRepaint(boolean _blockRepaint) {
		this._blockRepaint = _blockRepaint;
	}

	protected void blockRepaint()
	{
		setBlockRepaint( true );
	}

	protected void unblockRepaint()
	{
		setBlockRepaint( false );
	}
}
