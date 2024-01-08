package com.frojasg1.sun.misc;

import com.frojasg1.sun.misc.FIFOQueueEnumerator;
import com.frojasg1.sun.misc.LIFOQueueEnumerator;
import com.frojasg1.sun.misc.QueueElement;

import java.util.Enumeration;

public class Queue<T> {
   int length = 0;
   com.frojasg1.sun.misc.QueueElement<T> head = null;
   com.frojasg1.sun.misc.QueueElement<T> tail = null;

   public Queue() {
   }

   public synchronized void enqueue(T var1) {
      com.frojasg1.sun.misc.QueueElement var2 = new com.frojasg1.sun.misc.QueueElement(var1);
      if (this.head == null) {
         this.head = var2;
         this.tail = var2;
         this.length = 1;
      } else {
         var2.next = this.head;
         this.head.prev = var2;
         this.head = var2;
         ++this.length;
      }

      this.notify();
   }

   public T dequeue() throws InterruptedException {
      return this.dequeue(0L);
   }

   public synchronized T dequeue(long var1) throws InterruptedException {
      while(this.tail == null) {
         this.wait(var1);
      }

      com.frojasg1.sun.misc.QueueElement var3 = this.tail;
      this.tail = var3.prev;
      if (this.tail == null) {
         this.head = null;
      } else {
         this.tail.next = null;
      }

      --this.length;
      return (T) var3.obj;
   }

   public synchronized boolean isEmpty() {
      return this.tail == null;
   }

   public final synchronized Enumeration<T> elements() {
      return new com.frojasg1.sun.misc.LIFOQueueEnumerator(this);
   }

   public final synchronized Enumeration<T> reverseElements() {
      return new com.frojasg1.sun.misc.FIFOQueueEnumerator(this);
   }

   public synchronized void dump(String var1) {
      System.err.println(">> " + var1);
      System.err.println("[" + this.length + " elt(s); head = " + (this.head == null ? "null" : this.head.obj + "") + " tail = " + (this.tail == null ? "null" : this.tail.obj + ""));
      com.frojasg1.sun.misc.QueueElement var2 = this.head;

      com.frojasg1.sun.misc.QueueElement var3;
      for(var3 = null; var2 != null; var2 = var2.next) {
         System.err.println("  " + var2);
         var3 = var2;
      }

      if (var3 != this.tail) {
         System.err.println("  tail != last: " + this.tail + ", " + var3);
      }

      System.err.println("]");
   }
}
