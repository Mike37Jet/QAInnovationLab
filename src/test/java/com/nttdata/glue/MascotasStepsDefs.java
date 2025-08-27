package com.nttdata.glue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.util.Map;
import io.cucumber.datatable.DataTable;

public class MascotasStepsDefs {
    private String baseUrl;
    private Response lastResponse;
    private static Long orderId;
    private static Map<String, Object> lastOrder;


    @Given("la API base {string}")
    public void laAPIBase(String url) {
        baseUrl = url;
    }

    @When("creo una orden con los datos:")
    public void creoUnaOrdenConLosDatos(DataTable dataTable) {
        Map<String, String> map = dataTable.asMap(String.class, String.class);


        Map<String, Object> order = Map.of(
                "id", Long.valueOf(map.get("id")),
                "petId", Long.valueOf(map.get("petId")),
                "quantity", Integer.valueOf(map.get("quantity")),
                "shipDate", map.get("shipDate"),
                "status", map.get("status"),
                "complete", Boolean.valueOf(map.get("complete"))
        );
        lastOrder = order;
        orderId = (Long) order.get("id");

        Response response = given()
                .baseUri(baseUrl)
                .basePath("/store/order")
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post();

        lastResponse = response;
    }

    @Then("la respuesta debe tener status {int}")
    public void laRespuestaDebeTenerStatus(int arg0) {
        lastResponse.then().statusCode(arg0);
        assertThat("Status code", lastResponse.statusCode(), is(arg0));
    }

    @And("el body debe contener la orden creada con:")
    public void elBodyDebeContenerLaOrdenCreadaCon(DataTable dataTable) {
        Map<String, String> expected = dataTable.asMap(String.class, String.class);

        lastResponse.then()
                .contentType(ContentType.JSON)
                .body("id", equalTo(Integer.valueOf(expected.get("id"))))
                .and().body("petId", equalTo(Integer.valueOf(expected.get("petId"))))
                .and().body("quantity", equalTo(Integer.valueOf(expected.get("quantity"))))
                .and().body("status", equalTo(expected.get("status")))
                .and().body("complete", equalTo(Boolean.valueOf(expected.get("complete"))));

        Map<String, ?> body = lastResponse.jsonPath().getMap("$");
        assertThat(body, hasEntry("status", expected.get("status")));
        assertThat(body, hasKey("shipDate"));
    }

    @And("guardo el {string} de la orden como {string} para uso posterior")
    public void guardoElDeLaOrdenComoParaUsoPosterior(String arg0, String arg1) {
        Long idFromResponse = lastResponse.jsonPath().getLong(arg0);
        assertThat("El id devuelto debe existir y ser positivo", idFromResponse, greaterThan(0L));
        orderId = idFromResponse;
    }

    @Given("tengo el {string} creado previamente")
    public void tengoElCreadoPreviamente(String arg0) {
        assertThat("Debe existir orderId del escenario anterior", orderId, notNullValue());
    }

    @When("consulto la orden por id")
    public void consultoLaOrdenPorId() {
        Response response = given()
                .baseUri(baseUrl)
                .basePath("/store/order/{orderId}")
                .pathParam("orderId", orderId)
                .when()
                .get();

        lastResponse = response;
    }

    @And("el body de la orden consultada debe coincidir con:")
    public void elBodyDeLaOrdenConsultadaDebeCoincidirCon(DataTable dataTable) {
        Map<String, String> expected = dataTable.asMap(String.class, String.class);

        lastResponse.then()
                .contentType(ContentType.JSON)
                .body("id", equalTo(Integer.valueOf(expected.get("id"))))
                .and().body("petId", equalTo(Integer.valueOf(expected.get("petId"))))
                .and().body("quantity", equalTo(Integer.valueOf(expected.get("quantity"))))
                .and().body("status", equalTo(expected.get("status")))
                .and().body("complete", equalTo(Boolean.valueOf(expected.get("complete"))));

        assertThat("orderId consultado debe coincidir",
                lastResponse.jsonPath().getLong("id"),
                is(Long.valueOf(expected.get("id"))));
    }
}
