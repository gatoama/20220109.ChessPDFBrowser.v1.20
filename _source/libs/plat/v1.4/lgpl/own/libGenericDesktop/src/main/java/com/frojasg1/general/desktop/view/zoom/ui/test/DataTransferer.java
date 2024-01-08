package com.frojasg1.general.desktop.view.zoom.ui.test;

import java.awt.datatransfer.DataFlavor;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataTransferer {
    private static final Map<String, Boolean> textMIMESubtypeCharsetSupport;

    static {
        Map<String, Boolean> tempMap = new HashMap<>(17);
        tempMap.put("sgml", Boolean.TRUE);
        tempMap.put("xml", Boolean.TRUE);
        tempMap.put("html", Boolean.TRUE);
        tempMap.put("enriched", Boolean.TRUE);
        tempMap.put("richtext", Boolean.TRUE);
        tempMap.put("uri-list", Boolean.TRUE);
        tempMap.put("directory", Boolean.TRUE);
        tempMap.put("css", Boolean.TRUE);
        tempMap.put("calendar", Boolean.TRUE);
        tempMap.put("plain", Boolean.TRUE);
        tempMap.put("rtf", Boolean.FALSE);
        tempMap.put("tab-separated-values", Boolean.FALSE);
        tempMap.put("t140", Boolean.FALSE);
        tempMap.put("rfc822-headers", Boolean.FALSE);
        tempMap.put("parityfec", Boolean.FALSE);
        textMIMESubtypeCharsetSupport = Collections.synchronizedMap(tempMap);
    }

    public static boolean isFlavorCharsetTextType(DataFlavor flavor) {
        // Although stringFlavor doesn't actually support the charset
        // parameter (because its primary MIME type is not "text"), it should
        // be treated as though it does. stringFlavor is semantically
        // equivalent to "text/plain" data.
        if (DataFlavor.stringFlavor.equals(flavor)) {
            return true;
        }

        if (!"text".equals(flavor.getPrimaryType()) ||
                !doesSubtypeSupportCharset(flavor))
        {
            return false;
        }

        Class<?> rep_class = flavor.getRepresentationClass();

        if (flavor.isRepresentationClassReader() ||
                String.class.equals(rep_class) ||
                flavor.isRepresentationClassCharBuffer() ||
                char[].class.equals(rep_class))
        {
            return true;
        }

        if (!(flavor.isRepresentationClassInputStream() ||
                flavor.isRepresentationClassByteBuffer() ||
                byte[].class.equals(rep_class))) {
            return false;
        }

        String charset = flavor.getParameter("charset");

        // null equals default encoding which is always supported
        return (charset == null) || isEncodingSupported(charset);
    }
    private static boolean isEncodingSupported(String encoding) {
        if (encoding == null) {
            return false;
        }
        try {
            return Charset.isSupported(encoding);
        } catch (IllegalCharsetNameException icne) {
            return false;
        }
    }

    public static String getTextCharset(DataFlavor flavor) {
        if (!isFlavorCharsetTextType(flavor)) {
            return null;
        }

        String encoding = flavor.getParameter("charset");

        return (encoding != null) ? encoding : Charset.defaultCharset().name();
    }
    public static boolean doesSubtypeSupportCharset(DataFlavor flavor) {
        String subType = flavor.getSubType();
        if (subType == null) {
            return false;
        }

        Boolean support = textMIMESubtypeCharsetSupport.get(subType);

        if (support != null) {
            return support;
        }

        boolean ret_val = (flavor.getParameter("charset") != null);
        textMIMESubtypeCharsetSupport.put(subType, ret_val);
        return ret_val;
    }
}
