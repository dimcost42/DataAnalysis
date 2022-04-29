import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainClass {

    public static void main(String[] args) throws ParseException {

        List<Error> errors = error("C:\\Users\\dimcost42\\Downloads\\0820599020-20220422T154317Z-001\\0820599020\\logger", "server error", ".log");

        errors.forEach(System.out::println);
        ExcelWriter excelWriter = new ExcelWriter("C:\\Users\\dimcost42\\Downloads\\test.xlsx");
        excelWriter.write(errors);
    }

    private static List<Error> error(String folder, String error, String fileEndsWith) {
        HashMap<String, Error> errorHashMap = new HashMap<>();

        File rootFolder = new File(folder);
        String[] filePaths = null;
        if (fileEndsWith != null) {
            filePaths = rootFolder.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(fileEndsWith);
                }
            });
        } else {
            filePaths = rootFolder.list();
        }
        for (String f : filePaths) {
            errorHashMap.putAll(findErrorsInFile(rootFolder.getPath() + File.separator + f, error));
        }
        return errorHashMap.values().stream().sorted((i1, i2) ->
                {
                    try {
                        return Long.compare(new SimpleDateFormat("dd/MM/yyyy").parse(i1.getDate()).getTime(), new SimpleDateFormat("dd/MM/yyyy").parse(i2.getDate()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
        ).collect(Collectors.toList());
    }

    private static HashMap<String, Error> findErrorsInFile(String file, String keywordFound) {
        HashMap<String, Error> errorHashMap = new HashMap<>();

        List<String> lines = readFile(file);

        for (String s : lines) {
            if (s.toLowerCase(Locale.ROOT).contains(keywordFound.toLowerCase(Locale.ROOT))) {
                if (errorHashMap.containsKey(keyTimeStamp(s))) {
                    errorHashMap.get(keyTimeStamp(s)).increaseTimes();
                } else {
                    errorHashMap.put(keyTimeStamp(s), new Error(keyTimeStamp(s)));
                }
            }
        }
//        for (Error error : errorHashMap.values()) {
//            System.out.println(error.toString());
//        }
        return errorHashMap;
    }

    private static String keyTimeStamp(String timeStamp) {
        if (timeStamp.length() < 10) {
            return "<10";
        }
        if (!timeStamp.contains("-")) {
            return "-";
        }
        String time = timeStamp.substring(0, 10);
        String[] timeFixer = time.split("-");
        if (timeFixer.length < 3) {
            return time;
        }
        return timeFixer[1] + "/" + timeFixer[2] + "/" + timeFixer[0];
    }

    private static String timestampFormatted() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public static List<String> readFile(String path) {
        try {
            return Files.readAllLines(Paths.get(path), Charset.forName(UniversalDetector.detectCharset(new File(path))));
        } catch (Exception e) {
            System.out.println(path);
            e.printStackTrace();
        }
        return List.of("");
    }
}
