package com.test.json.model;

import lombok.Data;

@Data
public class APIResponse<T> {
    private String code;
    private String message;
    private String debugMessage;
}
