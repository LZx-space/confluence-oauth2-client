package org.lzx.confluence.oauth2.util;

import org.apache.http.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author LZx
 * @since 2018年5月20日
 */
public abstract class HttpClientUtils {

    private static final CloseableHttpClient HTTP_CLIENT;

    /**
     * 查看代码这是线程安全的对象，将请求返回内容序列化为字符串
     */
    private static final ResponseHandler<String> BASIC_RESPONSE_HANDLER = new BasicResponseHandler();

    /**
     * 从内部连接池中获取连接的超时时间
     */
    private static final int ACQUIRE_CONNECTION_REQUEST_TIMEOUT_MILLISECONDS = 10000;

    /**
     * 与目标地址建立连接的超时时间
     */
    private static final int CONNECT_TARGET_HOST_REQUEST_TIMEOUT_MILLISECONDS = 1000;

    /**
     * SOCKET发送数据和返回数据的时间间隔，注意这意味着中间有业务处理时间
     */
    private static final int REQUEST_SOCKET_TIMEOUT_MILLISECONDS = 1500;

    /**
     * 连接池中连接最长闲置时间
     */
    private static final int CONNECTION_IDLE_MILLISECONDS_TO_BE_CLOSE = 30000;

    /**
     * 小线程池有利于单帧处理数据量，减少线程切换开销，提升TPS，
     * <strong style="color:red;">但是，需要设置较小的建立链接和套接字返回超时时间，不然，极限情况下所有请求将超时</strong>
     */
    private static final int CONNECTION_MAX_TOTAL = Runtime.getRuntime().availableProcessors() * 3;

    static {
        HTTP_CLIENT = HttpClients.custom()
                .setConnectionManager(connectionManager())
                .setKeepAliveStrategy(keepAliveStrategy())
                .setDefaultRequestConfig(defaultRequestConfig())
                .disableAuthCaching()
                .disableAutomaticRetries()
                .disableCookieManagement()
                .disableRedirectHandling()
                .build();
    }

    /**
     * 创建链接管理器实例，一般用HTTP链接池管理器实例
     *
     * @return 链接管理器
     */
    private static HttpClientConnectionManager connectionManager() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.closeIdleConnections(CONNECTION_IDLE_MILLISECONDS_TO_BE_CLOSE, TimeUnit.MILLISECONDS);
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .build();
        connManager.setDefaultSocketConfig(socketConfig);
        ConnectionConfig connConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .build();
        connManager.setDefaultConnectionConfig(connConfig);
        connManager.setValidateAfterInactivity(1000);
        connManager.setDefaultMaxPerRoute(CONNECTION_MAX_TOTAL);
        connManager.setMaxTotal(CONNECTION_MAX_TOTAL);
        return connManager;
    }

    /**
     * 默认连接存活策略中，当响应头重Keep-Alive属性不存在时，HttpClient将视该连接需要永远保持。
     * 自定义如上情况时反馈的连接存活时间
     *
     * @return keepAlive的策略
     */
    private static ConnectionKeepAliveStrategy keepAliveStrategy() {
        return (response, context) -> {
            Args.notNull(response, "HTTP response must not be null");
            final HeaderElementIterator headerElementIterator = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (headerElementIterator.hasNext()) {
                final HeaderElement he = headerElementIterator.nextElement();
                final String param = he.getName();
                final String value = he.getValue();
                if (value != null && HttpHeaders.TIMEOUT.equalsIgnoreCase(param)) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (final NumberFormatException ignore) {
                    }
                }
            }
            return 0;
        };
    }

    /**
     * 默认的请求配置信息
     *
     * @return 默认的请求配置信息对象
     */
    private static RequestConfig defaultRequestConfig() {
        return RequestConfig.copy(RequestConfig.DEFAULT)
                .setSocketTimeout(REQUEST_SOCKET_TIMEOUT_MILLISECONDS)
                .setConnectTimeout(CONNECT_TARGET_HOST_REQUEST_TIMEOUT_MILLISECONDS)
                .setConnectionRequestTimeout(ACQUIRE_CONNECTION_REQUEST_TIMEOUT_MILLISECONDS)
                /*
                 * 对于需要服务端验证的请求实体可关闭的请求（such as POST PUT） 开启此
                 * 选项将显著提高性能，是否生效取决于报文头和服务端（具体待明），服务端
                 * 不支持HTTP/1.1可能出现异常
                 */
                .setExpectContinueEnabled(true)
                .build();
    }

    /**
     * 发送GET请求
     *
     * @param uri        <span style="color:red;">可以带参数的URI</span>
     * @param parameters 请求参数，常用的为{@link BasicNameValuePair}实现类的对象
     * @return 响应字符串
     * @throws IOException 连接或者处理协议相关异常
     */
    public static String get(String uri, NameValuePair... parameters) throws IOException {
        return HTTP_CLIENT.execute(RequestBuilder.get(uri)
                        .addParameters(parameters)
                        .setCharset(StandardCharsets.UTF_8)
                        .build(),
                BASIC_RESPONSE_HANDLER);
    }

    /**
     * 发送POST请求
     *
     * @param uri        目标地址
     * @param parameters 请求参数，常用的为{@link BasicNameValuePair}实现类的对象
     * @return 响应字符串
     * @throws IOException 连接或者处理协议相关异常
     */
    public static String post(String uri, NameValuePair... parameters) throws IOException {
        return HTTP_CLIENT.execute(RequestBuilder.post(uri)
                        .addParameters(parameters)
                        .setCharset(StandardCharsets.UTF_8)
                        .build(),
                BASIC_RESPONSE_HANDLER);
    }

    /**
     * 执行请求，除了返回内容生成其字符串
     *
     * @param request 请求，<strong style="color:red;">API中有{@link RequestBuilder}</strong>
     * @return 响应字符串
     * @throws IOException 请求异常
     */
    public static String execute(HttpUriRequest request) throws IOException {
        return HTTP_CLIENT.execute(request, BASIC_RESPONSE_HANDLER);
    }

    /**
     * 执行请求，除了返回内容生成其字符串
     *
     * @param request         请求，<strong style="color:red;">API中有{@link RequestBuilder}</strong>
     * @param responseHandler HttpResponse数据处理器
     * @param <T>             返回对象的类型
     * @return 响应的类型对象
     * @throws IOException 请求异常
     */
    public static <T> T execute(HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException {
        return HTTP_CLIENT.execute(request, responseHandler);
    }

}
