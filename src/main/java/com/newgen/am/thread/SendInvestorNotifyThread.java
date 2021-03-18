package com.newgen.am.thread;

import org.springframework.http.HttpStatus;

import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.LocalServiceConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.NotificationDTO;
import com.newgen.am.exception.CustomException;

import lombok.Data;

@Data
public class SendInvestorNotifyThread extends Thread {
	private String className = "SendInvestorNotifyThread";
	private long refId;
	private String investorCode;
	private String amount;
	private String transType;
	
	public SendInvestorNotifyThread(long refId, String investorCode, String amount, String transType) {
		this.refId = refId;
		this.investorCode = investorCode;
		this.amount = amount;
		this.transType = transType;
	}
	
	@Override
	public void run() {
		try {
			AMLogger.logMessage(className, "run", refId, "Start calling investor notification.");
			callInvestorNotification();
			AMLogger.logMessage(className, "run", refId, "Finish calling investor notification.");
		} catch (Exception e) {
			AMLogger.logError(className, "run", refId, e);
		}
	}
	
	private String buildContent() {
		String content = "";
		switch(transType) {
		case Constant.MARGIN_TRANS_TYPE_DEPOSIT:
			content = String.format("Nộp tiền TKGD %s thành công. Số tiền +%s VND", investorCode, Utility.formatAmount(amount));
			break;
		case Constant.MARGIN_TRANS_TYPE_WITHDRAW:
			content = String.format("Rút tiền TKGD %s thành công. Số tiền -%s VND", investorCode, Utility.formatAmount(amount));
			break;
		case Constant.MARGIN_TRANS_TYPE_REFUND:
			content = String.format("Hoàn trả nộp tiền TKGD %s thành công. Số tiền -%s VND", investorCode, Utility.formatAmount(amount));
			break;
		default:
			break;
		}
		return content;
	}
	
	private void callInvestorNotification() {
		String methodName = "callInvestorNotification";
		try {
			NotificationDTO notification = new NotificationDTO();
			notification.setType(3);
			notification.setInvestor(investorCode);
			notification.setContent(buildContent());
			notification.setFrom(Constant.NOTIFICATION_FROM);
			notification.setSendType(1);
			
			String notificationJson = Utility.getGson().toJson(notification);
			AMLogger.logMessage(className, methodName, refId, "Notification: " + notificationJson);
			
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			serviceCon.sendPostRequest(serviceCon.getInvestorNotificationServiceURL(), notificationJson, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
