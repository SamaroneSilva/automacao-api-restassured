 package tests;

import config.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductTest extends BaseTest {

    // Lista para armazenar IDs criados durante os testes
    private static final List<String> produtosCriados = new ArrayList<>();

    // ============================================================
    // ================ CT-01 - GET Lista ==========================
    // ============================================================

    @Test
    @Description("01 - Deve buscar a lista completa de produtos e validar a estrutura e consistência da resposta.")
    public void deveBuscarListaCompletaDeProdutosComSucesso() {
        Response resposta = quandoEuBuscoAListaDeProdutos();

        entaoOStatusCodeDeveSer(resposta, 200);
        eARespostaDeveConterUmaListaDeProdutosValida(resposta);
        eCadaProdutoDeveTerCamposObrigatorios(resposta);
    }

    // ============================================================
    // ================ CT-02 - GET por ID =========================
    // ============================================================

    @Test
    @Description("02 - Deve buscar um produto específico por ID e validar os campos detalhados do retorno.")
    public void deveBuscarProdutoPorIdComSucesso() {
        // Cria massa temporária para garantir que o ID existe
        String nomeProduto = "Produto-" + UUID.randomUUID().toString().substring(0, 5);
        Response respostaCriacao = quandoEuCrioUmNovoProduto(nomeProduto, "Categoria Teste", 199.90f);
        String idProduto = respostaCriacao.jsonPath().getString("id");
        produtosCriados.add(idProduto);

        // Busca produto criado
        Response resposta = quandoEuBuscoProdutoPorId(idProduto);
        entaoOStatusCodeDeveSer(resposta, 200);
        eOProdutoRetornadoDeveSerValido(resposta, idProduto);
    }

    // ============================================================
    // ================ CT-03 - POST (Criar) =======================
    // ============================================================

    @Test
    @Description("03 - Deve criar um novo produto e validar o retorno do cadastro.")
    public void deveCriarNovoProdutoComSucesso() {
        String nomeProduto = "Produto-" + UUID.randomUUID().toString().substring(0, 5);
        Response resposta = quandoEuCrioUmNovoProduto(nomeProduto, "Categoria Teste", 199.90f);

        String idProduto = resposta.jsonPath().getString("id");
        produtosCriados.add(idProduto);

        entaoOStatusCodeDeveSer(resposta, 201);
        eOBodyDeveConterOsDadosDoProdutoCriado(resposta, nomeProduto, "Categoria Teste");
    }

    // ============================================================
    // ================ CT-04 - PUT (Atualizar) ===================
    // ============================================================

    @Test
    @Description("04 - Deve atualizar um produto existente e validar os dados atualizados.")
    public void deveAtualizarProdutoComSucesso() {
        String nomeOriginal = "Produto-" + UUID.randomUUID().toString().substring(0, 5);
        Response respostaCriacao = quandoEuCrioUmNovoProduto(nomeOriginal, "Categoria Original", 100.0f);
        String idProduto = respostaCriacao.jsonPath().getString("id");
        produtosCriados.add(idProduto);

        String nomeAtualizado = nomeOriginal + "-Atualizado";
        Response respostaUpdate = quandoEuAtualizoProduto(idProduto, nomeAtualizado, "Categoria Atualizada", 250.0f);

        entaoOStatusCodeDeveSer(respostaUpdate, 200);
        eOBodyDeveConterOsDadosDoProdutoAtualizado(respostaUpdate, nomeAtualizado, "Categoria Atualizada");
    }

    // ============================================================
    // ================ CT-05 - DELETE =============================
    // ============================================================

    @Test
    @Description("05 - Deve excluir um produto existente e garantir que não pode mais ser consultado.")
    public void deveExcluirProdutoComSucesso() {
        String nome = "Produto-" + UUID.randomUUID().toString().substring(0, 5);
        Response respostaCriacao = quandoEuCrioUmNovoProduto(nome, "Categoria Delete", 50.0f);
        String idProduto = respostaCriacao.jsonPath().getString("id");

        Response respostaDelete = quandoEuExcluoProduto(idProduto);
        entaoOStatusCodeDeveSer(respostaDelete, 200);

        // Não precisa adicionar na lista de limpeza, pois já foi deletado
        Response respostaBusca = quandoEuBuscoProdutoPorId(idProduto);
        entaoOStatusCodeDeveSer(respostaBusca, 404);
    }

    // ============================================================
    // ================ @AfterEach (Limpeza) =======================
    // ============================================================

    @AfterEach
    @Description("Limpa a massa criada durante o teste")
    public void limparProdutosCriados() {
        for (String id : produtosCriados) {
            try {
                Response resposta = quandoEuExcluoProduto(id);
                if (resposta.statusCode() == 200 || resposta.statusCode() == 204) {
                    System.out.println("🧹 Produto ID " + id + " removido com sucesso após o teste.");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Falha ao excluir produto ID: " + id + " — talvez já tenha sido removido.");
            }
        }
        produtosCriados.clear();
    }



    // ============================================================
    // ================ Métodos de Ação ============================
    // ============================================================

    @Step("Quando eu busco a lista de produtos")
    private Response quandoEuBuscoAListaDeProdutos() {
        return given().when().get().then().extract().response();
    }

    @Step("Quando eu busco o produto de ID {idProduto}")
    private Response quandoEuBuscoProdutoPorId(String idProduto) {
        return given().when().get("/" + idProduto).then().extract().response();
    }

    @Step("Quando eu crio um novo produto com nome {nomeProduto}")
    private Response quandoEuCrioUmNovoProduto(String nomeProduto, String categoria, float preco) {
        Map<String, Object> produto = new HashMap<>();
        produto.put("name", nomeProduto);
        produto.put("category", categoria);
        produto.put("price", preco);

        return given()
                .header("Content-Type", "application/json")
                .body(produto)
                .when()
                .post()
                .then()
                .extract()
                .response();
    }

    @Step("Quando eu atualizo o produto ID {idProduto}")
    private Response quandoEuAtualizoProduto(String idProduto, String novoNome, String novaCategoria, float novoPreco) {
        Map<String, Object> produtoAtualizado = new HashMap<>();
        produtoAtualizado.put("name", novoNome);
        produtoAtualizado.put("category", novaCategoria);
        produtoAtualizado.put("price", novoPreco);

        return given()
                .header("Content-Type", "application/json")
                .body(produtoAtualizado)
                .when()
                .put("/" + idProduto)
                .then()
                .extract()
                .response();
    }

    @Step("Quando eu excluo o produto ID {idProduto}")
    private Response quandoEuExcluoProduto(String idProduto) {
        return given().when().delete("/" + idProduto).then().extract().response();
    }

    // ============================================================
    // ================ Métodos de Validação ======================
    // ============================================================

    @Step("Então o status code deve ser {statusEsperado}")
    private void entaoOStatusCodeDeveSer(Response resposta, int statusEsperado) {
        resposta.then().statusCode(statusEsperado);
        System.out.println("🔹 Status code validado: " + statusEsperado);
    }

    @Step("E a resposta deve conter uma lista de produtos válida")
    private void eARespostaDeveConterUmaListaDeProdutosValida(Response resposta) {
        resposta.then().body("$", not(empty()));
        resposta.then().body("size()", greaterThan(0));
        System.out.println("✅ Lista de produtos retornada com sucesso!");
    }

    @Step("E cada produto deve conter campos obrigatórios")
    private void eCadaProdutoDeveTerCamposObrigatorios(Response resposta) {
        var lista = resposta.jsonPath().getList("$");
        for (int i = 0; i < lista.size(); i++) {
            resposta.then().body("[" + i + "].id", notNullValue());
            resposta.then().body("[" + i + "].name", notNullValue());
        }
        System.out.println("🧩 Todos os produtos possuem campos obrigatórios.");
    }

    @Step("E o produto retornado deve ser válido para o ID {idProduto}")
    private void eOProdutoRetornadoDeveSerValido(Response resposta, String idProduto) {
        resposta.then().body("id", equalTo(idProduto));
        resposta.then().body("name", notNullValue());
        resposta.then().body("createdAt", notNullValue());
        System.out.println("🔍 Produto retornado com dados válidos.");
    }

    @Step("E o corpo deve conter os dados corretos do produto criado")
    private void eOBodyDeveConterOsDadosDoProdutoCriado(Response resposta, String nome, String categoria) {
        resposta.then().body("name", equalTo(nome));
        resposta.then().body("category", equalTo(categoria));
        System.out.println("🟢 Produto criado validado com sucesso.");
    }

    @Step("E o corpo deve conter os dados corretos do produto atualizado")
    private void eOBodyDeveConterOsDadosDoProdutoAtualizado(Response resposta, String nome, String categoria) {
        resposta.then().body("name", equalTo(nome));
        resposta.then().body("category", equalTo(categoria));
        System.out.println("🟡 Produto atualizado validado com sucesso.");
    }

    // ============================================================
    // ================ CT-06 - PERFORMANCE =======================
    // ============================================================

    @Test
    @Description("CT-06 - Deve validar o tempo médio de resposta da API em múltiplas execuções consecutivas.")
    public void deveValidarTempoDeRespostaDaApi() {
        // número de chamadas GET consecutivas
        int totalRequisicoes = 15;

        // limite máximo aceitável (em milissegundos)
        long limiteMs = 10;
        List<Long> tempos = new ArrayList<>();

        System.out.println("🚀 Iniciando teste de performance com " + totalRequisicoes + " requisições...");

        for (int i = 1; i <= totalRequisicoes; i++) {
            long tempo = medirTempoDeRespostaDaRequisicao(i);
            tempos.add(tempo);
        }

        long tempoMax = tempos.stream().max(Long::compare).orElse(0L);
        long tempoMin = tempos.stream().min(Long::compare).orElse(0L);
        double tempoMedio = tempos.stream().mapToLong(Long::longValue).average().orElse(0.0);

        System.out.println("\n📊 Resultados de Performance:");
        System.out.println("➡️  Total de requisições: " + totalRequisicoes);
        System.out.println("⏱️  Tempo mínimo: " + tempoMin + " ms");
        System.out.println("⏱️  Tempo máximo: " + tempoMax + " ms");
        System.out.println("⚙️  Tempo médio: " + String.format("%.2f", tempoMedio) + " ms");

        if (tempoMedio > limiteMs) {
            System.out.println("⚠️ Tempo médio (" + tempoMedio + " ms) excedeu o limite de " + limiteMs + " ms!");
            throw new AssertionError("Tempo médio acima do esperado: " + tempoMedio + " ms");
        } else {
            System.out.println("✅ Tempo médio dentro do limite esperado (" + tempoMedio + " ms).");
        }
    }

    @Step("Medindo tempo de resposta da requisição número {numero}")
    private long medirTempoDeRespostaDaRequisicao(int numero) {
        long inicio = System.currentTimeMillis();

        Response resposta = given()
                .when()
                .get()
                .then()
                .extract()
                .response();

        long fim = System.currentTimeMillis();
        long tempoTotal = fim - inicio;

        System.out.println("➡️  Requisição " + numero + " — Status: " + resposta.statusCode() + " — Tempo: " + tempoTotal + " ms");
        return tempoTotal;
    }

}
