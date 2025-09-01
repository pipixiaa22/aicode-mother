package com.ckrey.ckreycodemother.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/stream")
public class SseController {

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamWithSseEmitter() {
        // 设置超时时间（可选，避免连接长期闲置）
        SseEmitter emitter = new SseEmitter(60_000L); // 60秒超时

        executor.submit(() -> {
            try {
                // 发送3条SSE消息（自动添加data:前缀和\n\n）
                for (int i = 1; i <= 3; i++) {
                    // 发送纯文本
//                    emitter.send("第" + i + "条SSE消息");
                    // 也可发送带ID和事件类型的消息（符合SSE规范）
                     emitter.send(SseEmitter.event().id("id-" + i).name("message").data("内容"));
                    Thread.sleep(1000);
                }
                emitter.complete(); // 完成发送
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @GetMapping(value = "/time", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> timeStream() {
        // 每1秒生成一个当前时间字符串
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> {
                    // 格式化当前时间
                    String time = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    // 构建SSE格式消息（包含data字段）
                    return "data: 当前时间: " + time + "\n\n";
                })
                // 限制只推送10条消息后结束
                .take(10);
    }

    // 带参数的进度流：模拟任务进度推送
    @GetMapping(value = "/progress/{taskId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> progressStream(@PathVariable String taskId) {
        // 从0%到100%，每500毫秒更新一次进度
        return Flux.range(0, 11)  // 生成0-10的数字（对应0%、10%...100%）
                .delayElements(Duration.ofMillis(500))  // 每个元素延迟500ms发送
                .map(progress -> {
                    int percentage = progress * 10;  // 转换为百分比
                    // 构建包含event和data字段的SSE消息
                    return "event: progress\n" +
                            "id: " + percentage + "\n" +
                            "data: {\"taskId\":\"" + taskId + "\",\"progress\":" + percentage + "%}\n\n";
                });
    }
}