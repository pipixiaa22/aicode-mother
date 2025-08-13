package com.ckrey.ckreycodemother.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class CodeFileSaverTemplate<T> {
    private static final String SAVE_DIR = System.getProperty("user.dir") + "/tmp/code_output";


    private void revalidate(T result) {
        if (result == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数检验为空");
        }
    }


    protected abstract void saveFiles(T result, String dir);

    //模板方法
    public final File save(T result,Long appId) {
        //验证输入
        revalidate(result);
        //定义保存目录
        String dir = buildUniqueDir(appId);
        //保存文件
        saveFiles(result, dir);
        //返回目录对象
        return new File(dir);
    }


    private String buildUniqueDir(Long appId) {
        if (appId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"appId 不能为空");
        }
        String bizType = getCodeType().getValue();

        //这只是文件名字，文件路径还需要额外加上SAVE_DIR
        String uniqueDirName = StrUtil.format("{}_{}", bizType, appId);
        String dir = SAVE_DIR + File.separator + uniqueDirName;
        Path path = Paths.get(dir);
        try {
            Files.createDirectories(path);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建文件失败");
        }
        return dir;
    }


    protected void writeToFile(String dir, String fileName, String content) {
        if (StrUtil.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容为空");
        }
        String fileDir = dir + File.separator + fileName;
        FileUtil.writeString(content, fileDir, StandardCharsets.UTF_8);
    }

    protected abstract CodeGenTypeEnum getCodeType();


}
