/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.yzxdemo.restClient;

import java.security.MessageDigest;  


public class EncryptUtil {  

    private static final String UTF8 = "utf-8";  
  
    /** 
     * MD5����ǩ�� 
     * @param src 
     * @return 
     * @throws Exception 
     */  
    public String md5Digest(String src) throws Exception {  
    	// ��������ǩ������, ���ã�MD5, SHA-1  
       MessageDigest md = MessageDigest.getInstance("MD5");  
       byte[] b = md.digest(src.getBytes(UTF8));  
       return this.byte2HexStr(b);  
    }  
      
    /** 
     * BASE64����
     * @param src 
     * @return 
     * @throws Exception 
     */  
    public String base64Encoder(String src) throws Exception {  
    	return new String(Base64.encode(src.getBytes(UTF8)));
    }
      
    /** 
     * BASE64����
     * @param dest 
     * @return 
     * @throws Exception 
     */  
    public String base64Decoder(String dest) throws Exception {
        return new String(Base64.decode(dest));  
    }  
      
    /** 
     * �ֽ�����ת��Ϊ��д16�����ַ��� 
     * @param b 
     * @return 
     */  
    private String byte2HexStr(byte[] b) {  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < b.length; i++) {  
            String s = Integer.toHexString(b[i] & 0xFF);  
            if (s.length() == 1) {  
                sb.append("0");  
            }  
            sb.append(s.toUpperCase());  
        }  
        return sb.toString();  
    }  
}  
