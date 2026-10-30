package petstore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import petstore.model.User;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest extends BaseTest {

    //Testler

    @Test
    void userOlusturulur() {
        User gonderilen = ornekUser();

        // 1. Oluştur
        given()
                .spec(spec)
                .body(gonderilen)
                .when()
                .post("/user")
                .then()
                .statusCode(200);

        // 2. Oku ve karşılaştır
        User okunan =
                given()
                        .spec(spec)
                        .pathParam("username", gonderilen.getUsername())
                        .when()
                        .get("/user/{username}")
                        .then()
                        .statusCode(200)
                        .extract().as(User.class);

        assertEquals(gonderilen.getUsername(), okunan.getUsername());
        assertEquals(gonderilen.getFirstName(), okunan.getFirstName());

        // 3. Sil
        given()
                .spec(spec)
                .pathParam("username", gonderilen.getUsername())
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(200);
    }


    //Yardımcı metotlar

    private User ornekUser() {
        return new User(
                123321L,
                "test_kullanici",
                "Test",
                "Kullanici",
                "test@example.com",
                "Test1234!",
                "5550000000",
                0
        );
    }
}