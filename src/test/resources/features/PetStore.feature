

Feature: Pedidos de una mascota en PetStore
  Como usuario de PetStore
  Quiero poder crear y consultar pedidos de mascotas
  Para controlar mis órdenes de compra

  Background:
    Given la API base "https://petstore.swagger.io/v2"

  @TestEjecucion
  Scenario Outline: Creación de Order: POST /store/order
    When creo una orden con los datos:
      | id       | <id>       |
      | petId    | <petId>    |
      | quantity | <quantity> |
      | shipDate | <shipDate> |
      | status   | <status>   |
      | complete | <complete> |
    Then la respuesta debe tener status 200
    And el body debe contener la orden creada con:
      | id       | <id>       |
      | petId    | <petId>    |
      | quantity | <quantity> |
      | status   | <status>   |
      | complete | <complete> |
    And guardo el "id" de la orden como "orderId" para uso posterior

    Examples:
      | id     | petId | quantity | shipDate                | status  | complete |
      | 900001 | 10001 | 2        | 2025-08-26T10:00:00.000Z| placed  | true     |

  @TestEjecucion
  Scenario Outline: Consulta de Order: GET /store/order/{orderId}
    Given tengo el "orderId" creado previamente
    When consulto la orden por id
    Then la respuesta debe tener status 200
    And el body de la orden consultada debe coincidir con:
      | id       | <id>       |
      | petId    | <petId>    |
      | quantity | <quantity> |
      | status   | <status>   |
      | complete | <complete> |

    Examples:
      | id     | petId | quantity | status | complete |
      | 900001 | 10001 | 2        | placed| true     |
