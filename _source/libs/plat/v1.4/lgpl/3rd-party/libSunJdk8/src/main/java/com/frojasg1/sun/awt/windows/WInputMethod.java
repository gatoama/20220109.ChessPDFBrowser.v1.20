package com.frojasg1.sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InvocationEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodHighlight;
import java.awt.im.InputSubset;
import java.awt.im.spi.InputMethodContext;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.lang.Character.Subset;
import java.lang.Character.UnicodeBlock;
import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.frojasg1.sun.awt.im.InputMethodAdapter;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WInputMethodDescriptor;
import com.frojasg1.sun.awt.windows.WToolkit;

final class WInputMethod extends InputMethodAdapter {
   private InputMethodContext inputContext;
   private Component awtFocussedComponent;
   private com.frojasg1.sun.awt.windows.WComponentPeer awtFocussedComponentPeer = null;
   private com.frojasg1.sun.awt.windows.WComponentPeer lastFocussedComponentPeer = null;
   private boolean isLastFocussedActiveClient = false;
   private boolean isActive;
   private int context = this.createNativeContext();
   private boolean open;
   private int cmode;
   private Locale currentLocale;
   private boolean statusWindowHidden = false;
   public static final byte ATTR_INPUT = 0;
   public static final byte ATTR_TARGET_CONVERTED = 1;
   public static final byte ATTR_CONVERTED = 2;
   public static final byte ATTR_TARGET_NOTCONVERTED = 3;
   public static final byte ATTR_INPUT_ERROR = 4;
   public static final int IME_CMODE_ALPHANUMERIC = 0;
   public static final int IME_CMODE_NATIVE = 1;
   public static final int IME_CMODE_KATAKANA = 2;
   public static final int IME_CMODE_LANGUAGE = 3;
   public static final int IME_CMODE_FULLSHAPE = 8;
   public static final int IME_CMODE_HANJACONVERT = 64;
   public static final int IME_CMODE_ROMAN = 16;
   private static final boolean COMMIT_INPUT = true;
   private static final boolean DISCARD_INPUT = false;
   private static Map<TextAttribute, Object>[] highlightStyles;

   public WInputMethod() {
      this.cmode = this.getConversionStatus(this.context);
      this.open = this.getOpenStatus(this.context);
      this.currentLocale = getNativeLocale();
      if (this.currentLocale == null) {
         this.currentLocale = Locale.getDefault();
      }

   }

   protected void finalize() throws Throwable {
      if (this.context != 0) {
         this.destroyNativeContext(this.context);
         this.context = 0;
      }

      super.finalize();
   }

   public synchronized void setInputMethodContext(InputMethodContext var1) {
      this.inputContext = var1;
   }

   public final void dispose() {
   }

   public Object getControlObject() {
      return null;
   }

   public boolean setLocale(Locale var1) {
      return this.setLocale(var1, false);
   }

   private boolean setLocale(Locale var1, boolean var2) {
      Locale[] var3 = com.frojasg1.sun.awt.windows.WInputMethodDescriptor.getAvailableLocalesInternal();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         Locale var5 = var3[var4];
         if (var1.equals(var5) || var5.equals(Locale.JAPAN) && var1.equals(Locale.JAPANESE) || var5.equals(Locale.KOREA) && var1.equals(Locale.KOREAN)) {
            if (this.isActive) {
               setNativeLocale(var5.toLanguageTag(), var2);
            }

            this.currentLocale = var5;
            return true;
         }
      }

      return false;
   }

   public Locale getLocale() {
      if (this.isActive) {
         this.currentLocale = getNativeLocale();
         if (this.currentLocale == null) {
            this.currentLocale = Locale.getDefault();
         }
      }

      return this.currentLocale;
   }

   public void setCharacterSubsets(Subset[] var1) {
      if (var1 == null) {
         this.setConversionStatus(this.context, this.cmode);
         this.setOpenStatus(this.context, this.open);
      } else {
         Subset var2 = var1[0];
         Locale var3 = getNativeLocale();
         if (var3 != null) {
            byte var4;
            if (var3.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
               if (var2 != UnicodeBlock.BASIC_LATIN && var2 != InputSubset.LATIN_DIGITS) {
                  if (var2 != UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS && var2 != InputSubset.KANJI && var2 != UnicodeBlock.HIRAGANA) {
                     if (var2 == UnicodeBlock.KATAKANA) {
                        var4 = 11;
                     } else if (var2 == InputSubset.HALFWIDTH_KATAKANA) {
                        var4 = 3;
                     } else {
                        if (var2 != InputSubset.FULLWIDTH_LATIN) {
                           return;
                        }

                        var4 = 8;
                     }
                  } else {
                     var4 = 9;
                  }

                  this.setOpenStatus(this.context, true);
                  int var5 = var4 | this.getConversionStatus(this.context) & 16;
                  this.setConversionStatus(this.context, var5);
               } else {
                  this.setOpenStatus(this.context, false);
               }
            } else if (var3.getLanguage().equals(Locale.KOREAN.getLanguage())) {
               if (var2 != UnicodeBlock.BASIC_LATIN && var2 != InputSubset.LATIN_DIGITS) {
                  if (var2 != UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS && var2 != InputSubset.HANJA && var2 != UnicodeBlock.HANGUL_SYLLABLES && var2 != UnicodeBlock.HANGUL_JAMO && var2 != UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {
                     if (var2 != InputSubset.FULLWIDTH_LATIN) {
                        return;
                     }

                     var4 = 8;
                  } else {
                     var4 = 1;
                  }

                  this.setOpenStatus(this.context, true);
                  this.setConversionStatus(this.context, var4);
               } else {
                  this.setOpenStatus(this.context, false);
               }
            } else if (var3.getLanguage().equals(Locale.CHINESE.getLanguage())) {
               if (var2 != UnicodeBlock.BASIC_LATIN && var2 != InputSubset.LATIN_DIGITS) {
                  if (var2 != UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS && var2 != InputSubset.TRADITIONAL_HANZI && var2 != InputSubset.SIMPLIFIED_HANZI) {
                     if (var2 != InputSubset.FULLWIDTH_LATIN) {
                        return;
                     }

                     var4 = 8;
                  } else {
                     var4 = 1;
                  }

                  this.setOpenStatus(this.context, true);
                  this.setConversionStatus(this.context, var4);
               } else {
                  this.setOpenStatus(this.context, false);
               }
            }

         }
      }
   }

   public void dispatchEvent(AWTEvent var1) {
      if (var1 instanceof ComponentEvent) {
         Component var2 = ((ComponentEvent)var1).getComponent();
         if (var2 == this.awtFocussedComponent) {
            if (this.awtFocussedComponentPeer == null || this.awtFocussedComponentPeer.isDisposed()) {
               this.awtFocussedComponentPeer = this.getNearestNativePeer(var2);
            }

            if (this.awtFocussedComponentPeer != null) {
               this.handleNativeIMEEvent(this.awtFocussedComponentPeer, var1);
            }
         }
      }

   }

   public void activate() {
      boolean var1 = this.haveActiveClient();
      if (this.lastFocussedComponentPeer != this.awtFocussedComponentPeer || this.isLastFocussedActiveClient != var1) {
         if (this.lastFocussedComponentPeer != null) {
            this.disableNativeIME(this.lastFocussedComponentPeer);
         }

         if (this.awtFocussedComponentPeer != null) {
            this.enableNativeIME(this.awtFocussedComponentPeer, this.context, !var1);
         }

         this.lastFocussedComponentPeer = this.awtFocussedComponentPeer;
         this.isLastFocussedActiveClient = var1;
      }

      this.isActive = true;
      if (this.currentLocale != null) {
         this.setLocale(this.currentLocale, true);
      }

      if (this.statusWindowHidden) {
         this.setStatusWindowVisible(this.awtFocussedComponentPeer, true);
         this.statusWindowHidden = false;
      }

   }

   public void deactivate(boolean var1) {
      this.getLocale();
      if (this.awtFocussedComponentPeer != null) {
         this.lastFocussedComponentPeer = this.awtFocussedComponentPeer;
         this.isLastFocussedActiveClient = this.haveActiveClient();
      }

      this.isActive = false;
   }

   public void disableInputMethod() {
      if (this.lastFocussedComponentPeer != null) {
         this.disableNativeIME(this.lastFocussedComponentPeer);
         this.lastFocussedComponentPeer = null;
         this.isLastFocussedActiveClient = false;
      }

   }

   public String getNativeInputMethodInfo() {
      return this.getNativeIMMDescription();
   }

   protected void stopListening() {
      this.disableInputMethod();
   }

   protected void setAWTFocussedComponent(Component var1) {
      if (var1 != null) {
         com.frojasg1.sun.awt.windows.WComponentPeer var2 = this.getNearestNativePeer(var1);
         if (this.isActive) {
            if (this.awtFocussedComponentPeer != null) {
               this.disableNativeIME(this.awtFocussedComponentPeer);
            }

            if (var2 != null) {
               this.enableNativeIME(var2, this.context, !this.haveActiveClient());
            }
         }

         this.awtFocussedComponent = var1;
         this.awtFocussedComponentPeer = var2;
      }
   }

   public void hideWindows() {
      if (this.awtFocussedComponentPeer != null) {
         this.setStatusWindowVisible(this.awtFocussedComponentPeer, false);
         this.statusWindowHidden = true;
      }

   }

   public void removeNotify() {
      this.endCompositionNative(this.context, false);
      this.awtFocussedComponent = null;
      this.awtFocussedComponentPeer = null;
   }

   static Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight var0) {
      int var2 = var0.getState();
      int var1;
      if (var2 == 0) {
         var1 = 0;
      } else {
         if (var2 != 1) {
            return null;
         }

         var1 = 2;
      }

      if (var0.isSelected()) {
         ++var1;
      }

      return highlightStyles[var1];
   }

   protected boolean supportsBelowTheSpot() {
      return true;
   }

   public void endComposition() {
      this.endCompositionNative(this.context, this.haveActiveClient());
   }

   public void setCompositionEnabled(boolean var1) {
      this.setOpenStatus(this.context, var1);
   }

   public boolean isCompositionEnabled() {
      return this.getOpenStatus(this.context);
   }

   public void sendInputMethodEvent(int var1, long var2, String var4, int[] var5, String[] var6, int[] var7, byte[] var8, int var9, int var10, int var11) {
      AttributedCharacterIterator var12 = null;
      if (var4 != null) {
         AttributedString var13 = new AttributedString(var4);
         var13.addAttribute(Attribute.LANGUAGE, Locale.getDefault(), 0, var4.length());
         int var14;
         if (var5 != null && var6 != null && var6.length != 0 && var5.length == var6.length + 1 && var5[0] == 0 && var5[var6.length] <= var4.length()) {
            for(var14 = 0; var14 < var5.length - 1; ++var14) {
               var13.addAttribute(Attribute.INPUT_METHOD_SEGMENT, new Annotation((Object)null), var5[var14], var5[var14 + 1]);
               var13.addAttribute(Attribute.READING, new Annotation(var6[var14]), var5[var14], var5[var14 + 1]);
            }
         } else {
            var13.addAttribute(Attribute.INPUT_METHOD_SEGMENT, new Annotation((Object)null), 0, var4.length());
            var13.addAttribute(Attribute.READING, new Annotation(""), 0, var4.length());
         }

         if (var7 != null && var8 != null && var8.length != 0 && var7.length == var8.length + 1 && var7[0] == 0 && var7[var8.length] == var4.length()) {
            for(var14 = 0; var14 < var7.length - 1; ++var14) {
               InputMethodHighlight var15;
               switch(var8[var14]) {
               case 0:
               case 4:
               default:
                  var15 = InputMethodHighlight.UNSELECTED_RAW_TEXT_HIGHLIGHT;
                  break;
               case 1:
                  var15 = InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT;
                  break;
               case 2:
                  var15 = InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT;
                  break;
               case 3:
                  var15 = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
               }

               var13.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, var15, var7[var14], var7[var14 + 1]);
            }
         } else {
            var13.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT, 0, var4.length());
         }

         var12 = var13.getIterator();
      }

      Component var16 = this.getClientComponent();
      if (var16 != null) {
         InputMethodEvent var17 = new InputMethodEvent(var16, var1, var2, var12, var9, TextHitInfo.leading(var10), TextHitInfo.leading(var11));
         com.frojasg1.sun.awt.windows.WToolkit.postEvent(com.frojasg1.sun.awt.windows.WToolkit.targetToAppContext(var16), var17);
      }
   }

   public void inquireCandidatePosition() {
      Component var1 = this.getClientComponent();
      if (var1 != null) {
         Runnable var2 = new Runnable() {
            public void run() {
               int var1 = 0;
               int var2 = 0;
               Component var3 = WInputMethod.this.getClientComponent();
               if (var3 != null) {
                  if (!var3.isShowing()) {
                     return;
                  }

                  if (WInputMethod.this.haveActiveClient()) {
                     Rectangle var4 = WInputMethod.this.inputContext.getTextLocation(TextHitInfo.leading(0));
                     var1 = var4.x;
                     var2 = var4.y + var4.height;
                  } else {
                     Point var6 = var3.getLocationOnScreen();
                     Dimension var5 = var3.getSize();
                     var1 = var6.x;
                     var2 = var6.y + var5.height;
                  }
               }

               WInputMethod.this.openCandidateWindow(WInputMethod.this.awtFocussedComponentPeer, var1, var2);
            }
         };
         com.frojasg1.sun.awt.windows.WToolkit.postEvent(WToolkit.targetToAppContext(var1), new InvocationEvent(var1, var2));
      }
   }

   private com.frojasg1.sun.awt.windows.WComponentPeer getNearestNativePeer(Component var1) {
      if (var1 == null) {
         return null;
      } else {
         ComponentPeer var2 = ((Component)var1).getPeer();
         if (var2 == null) {
            return null;
         } else {
            do {
               if (!(var2 instanceof LightweightPeer)) {
                  if (var2 instanceof com.frojasg1.sun.awt.windows.WComponentPeer) {
                     return (com.frojasg1.sun.awt.windows.WComponentPeer)var2;
                  }

                  return null;
               }

               var1 = ((Component)var1).getParent();
               if (var1 == null) {
                  return null;
               }

               var2 = ((Component)var1).getPeer();
            } while(var2 != null);

            return null;
         }
      }
   }

   private native int createNativeContext();

   private native void destroyNativeContext(int var1);

   private native void enableNativeIME(com.frojasg1.sun.awt.windows.WComponentPeer var1, int var2, boolean var3);

   private native void disableNativeIME(com.frojasg1.sun.awt.windows.WComponentPeer var1);

   private native void handleNativeIMEEvent(com.frojasg1.sun.awt.windows.WComponentPeer var1, AWTEvent var2);

   private native void endCompositionNative(int var1, boolean var2);

   private native void setConversionStatus(int var1, int var2);

   private native int getConversionStatus(int var1);

   private native void setOpenStatus(int var1, boolean var2);

   private native boolean getOpenStatus(int var1);

   private native void setStatusWindowVisible(com.frojasg1.sun.awt.windows.WComponentPeer var1, boolean var2);

   private native String getNativeIMMDescription();

   static native Locale getNativeLocale();

   static native boolean setNativeLocale(String var0, boolean var1);

   private native void openCandidateWindow(WComponentPeer var1, int var2, int var3);

   static {
      Map[] var0 = new Map[4];
      HashMap var1 = new HashMap(1);
      var1.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
      var0[0] = Collections.unmodifiableMap(var1);
      var1 = new HashMap(1);
      var1.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
      var0[1] = Collections.unmodifiableMap(var1);
      var1 = new HashMap(1);
      var1.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
      var0[2] = Collections.unmodifiableMap(var1);
      var1 = new HashMap(4);
      Color var2 = new Color(0, 0, 128);
      var1.put(TextAttribute.FOREGROUND, var2);
      var1.put(TextAttribute.BACKGROUND, Color.white);
      var1.put(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
      var1.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
      var0[3] = Collections.unmodifiableMap(var1);
      highlightStyles = var0;
   }
}
