package com.le.dts.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.le.dts.common.constants.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import com.le.dts.common.constants.Constants;

/**
 * Ini工具
 * @author tianyao.myc
 *
 */
public class IniUtil implements Constants {

	private static final Log logger = LogFactory.getLog(IniUtil.class);
	
	/**
	 * 获取配置信息值
	 * @param configFilePath
	 * @param sectionName
	 * @return
	 */
	public static Map<String, String> getIniValuesFromFile(String configFilePath, String sectionName) {
		Map<String, Section> sectionsMap = getIniSectionsFormFile(configFilePath, null);
		Section section = sectionsMap.get(sectionName);
		if(null != section) {
			Map<String, String> valueMap = new HashMap<String, String>(section.size());
			for(Map.Entry<String, String> entry : section.entrySet()) {
				valueMap.put(entry.getKey(), entry.getValue());
			}
			return valueMap;
		} else {
			return Collections.emptyMap();
		}
	}
	
	/**
	 * 获取Section Map
	 * @param filePath
	 * @param charset
	 * @return
	 */
	public static Map<String, Section> getIniSectionsFormFile(String filePath, Charset charset) {
		Ini ini = new Ini();
		Config config = ini.getConfig();
		config.setFileEncoding(charset != null ? charset : DEFAULT_CHARSET);
		File iniFile = new File(filePath);
		if(iniFile.exists() && iniFile.isFile()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(iniFile);
				ini.load(fis);
				Map<String, Section> sectionMap = new HashMap<String, Section>(ini.size());
				for(Map.Entry<String, Section> entry : ini.entrySet()) {
					sectionMap.put(entry.getKey(), entry.getValue());
				}
				return sectionMap;
			} catch (Throwable e) {
				logger.error("getIniFile Error!", e);
			} finally {
				if(null != fis) {
					try {
						fis.close();
					} catch (Throwable e) {
						logger.error("getIniFile close FileInputStream Error!", e);
					}
				}
			}
		} else {
			logger.error("getIniFile Error, file not exists, filePath:" + filePath);
		}
		return Collections.emptyMap();
	}
	
}
