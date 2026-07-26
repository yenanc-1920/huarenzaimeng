package com.huarenzaimeng.common.exception;

import com.huarenzaimeng.common.result.ResultCode;
import lombok.Getter;

@Getter
public class ReloadlyException extends RuntimeException {

    private final int code;
    private final String reloadlyErrorCode;

    public ReloadlyException(String message, String reloadlyErrorCode) {
        super(message);
        this.code = ResultCode.RELOADLY_ERROR.getCode();
        this.reloadlyErrorCode = reloadlyErrorCode;
    }

    public ReloadlyException(ResultCode resultCode, String reloadlyErrorCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.reloadlyErrorCode = reloadlyErrorCode;
    }
}
