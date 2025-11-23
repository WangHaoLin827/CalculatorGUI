import java.io.*;
import java.util.jar.*;

public class BuildJar {
    public static void main(String[] args) throws Exception {
        String jarName = args.length > 0 ? args[0] : "CalculatorGUI.jar";
        Manifest manifest = new Manifest();
        Attributes attrs = manifest.getMainAttributes();
        attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attrs.put(new Attributes.Name("Main-Class"), "CalculatorGUI");
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarName), manifest)) {
            File dir = new File(".");
            File[] files = dir.listFiles(f -> f.isFile() && f.getName().startsWith("CalculatorGUI") && f.getName().endsWith(".class"));
            if (files != null) {
                for (File f : files) {
                    addFile(jos, f, f.getName());
                }
            }
        }
    }

    private static void addFile(JarOutputStream jos, File file, String entryName) throws Exception {
        JarEntry entry = new JarEntry(entryName);
        entry.setTime(file.lastModified());
        jos.putNextEntry(entry);
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) {
                jos.write(buf, 0, r);
            }
        }
        jos.closeEntry();
    }
}