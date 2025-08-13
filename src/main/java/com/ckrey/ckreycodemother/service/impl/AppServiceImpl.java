package com.ckrey.ckreycodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.model.dto.app.AppQueryRequest;
import com.ckrey.ckreycodemother.model.entity.User;
import com.ckrey.ckreycodemother.model.vo.AppVO;
import com.ckrey.ckreycodemother.model.vo.UserVO;
import com.ckrey.ckreycodemother.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ckrey.ckreycodemother.model.entity.App;
import com.ckrey.ckreycodemother.mapper.AppMapper;
import com.ckrey.ckreycodemother.service.AppService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

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
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public List<AppVO> getAppVoList(List<App> appList) {
        //这里要防止重复查询，因为appvo里面需要存入user，而一个user可以对应多个app，避免重复查询，先将userid去重
        Set<Long> userId = appList.stream().map(App::getUserId).collect(Collectors.toSet());

        Map<Long, UserVO> userVOMap = userService.listByIds(userId).stream().collect(Collectors.toMap(User::getId, userService::getUserVo));

        return appList.stream().map((app) -> {
            AppVO appVo = new AppVO();
            BeanUtil.copyProperties(app,appVo);
            appVo.setUser(userVOMap.get(app.getUserId()));
            return appVo;
        }).toList();

    }

}
