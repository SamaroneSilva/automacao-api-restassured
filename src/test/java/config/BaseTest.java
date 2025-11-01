package config;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://68f81403deff18f212b515ee.mockapi.io";

        // Ajuste aqui: Remova o /products desta linha
        RestAssured.basePath = "/testesdeapi";

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}