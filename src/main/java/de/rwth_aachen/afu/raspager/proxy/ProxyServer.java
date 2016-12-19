/*
 * Copyright (C) 2016 Amateurfunkgruppe der RWTH Aachen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.rwth_aachen.afu.raspager.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 *
 * @author Philipp Thiel
 */
public class ProxyServer implements Runnable {

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final String frontendHost;
    private final int frontendPort;
    private final String backendHost;
    private final int backendPort;

    public ProxyServer(String frontendHost, int frontendPort,
            String backendHost, int backendPort) {
        this.frontendHost = frontendHost;
        this.frontendPort = frontendPort;
        this.backendHost = backendHost;
        this.backendPort = backendPort;
    }

    @Override
    public void run() {
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new FrontendHandler(backendHost, backendPort));
            b.option(ChannelOption.AUTO_READ, false);

            b.connect(frontendHost, frontendPort).sync().channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
