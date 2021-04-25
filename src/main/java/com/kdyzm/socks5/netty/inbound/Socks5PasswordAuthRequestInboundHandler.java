package com.kdyzm.socks5.netty.inbound;

import com.kdyzm.socks5.netty.properties.UsersProperties;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kdyzm
 * @date 2021/4/25
 */
@Slf4j
@AllArgsConstructor
public class Socks5PasswordAuthRequestInboundHandler extends SimpleChannelInboundHandler<DefaultSocks5PasswordAuthRequest> {

    private final UsersProperties usersProperties;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5PasswordAuthRequest msg) throws Exception {
        log.debug("用户名：{}，密码：{}", msg.username(), msg.password());
        //认证成功
        if (usersProperties.getUsers().get(msg.username()).equals(msg.password().trim())) {
            Socks5PasswordAuthResponse passwordAuthResponse = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS);
            ctx.writeAndFlush(passwordAuthResponse);
            return;
        }
        Socks5PasswordAuthResponse passwordAuthResponse = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE);
        //发送鉴权失败消息，完成后关闭channel
        ctx.writeAndFlush(passwordAuthResponse).addListener(ChannelFutureListener.CLOSE);
    }
}
