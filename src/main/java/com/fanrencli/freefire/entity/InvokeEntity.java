package com.fanrencli.freefire.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InvokeEntity {
    private String className;
    private String methodName;
    private String requestParamJsonString;
    private String responseParamJsonString;
    private Integer invokeDeepLevel;
    private List<InvokeEntity> children = new ArrayList<>(10);
}
