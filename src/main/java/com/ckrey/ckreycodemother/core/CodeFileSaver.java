package com.ckrey.ckreycodemother.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ckrey.ckreycodemother.ai.model.HtmlCodeResult;
import com.ckrey.ckreycodemother.ai.model.MultiFileCodeResult;
import com.ckrey.ckreycodemother.config.MyBatisFlexConfig;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CodeFileSaver {
    //文件保存的根目录
    private static final String SAVE_DIR = System.getProperty("user.dir") + "/tmp/code_output";


    public static Path saveHtmlCodeResult(HtmlCodeResult htmlCodeResult){
        String buildDir = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        saveFile(buildDir,"index.html",htmlCodeResult.getHtmlCode());
        return Paths.get(buildDir);
    }


    public static Path saveMultiFileCodeResult(MultiFileCodeResult multiFileCodeResult){
        String buildDir = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        saveFile(buildDir,"index.html", multiFileCodeResult.getHtmlCode());
        saveFile(buildDir,"style.css", multiFileCodeResult.getCssCode());
        saveFile(buildDir,"script.js", multiFileCodeResult.getJsCode());
        return Paths.get(buildDir);
    }

    private static void saveFile(String dir, String fileName, String content) {
        String fileDir = dir + File.separator + fileName;
        FileUtil.writeString(content, fileDir, StandardCharsets.UTF_8);
    }


    private static String buildUniqueDir(String bizType) {
        //这只是文件名字，文件路径还需要额外加上SAVE_DIR
        String uniqueDirName = StrUtil.format("{}_{}",bizType, IdUtil.getSnowflakeNextIdStr());
        String dir = SAVE_DIR + File.separator + uniqueDirName;
        Path path = Paths.get(dir);
        try {
            Files.createDirectories(path);
        }
        catch (Exception e){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"创建文件失败");
        }
        return dir;
    }

}
