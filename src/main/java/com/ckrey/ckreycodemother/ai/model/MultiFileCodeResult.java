package com.ckrey.ckreycodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public class MultiFileCodeResult {
    @Description("HTML代码")
    private String htmlCode;
    @Description("JAVASCRIPT代码")
    private String jsCode;
    @Description("CSS代码")
    private String cssCode;
    @Description("描述")
    private String description;

}
