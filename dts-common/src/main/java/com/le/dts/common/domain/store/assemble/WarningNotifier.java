package com.le.dts.common.domain.store.assemble;
/**
 * 报警通知人
 * @author luliang.ll
 *
 */
public class WarningNotifier {
    // 旺旺通知人ID
	private String wwId;
	// 短信通知人ID
	private String mobileId;
	
	public WarningNotifier(){}
	
	public WarningNotifier(String wwId, String mobileId) {
		
		this.wwId = wwId;
		this.mobileId = mobileId;
	}

	public String getWwId() {
		return wwId;
	}

	public void setWwId(String wwId) {
		this.wwId = wwId;
	}

	public String getMobileId() {
		return mobileId;
	}

	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}
	
}
