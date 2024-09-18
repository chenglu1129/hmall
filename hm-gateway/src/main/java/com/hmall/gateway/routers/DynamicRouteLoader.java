package com.hmall.gateway.routers;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @Author Cheng
 * @Date 2024 09 18 18 31
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRouteLoader {

    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter routeDefinitionWriter;

    private final String dataId = "gateway-routes.json";
    private final String group = "DEFAULT_GROUP";
    private final Set<String> routeIds = new HashSet<>();

    @PostConstruct
    public void initRouteConfigListener() throws NacosException {
        //1.项目启动时，先拉取一次配置信息，并且添加配置监听器
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        //2.监听到配置信息变化时，需要去更新路由表
                        updateRouteConfig(configInfo);
                    }
                });

        //3.第一次读取到配置，也需要更新到路由表
        updateRouteConfig(configInfo);
    }

    public void updateRouteConfig(String configInfo) {
        log.debug("监听到路由配置信息: {}",configInfo);
        //1.解析配置文件，转为RouteDefinition对象
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);
        //2.删除旧路由表
        for (String routeId : routeIds) {
            routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
        }
        routeIds.clear();
        //3.更新路由表
        for (RouteDefinition routeDef : routeDefinitions) {
            //3.1.更新路由表
            routeDefinitionWriter.save(Mono.just(routeDef)).subscribe();
            //3.2
            routeIds.add(routeDef.getId());
        }
    }
}
