/*
 * Copyright 2024 Azul Systems, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package input;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class BytecodeExtractor {

    static public List<String> extractJarFile(String jarFilePath) {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            List<String> classNames = new ArrayList<>();
            Enumeration<JarEntry> entries = jarFile.entries();
            if (!entries.hasMoreElements()) {
                System.out.println("JAR IS EMPTY");
            }
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getRealName();
                if (entryName.endsWith(".class")) {
                    classNames.add(entryName);
                }

            }

            return classNames;

        } catch (java.io.IOException e) {
            throw new RuntimeException(e);

        }

    }

    static public List<String> extractZipFile(String zipFilePath) {
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            List<String> classNames = new ArrayList<>();
            var entries = zipFile.entries();
            if (!entries.hasMoreElements()) {
                System.out.println("JAR IS EMPTY");
            }
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                System.out.println(entryName);

                if (entryName.endsWith(".class")) {
                    classNames.add(entryName);
                }

            }
            return classNames;


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getSourceFile(String className) {

        Path p = Paths.get(URI.create("jrt:/")).resolve("/modules/java.base/" + className);
        return p.getFileName().toString();

    }

}
