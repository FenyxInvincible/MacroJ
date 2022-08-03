package local.macroj.utils;

import com.google.common.base.Charsets;
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
    public static String readFile(String filePath) throws IOException {
        return com.google.common.io.Files.asCharSource(new File(filePath), Charsets.UTF_8).read();
    }
}
