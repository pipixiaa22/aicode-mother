package com.ckrey.ckreycodemother.service;

import com.ckrey.ckreycodemother.model.dto.user.UserRegisterRequest;
import com.ckrey.ckreycodemother.model.dto.user.UserQueryRequest;
import com.ckrey.ckreycodemother.model.vo.LoginUserVO;
import com.ckrey.ckreycodemother.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ckrey.ckreycodemother.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author ckrey
 * @since 2025-08-06
 */
public interface UserService extends IService<User> {

    long registerUser(UserRegisterRequest registerRequest);

    String getEncrpytPassword(String userPassword);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    UserVO getUserVo(User user);


    List<UserVO> getUserVoList(List<User> userList);

    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);
}
