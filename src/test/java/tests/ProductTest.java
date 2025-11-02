package tests;

import config.BaseTest;
import io.restassured.response.Response; // Precisamos importar a classe Response
import org.junit.jupiter.api.Assertions; // Precisamos importar o Assertions do JUnit
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*; // Importante para os validadores (equalTo, etc.)

public class ProductTest extends BaseTest {

    @Test
    public void deveBuscarProdutosComSucesso() {
        System.out.println("Iniciando o teste: deveBuscarProdutosComSucesso");

        // [1] Executamos a requisição
        Response resposta = given()
                .when()
                .get("/products");

        // [2] Validamos o status code
        validarStatusCodeEsperado(resposta, 200);

        // [3] Validamos os campos principais do body
        validarCamposPrincipaisDoProduto(resposta, "4", "Teclado Automator", 499.9f);

        System.out.println("Teste 'deveBuscarProdutosComSucesso' finalizado!");
    }

    // --- NOSSO NOVO CENÁRIO DE TESTE ---

    @Test
    public void deveBuscarProdutoEspecificoPorId() {
        System.out.println("Iniciando o teste: deveBuscarProdutoEspecificoPorId");

        // ID que queremos buscar (o "Teclado Automator")
        String idProduto = "4";

        // [1] Executamos a requisição
        Response resposta = given()
                .pathParam("id", idProduto) // [A] Passamos o ID como parâmetro de caminho
                .when()
                .get("/products/{id}"); // [B] Usamos {id} no endpoint

        // [2] Validamos o status code
        validarStatusCodeEsperado(resposta, 200);

        // [3] Validamos o corpo da resposta (Body)
        System.out.println("Validando o corpo do produto ID: " + idProduto);

        // Como a resposta é um objeto (não uma lista),
        // validamos os campos diretamente
        resposta.then().body(
                "id", equalTo(idProduto),
                "name", equalTo("Teclado Automator"),
                "price", equalTo(499.9f),
                "category", equalTo("Periféricos")
        );

        System.out.println("Teste 'deveBuscarProdutoEspecificoPorId' finalizado!");
    }


    // --- MÉTODOS AUXILIARES ---

    /**
     * Método auxiliar simples para validar o Status Code de uma resposta.
     */

    private void validarStatusCodeEsperado(Response resposta, int statusCodeEsperado) {
        System.out.println("Validando status code...");
        resposta.then().statusCode(statusCodeEsperado);
    }

    /**
     * Método auxiliar para validar campos principais de um item específico na resposta (lista).
     */
    private void validarCamposPrincipaisDoProduto(Response resposta, String idProduto, String nomeProduto, float precoProduto) {
        System.out.println("Validando campos principais do produto ID: " + idProduto);

        resposta.then().body(
                "find { it.id == '" + idProduto + "' }.name", equalTo(nomeProduto),
                "find { it.id == '" + idProduto + "' }.price", equalTo(precoProduto)
        );
    }


}