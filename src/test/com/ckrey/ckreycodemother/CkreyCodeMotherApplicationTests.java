package com.ckrey.ckreycodemother;

import cn.hutool.http.HttpUtil;
import com.ckrey.ckreycodemother.langGraph4j.model.ImageResource;
import com.ckrey.ckreycodemother.langGraph4j.state.ImageCategoryEnum;
import com.ckrey.ckreycodemother.langGraph4j.tools.LogoGeneratorTool;
import com.ckrey.ckreycodemother.manager.CosManager;
import com.ckrey.ckreycodemother.utils.WebScreenShotUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CkreyCodeMotherApplicationTests {

    @Resource
    private LogoGeneratorTool logoGeneratorTool;

    @Resource
    private CosManager cosManager;

    @Test
    void testGenerateLogos() throws IOException {
        // 测试生成Logo
//        List<ImageResource> logos = logoGeneratorTool.generateLogos("技术公司现代简约风格Logo");
//        assertNotNull(logos);
//        ImageResource firstLogo = logos.getFirst();
        File tmp = Files.createTempFile("tmp", ".png").toFile();

        //将图片下载到本地临时目录
        HttpUtil.downloadFile("https://dashscope-result-wlcb-acdr-1.oss-cn-wulanchabu-acdr-1.aliyuncs.com/1d/94/20250828/e731159d/96174b07-1a44-4f8d-a3ea-60c46f86f733450050794.png?Expires=1756436184&OSSAccessKeyId=LTAI5tKPD3TMqf2Lna1fASuh&Signature=WCdw2EHI0EIei6H7tx8BPrLwbjE%3D"
        ,tmp);


//        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tmp)))
//        {
//            bufferedWriter.write(firstLogo.getUrl());
//        }



        cosManager.uploadFile("/picture/"+System.currentTimeMillis()+".jpg",tmp);
//        assertEquals(ImageCategoryEnum.LOGO, firstLogo.getCategory());
//        assertNotNull(firstLogo.getDescription());
//        assertNotNull(firstLogo.getUrl());
//        logos.forEach(logo ->
//                System.out.println("Logo: " + logo.getDescription() + " - " + logo.getUrl())
//        );
    }
}
