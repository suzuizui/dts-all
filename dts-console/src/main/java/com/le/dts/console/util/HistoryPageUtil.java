package com.le.dts.console.util;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.domain.ProgressBar;
import com.le.dts.common.domain.ProgressDetail;
import com.le.dts.common.util.CommonUtil;

/**
 * 将历史记录转化成页面字符串;
 * @author luliang.ll
 *
 */
public class HistoryPageUtil implements Constants {

	public static String resultToPageInfo(ProgressDetail progressDetail) {
		
		StringBuilder sb = new StringBuilder();
		if(progressDetail != null) {
			String overall = progressDetail.getTotalProgressBar().parsePercentRate();
			
			if(CommonUtil.isSimpleJob(progressDetail.getType())) {
				
//				long totalAmount 	= progressDetail.getTotalProgressBar().getTotalAmount();
				long successAmount 	= progressDetail.getTotalProgressBar().getSuccessAmount();
//				long failureAmount 	= progressDetail.getTotalProgressBar().getFailureAmount();
				
				if(CommonUtil.isAllJob(progressDetail.getType())) {
					sb.append("完成度: " + overall + "</br>");
				} else {
					sb.append("完成度: " + overall + ", 结果：" + (successAmount > 0L ? "成功" : "失败") + "</br>");
				}
			} else {
				sb.append("总体完成: " + overall + "</br>");
			}
			
			sb.append("<table>");
			
			for(ProgressBar progressBar: progressDetail.getProgressBarList()) {
				
				String name = progressBar.getName();
				if(DEFAULT_ROOT_LEVEL_TASK_NAME.equals(progressBar.getName())) {
					if(CommonUtil.isSimpleJob(progressDetail.getType())) {
						if(CommonUtil.isAllJob(progressDetail.getType())) {
							name = "所有机器的触发任务";
						} else {
							name = "简单触发任务";
						}
					} else {
						name = "START任务";
					}
				}
				
				sb.append("<tr style='font-size:12px' height='10'>");
				
				sb.append("<td align='right'>" + name + " -> </td>");
				sb.append("<td align='left'>总量[T:" + progressBar.getTotalAmount() + "]</td>");
				sb.append("<td align='left'>成功[S:" + progressBar.getSuccessAmount() + "]</td>");
				sb.append("<td align='left'>失败[F:" + progressBar.getFailureAmount() + "]</td>");
				if(progressBar.getFoundAmount() != 0) {
					sb.append("<td>" + progressBar.getFoundAmount() + "个找不到执行器</td>");
				}
				sb.append("</tr>");
			}
			
			sb.append("</table></br>按机器维度统计</br>" + progressDetail.getMachineProgress());
		}
		return sb.toString();
	}
}
