import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A class responsible for generating save data, writing it to a file and retrieving
 * data from a file
 *
 * @version 0.2
 * @author Albert Shakirzianov
 */
public class SaveManager {

    private final Path pathToSave;
    private final DataManager dataManager;

    /**
     * Constructor for the SaveManager class
     *
     * @param dataManager
     * @throws IOException
     */
    public SaveManager(DataManager dataManager) throws IOException {
        pathToSave = Paths.get(System.getProperty("user.dir") + "\\.url_spy_data.txt");
        this.dataManager = dataManager;

        if(!Files.exists(pathToSave)){
            Files.createFile(pathToSave);
        }
    }

    /**
     * Write the save data to a file
     *
     * @throws IOException
     */
    public void save() throws IOException {
        if(!Files.exists(pathToSave)){
            Files.createFile(pathToSave);
        }

        OutputStream outputStream = new FileOutputStream(pathToSave.toString());
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.print(getSaveData());
        writer.close();
    }

    /**
     * Generate a String with save data
     *
     * @return save data
     */
    public String getSaveData(){
        HashMap<String, ArrayList<String>> urlMap = dataManager.getUrlKeyMap();

        StringBuilder builder = new StringBuilder();
        SimpleDateFormat sdt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date now = new Date();
        builder.append("---URL Spy Save File " + sdt.format(now) + "---" + "\n");
        builder.append("<URLs + keywords>" + "\n");
        for (String url: urlMap.keySet()) {
            builder.append(url + "|");
            if(!urlMap.get(url).isEmpty()){
                for (String keyword: urlMap.get(url)) {
                    builder.append(keyword + ",");
                }
                builder.deleteCharAt(builder.lastIndexOf(","));
            }
            builder.append("\n");
        }
        builder.append("</URLs + keywords>" + "\n");
        builder.append("<Logs>" + "\n");
        for (String logEntry: dataManager.getLogs()) {
            builder.append("~" + logEntry + "\n");
        }
        builder.append("</Logs>" + "\n");

        return builder.toString();
    }

    /**
     * Retrieve the URL to keywords map from a save file
     *
     * @return URL to list of keywords Hashmap
     * @throws IOException
     */
    public HashMap<String, ArrayList<String>> loadKeyMap() throws IOException{
        List<String> lines = Files.readAllLines(pathToSave);
        lines.removeIf(line -> !line.contains("|"));
        HashMap<String, ArrayList<String>> keyMap = new HashMap<>();
        for(String line: lines){
            String url = line.substring(0, line.indexOf('|'));
            ArrayList<String> keywords = new ArrayList<>(Arrays.asList(line.substring(line.indexOf('|') + 1).split(",")));
            keyMap.put(url, keywords);
        }
        return keyMap;
    }

    /**
     * Retrieve the logs data from a save file
     *
     * @return list of logs
     * @throws IOException
     */
    public ArrayList<String> loadLogs() throws IOException{
        List<String> lines = Files.readAllLines(pathToSave);
        lines.removeIf(line -> !line.contains("~"));
        ArrayList<String> logs = new ArrayList<>();
        for(String line: lines){
            logs.add(line.substring(1));
        }
        return logs;
    }
}
