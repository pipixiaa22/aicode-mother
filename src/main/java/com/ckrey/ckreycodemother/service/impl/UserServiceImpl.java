package com.ckrey.ckreycodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ckrey.ckreycodemother.exception.BusinessException;
import com.ckrey.ckreycodemother.exception.ErrorCode;
import com.ckrey.ckreycodemother.exception.ThrowUtils;
import com.ckrey.ckreycodemother.model.dto.user.UserRegisterRequest;
import com.ckrey.ckreycodemother.model.dto.user.UserQueryRequest;
import com.ckrey.ckreycodemother.model.enums.UserRoleEnum;
import com.ckrey.ckreycodemother.model.vo.LoginUserVO;
import com.ckrey.ckreycodemother.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ckrey.ckreycodemother.model.entity.User;
import com.ckrey.ckreycodemother.mapper.UserMapper;
import com.ckrey.ckreycodemother.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

import static com.ckrey.ckreycodemother.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author ckrey
 * @since 2025-08-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Override
    //返回的主键id
    public long registerUser(UserRegisterRequest registerRequest) {
        //校验参数是否存在
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkPassword = registerRequest.getCheckPassword();
        ThrowUtils.throwIf(StrUtil.isBlank(userAccount), new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空"));
        ThrowUtils.throwIf(StrUtil.isBlank(userPassword), new BusinessException(ErrorCode.PARAMS_ERROR));
        ThrowUtils.throwIf(StrUtil.isBlank(checkPassword), new BusinessException(ErrorCode.PARAMS_ERROR));
        //如有必要，可以对密码哈希加密
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不一致");
        }

        //判断用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(User::getUseraccount, userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户已存在");
        }


        //插入数据库
        User user = new User();

        user.setUseraccount(userAccount);
        user.setUserpassword(getEncrpytPassword(userPassword));
        user.setUsername("无名");
        user.setUserrole(UserRoleEnum.USER.getRole());
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
        }
        //返回主键id

        return user.getId();
    }

    @Override
    public String getEncrpytPassword(String userPassword) {
        String salt = "ckrey";
        //有些盐值是不固定的，所以要从数据库当中取出后几位固定的数字，然后重新与传过来的密码进行加密处理然后比对
        //这里为了方便直接固定盐值
        return DigestUtils.md5DigestAsHex((userPassword + salt).getBytes());
    }


    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }


    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {


        QueryWrapper wrapper = new QueryWrapper().eq(User::getUseraccount, userAccount).eq(User::getUserpassword, getEncrpytPassword(userPassword));

        User user = this.mapper.selectOneByQuery(wrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户名或密码错误");
        }

        LoginUserVO loginUserVO = getLoginUserVO(user);

        request.getSession().setAttribute(USER_LOGIN_STATE, user.getId());

        return loginUserVO;
    }


    @Override
    public User getLoginUser(HttpServletRequest request) {
        Long id = (Long) request.getSession().getAttribute(USER_LOGIN_STATE);

        if (id == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }


        return this.getById(id);

    }


    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVo(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVoList(List<User> userList) {
        return userList.stream().map(this::getUserVo).toList();
    }


    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq(User::getId, id, id!=null)
                .eq(User::getUserrole, userRole,StrUtil.isNotBlank(userRole))
                .like(User::getUseraccount, userAccount, StrUtil.isNotBlank(userAccount))
                    .like(User::getUsername, userName, StrUtil.isNotBlank(userName))
                .like(User::getUserprofile, userProfile, StrUtil.isNotBlank(userProfile))
                .orderBy(sortField, "ascend".equals(sortOrder));
    }



}
