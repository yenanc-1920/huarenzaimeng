package com.huarenzaimeng.module.user.controller;

import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.user.entity.User;
import com.huarenzaimeng.module.user.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WxUserControllerTest {

    private UserMapper userMapper;
    private HttpServletRequest request;
    private WxUserController controller;
    private User user;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        request = mock(HttpServletRequest.class);
        controller = new WxUserController(userMapper);
        user = new User();
        user.setId(1L);
        user.setOpenid("openid_test");
        user.setUserType("VISITOR");
        when(request.getAttribute("userId")).thenReturn(1L);
        when(userMapper.selectById(1L)).thenReturn(user);
    }

    @Test
    void loginReturnsCurrentVisitor() {
        Result<User> result = controller.login(request);
        assertEquals(0, result.getCode());
        assertEquals("openid_test", result.getData().getOpenid());
    }

    @Test
    void profileReturnsCurrentUser() {
        Result<User> result = controller.profile(request);
        assertSame(user, result.getData());
    }

    @Test
    void updateProfilePromotesVisitorToMember() {
        Result<User> result = controller.updateProfile(request,
                new WxUserController.UpdateProfileRequest("测试用户", "https://example.com/avatar.png"));
        assertEquals("MEMBER", result.getData().getUserType());
        assertEquals("测试用户", result.getData().getNickname());
        verify(userMapper).updateById(user);
    }
}