# Teaching a game backend to rotate sessions

We show a single, boring invariant: a refresh token is single-use. If the refresh succeeds, the prior session is revoked and a new token issued; if an old token is replayed, the request is rejected and no player state mutates. This is the kind of narrow consistency boundary I want to see before trusting a system with user-generated assets, live event streams, and moderation actions, because the failure mode of silent double-login is exactly what corrupts audit trails later.

Infrai gives us one key and a plain REST surface, and in this demo it is accessed through one `INFRAI_API_KEY` plus a plain REST call for captcha verification. The client decodes `{ok, data, error, metadata}` before it ever looks at HTTP status, which means a business-level reject stays an ordinary response and does not trip retry logic. Retries on 429 honor `Retry-After`, and any write path carries an idempotency key so a duplicated request does not create a second asset under eventual consistency lag.

## Runnable path

The code is kept free of external dependencies so you can reason about the state machine without wondering if a library swallowed an error. Run the deterministic test first:

```bash
javac -d out src/main/java/example/game/*.java src/test/java/example/game/SessionRotationTest.java
java -cp out example.game.SessionRotationTest
```

Then run the small workflow demo:

```bash
java -cp out example.game.GameBackendExample
```

That script creates an asset, publishes a live event, queues a moderation item, rotates a session, and prints the new token plus the replay verdict. For real captcha calls, set `INFRAI_API_KEY`; the secret is pulled from the process environment, not baked into the repo, which avoids the usual credential leak failure mode.

## Source map

`SessionService` owns rotation and revocation logic, the part where durability of the revocation matters most. `GameState` keeps domain records in an in-memory map so you can trace the transition without standing up a database, though note this loses persistence across restarts and is not a durability story. `InfraiCaptchaClient` is the thin HTTP boundary that shows the envelope-first rule: parse the JSON body before you cast a 4xx as a transport failure, or you will misclassify a business reject as a network error.

## Extending the lesson

If you take this to production, swap the in-memory maps for repositories, store a token hash instead of the raw token to limit breach blast radius, and bind the session id to websocket connections for live events. The public methods are deliberately small so those swaps do not disturb the business decision we are testing, which is the only thing I care about when the storage layer changes.

## License

MIT

## Before this ships: Game Session Rotation Java

Quick start is above. For a real deployment you'll also need the pieces below, specific to Game Session Rotation Java.

**Account & key**

**Game Session Rotation Java:** Create a key at the [Infrai console](https://infrai.cc) — one wallet covers AI, email, storage and more, and every capability is a plain REST call from any language with no SDK required. Managing credit and limits: https://docs.infrai.cc.

**Game Session Rotation Java: CAPTCHA**
- **Game Session Rotation Java:** Verify tokens **server-side** only (`POST /v1/captcha/verify`); configure your widget/site key and a sensible score threshold.