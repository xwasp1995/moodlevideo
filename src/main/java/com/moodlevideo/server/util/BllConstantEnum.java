package com.moodlevideo.server.util;

/**
 * 错误异常CODE定义
 * 
 * @author wxb
 * 
 */
public enum BllConstantEnum {

    /** * 操作成功 */
    RESCODE_0(200, null),
    /** *服务器发生异常 */
    RESCODE_1(5000, "服务器端内部逻辑错误"),
    RESCODE_2(5001, "三方服务器响应异常"),
    /** *请求参数错误或消息体解析失败 */
    RESCODE_3(2000, "请求参数错误或消息体解析失败"),
    /** *操作失败! */
    RESCODE_4(3, "操作失败!"),
    RESCODE_5(5, "请求不符合规范"),
    RESCODE_6(6, "服务器限流"),
    RESCODE_7(7, "服务器响应超时"),
    RESCODE_11(11, "未检索到数据"),
    
    RESCODE_21(2001, "信息验证失败"),
    RESCODE_22(2002, "信息录入失败"),
    RESCODE_23(2003, "信息修改失败"),
    RESCODE_24(2004, "信息删除失败"),
    ;

    /**
     * 枚举的值
     * */
    private int code;

    /**
     * 枚举的中文描述
     * */
    private String desc;


    public static String getDesc(int code) {
        for (BllConstantEnum b : BllConstantEnum.values()) {
            if (b.code == code) {
                return b.desc;
            }
        }
        return "";
    }


    /**
     * 匹配提示码 未匹配的返回错误码1
     * @param code
     * @return
     */
    public static BllConstantEnum getBllConstantEnum(long code) {
        for (BllConstantEnum b : BllConstantEnum.values()) {
            if (b.code == code) {
                return b;
            }
        }
        return BllConstantEnum.RESCODE_1;
    }


    private BllConstantEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public int getCode() {
        return code;
    }


    public void setCode(int code) {
        this.code = code;
    }


    public String getDesc() {
        return desc;
    }


    public void setDesc(String desc) {
        this.desc = desc;
    }

}
