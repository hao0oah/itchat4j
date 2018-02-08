package cn.zhouyafeng.itchat4j.core;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.enums.MsgCodeEnum;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

/**
 * 消息处理中心
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年5月14日 下午12:47:50
 * @version 1.0
 *
 */
public class MsgCenter {
	private static Logger LOG = LoggerFactory.getLogger(MsgCenter.class);

	private static Core core = Core.getInstance();

	/**
	 * 接收消息，放入队列
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月23日 下午2:30:48
	 * @param msgList
	 * @return
	 */
	public static JSONArray produceMsg(JSONArray msgList) {
		JSONArray result = new JSONArray();

		for (int i = 0; i < msgList.size(); i++) {
			JSONObject msg = new JSONObject();
			JSONObject m = msgList.getJSONObject(i);
			m.put("groupMsg", false);// 是否是群消息
			if (m.getString("FromUserName").contains("@@") || m.getString("ToUserName").contains("@@")) { // 群聊消息
				if (m.getString("FromUserName").contains("@@")
						&& !core.getGroupIdList().contains(m.getString("FromUserName"))) {
					core.getGroupIdList().add((m.getString("FromUserName")));
				} else if (m.getString("ToUserName").contains("@@")
						&& !core.getGroupIdList().contains(m.getString("ToUserName"))) {
					core.getGroupIdList().add((m.getString("ToUserName")));
				}
				// 群消息与普通消息不同的是在其消息体（Content）中会包含发送者id及":<br/>"消息，这里需要处理一下，去掉多余信息，只保留消息内容
				if (m.getString("Content").contains("<br/>")) {
					String content = m.getString("Content").substring(m.getString("Content").indexOf("<br/>") + 5);
					m.put("Content", content);
					m.put("groupMsg", true);
				}
			} else {
				CommonTools.msgFormatter(m, "Content");
			}

			int type = m.getInteger("MsgType")==null?-1:m.getInteger("MsgType");
			if (type == MsgCodeEnum.MSGTYPE_TEXT.getCode()) {// 文本消息
				if (m.getString("Url").length() != 0) {
					String regEx = "(.+?\\(.+?\\))";
					Matcher matcher = CommonTools.getMatcher(regEx, m.getString("Content"));
					String data = "Map";
					if (matcher.find()) {
						data = matcher.group(1);
					}
					msg.put("Type", "Map");
					msg.put("Text", data);
				} else {
					msg.put("Type", MsgTypeEnum.TEXT.getType());
					msg.put("Text", m.getString("Content"));
				}
				m.put("Type", msg.getString("Type"));
				m.put("Text", msg.getString("Text"));
			} else if (type == MsgCodeEnum.MSGTYPE_IMAGE.getCode() || type == MsgCodeEnum.MSGTYPE_EMOTICON.getCode()) { // 图片消息
				m.put("Type", MsgTypeEnum.PIC.getType());
			} else if (type == MsgCodeEnum.MSGTYPE_VOICE.getCode()) { // 语音消息
				m.put("Type", MsgTypeEnum.VOICE.getType());
			} else if (type == MsgCodeEnum.MSGTYPE_VERIFYMSG.getCode()) {// friends
				// 好友确认消息
				// MessageTools.addFriend(core, userName, 3, ticket); // 确认添加好友
				m.put("Type", MsgTypeEnum.VERIFYMSG.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_SHARECARD.getCode())) { // 共享名片
				m.put("Type", MsgTypeEnum.NAMECARD.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VIDEO.getCode())
					|| m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_MICROVIDEO.getCode())) {// viedo
				m.put("Type", MsgTypeEnum.VIEDO.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_MEDIA.getCode())) { // 多媒体消息
				m.put("Type", MsgTypeEnum.MEDIA.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_STATUSNOTIFY.getCode())) {// phone
				// init
				// 微信初始化消息

			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_SYS.getCode())) {// 系统消息
				m.put("Type", MsgTypeEnum.SYS.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_RECALLED.getCode())) { // 撤回消息

			} else {
				LOG.info("Useless msg");
			}
			LOG.info("收到消息一条，来自: " + m.getString("FromUserName"));
			result.add(m);
		}
		return result;
	}

	/**
	 * 消息处理
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月14日 上午10:52:34
	 * @param msgHandler
	 */
	public static void handleMsg(IMsgHandlerFace msgHandler) {
		while (true) {
			List<BaseMsg> msgList = core.getMsgList();
			if(!msgList.isEmpty()){
				BaseMsg msg = msgList.get(0);
				if(msg.getContent()!=null && msg.getContent().length()>0){
					String type = msg.getType();
					LOG.debug("处理消息类型为[{}]",type);

					if(MsgTypeEnum.TEXT.getType().equals(type)){
						String result = msgHandler.textMsgHandle(msg);
						MessageTools.sendMsgById(result, msg.getFromUserName());
					} else if(MsgTypeEnum.PIC.getType().equals(type)){
						String result = msgHandler.picMsgHandle(msg);
						MessageTools.sendMsgById(result, msg.getFromUserName());
					} else if(MsgTypeEnum.VOICE.getType().equals(type)){
						String result = msgHandler.voiceMsgHandle(msg);
						MessageTools.sendMsgById(result, msg.getFromUserName());
					} else if(MsgTypeEnum.VIEDO.getType().equals(type)){
						String result = msgHandler.viedoMsgHandle(msg);
						MessageTools.sendMsgById(result, msg.getFromUserName());
					} else if(MsgTypeEnum.NAMECARD.getType().equals(type)){
						String result = msgHandler.nameCardMsgHandle(msg);
						MessageTools.sendMsgById(result, msg.getFromUserName());
					} else if(MsgTypeEnum.SYS.getType().equals(type)){// 系统消息
						msgHandler.sysMsgHandle(msg);
					} else if(MsgTypeEnum.VERIFYMSG.getType().equals(type)){// 确认添加好友消息
						String result = msgHandler.verifyAddFriendMsgHandle(msg);
						MessageTools.sendMsgById(result, msg.getRecommendInfo().getUserName());
					} else if(MsgTypeEnum.MEDIA.getType().equals(type)){// 多媒体消息
						String result = msgHandler.mediaMsgHandle(msg);
						MessageTools.sendMsgById(result, msg.getFromUserName());
					}
				}
				core.getMsgList().remove(0);
			}
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
