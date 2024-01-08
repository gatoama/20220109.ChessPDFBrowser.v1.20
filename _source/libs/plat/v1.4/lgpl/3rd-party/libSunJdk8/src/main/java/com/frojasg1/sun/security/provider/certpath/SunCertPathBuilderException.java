package com.frojasg1.sun.security.provider.certpath;

import com.frojasg1.sun.security.provider.certpath.AdjacencyList;

import java.security.cert.CertPathBuilderException;

public class SunCertPathBuilderException extends CertPathBuilderException {
   private static final long serialVersionUID = -7814288414129264709L;
   private transient com.frojasg1.sun.security.provider.certpath.AdjacencyList adjList;

   public SunCertPathBuilderException() {
   }

   public SunCertPathBuilderException(String var1) {
      super(var1);
   }

   public SunCertPathBuilderException(Throwable var1) {
      super(var1);
   }

   public SunCertPathBuilderException(String var1, Throwable var2) {
      super(var1, var2);
   }

   SunCertPathBuilderException(String var1, com.frojasg1.sun.security.provider.certpath.AdjacencyList var2) {
      this(var1);
      this.adjList = var2;
   }

   SunCertPathBuilderException(String var1, Throwable var2, com.frojasg1.sun.security.provider.certpath.AdjacencyList var3) {
      this(var1, var2);
      this.adjList = var3;
   }

   public AdjacencyList getAdjacencyList() {
      return this.adjList;
   }
}
