package com.boxlab.utils;

import java.io.UnsupportedEncodingException;

public class EncodingConversionUtil {

	/**
	 * 字符串转换成十六进制字符串（转GBK编码）
	 * 
	 * @param String
	 *            str 待转换的ASCII字符串
	 * @return String 如: [616C6B]
	 */
	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = null;
		try {
			bs = str.getBytes("GBK");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString().trim();
	}

	/**
	 * 生成16进制累加和校验码
	 * 
	 * @param data
	 *            除去校验位的数据
	 * @return
	 */
	public static String makeChecksum(String data) {
		int total = 0;
		int len = data.length();
		int num = 0;
		while (num < len) {
			String s = data.substring(num, num + 2);
			total += Integer.parseInt(s, 16);
			num = num + 2;
		}
		/**
		 * 用256求余最大是255，即16进制的FF
		 */
		int mod = total % 65535;
		String hex = Integer.toHexString(mod);
		len = hex.length();
		// 如果不够校验位的长度，补0
		switch (len) {
		case 1:
			hex = "000" + hex;
			break;
		case 2:
			hex = "00" + hex;
			break;
		case 3:
			hex = "0" + hex;
			break;
		default:
			break;
		}
		return hex;
	}

	public static byte[] HexString2Bytes(String src) {
		int lenth = src.length() / 2;
		byte[] ret = new byte[lenth];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < lenth; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	// 将byte转换为十六进制字符串
	public static String byte2HexStr(byte val) {
		String hexTab[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"A", "B", "C", "D", "E", "F" };
		return (hexTab[(val >> 4) & 0x0F] + hexTab[val & 0x0F]);
	}

	public static String getHexStr(String str) {
		String rtn = "";
		String hexStr = "0123456789ABCDEF";

		str = str.toUpperCase();
		for (int i = 0; i < str.length(); i++) {
			if (hexStr.indexOf(str.charAt(i)) != -1) {
				rtn = rtn + str.charAt(i);
			}
		}
		return rtn;
	}

	public static String strToHexStr(String str) {
		System.out.println(str);
		StringBuilder data = new StringBuilder(
				"00000000000000000000000000000000");
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		data.replace(32 - sb.toString().length(), 32, sb.toString());
		return data.toString().trim();
	}

	public static byte charToByte(char c) {
		String hexHTab = "0123456789ABCDEF";
		String hexLTab = "0123456789abcdef";
		int index;

		index = hexHTab.indexOf(c);
		if (index == -1) {
			index = hexLTab.indexOf(c);
		}
		return (byte) index;
	}

	public static byte[] hexStr2ByteArray(String hexStr) {
		int pos;
		int len;
		byte[] byteArray;
		char[] hexChars;

		if (hexStr == null || hexStr.equals("")) {
			return null;
		}
		hexStr = hexStr.toUpperCase();
		len = hexStr.length() / 2;
		hexChars = hexStr.toCharArray();
		byteArray = new byte[len];
		for (int i = 0; i < len; i++) {
			pos = i * 2;
			byteArray[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return byteArray;
	}

	// 将byte[]转换为十六进制字符串
	public static String byteArray2HexStr(byte[] bytes, boolean isSplitStr,
			String splitStr) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int val = bytes[i] & 0xFF;
			if (val < 0x10) {
				buf.append("0");
			}
			buf.append(Integer.toHexString(val).toUpperCase());
			if (i != (bytes.length - 1)) {
				if (isSplitStr) {
					buf.append(splitStr);
				}
			}
		}
		return buf.toString();
	}

	// 十六进制转换字符串
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}
	
	public static String trimStr(String str) {
		return str.replace('\t', ' ').replace('\n', ' ').replace('\f', ' ')
				.replace('\r', ' ').replace(" ", "");
	}

}
