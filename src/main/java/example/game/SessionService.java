package example.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SessionService {
    private final Map<String, Session> sessions = new HashMap<>();

    public Session issue(String userId) {
        Session session = new Session(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false);
        sessions.put(session.refreshToken(), session);
        return session;
    }

    public Session refresh(String refreshToken) {
        Session old = sessions.get(refreshToken);
        if (old == null || old.revoked()) throw new IllegalStateException("refresh token rejected");
        sessions.put(refreshToken, old.revoke());
        Session next = new Session(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false);
        sessions.put(next.refreshToken(), next);
        return next;
    }

    public void revoke(String sessionId) {
        sessions.replaceAll((token, s) -> s.id().equals(sessionId) ? s.revoke() : s);
    }
}
