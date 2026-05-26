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
import java.util.List;

@Service
public class SheetsService {

    @Value("${google.credentials.path}")
    private String credentialsPath;

    @Value("${google.sheet.id}")
    private String sheetId;

    public void adicionarItem(Item item) throws Exception {
        InputStream credentialsStream = getClass()
                .getClassLoader()
                .getResourceAsStream(credentialsPath);

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(credentialsStream)
                .createScoped("https://www.googleapis.com/auth/spreadsheets");

        Sheets sheetsClient = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName("bluezone-teste").build();

        List<List<Object>> valores = List.of(
                List.of(item.getNome(), item.getQuantidade())
        );

        ValueRange corpo = new ValueRange().setValues(valores);

        sheetsClient.spreadsheets().values()
                .append(sheetId, "Página1!A:B", corpo)
                .setValueInputOption("RAW")
                .execute();
    }
}
