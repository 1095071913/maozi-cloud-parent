package com.maozi.common.result.error.code;

public class SystemErrorCode extends AbstractBaseErrorCode{

    //业务异常默认码
    public static final Integer BUSINESS_ERROR_DEFAULT_CODE = 400;

    //系统异常默认码
    public static final Integer SYSTEM_ERROR_DEFAULT_CODE = 500;

    //限流异常默认码
    public static final Integer CURRENT_LIMITING_ERROR_DEFAULT_CODE = 429;

    //系统最大错误码
    public static final Integer MAX_SYSTEM_ERROR_CODE = 99999;

    //默认异常
    public static final ErrorCode DEFAULT_ERROR = new ErrorCode(SYSTEM_ERROR_DEFAULT_CODE,"服务网络异常");

    //系统异常
    public final static ErrorCode NOT_EXIST_CODE_ERROR = new ErrorCode(0,"服务网络异常","没有此编码");

    public final static ErrorCode SERVICE_RPC_ERROR = new ErrorCode(1,"服务网络异常","服务调用失败");

    public final static ErrorCode SERVICE_NOT_EXIST_ERROR = new ErrorCode(2,"服务网络异常","服务不存在");

    public final static ErrorCode NOT_SET_RESPONSE_ERROR = new ErrorCode(3,"服务网络异常","未设置资源返回类型");

    public final static ErrorCode PARAM_ERROR = new ErrorCode(BUSINESS_ERROR_DEFAULT_CODE,"参数错误");

    public final static ErrorCode USER_AUTH_ERROR = new ErrorCode(401,"用户认证授权失败");

    public final static ErrorCode MALICE_REQUEST_ERROR = new ErrorCode(402,"恶意请求");

    public final static ErrorCode PERMISSION_ERROR = new ErrorCode(403,"权限不足");

    public final static ErrorCode NOT_RESOURCE_ERROR = new ErrorCode(404,"接口不存在");

    public final static ErrorCode REQUEST_METHOD_ERROR = new ErrorCode(405,"请求方法错误");

    public final static ErrorCode REQUEST_FORMAT_ERROR = new ErrorCode(415,"请求格式错误");

    public final static ErrorCode CURRENT_LIMITING_ERROR = new ErrorCode(CURRENT_LIMITING_ERROR_DEFAULT_CODE,"请求限流中");

    public final static ErrorCode SYSTEM_ERROR = DEFAULT_ERROR;


    //业务异常
    public final static ErrorCode FORBIDDEN_ERROR = new ErrorCode(100000,"已被禁用");

    public final static ErrorCode DATA_NOT_EXIST_ERROR = new ErrorCode(100001,"资源不存在");

    public final static ErrorCode DATA_EXIST_ERROR = new ErrorCode(100002,"资源已存在");

    //... 999999

}
