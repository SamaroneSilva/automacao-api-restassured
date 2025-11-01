package tests;

import config.BaseTest;
import io.restassured.response.Response; // Precisamos importar a classe Response
import org.junit.jupiter.api.Assertions; // Precisamos importar o Assertions do JUnit
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ProductTest extends BaseTest {

    @Test
    public void deveBuscarProdutosComSucesso() {

        System.out.println("Iniciando o teste: deveBuscarProdutosComSucesso");

        // [1] Primeiro, executamos a requisição e salvamos a resposta em uma variável
        Response resposta = given()
                .when()
                .get("/products");

        // [2] Agora, chamamos nosso método auxiliar para validar o status
        validarStatusCodeEsperado(resposta, 200);

        System.out.println("Teste finalizado com sucesso!");
    }

    // --- NOSSO NOVO MÉTODO AUXILIAR ---

    /**
     * Método auxiliar simples para validar o Status Code de uma resposta.
     * * @param resposta A resposta (Response) da requisição que queremos validar.
     * @param statusCodeEsperado O código numérico esperado (ex: 200, 201, 404).
     */
    private void validarStatusCodeEsperado(Response resposta, int statusCodeEsperado) {
        System.out.println("Validando status code...");

        // [Opção A] - Usando a validação do próprio Rest Assured (Recomendado)
        // Pega a 'resposta' e continua a validação 'then()' nela.
        resposta.then().statusCode(statusCodeEsperado);

        /* // [Opção B] - Usando a validação do JUnit 5 (Alternativa)
        // Extrai o status code (int) e compara com o esperado.
        int statusCodeAtual = resposta.getStatusCode();
        Assertions.assertEquals(statusCodeEsperado, statusCodeAtual, "O status code esperado era " + statusCodeEsperado + ", mas foi " + statusCodeAtual);
        */
    }
}