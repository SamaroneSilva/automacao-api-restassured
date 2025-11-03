package tests;

import config.BaseTest;
import io.restassured.response.Response;
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

    // --- TESTE POST ---
    @Test
    @Description("CT-01-Deve criar um novo produto com sucesso")
    public void deveCriarNovoProdutoComSucesso() {
        System.out.println("Iniciando o teste: deveCriarNovoProdutoComSucesso");

        Map<String, Object> payload = montarPayloadProduto("Novo Head Phone (Teste POST)", 299.99f);
        Response resposta = executarPostProduto(payload);

        validarStatusCodeEsperado(resposta, 201);
        validarCorpoRespostaPost(resposta, "Novo Head Phone (Teste POST)");

        System.out.println("Teste 'deveCriarNovoProdutoComSucesso' finalizado!");
    }

    // --- TESTE GET (LISTA) ---
    @Test
    @Description("CT-02-Deve buscar a lista completa de produtos e validar um item específico (ID 4) na lista.")
    public void deveBuscarProdutosComSucesso() {
        System.out.println("Iniciando o teste: deveBuscarProdutosComSucesso");

        Response resposta = executarBuscaDeProdutos();
        validarStatusCodeEsperado(resposta, 200);
        validarCamposPrincipaisDoProduto(resposta, "4", "Teclado Automator", 499.9f);

        System.out.println("Teste 'deveBuscarProdutosComSucesso' finalizado!");
    }

    // --- TESTE GET (POR ID) ---
    @Test
    @Description("CT-03-Deve buscar um produto específico pelo ID (ID 4) e validar todo o seu corpo.")
    public void deveBuscarProdutoEspecificoPorId() {
        System.out.println("Iniciando o teste: deveBuscarProdutoEspecificoPorId");

        String idProduto = "4";
        Response resposta = executarBuscaDeProdutoPorId(idProduto);
        validarStatusCodeEsperado(resposta, 200);
        validarCorpoProdutoEspecifico(resposta, idProduto);

        System.out.println("Teste 'deveBuscarProdutoEspecificoPorId' finalizado!");
    }

    // --- TESTE PUT (ATUALIZAÇÃO) ---
    @Test
    @Description("CT-04-Deve atualizar o preço de um produto específico (ID 4) com sucesso.")
    public void deveAtualizarPrecoDoProduto() {
        System.out.println("Iniciando o teste: deveAtualizarPrecoDoProduto");

        String idProduto = "4";
        float novoPreco = 549.9f;

        Map<String, Object> payloadAtualizacao = new HashMap<>();
        payloadAtualizacao.put("price", novoPreco);

        Response resposta = executarAtualizacaoProduto(idProduto, payloadAtualizacao);
        validarStatusCodeEsperado(resposta, 200);
        validarPrecoProdutoAtualizado(resposta, novoPreco);

        System.out.println("Teste 'deveAtualizarPrecoDoProduto' finalizado!");
    }

    // --- TESTE DELETE (EXCLUSÃO) ---
    @Test
    @Description("CT-05-Deve excluir um produto específico (ID 4) com sucesso.")
    public void deveExcluirProduto() {
        System.out.println("Iniciando o teste: deveExcluirProduto");

        String idProduto = "4";

        Response resposta = executarExclusaoProduto(idProduto);
        validarStatusCodeEsperado(resposta, 200);
        validarProdutoInexistente(idProduto);

        System.out.println("Teste 'deveExcluirProduto' finalizado!");
    }

    // --- MÉTODOS AUXILIARES (COM @Step) ---

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

    @Step("Montar payload do produto: {nome} - {preco}")
    private Map<String, Object> montarPayloadProduto(String nome, float preco) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", nome);
        payload.put("price", preco);
        payload.put("category", "Periféricos");
        payload.put("description", "Produto criado via automação");
        return payload;
    }

    @Step("Executar requisição POST para /products")
    private Response executarPostProduto(Map<String, Object> payload) {
        return given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/products");
    }

    @Step("Executar requisição PATCH para /products/{idProduto}")
    private Response executarAtualizacaoProduto(String idProduto, Map<String, Object> payload) {
        return given()
                .header("Content-Type", "application/json")
                .pathParam("id", idProduto)
                .body(payload)
                .when()
                .patch("/products/{id}");
    }

    @Step("Executar requisição DELETE para /products/{idProduto}")
    private Response executarExclusaoProduto(String idProduto) {
        return given()
                .pathParam("id", idProduto)
                .when()
                .delete("/products/{id}");
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

    @Step("Validar corpo da resposta da criação para o produto: {nomeProduto}")
    private void validarCorpoRespostaPost(Response resposta, String nomeProduto) {
        System.out.println("Validando corpo da resposta do POST...");
        resposta.then().body(
                "name", equalTo(nomeProduto),
                "id", is(notNullValue())
        );
    }

    @Step("Validar preço atualizado do produto: {novoPreco}")
    private void validarPrecoProdutoAtualizado(Response resposta, float novoPreco) {
        System.out.println("Validando preço atualizado...");
        resposta.then().body("price", equalTo(novoPreco));
    }

    @Step("Validar que o produto ID {idProduto} não existe mais")
    private void validarProdutoInexistente(String idProduto) {
        System.out.println("Validando que o produto não existe mais...");
        Response resposta = executarBuscaDeProdutoPorId(idProduto);
        validarStatusCodeEsperado(resposta, 404);
    }
}
