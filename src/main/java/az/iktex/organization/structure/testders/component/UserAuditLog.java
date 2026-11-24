package az.iktex.organization.structure.testders.component;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Scope("prototype")
public class UserAuditLog {

    private final UUID auditId = UUID.randomUUID();
    private final LocalDateTime createdAt = LocalDateTime.now();
    private String action;

    public UserAuditLog() {
        System.out.println("UserAuditLog yaradildi: " + auditId);
    }

    public UUID getAuditId() {
        return auditId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @PreDestroy
    public void destroy() {
        System.out.println("UserAuditLog mehv edildi: " + auditId);
    }
}
