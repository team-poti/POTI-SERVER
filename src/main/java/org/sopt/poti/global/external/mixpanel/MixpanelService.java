package org.sopt.poti.global.external.mixpanel;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
@RequiredArgsConstructor
@Slf4j
public class MixpanelService {

    private final MixpanelTrackClient mixpanelTrackClient;

    @Value("${mixpanel.token}")
    private String token;

    @Async("mixpanelExecutor")
    public void track(Long userId, String event, Map<String, Object> props) {
        try {
            Map<String, Object> properties = new HashMap<>(props);
            properties.put("token", token);
            properties.put("distinct_id", String.valueOf(userId));

            mixpanelTrackClient.track(Map.of("event", event, "properties", properties));
        } catch (Exception e) {
            log.warn("Mixpanel track 실패: event={}, userId={}", event, userId, e);
        }
    }

    @Async("mixpanelExecutor")
    public void setUserProperties(Long userId, Map<String, Object> props) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("$token", token);
            body.put("$distinct_id", String.valueOf(userId));
            body.put("$set", props);
            mixpanelTrackClient.engage(body);
        } catch (Exception e) {
            log.warn("Mixpanel engage 실패: userId={}", userId, e);
        }
    }
}
