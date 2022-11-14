package com.wzq.nio_base.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 遍历目录
 *
 * @author wzq
 * @create 2022-11-14 21:04
 */
public class TestFileWalkFileTree {

    public static void main(String[] args) throws IOException {

        AtomicInteger disCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();

        Files.walkFileTree(Paths.get("D:\\Program Files\\Java\\jdk1.8.0_301"), new SimpleFileVisitor<Path>() {
            // 访问目录
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                disCount.incrementAndGet();
                System.out.println("====>" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            // 访问文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("dir count: " + disCount);
        System.out.println("file count: " + fileCount);
    }

}
