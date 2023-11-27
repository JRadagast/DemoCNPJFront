/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.demo.democnpjfrontend.beans;

import com.demo.democnpjfrontend.services.EmpresaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Date;
import java.util.InputMismatchException;

/**
 *
 * @author JRadagast
 */
@RequestScoped
@Named
public class Empresa {
    
    @Inject
    private EmpresaService empresaService;
    
    private Long idempresa;
    private String cnpj;
    private String razaoSocial;
    private String cidade;
    private String situacaoCadastral;
    private Date dataCadastro;
    private String endereco;
    private String telefone;
    
    private String msgSalvamento;
    private String msgCnpjInvalido;
    private Empresa paramInfo;

    public Empresa() {
    }

    public Long getIdempresa() {
        return idempresa;
    }

    public void setIdempresa(Long idempresa) {
        this.idempresa = idempresa;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(String situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getMsgSalvamento() {
        return msgSalvamento;
    }

    public void setMsgSalvamento(String msgSalvamento) {
        this.msgSalvamento = msgSalvamento;
    }

    public String getMsgCnpjInvalido() {
        return msgCnpjInvalido;
    }

    public void setMsgCnpjInvalido(String msgCnpjInvalido) {
        this.msgCnpjInvalido = msgCnpjInvalido;
    }
    
    public String doRequest(){
         if ( isValidCnpj(cnpj) ) {
            // Faz a Request da Api.
            Empresa e = empresaService.doRequest( this.cnpj );
            Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
            flash.put("empresa", e);

            // Busca contexto de navegação e executa o redirecionamento para a pagina de visualização da empresa.
            FacesContext context = FacesContext.getCurrentInstance();
            NavigationHandler handler = context.getApplication().getNavigationHandler();
            handler.handleNavigation(context, null, "empresaInformacao.xhtml?faces-redirect=true");
        } else {
            // Caso de erro, retorna mensagem de CNPJ inválido
            this.msgCnpjInvalido = "CNPJ Inválido.";
        }       
        return null;
    }
    
    public String getObjValue() {
        // Busca dados do escopo do Flash
        Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
        paramInfo = (Empresa) flash.get("empresa");

        // Verifica se o parametro é diferente de nulo, se for, preenche o objeto com os dados da memória.
        if (paramInfo != null) {
            System.out.println("paramInfo: " + paramInfo );
            this.cidade = paramInfo.getCidade();
            this.cnpj = paramInfo.getCnpj();
            this.dataCadastro = paramInfo.getDataCadastro();
            this.razaoSocial = paramInfo.getRazaoSocial();
            this.situacaoCadastral = paramInfo.getSituacaoCadastral();
            
            return null;
        }
        
        // Se não tiver, redireciona devolta para a tela inicial.
        return "index.xhtml?faces-redirect=true";
    }
    
    public String salvar(){
        // Envia a request para a API atualizar o objeto no banco de dados.
        
        if ( empresaService.validateSave( this ) ){
           this.msgSalvamento = empresaService.salvar( this );
        } else {
            this.msgSalvamento = "Digite um telefone e um endereço válido.";
        }
        return null;
    }
    
//    public boolean isValidCnpj(String cnpj) {;
//        // Remove any non-digit characters
//        cnpj = cnpj.replaceAll("\\D", "");
//
//        // Check if CNPJ has 14 digits
//        if (cnpj.length() != 14) {
//            return false;
//        }
//
//        // Check for known invalid CNPJs (e.g., 00000000000000)
//        if (cnpj.matches("(\\d)\\1+")) {
//            return false;
//        }
//
//        // Calculate the first verification digit
//        int sum = 0;
//        for (int i = 0; i < 12; i++) {
//            sum += Character.getNumericValue(cnpj.charAt(i)) * (13 - i);
//        }
//        int remainder = sum % 11;
//        int digit1 = (remainder < 2) ? 0 : (11 - remainder);
//
//        // Calculate the second verification digit
//        sum = 0;
//        for (int i = 0; i < 13; i++) {
//            sum += Character.getNumericValue(cnpj.charAt(i)) * (14 - i);
//        }
//        remainder = sum % 11;
//        int digit2 = (remainder < 2) ? 0 : (11 - remainder);
//
//        // Check if the calculated digits match the provided ones
//        return Character.getNumericValue(cnpj.charAt(12)) == digit1 &&
//               Character.getNumericValue(cnpj.charAt(13)) == digit2;
//    }
    
    /**
   * Realiza a validação de um cnpj
   * 
   * @param cnpj String - o CNPJ pode ser passado no formato 99.999.999/9999-99 ou 99999999999999
   * @return boolean
   */
    private boolean isValidCnpj(String cnpj) {
        // Remove todos os caracteres que não são digitos
        cnpj = cnpj.replaceAll("\\D", "");

        try{
            Long.parseLong(cnpj);
        } catch(NumberFormatException e){
            return false;
        }

        // Verifica se o CNPJ tem 14 digitos
        if (cnpj.length() != 14) {
            return false;
        }

        // Verifica CNPJs invalidos já conhecidos. (exemplo: 00000000000000)
        if (cnpj.matches("(\\d)\\1+")) {
            return false;
        }
        
        
        char dig13, dig14;
        int sm, i, r, num, peso; 
        
        try {
            // Calculo do primeiro Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {
              // converte o i-ésimo caractere do CNPJ em um número: // por
              // exemplo, transforma o caractere '0' no inteiro 0 // (48 é a
              // posição de '0' na tabela ASCII)
              num = (int) (cnpj.charAt(i) - 48);
              sm = sm + (num * peso);
              peso = peso + 1;
              if (peso == 10)
                peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
              dig13 = '0';
            else
              dig13 = (char) ((11 - r) + 48);

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
              num = (int) (cnpj.charAt(i) - 48);
              sm = sm + (num * peso);
              peso = peso + 1;
              if (peso == 10)
                peso = 2;
            }
            r = sm % 11;
            if ((r == 0) || (r == 1))
              dig14 = '0';
            else
              dig14 = (char) ((11 - r) + 48);
            
            // Verifica se os dígitos calculados conferem com os dígitos do cnpj.
            if ((dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13)))
              return (true);
            else
              return (false);
            
        } catch (InputMismatchException ex) {
            return (false);
        }
    }
    

    @Override
    public String toString() {
        return "Empresa{" + "idempresa=" + idempresa + ", cnpj=" + cnpj + ", razaoSocial=" + razaoSocial + ", cidade=" + cidade + ", situacaoCadastral=" + situacaoCadastral + ", dataCadastro=" + dataCadastro + ", endereco=" + endereco + ", telefone=" + telefone + '}';
    }
    
}
