package com.frojasg1.sun.misc.resources;

import java.util.ListResourceBundle;

public final class Messages_zh_TW extends ListResourceBundle {
   public Messages_zh_TW() {
   }

   protected Object[][] getContents() {
      return new Object[][]{{"optpkg.versionerror", "錯誤: {0} JAR 檔使用了無效的版本格式。請檢查文件，以獲得支援的版本格式。"}, {"optpkg.attributeerror", "錯誤: {1} JAR 檔中未設定必要的 {0} JAR 資訊清單屬性。"}, {"optpkg.attributeserror", "錯誤: {0} JAR 檔中未設定某些必要的 JAR 資訊清單屬性。"}};
   }
}
