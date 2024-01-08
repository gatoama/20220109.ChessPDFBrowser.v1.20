package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.java2d.loops.DrawGlyphList;
import com.frojasg1.sun.java2d.loops.DrawGlyphListAA;
import com.frojasg1.sun.java2d.loops.DrawGlyphListLCD;
import com.frojasg1.sun.java2d.loops.DrawLine;
import com.frojasg1.sun.java2d.loops.DrawParallelogram;
import com.frojasg1.sun.java2d.loops.DrawPath;
import com.frojasg1.sun.java2d.loops.DrawPolygons;
import com.frojasg1.sun.java2d.loops.DrawRect;
import com.frojasg1.sun.java2d.loops.FillParallelogram;
import com.frojasg1.sun.java2d.loops.FillPath;
import com.frojasg1.sun.java2d.loops.FillRect;
import com.frojasg1.sun.java2d.loops.FillSpans;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;

public class RenderLoops {
   public static final int primTypeID = GraphicsPrimitive.makePrimTypeID();
   public DrawLine drawLineLoop;
   public FillRect fillRectLoop;
   public DrawRect drawRectLoop;
   public DrawPolygons drawPolygonsLoop;
   public DrawPath drawPathLoop;
   public FillPath fillPathLoop;
   public FillSpans fillSpansLoop;
   public FillParallelogram fillParallelogramLoop;
   public DrawParallelogram drawParallelogramLoop;
   public DrawGlyphList drawGlyphListLoop;
   public DrawGlyphListAA drawGlyphListAALoop;
   public DrawGlyphListLCD drawGlyphListLCDLoop;

   public RenderLoops() {
   }
}
