package com.frojasg1.sun.misc;

import com.frojasg1.sun.misc.Queue;
import com.frojasg1.sun.misc.QueueElement;

import java.util.Enumeration;
import java.util.NoSuchElementException;

final class LIFOQueueEnumerator<T> implements Enumeration<T> {
   com.frojasg1.sun.misc.Queue<T> queue;
   com.frojasg1.sun.misc.QueueElement<T> cursor;

   LIFOQueueEnumerator(Queue<T> var1) {
      this.queue = var1;
      this.cursor = var1.head;
   }

   public boolean hasMoreElements() {
      return this.cursor != null;
   }

   public T nextElement() {
      synchronized(this.queue) {
         if (this.cursor != null) {
            com.frojasg1.sun.misc.QueueElement var2 = this.cursor;
            this.cursor = this.cursor.next;
            return (T) var2.obj;
         }
      }

      throw new NoSuchElementException("LIFOQueueEnumerator");
   }
}
