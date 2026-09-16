package org.sopt.poti.global.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    @Bean(name = "mixpanelExecutor")
    public Executor mixpanelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("mixpanel-");
        // 큐 포화 시 이벤트를 drop하고 경고 로그만 남김 — 분석 이벤트는 best-effort
        executor.setRejectedExecutionHandler((r, e) ->
            log.warn("Mixpanel 이벤트 드롭: 큐 포화 (active={}, queue={})",
                e.getActiveCount(), e.getQueue().size())
        );
        executor.initialize();
        return executor;
    }
}
