/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * DConfig - Free Dynamic Configuration Toolkit
 * Copyright (C) 2006, 2007 Jonathan Luo
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package org.moonwave.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Jonathan Luo
 */
public class FileUtil {

    private static final Log log = LogFactory.getLog(FileUtil.class);

    public FileUtil() {
    }

    public static File createDirectory(String path) {
        File dir = new File(path);

        // if the directory does not exist, create it
        if (!dir.exists()) {
            try{
                dir.mkdir();
            } catch(SecurityException se){
                //handle it
            }
        }
        return dir;
    }

    /**
     * Returns URL for a relative file path.
     * 
     * @param filePath file path
     * @return URL for a relative file path.
     */
    public static java.net.URL toURL(String filePath) {
        java.net.URL fileURL = FileUtil.class.getClassLoader().getResource(filePath);
        if (fileURL == null)
            fileURL = ClassLoader.class.getResource("/" + filePath);
        if (fileURL == null) {
            try {
                fileURL = new java.net.URL(filePath);
            } catch (Exception e)
            {}
        }
        return fileURL;
    }


    public static void copy(File fromFile, File toFile)
                       throws IOException  {

        String fromFilename = fromFile.getPath();
        String toFileName = toFile.getPath();

        if (!fromFile.exists())
            throw new IOException("FileCopy: " + "no such source file: " + fromFilename);
        if (!fromFile.isFile())
            throw new IOException("FileCopy: " + "can't copy directory: "  + fromFilename);
        if (!fromFile.canRead())
            throw new IOException("FileCopy: " + "source file is unreadable: " + fromFilename);

        if (toFile.isDirectory())
            toFile = new File(toFile, fromFile.getName());

        if (toFile.exists()) {
            if (!toFile.canWrite())
                throw new IOException("FileCopy: " + "destination file is unwriteable: " + toFileName);
            System.out.print("Overwrite existing file " + toFile.getName() + "? (Y/N): ");
            System.out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String response = in.readLine();
            if (!response.equals("Y") && !response.equals("y"))
                throw new IOException("FileCopy: " + "existing file was not overwritten.");
        } else {
            String parent = toFile.getParent();
            if (parent == null)
                parent = System.getProperty("user.dir");
            File dir = new File(parent);
            if (!dir.exists())
                throw new IOException("FileCopy: "
                + "destination directory doesn't exist: " + parent);
            if (dir.isFile())
                throw new IOException("FileCopy: "
                + "destination is not a directory: " + parent);
            if (!dir.canWrite())
                throw new IOException("FileCopy: "
                + "destination directory is unwriteable: " + parent);
        }
        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1)
                to.write(buffer, 0, bytesRead); // write
        } finally {
            if (from != null)
                try {
                    from.close();
                } catch (IOException e) {
                    ;
                }
            if (to != null)
                try {
                    to.close();
                } catch (IOException e) {
                    ;
                }
        }        
    }
    
    public static void copy(java.net.URI fromFileName, java.net.URI toFileName)
                       throws IOException  {
        copy(new File(fromFileName), new File(toFileName));
    }

    public static void copyResource(String fromFileName, String toFileName)
                       throws IOException  {
        try {
            java.net.URL fromURL = toURL(fromFileName);
            java.net.URL toURL = toURL(toFileName);
            copy(fromURL.toURI(), toURL.toURI());
        } catch (Exception e) {
            throw new IOException("File copy failed");
        }
    }
    
    public static void copyFromJarToLocal(String fromFilename, String localFilename)
                       throws IOException  {
        try {
            java.net.URL fromURL = toURL(fromFilename);
            copy(new File(fromURL.toURI()), new File(localFilename));
        } catch (Exception e) {
            throw new IOException("File copy failed");
        }
    }
    
    /**
     * Copies the contents of source file to a target file. Creates the target file
     * automatically if it does not exist.
     * 
     * @param sFromFile the source file physical path.
     * @param sToFile the target file physical path.
     * @throws IOException
     */
    public static void copyTextFile(String sFromFile, String sToFile) throws IOException
    {              
        File fromFile = new File(sFromFile);
        if(!fromFile.exists()) {
            throw new IOException("File to copy, " + fromFile.getAbsolutePath() + ", does not exist.");
        }
  
        File toFile = null;
        try {
            toFile = createFile(sToFile);
        } catch (Exception e) {
            throw new IOException("failed to create file: '" + sToFile + "'");
        }
  
        FileInputStream inFile = null;
        FileOutputStream outFile = null;
        try {
            inFile = new FileInputStream(fromFile); 
            outFile = new FileOutputStream(toFile);
        }
        catch(FileNotFoundException e) {
            throw new IOException(e.getMessage());
        }
      
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inFile));
            // Output is automatically flushed by PrintWriter:
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outFile)),true);
            while (true) {
                String str = in.readLine();
                if (str == null) 
                    break;
                out.println(str);
            }
            in.close();
            out.close();
         } catch (Exception e) {
             throw new IOException(e.getMessage());
         }
    }

    /**
     * Gets the content of a given text file
     * 
     * @param sFile file physical path.
     * @throws IOException
     * @return content of text file
     */
    public static String getTextFileContent(String sFile) throws IOException
    {  
        StringBuilder sb = new StringBuilder(4096);
        File file = new File(sFile);
        if(!file.exists()) {
            throw new IOException("File '" + file.getAbsolutePath() + "' does not exist.");
        }
    
        FileInputStream inFile = null;
        try {
            inFile = new FileInputStream(file); 
        }
        catch(FileNotFoundException e) {
            throw new IOException(e.getMessage());
        }
      
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inFile));
            while (true) {
                String str = in.readLine();
                if (str == null) 
                    break;
                sb.append(str).append("\n");
            }
            in.close();
         } catch (Exception e) {
             throw new IOException(e.getMessage());
         }
         return sb.toString();
    }

    /**
     * Copy a binary file to a new file
     *
     * @param sFromFile
     * @param sToFile
     */
    public static void copyfile(String sFromFile, String sToFile){
        try{
            File f1 = new File(sFromFile);
            File f2 = new File(sToFile);

            FileInputStream in = new FileInputStream(f1);
            FileOutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            log.info("File copy successful from " + sFromFile + " to " + sToFile);
        } catch(FileNotFoundException e){
            log.error(e, e);
        } catch(IOException e){
            log.error(e, e);
        }
    }
  
    /**
     * Append to an existing file from a source file
     *
     * @param sFromFile
     * @param sToFile
     */
    public static void appendfile(String sFromFile, String sToFile){
        try {
            File f1 = new File(sFromFile);
            File f2 = new File(sToFile);

            FileInputStream in = new FileInputStream(f1);
            FileOutputStream out = new FileOutputStream(f2, true);

            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            log.info("File append successful");
        } catch(FileNotFoundException e){
            log.error(e, e);
        } catch(IOException e){
            log.error(e, e);
        }
    }

    /**
     * Creates a file on the hard drive.
     * 
     * @param filename a file with full path.
     * @return the File object created.
     * @throws IOException
     */
    public static File createFile(String filename) throws IOException {      
        File file = new File(filename);
        try {
            if (file.isDirectory())
                throw new IOException("The path '" + file.getPath() + "' does not specify a file.");
            if (!file.isFile()) { // If the file doesn't exist
                // Check the parent directory...
                file = file.getAbsoluteFile();
                File parentDir = new File(file.getParent());
                if (!parentDir.exists()) // ... and create it if necessary
                    parentDir.mkdirs();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IOException(e.getMessage());
        }
        return file;
    }
    
    /**
     * Removes files under given directory for a given filename filter. 
     * 
     * @param sDir the directory name to remove files from.
     * @param sFilter filename filter, for example, ".log"
     * @return true if operation is successful, false otherwise.
     */
    public static boolean removeFiles(String sDir, String sFilter)
    {
        boolean bRet = true;
        try {
            File file = new File(sDir);
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    int idx = fileList[i].getName().lastIndexOf(sFilter);
                    if (idx > 0) {
                        if (fileList[i].delete())
                            log.info(fileList[i].getAbsolutePath() + " is deleted");
                        else
                            log.info("failed to delete file " + fileList[i].getAbsolutePath());
                    }
                }
            }
        }
        catch(Exception e) {
            bRet = false;
            log.error(e);
        }
        return bRet;
    }

    /**
     * The function came from
     * http://stackoverflow.com/questions/2056221/recursively-list-files-in-java
     * 
     * @param files
     * @param dir
     * @return 
     */
    public static List<File> addFiles(List<File> files, File dir)
    {
        if (files == null)
            files = new LinkedList<File>();

        if (!dir.isDirectory())
        {
            files.add(dir);
            return files;
        }

        for (File file : dir.listFiles())
            addFiles(files, file);
        return files;
    }

    /**
     * Delete a specified file from disk
     *
     * @param file full file path
     * @return
     */
    public static boolean deleteFile(String file) {
        boolean ret = false;
        try {
            ret = (new File(file)).delete();
        } catch (Exception e) {
            System.out.println("Failecd to delet file "+ file + " ");
        }
        return ret;
    }

    /**
     * Read a file from /webapp and return as a string
     * e.g. FileUtil.readFile("/xmldata.txt");
     *
     * @param filename
     * @return
     */
    @SuppressWarnings("resource")
    public static String readFile(String filename) {
        InputStream inputStream = null;
        String result = null;
        Scanner s = null;
        try {
            inputStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(filename);
            s = new Scanner(inputStream).useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (s != null) {
                s.close();
            }
        }
        return result;
    }

}
