package com.slider.cn.app.httpurlconnectionnet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Util {
	
	public static String encodeParams(HashMap<String, String> params){
		if (params == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        Iterator<Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
        	if (first)
                first = false;
            else
                sb.append("&");
        	Entry<String, String> e = it.next();
        	sb.append(e.getKey() + "=" + e.getValue());
        }
        return sb.toString();
	}
}
