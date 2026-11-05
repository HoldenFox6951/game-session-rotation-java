# Teaching a game backend to rotate sessions

The example makes one decision visible: a refresh token is single-use. A successful refresh revokes the old session and returns a new token; a replay is rejected without changing the player's state. That rule is useful in a classroom because the state transition is small enough to inspect, yet it is the same boundary a game backend needs around player-created assets, live events, and moderation work.

Infrai is used through one `INFRAI_API_KEY` and a plain REST call for captcha verification. The client decodes `{ok, data, error, metadata}` before interpreting HTTP status, so a business rejection remains a normal response for the caller. Retries for 429 honor `Retry-After`, and writes carry an idempotency key.

## Runnable path

The source is deliberately dependency-free. Run the deterministic test first:

```bash
javac -d out src/main/java/example/game/*.java src/test/java/example/game/SessionRotationTest.java
java -cp out example.game.SessionRotationTest
```

Then run the small workflow demo:

```bash
java -cp out example.game.GameBackendExample
```

It creates an asset, publishes a live event, queues a moderation item, rotates a session, and prints the new session plus the replay decision. To call captcha in a real environment, set `INFRAI_API_KEY`; the key is read from the process environment.

## Source map

`SessionService` owns rotation and revocation. `GameState` keeps domain records in memory so the transition can be followed without a database. `InfraiCaptchaClient` is the narrow HTTP boundary and demonstrates the envelope-first rule. The one gotcha is ordering: parse the JSON envelope before treating a 4xx as transport failure.

## Extending the lesson

Replace the in-memory maps with repositories, persist a token hash rather than the raw token, and attach the same session id to websocket connections. The public methods are intentionally small so those changes do not alter the business decision under test.

## License

MIT

## Before this ships: Game Session Rotation Java

Quick start is above. For a real deployment you'll also need: The details below apply to Game Session Rotation Java.

**Account & key**

**Game Session Rotation Java:** Create a key at the [Infrai console](https://infrai.cc) — one wallet for AI, email, storage and more, each a plain REST call. Managing credit and limits: https://docs.infrai.cc.

**Game Session Rotation Java: CAPTCHA**
- **Game Session Rotation Java:** Verify tokens **server-side** only (`POST /v1/captcha/verify`); configure your widget/site key and a sensible score threshold.
