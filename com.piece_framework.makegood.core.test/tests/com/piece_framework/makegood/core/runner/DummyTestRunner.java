package com.piece_framework.makegood.core.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DummyTestRunner extends Thread {
    private File xmlFile;
    private List<File> fragmentFiles;
    private int interval;

    public DummyTestRunner(File xmlFile,
                           List<File> fragmentFiles,
                           int interval
                           ) {
        this.xmlFile = xmlFile;
        this.fragmentFiles = fragmentFiles;
        this.interval = interval;
    }

    @Override
    public void run() {
        for (File fragmentFile: fragmentFiles) {
            StringBuilder fragment = new StringBuilder();
            try {
                FileReader reader = new FileReader(fragmentFile);
                int read = 0;
                while ((read = reader.read()) != -1) {
                    fragment.append((char) read);
                }
                reader.close();

                FileWriter writer = new FileWriter(xmlFile, true);
                writer.write(fragment.toString());
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void waitForEnd() {
        try {
            while (true) {
                if (!isAlive()) {
                    break;
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
