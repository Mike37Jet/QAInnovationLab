package com.nttdata.stepsdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static com.nttdata.core.DriverManager.getDriver;
import static com.nttdata.core.DriverManager.screenShot;

public class ProductStoreStepsDef {
    private WebDriver driver;

    @Dado("estoy en la página de la tienda")
    public void estoyEnLaPáginaDeLaTienda() {
        driver = getDriver();
        driver.get("https://qalab.bensg.com/store/pe/iniciar-sesion?back=https%3A%2F%2Fqalab.bensg.com%2Fstore%2Fpe%2F3-clothes");
        screenShot();
    }

    @Y("me logueo con mi usuario {string} y clave {string}")
    public void meLogueoConMiUsuarioYClave(String usuario, String clave) {
        // Ajusta los selectores según el HTML real de la tienda
        WebElement userInput = driver.findElement(By.id("field-email")); // ejemplo: id del input de email
        WebElement passInput = driver.findElement(By.id("field-password")); // ejemplo: id del input de password
        WebElement loginBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        userInput.clear();
        userInput.sendKeys(usuario);
        passInput.clear();
        passInput.sendKeys(clave);
        screenShot();
        loginBtn.click();

        // Espera explícita para validar login exitoso o mensaje de error
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // Ajusta el selector a un elemento que solo aparece si el login fue exitoso
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".header-nav")));
            screenShot();
        } catch (TimeoutException e) {
            // Si no aparece el header-nav, intentamos capturar el mensaje de error
            String mensajeError = "Login fallido: Credenciales incorrectas o error desconocido.";
            try {
                WebElement error = driver.findElement(By.cssSelector("#content > section > div > ul > li"));
                screenShot();
                if (error.isDisplayed()) {
                    mensajeError = "Login fallido: " + error.getText();
                }
            } catch (Exception ex) {
                // No se encontró el mensaje de error, igual fallamos el step
                screenShot();
            }
            System.out.println("[DEBUG] Lanzando AssertionError por login fallido: " + mensajeError);
            throw new AssertionError(mensajeError);
        } 
    }

    @Cuando("navego a la categoria {string} y subcategoria {string}")
    public void navegoALaCategoriaYSubcategoria(String categoria, String subcategoria) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                try {
                    // Espera a que haya enlaces en la página
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("a")));

                    // Buscar y hacer click en la categoría ignorando mayúsculas/minúsculas
                    boolean categoriaEncontrada = false;
                    for (WebElement link : driver.findElements(By.tagName("a"))) {
                        if (link.getText().trim().toUpperCase().equals(categoria.trim().toUpperCase())) {
                            wait.until(ExpectedConditions.elementToBeClickable(link)).click();
                            categoriaEncontrada = true;
                            screenShot();
                            break;
                        }
                    }
                    if (!categoriaEncontrada) {
                        screenShot();
                        throw new AssertionError("Categoría no encontrada: " + categoria);
                    }

                    // Espera a que se carguen los enlaces de subcategoría (no redeclarar 'wait')
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("a")));
                    boolean subcategoriaEncontrada = false;
                    for (WebElement link : driver.findElements(By.tagName("a"))) {
                        if (link.getText().trim().toUpperCase().equals(subcategoria.trim().toUpperCase())) {
                            wait.until(ExpectedConditions.elementToBeClickable(link)).click();
                            subcategoriaEncontrada = true;
                            screenShot();
                            break;
                        }
                    }
                    if (!subcategoriaEncontrada) {
                        screenShot();
                        throw new AssertionError("Subcategoría no encontrada: " + subcategoria);
                    }
                } catch (TimeoutException e) {
                    screenShot();
                    throw new AssertionError("Categoría o subcategoría no encontrada: " + categoria + " / " + subcategoria);
                }
            }
    @Y("agrego {int} unidades del primer producto al carrito")
    public void agregoUnidadesDelPrimerProductoAlCarrito(int cantidad) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // Selecciona el primer producto de la lista
            WebElement primerProducto = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".product-miniature")));
            primerProducto.click();
            screenShot();
            // Selecciona la cantidad
            WebElement cantidadInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input.input-group")));
            cantidadInput.clear();
            cantidadInput.sendKeys(String.valueOf(cantidad));
            // Agrega al carrito
            WebElement addToCartBtn = driver.findElement(By.cssSelector("button.add-to-cart"));
            addToCartBtn.click();
            screenShot();
        } catch (TimeoutException e) {
            screenShot();
            throw new AssertionError("No se pudo agregar el producto al carrito");
        }
    }

    @Entonces("valido en el popup la confirmación del producto agregado")
    public void validoEnElPopupLaConfirmaciónDelProductoAgregado() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-content-btn .btn-primary")));
            WebElement confirmMsg = driver.findElement(By.cssSelector(".cart-products-count"));
            screenShot();
            Assertions.assertTrue(confirmMsg.isDisplayed(), "No se muestra confirmación de producto agregado");
        } catch (TimeoutException e) {
            screenShot();
            throw new AssertionError("No se muestra el popup de confirmación");
        }
    }

    @Y("valido en el popup que el monto total sea calculado correctamente")
    public void validoEnElPopupQueElMontoTotalSeaCalculadoCorrectamente() {
        // Espera a que el modal esté visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("blockcart-modal")));
            screenShot();
            // Ajusta estos selectores según el HTML real del popup:
            WebElement precioUnitario = driver.findElement(By.cssSelector("#blockcart-modal .product-price"));
            WebElement cantidad = driver.findElement(By.cssSelector("#blockcart-modal .product-quantity"));
            // Usar el XPath proporcionado para el monto total:
            WebElement total = driver.findElement(By.xpath("//*[@id='blockcart-modal']/div/div/div[2]/div/div[2]/div/p[4]"));
            screenShot();
            double precio = Double.parseDouble(precioUnitario.getText().replace("S/", "").trim());
            String cantidadTexto = cantidad.getText().replaceAll("\\D+", ""); // Solo deja los dígitos
            int cant = Integer.parseInt(cantidadTexto);
            double totalEsperado = precio * cant;
            // Extrae solo la línea con el número
            String[] lineas = total.getText().split("\\r?\\n");
            String totalLinea = lineas[lineas.length - 1].trim(); // Última línea suele ser el número

            // Elimina separadores de miles y normaliza el decimal
            totalLinea = totalLinea.replaceAll("[^\\d,\\.]", ""); // Solo dígitos, punto y coma
            if (totalLinea.contains(",") && totalLinea.contains(".")) {
                // Si hay ambos, asume formato europeo: 1.234,56 -> 1234.56
                totalLinea = totalLinea.replace(".", "").replace(",", ".");
            } else if (totalLinea.contains(",")) {
                // Si solo hay coma, reemplaza por punto
                totalLinea = totalLinea.replace(",", ".");
            }
            // Ahora convierte
            double totalObtenido = Double.parseDouble(totalLinea);
            Assertions.assertEquals(totalEsperado, totalObtenido, 0.01, "El monto total no es correcto");
        } catch (TimeoutException e) {
            screenShot();
            throw new AssertionError("No se encontró el monto total en el popup del carrito. Ajusta el selector según el HTML real.");
        }
    }

    @Cuando("finalizo la compra")
    public void finalizoLaCompra() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement irAlCarritoBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".cart-content-btn .btn-primary")));
            irAlCarritoBtn.click();
            screenShot();
        } catch (TimeoutException e) {
            screenShot();
            throw new AssertionError("No se pudo finalizar la compra");
        }
    }

    @Entonces("valido el titulo de la pagina del carrito")
    public void validoElTituloDeLaPaginaDelCarrito() {
    // Validar el título de la página usando el <title> del head
    screenShot();
    String tituloPagina = driver.getTitle();
    Assertions.assertEquals("Carrito", tituloPagina.trim());
    }

    @Y("vuelvo a validar el calculo de precios en el carrito")
    public void vuelvoAValidarElCalculoDePreciosEnElCarrito() {
    // Extrae el precio unitario usando el selector correcto del carrito
    WebElement precioUnitario = driver.findElement(By.cssSelector("div.current-price > span"));
        // Busca la cantidad en el input según el selector real del HTML
        int cant = 0;
        try {
            WebElement cantidadInput = driver.findElement(By.cssSelector("div.qty > div > input"));
            cant = Integer.parseInt(cantidadInput.getAttribute("value").replaceAll("\\D+", ""));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            throw new AssertionError("No se pudo encontrar el input de cantidad en el carrito");
        }
        // Extrae el total desde el resumen del carrito
        WebElement total = driver.findElement(By.cssSelector(".cart-total .value"));
        screenShot();
        // Limpia el precio unitario
        String precioTexto = precioUnitario.getText().replace("S/", "").replace("\u00a0", "").replace(",", ".").trim();
        double precio = Double.parseDouble(precioTexto);
        double totalEsperado = precio * cant;
        // Limpia el total
        String totalTexto = total.getText().replace("S/", "").replace("\u00a0", "").replace(",", ".").trim();
        double totalObtenido = Double.parseDouble(totalTexto);
        Assertions.assertEquals(totalEsperado, totalObtenido, 0.01, "El monto total en el carrito no es correcto");
    }
}
