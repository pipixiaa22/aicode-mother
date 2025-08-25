package com.ckrey.ckreycodemother.ai.tools;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ToolManager {

    private static final Map<String, BaseTool> toolMap = new HashMap<>();

    @Resource
    private BaseTool[] baseTools;


    @PostConstruct
    public void init(){
        for (BaseTool baseTool : baseTools) {
            String toolName = baseTool.getToolName();
            toolMap.put(toolName, baseTool);
            log.info("注册工具:{}",toolName);
        }
    }

    public BaseTool getTool(String toolName){
        return toolMap.get(toolName);
    }


    public BaseTool[] getAllTools(){
        return baseTools;
    }

}
