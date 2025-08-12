package com.ckrey.ckreycodemother.core.saver;

import com.ckrey.ckreycodemother.ai.model.HtmlCodeResult;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;

import java.nio.file.Files;
import java.nio.file.Paths;

public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected void saveFiles(HtmlCodeResult result, String dir) {
        writeToFile(dir, "index.html", result.getHtmlCode());
    }

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }
}
