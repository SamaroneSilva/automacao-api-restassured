package config;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://68f81403deff18f212b515ee.mockapi.io";


        RestAssured.basePath = "/testesdeapi/products";

        // Habilita log automático de request/response em caso de falha
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        System.out.println("✅ Base configurada em: " + RestAssured.baseURI + RestAssured.basePath);
    }
}
