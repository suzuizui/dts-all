package com.le.dts.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.BytesException;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.exception.BytesException;
import com.le.dts.com.caucho.hessian.io.HessianInput;
import com.le.dts.com.caucho.hessian.io.HessianOutput;


/**
 * 字节数组工具
 * @author tianyao.myc
 *
 */
public class BytesUtil4Client implements Constants {

	/**
	 * 判断字节数组是否为空
	 * @param bytes
	 * @return
	 */
	public static boolean isEmpty(byte[] bytes) {
		if(null == bytes) {
			return true;
		}
		if(bytes.length <= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 将对象转换为byte数组
	 * @param object
	 * @return
	 * @throws BytesException
	 */
	public static byte[] objectToBytes(Object object) 
			throws BytesException {
		if(null == object) {
			throw new BytesException("object is null");
		}
		ByteArrayOutputStream byteArrayOutputStream = 
				new ByteArrayOutputStream();
		HessianOutput hessianOutput = 
				new HessianOutput(byteArrayOutputStream);
		try {
			hessianOutput.writeObject(object);
		} catch (Exception e) {
			throw new BytesException("write object error", e);
		}
		return byteArrayOutputStream.toByteArray();
	}
	
	/**
	 * 将byte数组转换成对象
	 * @param bytes
	 * @return
	 * @throws BytesException
	 */
	public static Object bytesToObject(byte[] bytes) 
			throws BytesException {
		if(null == bytes) {
			throw new BytesException("bytes is null");
		}
		ByteArrayInputStream byteArrayInputStream = 
				new ByteArrayInputStream(bytes);
		HessianInput hessianInput = 
				new HessianInput(byteArrayInputStream);
		Object object = null;
		try {
			object = hessianInput.readObject();
		} catch (Exception e) {
			throw new BytesException("read object error", e);
		}
		return object;
	}
	
	/**
	 * 将byte数组计算MD5
	 * @param bytes
	 * @return
	 * @throws BytesException
	 */
	public static String md5(byte[] bytes) throws BytesException {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			throw new BytesException("message digest error", e);
		}
		messageDigest.update(bytes);
		byte[] resultBytes = messageDigest.digest();
		return bytesToHex(resultBytes);
	}
	
	/**
	 * 把byte数组转换成字符串
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		char[] hexDigits = {
				'0', '1', '2', '3', '4', 
				'5', '6', '7', '8', '9', 
				'a', 'b', 'c', 'd', 'e', 'f'};
		char[] resultCharArray = new char[CHAR_AMOUNT * 2];
		int index = 0;
		for(int i = 0 ; i < CHAR_AMOUNT ; i ++) {
			resultCharArray[index ++] = hexDigits[bytes[i] >>> 4 & 0xf];
			resultCharArray[index ++] = hexDigits[bytes[i] & 0xf];
		}
		return new String(resultCharArray);
	}
	
}
