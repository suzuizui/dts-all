package com.le.dts.console.config;

/**
 * Created by luliang on 15/1/28.
 */
public class EnvData {

    private String envName;

//    private String tddlAppName;
//
//    private String tddlRuleFile;
//
//    private String dataSourceAppNameMeta;
//
//    private String dbGroupKeyMeta;

    private String zkNameSpace;
    
    private String flowDomainName;
    
    private String authKey;
    
    private String timerMsgDomainName;

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

//    public String getTddlAppName() {
//        return tddlAppName;
//    }
//
//    public void setTddlAppName(String tddlAppName) {
//        this.tddlAppName = tddlAppName;
//    }
//
//    public String getTddlRuleFile() {
//        return tddlRuleFile;
//    }
//
//    public void setTddlRuleFile(String tddlRuleFile) {
//        this.tddlRuleFile = tddlRuleFile;
//    }
//
//    public String getDataSourceAppNameMeta() {
//        return dataSourceAppNameMeta;
//    }
//
//    public void setDataSourceAppNameMeta(String dataSourceAppNameMeta) {
//        this.dataSourceAppNameMeta = dataSourceAppNameMeta;
//    }
//
//    public String getDbGroupKeyMeta() {
//        return dbGroupKeyMeta;
//    }
//
//    public void setDbGroupKeyMeta(String dbGroupKeyMeta) {
//        this.dbGroupKeyMeta = dbGroupKeyMeta;
//    }

    public String getZkNameSpace() {
        return zkNameSpace;
    }

    public void setZkNameSpace(String zkNameSpace) {
        this.zkNameSpace = zkNameSpace;
    }

	public String getFlowDomainName() {
		return flowDomainName;
	}

	public void setFlowDomainName(String flowDomainName) {
		this.flowDomainName = flowDomainName;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getTimerMsgDomainName() {
		return timerMsgDomainName;
	}

	public void setTimerMsgDomainName(String timerMsgDomainName) {
		this.timerMsgDomainName = timerMsgDomainName;
	}

}
