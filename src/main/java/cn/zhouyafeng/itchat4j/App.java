package cn.zhouyafeng.itchat4j;

import cn.zhouyafeng.itchat4j.face.MsgHandler;
import cn.zhouyafeng.itchat4j.face.impl.MsgHandlerImpl;
import cn.zhouyafeng.itchat4j.utils.Config;

/**
 * 程序启动入口
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月28日 上午12:44:10
 * @version 1.0
 */
public class App {
	public static void main(String[] args) {
		String qrPath = Config.QR_DIR; // 保存登陆二维码图片的路径，这里需要在本地新建目录
		MsgHandler msgHandler = new MsgHandlerImpl(); // 实现IMsgHandlerFace接口的类
		Wechat wechat = new Wechat(msgHandler, qrPath); // 【注入】
		wechat.start(); // 启动服务，会在qrPath下生成一张二维码图片，扫描即可登陆，注意，二维码图片如果超过一定时间未扫描会过期，过期时会自动更新，所以你可能需要重新打开图片
	}
}
