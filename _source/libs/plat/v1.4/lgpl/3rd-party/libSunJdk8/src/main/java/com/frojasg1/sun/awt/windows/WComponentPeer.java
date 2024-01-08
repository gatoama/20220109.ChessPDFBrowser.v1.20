package com.frojasg1.sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.dnd.DropTarget;
import java.awt.dnd.peer.DropTargetPeer;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.PaintEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import com.frojasg1.sun.awt.AWTAccessor;
import com.frojasg1.sun.awt.CausedFocusEvent;
import com.frojasg1.sun.awt.PaintEventDispatcher;
import com.frojasg1.sun.awt.RepaintArea;
import com.frojasg1.sun.awt.SunToolkit;
import com.frojasg1.sun.awt.Win32GraphicsConfig;
import com.frojasg1.sun.awt.Win32GraphicsEnvironment;
import com.frojasg1.sun.awt.event.IgnorePaintEvent;
import com.frojasg1.sun.awt.image.SunVolatileImage;
import com.frojasg1.sun.awt.image.ToolkitImage;
import com.frojasg1.sun.awt.windows.WEmbeddedFrame;
import com.frojasg1.sun.awt.windows.WEmbeddedFramePeer;
import com.frojasg1.sun.awt.windows.WFontMetrics;
import com.frojasg1.sun.awt.windows.WGlobalCursorManager;
import com.frojasg1.sun.awt.windows.WKeyboardFocusManagerPeer;
import com.frojasg1.sun.awt.windows.WObjectPeer;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.awt.windows.WWindowPeer;
import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.ScreenUpdateManager;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;
import com.frojasg1.sun.java2d.pipe.Region;
import com.frojasg1.sun.util.logging.PlatformLogger;

public abstract class WComponentPeer extends com.frojasg1.sun.awt.windows.WObjectPeer implements ComponentPeer, DropTargetPeer {
   protected volatile long hwnd;
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WComponentPeer");
   private static final PlatformLogger shapeLog = PlatformLogger.getLogger("sun.awt.windows.shape.WComponentPeer");
   private static final PlatformLogger focusLog = PlatformLogger.getLogger("sun.awt.windows.focus.WComponentPeer");
   SurfaceData surfaceData;
   private RepaintArea paintArea;
   protected Win32GraphicsConfig winGraphicsConfig;
   boolean isLayouting = false;
   boolean paintPending = false;
   int oldWidth = -1;
   int oldHeight = -1;
   private int numBackBuffers = 0;
   private VolatileImage backBuffer = null;
   private BufferCapabilities backBufferCaps = null;
   private Color foreground;
   private Color background;
   private Font font;
   int nDropTargets;
   long nativeDropTargetContext;
   public int serialNum = 0;
   private static final double BANDING_DIVISOR = 4.0D;
   static final Font defaultFont = new Font("Dialog", 0, 12);
   private int updateX1;
   private int updateY1;
   private int updateX2;
   private int updateY2;
   private volatile boolean isAccelCapable = true;

   public native boolean isObscured();

   public boolean canDetermineObscurity() {
      return true;
   }

   private synchronized native void pShow();

   synchronized native void hide();

   synchronized native void enable();

   synchronized native void disable();

   public long getHWnd() {
      return this.hwnd;
   }

   public native Point getLocationOnScreen();

   public void setVisible(boolean var1) {
      if (var1) {
         this.show();
      } else {
         this.hide();
      }

   }

   public void show() {
      Dimension var1 = ((Component)this.target).getSize();
      this.oldHeight = var1.height;
      this.oldWidth = var1.width;
      this.pShow();
   }

   public void setEnabled(boolean var1) {
      if (var1) {
         this.enable();
      } else {
         this.disable();
      }

   }

   private native void reshapeNoCheck(int var1, int var2, int var3, int var4);

   public void setBounds(int var1, int var2, int var3, int var4, int var5) {
      this.paintPending = var3 != this.oldWidth || var4 != this.oldHeight;
      if ((var5 & 16384) != 0) {
         this.reshapeNoCheck(var1, var2, var3, var4);
      } else {
         this.reshape(var1, var2, var3, var4);
      }

      if (var3 != this.oldWidth || var4 != this.oldHeight) {
         try {
            this.replaceSurfaceData();
         } catch (InvalidPipeException var7) {
         }

         this.oldWidth = var3;
         this.oldHeight = var4;
      }

      ++this.serialNum;
   }

   void dynamicallyLayoutContainer() {
      Container var1;
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         var1 = com.frojasg1.sun.awt.windows.WToolkit.getNativeContainer((Component)this.target);
         if (var1 != null) {
            log.fine("Assertion (parent == null) failed");
         }
      }

      Container var1Final = (Container)this.target;
      com.frojasg1.sun.awt.windows.WToolkit.executeOnEventHandlerThread(var1Final, new Runnable() {
         public void run() {
            var1Final.invalidate();
            var1Final.validate();
            if (WComponentPeer.this.surfaceData instanceof D3DSurfaceData.D3DWindowSurfaceData || WComponentPeer.this.surfaceData instanceof OGLSurfaceData) {
               try {
                  WComponentPeer.this.replaceSurfaceData();
               } catch (InvalidPipeException var2) {
               }
            }

         }
      });
   }

   void paintDamagedAreaImmediately() {
      this.updateWindow();
      SunToolkit.flushPendingEvents();
      this.paintArea.paint(this.target, this.shouldClearRectBeforePaint());
   }

   synchronized native void updateWindow();

   public void paint(Graphics var1) {
      ((Component)this.target).paint(var1);
   }

   public void repaint(long var1, int var3, int var4, int var5, int var6) {
   }

   private native int[] createPrintedPixels(int var1, int var2, int var3, int var4, int var5);

   public void print(Graphics var1) {
      Component var2 = (Component)this.target;
      int var3 = var2.getWidth();
      int var4 = var2.getHeight();
      int var5 = (int)((double)var4 / 4.0D);
      if (var5 == 0) {
         var5 = var4;
      }

      for(int var6 = 0; var6 < var4; var6 += var5) {
         int var7 = var6 + var5 - 1;
         if (var7 >= var4) {
            var7 = var4 - 1;
         }

         int var8 = var7 - var6 + 1;
         Color var9 = var2.getBackground();
         int[] var10 = this.createPrintedPixels(0, var6, var3, var8, var9 == null ? 255 : var9.getAlpha());
         if (var10 != null) {
            BufferedImage var11 = new BufferedImage(var3, var8, 2);
            var11.setRGB(0, 0, var3, var8, var10, 0, var3);
            var1.drawImage(var11, 0, var6, (ImageObserver)null);
            var11.flush();
         }
      }

      var2.print(var1);
   }

   public void coalescePaintEvent(PaintEvent var1) {
      Rectangle var2 = var1.getUpdateRect();
      if (!(var1 instanceof IgnorePaintEvent)) {
         this.paintArea.add(var2, var1.getID());
      }

      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
         switch(var1.getID()) {
         case 800:
            log.finest("coalescePaintEvent: PAINT: add: x = " + var2.x + ", y = " + var2.y + ", width = " + var2.width + ", height = " + var2.height);
            return;
         case 801:
            log.finest("coalescePaintEvent: UPDATE: add: x = " + var2.x + ", y = " + var2.y + ", width = " + var2.width + ", height = " + var2.height);
            return;
         }
      }

   }

   public synchronized native void reshape(int var1, int var2, int var3, int var4);

   public boolean handleJavaKeyEvent(KeyEvent var1) {
      return false;
   }

   public void handleJavaMouseEvent(MouseEvent var1) {
      switch(var1.getID()) {
      case 501:
         if (this.target == var1.getSource() && !((Component)this.target).isFocusOwner() && com.frojasg1.sun.awt.windows.WKeyboardFocusManagerPeer.shouldFocusOnClick((Component)this.target)) {
            com.frojasg1.sun.awt.windows.WKeyboardFocusManagerPeer.requestFocusFor((Component)this.target, CausedFocusEvent.Cause.MOUSE_EVENT);
         }
      default:
      }
   }

   native void nativeHandleEvent(AWTEvent var1);

   public void handleEvent(AWTEvent var1) {
      int var2 = var1.getID();
      if (var1 instanceof InputEvent && !((InputEvent)var1).isConsumed() && ((Component)this.target).isEnabled()) {
         if (var1 instanceof MouseEvent && !(var1 instanceof MouseWheelEvent)) {
            this.handleJavaMouseEvent((MouseEvent)var1);
         } else if (var1 instanceof KeyEvent && this.handleJavaKeyEvent((KeyEvent)var1)) {
            return;
         }
      }

      switch(var2) {
      case 800:
         this.paintPending = false;
      case 801:
         if (!this.isLayouting && !this.paintPending) {
            this.paintArea.paint(this.target, this.shouldClearRectBeforePaint());
         }

         return;
      case 1004:
      case 1005:
         this.handleJavaFocusEvent((FocusEvent)var1);
      default:
         this.nativeHandleEvent(var1);
      }
   }

   void handleJavaFocusEvent(FocusEvent var1) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
         focusLog.finer(var1.toString());
      }

      this.setFocus(var1.getID() == 1004);
   }

   native void setFocus(boolean var1);

   public Dimension getMinimumSize() {
      return ((Component)this.target).getSize();
   }

   public Dimension getPreferredSize() {
      return this.getMinimumSize();
   }

   public void layout() {
   }

   public Rectangle getBounds() {
      return ((Component)this.target).getBounds();
   }

   public boolean isFocusable() {
      return false;
   }

   public GraphicsConfiguration getGraphicsConfiguration() {
      return (GraphicsConfiguration)(this.winGraphicsConfig != null ? this.winGraphicsConfig : ((Component)this.target).getGraphicsConfiguration());
   }

   public SurfaceData getSurfaceData() {
      return this.surfaceData;
   }

   public void replaceSurfaceData() {
      this.replaceSurfaceData(this.numBackBuffers, this.backBufferCaps);
   }

   public void createScreenSurface(boolean var1) {
      Win32GraphicsConfig var2 = (Win32GraphicsConfig)this.getGraphicsConfiguration();
      ScreenUpdateManager var3 = ScreenUpdateManager.getInstance();
      this.surfaceData = var3.createScreenSurface(var2, this, this.numBackBuffers, var1);
   }

   public void replaceSurfaceData(int var1, BufferCapabilities var2) {
      SurfaceData var3 = null;
      VolatileImage var4 = null;
      synchronized(((Component)this.target).getTreeLock()) {
         label51: {
            synchronized(this) {
               if (this.pData != 0L) {
                  this.numBackBuffers = var1;
                  ScreenUpdateManager var7 = ScreenUpdateManager.getInstance();
                  var3 = this.surfaceData;
                  var7.dropScreenSurface(var3);
                  this.createScreenSurface(true);
                  if (var3 != null) {
                     var3.invalidate();
                  }

                  var4 = this.backBuffer;
                  if (this.numBackBuffers > 0) {
                     this.backBufferCaps = var2;
                     Win32GraphicsConfig var8 = (Win32GraphicsConfig)this.getGraphicsConfiguration();
                     this.backBuffer = var8.createBackBuffer(this);
                  } else if (this.backBuffer != null) {
                     this.backBufferCaps = null;
                     this.backBuffer = null;
                  }
                  break label51;
               }
            }

            return;
         }
      }

      if (var3 != null) {
         var3.flush();
         var3 = null;
      }

      if (var4 != null) {
         var4.flush();
         var3 = null;
      }

   }

   public void replaceSurfaceDataLater() {
      Runnable var1 = new Runnable() {
         public void run() {
            if (!WComponentPeer.this.isDisposed()) {
               try {
                  WComponentPeer.this.replaceSurfaceData();
               } catch (InvalidPipeException var2) {
               }
            }

         }
      };
      Component var2 = (Component)this.target;
      if (!PaintEventDispatcher.getPaintEventDispatcher().queueSurfaceDataReplacing(var2, var1)) {
         this.postEvent(new InvocationEvent(var2, var1));
      }

   }

   public boolean updateGraphicsData(GraphicsConfiguration var1) {
      this.winGraphicsConfig = (Win32GraphicsConfig)var1;

      try {
         this.replaceSurfaceData();
      } catch (InvalidPipeException var3) {
      }

      return false;
   }

   public ColorModel getColorModel() {
      GraphicsConfiguration var1 = this.getGraphicsConfiguration();
      return var1 != null ? var1.getColorModel() : null;
   }

   public ColorModel getDeviceColorModel() {
      Win32GraphicsConfig var1 = (Win32GraphicsConfig)this.getGraphicsConfiguration();
      return var1 != null ? var1.getDeviceColorModel() : null;
   }

   public ColorModel getColorModel(int var1) {
      GraphicsConfiguration var2 = this.getGraphicsConfiguration();
      return var2 != null ? var2.getColorModel(var1) : null;
   }

   public Graphics getGraphics() {
      if (this.isDisposed()) {
         return null;
      } else {
         Component var1 = (Component)this.getTarget();
         Window var2 = SunToolkit.getContainingWindow(var1);
         if (var2 != null) {
            Graphics var3 = ((com.frojasg1.sun.awt.windows.WWindowPeer)var2.getPeer()).getTranslucentGraphics();
            if (var3 != null) {
               int var9 = 0;
               int var10 = 0;

               for(Object var11 = var1; var11 != var2; var11 = ((Component)var11).getParent()) {
                  var9 += ((Component)var11).getX();
                  var10 += ((Component)var11).getY();
               }

               var3.translate(var9, var10);
               var3.clipRect(0, 0, var1.getWidth(), var1.getHeight());
               return var3;
            }
         }

         SurfaceData var8 = this.surfaceData;
         if (var8 != null) {
            Object var4 = this.background;
            if (var4 == null) {
               var4 = SystemColor.window;
            }

            Object var5 = this.foreground;
            if (var5 == null) {
               var5 = SystemColor.windowText;
            }

            Font var6 = this.font;
            if (var6 == null) {
               var6 = defaultFont;
            }

            ScreenUpdateManager var7 = ScreenUpdateManager.getInstance();
            return var7.createGraphics(var8, this, (Color)var5, (Color)var4, var6);
         } else {
            return null;
         }
      }
   }

   public FontMetrics getFontMetrics(Font var1) {
      return com.frojasg1.sun.awt.windows.WFontMetrics.getFontMetrics(var1);
   }

   private synchronized native void _dispose();

   protected void disposeImpl() {
      SurfaceData var1 = this.surfaceData;
      this.surfaceData = null;
      ScreenUpdateManager.getInstance().dropScreenSurface(var1);
      var1.invalidate();
      com.frojasg1.sun.awt.windows.WToolkit.targetDisposedPeer(this.target, this);
      this._dispose();
   }

   public void disposeLater() {
      this.postEvent(new InvocationEvent(this.target, new Runnable() {
         public void run() {
            WComponentPeer.this.dispose();
         }
      }));
   }

   public synchronized void setForeground(Color var1) {
      this.foreground = var1;
      this._setForeground(var1.getRGB());
   }

   public synchronized void setBackground(Color var1) {
      this.background = var1;
      this._setBackground(var1.getRGB());
   }

   public Color getBackgroundNoSync() {
      return this.background;
   }

   private native void _setForeground(int var1);

   private native void _setBackground(int var1);

   public synchronized void setFont(Font var1) {
      this.font = var1;
      this._setFont(var1);
   }

   synchronized native void _setFont(Font var1);

   public void updateCursorImmediately() {
      com.frojasg1.sun.awt.windows.WGlobalCursorManager.getCursorManager().updateCursorImmediately();
   }
/*
   @Override
   public boolean requestFocus(Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6) {
      if (com.frojasg1.sun.awt.windows.WKeyboardFocusManagerPeer.processSynchronousLightweightTransfer((Component)this.target, var1, var2, var3, var4)) {
         return true;
      } else {
         int var7 = com.frojasg1.sun.awt.windows.WKeyboardFocusManagerPeer.shouldNativelyFocusHeavyweight((Component)this.target, var1, var2, var3, var4, var6);
         switch(var7) {
         case 0:
            return false;
         case 1:
            return true;
         case 2:
            if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
               focusLog.finer("Proceeding with request to " + var1 + " in " + this.target);
            }

            Window var8 = SunToolkit.getContainingWindow((Component)this.target);
            if (var8 == null) {
               return this.rejectFocusRequestHelper("WARNING: Parent window is null");
            } else {
               com.frojasg1.sun.awt.windows.WWindowPeer var9 = (WWindowPeer)var8.getPeer();
               if (var9 == null) {
                  return this.rejectFocusRequestHelper("WARNING: Parent window's peer is null");
               } else {
                  boolean var10 = var9.requestWindowFocus(var6);
                  if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                     focusLog.finer("Requested window focus: " + var10);
                  }

                  if (var10 && var8.isFocused()) {
                     return com.frojasg1.sun.awt.windows.WKeyboardFocusManagerPeer.deliverFocus(var1, (Component)this.target, var2, var3, var4, var6);
                  }

                  return this.rejectFocusRequestHelper("Waiting for asynchronous processing of the request");
               }
            }
         default:
            return false;
         }
      }
   }
*/
   private boolean rejectFocusRequestHelper(String var1) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
         focusLog.finer(var1);
      }

      com.frojasg1.sun.awt.windows.WKeyboardFocusManagerPeer.removeLastFocusRequest((Component)this.target);
      return false;
   }

   public Image createImage(ImageProducer var1) {
      return new ToolkitImage(var1);
   }

   public Image createImage(int var1, int var2) {
      Win32GraphicsConfig var3 = (Win32GraphicsConfig)this.getGraphicsConfiguration();
      return var3.createAcceleratedImage((Component)this.target, var1, var2);
   }

   public VolatileImage createVolatileImage(int var1, int var2) {
      return new SunVolatileImage((Component)this.target, var1, var2);
   }

   public boolean prepareImage(Image var1, int var2, int var3, ImageObserver var4) {
      return Toolkit.getDefaultToolkit().prepareImage(var1, var2, var3, var4);
   }

   public int checkImage(Image var1, int var2, int var3, ImageObserver var4) {
      return Toolkit.getDefaultToolkit().checkImage(var1, var2, var3, var4);
   }

   public String toString() {
      return this.getClass().getName() + "[" + this.target + "]";
   }

   WComponentPeer(Component var1) {
      this.target = var1;
      this.paintArea = new RepaintArea();
      this.create(this.getNativeParent());
      this.checkCreation();
      this.createScreenSurface(false);
      this.initialize();
      this.start();
   }

   abstract void create(WComponentPeer var1);

   WComponentPeer getNativeParent() {
      Container var1 = SunToolkit.getNativeContainer((Component)this.target);
      return (WComponentPeer) com.frojasg1.sun.awt.windows.WToolkit.targetToPeer(var1);
   }

   protected void checkCreation() {
      if (this.hwnd == 0L || this.pData == 0L) {
         if (this.createError != null) {
            throw this.createError;
         } else {
            throw new InternalError("couldn't create component peer");
         }
      }
   }

   synchronized native void start();

   void initialize() {
      if (((Component)this.target).isVisible()) {
         this.show();
      }

      Color var1 = ((Component)this.target).getForeground();
      if (var1 != null) {
         this.setForeground(var1);
      }

      Font var2 = ((Component)this.target).getFont();
      if (var2 != null) {
         this.setFont(var2);
      }

      if (!((Component)this.target).isEnabled()) {
         this.disable();
      }

      Rectangle var3 = ((Component)this.target).getBounds();
      this.setBounds(var3.x, var3.y, var3.width, var3.height, 3);
   }

   void handleRepaint(int var1, int var2, int var3, int var4) {
   }

   void handleExpose(int var1, int var2, int var3, int var4) {
      this.postPaintIfNecessary(var1, var2, var3, var4);
   }

   public void handlePaint(int var1, int var2, int var3, int var4) {
      this.postPaintIfNecessary(var1, var2, var3, var4);
   }

   private void postPaintIfNecessary(int var1, int var2, int var3, int var4) {
      if (!AWTAccessor.getComponentAccessor().getIgnoreRepaint((Component)this.target)) {
         PaintEvent var5 = PaintEventDispatcher.getPaintEventDispatcher().createPaintEvent((Component)this.target, var1, var2, var3, var4);
         if (var5 != null) {
            this.postEvent(var5);
         }
      }

   }

   void postEvent(AWTEvent var1) {
      this.preprocessPostEvent(var1);
      com.frojasg1.sun.awt.windows.WToolkit.postEvent(WToolkit.targetToAppContext(this.target), var1);
   }

   void preprocessPostEvent(AWTEvent var1) {
   }

   public void beginLayout() {
      this.isLayouting = true;
   }

   public void endLayout() {
      if (!this.paintArea.isEmpty() && !this.paintPending && !((Component)this.target).getIgnoreRepaint()) {
         this.postEvent(new PaintEvent((Component)this.target, 800, new Rectangle()));
      }

      this.isLayouting = false;
   }

   public native void beginValidate();

   public native void endValidate();

   public Dimension preferredSize() {
      return this.getPreferredSize();
   }

   public synchronized void addDropTarget(DropTarget var1) {
      if (this.nDropTargets == 0) {
         this.nativeDropTargetContext = this.addNativeDropTarget();
      }

      ++this.nDropTargets;
   }

   public synchronized void removeDropTarget(DropTarget var1) {
      --this.nDropTargets;
      if (this.nDropTargets == 0) {
         this.removeNativeDropTarget();
         this.nativeDropTargetContext = 0L;
      }

   }

   native long addNativeDropTarget();

   native void removeNativeDropTarget();

   native boolean nativeHandlesWheelScrolling();

   public boolean handlesWheelScrolling() {
      return this.nativeHandlesWheelScrolling();
   }

   public boolean isPaintPending() {
      return this.paintPending && this.isLayouting;
   }

   public void createBuffers(int var1, BufferCapabilities var2) throws AWTException {
      Win32GraphicsConfig var3 = (Win32GraphicsConfig)this.getGraphicsConfiguration();
      var3.assertOperationSupported((Component)this.target, var1, var2);

      try {
         this.replaceSurfaceData(var1 - 1, var2);
      } catch (InvalidPipeException var5) {
         throw new AWTException(var5.getMessage());
      }
   }

   public void destroyBuffers() {
      this.replaceSurfaceData(0, (BufferCapabilities)null);
   }

   public void flip(int var1, int var2, int var3, int var4, FlipContents var5) {
      VolatileImage var6 = this.backBuffer;
      if (var6 == null) {
         throw new IllegalStateException("Buffers have not been created");
      } else {
         Win32GraphicsConfig var7 = (Win32GraphicsConfig)this.getGraphicsConfiguration();
         var7.flip(this, (Component)this.target, var6, var1, var2, var3, var4, var5);
      }
   }

   public synchronized Image getBackBuffer() {
      VolatileImage var1 = this.backBuffer;
      if (var1 == null) {
         throw new IllegalStateException("Buffers have not been created");
      } else {
         return var1;
      }
   }

   public BufferCapabilities getBackBufferCaps() {
      return this.backBufferCaps;
   }

   public int getBackBuffersNum() {
      return this.numBackBuffers;
   }

   public boolean shouldClearRectBeforePaint() {
      return true;
   }

   native void pSetParent(ComponentPeer var1);

   public void reparent(ContainerPeer var1) {
      this.pSetParent(var1);
   }

   public boolean isReparentSupported() {
      return true;
   }

   public void setBoundsOperation(int var1) {
   }

   public boolean isAccelCapable() {
      if (this.isAccelCapable && isContainingTopLevelAccelCapable((Component)this.target)) {
         boolean var1 = SunToolkit.isContainingTopLevelTranslucent((Component)this.target);
         return !var1 || Win32GraphicsEnvironment.isVistaOS();
      } else {
         return false;
      }
   }

   public void disableAcceleration() {
      this.isAccelCapable = false;
   }

   native void setRectangularShape(int var1, int var2, int var3, int var4, Region var5);

   private static final boolean isContainingTopLevelAccelCapable(Component var0) {
      while(var0 != null && !(var0 instanceof WEmbeddedFrame)) {
         var0 = ((Component)var0).getParent();
      }

      if (var0 == null) {
         return true;
      } else {
         return ((WEmbeddedFramePeer)((Component)var0).getPeer()).isAccelCapable();
      }
   }
/*
   public void applyShape(Region var1) {
      if (shapeLog.isLoggable(PlatformLogger.Level.FINER)) {
         shapeLog.finer("*** INFO: Setting shape: PEER: " + this + "; TARGET: " + this.target + "; SHAPE: " + var1);
      }

      if (var1 != null) {
         this.setRectangularShape(var1.getLoX(), var1.getLoY(), var1.getHiX(), var1.getHiY(), var1.isRectangular() ? null : var1);
      } else {
         this.setRectangularShape(0, 0, 0, 0, (Region)null);
      }

   }
*/
   public void setZOrder(ComponentPeer var1) {
      long var2 = var1 != null ? ((WComponentPeer)var1).getHWnd() : 0L;
      this.setZOrder(var2);
   }

   private native void setZOrder(long var1);

   public boolean isLightweightFramePeer() {
      return false;
   }
}
