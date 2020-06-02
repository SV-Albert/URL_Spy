import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileManager {

    private Path pathToSave;
    private Monitor monitor;


    public FileManager(Path path, Monitor monitor){
        pathToSave = path;
        this.monitor = monitor;
    }

    public void save() throws IOException {
        if(!Files.exists(pathToSave)){
            Files.createFile(pathToSave);
        }

        PrintWriter writer = new PrintWriter(new File(String.valueOf(pathToSave)));
        writer.print(getSaveData());
        writer.close();
    }

    public String getSaveData(){
        HashMap<String, ArrayList<String>> urlMap = monitor.getUrlMap();
        List<Integer> hashes = monitor.getHashes();

        StringBuilder builder = new StringBuilder();
        SimpleDateFormat sdt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date now = new Date();
        builder.append("---URL_Update_Checker Save File " + sdt.format(now) + "---" + "\n");
        builder.append("<Hashed values>" + "\n");
        for (Integer hash: hashes) {
            builder.append("#" + hash + "\n");
        }
        builder.append("</Hashed values>" + "\n");
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
        for (String logEntry: monitor.getLogs()) {
            builder.append("~" + logEntry + "\n");
        }
        builder.append("</Logs>" + "\n");

        return builder.toString();
    }

    public ArrayList<Integer> loadHashes() throws IOException {
        List<String> lines = Files.readAllLines(pathToSave);
        lines.removeIf(line -> !line.startsWith("#"));
        ArrayList<Integer> hashes = new ArrayList<>();
        for (String line: lines) {
            hashes.add(Integer.valueOf(line.replaceAll("#", "")));
        }
        return hashes;
    }
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
