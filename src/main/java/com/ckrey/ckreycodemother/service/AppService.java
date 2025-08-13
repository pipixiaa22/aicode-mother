package com.ckrey.ckreycodemother.service;

import com.ckrey.ckreycodemother.model.dto.app.AppQueryRequest;
import com.ckrey.ckreycodemother.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ckrey.ckreycodemother.model.entity.App;

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
}
