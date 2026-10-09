package org.sopt.poti.global.external.mixpanel;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mixpanel-client", url = "https://api.mixpanel.com")
public interface MixpanelTrackClient {

    @PostMapping(value = "/track", consumes = MediaType.APPLICATION_JSON_VALUE)
    void track(@RequestBody List<Map<String, Object>> body);

    @PostMapping(value = "/engage", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void engage(@RequestBody MultiValueMap<String, String> formData);
}
