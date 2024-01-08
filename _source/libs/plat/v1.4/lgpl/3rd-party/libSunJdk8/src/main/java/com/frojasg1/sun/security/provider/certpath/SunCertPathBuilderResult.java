package com.frojasg1.sun.security.provider.certpath;

import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;

import com.frojasg1.sun.security.provider.certpath.AdjacencyList;
import com.frojasg1.sun.security.util.Debug;

public class SunCertPathBuilderResult extends PKIXCertPathBuilderResult {
   private static final Debug debug = Debug.getInstance("certpath");
   private com.frojasg1.sun.security.provider.certpath.AdjacencyList adjList;

   SunCertPathBuilderResult(CertPath var1, TrustAnchor var2, PolicyNode var3, PublicKey var4, com.frojasg1.sun.security.provider.certpath.AdjacencyList var5) {
      super(var1, var2, var3, var4);
      this.adjList = var5;
   }

   public AdjacencyList getAdjacencyList() {
      return this.adjList;
   }
}
