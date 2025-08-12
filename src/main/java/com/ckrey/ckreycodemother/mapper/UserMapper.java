package com.ckrey.ckreycodemother.mapper;

import com.mybatisflex.core.BaseMapper;
import com.ckrey.ckreycodemother.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 映射层。
 *
 * @author ckrey
 * @since 2025-08-06
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
