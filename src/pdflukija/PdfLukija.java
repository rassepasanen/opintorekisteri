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
        
        String studentNumber = "266972";
        String fileName = "/Users/rasmuspasanen/Downloads/weboodiPasanen.pdf";
        
        //fileName tuodaan lopulta opiskelijanumerosta
        new PdfLukija().createNewPdfFile(studentNumber);
        new PdfLukija().readExistingPdfFile(fileName);
        System.out.println(fileName);
        
        

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
    * Tapa 1: (Helpompi, mutta hitaampi)
    * Luetaan PDF-tiedosto tekstimuotoon, jota karsitaan (mitä keinoja?), 
    * kunnes suoritettujen opintojen numerosarjat ovat tallella
    *
    * Tapa 2: (Vaikeampi, mutta tehokkaampi)
    * R-kielellä luodaan metodi, joka siirtää PDF-tiedoston SQL-tauluun,
    * josta tiedot siirretään käsiteltäväksi Javaan.
    */
    
    public void readExistingPdfFile (String pdfFile){
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
                    System.out.println(sb.append((strategy.getResultantText())));
                    //Tähän for silmukka, joka etsii kurssitunnisteet dokumentista
                    
                }
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Not able to read file " +pdfFile,e);
            
        }
    }
    
    //metodi etsii dokumentista opiskelijanumeron
    public void setStudentNumber(String num){
        this.studentNumber = num;
    }
    public String getStudentNumber(){
        return this.studentNumber;
    }
    //tämä metodi lukee pdf-tiedostossa olevat kurssinumerot ja arvosanat
    public static void readCredits (String fileName){
        /*PdfReader reader = new PdfReader();*/
    }
    
    
}
