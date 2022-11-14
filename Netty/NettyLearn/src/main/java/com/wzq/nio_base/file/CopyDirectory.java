package com.wzq.nio_base.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author wzq
 * @create 2022-11-14 21:13
 */
public class CopyDirectory {

    public static void main(String[] args) throws IOException {
        String source = "D:\\img";
        String target = "D:\\img-copy";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                // 如果是目录
                if (Files.isDirectory(path)) {
                    Files.createDirectory(Paths.get(targetName));
                } else {
                    // 是文件
                    Files.copy(path,Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
