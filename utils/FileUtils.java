package utils;

public class FileUtils {

    public static String getCategory(String fileName) {

        fileName = fileName.toLowerCase();

        if (fileName.contains("resume") || fileName.contains("cv"))
            return "Resume";

        if (fileName.contains("invoice") || fileName.contains("bill"))
            return "Bills";

        if (fileName.endsWith(".pdf") || fileName.endsWith(".docx"))
            return "Documents";

        if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg"))
            return "Images";

        if (fileName.endsWith(".mp4"))
            return "Videos";

        if (fileName.endsWith(".py") || fileName.endsWith(".java"))
            return "Code";

        return "Others";
    }

    public static String cleanFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\.]", "_");
    }
}