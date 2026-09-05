package example.game;

public final class SessionRotationTest {
    public static void main(String[] args) {
        SessionService service = new SessionService();
        Session first = service.issue("student-player");
        Session second = service.refresh(first.refreshToken());
        if (second.refreshToken().equals(first.refreshToken()) || second.revoked()) throw new AssertionError("rotation failed");
        try { service.refresh(first.refreshToken()); throw new AssertionError("replay accepted"); }
        catch (IllegalStateException expected) { System.out.println("PASS: refresh tokens rotate once and replay is rejected"); }
    }
}
