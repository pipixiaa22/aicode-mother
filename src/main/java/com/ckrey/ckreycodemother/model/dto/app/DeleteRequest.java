package com.ckrey.ckreycodemother.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class DeleteRequest implements Serializable {

    private Long id;

    /**
     * 应用名称
     */
    private String appName;


}
