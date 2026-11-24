package az.iktex.organization.structure.testders.component;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
@SessionScope
public class RecentlyViewedUsers implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int MAX_SIZE = 5;

    private final LinkedList<Long> ids = new LinkedList<>();

    public synchronized void add(Long id) {
        ids.remove(id);
        ids.addFirst(id);
        if (ids.size() > MAX_SIZE) {
            ids.removeLast();
        }
    }

    public synchronized List<Long> getIds() {
        return new ArrayList<>(ids);
    }

    @PreDestroy
    public void onSessionEnd() {
        System.out.println("RecentlyViewedUsers: session bitdi, son baxilanlar=" + ids);
    }
}
