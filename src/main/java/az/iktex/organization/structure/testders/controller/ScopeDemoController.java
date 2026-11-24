package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.component.RequestContext;
import az.iktex.organization.structure.testders.component.UserAuditLog;
import az.iktex.organization.structure.testders.component.UserStatistics;
import az.iktex.organization.structure.testders.service.UserInfoService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ScopeDemoController {

    private final UserInfoService userInfoService;
    private final ObjectProvider<UserAuditLog> auditLogProvider;
    private final RequestContext requestContext;
    private final UserStatistics userStatistics;

    public ScopeDemoController(
            UserInfoService userInfoService,
            ObjectProvider<UserAuditLog> auditLogProvider,
            RequestContext requestContext,
            UserStatistics userStatistics
    ) {
        this.userInfoService = userInfoService;
        this.auditLogProvider = auditLogProvider;
        this.requestContext = requestContext;
        this.userStatistics = userStatistics;
    }

    @GetMapping("/scope-demo")
    public Map<String, Object> scopeDemo() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("singletonHashCode", userInfoService.hashCode());
        result.put("prototypeAuditId1", auditLogProvider.getObject().getAuditId().toString());
        result.put("prototypeAuditId2", auditLogProvider.getObject().getAuditId().toString());
        result.put("requestId", requestContext.getRequestId());
        return result;
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("createdUsers", userStatistics.getCreatedUsers());
        result.put("getByIdCalls", userStatistics.getGetByIdCalls());
        return result;
    }
}
