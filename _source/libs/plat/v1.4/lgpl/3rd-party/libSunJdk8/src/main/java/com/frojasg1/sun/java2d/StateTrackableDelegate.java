package com.frojasg1.sun.java2d;

import com.frojasg1.sun.java2d.StateTrackable;
import com.frojasg1.sun.java2d.StateTracker;

public final class StateTrackableDelegate implements com.frojasg1.sun.java2d.StateTrackable {
   public static final StateTrackableDelegate UNTRACKABLE_DELEGATE;
   public static final StateTrackableDelegate IMMUTABLE_DELEGATE;
   private State theState;
   com.frojasg1.sun.java2d.StateTracker theTracker;
   private int numDynamicAgents;

   public static StateTrackableDelegate createInstance(State var0) {
      switch(var0) {
      case UNTRACKABLE:
         return UNTRACKABLE_DELEGATE;
      case STABLE:
         return new StateTrackableDelegate(com.frojasg1.sun.java2d.StateTrackable.State.STABLE);
      case DYNAMIC:
         return new StateTrackableDelegate(com.frojasg1.sun.java2d.StateTrackable.State.DYNAMIC);
      case IMMUTABLE:
         return IMMUTABLE_DELEGATE;
      default:
         throw new InternalError("unknown state");
      }
   }

   private StateTrackableDelegate(State var1) {
      this.theState = var1;
   }

   public State getState() {
      return this.theState;
   }

   public synchronized com.frojasg1.sun.java2d.StateTracker getStateTracker() {
      com.frojasg1.sun.java2d.StateTracker var1 = this.theTracker;
      if (var1 == null) {
         switch(this.theState) {
         case UNTRACKABLE:
         case DYNAMIC:
            var1 = com.frojasg1.sun.java2d.StateTracker.NEVER_CURRENT;
            break;
         case STABLE:
            var1 = new com.frojasg1.sun.java2d.StateTracker() {
               public boolean isCurrent() {
                  return StateTrackableDelegate.this.theTracker == this;
               }
            };
            break;
         case IMMUTABLE:
            var1 = StateTracker.ALWAYS_CURRENT;
         }

         this.theTracker = var1;
      }

      return var1;
   }

   public synchronized void setImmutable() {
      if (this.theState != com.frojasg1.sun.java2d.StateTrackable.State.UNTRACKABLE && this.theState != com.frojasg1.sun.java2d.StateTrackable.State.DYNAMIC) {
         this.theState = com.frojasg1.sun.java2d.StateTrackable.State.IMMUTABLE;
         this.theTracker = null;
      } else {
         throw new IllegalStateException("UNTRACKABLE or DYNAMIC objects cannot become IMMUTABLE");
      }
   }

   public synchronized void setUntrackable() {
      if (this.theState == com.frojasg1.sun.java2d.StateTrackable.State.IMMUTABLE) {
         throw new IllegalStateException("IMMUTABLE objects cannot become UNTRACKABLE");
      } else {
         this.theState = com.frojasg1.sun.java2d.StateTrackable.State.UNTRACKABLE;
         this.theTracker = null;
      }
   }

   public synchronized void addDynamicAgent() {
      if (this.theState == com.frojasg1.sun.java2d.StateTrackable.State.IMMUTABLE) {
         throw new IllegalStateException("Cannot change state from IMMUTABLE");
      } else {
         ++this.numDynamicAgents;
         if (this.theState == com.frojasg1.sun.java2d.StateTrackable.State.STABLE) {
            this.theState = com.frojasg1.sun.java2d.StateTrackable.State.DYNAMIC;
            this.theTracker = null;
         }

      }
   }

   protected synchronized void removeDynamicAgent() {
      if (--this.numDynamicAgents == 0 && this.theState == com.frojasg1.sun.java2d.StateTrackable.State.DYNAMIC) {
         this.theState = com.frojasg1.sun.java2d.StateTrackable.State.STABLE;
         this.theTracker = null;
      }

   }

   public final void markDirty() {
      this.theTracker = null;
   }

   static {
      UNTRACKABLE_DELEGATE = new StateTrackableDelegate(com.frojasg1.sun.java2d.StateTrackable.State.UNTRACKABLE);
      IMMUTABLE_DELEGATE = new StateTrackableDelegate(StateTrackable.State.IMMUTABLE);
   }
}
