/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.democnpjfrontend.services;

import com.demo.democnpjfrontend.beans.Empresa;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.Date;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author JRadagast
 */
@ApplicationScoped
public class EmpresaService {
    
    private String baseurl = "http://democnpj:8081/democnpj/empresa";
    
    public Empresa doRequest( String cnpj ) {
        StringBuilder response = new StringBuilder();
        
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet( baseurl+"/busca-cnpj?cnpj="+cnpj);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            // Le a resposta da request
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                response.append(EntityUtils.toString(httpResponse.getEntity()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response.length() > 0){
            // Preenche os dados da empresa buscado e retorna para o front.
            Empresa e = getDataFromResponse(response.toString());
            return e;
        }
        
        // Caso a resposta seja vazia, retorna null
        return null;
    }
    
    private Empresa getDataFromResponse( String response ){
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            /**
             * Le o json e retorna a empresa pega no jsonNode.
             */
            JsonNode node = objectMapper.readTree(response);
            Empresa e = new Empresa();

            e.setCnpj(node.get("cnpj").asText());
            /* Verifica se cada campo buscado existe primeiro, caso exista, pega-se o valor. Se não, apenas ignora.*/
            if (node.has("razaoSocial")) {
                e.setRazaoSocial(node.get("razaoSocial").asText());
            }
            if ( node.has("situacaoCadastral")) {
                e.setSituacaoCadastral(node.get("situacaoCadastral").asText());
            }
            if ( node.has("dataCadastro") ) {
                e.setDataCadastro(Date.valueOf(node.get("dataCadastro").asText()));
            }

            if ( node.has("cidade") ) {
                e.setCidade(node.get("cidade").asText());
            }
            return e;
        } catch (JsonProcessingException ex){
            ex.printStackTrace();
        }
        
        // Caso falhe em transformar o json, retorna null
        return null;
    }
    
    public String salvar( Empresa empresa ){
        // Create HttpClient, Define a URL e cria a request POST
        HttpClient httpClient = HttpClients.createDefault();
        String url = baseurl + "/salvar";
        HttpPost httpPost = new HttpPost(url);

        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(empresa);
            
            // Adiciona os parametros no corpo da requisição
            StringEntity body = new StringEntity(json, ContentType.APPLICATION_JSON );
            httpPost.setEntity( body );
            httpPost.setHeader("Content-Type", "application/json");

            // Faz a requisição
            HttpResponse response = httpClient.execute(httpPost);

            // Caso a resposta seja 200, deu sucesso, se não, deu erro.
            if (response.getStatusLine().getStatusCode() == 200){
                return "Sucesso ao salvar";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "Ocorreu algum erro ao salvar";
    }
    
    public boolean validateSave( Empresa empresa ){
        if (empresa.getTelefone().length() > 20){            
            return false;
        }
        if (empresa.getEndereco().length() > 255){
            return false;
        }
        
        return true;
    }
}
