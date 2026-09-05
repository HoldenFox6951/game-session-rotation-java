package example.game;

public final class GameBackendExample {
    public static void main(String[] args) {
        GameState state = new GameState();
        GameState.Asset asset = state.createAsset("player-42", "forest-sword");
        state.publishEvent("Saturday arena");
        state.queueForReview(asset, "player report");
        SessionService sessions = new SessionService();
        Session first = sessions.issue("player-42");
        Session rotated = sessions.refresh(first.refreshToken());
        System.out.println("new session=" + rotated.id() + ", moderation items=" + state.moderationSize());
        try { sessions.refresh(first.refreshToken()); } catch (IllegalStateException replay) { System.out.println("replay decision=rejected"); }
    }
}
