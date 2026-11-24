package az.iktex.organization.structure.testders.component;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@RequestScope
public class RequestContext {

    private final String requestId = UUID.randomUUID().toString().substring(0, 8);
    private final long startTime = System.currentTimeMillis();

    public RequestContext() {
        System.out.println("RequestContext yaradildi: " + requestId);
    }

    public String getRequestId() {
        return requestId;
    }

    public long getStartTime() {
        return startTime;
    }
}
