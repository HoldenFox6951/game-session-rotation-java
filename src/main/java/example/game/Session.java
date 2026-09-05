package example.game;

public record Session(String id, String refreshToken, boolean revoked) {
    public Session revoke() { return new Session(id, refreshToken, true); }
}
