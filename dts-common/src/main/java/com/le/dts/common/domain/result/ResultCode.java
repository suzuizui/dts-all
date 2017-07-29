package com.le.dts.common.domain.result;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.remoting.protocol.RemotingSerializable;
import com.le.dts.common.constants.Constants;
import com.le.dts.common.remoting.protocol.RemotingSerializable;

/**
 * 返回码
 * @author tianyao.myc
 *
 */
public enum ResultCode implements Constants {

	///// 返回码
	SUCCESS(		true, 	100, "成功"),
	FAILURE(		false, 	101, "失败"),
	NO_SUCH_METHOD(	false, 	102, "找不到该方法"),
	
	///// 创建job相关返回码
	CREATE_INTERNAL_JOB_SUCCESS(			true, 	200, "创建成功！"),
	CREATE_INTERNAL_JOB_NEVER_FIRE(			false, 	201, "时间表达式描述的是一个过去的时间点，job将永远不会被触发！"),
	CREATE_INTERNAL_JOB_INIT_FAILURE(		false, 	202, "定时触发器初始化失败！"),
	CREATE_INTERNAL_JOB_PUT_FAILURE(		false, 	203, "定时触发器放入内存Job池失败！"),
	CREATE_PERSISTENCE_JOB_INSERT_FAILURE(	false, 	204, "定时触发器配置持久化失败！"),
	CREATE_PERSISTENCE_JOB_SUCCESS(			true, 	205, "创建成功！"),
	CREATE_JOB_IP_LIST_ERROR(				false, 	206, "创建失败，服务器列表异常！"),
	CREATE_JOB_BACKUP_ERROR(				false, 	207, "由于DTS内部Job备份创建失败，Job处于禁用状态，可以点击[启用]按钮使Job恢复正常！"),
	CREATE_JOB_ENABLE_ERROR(				false, 	208, "由于DTS内部自动启用Job失败，Job处于禁用状态，可以点击[启用]按钮使Job恢复正常！"),
	CREATE_JOB_SUCCESS(						true, 	209, "创建成功！"),
	CREATE_INTERNAL_JOB_MAPPING_FAILURE(	false, 	210, "创建定时器关系映射失败！"),
	CREATE_INTERNAL_JOB_FAILURE(			false, 	211, "创建失败！"),
	CREATE_INTERNAL_JOB_EXISTS(				false, 	212, "internalJob is already exists"),
	CRON_EXPRESSION_ERROR(                  false, 222, "时间表达式不规范！"),
	ARGUMENT_NULL(                          false, 223, "参数不合法,为空!"),
    QUERY_SERVER_GROUP_ERROR(               false, 224, "查询集群组失败"),

    ///// 事务相关的
    CREARE_SUCCESS_START_FAILED(            false, 225, "Job创建成功，但内部启动失败，请重新启用！"),
    CREATE_SUCCESS_CANNOT_USE(              false, 226, "由于系统内部出现异常，创建的Job不可用，请删除后重新创建！"),

    ENABLE_JOB_ERROR(                       false, 227, "启用Job失败！"),
    ENABLE_JOB_ERROR_BAD_RETRY(             false, 228, "启用Job失败，系统出现异常，请先停用，再启用！"),

    DISTABLE_JOB_ERROR(                       false, 227, "停用Job失败！"),
    DISABLE_JOB_ERROR_BAD_RETRY(             false, 228, "停用Job失败，系统出现异常，请先启用，再去停用！"),

    UPDATE_ERROR_NEED_REDO(                 false, 229, "系统更新出现异常，需要重新操作才能生效！"),

    CREATE_JOB_OPERATION_ERROR(             false, 230, "创建时写入DB失败！"),

    QUERY_JOB_OPERATION_ERROR(              false, 231, "查询DB失败！"),

    DELETE_JOB_OPERATION_ERROR(             false, 232, "删除DB失败！"),
    
    SYNTAX_ERROR(             				false, 233, "分发的对象没有实现java.io.Serializable接口"),
    
    CRON_EXPRESSION_OBSOLETE(                  false, 234, "cronExpression become obsolete"),

	///// 删除job相关返回码
    DELETE_INTERNAL_JOB_SUCCESS(		true, 	300, "删除成功！"),
	DELETE_INTERNAL_JOB_IS_NULL(		false, 	301, "job内存实例为空！"),
	DELETE_INTERNAL_JOB_FAILURE(		false, 	302, "删除job异常！"),
	DELETE_INTERNAL_JOB_REMOVE_FAILURE(	false, 	303, "从内存清除job异常！"),
	DELETE_PERSISTENCE_JOB_FAILURE(		false, 	304, "删除持久化的job异常！"),
	DELETE_PERSISTENCE_JOB_SUCCESS(		true, 	305, "删除成功！"),
	DELETE_JOB_IP_LIST_ERROR(			false, 	306, "删除失败，服务器列表异常！"),
	DELETE_JOB_SUCCESS(					true, 	307, "删除成功！"),
	DELETE_JOB_FAILURE(					false, 	308, "系统繁忙，请稍后再试！"),
	DELETE_JOB_MATION_RELATION(			false, 	309, "删除Job时,接触机器和Job的绑定关系失败!"),
	
	CLUSTER_SERVER_ERROR(			        false, 	310, "集群机器没有启动或者没达到最低备份数量!"),

	QUERY_CLUSTER_GROUP_ERROR(			false, 	311, "内部查询出错!"),

    USER_PARAMETER_ERROR(               false,  312, "参数不合法!"),

    CREATE_JOB_SERVER_RELATION_ERROR(false, 313, "创建Job机器关系异常！"),

    PUBLISH_ZK_DATA_ERROR(false, 314, "ZK发布数据错误！"),

    NO_JOB_SERVER_RELATION_ERROR(false, 315, "不存在这样的对应关系！"),

    QUERY_JOB_SERVER_ERROR(false, 316, "查询Job和机器关系异常"),

    CLIENT_MACHINE_EMPTY(false, 317, "客户端没有正在运行的机器！"),

    DAUTH_ERROR(                        false, 318, "调用DAuth鉴权失败!"),
    DAUTH_DELETE_ERROR(                 false, 319, "DAuth删除资源错误！"),

    REPLAY_ERROR(                       false, 320, "重放错误！"),

	///// 查询job相关返回码
	QUERY_INTERNAL_JOB_SUCCESS(		    true, 	400, "查询成功！"),
	QUERY_INTERNAL_JOB_IS_NULL(		    false, 	401, "job内存实例为空！"),
	QUERY_PERSISTENCE_JOB_SUCCESS(	    true, 	402, "查询成功！"),
	QUERY_PERSISTENCE_JOB_IS_NULL(	    false, 	403, "查询job为空！"),
	QUERY_JOB_COUNT_ERROR(              false,  404, "统计Group中Job数目失败!"),
	QUERY_PERSISTENCE_JOB_ERROR(	    false, 	405, "查询JOB失败！"),
	BEFORE_JOB_ERROR(	    			false, 	406, "前置JOB执行失败！"),
	
	///// 更新job相关返回码
	UPDATE_INTERNAL_JOB_SUCCESS(	    true, 	500, "更新成功！"),
	UPDATE_PERSISTENCE_JOB_FAILURE(	    false, 	501, "更新持久化job异常！"),
	UPDATE_PERSISTENCE_JOB_SUCCESS(	    true, 	502, "更新成功！"),
	UPDATE_INTERNAL_JOB_IS_NULL(	    false, 	503, "更新失败，内存Job为空！"),
	UPDATE_JOB_IP_LIST_ERROR(		    false, 	504, "更新失败，服务器列表异常，请稍后再试！"),
	UPDATE_JOB_FAILURE(				    false, 	505, "系统繁忙，请稍后再试！"),
	UPDATE_JOB_SUCCESS(				    true, 	506, "更新成功！"),
	
	PULL_TASK_LIST_SUCCESS(			    true, 	600, "拉取任务列表成功！"),
	PULL_TASK_LIST_OVER(			    false, 	601, "拉取任务列表结束！"),
	PULL_TASK_GET_LOCK_FAILURE(		    false, 	602, "抢锁失败！"),
	PULL_OVER(			    			false, 	603, "拉取结束！"),
	
	DISPATCH_TASK_LIST_IS_EMPTY(				false, 	700, "任务列表是空的！"),
	DISPATCH_TASK_LIST_SERVER_DOWN(				false, 	701, "服务器宕机！"),
	DISPATCH_TASK_LIST_SERVER_DO_NOT_RESPONSE(	false, 	702, "分发任务失败！"),
	DISPATCH_TASK_LIST_NAME_IS_NULL(			false, 	770, "任务名称为空！"),
	
	QUERY_ALL_CLUSTER_FAILURE(false, 703, "获取所有集群信息异常"),
	
	QUERY_USER_CERTAIN_GROUP_FAILURE(false, 704, "获取用户组异常"),
	
	QUERY_ALL_USER_GROUP_FAILURE(false, 713, "获取用户所有组异常"),
	
	INSERT_USER_GROUP_FAILURE(false, 705, "数据库创建用户组异常"),
	
	DELE_GROUP_ERROR(false , 706, "删除组异常!"),
	
	QUERY_MONITOR_ERROR(false , 707, "查询监控配置异常!"),
	MONITOR_NOTSET(true , 708, "监控没有设置!"),
	MONITOR_UPDATE_ERROR(false, 709, "更新监控异常!"),
	
	QUERY_JOB_INSTANCE_ERROR(false, 710, "查询Job状态异常!"),
	
	QUERY_TASK_SNAPSHOT_PROGRESS_ERROR(false, 711, "查询进度异常!"),
	QUERY_TASK_SNAPSHOT_DETAIL_PROGRESS_ERROR(false, 712, "查询详细进度异常!"),
	
	QUERY_USER_ERROR(false, 714, "查询用户异常!"),
	USER_NOT_EXISTS(false, 715, "该用户还没有开通DTS服务!"),
	USER_NOT_OWN_RESOURCE(false, 716, "资源不属于该用户!"),
	
	CHANGE_JOB_STATUS_ERROR(false, 717, "改变状态失败!"),

	CREATE_USER_GROUP_ERROR(false , 718, "创建用户和用户组关联异常!"),
	QUERY_USER_GROUP_ERROR(false , 719, "查询用户和组关系异常!"),
	DELETE_USER_GROUP_ERROR(false , 720, "删除用户组内部异常!"),

	QUERY_JOB_RELATION_ERROR(false, 721, "查询Job关系依赖异常!"),
	CREATE_JOB_RELATION_ERROR(false, 722, "创建Job关系依赖异常!"),
	DELETE_JOB_RELATION_ERROR(false, 723, "删除Job关系依赖异常!"),
    NO_SUCH_RELATION(           false, 724, "不存在这样的依赖关系!"),

    NO_NEED_CALL_DEPENDENCY(           false, 725, "已经调用过的不需要重新调用!"),

	DEPENDENCE_JOB_WAIT(false, 726, "依赖Job等待被调用!"),

    UPDATE_JOB_RELATION_ERROR(false, 727, "更新Job依赖表异常!"),
	
	HEART_BEAT_CHECK_SUCCESS(	true, 	800, "I am alive !"),
	HEART_BEAT_CHECK_FAILURE(	false, 	801, "I am over !"),
	HEART_BEAT_CHECK_EXIT(		false, 	802, "I am exit !"),
	HEART_BEAT_CHECK_CRASH(		false, 	803, "I am crash !"),
	
	SET_GLOBAL_ARGUMENTS_NULL(			false, 901, "参数为空"),
	SET_GLOBAL_OBJECT_TO_BYTES_FAILURE(	false, 902, "序列化全局变量失败"),
	SET_GLOBAL_SERVER_DOWN(				false, 903, "服务器宕机！"),
	SET_GLOBAL_FAILURE(					false, 904, "设置全局变量失败"),
	
	FIRE_JOB_WORKING_FAILURE(					false, 1001, "触发失败，原因：该Job正在运行过程中！"),
	FIRE_JOB_NO_CLIENT_FAILURE(					false, 1002, "触发失败，原因：No client available!"),
	FIRE_JOB_LOAD_INSTANCE_FAILURE(				false, 1003, "触发失败，原因：获取实例异常!"),
	FIRE_JOB_INSTANCE_STATUS_ERROR(				false, 1004, "触发失败，原因：实例状态异常!"),
	FIRE_JOB_ACQUIRE_LOCK_FAILURE(				false, 1005, "触发失败，原因：抢锁，获取执行权限失败!"),
	FIRE_JOB_LOAD_INSTANCE_FAILURE_AFTER_LOCK(	false, 1006, "触发失败，原因：抢锁成功后获取实例异常!"),
	FIRE_JOB_INSTANCE_STATUS_ERROR_AFTER_LOCK(	false, 1007, "触发失败，原因：抢锁成功后实例状态异常!"),
	FIRE_JOB_INIT_ROOT_TASK_ERROR(				false, 1008, "触发失败，原因：初始化根任务失败!"),
	FIRE_JOB_EXECUTE_TASK_ERROR(				false, 1009, "触发失败，原因：触发客户端失败!"),

    SDK_IO_ERROR(false, 1003, "URL访问异常"),
	
	CONNECT_ACCESS_FAILURE(					false, 1101, "accessKey or groupId is not correct!"),

    INNER_ERROR(false, 1102, "系统内部错误!"),

    QUERY_DESIGNATEDMATCHINE_ERROR(false, 1110, "查询指定机器失败"),

    DELETE_DESIGNATEDMATCHINE_ERROR(false, 1111, "删除原有的指定机器关系失败"),

    INSERT__DESIGNATEDMATCHINE_ERROR(false, 1112, "更新机器JOB关系失败"),
    
    CAN_NOT_FIND_JOB_BACKUP_SERVER_LIST_ERROR(false, 1113, "can not find job backup server list"),
    
    PUSH_JOB_TYPE_ERROR(			false, 1201, "push job type error"),
    PUSH_UNIT_MAP_IS_EMPTY_ERROR(	false, 1202, "executorUnitMap is empty error"),
    PUSH_UNIT_IS_NULL_ERROR(		false, 1203, "executorUnit is null error");
	
	private boolean success;
	
	private int code;
	
	private String information;

	private ResultCode(boolean success, int code, String information) {
		this.success = success;
		this.code = code;
		this.information = information;
	}
	
	/**
	 * 重写toString方法
	 */
	public String toString() {
		return success + BLANK + code + BLANK + information;
	}
	
	/**
	 * json转换成对象
	 * @param json
	 * @return
	 */
	public static ResultCode newInstance(String json) {
		return RemotingSerializable.fromJson(json, ResultCode.class);
	}
	
	/**
	 * 将对象转换成字符串
	 * @return
	 */
	public String toJsonString() {
		return RemotingSerializable.toJson(this, false);
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

}
