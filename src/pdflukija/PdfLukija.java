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
import java.util.HashSet;
import java.util.Set;

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
    public PdfLukija() throws IOException{
        /*
        * Main constructor
        */
        try{
            String teksti = readExistingPdfFile("/Users/rasmuspasanen/Downloads/weboodiPasanen.pdf");
            File temp = new File("temp.txt");
            FileWriter fw = new FileWriter(temp);
            fw.write(teksti);
            fw.close();
            parseStudentNumber(temp);
            String infoStudentNumber = getStudentNumber();
            parseCompletedCourses(temp);
            ArrayList infoCompletedCourses = getCompletedCourses();
            parseStartingYear(temp);
            temp.delete();
            String infoStartingYear = getStartingYear();
            System.out.println("Information: ");
            System.out.println("Student number: "+infoStudentNumber);
            System.out.println("Starting year: "+infoStartingYear);
            System.out.println("Completed courses: "+infoCompletedCourses);
            //Correct Study guide is handled here
            String studentGuide = "OpintoOpas"+getStartingYear();
            String studentGuideText = readExistingPdfFile("/Users/rasmuspasanen/Downloads/pakolliset kurssit/"+studentGuide+".pdf");
            File StudentGuideTemp = new File ("temp2.txt");
            FileWriter fw1 = new FileWriter(StudentGuideTemp);
            fw1.write(studentGuideText);
            fw1.close();
            parseMandatoryCourses(StudentGuideTemp);
            ArrayList allMandatoryCourses = getAllMandatoryCourses();
            System.out.println("All mandatory courses :"+allMandatoryCourses);
            StudentGuideTemp.delete();
            //Parsing missing courses
            missingCourses = missingCourses(allMandatoryCourses,infoCompletedCourses);
            missingCourses = getMissingCourses();
            System.out.println("Missing courses: " + missingCourses);
            
            //Creating PDF-file, by name of the student number, to report all missing courses
            createNewPdfFile(studentNumber);
            }
        catch(Exception e){
            System.out.println(e + " Exeption.");
        }
    }

    public void setMissingCourses(ArrayList missingCourses) {
        this.missingCourses = missingCourses;
    }

    public ArrayList getMissingCourses() {
        return missingCourses;
    }
    //initializing variables
    private String studentNumber;
    private ArrayList completedCourses, allCourses, missingCourses;
    private String startingYear;

    public static void main(String[] args) throws FileNotFoundException, DocumentException, IOException {
        /*
        * Main program
        */    
        PdfLukija pdfl = new PdfLukija();
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
    public ArrayList getAllMandatoryCourses(){
        return allCourses;
    }
    public void setAllCourses(ArrayList allCourses){
        this.allCourses = allCourses;
    }
    public String getStartingYear() {
        return startingYear;
    }
    public void setStartingYear(String startingYear) {
        this.startingYear = startingYear;
    }
    //Method creates new PDF-file by the name of current Student Number
    public void createNewPdfFile (String fileName) throws FileNotFoundException, DocumentException{
        Document doc = new Document();
        doc.setPageSize(PageSize.A4);
        PdfWriter.getInstance(doc, new FileOutputStream(fileName + ".pdf"));
        doc.open();
        Paragraph paragraph = new Paragraph();
        ArrayList list = getMissingCourses();
        int i = 0;
        while (list.size()>i){
            paragraph.add((String) list.get(i));
            doc.add(paragraph);
            i++;
        }
        //doc.add(paragraph);
        doc.close();
} 

    public String readExistingPdfFile (String pdfFile){
        /*
        *reading existing PdfFile by using itext-library
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
    * Parsing String from anything but numbers
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
    * Method parsing Student number from temp.txt file created from PDF
    */
    private void parseStudentNumber(File temp) throws FileNotFoundException, IOException{
        String opiskelijanumero;
        FileReader fr= new FileReader (temp);
        BufferedReader br = new BufferedReader(fr);
        int i = 0;
        while (i < 14){
            String str = br.readLine();
            if (str.contains("Opiskelijanumero")){
                opiskelijanumero = buildNumber(str);
                setStudentNumber(opiskelijanumero);
                break;
            }
            else{i++;}
        }
    }
    /*
    * Method parsing Completed courses from temp.txt file created from PDF,
    * to ArrayList setted to setCompletedCourses()
    */
    private void parseCompletedCourses(File temp) throws FileNotFoundException, IOException {
        ArrayList ar = new ArrayList();
        FileReader fr = new FileReader (temp);
        BufferedReader br = new BufferedReader(fr);
        for(String currentLine; (currentLine = br.readLine()) != null; ){
            if (currentLine.contains("Tutkintoasetus")); //do nothing
            else{
                currentLine = buildNumber(currentLine);
                if (currentLine.length()==7){
                    ar.add(currentLine);
                }
            }
        }  
        setCompletedCourses(ar);
    }
    /*
    * Method parsing Starting Year from temp.txt file created from PDF
    */
    private void parseStartingYear(File temp) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader (temp);
        BufferedReader br = new BufferedReader(fr);
        for (String currentLine; (currentLine = br.readLine()) != null;){
            if (currentLine.contains("Kirjoilletulo")){
                currentLine = buildNumber(currentLine);
                currentLine = currentLine.substring(4);
                setStartingYear(currentLine);
            }
        }
    }
    /*
    * Method parses Mandartory Courses from temp text-file, named by the starting year of studies
    */
    private void parseMandatoryCourses(File temp) throws FileNotFoundException, IOException {
        ArrayList ar = new ArrayList();
        FileReader fr = new FileReader (temp);
        BufferedReader br = new BufferedReader(fr);
        for(String currentLine; (currentLine = br.readLine()) != null; ){
            if (currentLine.contains("Tutkintoasetus")); //do nothing
            else {
                currentLine = buildNumber(currentLine);
                if (currentLine.length()>=7){
                    currentLine = currentLine.substring(0,7);
                    ar.add(currentLine);
                }
            }
        }
        setAllCourses(ar);
    }
    /*
    * Comparement of ArrayLists to parse all mandatory courses that student is yet to complete
    */
    private ArrayList missingCourses(ArrayList allMandatoryCourses, ArrayList infoCompletedCourses){
        //rivit 254 ja 255 voi kommentoida pois kun alkaa rakentamaan
        allMandatoryCourses.retainAll(infoCompletedCourses);
        setMissingCourses(allMandatoryCourses);
        /*
        * TODO
        * 1. infoCompletedCourses poistetaan ne, jotka ei kuulu allMandatoryCourses()
        * 2. allMandatoryCourses poistetaan infoCompletedCourses() jääneet alkiot, jolloin jäljelle jää suorittamattomat pakolliset
        */
        return allMandatoryCourses;
    }
}