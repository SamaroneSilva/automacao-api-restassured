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

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

// [MUDANÇA] Mudei o @Feature para um nome mais genérico, já que agora temos POST
@Feature("Produtos - API")
public class ProductTest extends BaseTest {

    // --- [TESTE POST (AGORA REFATORADO)] ---
    @Test
    // Adicionei uma @Description para manter o padrão
    @Description("CT-03-Deve criar um novo produto com sucesso")
    public void deveCriarNovoProdutoComSucesso() {
        System.out.println("Iniciando o teste: deveCriarNovoProdutoComSucesso");

        // [1] Montar o corpo (payload) da requisição
        Map<String, Object> payload = montarPayloadProduto("Novo Head Phone (Teste POST)", 299.99f);

        // [2] Executar a requisição
        Response resposta = executarPostProduto(payload);

        // [3] Validar o Status Code
        validarStatusCodeEsperado(resposta, 201); // 201 Created

        // [4] Validar o corpo da resposta
        validarCorpoRespostaPost(resposta, "Novo Head Phone (Teste POST)");

        System.out.println("Teste 'deveCriarNovoProdutoComSucesso' finalizado!");
    }

    // --- TESTE GET (LISTA) ---
    @Test
    @Description("CT-01-Deve buscar a lista completa de produtos e validar um item específico (ID 4) na lista.")
    public void deveBuscarProdutosComSucesso() {
        System.out.println("Iniciando o teste: deveBuscarProdutosComSucesso");

        Response resposta = executarBuscaDeProdutos();
        validarStatusCodeEsperado(resposta, 200);
        validarCamposPrincipaisDoProduto(resposta, "4", "Teclado Automator", 499.9f);

        System.out.println("Teste 'deveBuscarProdutosComSucesso' finalizado!");
    }

    // --- TESTE GET (POR ID) ---
    @Test
    @Description("CT-02-Deve buscar um produto específico pelo ID (ID 4) e validar todo o seu corpo.")
    public void deveBuscarProdutoEspecificoPorId() {
        System.out.println("Iniciando o teste: deveBuscarProdutoEspecificoPorId");

        String idProduto = "4";
        Response resposta = executarBuscaDeProdutoPorId(idProduto);
        validarStatusCodeEsperado(resposta, 200);
        validarCorpoProdutoEspecifico(resposta, idProduto);

        System.out.println("Teste 'deveBuscarProdutoEspecificoPorId' finalizado!");
    }


    // --- MÉTODOS AUXILIARES (COM @Step) ---

    // Métodos de Requisição (Given/When)

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

    // [NOVO MÉTODO AUXILIAR]
    @Step("Montar payload do produto: {nome} - {preco}")
    private Map<String, Object> montarPayloadProduto(String nome, float preco) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", nome);
        payload.put("price", preco);
        payload.put("category", "Periféricos");
        payload.put("description", "Produto criado via automação");
        return payload;
    }

    // [NOVO MÉTODO AUXILIAR]
    @Step("Executar requisição POST para /products")
    private Response executarPostProduto(Map<String, Object> payload) {
        return given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/products");
    }

    // Métodos de Validação (Then)

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

    // [NOVO MÉTODO AUXILIAR]
    @Step("Validar corpo da resposta da criação para o produto: {nomeProduto}")
    private void validarCorpoRespostaPost(Response resposta, String nomeProduto) {
        System.out.println("Validando corpo da resposta do POST...");
        resposta.then().body(
                "name", equalTo(nomeProduto),
                "id", is(notNullValue())
        );
    }
}