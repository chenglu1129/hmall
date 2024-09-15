package com.hmall.api.client;

import com.hmall.api.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @Author Cheng
 * @Date 2024 09 15 22 45
 **/
@FeignClient("trade-service")
public interface TradeClient {

    @PutMapping("/orders/{orderId}")
    void markOrderPaySuccess(@PathVariable("orderId") Long orderId);
}
