#language: es
@testfeature
Característica: Product - Store
  Como usuario de la tienda
  Quiero ver los productos disponibles
  Para poder comprarlos

  @testEscenario
  Esquema del escenario: Validación del precio de un producto
    Dado estoy en la página de la tienda
    Y me logueo con mi usuario "<usuario>" y clave "<clave>"
    Cuando navego a la categoria "<categoria>" y subcategoria "<subcategoria>"
    Y agrego <cantidad> unidades del primer producto al carrito
    Entonces valido en el popup la confirmación del producto agregado
    Y valido en el popup que el monto total sea calculado correctamente
    Cuando finalizo la compra
    Entonces valido el titulo de la pagina del carrito
    Y vuelvo a validar el calculo de precios en el carrito

    Ejemplos:
      | usuario                      | clave                | categoria | subcategoria | cantidad |
      | migue030603@gmail.com        | 3qNn7X@qL8LSbh       | Clothes   | Men         | 2        |
      | usuario_invalido@gmail.com   | clave_invalida       | Clothes   | Men         | 2        |
      | migue030603@gmail.com        | 3qNn7X@qL8LSbh       | Autos     | Men         | 2        |

      