package example.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class GameState {
    public record Asset(String id, String ownerId, String name) {}
    public record LiveEvent(String id, String title) {}
    public record ModerationItem(String id, String assetId, String reason) {}
    private final List<Asset> assets = new ArrayList<>();
    private final List<LiveEvent> events = new ArrayList<>();
    private final List<ModerationItem> moderation = new ArrayList<>();

    public Asset createAsset(String ownerId, String name) {
        Asset asset = new Asset(UUID.randomUUID().toString(), ownerId, name); assets.add(asset); return asset;
    }
    public LiveEvent publishEvent(String title) {
        LiveEvent event = new LiveEvent(UUID.randomUUID().toString(), title); events.add(event); return event;
    }
    public ModerationItem queueForReview(Asset asset, String reason) {
        ModerationItem item = new ModerationItem(UUID.randomUUID().toString(), asset.id(), reason); moderation.add(item); return item;
    }
    public int moderationSize() { return moderation.size(); }
}
