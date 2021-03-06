package no.ntnu.tdt4240.g17.server.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import no.ntnu.tdt4240.g17.server.physics.box2d.Box2dBodyComponent;
import no.ntnu.tdt4240.g17.server.physics.box2d.TransformComponent;

/**
 * Created by Kristian 'krissrex' Rekstad on 3/11/2019.
 *
 * Based on https://www.gamedevelopment.blog/ashley-and-box2d-tutorial/
 * @author Kristian 'krissrex' Rekstad
 */
@Slf4j
public class PhysicsSystem extends IntervalSystem {

    /**
     * Family of components the engine should work on.
     * Currently {@link Box2dBodyComponent} and {@link TransformComponent}.
     */
    public static final Family PHYSICS_FAMILY = Family.all(
            Box2dBodyComponent.class,
            TransformComponent.class
    ).get();
    private final Family family;
    private final float intervalSeconds;

    private int velocityIterations = 6;
    private int positionIterations = 4;

    private final World world;
    Iterable<Entity> entitiesToUpdate = Collections.emptyList();

    /**
     * Create a new Physics system for an {@link com.badlogic.ashley.core.Engine}.
     *  @param family   the family to work with. Use {@link #PHYSICS_FAMILY}
     * @param priority the priority of the system in {@link Engine}.
     * @param intervalSeconds how often to update the physics at minimum in seconds.
     * @param world    {@link com.badlogic.gdx.physics.box2d.Box2D} world.
     */
    public PhysicsSystem(final Family family, final int priority, final float intervalSeconds, final World world) {
        super(intervalSeconds, priority);
        this.intervalSeconds = intervalSeconds;
        this.family = family;
        this.world = world;
    }

    @Override
    protected final void updateInterval() {
        final long beforeUpdate = System.currentTimeMillis();

        final long before = System.currentTimeMillis();
        world.step(intervalSeconds, velocityIterations, positionIterations);
        final long time = System.currentTimeMillis() - before;
        //log.trace("Physics update took {} ms", time);
        // Can reduce iterations if time is too long

        for (Entity entity : entitiesToUpdate) {
            TransformComponent transformComponent = TransformComponent.MAPPER.get(entity);
            Box2dBodyComponent box2dBodyComponent = Box2dBodyComponent.MAPPER.get(entity);
            final Body body = box2dBodyComponent.getBody();

            final Vector2 position = body.getPosition();
            transformComponent.setPosition(position);
            transformComponent.setRotation(body.getAngle());
        }
        final long updateTime = System.currentTimeMillis() - beforeUpdate;
        log.trace("Update time: {}", updateTime);
    }

    @Override
    public final void addedToEngine(final Engine engine) {
        entitiesToUpdate = engine.getEntitiesFor(family);
    }

    @Override
    public final void removedFromEngine(final Engine engine) {
        entitiesToUpdate = Collections.emptyList();
    }
}
