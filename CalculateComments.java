// Imports
import java.io.*;
import java.util.ArrayList;

/**
 * CalculateComments
 * - Driver Class for Program
 */
public class CalculateComments {
    /**
     * fallsInStringGeneral()
     * - Checks if a comment character is part of a string or not.
     *
     * @param line Line to check
     * @return A boolean array of whether // or /* are within a string or not
     */
    public boolean[] fallsInStringGeneral(String line) {
        // Get a list of all the positions of a double-quote (") character
        ArrayList<Integer> quoteLocations = new ArrayList<>();
        if ((line.charAt(0) == '\"')) {
            quoteLocations.add(0);
        }
        for (int i = 1; i < line.length(); i++) {
            if ((line.charAt(i) == '\"' && line.charAt(i - 1) != '\\')) {
                quoteLocations.add(i);
            }
        }

        // Get the indices of the first occurrence of the comment characters (for single and multi-line)
        int multiLineComment = line.indexOf("/*"), singleLineComment = line.indexOf("//");
        boolean[] isPartOfString = {false, false};

        // In a for-loop, check if the single-line of multiline comment characters fall between
        // any 2 pair of string apostrophes
        for (int i = 0; i < quoteLocations.size() - 1; i += 2) {
            if (multiLineComment > quoteLocations.get(i) && multiLineComment < quoteLocations.get(i + 1)) {
                isPartOfString[0] = true;
            }
            if (singleLineComment > quoteLocations.get(i) && singleLineComment < quoteLocations.get(i + 1)) {
                isPartOfString[1] = true;
            }
            if (isPartOfString[0] && isPartOfString[1]) {
                break;
            }
        }

        return isPartOfString;
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
    public boolean fallsInStringPython(String line, int commentCharacterIndex) {
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
                while (line.charAt(j) != s && j < commentCharacterIndex) {
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
     * parsePython()
     * - Parses through Python code for comments
     *
     * @param program Main program code
     */
    public void parsePython(String program) {
        int totComLines = 0, totSingleLineComs = 0, totNumComLinesInBlock = 0, totNumBlockComs = 0, totNumTodos = 0;
        int i = 0, end, numConsecutiveComments = 0;

        // Loop through every line of the program
        String[] lines = program.split("\\r?\\n");
        while (i < lines.length) {
            // Check if there is a comment character, and it is not a part of a string
            end = lines[i].indexOf('#');
            if (end != -1 && lines[i].length() > 0 && !fallsInStringPython(lines[i], end)) {
                // Check if there is any TODOs, and track the number of consecutive comments and total comments
                if (lines[i].toUpperCase().contains("TODO")) {
                    totNumTodos++;
                }
                numConsecutiveComments++;
                totComLines++;
            }
            // If not, then check if any comments have formed from previous loop iterations
            else {
                // If a block comment has formed, then number of consecutive comments is more than 1
                if (numConsecutiveComments > 1) {
                    totNumBlockComs++;
                    totNumComLinesInBlock += numConsecutiveComments;
                }
                // Else, a single-line comment has formed, or no comments were encountered
                else {
                    totSingleLineComs += numConsecutiveComments;
                }
                // Reset the consecutive-comment counter
                numConsecutiveComments = 0;
            }
            i++;
        }

        // Print the results
        this.printOutput(lines.length, totComLines, totSingleLineComs, totNumComLinesInBlock, totNumBlockComs, totNumTodos);
    }

    /**
     * parseGeneral()
     * - Parse JavaScript, Java, Kotlin, Scala, Groovy, and C++ programs for comments
     *
     * @param program The program to parse
     */
    public void parseGeneral(String program) {
        int totComLines = 0, totSingleLineComs = 0, totNumComLinesInBlock = 0, totNumBlockComs = 0, totNumTodos = 0;
        int multiLineComment, singleLineComment, i;
        String[] lines = program.split("\\r?\\n");

        // Loop through the program
        i = 0;
        while (i < lines.length) {
            // Skip blank lines
            if (lines[i].length() == 0) { i++; continue; }
            // Get the starting positions of the comment characters
            multiLineComment = lines[i].indexOf("/*");
            singleLineComment = lines[i].indexOf("//");

            // Check if the comment characters are part of a string
            boolean[] isPartOfString = this.fallsInStringGeneral(lines[i]);

            // Check if the multi-line comment start character exists, and if it does, whether it is part of a string
            if (multiLineComment != -1 && !isPartOfString[0]) {
                // If a valid single-comment character precedes it, then parse it as a single-string
                if (singleLineComment != -1 && singleLineComment < multiLineComment && !isPartOfString[1]) {
                    // Check if there TODOs are there in any of the lines
                    if (lines[i].toUpperCase().contains("TODO")) {
                        totNumTodos++;
                    }
                    totSingleLineComs++;
                }
                // Else, it is a multi-line comment. Find the ending line for it, and increment the trackers
                else {
                    totNumBlockComs++;
                    if (lines[i].indexOf("*/") < multiLineComment) {
                        while (!lines[i].contains("*/")) {
                            totComLines++;
                            totNumComLinesInBlock++;

                            // Check if there TODOs are there in any of the lines
                            if (lines[i].toUpperCase().contains("TODO")) {
                                totNumTodos++;
                            }
                            i++;
                        }
                    }
                    totNumComLinesInBlock++;
                }
                totComLines++;

                // Check if there TODOs are there in any of the lines
                if (lines[i].toUpperCase().contains("TODO")) {
                    totNumTodos++;
                }
            }
            // Else, check if the line is (or contains) a single-line coment
            else if (singleLineComment != -1 && !isPartOfString[1]) {
                totComLines++;
                totSingleLineComs++;

                if (lines[i].toUpperCase().contains("TODO")) {
                    totNumTodos++;
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

        // List of supported formats
        String generalFormats = "java kt scala js cpp cxx C c++ hpp py groovy gvy gy gsh";

        // for every file passed in
        for (String arg : args) {
            // If the file is not a program (based on extension)
            if (!arg.contains(".") || arg.charAt(0) == '.') {
                System.out.println("\nThe file \"" + arg + "\" is not a program file.\n");
                continue;
            }

            // Get the file extension
            file = arg.substring(arg.lastIndexOf('.') + 1);

            // Check if the program's language is supported
            if (generalFormats.contains(file)) {
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

                    // Send the program to the appropriate parser
                    if (file.equals("py")) {
                        System.out.println(arg);
                        commentsObj.parsePython(builder.toString());
                        System.out.println("\n--------------------");
                    } else {
                        System.out.println(arg);
                        commentsObj.parseGeneral(builder.toString());
                        System.out.println("\n--------------------");
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("\nThe file \"" + arg + "\" was not found.\n");
                }
            }
            // Else, output an error message that the program is not supported (yet)
            else {
                System.out.println("This program type is not supported yet.");
            }
        }
    }
}
