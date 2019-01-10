/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mp3tageditor;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import mp3tageditor.model.MP3TagHandler;

/**
 *
 * @author Ricardoc
 */
public class MP3TagEditor {

    private static void test1() throws Exception {
        File f = new File("mp3files/SampleAudio_0.4mb.mp3");
        MP3TagHandler handler = new MP3TagHandler(f);
        handler.setSongTitle("Test");
        
        System.out.println("Title: " + handler.getSongTitle());
        System.out.println("Artist: " + handler.getArtist());
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            test1();
        } catch (Exception ex) {
            Logger.getLogger(MP3TagEditor.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
    
}
