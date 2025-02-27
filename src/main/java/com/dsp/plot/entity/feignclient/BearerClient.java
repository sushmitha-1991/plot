// package com.dsp.plot.entity.feignclient;
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;

// import com.dsp.plot.dto.SerialNumberRequest;
// import com.dsp.plot.dto.SerialNumberResponse;

// // @FeignClient(name = "bearerClient",url="https://dsptest.sansad.in" ,configuration = BearerFeignConfiguration.class)
// //@FeignClient(name = "bearerClient",url="http://10.247.128.16" ,configuration = BearerFeignConfiguration.class)

// @FeignClient(
//        name = "${feign.client.bearer.name}",
//        url = "${feign.client.bearer.url}",
//        configuration = BearerFeignConfiguration.class
// )
// public interface BearerClient {
//     @PostMapping("/master/serial-number")
//     SerialNumberResponse getResource(@RequestBody SerialNumberRequest m);
// }