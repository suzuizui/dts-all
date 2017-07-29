package com.le.dts.server.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.exception.InitException;


/**
 * 报警管理
 * @author tianyao.myc
 *
 */
public class InnerAlertManager {

	private static final Log logger = LogFactory.getLog(InnerAlertManager.class);
	
//	private NoticenterClient noticenterClient;
	
	/**
	 * 初始化
	 * @throws InitException
	 */
    public void init() throws InitException {
//        try {
//			this.noticenterClient = new NoticenterClient(InnerAlertConfig.ALERT_ACCOUNT,
//			        InnerAlertConfig.ALERT_ACCOUNT_PASSWORD, HttpMethodEnum.POST.getHttpMethod());
//		} catch (Throwable e) {
//			throw new InitException("[InnerAlertManager]: new NoticenterClient error", e);
//		}
    }
	
    /**
     * 发送旺旺消息
     * @param user
     * @param msg
     */
    public void sendWW(String user, String msg) {
//        Notification notification = new Notification();
//        notification.setEmpId(user);
//        notification.setEmpIdMode(true);
//        notification.setMessage(msg);
//        notification.setSubtitle("DTS告警");
//        notification.setMethod(InnerAlertConfig.ALERT_WW);
//        String httpResponseText = this.noticenterClient.send(notification);
//        logger.info("[InnerAlertManager]: send alert ww message"
//        		+ ", user:" + user
//        		+ ", message:" + msg
//        		+ ", send result:" + httpResponseText);
    }
    
    /**
     * 发送短信消息
     * @param user
     * @param msg
     */
    public void sendSMS(String user, String msg) {
//    	Notification notification = new Notification();
//        notification.setEmpId(user);
//        notification.setEmpIdMode(true);
//        notification.setSubtitle("DTS告警");
//        notification.setMessage(msg);
//        notification.setMethod(InnerAlertConfig.ALERT_SMS);
//        String httpResponseText = this.noticenterClient.send(notification);
//        logger.info("[InnerAlertManager]: send alert ww message"
//        		+ ", user:" + user
//        		+ ", message:" + msg
//        		+ ", send result:" + httpResponseText);
    }
    
}
