package local.macroj.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Files {
    public static InputStream loadResource(String filePath) throws IOException {
        File localFile = new File("./" + filePath);
        InputStream is;
        if(localFile.exists()) {
            is = new FileInputStream(localFile);
        } else {
            is =  new ClassPathResource(filePath).getInputStream();
        }
        return is;
    }
}
