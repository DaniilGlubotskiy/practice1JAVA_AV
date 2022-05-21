import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.html.HTMLDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) throws Exception {
        //FileSystemInfo();
        //WriteFile();
        //ReadFile();
        //DeleteFile();
        //JsonWriter();
        JsonReader();
    }

    public static void FileSystemInfo() {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File[] roots = fileSystemView.getRoots();
        for (int i = 0; i < roots.length; i++) {
            System.out.println("Root: " + roots[i]);
        }

        System.out.println("Home directory: " + fileSystemView.getHomeDirectory());

        File[] f = File.listRoots();
        for (int i = 0; i < f.length; i++) {
            System.out.println("Drive: " + f[i]);
            System.out.println("Display name: " + fileSystemView.getSystemDisplayName(f[i]));
            System.out.println("Is drive: " + fileSystemView.isDrive(f[i]));
            System.out.println("Is floppy: " + fileSystemView.isFloppyDrive(f[i]));
            System.out.println("Readable: " + f[i].canRead());
            System.out.println("Writable: " + f[i].canWrite());
            System.out.println("Total space: " + f[i].getTotalSpace());
            System.out.println("Usable space: " + f[i].getUsableSpace());
        }
    }

    public static void WriteFile() {
        try (FileWriter fileWriter = new FileWriter("test.txt", false)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите строку для записи в файл");
            String text = scanner.nextLine();
            fileWriter.write(text);
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void ReadFile() {
        try (FileReader fileReader = new FileReader("test.txt")) {
            int c;
            System.out.println("Содержимое файла: ");
            while ((c = fileReader.read()) != -1) {
                System.out.print((char) c);
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void DeleteFile() {
        File file = new File("test.txt");
        if (file.delete()) {
            System.out.println("Файл был успешно удален с корневой папки проекта");
        } else {
            System.out.println("Файл не был найден.");
        }
    }

    public static void JsonWriter() {
        JSONObject personInfo = new JSONObject();

        personInfo.put("surname", "Glubotskiy");
        personInfo.put("name", "Daniil");
        personInfo.put("age", new Integer(20));

        JSONArray groups = new JSONArray();
        groups.add("BSBO-01-19");
        groups.add("BSBO-02-19");
        groups.add("BSBO-03-19");

        personInfo.put("groups", groups);

        try {
            FileWriter file = new FileWriter("jsonFile.txt");
            file.write(personInfo.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void JsonReader() throws ParseException, FileNotFoundException, IOException{
        try{
            JSONParser parser = new JSONParser();
            Reader reader = new FileReader("jsonFile.txt");
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println("Содержимое json-файла");
            String surname = (String) jsonObject.get("surname");
            System.out.println("Surname =" + surname);
            String name = (String) jsonObject.get("name");
            System.out.println("Name = " + name);
            long age = (Long) jsonObject.get("age");
            System.out.println("Age = " + age);
            JSONArray groups = (JSONArray) jsonObject.get("groups");
            Iterator it = groups.iterator();
            while (it.hasNext()) {
                System.out.println("Group = " + it.next());
            }
            reader.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void WriteXML() throws ParserConfigurationException,IOException, SAXException, TransformerException{
        File xmlFile = new File("xmlFile.xml");
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        Element element = doc.getDocumentElement();
        Element item = doc.createElement("item");
        Attr name = doc.createAttribute("Name");
        name.setValue("Glubotskiy");
        Attr group = doc.createAttribute("group");
        group.setValue("BSBO-02-19");
        item.setAttributeNode(name);
        item.setAttributeNode(group);
        element.appendChild(item);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult streamResult = new StreamResult(new File("xmlFile.xml"));
        DOMSource source = new DOMSource(doc);
        transformer.transform(source,streamResult);
    }

    public static void AddZip()
    {
        JFileChooser fileopen = new JFileChooser("./");
        int ret = fileopen.showDialog(null, "Открыть файл");
        String filename = fileopen.getSelectedFile().getName();
        try(ZipOutputStream zout = new ZipOutputStream(new FileOutputStream("output.zip"));
            FileInputStream fis= new FileInputStream(filename);)
        {
            ZipEntry entry1=new ZipEntry(filename);
            zout.putNextEntry(entry1);
            // считываем содержимое файла в массив byte
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            // добавляем содержимое к архиву
            zout.write(buffer);
            // закрываем текущую запись для новой записи
            zout.closeEntry();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    public static void ExtractZip()
    {
        try(ZipInputStream zin = new ZipInputStream(new FileInputStream("output.zip")))
        {
            ZipEntry entry;
            String name;
            long size;
            while((entry=zin.getNextEntry())!=null){

                name = entry.getName(); // получим название файла
                size=entry.getSize();  // получим его размер в байтах
                System.out.printf("File name: %s \t File size: %d \n", name, size);

                // распаковка
                FileOutputStream fout = new FileOutputStream(name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        }
        catch(Exception ex){

            System.out.println(ex.getMessage());
        }
    }
}
