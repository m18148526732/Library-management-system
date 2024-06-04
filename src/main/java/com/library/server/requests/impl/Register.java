package main.java.com.library.server.requests.impl;

import main.java.com.library.common.entity.Entity;
import main.java.com.library.common.entity.impl.User;
import main.java.com.library.common.network.RequestPack;
import main.java.com.library.common.network.ResponsePack;
import main.java.com.library.common.network.handlers.RequestHandler;
import main.java.com.library.common.network.handlers.ResponseHandler;
import main.java.com.library.server.requests.Request;
import main.java.com.library.server.service.impl.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Register implements Request<User> {
    private static final Logger logger = LoggerFactory.getLogger(Register.class);
    private final String action = "register";
    private final UserService userService = new UserService();

    /**
     * 返回与此请求相关的操作。
     *
     * @return 表示要执行的操作的字符串
     */
    @Override
    public String getAction() {
        return action;
    }

    /**
     * 处理请求并返回响应对象。
     *
     * @param requestPack 包含请求数据的对象,成功时包含具体的请求数据对象(User),失败时为null
     * @return 包含响应数据的对象
     */
    @Override
    public ResponsePack<User> handle(RequestPack<? extends Entity> requestPack) {
        try {
            Entity entity = RequestHandler.unPackRequest(requestPack);
            if (!(entity instanceof User user)) {
                return ResponseHandler.packResponse(action, false, "Invalid request type, expected User", null);
            }
            // 调用 UserService 的 registerUser 方法进行注册
            String result = userService.registerUser(user);
            user = userService.get(user.getId());
            logger.info("Registration result: {}", result);
            if (result.startsWith("Success")) {
                return ResponseHandler.packResponse(action, true, result, user);
            } else {
                return ResponseHandler.packResponse(action, false, result, null);
            }

        } catch (Exception e) {
            logger.error("Registration failed for user: {}", requestPack, e);
            return ResponseHandler.packResponse(action, false, "Internal server error", null);
        }
    }
}