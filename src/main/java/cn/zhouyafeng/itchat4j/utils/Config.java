package cn.zhouyafeng.itchat4j.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import cn.zhouyafeng.itchat4j.utils.enums.OsNameEnum;

/**
 * 配置信息
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月23日 下午2:26:21
 * @version 1.0
 */
public class Config {

	private static Properties prop = new Properties();

	static{
		String setting_path = "/setting.properties";
		try {
			prop.load(Config.class.getClass().getResourceAsStream(setting_path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String API_WXAPPID = "API_WXAPPID";
	public static final String BASE_DIR = prop.getProperty("itchat4j.base_dir");
	public static final String PIC_DIR = prop.getProperty("itchat4j.pic_dir");
	public static final String QR_DIR = prop.getProperty("itchat4j.qr_dir");
	public static final String FILE_DIR = prop.getProperty("itchat4j.file_dir");
	public static final String VIDEO_DIR = prop.getProperty("itchat4j.video_dir");
	public static final String VOICE_DIR = prop.getProperty("itchat4j.voice_dir");
	public static final String VERSION = prop.getProperty("itchat4j.version");
	public static final String BASE_URL = prop.getProperty("itchat4j.base_url");
	public static final String DEFAULT_QR = prop.getProperty("itchat4j.qr_jpg");
	public static final String OS = "";
	public static final String DIR = "";
	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";
	public static final ArrayList<String> API_SPECIAL_USER = new ArrayList<String>(Arrays.asList(prop.getProperty("itchat4j.special_user").split(",")));

	public static final int GET_QR_MAX_COUNT = Integer.valueOf(prop.getProperty("itchat4j.get_qr_max_count"));
	public static final int SYNC_CHECK_SLEEP = Integer.valueOf(prop.getProperty("itchat4j.sync_check_sleep"));

	/**
	 * 获取文件目录
	 */
	public static String getLocalPath() {
		String localPath = null;
		try {
			localPath = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return localPath;
	}

	/**
	 * 获取操作系统平台
	 */
	public static OsNameEnum getOsNameEnum() {
		String os = System.getProperty("os.name").toUpperCase();
		if (os.indexOf(OsNameEnum.DARWIN.toString()) >= 0) {
			return OsNameEnum.DARWIN;
		} else if (os.indexOf(OsNameEnum.WINDOWS.toString()) >= 0) {
			return OsNameEnum.WINDOWS;
		} else if (os.indexOf(OsNameEnum.LINUX.toString()) >= 0) {
			return OsNameEnum.LINUX;
		} else if (os.indexOf(OsNameEnum.MAC.toString()) >= 0) {
			return OsNameEnum.MAC;
		}
		return OsNameEnum.OTHER;
	}

}
