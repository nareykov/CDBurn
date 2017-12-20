package CDBurn.CDBurn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class CDBurn {

    private List<File> listFiles = new LinkedList<>();
    private CDBurnWatcher watcher;

    public void setWatcher(CDBurnWatcher watcher) {
        this.watcher = watcher;
    }

    public void addFile(File file) {
        listFiles.add(file);
    }

    private void copyFiles() {
        changeProgress("Copy files to temp directory DiskBuffer");
        for (File file : listFiles) {
            Execute("cp -R " + file.getAbsolutePath() + " /home/ptaxom/DiskBuffer");
        }
    }

    private void createDirectory() {
        changeProgress("Create temp directory DiskBuffer");
        Execute("mkdir /home/ptaxom/DiskBuffer");
    }

    private void umountDisk() {
        changeProgress("Umount disk");
        Execute("umount /dev/sr0");
    }

    private void createISO() {
        changeProgress("Create ISO Disk.iso");
        Execute("mkisofs -v -J -o Disk.iso /home/ptaxom/DiskBuffer");
    }

    private void formatDisk() {
        changeProgress("Format CD disk");
        Execute("cdrecord -dev=/dev/sr0 -v -blank=fast");
    }

    private void writeFilesToDisk() {
        changeProgress("Record Disk.iso to CD");
        Execute("cdrecord -dev=/dev/sr0 -speed=16 -eject -v Disk.iso");
    }

    private void deleteISO() {
        changeProgress("Delete temp files");
        Execute("rm ./Disk.iso");
    }

    private void deleteFiles() {
        Execute("rm -rf /home/ptaxom/DiskBuffer");
    }

    private void changeProgress(String message) {
        watcher.setProgressValue(message);
    }

    public void burnFiles() {
        if (!listFiles.isEmpty()) {
            if (checkDisk()) {
                createDirectory();
                copyFiles();
                umountDisk();
                createISO();
                formatDisk();
                writeFilesToDisk();
                deleteISO();
                deleteFiles();
                changeProgress("Complete!");
            } else {
                watcher.showError("Not founded CD disk");
                changeProgress("Error!");
            }
            watcher.endRecord();
            listFiles.clear();
        }
    }

    private void Execute(String command) {
        Process process;
        BufferedReader reader;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", "echo "
                    + System.getenv("PASSWORD") + " |  " + command});
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.contains(" written ")) {
                    watcher.setProgressValue(line.substring(0, line.indexOf(" written ")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkDisk() {
        ProcessBuilder bash = new ProcessBuilder("bash", "-c", "df | grep 'sr0'");
        Process process;
        try {
            process = bash.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine() != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

