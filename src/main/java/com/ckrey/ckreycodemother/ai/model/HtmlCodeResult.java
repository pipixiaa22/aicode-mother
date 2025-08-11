package com.ckrey.ckreycodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HtmlCodeResult {


    @Description("HTML代码")
    private String htmlCode;
    @Description("描述")
    private String description;



    public HtmlCodeResult(String htmlCode) {
        this.htmlCode = htmlCode;
    }
}
