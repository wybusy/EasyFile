package com.wybusy;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.attribute.PosixFilePermission.*;

public class EasyFile {
    /**
     * ## 文件操作
     *
     */

    /**
     * ##### - mkDir
     * 若目录不存在，则新建各级目录
     *
     * @param path
     * @return Path
     */
    public static Path mkDir(String path) {
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(filePath);
            } catch (IOException e) {
                filePath = null;
            }
        }
        return filePath;
    }

    /**
     * ##### - lastModify
     * 文件的最后修改时间
     *
     * @param path
     * @param fileName
     * @return 毫秒时间戳
     */
    public static Long lastModify(String path, String fileName) {
        Long result = -1l;
        Path filePath = Paths.get(path, fileName);
        try {
            result = Files.getLastModifiedTime(filePath).toMillis();
        } catch (IOException e) {
        }
        return result;
    }

    //"*.{jpg,png,gif}"

    /**
     * ##### - dirTree
     * 返回目录下所有文件及目录
     *
     * @param path
     * @param basePath
     * @param filter    "*.{jpg,png,gif}"
     * @param recursion 是否递归
     * @return List<Map>
     */
    public static List<Map> dirTree(String path, String basePath, String filter, Boolean recursion) {
        if (filter == null || filter.trim().equals("")) filter = "*";
        List<Map> result = new ArrayList<>();
        Path dirFile = Paths.get(path);
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(dirFile, filter);
            for (Path file : stream) {
                Map fileInfo = new HashMap();
                fileInfo.put("name", file.getFileName());
                fileInfo.put("date", Files.getLastModifiedTime(file).toMillis());
                fileInfo.put("path", file.toAbsolutePath().toString()
                        .replaceAll("(?i)^c:", "")
                        .replaceAll("\\\\", "/")
                        .replaceAll("^" + basePath, ""));
                if (Files.isDirectory(file)) {
                    fileInfo.put("isDir", true);
                    if (recursion) {
                        fileInfo.put("subDir", dirTree(file.toAbsolutePath().toString(), basePath, filter, recursion));
                    }
                } else {
                    fileInfo.put("size", Files.size(file));
                    fileInfo.put("isDir", false);
                }
                result.add(fileInfo);
            }
        } catch (IOException e) {
        }
        return result;
    }

    public static String read(String path, String fileName) {
        return read(path, fileName, "UTF-8");
    }

    /**
     * ##### - read
     * 读取文件内容
     *
     * @param path
     * @param fileName
     * @param charset  编码方式，可选参数
     * @return String
     */
    public static String read(String path, String fileName, String charset) {
        String result = "";
        Path filePath = Paths.get(path, fileName);
        if (Files.exists(filePath)) {
            try {
                byte[] bytes = Files.readAllBytes(filePath);
                result = new String(bytes, Charset.forName(charset));
            } catch (IOException e) {
            }
        }
        return result;
    }

    public static boolean save(String path, String fileName, Boolean append, String content) {
        return save(path, fileName, append, content, "UTF-8");
    }

    /**
     * ##### - write
     * 把内容写入文件，若在linux中，把属性改为666
     *
     * @param path
     * @param fileName
     * @param append   追加方式
     * @param content
     * @param charset  编码方式，可选参数
     * @return boolean 成功与否
     */
    public static boolean save(String path, String fileName, Boolean append, String content, String charset) {
        boolean result = true;
        Path filePath = Paths.get(path, fileName);
        byte[] bytes = new byte[0];
        try {
            bytes = content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mkDir(path);
        try {
            if (append)
                Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            else
                Files.write(filePath, bytes, StandardOpenOption.CREATE);
            PosixFileAttributeView attr = Files.getFileAttributeView(filePath, PosixFileAttributeView.class);
            if (attr != null) {// 如果是linux的话
                attr.setPermissions(EnumSet.of(OWNER_READ, OWNER_WRITE,
                        GROUP_READ, GROUP_WRITE, OTHERS_READ, OTHERS_WRITE));
            }
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ##### - copy
     * 拷贝，若2,4参数为空，则为拷贝目录（其实没有用处，不会递归拷贝文件，只是新建了一个目录而已）
     *
     * @param sourcePath
     * @param sourceFile
     * @param targetPath
     * @param targetFile
     * @return boolean
     */
    public static boolean copy(String sourcePath, String sourceFile, String targetPath, String targetFile) {
        boolean result = true;
        mkDir(targetPath);
        if (sourceFile != null) sourcePath += "/" + sourceFile;
        Path source = Paths.get(sourcePath);
        if (!Files.isDirectory(source)) {
            if (targetFile != null) targetPath += "/" + targetFile;
            Path target = Paths.get(targetPath);
            try {
                Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                result = false;
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * ##### - move
     * 移动与改名，若2，4参数为空，且在同一目录下，则为目录改名，否则目录不为空，就失败
     *
     * @param sourcePath
     * @param sourceFile
     * @param targetPath
     * @param targetFile
     * @return boolean
     */
    public static boolean move(String sourcePath, String sourceFile, String targetPath, String targetFile) {
        boolean result = true;
        if (sourceFile != null) sourcePath += "/" + sourceFile;
        Path source = Paths.get(sourcePath);
        if (targetFile != null) targetPath += "/" + targetFile;
        Path target = Paths.get(targetPath);
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ##### - del
     * 删除，若2参数为空，则删除和目录，但目录内有文件时，删除失败
     *
     * @param path
     * @param fileName
     * @return boolean
     */
    public static boolean del(String path, String fileName) {
        boolean result = true;
        if (fileName != null) path += "/" + fileName;
        Path filePath = Paths.get(path);
        try {
            result = Files.deleteIfExists(filePath);
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * ##### - unzip
     * 解压文件。内含中文文件名会出错
     *
     * @param zipFileName
     * @param targetPath
     */
    public static void unzip(String zipFileName, String targetPath) throws Exception {
        FileSystem fs = FileSystems.newFileSystem(Paths.get(zipFileName), null);
        Files.walkFileTree(fs.getPath("/"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path destPath = Paths.get(targetPath, file.toString());
                Files.deleteIfExists(destPath);
                Files.createDirectories(destPath.getParent());
                Files.move(file, destPath);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * ##### - zip
     * 压缩文件。内含中文文件名不影响
     *
     * @param source 可以是文件或文件夹
     * @param targetZipName
     */

    public static void zip(String source, String targetZipName) throws Exception {
        //创建zip输出流
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetZipName));
        //创建缓冲输出流
        BufferedOutputStream bos = new BufferedOutputStream(out);
        File input = new File(source);
        compress(out, bos, input, null);
        bos.close();
        out.close();
    }

    //递归压缩
    private static void compress(ZipOutputStream out, BufferedOutputStream bos, File input, String name) throws IOException {
        if (name == null) {
            name = input.getName();
        }
        //如果路径为目录（文件夹）
        if (input.isDirectory()) {
            //取出文件夹中的文件（或子文件夹）
            File[] flist = input.listFiles();

            if (flist.length == 0)//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入
            {
                out.putNextEntry(new ZipEntry(name + "/"));
            } else//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
            {
                for (int i = 0; i < flist.length; i++) {
                    compress(out, bos, flist[i], name + "/" + flist[i].getName());
                }
            }
        } else//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
        {
            out.putNextEntry(new ZipEntry(name));
            FileInputStream fos = new FileInputStream(input);
            BufferedInputStream bis = new BufferedInputStream(fos);
            int len = -1;
            //将源文件写入到zip文件中
            byte[] buf = new byte[1024];
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bis.close();
            fos.close();
        }
    }
}
/**
 * 替换规则
 * ^ *
 * ^[^\*].*\n
 * \* ?/?
 *
 * @(.*)\n -> > \1    \n
 */
