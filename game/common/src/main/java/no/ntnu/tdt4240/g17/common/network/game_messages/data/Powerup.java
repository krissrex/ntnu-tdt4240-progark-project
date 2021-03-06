package no.ntnu.tdt4240.g17.common.network.game_messages.data;

import lombok.ToString;

/**
 * Created by Morten 'bujordet' Bujordet on 3/25/2019.
 *
 * @author Morten 'bujordet' Bujordet
 */
@SuppressWarnings("VisibilityModifier")
@ToString
public class Powerup {
    /** Id of the effect. */
    public String powerupId;
    /** Type of effect. */
    public PowerupType powerupType;
    /** Position of the effect. */
    public Position powerupPosition;
}
