# Capital One Assessment: Automated Code Checker
The purpose of this project is to build an automated checker whenever code is merged into a build pipeline.

## Approach
The latest approach taken was to generify the parsing function so that it could count the comments from any programming language. To do this, 2 maps were created:

1. The first map holds a list of comment characters for each programming language. It also distinguished between single-line comment characters and block-line comment characters.
2. The second map holds a file-to-program mapping, so that the language of a file passed in at the comment line can be determined based on its extension.

In this program, adding a programming language is easy: simply add the comment characters and the file extension - to - language mapping for the language. See the "How To Run" section for details.

## Assumptions
The following assumptions were made:

1. For parsing Python programs, docstrings were not counted as comments. The assumption made here was that docstrings do not count as comments given that they are accessible by the program at run-time. Also, the official documentation for Python states that a block comment is a set of lines beginning with a # character, and not the triple-quotes used to generate a docstring.
2. Comment characters that occur within a string are not parsed as comments.
3. The program can check multiple code files. All files can be passed in as command-line arguments via their relative or absolute paths.

## How to Run
To run, simply execute the program using `java CalculateComments.java <FILE_PATH> ...`. Multiple files can be specified as command-line arguments, and the path can be relative or absolute.

To add a custom language, go to the `CalculateComments()` constructor, and under `this.COMMENT_CHARS = new HashMap<>() {{`, add the following block of code:

```java
put("LANG_NAME", new HashMap<>() {{
    put("SINGLE_LINE_CHARACTER", "0");
    put("MULTI_LINE_CHARACTER_START", "MULTI_LINE_CHARACTER_END");
}});
```

The '0' in the `put("SINGLE_LINE_CHARACTER", "0");` indicates that this is a single-line comment character.

Re-compile the code, and run it again on your file to get the results for your new language.

## Author
Aryan Kukreja

Test code and problem statement provided by Capital One
