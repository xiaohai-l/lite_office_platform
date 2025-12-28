package io.github.xiaohai.lite_office_platform.common;

public class R<T> {
    private Integer code; // 状态码：200成功，其他数字代表各种错误
    private String msg;   // 提示信息
    private T data;       // 返回的数据

    // 快速生成成功和失败结果的方法


    // 成功，无数据
    public static <T> R<T> success() {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("操作成功");
        return r;
    }
    // 成功，有数据
    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }
    // 失败
    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.setCode(500); // 500通常代表服务器内部错误，你可以定义自己的错误码体系
        r.setMsg(msg);
        return r;
    }

    // 失败，自定义状态码,第一个参数用来告诉前端错误码，第二个参数用来表明错误信息
    public static <T> R<T> error(Integer code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }


    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}

