package com.bluezone.blue_zone_api.service;

import com.bluezone.blue_zone_api.model.Item;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class SheetsService {

    @Value("${google.credentials.path}")
    private String credentialsPath;

    @Value("${google.sheet.id}")
    private String sheetId;

    private Sheets criarClienteSheets() throws Exception {
        InputStream credentialsStream = getClass()
                .getClassLoader()
                .getResourceAsStream(credentialsPath);

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(credentialsStream)
                .createScoped("https://www.googleapis.com/auth/spreadsheets");

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName("blue-zone-api").build();
    }

    private String getCell(List<Object> row, int index) {
        if (row.size() > index && row.get(index) != null) {
            return row.get(index).toString();
        }
        return "";
    }

    public List<Item> listarItens() throws Exception {
        Sheets sheetsClient = criarClienteSheets();

        List<List <Object>> linhas = sheetsClient.spreadsheets().values()
                .get(sheetId, "Estoque Geral!A:J")
                .execute()
                .getValues();

        List<Item> itens = new ArrayList<>();

        if (linhas == null) return itens;

        for(int i = 1; i < linhas.size(); i++) {
            List<Object> linha = linhas.get(i);
            Item item = new Item();
            item.setCategoriaPrincipal(getCell(linha, 1));
            item.setSubcategoria(getCell(linha, 1));
            item.setItem(getCell(linha, 2));
            item.setControlado(getCell(linha, 3));
            item.setQuantidade(getCell(linha, 4));
            item.setEstoqueMinimo(getCell(linha, 5));
            item.setDataVencimento(getCell(linha, 6));
            item.setStatus(getCell(linha, 7));
            item.setObservacoes(getCell(linha, 8));
            item.setUltimaAtualizacao(getCell(linha, 9));
            itens.add(item);
        }

        return itens;
    }

    public void adicionarItem(Item item) throws Exception {
        Sheets sheetsClient = criarClienteSheets();

        List<List<Object>> valores = List.of(List.of(
                item.getCategoriaPrincipal(),
                item.getSubcategoria(),
                item.getItem(),
                item.getControlado(),
                item.getQuantidade(),
                item.getEstoqueMinimo(),
                item.getDataVencimento(),
                item.getStatus(),
                item.getObservacoes(),
                item.getUltimaAtualizacao()
        ));

        ValueRange corpo = new ValueRange().setValues(valores);

        sheetsClient.spreadsheets().values()
                .append(sheetId, "Estoque Geral!A:J", corpo)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    public void atualizarItem(Item item) throws Exception {
        Sheets sheetsClient = criarClienteSheets();

        List<List<Object>> linhas = sheetsClient.spreadsheets().values()
                .get(sheetId, "Estoque Geral!A:J")
                .execute()
                .getValues();

        int numeroLinha = -1;
        for (int i = 1; i < linhas.size(); i++) {
            if (getCell(linhas.get(i), 2).equals(item.getItem())) {
                numeroLinha = i + 1; // sheets eh 1-indexed
                break;
            }
        }

        if (numeroLinha == -1) throw new Exception("Item não encontrado: " + item.getItem());

        String range = "Estoque Geral!A" + numeroLinha + ":J" + numeroLinha;

        List<List<Object>> valores = List.of(List.of(
                item.getCategoriaPrincipal(),
                item.getSubcategoria(),
                item.getItem(),
                item.getControlado(),
                item.getQuantidade(),
                item.getEstoqueMinimo(),
                item.getDataVencimento(),
                item.getStatus(),
                item.getObservacoes(),
                item.getUltimaAtualizacao()
        ));

        ValueRange corpo = new ValueRange().setValues(valores);

        sheetsClient.spreadsheets().values()
                .update(sheetId, range, corpo)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }
}
