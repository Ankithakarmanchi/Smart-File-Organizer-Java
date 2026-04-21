package ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.util.*;

public class MainUI {

    // store moves for undo
    private Map<File, File> moveHistory = new HashMap<>();

    public void createWindow() {

        JFrame frame = new JFrame("Smart File Organizer");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JButton selectButton = new JButton("Select Folder");
        JButton undoButton = new JButton("Undo");

        JLabel pathLabel = new JLabel("No folder selected");
        JLabel statsLabel = new JLabel("Stats will appear here");

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> fileList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(fileList);

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(selectButton);
        topPanel.add(undoButton);
        topPanel.add(pathLabel);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statsLabel, BorderLayout.SOUTH);

        // SELECT FOLDER
        selectButton.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = chooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {

                moveHistory.clear();
                listModel.clear();

                File selectedFolder = chooser.getSelectedFile();
                pathLabel.setText("Selected: " + selectedFolder.getAbsolutePath());

                File[] files = selectedFolder.listFiles();

                int docs = 0, images = 0, videos = 0, code = 0, others = 0;

                if (files != null) {
                    for (File file : files) {

                        if (file.isFile()) {

                            String name = file.getName();
                            String category = getCategory(name);

                            // count stats
                            switch (category) {
                                case "Documents": docs++; break;
                                case "Images": images++; break;
                                case "Videos": videos++; break;
                                case "Code": code++; break;
                                default: others++;
                            }

                            // rename file (clean)
                            String newName = cleanFileName(name);

                            File categoryFolder = new File(selectedFolder, category);
                            if (!categoryFolder.exists()) {
                                categoryFolder.mkdir();
                            }

                            try {
                                Path source = file.toPath();
                                Path destination = new File(categoryFolder, newName).toPath();

                                Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);

                                moveHistory.put(destination.toFile(), source.toFile());

                                listModel.addElement(name + " → " + category);

                            } catch (Exception ex) {
                                listModel.addElement(name + " → error");
                            }
                        }
                    }
                }

                // update stats
                statsLabel.setText("Docs: " + docs + " | Images: " + images +
                        " | Videos: " + videos + " | Code: " + code + " | Others: " + others);
            }
        });

        // UNDO FEATURE
        undoButton.addActionListener(e -> {

            for (Map.Entry<File, File> entry : moveHistory.entrySet()) {
                try {
                    Files.move(entry.getKey().toPath(),
                            entry.getValue().toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ex) {
                    System.out.println("Undo failed");
                }
            }

            moveHistory.clear();
            listModel.addElement("Undo completed");
        });

        frame.setVisible(true);
    }

    // SMART CATEGORY
    private String getCategory(String fileName) {
        fileName = fileName.toLowerCase();

        if (fileName.contains("resume") || fileName.contains("cv")) return "Resume";

        if (fileName.contains("invoice") || fileName.contains("bill")) return "Bills";

        if (fileName.endsWith(".pdf") || fileName.endsWith(".docx")) return "Documents";

        if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg"))
            return "Images";

        if (fileName.endsWith(".mp4")) return "Videos";

        if (fileName.endsWith(".py") || fileName.endsWith(".java"))
            return "Code";

        return "Others";
    }

    // CLEAN FILE NAME
    private String cleanFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\.]", "_");
    }
}