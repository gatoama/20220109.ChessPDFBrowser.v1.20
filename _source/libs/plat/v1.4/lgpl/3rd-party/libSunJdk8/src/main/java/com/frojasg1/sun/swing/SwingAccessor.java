package com.frojasg1.sun.swing;

import java.awt.Point;
import javax.swing.RepaintManager;
import javax.swing.TransferHandler.DropLocation;
import javax.swing.text.JTextComponent;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.swing.JLightweightFrame;
import com.frojasg1.sun.swing.SwingUtilities2;

public final class SwingAccessor {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static SwingAccessor.JTextComponentAccessor jtextComponentAccessor;
   private static SwingAccessor.JLightweightFrameAccessor jLightweightFrameAccessor;
   private static SwingAccessor.RepaintManagerAccessor repaintManagerAccessor;

   private SwingAccessor() {
   }

   public static void setJTextComponentAccessor(SwingAccessor.JTextComponentAccessor var0) {
      jtextComponentAccessor = var0;
   }

   public static SwingAccessor.JTextComponentAccessor getJTextComponentAccessor() {
      if (jtextComponentAccessor == null) {
         unsafe.ensureClassInitialized(JTextComponent.class);
      }

      return jtextComponentAccessor;
   }

   public static void setJLightweightFrameAccessor(SwingAccessor.JLightweightFrameAccessor var0) {
      jLightweightFrameAccessor = var0;
   }

   public static SwingAccessor.JLightweightFrameAccessor getJLightweightFrameAccessor() {
      if (jLightweightFrameAccessor == null) {
         unsafe.ensureClassInitialized(com.frojasg1.sun.swing.JLightweightFrame.class);
      }

      return jLightweightFrameAccessor;
   }

   public static void setRepaintManagerAccessor(SwingAccessor.RepaintManagerAccessor var0) {
      repaintManagerAccessor = var0;
   }

   public static SwingAccessor.RepaintManagerAccessor getRepaintManagerAccessor() {
      if (repaintManagerAccessor == null) {
         unsafe.ensureClassInitialized(RepaintManager.class);
      }

      return repaintManagerAccessor;
   }

   public interface JLightweightFrameAccessor {
      void updateCursor(JLightweightFrame var1);
   }

   public interface JTextComponentAccessor {
      DropLocation dropLocationForPoint(JTextComponent var1, Point var2);

      Object setDropLocation(JTextComponent var1, DropLocation var2, Object var3, boolean var4);
   }

   public interface RepaintManagerAccessor {
      void addRepaintListener(RepaintManager var1, com.frojasg1.sun.swing.SwingUtilities2.RepaintListener var2);

      void removeRepaintListener(RepaintManager var1, SwingUtilities2.RepaintListener var2);
   }
}
