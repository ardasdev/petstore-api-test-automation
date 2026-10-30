package petstore;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

class StoreTest extends BaseTest {

    @Test
    void apiAyakta() {
        given()
                .spec(spec)
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200);
    }

    @Test
    void stokBilgisiMusaitSayisiniIcerir() {
        given()
                .spec(spec)
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .body("available", notNullValue());
    }
}