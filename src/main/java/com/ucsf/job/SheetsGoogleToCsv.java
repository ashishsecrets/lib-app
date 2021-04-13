/*
 * package com.ucsf.job;
 * 
 * import com.google.api.client.auth.oauth2.Credential; import
 * com.google.api.client.extensions.java6.auth.oauth2.
 * AuthorizationCodeInstalledApp; import
 * com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
 * import
 * com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
 * import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
 * import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
 * import com.google.api.client.http.javanet.NetHttpTransport; import
 * com.google.api.client.json.JsonFactory; import
 * com.google.api.client.json.jackson2.JacksonFactory; import
 * com.google.api.client.util.store.FileDataStoreFactory; import
 * com.google.api.services.sheets.v4.Sheets; import
 * com.google.api.services.sheets.v4.SheetsScopes; import
 * com.google.api.services.sheets.v4.model.ValueRange; import
 * org.springframework.stereotype.Service;
 * 
 * import java.io.*; import java.security.GeneralSecurityException; import
 * java.util.Collections; import java.util.List;
 * 
 * @Service public class SheetsGoogleToCsv { private static final String
 * APPLICATION_NAME = "Google Sheets API Java Quickstart"; private static final
 * JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance(); private
 * static final String TOKENS_DIRECTORY_PATH = "tokens";
 * 
 *//**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
/*
 * private static final List<String> SCOPES =
 * Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY); private static
 * final String CREDENTIALS_FILE_PATH = "/credentials.json";
 * 
 *//**
	 * Creates an authorized Credential object.
	 * 
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
/*
 * private static Credential getCredentials(final NetHttpTransport
 * HTTP_TRANSPORT) throws IOException { // Load client secrets. InputStream in =
 * SheetsGoogleToCsv.class.getResourceAsStream(CREDENTIALS_FILE_PATH); if (in ==
 * null) { throw new FileNotFoundException("Resource not found: " +
 * CREDENTIALS_FILE_PATH); } GoogleClientSecrets clientSecrets =
 * GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
 * 
 * // Build flow and trigger user authorization request.
 * GoogleAuthorizationCodeFlow flow = new
 * GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
 * clientSecrets, SCOPES) .setDataStoreFactory(new FileDataStoreFactory(new
 * java.io.File(TOKENS_DIRECTORY_PATH))) .setAccessType("offline").build();
 * LocalServerReceiver receiver = new
 * LocalServerReceiver.Builder().setPort(8888).build(); return new
 * AuthorizationCodeInstalledApp(flow, receiver).authorize("user"); }
 * 
 *//**
	 * Prints the names and majors of students in a sample spreadsheet:
	 * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
	 *//*
		 * public void main(String lastCell, String filename, int colLength) throws
		 * IOException, GeneralSecurityException { // Build a new authorized API client
		 * service. final NetHttpTransport HTTP_TRANSPORT =
		 * GoogleNetHttpTransport.newTrustedTransport(); final String spreadsheetId =
		 * "1UhvTWTf_xp1NHm8VcVgFXxpxzAy8IOzWhWod1s_PqYU";
		 * 
		 * final String range = "A2:" + lastCell; Sheets service = new
		 * Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
		 * .setApplicationName(APPLICATION_NAME).build(); ValueRange response =
		 * service.spreadsheets().values().get(spreadsheetId, range).execute();
		 * List<List<Object>> values = response.getValues(); if (values == null ||
		 * values.isEmpty()) { System.out.println("No data found."); } else {
		 * 
		 * try (PrintWriter writer = new PrintWriter(new File(filename))) {
		 * 
		 * StringBuilder sb = new StringBuilder(); for (List row : values) { int count =
		 * 0; for (Object cell : row) { System.out.println(cell); sb.append(cell);
		 * sb.append(','); count++; if (colLength == count) { sb.append('\n'); count =
		 * 0; } } }
		 * 
		 * writer.write(sb.toString());
		 * 
		 * System.out.println("Saved Google Sheet to CSV");
		 * 
		 * } catch (FileNotFoundException e) { System.out.println(e.getMessage()); }
		 * 
		 * }
		 * 
		 * } }
		 */