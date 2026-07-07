package com.zhenyu.drama.config;

import com.zhenyu.common.utils.JacksonObjectMapper;
import com.zhenyu.drama.interceptor.JwtTokenAdminInterceptor;
import com.zhenyu.drama.interceptor.JwtTokenUserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;


/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport{
    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")              // 拦截管理端所有请求
                .excludePathPatterns("/admin/login")       // 登录接口放行
                .excludePathPatterns("/admin/logout");     // 登出接口放行

        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**")               // 拦截用户端所有请求
                .excludePathPatterns("/user/auth/login")   // 登录接口放行
                .excludePathPatterns("/user/auth/sendCode") // 发送验证码放行
                .excludePathPatterns("/user/auth/loginByPhone"); // 手机号登录放行
    }

    /**
     * 扩展 Spring MVC 消息转换器
     * 统一处理 LocalDateTime 等日期类型的序列化格式
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层使用 Jackson 将 Java 对象转为 JSON
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的消息转换器对象追加到 MVC 框架的转换器集合中（优先级最高）
        converters.add(0, messageConverter);
    }

    /**
     * 添加静态资源处理器，放行 Knife4j/Swagger 相关资源
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
