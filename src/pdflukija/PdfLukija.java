/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdflukija;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 *
 * @author rasmuspasanen
 */
public class PdfLukija {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws com.itextpdf.text.DocumentException
     */
    private String studentNumber;
    
    public static void main(String[] args) throws FileNotFoundException, DocumentException {
        /*
        * Pääohjelma
        */
        String studentNumber = "123456";
        System.out.println("Opiskelijanro aluksi:");
        System.out.println(studentNumber);
        String fileName = "/Users/rasmuspasanen/Downloads/weboodiPasanen.pdf";
        
        //fileName tuodaan lopulta opiskelijanumerosta
        new PdfLukija().createNewPdfFile(studentNumber);
        //siirretään tiedot PDF to String
        String teksti;
        teksti = new PdfLukija().readExistingPdfFile(fileName);
        System.out.println(teksti);
        String teksti2;
        teksti2 = new PdfLukija().buildNumber(teksti);
        studentNumber = new PdfLukija().parseStudentNumber(teksti2, studentNumber);
        System.out.println("Opiskelijanro lopuksi:");
        System.out.println(studentNumber);
        //
        new PdfLukija().parseStringKurssinumero(teksti);
        
        
        

    }
    //tämä metodi luo uuden pdf-tiedoston käsittelyssä olevalla opiskelijanumerolla
    public void createNewPdfFile (String fileName) throws FileNotFoundException, DocumentException{
        Document doc = new Document();
        doc.setPageSize(PageSize.A4);
        PdfWriter.getInstance(doc, new FileOutputStream(fileName + ".pdf"));
        doc.open();
        Paragraph paragraph = new Paragraph();
        paragraph.add("Moves credits to pdf file.");
        paragraph.add("");
        doc.add(paragraph);
        doc.close();
}
    
    /* readExistingPdfFile() -toteutusvaihtoehdot:
    * KÄYTÖSSÄ:
    * Tapa 1: (Helpompi, mutta hitaampi)
    * Luetaan PDF-tiedosto tekstimuotoon, jota karsitaan (mitä keinoja?), 
    * kunnes suoritettujen opintojen numerosarjat ovat tallella
    * Metodi palauttaa PDF:n sisällön String muodossa
    * EI KÄYTÖSSÄ:
    * Tapa 2: (Vaikeampi, mutta tehokkaampi)
    * R-kielellä luodaan metodi, joka siirtää PDF-tiedoston SQL-tauluun,
    * josta tiedot siirretään käsiteltäväksi Javaan.
    */
    public String readExistingPdfFile (String pdfFile){
        /*
        * https://www.programcreek.com/java-api-examples/?class=com.itextpdf.text.pdf.PdfReader&method=close
        * 1st Example from source
        */
        try {
            PdfReader reader = new PdfReader(pdfFile);
            StringBuilder sb = new StringBuilder();
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		TextExtractionStrategy strategy;
                for (int i = 1; i <= reader.getNumberOfPages(); i++){
                    strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
                    sb.append(strategy.getResultantText());
                    //tulostaa testiksi koko pdf-tiedoston sisällön
                    //System.out.println(sb.append((strategy.getResultantText())));
                    
                    //Tähän for silmukka, joka etsii kurssitunnisteet dokumentista
                }
                reader.close();
                return sb.toString();
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Not able to read file " + pdfFile, e);
            
        }
    }

    private String parseStringKurssinumero(String teksti) {
        return null;
        //Ohjelma poistaa tekstistä kaiken ylimääräisen, paitsi kurssinumerot
    }

    /*
    * Seuraavat kaksi metodia (buildNumber ja parseStudentNumber) etsivät tekstistä
    * kaikki numerot, jonka jälkeen etsivät numeroiden joukosta opiskelijanumeron.
    * Molempia kutsutaan pääohjelmasta.
    */
    private String buildNumber(String str) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if(Character.isDigit(ch)) 
                strBuilder.append(ch);
            }
        return strBuilder.toString();
}
    private String parseStudentNumber(String teksti2, String studentNumber){
        
        teksti2 = teksti2.substring(48);
        System.out.println("Tekstin ensimmäiset numerot poistettu:");
        System.out.println(teksti2);
        studentNumber = teksti2.substring(0, 6);
        System.out.println("Opiskelijanumero keskellä: ");
        System.out.println(studentNumber);
        return studentNumber;
    }
    
    
}
