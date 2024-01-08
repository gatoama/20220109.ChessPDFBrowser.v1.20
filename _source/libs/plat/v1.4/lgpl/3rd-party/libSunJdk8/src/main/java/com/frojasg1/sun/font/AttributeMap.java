package com.frojasg1.sun.font;

import com.frojasg1.sun.font.AttributeValues;

import java.awt.font.TextAttribute;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class AttributeMap extends AbstractMap<TextAttribute, Object> {
   private com.frojasg1.sun.font.AttributeValues values;
   private Map<TextAttribute, Object> delegateMap;
   private static boolean first = false;

   public AttributeMap(com.frojasg1.sun.font.AttributeValues var1) {
      this.values = var1;
   }

   public Set<Entry<TextAttribute, Object>> entrySet() {
      return this.delegate().entrySet();
   }

   public Object put(TextAttribute var1, Object var2) {
      return this.delegate().put(var1, var2);
   }

   public AttributeValues getValues() {
      return this.values;
   }

   private Map<TextAttribute, Object> delegate() {
      if (this.delegateMap == null) {
         if (first) {
            first = false;
            Thread.dumpStack();
         }

         this.delegateMap = this.values.toMap(new HashMap(27));
         this.values = null;
      }

      return this.delegateMap;
   }

   public String toString() {
      return this.values != null ? "map of " + this.values.toString() : super.toString();
   }
}
