package petstore;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import petstore.model.Pet;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PetTest extends BaseTest {

    //Testler


    @Test
    void petOlusturulur() {
        Pet gonderilen = ornekPet();

        Pet donen =
                given()
                        .spec(spec)
                        .body(gonderilen)
                        .when()
                        .post("/pet")
                        .then()
                        .statusCode(200)
                        .extract().as(Pet.class);

        assertEquals(gonderilen.getId(), donen.getId());
    }

    @Test
    void petOlusturulurOkunurGuncellenirSilinir() {
        Pet gonderilen = ornekPet();

        // 1. Oluştur
        petOlustur(gonderilen);

        // 2. Oku
        Pet okunan =
                given()
                        .spec(spec)
                        .pathParam("petId", gonderilen.getId())
                        .when()
                        .get("/pet/{petId}")
                        .then()
                        .statusCode(200)
                        .extract().as(Pet.class);

        assertEquals(gonderilen.getName(), okunan.getName());

        // 3. Güncelle
        gonderilen.setStatus("sold");
        gonderilen.setName("MollyDolly");

        given()
                .spec(spec)
                .body(gonderilen)
                .when()
                .put("/pet")
                .then()
                .statusCode(200);

        // 4. Güncellendiğini doğrula
        Pet guncellenen =
                given()
                        .spec(spec)
                        .pathParam("petId", gonderilen.getId())
                        .when()
                        .get("/pet/{petId}")
                        .then()
                        .statusCode(200)
                        .extract().as(Pet.class);

        assertEquals(gonderilen.getName(), guncellenen.getName());
        assertEquals(gonderilen.getStatus(), guncellenen.getStatus());

        // 5. Sil
        given()
                .spec(spec)
                .pathParam("petId", gonderilen.getId())
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(200);

        // 6. Silindiğini doğrula
        given()
                .spec(spec)
                .pathParam("petId", gonderilen.getId())
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(404)
                .body("message", equalTo("Pet not found"));
    }

    //Negatif testler

    @Test
    void olmayanHayvanIstenince404Doner(){

        given()
                .spec(spec)
                .pathParam("petId", 123331234)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(404)
                .body("message",equalTo("Pet not found"));


    }

    @Test
    @Disabled("BUG-1: Gecersiz ID 400 yerine 404 donuyor")
    void gecersizIdIleIstenince400Doner(){


        given()
                .spec(spec)
                .pathParam("petId", "abc")
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(400);

    }

    @Test
    @Disabled("BUG-3: Gecersiz durum degeri 400 yerine 200 ve bos liste donuyor")
    void gecersizDurumlaListelenince400Doner(){

        given()
                .spec(spec)
                .queryParam("status", "ucan")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(400);

    }

    @Test
    @Disabled("BUG-4: Zorunlu name alani olmadan hayvan olusturulabiliyor")
    void adiOlmayanHayvanOlusturulamaz(){

        Pet gonderilen = ornekPet();

        gonderilen.setName(null);

        given()
                .spec(spec)
                .body(gonderilen)
                .when()
                .post("/pet")
                .then()
                .statusCode(405);

    }
    @ParameterizedTest // 3 test
    @ValueSource(strings = {"available", "pending", "sold"})
    void hayvanListesiDurumaGoreDoluGelir(String durum) {
        given()
                .spec(spec)
                .queryParam("status", durum)
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }


    //Yardımcı metotlar

    private Pet ornekPet() {
        return new Pet(
                987654321L,
                "Karabas",
                List.of("https://ornek.com/karabas.jpg"),
                "available"
        );
    }

    private void petOlustur(Pet pet) {
        given()
                .spec(spec)
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200);
    }
}