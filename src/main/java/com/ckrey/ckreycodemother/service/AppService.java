package com.ckrey.ckreycodemother.service;

import com.ckrey.ckreycodemother.generator.Codegen;
import com.ckrey.ckreycodemother.model.dto.app.AppQueryRequest;
import com.ckrey.ckreycodemother.model.entity.User;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;
import com.ckrey.ckreycodemother.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ckrey.ckreycodemother.model.entity.App;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author ckrey
 * @since 2025-08-12
 */
public interface AppService extends IService<App> {

    AppVO getAppVo(App app);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVoList(List<App> appList);


    /**
     * 通过对话生成代码
     * @param userMessage
     * @param appId
     * @param
     * @return
     */
    Flux<String> chatToGenCode(String userMessage, Long appId, User loginUser);

    /**
     * 部署应用
     * @param appId
     * @param loginUser
     * @return
     */
    String deployApp(Long appId,User loginUser);


    void generateAppScreenshotAsync(long appId, String webUrl);
}
