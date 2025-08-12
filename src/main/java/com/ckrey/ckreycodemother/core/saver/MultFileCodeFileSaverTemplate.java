package com.ckrey.ckreycodemother.core.saver;

import com.ckrey.ckreycodemother.ai.model.MultiFileCodeResult;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;

public class MultFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {
    @Override
    protected void saveFiles(MultiFileCodeResult result, String dir) {
        writeToFile(dir, "index.html", result.getHtmlCode());
        writeToFile(dir, "script.js", result.getJsCode());
        writeToFile(dir, "style.css", result.getCssCode());
    }

    @Override
   protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }
}
