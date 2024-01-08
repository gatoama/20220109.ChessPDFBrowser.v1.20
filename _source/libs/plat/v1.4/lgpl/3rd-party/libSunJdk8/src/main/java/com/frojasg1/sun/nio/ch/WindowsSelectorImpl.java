package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.IOUtil;
import com.frojasg1.sun.nio.ch.Net;
import com.frojasg1.sun.nio.ch.PollArrayWrapper;
import com.frojasg1.sun.nio.ch.SelChImpl;
import com.frojasg1.sun.nio.ch.SelectionKeyImpl;
import com.frojasg1.sun.nio.ch.SelectorImpl;
import com.frojasg1.sun.nio.ch.SinkChannelImpl;
import com.frojasg1.sun.nio.ch.SocketChannelImpl;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

final class WindowsSelectorImpl extends SelectorImpl {
   private final int INIT_CAP = 8;
   private static final int MAX_SELECTABLE_FDS = 1024;
   private com.frojasg1.sun.nio.ch.SelectionKeyImpl[] channelArray = new com.frojasg1.sun.nio.ch.SelectionKeyImpl[8];
   private com.frojasg1.sun.nio.ch.PollArrayWrapper pollWrapper = new com.frojasg1.sun.nio.ch.PollArrayWrapper(8);
   private int totalChannels = 1;
   private int threadsCount = 0;
   private final List<WindowsSelectorImpl.SelectThread> threads = new ArrayList();
   private final Pipe wakeupPipe = Pipe.open();
   private final int wakeupSourceFd;
   private final int wakeupSinkFd;
   private Object closeLock = new Object();
   private final WindowsSelectorImpl.FdMap fdMap = new WindowsSelectorImpl.FdMap();
   private final WindowsSelectorImpl.SubSelector subSelector = new WindowsSelectorImpl.SubSelector();
   private long timeout;
   private final Object interruptLock = new Object();
   private volatile boolean interruptTriggered = false;
   private final WindowsSelectorImpl.StartLock startLock = new WindowsSelectorImpl.StartLock();
   private final WindowsSelectorImpl.FinishLock finishLock = new WindowsSelectorImpl.FinishLock();
   private long updateCount = 0L;

   WindowsSelectorImpl(SelectorProvider var1) throws IOException {
      super(var1);
      this.wakeupSourceFd = ((com.frojasg1.sun.nio.ch.SelChImpl)this.wakeupPipe.source()).getFDVal();
      com.frojasg1.sun.nio.ch.SinkChannelImpl var2 = (com.frojasg1.sun.nio.ch.SinkChannelImpl)this.wakeupPipe.sink();
      var2.sc.socket().setTcpNoDelay(true);
      this.wakeupSinkFd = var2.getFDVal();
      this.pollWrapper.addWakeupSocket(this.wakeupSourceFd, 0);
   }

   protected int doSelect(long var1) throws IOException {
      if (this.channelArray == null) {
         throw new ClosedSelectorException();
      } else {
         this.timeout = var1;
         this.processDeregisterQueue();
         if (this.interruptTriggered) {
            this.resetWakeupSocket();
            return 0;
         } else {
            this.adjustThreadsCount();
            this.finishLock.reset();
            this.startLock.startThreads();

            try {
               this.begin();

               try {
                  this.subSelector.poll();
               } catch (IOException var7) {
                  this.finishLock.setException(var7);
               }

               if (this.threads.size() > 0) {
                  this.finishLock.waitForHelperThreads();
               }
            } finally {
               this.end();
            }

            this.finishLock.checkForException();
            this.processDeregisterQueue();
            int var3 = this.updateSelectedKeys();
            this.resetWakeupSocket();
            return var3;
         }
      }
   }

   private void adjustThreadsCount() {
      int var1;
      if (this.threadsCount > this.threads.size()) {
         for(var1 = this.threads.size(); var1 < this.threadsCount; ++var1) {
            WindowsSelectorImpl.SelectThread var2 = new WindowsSelectorImpl.SelectThread(var1);
            this.threads.add(var2);
            var2.setDaemon(true);
            var2.start();
         }
      } else if (this.threadsCount < this.threads.size()) {
         for(var1 = this.threads.size() - 1; var1 >= this.threadsCount; --var1) {
            ((WindowsSelectorImpl.SelectThread)this.threads.remove(var1)).makeZombie();
         }
      }

   }

   private void setWakeupSocket() {
      this.setWakeupSocket0(this.wakeupSinkFd);
   }

   private native void setWakeupSocket0(int var1);

   private void resetWakeupSocket() {
      synchronized(this.interruptLock) {
         if (this.interruptTriggered) {
            this.resetWakeupSocket0(this.wakeupSourceFd);
            this.interruptTriggered = false;
         }
      }
   }

   private native void resetWakeupSocket0(int var1);

   private native boolean discardUrgentData(int var1);

   private int updateSelectedKeys() {
      ++this.updateCount;
      byte var1 = 0;
      int var4 = var1 + this.subSelector.processSelectedKeys(this.updateCount);

      WindowsSelectorImpl.SelectThread var3;
      for(Iterator var2 = this.threads.iterator(); var2.hasNext(); var4 += var3.subSelector.processSelectedKeys(this.updateCount)) {
         var3 = (WindowsSelectorImpl.SelectThread)var2.next();
      }

      return var4;
   }

   protected void implClose() throws IOException {
      synchronized(this.closeLock) {
         if (this.channelArray != null && this.pollWrapper != null) {
            synchronized(this.interruptLock) {
               this.interruptTriggered = true;
            }

            this.wakeupPipe.sink().close();
            this.wakeupPipe.source().close();

            for(int var2 = 1; var2 < this.totalChannels; ++var2) {
               if (var2 % 1024 != 0) {
                  this.deregister(this.channelArray[var2]);
                  SelectableChannel var3 = this.channelArray[var2].channel();
                  if (!var3.isOpen() && !var3.isRegistered()) {
                     ((com.frojasg1.sun.nio.ch.SelChImpl)var3).kill();
                  }
               }
            }

            this.pollWrapper.free();
            this.pollWrapper = null;
            this.selectedKeys = null;
            this.channelArray = null;
            Iterator var7 = this.threads.iterator();

            while(var7.hasNext()) {
               WindowsSelectorImpl.SelectThread var8 = (WindowsSelectorImpl.SelectThread)var7.next();
               var8.makeZombie();
            }

            this.startLock.startThreads();
         }

      }
   }

   protected void implRegister(com.frojasg1.sun.nio.ch.SelectionKeyImpl var1) {
      synchronized(this.closeLock) {
         if (this.pollWrapper == null) {
            throw new ClosedSelectorException();
         } else {
            this.growIfNeeded();
            this.channelArray[this.totalChannels] = var1;
            var1.setIndex(this.totalChannels);
            this.fdMap.put(var1);
            this.keys.add(var1);
            this.pollWrapper.addEntry(this.totalChannels, var1);
            ++this.totalChannels;
         }
      }
   }

   private void growIfNeeded() {
      if (this.channelArray.length == this.totalChannels) {
         int var1 = this.totalChannels * 2;
         com.frojasg1.sun.nio.ch.SelectionKeyImpl[] var2 = new com.frojasg1.sun.nio.ch.SelectionKeyImpl[var1];
         System.arraycopy(this.channelArray, 1, var2, 1, this.totalChannels - 1);
         this.channelArray = var2;
         this.pollWrapper.grow(var1);
      }

      if (this.totalChannels % 1024 == 0) {
         this.pollWrapper.addWakeupSocket(this.wakeupSourceFd, this.totalChannels);
         ++this.totalChannels;
         ++this.threadsCount;
      }

   }

   protected void implDereg(com.frojasg1.sun.nio.ch.SelectionKeyImpl var1) throws IOException {
      int var2 = var1.getIndex();

      assert var2 >= 0;

      synchronized(this.closeLock) {
         if (var2 != this.totalChannels - 1) {
            com.frojasg1.sun.nio.ch.SelectionKeyImpl var4 = this.channelArray[this.totalChannels - 1];
            this.channelArray[var2] = var4;
            var4.setIndex(var2);
            this.pollWrapper.replaceEntry(this.pollWrapper, this.totalChannels - 1, this.pollWrapper, var2);
         }

         var1.setIndex(-1);
      }

      this.channelArray[this.totalChannels - 1] = null;
      --this.totalChannels;
      if (this.totalChannels != 1 && this.totalChannels % 1024 == 1) {
         --this.totalChannels;
         --this.threadsCount;
      }

      this.fdMap.remove(var1);
      this.keys.remove(var1);
      this.selectedKeys.remove(var1);
      this.deregister(var1);
      SelectableChannel var3 = var1.channel();
      if (!var3.isOpen() && !var3.isRegistered()) {
         ((SelChImpl)var3).kill();
      }

   }

   public void putEventOps(com.frojasg1.sun.nio.ch.SelectionKeyImpl var1, int var2) {
      synchronized(this.closeLock) {
         if (this.pollWrapper == null) {
            throw new ClosedSelectorException();
         } else {
            int var4 = var1.getIndex();
            if (var4 == -1) {
               throw new CancelledKeyException();
            } else {
               this.pollWrapper.putEventOps(var4, var2);
            }
         }
      }
   }

   public Selector wakeup() {
      synchronized(this.interruptLock) {
         if (!this.interruptTriggered) {
            this.setWakeupSocket();
            this.interruptTriggered = true;
         }

         return this;
      }
   }

   static {
      IOUtil.load();
   }

   private static final class FdMap extends HashMap<Integer, WindowsSelectorImpl.MapEntry> {
      static final long serialVersionUID = 0L;

      private FdMap() {
      }

      private WindowsSelectorImpl.MapEntry get(int var1) {
         return (WindowsSelectorImpl.MapEntry)this.get(new Integer(var1));
      }

      private WindowsSelectorImpl.MapEntry put(com.frojasg1.sun.nio.ch.SelectionKeyImpl var1) {
         return (WindowsSelectorImpl.MapEntry)this.put(new Integer(var1.channel.getFDVal()), new WindowsSelectorImpl.MapEntry(var1));
      }

      private WindowsSelectorImpl.MapEntry remove(com.frojasg1.sun.nio.ch.SelectionKeyImpl var1) {
         Integer var2 = new Integer(var1.channel.getFDVal());
         WindowsSelectorImpl.MapEntry var3 = (WindowsSelectorImpl.MapEntry)this.get(var2);
         return var3 != null && var3.ski.channel == var1.channel ? (WindowsSelectorImpl.MapEntry)this.remove(var2) : null;
      }
   }

   private final class FinishLock {
      private int threadsToFinish;
      IOException exception;

      private FinishLock() {
         this.exception = null;
      }

      private void reset() {
         this.threadsToFinish = WindowsSelectorImpl.this.threads.size();
      }

      private synchronized void threadFinished() {
         if (this.threadsToFinish == WindowsSelectorImpl.this.threads.size()) {
            WindowsSelectorImpl.this.wakeup();
         }

         --this.threadsToFinish;
         if (this.threadsToFinish == 0) {
            this.notify();
         }

      }

      private synchronized void waitForHelperThreads() {
         if (this.threadsToFinish == WindowsSelectorImpl.this.threads.size()) {
            WindowsSelectorImpl.this.wakeup();
         }

         while(this.threadsToFinish != 0) {
            try {
               WindowsSelectorImpl.this.finishLock.wait();
            } catch (InterruptedException var2) {
               Thread.currentThread().interrupt();
            }
         }

      }

      private synchronized void setException(IOException var1) {
         this.exception = var1;
      }

      private void checkForException() throws IOException {
         if (this.exception != null) {
            StringBuffer var1 = new StringBuffer("An exception occurred during the execution of select(): \n");
            var1.append(this.exception);
            var1.append('\n');
            this.exception = null;
            throw new IOException(var1.toString());
         }
      }
   }

   private static final class MapEntry {
      com.frojasg1.sun.nio.ch.SelectionKeyImpl ski;
      long updateCount = 0L;
      long clearedCount = 0L;

      MapEntry(com.frojasg1.sun.nio.ch.SelectionKeyImpl var1) {
         this.ski = var1;
      }
   }

   private final class SelectThread extends Thread {
      private final int index;
      final WindowsSelectorImpl.SubSelector subSelector;
      private long lastRun;
      private volatile boolean zombie;

      private SelectThread(int var2) {
         this.lastRun = 0L;
         this.index = var2;
         this.subSelector = WindowsSelectorImpl.this.new SubSelector(var2);
         this.lastRun = WindowsSelectorImpl.this.startLock.runsCounter;
      }

      void makeZombie() {
         this.zombie = true;
      }

      boolean isZombie() {
         return this.zombie;
      }

      public void run() {
         for(; !WindowsSelectorImpl.this.startLock.waitForStart(this); WindowsSelectorImpl.this.finishLock.threadFinished()) {
            try {
               this.subSelector.poll(this.index);
            } catch (IOException var2) {
               WindowsSelectorImpl.this.finishLock.setException(var2);
            }
         }

      }
   }

   private final class StartLock {
      private long runsCounter;

      private StartLock() {
      }

      private synchronized void startThreads() {
         ++this.runsCounter;
         this.notifyAll();
      }

      private synchronized boolean waitForStart(WindowsSelectorImpl.SelectThread var1) {
         while(this.runsCounter == var1.lastRun) {
            try {
               WindowsSelectorImpl.this.startLock.wait();
            } catch (InterruptedException var3) {
               Thread.currentThread().interrupt();
            }
         }

         if (var1.isZombie()) {
            return true;
         } else {
            var1.lastRun = this.runsCounter;
            return false;
         }
      }
   }

   private final class SubSelector {
      private final int pollArrayIndex;
      private final int[] readFds;
      private final int[] writeFds;
      private final int[] exceptFds;

      private SubSelector() {
         this.readFds = new int[1025];
         this.writeFds = new int[1025];
         this.exceptFds = new int[1025];
         this.pollArrayIndex = 0;
      }

      private SubSelector(int var2) {
         this.readFds = new int[1025];
         this.writeFds = new int[1025];
         this.exceptFds = new int[1025];
         this.pollArrayIndex = (var2 + 1) * 1024;
      }

      private int poll() throws IOException {
         return this.poll0(WindowsSelectorImpl.this.pollWrapper.pollArrayAddress, Math.min(WindowsSelectorImpl.this.totalChannels, 1024), this.readFds, this.writeFds, this.exceptFds, WindowsSelectorImpl.this.timeout);
      }

      private int poll(int var1) throws IOException {
         return this.poll0(WindowsSelectorImpl.this.pollWrapper.pollArrayAddress + (long)(this.pollArrayIndex * com.frojasg1.sun.nio.ch.PollArrayWrapper.SIZE_POLLFD), Math.min(1024, WindowsSelectorImpl.this.totalChannels - (var1 + 1) * 1024), this.readFds, this.writeFds, this.exceptFds, WindowsSelectorImpl.this.timeout);
      }

      private native int poll0(long var1, int var3, int[] var4, int[] var5, int[] var6, long var7);

      private int processSelectedKeys(long var1) {
         byte var3 = 0;
         int var4 = var3 + this.processFDSet(var1, this.readFds, com.frojasg1.sun.nio.ch.Net.POLLIN, false);
         var4 += this.processFDSet(var1, this.writeFds, com.frojasg1.sun.nio.ch.Net.POLLCONN | com.frojasg1.sun.nio.ch.Net.POLLOUT, false);
         var4 += this.processFDSet(var1, this.exceptFds, com.frojasg1.sun.nio.ch.Net.POLLIN | com.frojasg1.sun.nio.ch.Net.POLLCONN | Net.POLLOUT, true);
         return var4;
      }

      private int processFDSet(long var1, int[] var3, int var4, boolean var5) {
         int var6 = 0;

         for(int var7 = 1; var7 <= var3[0]; ++var7) {
            int var8 = var3[var7];
            if (var8 == WindowsSelectorImpl.this.wakeupSourceFd) {
               synchronized(WindowsSelectorImpl.this.interruptLock) {
                  WindowsSelectorImpl.this.interruptTriggered = true;
               }
            } else {
               WindowsSelectorImpl.MapEntry var9 = WindowsSelectorImpl.this.fdMap.get(var8);
               if (var9 != null) {
                  SelectionKeyImpl var10 = var9.ski;
                  if (!var5 || !(var10.channel() instanceof com.frojasg1.sun.nio.ch.SocketChannelImpl) || !WindowsSelectorImpl.this.discardUrgentData(var8)) {
                     if (WindowsSelectorImpl.this.selectedKeys.contains(var10)) {
                        if (var9.clearedCount != var1) {
                           if (var10.channel.translateAndSetReadyOps(var4, var10) && var9.updateCount != var1) {
                              var9.updateCount = var1;
                              ++var6;
                           }
                        } else if (var10.channel.translateAndUpdateReadyOps(var4, var10) && var9.updateCount != var1) {
                           var9.updateCount = var1;
                           ++var6;
                        }

                        var9.clearedCount = var1;
                     } else {
                        if (var9.clearedCount != var1) {
                           var10.channel.translateAndSetReadyOps(var4, var10);
                           if ((var10.nioReadyOps() & var10.nioInterestOps()) != 0) {
                              WindowsSelectorImpl.this.selectedKeys.add(var10);
                              var9.updateCount = var1;
                              ++var6;
                           }
                        } else {
                           var10.channel.translateAndUpdateReadyOps(var4, var10);
                           if ((var10.nioReadyOps() & var10.nioInterestOps()) != 0) {
                              WindowsSelectorImpl.this.selectedKeys.add(var10);
                              var9.updateCount = var1;
                              ++var6;
                           }
                        }

                        var9.clearedCount = var1;
                     }
                  }
               }
            }
         }

         return var6;
      }
   }
}
