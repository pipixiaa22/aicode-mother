package com.ckrey.ckreycodemother.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ckrey.ckreycodemother.annotaion.AuthCheck;
import com.ckrey.ckreycodemother.common.BaseResponse;
import com.ckrey.ckreycodemother.model.dto.app.*;
import com.ckrey.ckreycodemother.common.ResponseUtil;
import com.ckrey.ckreycodemother.constant.UserConstant;
import com.ckrey.ckreycodemother.core.constant.AppConstant;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.exception.ThrowUtils;
import com.ckrey.ckreycodemother.model.entity.User;
import com.ckrey.ckreycodemother.model.enums.CodeGenTypeEnum;
import com.ckrey.ckreycodemother.model.vo.AppVO;
import com.ckrey.ckreycodemother.service.ChatHistoryService;
import com.ckrey.ckreycodemother.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.aop.support.AopUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ckrey.ckreycodemother.model.entity.App;
import com.ckrey.ckreycodemother.service.AppService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用 控制层。
 *
 * @author ckrey
 * @since 2025-08-12
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Resource
    private AppService appService;

    @Resource
    private UserService userService;

    private ChatHistoryService chatHistoryService;


    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenType(@RequestParam Long appId, @RequestParam String message, HttpServletRequest request) {
        if (appId == null || appId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "appid错误");
        }
        if (StrUtil.isBlank(message)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提示词为空");
        }

        User loginUser = userService.getLoginUser(request);
        Flux<String> stringFlux = appService.chatToGenCode(message, appId, loginUser);

        return stringFlux.map((str) -> {
            Map<String, String> map = Map.of("d", str);
            String jsonData = JSONUtil.toJsonStr(map);
            return ServerSentEvent.<String>builder().data(jsonData).build();
        }).concatWith(Mono.just(
                ServerSentEvent.<String>builder().event("done").data("").build()
        ));

    }

    //返回部署的地址即可
    @PostMapping("/deploy")
    public BaseResponse<String> deployApp(@RequestBody AppDeployRequest appDeployRequest, HttpServletRequest request) {
        if (appDeployRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long appId = appDeployRequest.getAppId();
        User loginUser = userService.getLoginUser(request);

        String deployDir = appService.deployApp(appId, loginUser);
        return ResponseUtil.success(deployDir);
    }


    /**
     * 创建应用
     *
     * @param appAddRequest 创建应用请求
     * @param request       请求
     * @return 应用 id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 参数校验
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化 prompt 不能为空");


        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 构造入库对象
        App app = new App();
        BeanUtil.copyProperties(appAddRequest, app);
        app.setUserId(loginUser.getId());
        // 应用名称暂时为 initPrompt 前 12 位
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        // 暂时设置为多文件生成
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());
        // 插入数据库

        boolean result = appService.save(app);

        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResponseUtil.success(app.getId());
    }


    /**
     * 更新应用（用户只能更新自己的应用名称）
     *
     * @param appUpdateRequest 更新请求
     * @param request          请求
     * @return 更新结果
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        if (appUpdateRequest == null || appUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        //代码id
        long id = appUpdateRequest.getId();
        // 判断是否存在
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可更新
        if (!oldApp.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        App app = new App();
        app.setId(id);
        app.setAppName(appUpdateRequest.getAppName());
        // 设置编辑时间
        app.setEditTime(Timestamp.valueOf(LocalDateTime.now()));
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResponseUtil.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        App app = appService.getById(deleteRequest.getId());

        if (app == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无权限");
        }
        //TODO这里重写了removeid方法，在里面也删除了关联的历史记录
        boolean removed = appService.removeById(app.getId());


        if (!removed) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除失败");
        }

        return ResponseUtil.success(true);
    }

    @GetMapping("/get/vo")
    public BaseResponse<AppVO> getAppVo(Long id) {
        ThrowUtils.throwIf(id < 0, ErrorCode.PARAMS_ERROR);
        App app = appService.getById(id);

        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);

        return ResponseUtil.success(appService.getAppVo(app));
    }

    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AppVO>> listMyAppVoByPage(@RequestBody AppQueryRequest appQueryRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        appQueryRequest.setUserId(loginUser.getId());
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询20条数据");
        int pageNum = appQueryRequest.getPageNum();
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);

        Page<App> page = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        //将app转为appvo
        List<AppVO> appVoList = appService.getAppVoList(page.getRecords());

        Page<AppVO> result = new Page<>(pageNum, pageSize, page.getTotalRow());
        result.setRecords(appVoList);

        return ResponseUtil.success(result);
    }


    @PostMapping("/good/list/page/vo")
    public BaseResponse<Page<AppVO>> listGoodAppVoByPage(@RequestBody AppQueryRequest appQueryRequest) {
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询20条数据");
        int pageNum = appQueryRequest.getPageNum();
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);

        Page<App> page = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        //将app转为appvo
        List<AppVO> appVoList = appService.getAppVoList(page.getRecords());

        Page<AppVO> result = new Page<>(pageNum, pageSize, page.getTotalRow());
        result.setRecords(appVoList);

        return ResponseUtil.success(result);
    }


    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteByAdmin(DeleteRequest deleteRequest) {
        Long id = deleteRequest.getId();
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        boolean removed = appService.removeById(id);
        ThrowUtils.throwIf(!removed, ErrorCode.OPERATION_ERROR, "app数据删除失败");

//

        return ResponseUtil.success(true);
    }


    /**
     * 管理员更新应用
     *
     * @param appAdminUpdateRequest 更新请求
     * @return 更新结果
     */
    @PostMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAppByAdmin(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
        if (appAdminUpdateRequest == null || appAdminUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = appAdminUpdateRequest.getId();
        // 判断是否存在
        App oldApp = appService.getById(id);
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        App app = new App();
        BeanUtil.copyProperties(appAdminUpdateRequest, app);
        // 设置编辑时间
        app.setEditTime(Timestamp.valueOf(LocalDateTime.now()));
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResponseUtil.success(true);
    }
    /**
     * 管理员更新应用
     *
     * @param appAdminUpdateRequest 更新请求
     * @return 更新结果
     */
    /**
     * 管理员分页获取应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 应用列表
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AppVO>> listAppVOByPageByAdmin(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = appQueryRequest.getPageNum();
        long pageSize = appQueryRequest.getPageSize();
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
        // 数据封装
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVoList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResponseUtil.success(appVOPage);
    }

    /**
     * 管理员根据 id 获取应用详情
     *
     * @param id 应用 id
     * @return 应用详情
     */
    @GetMapping("/admin/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AppVO> getAppVOByIdByAdmin(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        AppVO res = appService.getAppVo(app);
        return ResponseUtil.success(res);
    }


    /**
     * 保存应用。
     *
     * @param app 应用
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody App app) {
        return appService.save(app);
    }

    /**
     * 根据主键删除应用。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return appService.removeById(id);
    }

    /**
     * 根据主键更新应用。
     *
     * @param app 应用
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody App app) {
        return appService.updateById(app);
    }

    /**
     * 查询所有应用。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<App> list() {
        return appService.list();
    }

    /**
     * 根据主键获取应用。
     *
     * @param id 应用主键
     * @return 应用详情
     */
    @GetMapping("getInfo/{id}")
    public App getInfo(@PathVariable Long id) {
        return appService.getById(id);
    }

    /**
     * 分页查询应用。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<App> page(Page<App> page) {
        return appService.page(page);
    }

}
