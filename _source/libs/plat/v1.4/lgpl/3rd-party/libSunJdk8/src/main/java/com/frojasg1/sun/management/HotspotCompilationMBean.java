package com.frojasg1.sun.management;

import java.util.List;

import com.frojasg1.sun.management.CompilerThreadStat;
import com.frojasg1.sun.management.MethodInfo;
import com.frojasg1.sun.management.counter.Counter;

public interface HotspotCompilationMBean {
   int getCompilerThreadCount();

   List<CompilerThreadStat> getCompilerThreadStats();

   long getTotalCompileCount();

   long getBailoutCompileCount();

   long getInvalidatedCompileCount();

   com.frojasg1.sun.management.MethodInfo getLastCompile();

   com.frojasg1.sun.management.MethodInfo getFailedCompile();

   MethodInfo getInvalidatedCompile();

   long getCompiledMethodCodeSize();

   long getCompiledMethodSize();

   List<Counter> getInternalCompilerCounters();
}
