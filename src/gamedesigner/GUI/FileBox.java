/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.GUI;

import java.io.File;

/**
 *
 * @author Wojtek
 */
public class FileBox {
    private final String ext;
    private final File selectedFile;
    private final File directory;

    public FileBox(String ext, File selectedFile, File directory) {
        this.ext = ext;
        this.selectedFile = selectedFile;
        this.directory = directory;
    }
    
    public String getFileType() {
        return ext;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public File getDirectory() {
        return directory;
    }
    
}
