package EVPrimeTests;

import client.EVPrimeClient;
import data.SignUpLoginDataFactory;
import io.restassured.response.Response;
import models.request.SignUpLoginRequest;
import models.response.LoginResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import util.DateBuilder;

import static objectbuilder.SignUpObjectBuilder.createBodyForSignUp;
import static org.junit.Assert.*;

public class LoginTests {

    private SignUpLoginRequest signUpRequest;
    DateBuilder dateBuilder = new DateBuilder();

    @Before
    public void userSetUp(){
        signUpRequest = new SignUpLoginDataFactory(createBodyForSignUp())
                .setEmail(RandomStringUtils.randomAlphanumeric(10) + "@mail.com")
                .setPassword(RandomStringUtils.randomAlphanumeric(10))
                .createRequest();

        new EVPrimeClient()
                .signUp(signUpRequest);
    }

    @Test
    public void successfulLoginTest(){
        Response response = new EVPrimeClient()
                .login(signUpRequest);

        LoginResponse loginResponse = response.body().as(LoginResponse.class);

        assertEquals(200, response.statusCode());
        assertNotNull(loginResponse.getToken());
        assertTrue(loginResponse.getExpirationTime().contains(dateBuilder.currentTimeMinusOneHour()));
    }

}