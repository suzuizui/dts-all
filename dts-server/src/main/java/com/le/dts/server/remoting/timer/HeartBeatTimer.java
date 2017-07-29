package com.le.dts.server.remoting.timer;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.context.InvocationContext;
import com.le.dts.common.domain.remoting.RemoteMachine;
import com.le.dts.common.domain.result.Result;
import com.le.dts.common.domain.result.ResultCode;
import com.le.dts.common.service.ClientService;
import com.le.dts.server.context.ServerContext;

/**
 * 心跳定时器
 * @author tianyao.myc
 *
 */
public class HeartBeatTimer extends TimerTask implements ServerContext, Constants {

	private static final Log logger = LogFactory.getLog(HeartBeatTimer.class);

    /** 客户端基础服务 */
	private ClientService clientService = serverRemoting.proxyInterface(ClientService.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void run() {
        try {
			ConcurrentHashMap<String, ConcurrentHashMap<String, RemoteMachine>> machineGroupTable = serverRemoting
			        .getMachineGroupTable();
			int groupAmount = machineGroupTable.size();
			int clientAmount = 0;
			Iterator iterator = machineGroupTable.entrySet().iterator();
			while (iterator.hasNext()) {
			    Map.Entry entry = (Map.Entry) iterator.next();
			    ConcurrentHashMap<String, RemoteMachine> machineMap = (ConcurrentHashMap<String, RemoteMachine>) entry
			            .getValue();

			    Iterator machineIterator = machineMap.entrySet().iterator();
			    while (machineIterator.hasNext()) {
			        Map.Entry machineEntry = (Map.Entry) machineIterator.next();
			        RemoteMachine clientMachine = (RemoteMachine) machineEntry.getValue();
			        check4Alive(clientMachine);
			        clientAmount ++;
			    }

			}
			logger.info("[HeartBeatTimer]: run groupAmount:" + groupAmount + ", clientAmount:" + clientAmount);
		} catch (Throwable e) {
			logger.error("[HeartBeatTimer]: run error, serverConfig:" + serverConfig, e);
		}
    }

    private void check4Alive(RemoteMachine remoteMachine) {
        Result<String> checkResult = null;
        try {
            remoteMachine.setTimeout(serverConfig.getHeartBeatCheckTimeout());
            InvocationContext.setRemoteMachine(remoteMachine);
            checkResult = clientService.heartBeatCheck();
        } catch (Throwable e) {
            logger.error("[HeartBeatTimer]: heartBeatCheck error, remoteMachine:" + remoteMachine, e);
        }
        if(null == checkResult) {
            serverRemoting.deleteConnection(remoteMachine);
            logger.error("[HeartBeatTimer]: heartBeatCheck result is null, so delete remoteMachine:" + remoteMachine);
            return;
        }
        if(! ResultCode.SUCCESS.equals(checkResult.getResultCode())) {
            serverRemoting.deleteConnection(remoteMachine);
            logger.error("[HeartBeatTimer]: heartBeatCheck failed, so delete remoteMachine:" + remoteMachine);
        }
    }



}
