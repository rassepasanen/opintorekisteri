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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
    private ArrayList completedCourses;
    private String startingYear;

    public static void main(String[] args) throws FileNotFoundException, DocumentException, IOException {
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
        //Siirretään teksti (string) tekstitiedostoon
        File temp = new File("temp.txt");
        FileWriter fWriter = new FileWriter(temp);
        fWriter.write(teksti);
        fWriter.close();
        //Haetaan tiedostosta tietoja
        new PdfLukija().parseStudentNumber(temp);
        new PdfLukija().parseStartingYear(temp);
        new PdfLukija().parseCourseNumbers(temp);
    }
    public String getStudentNumber() {
        return studentNumber;
    }
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    public ArrayList getCompletedCourses() {
        return completedCourses;
    }
    public void setCompletedCourses(ArrayList completedCourses) {
        this.completedCourses = completedCourses;
    }  
    public String getStartingYear() {
        return startingYear;
    }
    public void setStartingYear(String startingYear) {
        this.startingYear = startingYear;
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
        * 
        */
        try {
            PdfReader reader = new PdfReader(pdfFile);
            StringBuilder sb = new StringBuilder();
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		TextExtractionStrategy strategy;
                for (int i = 1; i <= reader.getNumberOfPages(); i++){
                    strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
                    sb.append(strategy.getResultantText());
                }
                reader.close();
                return sb.toString();
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Not able to read file " + pdfFile, e);
        }
    }

    /*
    * Metodi poistaa stringistä kaikki kirjaimet
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
    /*
    * Metodi etsii tiedostosta opiskelijanumeron
    */
    private void parseStudentNumber(File temp) throws FileNotFoundException, IOException{
        String opiskelijanumero;
        FileReader fr= new FileReader (temp);
        BufferedReader br = new BufferedReader(fr);
        int i = 0;
        while (i < 14){
            String str = br.readLine();
            if (str.contains("Opiskelijanumero")){
                System.out.println("Opiskelijanumero löytyy tältä riviltä");
                opiskelijanumero = new PdfLukija().buildNumber(str);
                System.out.println("opiskelijanumero: " + opiskelijanumero);
                setStudentNumber(opiskelijanumero);
                System.out.println (getStudentNumber());
               //setteri opiskelijanumerolle, jonka arvoksi asetetaan opiskelijanumero
                break;
            }
            else{i++;}
        }    
    }
    /*
    * Metodi muodostaa ArrayListan, johon syötetään kurssien koodit,
    * poistamalla kirjaimet ja valitsemalla ne rivit, joihin jää 7 numeroa
    * Metodi lukee tiedostoa ehdolla, ettei currentLine, eikä nextLine ole tyhjiä
    */
    private void parseCourseNumbers(File temp) throws FileNotFoundException, IOException {
        ArrayList ar = new ArrayList();
        FileReader fr = new FileReader (temp);
        BufferedReader br = new BufferedReader(fr);
        int count = 0;
        for(String currentLine; (currentLine = br.readLine()) != null; ){
            if (currentLine.contains("Tutkintoasetus")){
                //do nothing
            }
            else{
                currentLine = new PdfLukija().buildNumber(currentLine);
                if (currentLine.length()==7){
                
                    count++;
                    ar.add(currentLine);
                }
            }
        }
        System.out.println("Suoritetut kurssit: ");
        System.out.println(ar);
        System.out.println("Suoritettuja kursseja: " + count);     
        setCompletedCourses(ar);
    }
    /*
    * Metodi etsii PDF-tiedostosta opintojen alkamisvuoden
    */
    private void parseStartingYear(File temp) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader (temp);
        BufferedReader br = new BufferedReader(fr);
        for (String currentLine; (currentLine = br.readLine()) != null;){
            if (currentLine.contains("Kirjoilletulo")){
                currentLine = new PdfLukija().buildNumber(currentLine);
                currentLine = currentLine.substring(4);
                System.out.println("Opinnot alkanut: " + currentLine);
                //startingYear = currentLine;
                setStartingYear(currentLine);
            }
        }
    }
}
