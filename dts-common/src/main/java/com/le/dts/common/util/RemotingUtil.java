/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.le.dts.common.util;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.helper.RemotingHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.le.dts.common.constants.Constants;
import com.le.dts.common.helper.RemotingHelper;


/**
 * 网络相关方法
 * 
 * @author shijia.wxr<vintage.wang@gmail.com>
 * @since 2013-7-13
 */
public class RemotingUtil implements Constants {
    
    private static final Log log = LogFactory.getLog(RemotingUtil.class);
    public static final String OS_NAME = System.getProperty("os.name");

    private static boolean isLinuxPlatform = false;
    private static boolean isWindowsPlatform = false;

    static {
        if (OS_NAME != null && OS_NAME.toLowerCase().indexOf("linux") >= 0) {
            isLinuxPlatform = true;
        }

        if (OS_NAME != null && OS_NAME.toLowerCase().indexOf("windows") >= 0) {
            isWindowsPlatform = true;
        }
    }


    public static boolean isLinuxPlatform() {
        return isLinuxPlatform;
    }


    public static boolean isWindowsPlatform() {
        return isWindowsPlatform;
    }


    public static Selector openSelector() throws IOException {
        Selector result = null;
        // 在linux平台，尽量启用epoll实现
        if (isLinuxPlatform()) {
            try {
                final Class<?> providerClazz = Class.forName("sun.nio.ch.EPollSelectorProvider");
                if (providerClazz != null) {
                    try {
                        final Method method = providerClazz.getMethod("provider");
                        if (method != null) {
                            final SelectorProvider selectorProvider = (SelectorProvider) method.invoke(null);
                            if (selectorProvider != null) {
                                result = selectorProvider.openSelector();
                            }
                        }
                    }
                    catch (final Exception e) {
                        // ignore
                    }
                }
            }
            catch (final Exception e) {
                // ignore
            }
        }

        if (result == null) {
            result = Selector.open();
        }

        return result;
    }


    public static String getLocalAddress() {
        try {
            // 遍历网卡，查找一个非回路ip地址并返回
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            ArrayList<String> ipv4Result = new ArrayList<String>();
            ArrayList<String> ipv6Result = new ArrayList<String>();
            while (enumeration.hasMoreElements()) {
                final NetworkInterface networkInterface = enumeration.nextElement();
                final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
                while (en.hasMoreElements()) {
                    final InetAddress address = en.nextElement();
                    if (!address.isLoopbackAddress()) {
                        if (address instanceof Inet6Address) {
                            ipv6Result.add(normalizeHostAddress(address));
                        }
                        else {
                            ipv4Result.add(normalizeHostAddress(address));
                        }
                    }
                }
            }

            // 优先使用ipv4
            if (!ipv4Result.isEmpty()) {
                for (String ip : ipv4Result) {
                    if (ip.startsWith("127.0") || ip.startsWith("192.168")) {
                        continue;
                    }

                    return ip;
                }

                // 取最后一个
                return ipv4Result.get(ipv4Result.size() - 1);
            }
            // 然后使用ipv6
            else if (!ipv6Result.isEmpty()) {
                return ipv6Result.get(0);
            }
            // 然后使用本地ip
            final InetAddress localHost = InetAddress.getLocalHost();
            return normalizeHostAddress(localHost);
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String normalizeHostAddress(final InetAddress localHost) {
        if (localHost instanceof Inet6Address) {
            return "[" + localHost.getHostAddress() + "]";
        }
        else {
            return localHost.getHostAddress();
        }
    }


    /**
     * IP:PORT
     */
    public static SocketAddress string2SocketAddress(final String addr) {
        String[] s = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(s[0], Integer.valueOf(s[1]));
        return isa;
    }


    public static String socketAddress2String(final SocketAddress addr) {
        StringBuilder sb = new StringBuilder();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) addr;
        sb.append(inetSocketAddress.getAddress().getHostAddress());
        sb.append(":");
        sb.append(inetSocketAddress.getPort());
        return sb.toString();
    }


    public static SocketChannel connect(SocketAddress remote) {
        return connect(remote, 1000 * 5);
    }


    public static SocketChannel connect(SocketAddress remote, final int timeoutMillis) {
        SocketChannel sc = null;
        try {
            sc = SocketChannel.open();
            sc.configureBlocking(true);
            sc.socket().setSoLinger(false, -1);
            sc.socket().setTcpNoDelay(true);
            sc.socket().setReceiveBufferSize(1024 * 64);
            sc.socket().setSendBufferSize(1024 * 64);
            sc.socket().connect(remote, timeoutMillis);
            sc.configureBlocking(false);
            return sc;
        }
        catch (Exception e) {
            if (sc != null) {
                try {
                    sc.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return null;
    }

    public static String parseIpFromAddress(String address) {
    	return address.split(COLON)[0];
    }

    public static void closeChannel(Channel channel) {
        final String addrRemote = RemotingHelper.parseChannelRemoteAddr(channel);
        channel.close().addListener(new ChannelFutureListener() {
            
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("closeChannel: close the connection to remote address[{" + addrRemote + "}] result: {" + future.isSuccess() + "}");
            }
        });
    }
    
    public static String getLocalHostname() {
    	try {
			return InetAddress.getLocalHost().getHostName();
		} catch (Throwable e) {
			throw new RuntimeException("[RemotingUtil]:getLocalHostname error", e);
		}
    }

}
