package no.ntnu.tdt4240.g17.cool_game.screens.main_menu;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ntnu.tdt4240.g17.cool_game.screens.navigation.Navigator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HomeControllerTest {


    private Navigator navigator;
    private HomeController homeController;

    @BeforeEach
    void setUp(){
        navigator = mock(Navigator.class);
        homeController = new HomeController(navigator, new HomeModel());
    }

    @Test
    void shouldCallGdxQuit() {
        // When
        Gdx.app = mock(Application.class);

        // When
        homeController.quit();
        // Then
        verify(Gdx.app).exit();
    }


    @Test
    void changeToSettings() {
        // When
        homeController.settingsPressed();

        // Then
        verify(navigator).changeView(Navigator.Screen.SETTING);
    }
}