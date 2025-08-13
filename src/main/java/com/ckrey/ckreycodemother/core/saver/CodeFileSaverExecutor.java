package com.ckrey.ckreycodemother.core.saver;

import com.ckrey.ckreycodemother.ai.model.HtmlCodeResult;
import com.ckrey.ckreycodemother.ai.model.MultiFileCodeResult;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;

import java.io.File;

public class CodeFileSaverExecutor {
    private static final HtmlCodeFileSaverTemplate htmlCodeFileSaver = new HtmlCodeFileSaverTemplate();

    private static final MultFileCodeFileSaverTemplate multFileCodeSaver = new MultFileCodeFileSaverTemplate();


    public static File executorSaver(Object result, CodeGenTypeEnum codeGenTypeEnum,Long appId) {
        return switch (codeGenTypeEnum) {
            case MULTI_FILE -> multFileCodeSaver.save(((MultiFileCodeResult) result),appId);
            case HTML -> htmlCodeFileSaver.save((HtmlCodeResult) result,appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的保存类型");
        };
    }


}
