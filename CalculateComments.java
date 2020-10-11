// Imports
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * CalculateComments
 * - Driver Class for Program
 */
public class CalculateComments {
    public HashMap<String, HashMap<String, String>> COMMENT_CHARS;
    public HashMap<String, String> FILE_TO_PROGRAM;

    /**
     * CalculateComments() Constructor
     * - Used to initialize the list of comment characters per language and File-Program mapping
     *
     * NOTE: To add more languages, add them in this constructor.
     */
    public CalculateComments() {
        this.COMMENT_CHARS = new HashMap<>() {{
            put("python", new HashMap<>() {{
                put("#", "0");
            }});
            put("java", new HashMap<>() {{
                put("/*", "*/");
                put("//", "0");
            }});
            put("javascript", new HashMap<>() {{
                put("/*", "*/");
                put("//", "0");
            }});
            put("c++", new HashMap<>() {{
                put("/*", "*/");
                put("//", "0");
            }});
        }};

        // Other JVM languages, like Kotlin, are mapped to Java here as well because of the same commenting styles
        this.FILE_TO_PROGRAM = new HashMap<>() {{
            put("py", "python");
            put("js", "javascript");
            put("java", "java");
            put("kt", "java");
            put("scala", "java");
            put("cpp", "c++");
            put("c++", "c++");
            put("hpp", "c++");
            put("cxx", "c++");
        }};
    }

    /**
     * fallsInStringPython()
     * - Checks if a comment-character for Python falls within a string
     * - Different from fallsInStringGeneral() because it checks single and double quotes
     *
     * @param line The line to process
     * @param commentCharacterIndex The occurrence of the comment-character
     * @return Whether comment-character falls within a string or not
     */
    public boolean fallsInString(String line, int commentCharacterIndex) {
        // Placeholder value
        char s = 'a';

        // Special check for the first character
        int j = 0;
        if ((line.charAt(0) == '\"') || (line.charAt(0) == '\'')) {
            s = line.charAt(j++);
            while (line.charAt(j) != s && j < commentCharacterIndex) {
                j++;
            }
            if (j != commentCharacterIndex) {
                s = 'a';
            }
        } else { j++; }

        // Loop through string, and keep track of string characters
        while (j < commentCharacterIndex) {
            // If it is a valid string character (not an escaped one), then find its matching pair
            if ((line.charAt(j) == '\"' && line.charAt(j - 1) != '\\') || (line.charAt(j) == '\'' && line.charAt(j - 1) != '\\')) {
                s = line.charAt(j++);
                while (j < commentCharacterIndex && line.charAt(j) != s) {
                    j++;
                }
                // Reset the tracker once the closing pair of the quotes have been found.
                if (j != commentCharacterIndex) {
                    s = 'a';
                }
            }
            j++;
        }

        // Return whether it falls within a string (based on whether closing quote found or not)
        return s != 'a';
    }

    /**
     * printOutput()
     * - Prints the output to the console.
     *
     * @param numLines Number of lines in program
     * @param totComLines Total number of comment lines
     * @param totSingleLineComs Total number of one-line comments
     * @param totNumComLinesInBlock Total number of comments part of a comment block
     * @param totNumBlockComs Total number of block comments
     * @param totNumTodos Total number of TODOs
     */
    public void printOutput(int numLines, int totComLines, int totSingleLineComs, int totNumComLinesInBlock, int totNumBlockComs, int totNumTodos) {
        System.out.println(" - Total # of lines: " + numLines);
        System.out.println(" - Total # of comment lines: " + totComLines);
        System.out.println(" - Total # of single line comments: " + totSingleLineComs);
        System.out.println(" - Total # of comment lines within block comments: " + totNumComLinesInBlock);
        System.out.println(" - Total # of block line comments: " + totNumBlockComs);
        System.out.println(" - Total # of TODO's: " + totNumTodos);
    }

    /**
     * parse()
     * - Counts the number and type of comments in the program, based on the given language
     *
     * @param program The code to parse
     * @param language The language of the code
     */
    public void parse(String program, String language) {
        ArrayList<String> singleChars = new ArrayList<>();
        ArrayList<String> multiChars = new ArrayList<>();

        // Get the comment characters for the given language
        HashMap<String, String> commentChars = this.COMMENT_CHARS.get(language);
        for (String key : commentChars.keySet()) {
            if (commentChars.get(key).equals("0")) {
                singleChars.add(key);
            } else {
                multiChars.add(key);
            }
        }

        int totComLines = 0, totSingleLineComs = 0, totNumComLinesInBlock = 0, totNumBlockComs = 0, totNumTodos = 0;
        String[] lines = program.split("\\r?\\n");

        // Loop through the program
        int i = 0;
        while (i < lines.length) {
            // Skip blank lines
            lines[i] = lines[i].trim();
            if (lines[i].length() == 0) { i++; continue; }
            int lineLength = lines[i].length();

            // Get the starting positions of the comment characters
            int firstSingle = lineLength, firstMulti = lineLength, curr;
            String multi = "-1", single = "-1";

            // Get the first comment character for multi-line comments
            // The rest are irrelevant since they will fall within the first comment characters
            for (String chr : multiChars) {
                curr = lines[i].indexOf(chr);
                if (curr != -1 && curr < firstMulti && !this.fallsInString(lines[i], curr)) {
                    firstMulti = curr;
                    multi = chr;
                }
            }

            // Repeat the above for single-line comment characters
            for (String chr : singleChars) {
                curr = lines[i].indexOf(chr);
                if (curr != -1 && curr < firstSingle && !this.fallsInString(lines[i], curr)) {
                    firstSingle = curr;
                    single = chr;
                }
            }

            // Check if the multi-line comment start character exists, and if it does, whether it is part of a string
            if (firstMulti != lineLength && firstMulti < firstSingle) {
                // If the block comment does not end on the same line, cover all lines part of it
                if (lines[i].indexOf(commentChars.get(multi)) < firstMulti) {
                    while (i < lines.length && !lines[i].contains(commentChars.get(multi))) {
                        totComLines++;
                        totNumComLinesInBlock++;

                        // Check if there TODOs are there in any of the lines
                        if (Pattern.compile("TODO[^a-zA-Z0-9]").matcher(lines[i].toUpperCase()).find()) {
                            totNumTodos++;
                        }
                        i++;
                    }
                }

                totNumBlockComs++;
                totComLines++;
                totNumComLinesInBlock++;

                // Check if there TODOs are there in any of the lines
                if (Pattern.compile("TODO[^a-zA-Z0-9]").matcher(lines[i].toUpperCase()).find()) {
                    totNumTodos++;
                }
            }
            // Else, check if the line is (or contains) a single-line comment
            else if (firstSingle != lineLength) {
                // If the comment does not take up the whole line, it is a single-line comment; else, it is a block comment
                // This is done to conform with Python's comments, which use '#' for single and multi-line comments
                if (firstSingle > 0) {
                    totComLines++;
                    totSingleLineComs++;
                    if (Pattern.compile("TODO[^a-zA-Z0-9]").matcher(lines[i].toUpperCase()).find()) {
                        totNumTodos++;
                    }
                }
                // Check for multi-line comments in this else-block
                else {
                    int numComments = 0;
                    do {
                        totComLines++;
                        numComments++;
                        if (Pattern.compile("TODO[^a-zA-Z0-9]").matcher(lines[i].toUpperCase()).find()) {
                            totNumTodos++;
                        }
                        i++;
                    } while (i < lines.length && lines[i].trim().indexOf(single) == 0);

                    // A single full-line comment is still a single-line comment. This block checks for that
                    if (numComments > 1) {
                        totNumComLinesInBlock += numComments;
                        totNumBlockComs++;
                    } else {
                        totSingleLineComs += numComments;
                    }
                    i--;
                }
            }
            i++;
        }

        // Print the results
        this.printOutput(lines.length, totComLines, totSingleLineComs, totNumComLinesInBlock, totNumBlockComs, totNumTodos);
    }

    /**
     * main()
     * - Driver program
     *
     * @param args Command-line arguments
     * @throws IOException Input-Output Exception when attempting to read files.
     */
    public static void main(String[] args) throws IOException {
        // Check if no programs were passed in to parse
        if (args.length < 1) {
            System.err.println("Must specify at least one program file to process");
        }

        CalculateComments commentsObj = new CalculateComments();
        BufferedReader reader;
        StringBuilder builder;
        String temp, file;

        // for every file passed in
        for (String arg : args) {
            // If the file is not a program (based on extension)
            if (!arg.contains(".") || arg.charAt(0) == '.') {
                System.out.println("\nThe file \"" + arg + "\" is not a program file.\n");
                continue;
            }

            // Get the file extension
            file = arg.substring(arg.lastIndexOf('.') + 1);

            // Get the program language based on the file extension
            String program = commentsObj.FILE_TO_PROGRAM.get(file.toLowerCase());
            if (program == null) {
                System.out.println(arg);
                System.out.println("This program type is not supported yet.");
                continue;
            }

            // Attempt to read the program
            try {
                reader = new BufferedReader(new FileReader(arg));
                builder = new StringBuilder();
                while ((temp = reader.readLine()) != null) {
                    if (temp.length() == 0) {
                        builder.append("\n");
                    } else {
                        builder.append(temp).append("\n");
                    }
                }
                reader.close();

                // Send the program to the parser with the correct language
                System.out.println(arg);
                commentsObj.parse(builder.toString(), program);
                System.out.println("\n--------------------");
            } catch (FileNotFoundException e) {
                System.err.println("\nThe file \"" + arg + "\" was not found.\n");
            }
        }
    }
}
