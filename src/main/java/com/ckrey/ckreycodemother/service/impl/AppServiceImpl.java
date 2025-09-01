package com.ckrey.ckreycodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ckrey.ckreycodemother.ai.AiCodeRouterService;
import com.ckrey.ckreycodemother.ai.AiCodeRouterServiceFactory;
import com.ckrey.ckreycodemother.core.AiCodeGeneratorFacade;
import com.ckrey.ckreycodemother.core.builder.VueProjectBuilder;
import com.ckrey.ckreycodemother.constant.AppConstant;
import com.ckrey.ckreycodemother.core.handler.StreamHandlerExecutor;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.exception.ThrowUtils;
import com.ckrey.ckreycodemother.model.dto.app.AppAddRequest;
import com.ckrey.ckreycodemother.model.dto.app.AppQueryRequest;
import com.ckrey.ckreycodemother.model.entity.User;
import com.ckrey.ckreycodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;
import com.ckrey.ckreycodemother.model.vo.AppVO;
import com.ckrey.ckreycodemother.model.vo.UserVO;
import com.ckrey.ckreycodemother.service.ChatHistoryService;
import com.ckrey.ckreycodemother.service.ScreenshotService;
import com.ckrey.ckreycodemother.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ckrey.ckreycodemother.model.entity.App;
import com.ckrey.ckreycodemother.mapper.AppMapper;
import com.ckrey.ckreycodemother.service.AppService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author ckrey
 * @since 2025-08-12
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    @Resource
    private ScreenshotService screenshotService;
//
//    @Resource
//    private AiCodeRouterService aiCodeRouterService;

    @Resource
    private AiCodeRouterServiceFactory aiCodeRouterServiceFactory;

    @Override
    public AppVO getAppVo(App app) {
        if (app == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        Long userId = app.getUserId();
        AppVO appVO = new AppVO();
        if (userId != null) {
            UserVO userVo = userService.getUserVo(userService.getById(userId));
            appVO.setUser(userVo);
        }
        BeanUtil.copyProperties(app, appVO);
        return appVO;
    }


    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("\"id\"", id,id!=null)
                .like("\"appName\"", appName,appName!=null)
                .like("\"cover\"", cover,cover!=null)
                .like("\"initPrompt\"", initPrompt,initPrompt!=null)
                .eq("\"codeGenType\"", codeGenType,codeGenType!=null)
                .eq("\"deployKey\"", deployKey,deployKey!=null)
                .eq("\"priority\"", priority,priority!=null)
                .eq("\"userId\"", userId,userId!=null)
                .orderBy("\"" + sortField + "\"", "ascend".equals(sortOrder));
    }

    @Override
    public List<AppVO> getAppVoList(List<App> appList) {
        //这里要防止重复查询，因为appvo里面需要存入user，而一个user可以对应多个app，避免重复查询，先将userid去重
        Set<Long> userId = appList.stream().map(App::getUserId).collect(Collectors.toSet());

        Map<Long, UserVO> userVOMap = userService.listByIds(userId).stream().collect(Collectors.toMap(User::getId, userService::getUserVo));

        return appList.stream().map((app) -> {
            AppVO appVo = new AppVO();
            BeanUtil.copyProperties(app, appVo);
            appVo.setUser(userVOMap.get(app.getUserId()));
            return appVo;
        }).toList();

    }

    @Override
    public Flux<String> chatToGenCode(String userMessage, Long appId, User loginUser) {
        //校验参数
        if (userMessage == null || appId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //查询app信息

        App app = this.getById(appId);

        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //仅限本用户继续对话

        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }

        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum enumByValue = CodeGenTypeEnum.getEnumByValue(codeGenType);
        //第一个存的是用户消息
        boolean addUserMes = chatHistoryService.addChatMessage(appId, userMessage, ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());

        ThrowUtils.throwIf(!addUserMes, ErrorCode.OPERATION_ERROR, "存储用户消息失败");

        //发送请求到ai服务器
        //这里照样对flux流式处理
        //这里是获取ai响应
        Flux<String> stringFlux = aiCodeGeneratorFacade.codeGenerateAndSaveStream(userMessage, enumByValue, appId);

        //统一封装为执行器根据枚举来进行解析
        //对ai响应进行解析
        return streamHandlerExecutor.streamHandler(stringFlux, enumByValue, chatHistoryService, appId, loginUser);

    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        //获取app实例
        App app = this.getById(appId);
        //判断是否为当前用户
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");
        }
        log.info("开始进行部署:{}",appId);
        //检查是否已有部署deploykey,如果有,则直接在此基础上修改,如果没有生成一个并回填
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
            app.setDeployKey(deployKey);
        }
        //获取应用类型
        String codeGenType = app.getCodeGenType();
        //根据appid找到对应目录下的文件
        String output_dir = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + StrUtil.format("{}_{}", codeGenType, appId);

        File source = new File(output_dir);
        //检查路径是否存在

        if (!source.exists() || !source.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件路径错误");
        }


        String deploy_dir = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;


        //对vue工程模式进行特殊处理，上面的方法都是通用,这里只需要将构建完成的dist目录下的文件复制到对应目录即可，本身就是一个html文件

        if (codeGenType.equals(CodeGenTypeEnum.VUE_PROJECT.getValue())) {

            boolean buildProject = vueProjectBuilder.buildProject(output_dir);
            ThrowUtils.throwIf(!buildProject, ErrorCode.OPERATION_ERROR, "vue项目构建失败，请重试");
            File distDir = new File(output_dir, "dist");
            if (!distDir.exists()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "dist目录不存在");
            }
            //这里直接将dist目录复制到部署目录即可
            source = distDir;
        }

        File target = new File(deploy_dir);

        try {
            FileUtil.copyContent(source, target, true);
            log.info("文件复制完成，{}->{}",source,target);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
        //重新写回app,填充deployid
        app.setDeployedTime(Timestamp.valueOf(LocalDateTime.now()));
        boolean update = this.updateById(app);

        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库更新失败");
        }

        String path = StrUtil.format("{}/{}", AppConstant.CODE_DEPLOY_HOST, deployKey);
        //异步调用截图服务并上传oss
        generateAppScreenshotAsync(appId,path);


        //返回url地址
        return path;
    }

    @Override
    public void generateAppScreenshotAsync(long appId, String webUrl) {

        Thread.startVirtualThread(() -> {
            String coverPath = screenshotService.generateAndUploadScreenshot(webUrl);
            App app = new App();
            app.setId(appId);
            app.setCover(coverPath);
            boolean updated = this.updateById(app);
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "数据库更新失败");
        });

    }

    @Override
    public Long createApp(AppAddRequest appAddRequest, User loginUser) {
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt),ErrorCode.PARAMS_ERROR,"初始提示词不能为空");
        App app = new App();
        app.setUserId(loginUser.getId());
        app.setInitPrompt(initPrompt);
        app.setAppName(initPrompt.substring(0,10));
        //通过ai路由判断本次提示词是哪种模式
//        aiCodeRouterService.router()
        //每次请求获取新的实例
        AiCodeRouterService aiCodeRouterService = aiCodeRouterServiceFactory.createAiCodeRouterService();
        CodeGenTypeEnum codeGenTypeEnum = aiCodeRouterService.router(initPrompt);
        app.setCodeGenType(codeGenTypeEnum.getValue());

        boolean save = this.save(app);
        ThrowUtils.throwIf(!save,ErrorCode.OPERATION_ERROR,"app实例插入失败");
        log.info("app实例生成：{}",app.getId());
        return app.getId();
    }


    /**
     * 关联删除app历史对话记录
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        try {
            chatHistoryService.deleteChatMessage((Long) id);
        } catch (Exception e) {
            log.error("删除历史对话记录失败,{}", e.getMessage());
        }

        return super.removeById(id);
    }
}
