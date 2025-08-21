package com.ckrey.ckreycodemother.core.builder;

import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class VueProjectBuilder {


    public void buildProjectAsync(String projectPath){

        Thread.ofVirtual().name("vue_build_"+System.currentTimeMillis()).start(()->{
            try {
                buildProject(projectPath);
            }
            catch (Exception e){
                log.error("构建异步线程发生异常，{}",e.getMessage());
            }
        });
    }


    /**
     * 构建 Vue 项目
     *
     * @param projectPath 项目根目录路径
     * @return 是否构建成功
     */
    public boolean buildProject(String projectPath) {
        File projectDir = new File(projectPath);
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            log.error("项目目录不存在: {}", projectPath);
            return false;
        }
        // 检查 package.json 是否存在
        File packageJson = new File(projectDir, "package.json");
        if (!packageJson.exists()) {
            log.error("package.json 文件不存在: {}", packageJson.getAbsolutePath());
            return false;
        }
        log.info("开始构建 Vue 项目: {}", projectPath);
        // 执行 npm install
        if (!executeNpmInstall(projectDir)) {
            log.error("npm install 执行失败");
            return false;
        }
        // 执行 npm run build
        if (!executeNpmBuild(projectDir)) {
            log.error("npm run build 执行失败");
            return false;
        }
        // 验证 dist 目录是否生成
        File distDir = new File(projectDir, "dist");
        if (!distDir.exists()) {
            log.error("构建完成但 dist 目录未生成: {}", distDir.getAbsolutePath());
            return false;
        }
        log.info("Vue 项目构建成功，dist 目录: {}", distDir.getAbsolutePath());
        return true;
    }


    private boolean executeNpmInstall(File projectDir){
        log.info("执行npm install 命令");
        return executeCommand(projectDir, "npm.cmd install", 300);
    }


    private boolean executeNpmBuild(File projectDir){
        log.info("执行npm run build 命令");
        return executeCommand(projectDir,"npm.cmd run build",150);
    }


    private boolean executeCommand(File file, String command, int timeout) {

        try {
            String[] commands = command.split("\\s+");

            Process process = new ProcessBuilder(commands).directory(file).start();

            boolean finished = process.waitFor(timeout, TimeUnit.SECONDS);

            if (!finished) {
                log.error("命令执行超时");
                process.destroyForcibly();
                return false;
            }

            int exitValue = process.exitValue();

            if (exitValue == 0) {
                log.info("命令正常执行,{}", command);
                return true;
            } else {
                log.error("命令执行失败，退出码：{}", exitValue);
                return false;
            }
        } catch (Exception e) {
            log.error("执行失败,命令为：{},原因为：{}", command, e.getMessage());
            return false;
        }


    }

    public static void main(String[] args) {
        VueProjectBuilder vueProjectBuilder = new VueProjectBuilder();
        vueProjectBuilder.executeNpmInstall(new File("E:\\blog\\aicode-mother\\tmp\\code_output\\vue_project_315246425923108864"));
    }

}
