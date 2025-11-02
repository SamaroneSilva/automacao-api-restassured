package tests;

import config.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// --- Imports do Allure ---
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
// -------------------------

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Feature("Produtos - Testes de Leitura (GET)") // [Allure] Agrupa todos os testes desta classe
public class ProductTest extends BaseTest {

    @Test
    @Description("Deve buscar a lista completa de produtos e validar um item específico (ID 4) na lista.")
    public void deveBuscarProdutosComSucesso() {
        System.out.println("Iniciando o teste: deveBuscarProdutosComSucesso");

        // [1] Executamos a requisição
        Response resposta = executarBuscaDeProdutos();

        // [2] Validamos o status code
        validarStatusCodeEsperado(resposta, 200);

        // [3] Validamos os campos principais do body
        validarCamposPrincipaisDoProduto(resposta, "4", "Teclado Automator", 499.9f);

        System.out.println("Teste 'deveBuscarProdutosComSucesso' finalizado!");
    }

    @Test
    @Description("Deve buscar um produto específico pelo ID (ID 4) e validar todo o seu corpo.")
    public void deveBuscarProdutoEspecificoPorId() {
        System.out.println("Iniciando o teste: deveBuscarProdutoEspecificoPorId");

        String idProduto = "4";

        // [1] Executamos a requisição
        Response resposta = executarBuscaDeProdutoPorId(idProduto);

        // [2] Validamos o status code
        validarStatusCodeEsperado(resposta, 200);

        // [3] Validamos o corpo da resposta (Body)
        validarCorpoProdutoEspecifico(resposta, idProduto);

        System.out.println("Teste 'deveBuscarProdutoEspecificoPorId' finalizado!");
    }


    // --- MÉTODOS AUXILIARES (AGORA COM @Step) ---
    // Cada @Step vira um item clicável no relatório

    @Step("Executar requisição GET para /products")
    private Response executarBuscaDeProdutos() {
        return given()
                .when()
                .get("/products");
    }

    @Step("Executar requisição GET para /products/{idProduto}")
    private Response executarBuscaDeProdutoPorId(String idProduto) {
        return given()
                .pathParam("id", idProduto)
                .when()
                .get("/products/{id}");
    }

    @Step("Validar Status Code esperado: {statusCodeEsperado}")
    private void validarStatusCodeEsperado(Response resposta, int statusCodeEsperado) {
        System.out.println("Validando status code...");
        resposta.then().statusCode(statusCodeEsperado);
    }

    @Step("Validar campos principais (Nome e Preço) do produto ID: {idProduto} na lista")
    private void validarCamposPrincipaisDoProduto(Response resposta, String idProduto, String nomeProduto, float precoProduto) {
        System.out.println("Validando campos principais do produto ID: " + idProduto);

        resposta.then().body(
                "find { it.id == '" + idProduto + "' }.name", equalTo(nomeProduto),
                "find { it.id == '" + idProduto + "' }.price", equalTo(precoProduto)
        );
    }

    @Step("Validar corpo completo do produto ID: {idProduto}")
    private void validarCorpoProdutoEspecifico(Response resposta, String idProduto) {
        System.out.println("Validando o corpo do produto ID: " + idProduto);
        resposta.then().body(
                "id", equalTo(idProduto),
                "name", equalTo("Teclado Automator"),
                "price", equalTo(499.9f),
                "category", equalTo("Periféricos")
        );
    }
}