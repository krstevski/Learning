import java.io.*;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Main {

    final static String DIRECTORY_DIRTY = "C:\\Wordlists\\Test\\Dirty\\";
    final static String DIRECTORY_CLEAN = "C:\\Wordlists\\Test\\Clean\\";

    static boolean checkString(String s) {
        boolean check = false;

        for (char c : s.toCharArray()) {
            if ((int) c < 32 || (int) c == 127) {
                check = true;
                break;
            }
        }
        if (!check) {
            s = s.replaceAll("[^\\p{ASCII}]", "");
            if (s.length() > 8) {
                return true;
            }
        }
        return check;
    }

    static void cleanData(String parent, String fileName) throws IOException {
        HashMap<String, HashSet<String>> accounts = new HashMap<>();
        StringTokenizer st;
        File dirtyFile = new File(DIRECTORY_DIRTY + parent + "\\" + fileName);
        File cleanDirectory = new File(DIRECTORY_CLEAN + parent);
        File cleanFile = new File(cleanDirectory + "\\" + fileName);
        File checker = new File(DIRECTORY_CLEAN + dirtyFile.getParentFile().getName() + "\\" + dirtyFile.getName());

        if (checker.exists()) {
            System.out.println(checker.getName() + " FROM DIRECTORY " + checker.getParentFile().getName() + " ALREADY EXISTS!");
        } else {
            long bytes = dirtyFile.length();
            long megabytes = (bytes / 1024) / 1024;

            if (!(megabytes > 2200 * 1024)) {
                try (BufferedReader br = new BufferedReader(new FileReader(dirtyFile))) {
                    String line, email, pass;
                    System.out.println("WORKING ON " + dirtyFile.getName() + " INSIDE " + dirtyFile.getParentFile().getName());

                    while ((line = br.readLine()) != null) {
                        st = new StringTokenizer(line, " :;");
                        if (st.countTokens() == 2) {
                            email = st.nextToken();
                            pass = st.nextToken();

                            if (checkString(email) && checkString(pass)) {
                                if(accounts.containsKey(email)) {
                                    accounts.get(email).add(pass);
                                } else {
                                    HashSet passwords = new HashSet<>();
                                    passwords.add(pass);
                                    accounts.put(email, passwords);
                                }
                            }
                        }
                    }
                }

                FileWriter sorted;
                if (!cleanDirectory.exists()) {
                    cleanDirectory.mkdir();
                }

                sorted = new FileWriter(cleanFile);

                for (String k: accounts.keySet()) {
                    sorted.write(k + " =");

                    for (String v: accounts.get(k)) {
                        sorted.write(" " + v);
                    }
                    sorted.write("\n");
                }
                System.out.println("Finished with " + parent + " " + fileName);
                sorted.close();
            } 
            // else {
            //     largeFiles.add(dirtyFile);
            // }
        }
    }

    static void printFirstLine(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = br.readLine();
        System.out.println(line);
        for (char c : line.toCharArray()) {
            System.out.printf("%d ", (int) c);
        }
        br.close();
    }

    static void getFiles(File[] files) throws IOException {
        for (File f : files) {
            if (f.isDirectory()) {
                getFiles(f.listFiles());
            } else {
                if (f.toString().endsWith(".txt")) {
                    cleanData(f.getParentFile().getName(), f.getName());

                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            File[] files = new File(DIRECTORY_DIRTY).listFiles();

            if (files != null) {
                getFiles(files);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
