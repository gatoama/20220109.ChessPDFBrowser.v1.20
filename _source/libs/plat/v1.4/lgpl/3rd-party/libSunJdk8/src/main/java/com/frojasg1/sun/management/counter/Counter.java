package com.frojasg1.sun.management.counter;

import com.frojasg1.sun.management.counter.Units;
import com.frojasg1.sun.management.counter.Variability;

import java.io.Serializable;

public interface Counter extends Serializable {
   String getName();

   Units getUnits();

   Variability getVariability();

   boolean isVector();

   int getVectorLength();

   Object getValue();

   boolean isInternal();

   int getFlags();
}
