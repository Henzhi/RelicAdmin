package com.relic.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {

    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String ACCOUNT_LOCKED = "账号被锁定";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";

    public static final String ALREADY_EXISTS = "账号已存在";

    // 角色与权限
    public static final String ROLE_NOT_FOUND = "角色不存在";
    public static final String PERMISSION_DENIED = "权限不足";
    public static final String ROLE_NAME_EXISTS = "角色名称已存在";

    // 敏感词
    public static final String SENSITIVE_WORD_EXISTS = "敏感词已存在";
    public static final String SENSITIVE_WORD_NOT_FOUND = "敏感词不存在";

    // 审核
    public static final String AUDIT_CONTENT_NOT_FOUND = "审核内容不存在";
    public static final String AUDIT_REJECT_REASON_REQUIRED = "拒绝时必须填写原因";

    // 备份与恢复
    public static final String BACKUP_FAILED = "备份失败";
    public static final String RESTORE_CONFIRM_REQUIRED = "数据恢复需要二次确认";

    // 文物数据
    public static final String ARTIFACT_NOT_FOUND = "文物不存在";
    public static final String IMPORT_FILE_FORMAT_ERROR = "导入文件格式错误";
}
