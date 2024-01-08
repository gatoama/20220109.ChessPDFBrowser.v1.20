package com.frojasg1.sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.JobAttributes;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PageAttributes;
import java.awt.Panel;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.image.ColorModel;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.swing.text.JTextComponent;
import com.frojasg1.sun.awt.AWTAccessor;
import com.frojasg1.sun.awt.AWTAutoShutdown;
import com.frojasg1.sun.awt.AppContext;
import com.frojasg1.sun.awt.LightweightFrame;
import com.frojasg1.sun.awt.SunToolkit;
import com.frojasg1.sun.awt.Win32GraphicsDevice;
import com.frojasg1.sun.awt.Win32GraphicsEnvironment;
import com.frojasg1.sun.awt.datatransfer.DataTransferer;
import com.frojasg1.sun.awt.windows.ThemeReader;
import com.frojasg1.sun.awt.windows.WButtonPeer;
import com.frojasg1.sun.awt.windows.WCanvasPeer;
import com.frojasg1.sun.awt.windows.WCheckboxMenuItemPeer;
import com.frojasg1.sun.awt.windows.WCheckboxPeer;
import com.frojasg1.sun.awt.windows.WChoicePeer;
import com.frojasg1.sun.awt.windows.WClipboard;
import com.frojasg1.sun.awt.windows.WCustomCursor;
import com.frojasg1.sun.awt.windows.WDataTransferer;
import com.frojasg1.sun.awt.windows.WDesktopPeer;
import com.frojasg1.sun.awt.windows.WDesktopProperties;
import com.frojasg1.sun.awt.windows.WDialogPeer;
import com.frojasg1.sun.awt.windows.WDragSourceContextPeer;
import com.frojasg1.sun.awt.windows.WEmbeddedFrame;
import com.frojasg1.sun.awt.windows.WEmbeddedFramePeer;
import com.frojasg1.sun.awt.windows.WFileDialogPeer;
import com.frojasg1.sun.awt.windows.WFontMetrics;
import com.frojasg1.sun.awt.windows.WFontPeer;
import com.frojasg1.sun.awt.windows.WFramePeer;
import com.frojasg1.sun.awt.windows.WInputMethod;
import com.frojasg1.sun.awt.windows.WInputMethodDescriptor;
import com.frojasg1.sun.awt.windows.WKeyboardFocusManagerPeer;
import com.frojasg1.sun.awt.windows.WLabelPeer;
import com.frojasg1.sun.awt.windows.WLightweightFramePeer;
import com.frojasg1.sun.awt.windows.WListPeer;
import com.frojasg1.sun.awt.windows.WMenuBarPeer;
import com.frojasg1.sun.awt.windows.WMenuItemPeer;
import com.frojasg1.sun.awt.windows.WMenuPeer;
import com.frojasg1.sun.awt.windows.WMouseDragGestureRecognizer;
import com.frojasg1.sun.awt.windows.WPageDialog;
import com.frojasg1.sun.awt.windows.WPageDialogPeer;
import com.frojasg1.sun.awt.windows.WPanelPeer;
import com.frojasg1.sun.awt.windows.WPopupMenuPeer;
import com.frojasg1.sun.awt.windows.WPrintDialog;
import com.frojasg1.sun.awt.windows.WPrintDialogPeer;
import com.frojasg1.sun.awt.windows.WRobotPeer;
import com.frojasg1.sun.awt.windows.WScrollPanePeer;
import com.frojasg1.sun.awt.windows.WScrollbarPeer;
import com.frojasg1.sun.awt.windows.WSystemTrayPeer;
import com.frojasg1.sun.awt.windows.WTextAreaPeer;
import com.frojasg1.sun.awt.windows.WTextFieldPeer;
import com.frojasg1.sun.awt.windows.WTrayIconPeer;
import com.frojasg1.sun.awt.windows.WWindowPeer;
import com.frojasg1.sun.font.FontManager;
import com.frojasg1.sun.font.FontManagerFactory;
import com.frojasg1.sun.font.SunFontManager;
import com.frojasg1.sun.java2d.Disposer;
import com.frojasg1.sun.java2d.DisposerRecord;
import com.frojasg1.sun.java2d.d3d.D3DRenderQueue;
import com.frojasg1.sun.java2d.opengl.OGLRenderQueue;
import com.frojasg1.sun.misc.PerformanceLogger;
import com.frojasg1.sun.misc.ThreadGroupUtils;
import com.frojasg1.sun.print.PrintJob2D;
import com.frojasg1.sun.security.util.SecurityConstants;
import com.frojasg1.sun.util.logging.PlatformLogger;

public final class WToolkit extends SunToolkit implements Runnable {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WToolkit");
   public static final String XPSTYLE_THEME_ACTIVE = "win.xpstyle.themeActive";
   static GraphicsConfiguration config;
   com.frojasg1.sun.awt.windows.WClipboard clipboard;
   private Hashtable<String, FontPeer> cacheFontPeer;
   private com.frojasg1.sun.awt.windows.WDesktopProperties wprops;
   protected boolean dynamicLayoutSetting = false;
   private static boolean areExtraMouseButtonsEnabled = true;
   private static boolean loaded = false;
   private final Object anchor = new Object();
   private boolean inited = false;
   static ColorModel screenmodel;
   private static final String prefix = "DnD.Cursor.";
   private static final String postfix = ".32x32";
   private static final String awtPrefix = "awt.";
   private static final String dndPrefix = "DnD.";
   private static final WeakReference<Component> NULL_COMPONENT_WR;
   private volatile WeakReference<Component> compOnTouchDownEvent;
   private volatile WeakReference<Component> compOnMousePressedEvent;

   private static native void initIDs();

   public static void loadLibraries() {
      if (!loaded) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               System.loadLibrary("awt");
               return null;
            }
         });
         loaded = true;
      }

   }

   private static native String getWindowsVersion();

   private static native void disableCustomPalette();

   public static void resetGC() {
      if (GraphicsEnvironment.isHeadless()) {
         config = null;
      } else {
         config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      }

   }

   public static native boolean embeddedInit();

   public static native boolean embeddedDispose();

   public native void embeddedEventLoopIdleProcessing();

   private static native void postDispose();

   private static native boolean startToolkitThread(Runnable var0, ThreadGroup var1);

   public WToolkit() {
      this.compOnTouchDownEvent = NULL_COMPONENT_WR;
      this.compOnMousePressedEvent = NULL_COMPONENT_WR;
      if (PerformanceLogger.loggingEnabled()) {
         PerformanceLogger.setTime("WToolkit construction");
      }

      Disposer.addRecord(this.anchor, new WToolkit.ToolkitDisposer());
      AWTAutoShutdown.notifyToolkitThreadBusy();
      ThreadGroup var1 = (ThreadGroup)AccessController.doPrivileged((PrivilegedAction)ThreadGroupUtils::getRootThreadGroup);
      if (!startToolkitThread(this, var1)) {
         Thread var2 = new Thread(var1, this, "AWT-Windows");
         var2.setDaemon(true);
         var2.start();
      }

      try {
         synchronized(this) {
            while(!this.inited) {
               this.wait();
            }
         }
      } catch (InterruptedException var5) {
      }

      this.setDynamicLayout(true);
      areExtraMouseButtonsEnabled = Boolean.parseBoolean(System.getProperty("sun.awt.enableExtraMouseButtons", "true"));
      System.setProperty("sun.awt.enableExtraMouseButtons", "" + areExtraMouseButtonsEnabled);
      setExtraMouseButtonsEnabledNative(areExtraMouseButtonsEnabled);
   }

   private final void registerShutdownHook() {
      AccessController.doPrivileged((PrivilegedAction)() -> {
         Thread var1 = new Thread(ThreadGroupUtils.getRootThreadGroup(), this::shutdown);
         var1.setContextClassLoader((ClassLoader)null);
         Runtime.getRuntime().addShutdownHook(var1);
         return null;
      });
   }

   public void run() {
      AccessController.doPrivileged((PrivilegedAction)() -> {
         Thread.currentThread().setContextClassLoader((ClassLoader)null);
         return null;
      });
      Thread.currentThread().setPriority(6);
      boolean var1 = this.init();
      if (var1) {
         this.registerShutdownHook();
      }

      synchronized(this) {
         this.inited = true;
         this.notifyAll();
      }

      if (var1) {
         this.eventLoop();
      }

   }

   private native boolean init();

   private native void eventLoop();

   private native void shutdown();

   static native void startSecondaryEventLoop();

   static native void quitSecondaryEventLoop();

   public ButtonPeer createButton(Button var1) {
      com.frojasg1.sun.awt.windows.WButtonPeer var2 = null;//new com.frojasg1.sun.awt.windows.WButtonPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public TextFieldPeer createTextField(TextField var1) {
      com.frojasg1.sun.awt.windows.WTextFieldPeer var2 = null;//new com.frojasg1.sun.awt.windows.WTextFieldPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public LabelPeer createLabel(Label var1) {
      com.frojasg1.sun.awt.windows.WLabelPeer var2 = null;//new com.frojasg1.sun.awt.windows.WLabelPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public ListPeer createList(List var1) {
      com.frojasg1.sun.awt.windows.WListPeer var2 = null;//new com.frojasg1.sun.awt.windows.WListPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public CheckboxPeer createCheckbox(Checkbox var1) {
      com.frojasg1.sun.awt.windows.WCheckboxPeer var2 = null;//new com.frojasg1.sun.awt.windows.WCheckboxPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public ScrollbarPeer createScrollbar(Scrollbar var1) {
      com.frojasg1.sun.awt.windows.WScrollbarPeer var2 = null;//new com.frojasg1.sun.awt.windows.WScrollbarPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public ScrollPanePeer createScrollPane(ScrollPane var1) {
      com.frojasg1.sun.awt.windows.WScrollPanePeer var2 = null;//new com.frojasg1.sun.awt.windows.WScrollPanePeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public TextAreaPeer createTextArea(TextArea var1) {
      com.frojasg1.sun.awt.windows.WTextAreaPeer var2 = null;//new com.frojasg1.sun.awt.windows.WTextAreaPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public ChoicePeer createChoice(Choice var1) {
      com.frojasg1.sun.awt.windows.WChoicePeer var2 = null;//new com.frojasg1.sun.awt.windows.WChoicePeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public FramePeer createFrame(Frame var1) {
      com.frojasg1.sun.awt.windows.WFramePeer var2 = null;//new com.frojasg1.sun.awt.windows.WFramePeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public FramePeer createLightweightFrame(LightweightFrame var1) {
      com.frojasg1.sun.awt.windows.WLightweightFramePeer var2 = null;//new WLightweightFramePeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public CanvasPeer createCanvas(Canvas var1) {
      com.frojasg1.sun.awt.windows.WCanvasPeer var2 = null;//new com.frojasg1.sun.awt.windows.WCanvasPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public void disableBackgroundErase(Canvas var1) {
      com.frojasg1.sun.awt.windows.WCanvasPeer var2 = null;//(com.frojasg1.sun.awt.windows.WCanvasPeer)var1.getPeer();
      if (var2 == null) {
         throw new IllegalStateException("Canvas must have a valid peer");
      } else {
         var2.disableBackgroundErase();
      }
   }

   public PanelPeer createPanel(Panel var1) {
      com.frojasg1.sun.awt.windows.WPanelPeer var2 = null;//new com.frojasg1.sun.awt.windows.WPanelPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public WindowPeer createWindow(Window var1) {
      com.frojasg1.sun.awt.windows.WWindowPeer var2 = null;//new com.frojasg1.sun.awt.windows.WWindowPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public DialogPeer createDialog(Dialog var1) {
      com.frojasg1.sun.awt.windows.WDialogPeer var2 = null;//new com.frojasg1.sun.awt.windows.WDialogPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public FileDialogPeer createFileDialog(FileDialog var1) {
      com.frojasg1.sun.awt.windows.WFileDialogPeer var2 = null;//new com.frojasg1.sun.awt.windows.WFileDialogPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public MenuBarPeer createMenuBar(MenuBar var1) {
      com.frojasg1.sun.awt.windows.WMenuBarPeer var2 = new com.frojasg1.sun.awt.windows.WMenuBarPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public MenuPeer createMenu(Menu var1) {
      com.frojasg1.sun.awt.windows.WMenuPeer var2 = new com.frojasg1.sun.awt.windows.WMenuPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public PopupMenuPeer createPopupMenu(PopupMenu var1) {
      com.frojasg1.sun.awt.windows.WPopupMenuPeer var2 = new com.frojasg1.sun.awt.windows.WPopupMenuPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public MenuItemPeer createMenuItem(MenuItem var1) {
      com.frojasg1.sun.awt.windows.WMenuItemPeer var2 = new com.frojasg1.sun.awt.windows.WMenuItemPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem var1) {
      com.frojasg1.sun.awt.windows.WCheckboxMenuItemPeer var2 = new com.frojasg1.sun.awt.windows.WCheckboxMenuItemPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public RobotPeer createRobot(Robot var1, GraphicsDevice var2) {
      return new com.frojasg1.sun.awt.windows.WRobotPeer(var2);
   }

   public com.frojasg1.sun.awt.windows.WEmbeddedFramePeer createEmbeddedFrame(WEmbeddedFrame var1) {
      com.frojasg1.sun.awt.windows.WEmbeddedFramePeer var2 = null;//new WEmbeddedFramePeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   com.frojasg1.sun.awt.windows.WPrintDialogPeer createWPrintDialog(com.frojasg1.sun.awt.windows.WPrintDialog var1) {
      com.frojasg1.sun.awt.windows.WPrintDialogPeer var2 = null;//new com.frojasg1.sun.awt.windows.WPrintDialogPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   com.frojasg1.sun.awt.windows.WPageDialogPeer createWPageDialog(com.frojasg1.sun.awt.windows.WPageDialog var1) {
      com.frojasg1.sun.awt.windows.WPageDialogPeer var2 = null;//new com.frojasg1.sun.awt.windows.WPageDialogPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public TrayIconPeer createTrayIcon(TrayIcon var1) {
      com.frojasg1.sun.awt.windows.WTrayIconPeer var2 = null;//new com.frojasg1.sun.awt.windows.WTrayIconPeer(var1);
      targetCreatedPeer(var1, var2);
      return var2;
   }

   public SystemTrayPeer createSystemTray(SystemTray var1) {
      return new com.frojasg1.sun.awt.windows.WSystemTrayPeer(var1);
   }

   public boolean isTraySupported() {
      return true;
   }

   public DataTransferer getDataTransferer() {
      return com.frojasg1.sun.awt.windows.WDataTransferer.getInstanceImpl();
   }

   public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() throws HeadlessException {
      return com.frojasg1.sun.awt.windows.WKeyboardFocusManagerPeer.getInstance();
   }

   private native void setDynamicLayoutNative(boolean var1);

   public void setDynamicLayout(boolean var1) {
      if (var1 != this.dynamicLayoutSetting) {
         this.dynamicLayoutSetting = var1;
         this.setDynamicLayoutNative(var1);
      }
   }

   protected boolean isDynamicLayoutSet() {
      return this.dynamicLayoutSetting;
   }

   private native boolean isDynamicLayoutSupportedNative();

   public boolean isDynamicLayoutActive() {
      return this.isDynamicLayoutSet() && this.isDynamicLayoutSupported();
   }

   public boolean isFrameStateSupported(int var1) {
      switch(var1) {
      case 0:
      case 1:
      case 6:
         return true;
      default:
         return false;
      }
   }

   static native ColorModel makeColorModel();

   static ColorModel getStaticColorModel() {
      if (GraphicsEnvironment.isHeadless()) {
         throw new IllegalArgumentException();
      } else {
         if (config == null) {
            resetGC();
         }

         return config.getColorModel();
      }
   }

   public ColorModel getColorModel() {
      return getStaticColorModel();
   }

   public Insets getScreenInsets(GraphicsConfiguration var1) {
      return this.getScreenInsets(((Win32GraphicsDevice)var1.getDevice()).getScreen());
   }

   public int getScreenResolution() {
      Win32GraphicsEnvironment var1 = (Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
      return var1.getXResolution();
   }

   protected native int getScreenWidth();

   protected native int getScreenHeight();

   private native Insets getScreenInsets(int var1);

   public FontMetrics getFontMetrics(Font var1) {
      FontManager var2 = FontManagerFactory.getInstance();
      return var2 instanceof SunFontManager && ((SunFontManager)var2).usePlatformFontMetrics() ? com.frojasg1.sun.awt.windows.WFontMetrics.getFontMetrics(var1) : super.getFontMetrics(var1);
   }

   public FontPeer getFontPeer(String var1, int var2) {
      FontPeer var3 = null;
      String var4 = var1.toLowerCase();
      if (null != this.cacheFontPeer) {
         var3 = (FontPeer)this.cacheFontPeer.get(var4 + var2);
         if (null != var3) {
            return var3;
         }
      }

      com.frojasg1.sun.awt.windows.WFontPeer var5 = new com.frojasg1.sun.awt.windows.WFontPeer(var1, var2);
      if (var5 != null) {
         if (null == this.cacheFontPeer) {
            this.cacheFontPeer = new Hashtable(5, 0.9F);
         }

         if (null != this.cacheFontPeer) {
            this.cacheFontPeer.put(var4 + var2, var5);
         }
      }

      return var5;
   }

   private native void nativeSync();

   public void sync() {
      this.nativeSync();
      OGLRenderQueue.sync();
      D3DRenderQueue.sync();
   }

   public PrintJob getPrintJob(Frame var1, String var2, Properties var3) {
      return this.getPrintJob(var1, var2, (JobAttributes)null, (PageAttributes)null);
   }

   public PrintJob getPrintJob(Frame var1, String var2, JobAttributes var3, PageAttributes var4) {
      if (var1 == null) {
         throw new NullPointerException("frame must not be null");
      } else {
         PrintJob2D var5 = new PrintJob2D(var1, var2, var3, var4);
         if (!var5.printDialog()) {
            var5 = null;
         }

         return var5;
      }
   }

   public native void beep();

   public boolean getLockingKeyState(int var1) {
      if (var1 != 20 && var1 != 144 && var1 != 145 && var1 != 262) {
         throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState");
      } else {
         return this.getLockingKeyStateNative(var1);
      }
   }

   private native boolean getLockingKeyStateNative(int var1);

   public void setLockingKeyState(int var1, boolean var2) {
      if (var1 != 20 && var1 != 144 && var1 != 145 && var1 != 262) {
         throw new IllegalArgumentException("invalid key for Toolkit.setLockingKeyState");
      } else {
         this.setLockingKeyStateNative(var1, var2);
      }
   }

   private native void setLockingKeyStateNative(int var1, boolean var2);

   public Clipboard getSystemClipboard() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
      }

      synchronized(this) {
         if (this.clipboard == null) {
            this.clipboard = new com.frojasg1.sun.awt.windows.WClipboard();
         }
      }

      return this.clipboard;
   }

   protected native void loadSystemColors(int[] var1);

   public static final Object targetToPeer(Object var0) {
      return SunToolkit.targetToPeer(var0);
   }

   public static final void targetDisposedPeer(Object var0, Object var1) {
      SunToolkit.targetDisposedPeer(var0, var1);
   }

   public InputMethodDescriptor getInputMethodAdapterDescriptor() {
      return new com.frojasg1.sun.awt.windows.WInputMethodDescriptor();
   }

   public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight var1) {
      return com.frojasg1.sun.awt.windows.WInputMethod.mapInputMethodHighlight(var1);
   }

   public boolean enableInputMethodsForTextComponent() {
      return true;
   }

   public Locale getDefaultKeyboardLocale() {
      Locale var1 = com.frojasg1.sun.awt.windows.WInputMethod.getNativeLocale();
      return var1 == null ? super.getDefaultKeyboardLocale() : var1;
   }

   public Cursor createCustomCursor(Image var1, Point var2, String var3) throws IndexOutOfBoundsException {
      return new com.frojasg1.sun.awt.windows.WCustomCursor(var1, var2, var3);
   }

   public Dimension getBestCursorSize(int var1, int var2) {
      return new Dimension(com.frojasg1.sun.awt.windows.WCustomCursor.getCursorWidth(), com.frojasg1.sun.awt.windows.WCustomCursor.getCursorHeight());
   }

   public native int getMaximumCursorColors();

   static void paletteChanged() {
      ((Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment()).paletteChanged();
   }

   public static void displayChanged() {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            ((Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged();
         }
      });
   }

   public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var1) throws InvalidDnDOperationException {
      LightweightFrame var2 = SunToolkit.getLightweightFrame(var1.getComponent());
      return (DragSourceContextPeer)(var2 != null ? var2.createDragSourceContextPeer(var1) : com.frojasg1.sun.awt.windows.WDragSourceContextPeer.createDragSourceContextPeer(var1));
   }

   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> var1, DragSource var2, Component var3, int var4, DragGestureListener var5) {
      LightweightFrame var6 = SunToolkit.getLightweightFrame(var3);
      if (var6 != null) {
         return var6.createDragGestureRecognizer(var1, var2, var3, var4, var5);
      } else {
         return MouseDragGestureRecognizer.class.equals(var1) ? (T) new com.frojasg1.sun.awt.windows.WMouseDragGestureRecognizer(var2, var3, var4, var5) : null;
      }
   }

   protected Object lazilyLoadDesktopProperty(String var1) {
      if (var1.startsWith("DnD.Cursor.")) {
         String var2 = var1.substring("DnD.Cursor.".length(), var1.length()) + ".32x32";

         try {
            return Cursor.getSystemCustomCursor(var2);
         } catch (AWTException var5) {
            throw new RuntimeException("cannot load system cursor: " + var2, var5);
         }
      } else if (var1.equals("awt.dynamicLayoutSupported")) {
         return this.isDynamicLayoutSupported();
      } else if (!com.frojasg1.sun.awt.windows.WDesktopProperties.isWindowsProperty(var1) && !var1.startsWith("awt.") && !var1.startsWith("DnD.")) {
         return super.lazilyLoadDesktopProperty(var1);
      } else {
         synchronized(this) {
            this.lazilyInitWProps();
            return this.desktopProperties.get(var1);
         }
      }
   }

   private synchronized void lazilyInitWProps() {
      if (this.wprops == null) {
         this.wprops = new com.frojasg1.sun.awt.windows.WDesktopProperties(this);
         this.updateProperties(this.wprops.getProperties());
      }

   }

   private synchronized boolean isDynamicLayoutSupported() {
      boolean var1 = this.isDynamicLayoutSupportedNative();
      this.lazilyInitWProps();
      Boolean var2 = (Boolean)this.desktopProperties.get("awt.dynamicLayoutSupported");
      if (log.isLoggable(PlatformLogger.Level.FINER)) {
         log.finer("In WTK.isDynamicLayoutSupported()   nativeDynamic == " + var1 + "   wprops.dynamic == " + var2);
      }

      if (var2 != null && var1 == var2) {
         return var2;
      } else {
         this.windowsSettingChange();
         return var1;
      }
   }

   private void windowsSettingChange() {
      Map var1 = this.getWProps();
      if (var1 != null) {
         this.updateXPStyleEnabled(var1.get("win.xpstyle.themeActive"));
         if (AppContext.getAppContext() == null) {
            this.updateProperties(var1);
         } else {
            EventQueue.invokeLater(() -> {
               this.updateProperties(var1);
            });
         }

      }
   }

   private synchronized void updateProperties(Map<String, Object> var1) {
      if (null != var1) {
         this.updateXPStyleEnabled(var1.get("win.xpstyle.themeActive"));

         String var3;
         Object var4;
         for(Iterator var2 = var1.keySet().iterator(); var2.hasNext(); this.setDesktopProperty(var3, var4)) {
            var3 = (String)var2.next();
            var4 = var1.get(var3);
            if (log.isLoggable(PlatformLogger.Level.FINER)) {
               log.finer("changed " + var3 + " to " + var4);
            }
         }

      }
   }

   private synchronized Map<String, Object> getWProps() {
      return this.wprops != null ? this.wprops.getProperties() : null;
   }

   private void updateXPStyleEnabled(Object var1) {
      ThemeReader.xpStyleEnabled = Boolean.TRUE.equals(var1);
   }

   public synchronized void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      if (var1 != null) {
         if (com.frojasg1.sun.awt.windows.WDesktopProperties.isWindowsProperty(var1) || var1.startsWith("awt.") || var1.startsWith("DnD.")) {
            this.lazilyInitWProps();
         }

         super.addPropertyChangeListener(var1, var2);
      }
   }

   protected synchronized void initializeDesktopProperties() {
      this.desktopProperties.put("DnD.Autoscroll.initialDelay", 50);
      this.desktopProperties.put("DnD.Autoscroll.interval", 50);
      this.desktopProperties.put("DnD.isDragImageSupported", Boolean.TRUE);
      this.desktopProperties.put("Shell.shellFolderManager", "sun.awt.shell.Win32ShellFolderManager2");
   }

   protected synchronized RenderingHints getDesktopAAHints() {
      return this.wprops == null ? null : this.wprops.getDesktopAAHints();
   }

   public boolean isModalityTypeSupported(ModalityType var1) {
      return var1 == null || var1 == ModalityType.MODELESS || var1 == ModalityType.DOCUMENT_MODAL || var1 == ModalityType.APPLICATION_MODAL || var1 == ModalityType.TOOLKIT_MODAL;
   }

   public boolean isModalExclusionTypeSupported(ModalExclusionType var1) {
      return var1 == null || var1 == ModalExclusionType.NO_EXCLUDE || var1 == ModalExclusionType.APPLICATION_EXCLUDE || var1 == ModalExclusionType.TOOLKIT_EXCLUDE;
   }

   public static WToolkit getWToolkit() {
      WToolkit var0 = (WToolkit)Toolkit.getDefaultToolkit();
      return var0;
   }

   public boolean useBufferPerWindow() {
      return !Win32GraphicsEnvironment.isDWMCompositionEnabled();
   }

   public void grab(Window var1) {
      if (var1.getPeer() != null) {
         ((com.frojasg1.sun.awt.windows.WWindowPeer)var1.getPeer()).grab();
      }

   }

   public void ungrab(Window var1) {
      if (var1.getPeer() != null) {
         ((WWindowPeer)var1.getPeer()).ungrab();
      }

   }

   private boolean isComponentValidForTouchKeyboard(Component var1) {
      return var1 != null && var1.isEnabled() && var1.isFocusable() && (var1 instanceof TextComponent && ((TextComponent)var1).isEditable() || var1 instanceof JTextComponent && ((JTextComponent)var1).isEditable());
   }

   public void showOrHideTouchKeyboard(Component var1, AWTEvent var2) {
      if (var1 instanceof TextComponent || var1 instanceof JTextComponent) {
         if (var2 instanceof MouseEvent && this.isComponentValidForTouchKeyboard(var1)) {
            MouseEvent var4 = (MouseEvent)var2;
            if (var4.getID() == 501) {
               if (AWTAccessor.getMouseEventAccessor().isCausedByTouchEvent(var4)) {
                  this.compOnTouchDownEvent = new WeakReference(var1);
               } else {
                  this.compOnMousePressedEvent = new WeakReference(var1);
               }
            } else if (var4.getID() == 502) {
               if (AWTAccessor.getMouseEventAccessor().isCausedByTouchEvent(var4)) {
                  if (this.compOnTouchDownEvent.get() == var1) {
                     this.showTouchKeyboard(true);
                  }

                  this.compOnTouchDownEvent = NULL_COMPONENT_WR;
               } else {
                  if (this.compOnMousePressedEvent.get() == var1) {
                     this.showTouchKeyboard(false);
                  }

                  this.compOnMousePressedEvent = NULL_COMPONENT_WR;
               }
            }
         } else if (var2 instanceof FocusEvent) {
            FocusEvent var3 = (FocusEvent)var2;
            if (var3.getID() == 1005 && !this.isComponentValidForTouchKeyboard(var3.getOppositeComponent())) {
               this.hideTouchKeyboard();
            }
         }

      }
   }

   private native void showTouchKeyboard(boolean var1);

   private native void hideTouchKeyboard();

   public native boolean syncNativeQueue(long var1);

   public boolean isDesktopSupported() {
      return true;
   }

   public DesktopPeer createDesktopPeer(Desktop var1) {
      return new com.frojasg1.sun.awt.windows.WDesktopPeer();
   }

   private static native void setExtraMouseButtonsEnabledNative(boolean var0);

   public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
      return areExtraMouseButtonsEnabled;
   }

   private synchronized native int getNumberOfButtonsImpl();

   public int getNumberOfButtons() {
      if (numberOfButtons == 0) {
         numberOfButtons = this.getNumberOfButtonsImpl();
      }

      return numberOfButtons > 20 ? 20 : numberOfButtons;
   }

   public boolean isWindowOpacitySupported() {
      return true;
   }

   public boolean isWindowShapingSupported() {
      return true;
   }

   public boolean isWindowTranslucencySupported() {
      return true;
   }

   public boolean isTranslucencyCapable(GraphicsConfiguration var1) {
      return true;
   }

   public boolean needUpdateWindow() {
      return true;
   }

   static {
      loadLibraries();
      initIDs();
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine("Win version: " + getWindowsVersion());
      }

      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            String var1 = System.getProperty("browser");
            if (var1 != null && var1.equals("sun.plugin")) {
               WToolkit.disableCustomPalette();
            }

            return null;
         }
      });
      NULL_COMPONENT_WR = new WeakReference((Object)null);
   }

   static class ToolkitDisposer implements DisposerRecord {
      ToolkitDisposer() {
      }

      public void dispose() {
         WToolkit.postDispose();
      }
   }
}
