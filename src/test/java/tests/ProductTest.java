package tests;

import config.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Feature("Produtos - API")
public class ProductTest extends BaseTest {

    // ==============================================
    // CT-01 - Buscar lista de produtos (Gherkin style)
    // ==============================================

    @Test
    @Description("CT-01 - Deve buscar a lista completa de produtos e validar um item específico (ID 7).")
    public void deveBuscarListaDeProdutosComSucesso() {
        givenQueAApiEstaDisponivel();
        Response resposta = quandoEuBuscoATotalidadeDeProdutos();
        entaoARespostaDeveConterStatusCode(200);
        eDeveConterProdutoComCamposPrincipais("7", "Monitor Automator", 1499.9f);
    }

    // ==============================================
    // STEPS (Given / When / Then / And)
    // ==============================================

    @Step("Given que a API está disponível")
    private void givenQueAApiEstaDisponivel() {
        System.out.println("✅ API configurada em: " + io.restassured.RestAssured.baseURI);
    }

    @Step("When eu busco a lista de produtos")
    private Response quandoEuBuscoATotalidadeDeProdutos() {
        System.out.println("▶️ Realizando GET em /products");
        return given()
                .when()
                .get("/products")
                .then()
                .extract()
                .response();
    }

    @Step("Then a resposta deve conter o status code {statusCodeEsperado}")
    private void entaoARespostaDeveConterStatusCode(int statusCodeEsperado) {
        System.out.println("🔎 Validando status code esperado: " + statusCodeEsperado);
        given()
                .when()
                .get("/products")
                .then()
                .statusCode(statusCodeEsperado);
    }

    @Step("And deve conter o produto ID {idProduto} com nome '{nomeProduto}' e preço {precoProduto}")
    private void eDeveConterProdutoComCamposPrincipais(String idProduto, String nomeProduto, float precoProduto) {
        System.out.println("📦 Validando produto ID " + idProduto);
        given()
                .when()
                .get("/products")
                .then()
                .body(
                        "find { it.id == '" + idProduto + "' }.name", equalTo(nomeProduto),
                        "find { it.id == '" + idProduto + "' }.price", equalTo(precoProduto)
                );
    }

    // --- Cenário: Buscar produto específico pelo ID ---
    @Test
    @Description("CT-02 - Deve buscar um produto específico pelo ID e validar seu corpo completo.")
    public void deveBuscarProdutoPorIdComSucesso() {

        givenQueOProdutoExisteComId("7");
        Response resposta = quandoEuBuscoOProdutoPeloId("7");
        entaoOStatusCodeDeveSer(resposta, 200);
        eOBodyDeveConterOsDadosCorretos(resposta, "7", "Monitor Automator", 1499.9f, "Periféricos");
    }

    // --- Etapas (Steps) no estilo Gherkin ---

    @Step("Dado que o produto com ID {idProduto} existe")
    private void givenQueOProdutoExisteComId(String idProduto) {
        // (Aqui poderíamos futuramente validar se o produto existe antes de buscar)
        System.out.println("Dado que o produto com ID " + idProduto + " existe");
    }

    @Step("Quando eu busco o produto pelo ID {idProduto}")
    private Response quandoEuBuscoOProdutoPeloId(String idProduto) {
        System.out.println("Quando eu busco o produto pelo ID: " + idProduto);
        return given()
                .pathParam("id", idProduto)
                .when()
                .get("/products/{id}");
    }

    @Step("Então o status code deve ser {statusCode}")
    private void entaoOStatusCodeDeveSer(Response resposta, int statusCode) {
        resposta.then().statusCode(statusCode);
        System.out.println("Então o status code é " + statusCode);
    }

    @Step("E o corpo deve conter os dados corretos do produto")
    private void eOBodyDeveConterOsDadosCorretos(Response resposta, String id, String nome, float preco, String categoria) {
        resposta.then().body(
                "id", equalTo(id),
                "name", equalTo(nome),
                "price", equalTo(preco),
                "category", equalTo(categoria)
        );
        System.out.println("E o corpo contém os dados corretos do produto ID " + id);
    }
}
