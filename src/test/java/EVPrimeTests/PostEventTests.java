package EVPrimeTests;

import client.EVPrimeClient;
import data.PostEventDataFactory;
import data.SignUpLoginDataFactory;
import database.DbClient;
import io.restassured.response.Response;
import models.request.PostUpdateEventRequest;
import models.request.SignUpLoginRequest;
import models.response.LoginResponse;
import models.response.PostUpdateDeleteEventResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.SQLException;

import static objectbuilder.PostEventObjectBuilder.createBodyForPostEvent;
import static objectbuilder.SignUpObjectBuilder.createBodyForSignUp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PostEventTests {

    DbClient dbClient = new DbClient();
    private static String id;
    private SignUpLoginRequest signUpRequest;
    private LoginResponse loginResponseBody;
    private PostUpdateEventRequest requestBody;

    @Before
    public void setUp() {
        signUpRequest = new SignUpLoginDataFactory(createBodyForSignUp())
                .setEmail(RandomStringUtils.randomAlphanumeric(10) + "@mail.com")
                .setPassword(RandomStringUtils.randomAlphanumeric(10))
                .createRequest();

        new EVPrimeClient()
                .signUp(signUpRequest);

        Response loginResponse = new EVPrimeClient()
                .login(signUpRequest);

        loginResponseBody = loginResponse.body().as(LoginResponse.class);

        requestBody = new PostEventDataFactory(createBodyForPostEvent())
                .setTitle("Liverpool - Manchester United football match")
                .setImage("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.goal.com%2Fen-sg%2Fnews%2Fliverpool-vs-manchester-united-lineups-live-updates%2Fbltf4a9e3c54804c6b8&psig=AOvVaw11pYwQiECKpPWu17jL6s6X&ust=1712771074871000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCOiy883XtYUDFQAAAAAdAAAAABAE")
                .setDate("2024-04-07")
                .setLocation("Anfield")
                .setDescription("The match between the biggest rivals.")
                .createRequest();
    }

    @Test
    public void successfulPostEventTest() throws SQLException {

        Response response = new EVPrimeClient()
                .postEvent(requestBody, loginResponseBody.getToken());

        PostUpdateDeleteEventResponse postResponse = response.body().as(PostUpdateDeleteEventResponse.class);
        id = postResponse.getMessage().substring(39);

        assertEquals(201, response.statusCode());
        assertTrue(postResponse.getMessage().contains("Successfully created an event with id: "));
        assertEquals(requestBody.getTitle(), dbClient.getEventFromDB(postResponse.getMessage().substring(39)).getTitle());
        assertEquals(requestBody.getImage(), dbClient.getEventFromDB(postResponse.getMessage().substring(39)).getImage());
        assertEquals(requestBody.getDate(), dbClient.getEventFromDB(postResponse.getMessage().substring(39)).getDate());
        assertEquals(requestBody.getLocation(), dbClient.getEventFromDB(postResponse.getMessage().substring(39)).getLocation());
        assertEquals(requestBody.getDescription(), dbClient.getEventFromDB(postResponse.getMessage().substring(39)).getDescription());
    }

    @After
    public void deleteEvent() throws SQLException {
        assertTrue(new DbClient()
                .isEventDeletedFromDb(id));
    }
}
