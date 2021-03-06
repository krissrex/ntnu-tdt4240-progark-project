package no.ntnu.tdt4240.g17.cool_game.screens.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.ntnu.tdt4240.g17.cool_game.character.GameCharacter;
import no.ntnu.tdt4240.g17.cool_game.game_arena.Arena;
import no.ntnu.tdt4240.g17.cool_game.network.ClientData;
import no.ntnu.tdt4240.g17.cool_game.network.GameClient;
import no.ntnu.tdt4240.g17.cool_game.screens.game.controller.SendControlsSystem;
import no.ntnu.tdt4240.g17.cool_game.screens.game.player.PlayerComponent;
import no.ntnu.tdt4240.g17.cool_game.screens.game.player.PlayerSystem;

/**
 * Model for the GameView.
 */
@Getter
@Slf4j
public final class GameModel {

    private static final int MAX_PLAYERS = 4;
    /**
     * Arena.
     */
    private Arena arena;
    /**
     * Arena width in tiles.
     */
    private float width;
    /**
     * Arena height in tiles.
     */
    private float height;
    /**
     * Background image.
     */
    private Texture background;
    /**
     * Loading image.
     */
    private Texture loading;
    /**
     * Textureatlas.
     */
    private TextureAtlas dungeonTilset;
    private TextureAtlas projectilesTileset;
    /**
     * Characters.
     */
    private ArrayList<String> characters = new ArrayList();
    /**
     * Engine.
     */
    private Engine engine;
    /**
     * Entities.
     */
    private ArrayList<Entity> players;
    private ArrayList<Entity> projectiles;
    /**
     * Asset manager.
     */
    AssetManager assetManager;
    /**
     * Tilset path.
     */
    static String dungeonTilesetPath = "Assets/TextureAtlas/Characters/DungeonTileset.atlas";
    static String projectileTilesetPath = "Assets/TextureAtlas/Projectiles/Projectiles.atlas";
    static String loadingImgPath = "Loading.png";
    static String backgroundImgPath = "background.png";
    private ClientData clientData;

    /**
     * Create a new instance of the model.
     *
     * @param clientData data connected to network via {@link no.ntnu.tdt4240.g17.cool_game.network.GameClient}.
     */
    public GameModel(final ClientData clientData) {
        this.clientData = clientData;
        assetManager = new AssetManager();
        engine = new Engine();
        players = new ArrayList<>();
        characters.add("knight_m");
        characters.add("wizzard_f");
        characters.add("big_zombie");
        characters.add("necromancer");

        engine.addSystem(new PlayerSystem(ClientData.getInstance()));
        final float updateControlsInterval = 0.05f;
        final SendControlsSystem system = new SendControlsSystem(updateControlsInterval,
                GameClient.getNetworkClientInstance()::sendTCP);
        engine.addSystem(system);
        //engine.addSystem(new ProjectileSystem());
        loadAssets();
    }

    /**
     * Load all assets that wil be used in the gameView.
     */
    public void loadAssets() {
        assetManager.load("background.png", Texture.class);
        assetManager.load("Loading.png", Texture.class);
        assetManager.load(dungeonTilesetPath, TextureAtlas.class);
        assetManager.load(projectileTilesetPath, TextureAtlas.class);
        blockUntilLoadingComplete();
    }

    /**
     * Add assets to components when assetManager is finished loading.
     */
    public void blockUntilLoadingComplete() {
        log.debug("Loading assets...");
        assetManager.finishLoading();
        if (assetManager.update()) {
            dungeonTilset = assetManager.get(dungeonTilesetPath);
            projectilesTileset = assetManager.get(projectileTilesetPath);
            background = assetManager.get(backgroundImgPath);
            loading = assetManager.get(loadingImgPath);

            while (clientData.getMatchmadePlayers() == null) {
                try {
                    log.warn("Client data is null! Waiting...");
                    clientData.wait();
                } catch (InterruptedException ignored) { }
            }
            log.info("Client data is not null");

            for (int i = 0; i < clientData.getMatchmadePlayers().size(); i++) {
//            for (int i = 0; i < 1; i++) {
                players.add(new Entity());
                engine.addEntity(players.get(i));
                // Add player component
                players.get(i).add(
                        new PlayerComponent(
//                                "" + i,
                                clientData.getMatchmadePlayers().get(i).playerId,
//                                pos,
                                clientData.getMatchmadePlayers().get(i).position,
                                characters.get(i % MAX_PLAYERS),
                                dungeonTilset));
            }
            log.debug("Asset loading complete!");
        }
        log.trace("Asset loading progress: {}", assetManager.getProgress());
    }

    /**
     * Update animations and render all players onto the batch.
     *
     * @param deltaTime advance animations by this many seconds
     * @param batch     sprites are rendered to this batch.
     */
    public void renderPlayers(final float deltaTime, final SpriteBatch batch) {
        for (final Entity player : players) {
            final GameCharacter character = PlayerComponent.MAPPER.get(player).getCharacter();
            character.update(deltaTime);
            character.draw(batch);
        }
    }

}
