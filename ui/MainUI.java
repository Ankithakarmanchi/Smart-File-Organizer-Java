package ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.FileOrganizer;

public class MainUI {

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

        selectButton.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = chooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {

                File folder = chooser.getSelectedFile();
                pathLabel.setText("Selected: " + folder.getAbsolutePath());

                listModel.clear();
                moveHistory.clear();

                List<String> output = new ArrayList<>();

                moveHistory = FileOrganizer.organize(folder, output);

                int docs = 0, images = 0, videos = 0, code = 0, others = 0;

                for (String line : output) {
                    listModel.addElement(line);

                    if (line.contains("Documents")) docs++;
                    else if (line.contains("Images")) images++;
                    else if (line.contains("Videos")) videos++;
                    else if (line.contains("Code")) code++;
                    else others++;
                }

                statsLabel.setText("Docs: " + docs + " | Images: " + images +
                        " | Videos: " + videos + " | Code: " + code + " | Others: " + others);
            }
        });

        undoButton.addActionListener(e -> {
            FileOrganizer.undo(moveHistory);
            listModel.addElement("Undo completed");
        });

        frame.setVisible(true);
    }
}