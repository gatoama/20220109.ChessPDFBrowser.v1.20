package com.frojasg1.sun.java2d;

import com.frojasg1.sun.java2d.StateTracker;

public interface StateTrackable {
   StateTrackable.State getState();

   StateTracker getStateTracker();

   public static enum State {
      IMMUTABLE,
      STABLE,
      DYNAMIC,
      UNTRACKABLE;

      private State() {
      }
   }
}
