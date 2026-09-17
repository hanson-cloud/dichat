package com.diqin.cloud.module.ai.framework.ai.core.websearch;

import com.diqin.cloud.framework.common.util.json.JsonUtils;
import com.diqin.cloud.module.ai.framework.ai.core.webserch.AiWebSearchRequest;
import com.diqin.cloud.module.ai.framework.ai.core.webserch.AiWebSearchResponse;
import com.diqin.cloud.module.ai.framework.ai.core.webserch.bocha.AiBoChaWebSearchClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * {@link AiBoChaWebSearchClient} 集成测试类
 *
 * @author hanson
 */
public class AiBoChaWebSearchClientTest {

    private final AiBoChaWebSearchClient webSearchClient = new AiBoChaWebSearchClient(
            "CHANGE_ME");

    @Test
    @Disabled
    public void testSearch() {
        AiWebSearchRequest request = new AiWebSearchRequest()
                .setQuery("阿里巴巴")
                .setCount(3);
        AiWebSearchResponse response = webSearchClient.search(request);
        System.out.println(JsonUtils.toJsonPrettyString(response));
    }

}